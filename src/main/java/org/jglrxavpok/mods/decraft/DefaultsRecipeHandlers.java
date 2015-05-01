package org.jglrxavpok.mods.decraft;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public final class DefaultsRecipeHandlers
{

	private static class ShapedRecipeHandler extends RecipeHandler
	{
		public ShapedRecipeHandler(Class<? extends IRecipe> recipe)
		{
			super(recipe);
		}

		@Override
		public ItemStack[] getCraftingGrid(IRecipe r)
		{
			ItemStack[] stacks = new ItemStack[9];
			ShapedRecipes shaped = (ShapedRecipes)r;
			for(int j = 0;j<shaped.recipeItems.length;j++)
			{
				stacks[j] = shaped.recipeItems[j];
			}
			return stacks;
		}
	}
	
	private static class ShapelessOreRecipeHandler extends RecipeHandler
	{
		public ShapelessOreRecipeHandler(Class<? extends IRecipe> recipe)
		{
			super(recipe);
		}

		@Override
		public ItemStack[] getCraftingGrid(IRecipe r)
		{
			ItemStack[] stacks = new ItemStack[9];
			ShapelessOreRecipe shaped = (ShapelessOreRecipe)r;
			for(int j = 0;j<shaped.getInput().size();j++)
			{
				if(shaped.getInput().get(j) instanceof ItemStack)
				{
					stacks[j] = (ItemStack) shaped.getInput().get(j);
				}
				else if(shaped.getInput().get(j) instanceof ArrayList)
				{
					Object o = ((ArrayList<?>)shaped.getInput().get(j)).get(0);
					if(o instanceof ItemStack)
					{
						stacks[j] = (ItemStack)o;
					}
				}
			}
			return stacks;
		}
	}
	
	private static class ShapedOreRecipeHandler extends RecipeHandler
	{
		public ShapedOreRecipeHandler(Class<? extends IRecipe> recipe)
		{
			super(recipe);
		}

		@Override
		public ItemStack[] getCraftingGrid(IRecipe r)
		{
			ItemStack[] stacks = new ItemStack[9];
			ShapedOreRecipe shaped = (ShapedOreRecipe)r;
			for(int j = 0;j<shaped.getInput().length;j++)
			{
				if(shaped.getInput()[j] instanceof ItemStack)
				{
					stacks[j] = (ItemStack) shaped.getInput()[j];
				}
				else if(shaped.getInput()[j] instanceof ArrayList)
				{
					Object o = ((ArrayList<?>)shaped.getInput()[j]).get(0);
					if(o instanceof ItemStack)
					{
						stacks[j] = (ItemStack)o;
					}
				}
			}
			return stacks;
		}
	}
	
	private static class ShapelessRecipeHandler extends RecipeHandler
	{
		public ShapelessRecipeHandler(Class<? extends IRecipe> recipe)
		{
			super(recipe);
		}

		@Override
		public ItemStack[] getCraftingGrid(IRecipe r)
		{
			ItemStack[] stacks = new ItemStack[9];
			ShapelessRecipes shaped = (ShapelessRecipes)r;
			for(int j = 0;j<shaped.recipeItems.size();j++)
			{
				stacks[j] = (ItemStack) shaped.recipeItems.get(j);
			}
			return stacks;
		}
	}

	/**
	 * Default Recipe Handlers
	 */
	public static final RecipeHandler DEFAULT_SHAPELESS_RECIPE_HANDLER = new ShapelessRecipeHandler(ShapelessRecipes.class);
	public static final RecipeHandler DEFAULT_SHAPED_RECIPE_HANDLER = new ShapedRecipeHandler(ShapedRecipes.class);
	public static final RecipeHandler DEFAULT_SHAPELESS_ORE_RECIPE_HANDLER = new ShapelessOreRecipeHandler(ShapelessOreRecipe.class);
	public static final RecipeHandler DEFAULT_SHAPED_ORE_RECIPE_HANDLER = new ShapedOreRecipeHandler(ShapedOreRecipe.class);
	
	/**
	 * Set the default Recipe Handlers
	 */
	public static void load()
	{
		UncraftingManager.setRecipeHandler(ShapelessRecipes.class, DEFAULT_SHAPELESS_RECIPE_HANDLER);
		UncraftingManager.setRecipeHandler(ShapedRecipes.class, DEFAULT_SHAPED_RECIPE_HANDLER);
		UncraftingManager.setRecipeHandler(ShapelessOreRecipe.class, DEFAULT_SHAPELESS_ORE_RECIPE_HANDLER);
		UncraftingManager.setRecipeHandler(ShapedOreRecipe.class, DEFAULT_SHAPED_ORE_RECIPE_HANDLER);
	}
}
