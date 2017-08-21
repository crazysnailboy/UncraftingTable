package org.jglrxavpok.mods.decraft.item.uncrafting.handlers.external;

import java.util.ArrayList;
import java.util.List;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.RecipeHandlers.RecipeHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;


/**
 * Handlers for IRecipe implementations from the Mekanism mod
 *
 */
public class MekanismRecipeHandlers
{

	/**
	 * Handler for shaped recipes from the Mekanism mod
	 *
	 */
	public static class ShapedMekanismRecipeHandler extends RecipeHandler
	{

		public static final Class<? extends IRecipe> recipeClass = getRecipeClass("mekanism.common.recipe.ShapedMekanismRecipe");


		@Override
		public NonNullList<ItemStack> getCraftingGrid(IRecipe r)
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
					else itemStack = ItemStack.EMPTY;

					itemStacks.add(itemStack);
				}
			}
			catch(Exception ex) { }
			return copyRecipeStacks(itemStacks);
		}

	}


	/**
	 * Handler for shapeless recipes from the Mekanism mod
	 *
	 */
	public static class ShapelessMekanismRecipeHandler extends RecipeHandler
	{

		public static final Class<? extends IRecipe> recipeClass = getRecipeClass("mekanism.common.recipe.ShapelessMekanismRecipe");


		@Override
		public NonNullList<ItemStack> getCraftingGrid(IRecipe r)
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
			return copyRecipeStacks(itemStacks);
		}

	}

}