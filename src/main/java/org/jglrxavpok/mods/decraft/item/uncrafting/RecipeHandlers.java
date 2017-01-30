package org.jglrxavpok.mods.decraft.item.uncrafting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Iterables;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;


/**
 * Recipe Handlers return the "crafting grid" depending on a crafting recipe.
 * 
 */
public final class RecipeHandlers
{
	
	/**
	 * Abstract base class extended by the different types of recipe handler
	 *
	 */
	public static abstract class RecipeHandler
	{
		public abstract ItemStack[] getCraftingGrid(IRecipe s);
	}
	
	

	/**
	 * Handler for vanilla Minecraft shaped recipes
	 *
	 */
	public static class ShapedRecipeHandler extends RecipeHandler
	{
		@Override
		public ItemStack[] getCraftingGrid(IRecipe r)
		{
			// cast the IRecipe instance
			ShapedRecipes shapedRecipe = (ShapedRecipes)r;

			// get a copy of the recipe items with normalised metadata
			List<ItemStack> recipeItems = copyRecipeStacks(Arrays.asList(shapedRecipe.recipeItems));
			
			// get the recipe dimensions
			int recipeWidth = shapedRecipe.recipeWidth;
			int recipeHeight = shapedRecipe.recipeHeight;

			// rearrange the itemstacks according to the recipe width and height
			return reshapeRecipe(recipeItems, recipeWidth, recipeHeight);
		}
	}
	
	
	/**
	 * Handler for vanilla Minecraft shapeless recipes
	 *
	 */
	public static class ShapelessRecipeHandler extends RecipeHandler
	{
		@Override
		public ItemStack[] getCraftingGrid(IRecipe r)
		{
			// cast the IRecipe instance
			ShapelessRecipes shapelessRecipe = (ShapelessRecipes)r;
			
			// get a copy of the recipe items with normalised metadata
			List<ItemStack> recipeItems = copyRecipeStacks(shapelessRecipe.recipeItems);

			// convert the itemstack list to an array
			return recipeItems.toArray(new ItemStack[9]); // return Iterables.toArray(Iterables.filter(recipeItems, ItemStack.class), ItemStack.class);
		}
	}
	
	
	/**
	 * Handler for shaped recipes which utilise the Forge Ore Dictionary
	 *
	 */
	public static class ShapedOreRecipeHandler extends RecipeHandler
	{
		@Override
		public ItemStack[] getCraftingGrid(IRecipe r)
		{
			// cast the IRecipe instance
			ShapedOreRecipe shapedRecipe = (ShapedOreRecipe)r;
			
			// get a copy of the recipe items with normalised metadata
			List<ItemStack> recipeItems = copyRecipeStacks(getOreRecipeItems(Arrays.asList(shapedRecipe.getInput())));
			
			if (!recipeItems.isEmpty())
			{
				// get the recipe dimensions
				int recipeWidth = ((Integer)(ObfuscationReflectionHelper.getPrivateValue(ShapedOreRecipe.class, shapedRecipe, "width"))).intValue();
				int recipeHeight = ((Integer)(ObfuscationReflectionHelper.getPrivateValue(ShapedOreRecipe.class, shapedRecipe, "height"))).intValue();
	
				// rearrange the itemstacks according to the recipe width and height
				return reshapeRecipe(recipeItems, recipeWidth, recipeHeight);
			}
			else return new ItemStack[0];
		}
	}
	

	/**
	 * Handler for shapeless recipes which utilise the Forge Ore Dictionary
	 *
	 */
	public static class ShapelessOreRecipeHandler extends RecipeHandler
	{
		@Override
		public ItemStack[] getCraftingGrid(IRecipe r)
		{
			// cast the IRecipe instance
			ShapelessOreRecipe shapelessRecipe = (ShapelessOreRecipe)r;
			
			// get a copy of the recipe items with normalised metadata
			List<ItemStack> recipeItems = copyRecipeStacks(getOreRecipeItems(shapelessRecipe.getInput()));
			
			if (!recipeItems.isEmpty())
			{
				// convert the itemstack list to an array
				return recipeItems.toArray(new ItemStack[9]);
			}
			else return new ItemStack[0];
		}
	}
	
	
	/**
	 * Handler for shaped recipes from the Mekanism mod
	 *
	 */
	public static class ShapedMekanismRecipeHandler extends RecipeHandler
	{
		public static Class<? extends IRecipe> recipeClass;
		
		static
		{
			try
			{
				recipeClass = Class.forName("mekanism.common.recipe.ShapedMekanismRecipe").asSubclass(IRecipe.class);
			}
			catch(ClassNotFoundException ex) { }
		}

