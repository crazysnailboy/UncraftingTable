package org.jglrxavpok.mods.decraft.item.uncrafting.handlers.external;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.RecipeHandlers.RecipeHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;

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

		private static List<List<ItemStack>> replaceRecipeInputs(List list)
		{
			try
			{
				// *** modified from ic2.jeiIntegration.recipe.crafting.AdvRecipeWrapper ***
				List<List<ItemStack>> out = new ArrayList(list.size());
				for (Object recipe : list) // for (IRecipeInput recipe : list)
				{
					if (recipe == null)
					{
						out.add(java.util.Collections.<ItemStack>emptyList()); // out.add(Collections.emptyList());
					}
					else
					{
						List<ItemStack> replace = new ArrayList<ItemStack>((List<ItemStack>)(Class.forName("ic2.api.recipe.IRecipeInput").getMethod("getInputs", (Class[])null).invoke(recipe))); // List<ItemStack> replace = new ArrayList(recipe.getInputs());
						for (ListIterator<ItemStack> it = replace.listIterator(); it.hasNext();)
						{
							ItemStack stack = it.next();
							if ((stack != null) && (Class.forName("ic2.api.item.IElectricItem").isInstance(stack.getItem()))) // if ((stack != null) && ((stack.getItem() instanceof IElectricItem)))
							{
								it.set(stack.copy()); // it.set(StackUtil.copyWithWildCard(stack));
							}
						}
						out.add(replace);
					}
				}
				return out;
				// *** modified from ic2.jeiIntegration.recipe.crafting.AdvRecipeWrapper ***
			}
			catch (Exception ex) { return null; }
		}

		private static List<List<ItemStack>> getInputs(IRecipe r)
		{
			try
			{
				// *** modified from ic2.jeiIntegration.recipe.crafting.AdvRecipeWrapper ***
				Object[] input = (Object[])recipeClass.getField("input").get(r);
				int[] masks = (int[])recipeClass.getField("masks").get(r);
				int inputWidth = (Integer)(recipeClass.getField("inputWidth").get(r));
				int inputHeight = (Integer)(recipeClass.getField("inputHeight").get(r));

				int mask = masks[0]; // int mask = this.recipe.masks[0];
				int itemIndex = 0;
				List ret = new ArrayList(); // List<IRecipeInput> ret = new ArrayList();
				for (int i = 0; i < 9; i++)
				{
					if ((i % 3 < inputWidth) && (i / 3 < inputHeight)) // if ((i % 3 < this.recipe.inputWidth) && (i / 3 < this.recipe.inputHeight))
					{
						if ((mask >>> 8 - i & 0x1) != 0)
						{
							ret.add(input[(itemIndex++)]); // ret.add(this.recipe.input[(itemIndex++)]);
						}
						else
						{
							ret.add(null);
						}
					}
				}
				return replaceRecipeInputs(ret);
				// *** modified from ic2.jeiIntegration.recipe.crafting.AdvRecipeWrapper ***
			}
			catch (Exception ex) { return null; }
		}

		@Override
		public NonNullList<ItemStack> getCraftingGrid(IRecipe r)
		{
			List<ItemStack> itemStacks = new ArrayList<ItemStack>();

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
						itemStacks.add(ItemStack.EMPTY);
					}
				}
			}

			try
			{
				int inputWidth = (Integer)(recipeClass.getField("inputWidth").get(r));
				int inputHeight = (Integer)(recipeClass.getField("inputHeight").get(r));

				return reshapeRecipe(copyRecipeStacks(itemStacks), inputWidth, inputHeight);
			}
			catch (Exception ex)
			{
				return NonNullList.<ItemStack>withSize(9, ItemStack.EMPTY);
			}
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

		private static List<List<ItemStack>> getInputs(IRecipe r)
		{
			try
			{
				// *** modified from ic2.jeiIntegration.recipe.crafting.AdvShapelessRecipeWrapper ***
				List<List<ItemStack>> ret = new ArrayList();
				for (Object input : (Object[])recipeClass.getField("input").get(r)) // for (IRecipeInput input : this.recipe.input)
				{
					ret.add( (List<ItemStack>)(Class.forName("ic2.api.recipe.IRecipeInput").getMethod("getInputs", (Class[])null).invoke(input)) ); // ret.add(input.getInputs());
			    }
			    return ret;
				// *** modified from ic2.jeiIntegration.recipe.crafting.AdvShapelessRecipeWrapper ***
			}
			catch(Exception ex){ return null; }
		}


		@Override
		public NonNullList<ItemStack> getCraftingGrid(IRecipe r)
		{
			try
			{
				NonNullList<ItemStack> recipeStacks = NonNullList.<ItemStack>create();
				for ( List<ItemStack> input : getInputs(r) )
				{
					recipeStacks.add(input.size() > 0 ? input.get(0) : ItemStack.EMPTY);
				}
				return copyRecipeStacks(recipeStacks);
			}
			catch (Exception ex)
			{
				ModUncrafting.instance.getLogger().catching(ex);
			}
			return NonNullList.<ItemStack>create();
		}
	}

}
