package org.jglrxavpok.mods.decraft.item.uncrafting.handlers.external;

import java.util.ArrayList;
import java.util.List;

import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.RecipeHandlers.RecipeHandler;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

public class IC2RecipeHandlers
{

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
				recipeClass = Class.forName("ic2.core.AdvRecipe").asSubclass(IRecipe.class);
			}
			catch(ClassNotFoundException ex) { }
		}


		@Override
		public ItemStack[] getCraftingGrid(IRecipe r)
		{
			List<ItemStack> itemStacks = new ArrayList<ItemStack>();
			try
			{
				Object[] input = (Object[])recipeClass.getField("input").get(r);
				int[] masks = (int[])recipeClass.getField("masks").get(r);

				// *** copied from ic2.neiIntegration.core.recipehandler.AdvRecipeHandler ***
				List<ItemStack>[] items = new List[9];
				int i = 0;
				for (int j = 0; i < 9; i++)
				{
					if ((masks[0] & 1 << 8 - i) != 0)
					{
						List<ItemStack> inputs = (List<ItemStack>)(recipeClass.getMethod("expand", Object.class).invoke(r, input[j]));
						if (inputs.isEmpty()) break;
						items[i] = inputs;
						j++;
					}
				}
				// *** copied from ic2.neiIntegration.core.recipehandler.AdvRecipeHandler ***

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
				recipeClass = Class.forName("ic2.core.AdvShapelessRecipe").asSubclass(IRecipe.class);
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

}
