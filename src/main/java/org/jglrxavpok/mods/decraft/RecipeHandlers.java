package org.jglrxavpok.mods.decraft;


import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Iterables;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
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
			// ShapedRecipes.recipeItems is already an ItemStack[], so just return that
			return ((ShapedRecipes)r).recipeItems;
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
			List<ItemStack> stacks = new ArrayList<ItemStack>();
			
			for ( Object target : ((ShapedOreRecipe)r).getInput())
			{
				if (target instanceof ItemStack)
				{
					stacks.add((ItemStack)target);
				}
				else if (target instanceof List)
				{
					stacks.add(((List<ItemStack>)target).get(0));
				}
			}
			
			return stacks.toArray(new ItemStack[9]);
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
			List<ItemStack> stacks = new ArrayList<ItemStack>();
			
			for ( Object target : ((ShapelessOreRecipe)r).getInput())
			{
				if (target instanceof ItemStack)
				{
					stacks.add((ItemStack)target);
				}
				else if (target instanceof List)
				{
					stacks.add(((List<ItemStack>)target).get(0));
				}
			}
			
			return stacks.toArray(new ItemStack[9]);
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


}
