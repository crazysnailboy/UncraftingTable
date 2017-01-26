package org.jglrxavpok.mods.decraft.item.uncrafting;


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
		@Override
		public NonNullList<ItemStack> getCraftingGrid(IRecipe r)
		{
			// ShapelessRecipes.recipeItems is a List<ItemStack>, so convert it to an NonNullList<ItemStack> and return
			NonNullList<ItemStack> stacks = NonNullList.<ItemStack>create();
			stacks.addAll(Lists.newArrayList(Iterables.filter(((ShapelessRecipes)r).recipeItems, ItemStack.class)));
			return stacks;
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
			List<ItemStack> recipeItems = getOreRecipeItems(Arrays.asList(shapedRecipe.getInput()));
			if (!recipeItems.isEmpty())
			{
				int recipeWidth = shapedRecipe.getWidth(); // int recipeWidth = ((Integer)(ObfuscationReflectionHelper.getPrivateValue(ShapedOreRecipe.class, shapedRecipe, "width"))).intValue();
				int recipeHeight = shapedRecipe.getHeight(); // int recipeHeight = ((Integer)(ObfuscationReflectionHelper.getPrivateValue(ShapedOreRecipe.class, shapedRecipe, "height"))).intValue();

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
			NonNullList<ItemStack> recipeItems = getOreRecipeItems(((ShapelessOreRecipe)r).getInput());
			if (!recipeItems.isEmpty())
			{
				return recipeItems;
			}
			else return NonNullList.<ItemStack>create();
		}
	}
	
	
	/**
	 * Takes a list of ItemStacks from a shaped recipe and correctly positions them according to the recipe width and height
	 */
	private static NonNullList<ItemStack> reshapeRecipe(List<ItemStack> recipeItems, int recipeWidth, int recipeHeight) 
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
	private static NonNullList<ItemStack> getOreRecipeItems(List<Object> itemObjects)
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
			
			if (itemStack != ItemStack.EMPTY && itemStack.getItemDamage() == Short.MAX_VALUE) itemStack.setItemDamage(0); 
			itemStacks.set(i, itemStack);
		}
		return itemStacks;
	}
	

}
