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
			catch(Exception ex) { return null; }
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
						out.add(null);
					}
					else
					{
						List<ItemStack> replace = (List<ItemStack>)(Class.forName("ic2.api.recipe.IRecipeInput").getMethod("getInputs", (Class[])null).invoke(recipe)); // List<ItemStack> replace = recipe.getInputs();
						for (int i = 0; i < replace.size(); i++)
						{
							ItemStack stack = (ItemStack)replace.get(i);
							if ((stack != null) && (Class.forName("ic2.api.item.IElectricItem").isInstance(stack.getItem())))
							{
								replace.set(i, stack.copy());
							}
						}
						out.add(replace);
					}
				}
				return out;
				// *** copied from ic2.jeiIntegration.recipe.crafting.AdvRecipeWrapper ***
			}
			catch(Exception ex) { return null; }
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

}
