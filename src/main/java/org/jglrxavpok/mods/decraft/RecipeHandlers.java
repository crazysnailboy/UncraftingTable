package org.jglrxavpok.mods.decraft;


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
 * @author jglrxavpok
 */
public final class RecipeHandlers
{
	
	/**
	 * Abstract base class extended by the different types of recipe handler
	 *
	 */
	public static abstract class RecipeHandler
	{
//		private Class<? extends IRecipe> recipeType;

		public RecipeHandler(Class<? extends IRecipe> recipe)
		{
//			this.recipeType = recipe;
		}
		
//		public Class<? extends IRecipe> getType()
//		{
//			return recipeType;
//		}
		
		public abstract ItemStack[] getCraftingGrid(IRecipe s);
	}
	
	

	/**
	 * Handler for vanilla Minecraft shaped recipes
	 *
	 */
	public static class ShapedRecipeHandler extends RecipeHandler
	{
		public ShapedRecipeHandler(Class<? extends IRecipe> recipe)
		{
			super(recipe);
		}

		@Override
		public ItemStack[] getCraftingGrid(IRecipe r)
		{
			// cast the IRecipe instance
			ShapedRecipes shapedRecipe = (ShapedRecipes)r;

			// obtain the recipe items and the recipe dimensions
			List<ItemStack> recipeItems = Arrays.asList(shapedRecipe.recipeItems);
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
		public ShapelessRecipeHandler(Class<? extends IRecipe> recipe)
		{
			super(recipe);
		}

		@Override
		public ItemStack[] getCraftingGrid(IRecipe r)
		{
			// ShapelessRecipes.recipeItems is a List<ItemStack>, so convert it to an ItemStack[] and return
			return Iterables.toArray(Iterables.filter(((ShapelessRecipes)r).recipeItems, ItemStack.class), ItemStack.class);
		}
	}
	
	
	/**
	 * Handler for shaped recipes which utilise the Forge Ore Dictionary
	 *
	 */
	public static class ShapedOreRecipeHandler extends RecipeHandler
	{
		public ShapedOreRecipeHandler(Class<? extends IRecipe> recipe)
		{
			super(recipe);
		}

		@Override
		public ItemStack[] getCraftingGrid(IRecipe r)
		{
			// cast the IRecipe instance
			ShapedOreRecipe shapedRecipe = (ShapedOreRecipe)r;
			
			// obtain the recipe items and the recipe dimensions
			List<ItemStack> recipeItems = getOreRecipeItems(Arrays.asList(shapedRecipe.getInput()));
			int recipeWidth = ((Integer)(ObfuscationReflectionHelper.getPrivateValue(ShapedOreRecipe.class, shapedRecipe, "width"))).intValue();
			int recipeHeight = ((Integer)(ObfuscationReflectionHelper.getPrivateValue(ShapedOreRecipe.class, shapedRecipe, "height"))).intValue();

			// rearrange the itemstacks according to the recipe width and height
			return reshapeRecipe(recipeItems, recipeWidth, recipeHeight);
		}
	}
	

	/**
	 * Handler for shapeless recipes which utilise the Forge Ore Dictionary
	 *
	 */
	public static class ShapelessOreRecipeHandler extends RecipeHandler
	{
		public ShapelessOreRecipeHandler(Class<? extends IRecipe> recipe)
		{
			super(recipe);
		}

		@Override
		public ItemStack[] getCraftingGrid(IRecipe r)
		{
			return getOreRecipeItems(((ShapelessOreRecipe)r).getInput()).toArray(new ItemStack[9]);
		}
	}
	
	
	/**
	 * Handler for shaped recipes from the Mekanism mod
	 *
	 */
	public static class ShapedMekanismRecipeHandler extends RecipeHandler
	{
		public ShapedMekanismRecipeHandler(Class<? extends IRecipe> recipe) 
		{
			super(recipe);
		}
		
		@Override
		public ItemStack[] getCraftingGrid(IRecipe r)
		{
			try
			{
				List<ItemStack> stacks = new ArrayList<ItemStack>();
				
				for ( Object target : (Object[])Class.forName("mekanism.common.recipe.ShapedMekanismRecipe").getMethod("getInput", (Class[])null).invoke(r))
				{
					if (target instanceof ItemStack)
					{
						stacks.add((ItemStack)target);
					}
					else if (target instanceof ArrayList)
					{
						stacks.add(((ArrayList<ItemStack>)target).get(0));
					}
				}
				
				return stacks.toArray(new ItemStack[9]);
			}
			catch(Exception ex)
			{
				System.out.println("ShapedMekanismRecipeHandler.getCraftingGrid: " + ex.getMessage());
				System.out.println(ex.getStackTrace());
			}
			return null;
		}
		
	}
	
	
	/**
	 * Handler for shapeless recipes from the Mekanism mod
	 *
	 */
	public static class ShapelessMekanismRecipeHandler extends RecipeHandler
	{
		public ShapelessMekanismRecipeHandler(Class<? extends IRecipe> recipe) 
		{
			super(recipe);
		}
		
