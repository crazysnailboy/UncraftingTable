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
			else if (recipeItem instanceof ArrayList)
			{
				recipeStacks.add(((ArrayList<ItemStack>)recipeItem).get(0));
			}
			else if (recipeItem == null)
			{
				recipeStacks.add((ItemStack)null);
			}
		}
		return recipeStacks;
	}
	
	
}
