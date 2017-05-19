package org.jglrxavpok.mods.decraft.item.uncrafting.handlers.external;

import java.util.Arrays;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.RecipeHandlers.RecipeHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;

public class IGCMRecipeHandlers
{

	/**
	 * Handler for shaped recipes added by the InGameConfigManager mod
	 *
	 */
	public static class ShapedIGCMRecipeHandler extends RecipeHandler
	{
		public static Class<? extends IRecipe> recipeClass;

		static
		{
			try
			{
				recipeClass = Class.forName("com.creativemd.creativecore.common.recipe.BetterShapedRecipe").asSubclass(IRecipe.class);
			}
			catch(ClassNotFoundException ex) { }
		}

		@Override
		public NonNullList<ItemStack> getCraftingGrid(IRecipe r)
		{
			try
			{
				ItemStack[] recipeItems = (ItemStack[])recipeClass.getMethod("getInput", (Class[])null).invoke(r);
				int recipeWidth = (Integer)(recipeClass.getField("width").get(r));
				int recipeHeight = (Integer)(recipeClass.getField("height").get(r));

				return reshapeRecipe(copyRecipeStacks(Arrays.asList(recipeItems)), recipeWidth, recipeHeight);
			}
			catch (Exception ex)
			{
				return NonNullList.<ItemStack>withSize(9, ItemStack.EMPTY);
			}
		}

	}


	/**
	 * Handler for shapeless recipes added by the InGameConfigManager mod
	 *
	 */
	public static class ShapelessIGCMRecipeHandler extends RecipeHandler
	{
		public static Class<? extends IRecipe> recipeClass;

		static
		{
			try
			{
				recipeClass = Class.forName("com.creativemd.creativecore.common.recipe.BetterShapelessRecipe").asSubclass(IRecipe.class);
			}
			catch(ClassNotFoundException ex) { }
		}

		@Override
		public NonNullList<ItemStack> getCraftingGrid(IRecipe r)
		{
			try
			{
				ItemStack[] recipeItems = (ItemStack[])recipeClass.getMethod("getInput", (Class[])null).invoke(r);
				return copyRecipeStacks(Arrays.asList(recipeItems));
			}
			catch(Exception ex)
			{
				return NonNullList.<ItemStack>withSize(9, ItemStack.EMPTY);
			}
		}

	}

}