		@Override
		public ItemStack[] getCraftingGrid(IRecipe r)
		{
			List<ItemStack> itemStacks = new ArrayList<ItemStack>();
			try
			{
				for ( Object itemObject : (Object[])recipeClass.getMethod("getInput", (Class[])null).invoke(r))
				{
					ItemStack itemStack;
					
					if (itemObject instanceof ItemStack)
					{
						itemStack = (ItemStack)itemObject;
					}
					else if (itemObject instanceof List)
					{
						itemStack = ((List<ItemStack>)itemObject).get(0);
					}
					else itemStack = (ItemStack)null;

					itemStacks.add(itemStack);
				}
			}
			catch(Exception ex) { }
			return copyRecipeStacks(itemStacks).toArray(new ItemStack[9]);
		}
		
	}
	
	
	/**
	 * Handler for shapeless recipes from the Mekanism mod
	 *
	 */
	public static class ShapelessMekanismRecipeHandler extends RecipeHandler
	{
		public static Class<? extends IRecipe> recipeClass;
		
		static
		{
			try
			{
				recipeClass = Class.forName("mekanism.common.recipe.ShapelessMekanismRecipe").asSubclass(IRecipe.class);
			}
			catch(ClassNotFoundException ex) { }
		}
		
		@Override
		public ItemStack[] getCraftingGrid(IRecipe r)
		{
			List<ItemStack> itemStacks = new ArrayList<ItemStack>();
			try
			{
				for ( Object target : (ArrayList<Object>)recipeClass.getMethod("getInput", (Class[])null).invoke(r))
				{
					if (target instanceof ItemStack)
					{
						itemStacks.add((ItemStack)target);
					}
					else if (target instanceof ArrayList)
					{
						itemStacks.add(((ArrayList<ItemStack>)target).get(0));
					}
				}
			}
			catch(Exception ex) { }
			return copyRecipeStacks(itemStacks).toArray(new ItemStack[9]);
		}
		
	}
	

	
	/**
	 * Handler for shaped recipes from the IndustrialCraft2 mod
	 *
	 */
	public static class ShapedIC2RecipeHandler extends RecipeHandler
	{
		public static Class<? extends IRecipe> recipeClass;
		
		static
		{
			try
			{
				recipeClass = Class.forName("ic2.core.recipe.AdvRecipe").asSubclass(IRecipe.class);
			}
			catch(ClassNotFoundException ex) { }
		}
		
		private List<List<ItemStack>> getInputs(IRecipe r)
		{
			try
			{
				Object[] input = (Object[])recipeClass.getField("input").get(r);
				int[] masks = (int[])recipeClass.getField("masks").get(r);
				int inputWidth = (Integer)(recipeClass.getField("inputWidth").get(r));
				int inputHeight = (Integer)(recipeClass.getField("inputHeight").get(r));
				
				// *** copied from ic2.jeiIntegration.recipe.crafting.AdvRecipeWrapper ***
				int mask = masks[0];
				int itemIndex = 0;
				List ret = new ArrayList();
				for (int i = 0; i < 9; i++) 
				{
					if ((i % 3 < inputWidth) && (i / 3 < inputHeight)) 
					{
						if ((mask >>> 8 - i & 0x1) != 0) 
						{
							ret.add(input[(itemIndex++)]);
						} 
						else 
						{
							ret.add(null);
						}
					}
				}
				
				return replaceRecipeInputs(ret);
				// *** copied from ic2.jeiIntegration.recipe.crafting.AdvRecipeWrapper ***
				
			}
			catch (Exception ex) { return null; } 
			
		}
		
		private List<List<ItemStack>> replaceRecipeInputs(List list)
		{
			try
			{
				// *** copied from ic2.jeiIntegration.recipe.crafting.AdvRecipeWrapper ***
				List<List<ItemStack>> out = new ArrayList(list.size());
				for (Object recipe : list) // for (IRecipeInput recipe : list) 
				{
					if (recipe == null)
					{
						out.add(java.util.Collections.<ItemStack>emptyList());
					}
					else
					{
						List<ItemStack> replace = new ArrayList<ItemStack>((List<ItemStack>)(Class.forName("ic2.api.recipe.IRecipeInput").getMethod("getInputs", (Class[])null).invoke(recipe))); // List<ItemStack> replace = new ArrayList(recipe.getInputs());
						for (java.util.ListIterator<ItemStack> it = replace.listIterator(); it.hasNext();)
						{
							ItemStack stack = (ItemStack)it.next();
							if ((stack != null) && (Class.forName("ic2.api.item.IElectricItem").isInstance(stack.getItem())))
							{
								it.set(stack.copy());
							}
						}
						out.add(replace);
					}
				}
				return out;
				// *** copied from ic2.jeiIntegration.recipe.crafting.AdvRecipeWrapper ***
			}
			catch (Exception ex) { return null; }
		}
		
		
		@Override
		public ItemStack[] getCraftingGrid(IRecipe r)
		{
			List<ItemStack> itemStacks = new ArrayList<ItemStack>();
			try
			{
				List<List<ItemStack>> items = getInputs(r);
				if (items != null)
				{
					for ( List<ItemStack> list : items )
					{
						if (list != null && list.size() > 0)
						{
							itemStacks.add(list.get(0));
						}
						else
						{
							itemStacks.add(null);
						}
					}
				}
			}
			catch(Exception ex) { }
			return copyRecipeStacks(itemStacks).toArray(new ItemStack[9]);
		}
	}
	
