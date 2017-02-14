package org.jglrxavpok.mods.decraft.item.uncrafting.handlers;


import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
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
		public abstract NonNullList<ItemStack> getCraftingGrid(IRecipe s);

		/**
		 * Takes a list of ItemStacks from a shaped recipe and correctly positions them according to the recipe width and height
		 */
		protected static NonNullList<ItemStack> reshapeRecipe(List<ItemStack> recipeItems, int recipeWidth, int recipeHeight)
		{
			NonNullList<ItemStack> stacks = NonNullList.<ItemStack>withSize(9, ItemStack.EMPTY);
			for ( int row = 0 ; row < recipeHeight ; row++ )
			{
				for (int col = 0 ; col < recipeWidth ; col++ )
				{
					stacks.set((row * 3) + col, recipeItems.get(col + row * recipeWidth));
				}
			}
			return stacks;
		}


		/**
		 * Converts a collection of OreDictionary recipe items into a list of ItemStacks
		 */
		protected static NonNullList<ItemStack> getOreRecipeItems(List<Object> itemObjects)
		{
			NonNullList<ItemStack> itemStacks = NonNullList.<ItemStack>withSize(9, ItemStack.EMPTY);
			for ( int i = 0 ; i < itemObjects.size() ; i++ )
			{
				Object itemObject = itemObjects.get(i);
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
						return NonNullList.<ItemStack>create();
					}

					itemStack = ((List<ItemStack>)itemObject).get(0);
				}
				else itemStack = ItemStack.EMPTY;

				itemStacks.set(i, itemStack);
			}
			return itemStacks;
		}


		/**
		 * Copies the ItemStacks in a list to a new list, whilst normalising the item damage for the OreDictionary wildcard value
		 */
		protected static NonNullList<ItemStack> copyRecipeStacks(List<ItemStack> inputStacks)
		{
			NonNullList<ItemStack> outputStacks = NonNullList.<ItemStack>withSize(9, ItemStack.EMPTY);

			for ( int i = 0 ; i < inputStacks.size() ; i++ )
			{
				ItemStack outputStack = inputStacks.get(i).copy();
				if (outputStack.getItemDamage() == Short.MAX_VALUE) outputStack.setItemDamage(0);
				outputStacks.set(i, outputStack);
			}

			return outputStacks;
		}

	}



	/**
	 * Handler for vanilla Minecraft shaped recipes
	 *
	 */
	public static class ShapedRecipeHandler extends RecipeHandler
	{
		@Override
		public NonNullList<ItemStack> getCraftingGrid(IRecipe r)
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
		public NonNullList<ItemStack> getCraftingGrid(IRecipe r)
		{
			// cast the IRecipe instance
			ShapelessRecipes shapelessRecipe = (ShapelessRecipes)r;

			// get a copy of the recipe items with normalised metadata
			NonNullList<ItemStack> recipeItems = copyRecipeStacks(shapelessRecipe.recipeItems);

			// return the itemstacks
			return recipeItems;
		}
	}


	/**
	 * Handler for shaped recipes which utilise the Forge Ore Dictionary
	 *
	 */
	public static class ShapedOreRecipeHandler extends RecipeHandler
	{
		@Override
		public NonNullList<ItemStack> getCraftingGrid(IRecipe r)
		{
			// cast the IRecipe instance
			ShapedOreRecipe shapedRecipe = (ShapedOreRecipe)r;

			// obtain the recipe items and the recipe dimensions
			List<ItemStack> recipeItems = copyRecipeStacks(getOreRecipeItems(Arrays.asList(shapedRecipe.getInput())));

			if (!recipeItems.isEmpty())
			{
				// get the recipe dimensions
				int recipeWidth = shapedRecipe.getWidth();
				int recipeHeight = shapedRecipe.getHeight();

				// rearrange the itemstacks according to the recipe width and height
				return reshapeRecipe(recipeItems, recipeWidth, recipeHeight);
			}
			else return NonNullList.<ItemStack>create();
		}
	}


	/**
	 * Handler for shapeless recipes which utilise the Forge Ore Dictionary
	 *
	 */
	public static class ShapelessOreRecipeHandler extends RecipeHandler
	{
		@Override
		public NonNullList<ItemStack> getCraftingGrid(IRecipe r)
		{
			// cast the IRecipe instance
			ShapelessOreRecipe shapelessRecipe = (ShapelessOreRecipe)r;

			// get a copy of the recipe items with normalised metadata
			NonNullList<ItemStack> recipeItems = copyRecipeStacks(getOreRecipeItems(shapelessRecipe.getInput()));

			if (!recipeItems.isEmpty())
			{
				// return the itemstacks
				return recipeItems;
			}
			else return NonNullList.<ItemStack>create();
		}
	}



}