		@Override
		public ItemStack[] getCraftingGrid(IRecipe r)
		{
			try
			{
				List<ItemStack> stacks = new ArrayList<ItemStack>();
				
				for ( Object target : (ArrayList<Object>)Class.forName("mekanism.common.recipe.ShapelessMekanismRecipe").getMethod("getInput", (Class[])null).invoke(r))
				{
					if (target instanceof ItemStack)
					{
						stacks.add((ItemStack)target);
					}
					else if (target instanceof ArrayList)
					{
						stacks.add(((ArrayList<ItemStack>)target).get(0));
					}
				}
				
				return stacks.toArray(new ItemStack[9]);
			}
			catch(Exception ex)
			{
				System.out.println("ShapelessMekanismRecipeHandler.getCraftingGrid: " + ex.getMessage());
				System.out.println(ex.getStackTrace());
			}
			return null;
		}
		
	}
	
	
	
	/**
	 * Handler for shaped recipes from the IndustrialCraft2 mod
	 *
	 */
	public static class ShapedIC2RecipeHandler extends RecipeHandler
	{
		public ShapedIC2RecipeHandler(Class<? extends IRecipe> recipe) 
		{
			super(recipe);
		}
		
		@Override
		public ItemStack[] getCraftingGrid(IRecipe r)
		{
			try
			{
				Class AdvRecipe = Class.forName("ic2.core.recipe.AdvRecipe");
				Class RecipeInputItemStack = Class.forName("ic2.api.recipe.RecipeInputItemStack");
				Class RecipeInputOreDict = Class.forName("ic2.api.recipe.RecipeInputOreDict");
			
				List<ItemStack> stacks = new ArrayList<ItemStack>();
				for ( Object target : (Object[])AdvRecipe.getField("input").get(r))
				{
					if (RecipeInputItemStack.isInstance(target))
					{
						ItemStack itemStack = (ItemStack)RecipeInputItemStack.getField("input").get(target); 
						stacks.add(itemStack);
					}
					else if (RecipeInputOreDict.isInstance(target))
					{
						List<ItemStack> itemStacks = (List<ItemStack>)(RecipeInputOreDict.getMethod("getInputs", (Class[])null).invoke(target));
						stacks.add(itemStacks.get(0));
					}
					else if (target instanceof ItemStack)
					{
						stacks.add((ItemStack)target);
					}
					else if (target instanceof ArrayList)
					{
						stacks.add(((ArrayList<ItemStack>)target).get(0));
					}
//					else if (target == null)
//					{
//						stacks.add((ItemStack)null);
//					}
				}
				
				return stacks.toArray(new ItemStack[9]);
			
//				int recipeWidth = (Integer)(AdvRecipe.getField("inputWidth").get(r));
//				int recipeHeight = (Integer)(AdvRecipe.getField("inputHeight").get(r));
//				return reshapeRecipe(recipeItems, recipeWidth, recipeHeight);
				
			}
			catch(Exception ex) 
			{ 
				System.out.println("ShapedIC2RecipeHandler.getCraftingGrid: " + ex.getMessage());
				System.out.println(ex.getStackTrace().toString());
			}
			return null;
		}
	}
	
	/**
	 * Handler for shapeless recipes from the IndustrialCraft2 mod
	 *
	 */
	public static class ShapelessIC2RecipeHandler extends RecipeHandler
	{
		public ShapelessIC2RecipeHandler(Class<? extends IRecipe> recipe) 
		{
			super(recipe);
		}
		
		@Override
		public ItemStack[] getCraftingGrid(IRecipe r)
		{
			try
			{
				Class AdvShapelessRecipe = Class.forName("ic2.core.recipe.AdvShapelessRecipe");
				Class RecipeInputItemStack = Class.forName("ic2.api.recipe.RecipeInputItemStack");
				Class RecipeInputOreDict = Class.forName("ic2.api.recipe.RecipeInputOreDict");
				
				List<ItemStack> stacks = new ArrayList<ItemStack>();
				for ( Object target : (Object[])AdvShapelessRecipe.getField("input").get(r))
				{
					if (RecipeInputItemStack.isInstance(target))
					{
						ItemStack itemStack = (ItemStack)RecipeInputItemStack.getField("input").get(target); 
						stacks.add(itemStack);
					}
					else if (RecipeInputOreDict.isInstance(target))
					{
						List<ItemStack> itemStacks = (List<ItemStack>)(RecipeInputOreDict.getMethod("getInputs", (Class[])null).invoke(target));
						stacks.add(itemStacks.get(0));
					}
					else if (target instanceof ItemStack)
					{
						stacks.add((ItemStack)target);
					}
					else if (target instanceof ArrayList)
					{
						stacks.add(((ArrayList<ItemStack>)target).get(0));
					}
				}
				
				return stacks.toArray(new ItemStack[9]);
			}
			catch(Exception ex) 
			{ 
				System.out.println("ShapelessIC2RecipeHandler.getCraftingGrid: " + ex.getMessage());
				System.out.println(ex.getStackTrace().toString());
			}
			return null;
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
	private static List<ItemStack> getOreRecipeItems(List<Object> recipeItems)
	{
		List<ItemStack> recipeStacks = new ArrayList<ItemStack>();
		for ( Object recipeItem : recipeItems)
		{
			if (recipeItem instanceof ItemStack)
			{
				recipeStacks.add((ItemStack)recipeItem);
			}
			else if (recipeItem instanceof List)
			{
				recipeStacks.add(((List<ItemStack>)recipeItem).get(0));
			}
			else if (recipeItem == null)
			{
				recipeStacks.add((ItemStack)null);
			}
		}
		return recipeStacks;
	}


}