	/**
	 * Handler for shapeless recipes from the IndustrialCraft2 mod
	 *
	 */
	public static class ShapelessIC2RecipeHandler extends RecipeHandler
	{
		public static Class<? extends IRecipe> recipeClass;
		
		static
		{
			try
			{
				recipeClass = Class.forName("ic2.core.recipe.AdvShapelessRecipe").asSubclass(IRecipe.class);
			}
			catch(ClassNotFoundException ex) { }
		}
		
		@Override
		public ItemStack[] getCraftingGrid(IRecipe r)
		{
			List<ItemStack> itemStacks = new ArrayList<ItemStack>();
			try
			{
				Class RecipeInputItemStack = Class.forName("ic2.api.recipe.RecipeInputItemStack");
				Class RecipeInputOreDict = Class.forName("ic2.api.recipe.RecipeInputOreDict");
				
				for ( Object target : (Object[])recipeClass.getField("input").get(r))
				{
					if (RecipeInputItemStack.isInstance(target))
					{
						ItemStack itemStack = (ItemStack)RecipeInputItemStack.getField("input").get(target); 
						itemStacks.add(itemStack);
					}
					else if (RecipeInputOreDict.isInstance(target))
					{
						List<ItemStack> _itemStacks = (List<ItemStack>)(RecipeInputOreDict.getMethod("getInputs", (Class[])null).invoke(target));
						itemStacks.add(_itemStacks.get(0));
					}
					else if (target instanceof ItemStack)
					{
						itemStacks.add((ItemStack)target);
					}
					else if (target instanceof ArrayList)
					{
						itemStacks.add(((ArrayList<ItemStack>)target).get(0));
					}
				}
				
			}
			catch(Exception ex) { }
			return copyRecipeStacks(itemStacks).toArray(new ItemStack[9]);
		}
	}
	
	
	/**
	 * Takes a list of ItemStacks from a shaped recipe and correctly positions them according to the recipe width and height
	 */
	private static ItemStack[] reshapeRecipe(List<ItemStack> recipeItems, int recipeWidth, int recipeHeight) 
	{
		ItemStack[] stacks = new ItemStack[9];
		for ( int row = 0 ; row < recipeHeight ; row++ )
		{
			for (int col = 0 ; col < recipeWidth ; col++ )
			{
				stacks[(row * 3) + col] = recipeItems.get(col + row * recipeWidth);
			}
		}
		return stacks;
	}
	
	
	/**
	 * Converts a collection of OreDictionary recipe items into a list of ItemStacks
	 */
	private static List<ItemStack> getOreRecipeItems(List<Object> itemObjects)
	{
		List<ItemStack> itemStacks = new ArrayList<ItemStack>();
		for ( Object itemObject : itemObjects)
		{
			ItemStack itemStack;
			
			if (itemObject instanceof ItemStack)
			{
				itemStack = (ItemStack)itemObject;
			}
			else if (itemObject instanceof List)
			{
				List list = (List)itemObject;
				
				if (list.isEmpty()) // this happens if there's an ore dictionary recipe registered, but no items registered for that dictionary entry
				{
					// abort parsing this recipe and return an empty list
					itemStacks.clear();
					break;
				}
				
				itemStack = ((List<ItemStack>)itemObject).get(0);
			}
			else itemStack = (ItemStack)null;
			
			itemStacks.add(itemStack);
		}
		return itemStacks;
	}
	
	
	/**
	 * Copies the ItemStacks in a list to a new list, whilst normalising the item damage for the OreDictionary wildcard value
	 */
	private static List<ItemStack> copyRecipeStacks(List<ItemStack> inputStacks)
	{
		List<ItemStack> outputStacks = new ArrayList<ItemStack>();
		
		for ( ItemStack inputStack : inputStacks )
		{
			if (inputStack != null)
			{
				ItemStack outputStack = inputStack.copy();
				if (outputStack.getItemDamage() == Short.MAX_VALUE) outputStack.setItemDamage(0);
				outputStacks.add(outputStack);
			}
			else outputStacks.add(null);
		}
		
		return outputStacks;
	}


}
