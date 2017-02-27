package org.jglrxavpok.mods.decraft.item.uncrafting.handlers.external;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.RecipeHandlers.RecipeHandler;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

public class AE2RecipeHandlers
{

	/**
	 * Handler for shaped recipes from the Applied Energistics 2 mod
	 *
	 */
	public static class ShapedAE2RecipeHandler extends RecipeHandler
	{
		public static Class<? extends IRecipe> recipeClass;

		static
		{
			try
			{
				recipeClass = Class.forName("appeng.recipes.game.ShapedRecipe").asSubclass(IRecipe.class);
			}
			catch(ClassNotFoundException ex) { }
		}


		private List<List<ItemStack>> getIngredients(IRecipe r)
		{
			try
			{
				// *** adapted from appeng.integration.modules.jei.ShapedRecipeWrapper ***
				Object[] items = (Object[])(recipeClass.getMethod("getIngredients", (Class[])null).invoke(r));
				int width = (Integer)(recipeClass.getMethod("getWidth", (Class[])null).invoke(r));
				int height = (Integer)(recipeClass.getMethod("getHeight", (Class[])null).invoke(r));

				List<List<ItemStack>> in = new ArrayList(width * height);
				for (int x = 0; x < width; x++)
				{
					for (int y = 0; y < height; y++)
					{
						if (items[(x * height + y)] != null)
						{
							Object ing = items[(x * height + y)]; // IIngredient ing = (IIngredient)items[(x * height + y)];
							List<ItemStack> slotList = Collections.emptyList();

							try
							{
								ItemStack[] is = (ItemStack[])(ing.getClass().getMethod("getItemStackSet", (Class[])null).invoke(ing)); // ItemStack[] is = ing.getItemStackSet();
								slotList = Arrays.asList(is);
							}
							catch (Exception ex) { }
							in.add(slotList);
						}
						else in.add(new ArrayList<ItemStack>());
					}
				}
				// *** adapted from appeng.integration.modules.jei.ShapedRecipeWrapper ***
				return in;
			}
			catch (Exception ex) { return null; }
		}

		private static ItemStack[] reshapeRecipe(List<ItemStack> recipeItems, IRecipe r)
		{
			try
			{
				int width = (Integer)(recipeClass.getMethod("getWidth", (Class[])null).invoke(r));
				int height = (Integer)(recipeClass.getMethod("getHeight", (Class[])null).invoke(r));

				return reshapeRecipe(recipeItems, width, height);
			}
			catch(Exception ex) { return recipeItems.toArray(new ItemStack[9]); }
		}


		@Override
		public ItemStack[] getCraftingGrid(IRecipe r)
		{
			List<ItemStack> itemStacks = new ArrayList<ItemStack>();
			try
			{
				List<List<ItemStack>> items = getIngredients(r);
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
			return reshapeRecipe(copyRecipeStacks(itemStacks), r);
		}

	}


	/**
	 * Handler for shapeless recipes from the Applied Energistics 2 mod
	 *
	 */
	public static class ShapelessAE2RecipeHandler extends RecipeHandler
	{
		public static Class<? extends IRecipe> recipeClass;

		static
		{
			try
			{
				recipeClass = Class.forName("appeng.recipes.game.ShapelessRecipe").asSubclass(IRecipe.class);
			}
			catch(ClassNotFoundException ex) { }
		}

		private List<List<ItemStack>> getIngredients(IRecipe r)
		{
			try
			{
				Class IIngredient = Class.forName("appeng.api.recipes.IIngredient");

				// *** adapted from appeng.integration.modules.jei.ShapelessRecipeWrapper ***
				List<Object> recipeInput = (List<Object>)(recipeClass.getMethod("getInput", (Class[])null).invoke(r));
				List<List<ItemStack>> inputs = new ArrayList(recipeInput.size());

				for (Object inputObj : recipeInput)
				{
					if (IIngredient.isInstance(inputObj))
					{
						try
						{
							ItemStack[] is = (ItemStack[])(inputObj.getClass().getMethod("getItemStackSet", (Class[])null).invoke(inputObj));
							inputs.add(Lists.newArrayList(is));
						}
						catch (Exception ex) { ModUncrafting.instance.getLogger().catching(ex); }
					}
				}
				// *** adapted from appeng.integration.modules.jei.ShapelessRecipeWrapper ***
				return inputs;
			}
			catch (Exception ex) { return null; }
		}

		@Override
		public ItemStack[] getCraftingGrid(IRecipe r)
		{
			List<ItemStack> itemStacks = new ArrayList<ItemStack>();
			try
			{
				for (Object itemObject : getIngredients(r))
				{
					ItemStack itemStack;

					if (itemObject instanceof ItemStack)
					{
						itemStack = (ItemStack)itemObject;
					}
					else if (itemObject instanceof ArrayList)
					{
						itemStack = ((ArrayList<ItemStack>)itemObject).get(0);
					}
					else itemStack = null;

					itemStacks.add(itemStack);
				}
			}
			catch(Exception ex) { }
			return copyRecipeStacks(itemStacks).toArray(new ItemStack[9]);
		}

	}

}
