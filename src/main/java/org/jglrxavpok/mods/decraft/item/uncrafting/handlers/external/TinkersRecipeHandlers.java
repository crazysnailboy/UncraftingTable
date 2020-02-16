package org.jglrxavpok.mods.decraft.item.uncrafting.handlers.external;

import org.apache.commons.lang3.ArrayUtils;
import org.jglrxavpok.mods.decraft.common.config.ModJsonConfiguration;
import org.jglrxavpok.mods.decraft.common.config.ModJsonConfiguration.ItemMapping;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.NBTSensitiveRecipeHandlers.INBTSensitiveRecipeHandler;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.RecipeHandlers;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;


/**
 * Handlers for IRecipe implementations from the Tinker's Construct mod
 *
 */
public class TinkersRecipeHandlers
{

	/**
	 * Handler for Part Builders, Stencil Tables, Tool Forges and Tool Tables
	 *
	 */
	public static class TableRecipeHandler extends RecipeHandlers.ShapedRecipeHandler implements INBTSensitiveRecipeHandler
	{

		public static final Class<? extends IRecipe> recipeClass = getRecipeClass("slimeknights.tconstruct.tools.common.TableRecipeFactory$TableRecipe");

		private ItemStack inputStack;


		@Override
		public NonNullList<ItemStack> getCraftingGrid(IRecipe r)
		{
			NonNullList<ItemStack> result = super.getCraftingGrid(r);

			ItemMapping mapping = ModJsonConfiguration.ITEM_MAPPINGS.get(inputStack);
			if (mapping != null)
			{
				if (mapping.replaceSlots != null)
				{
					ItemStack textureBlock = ItemStack.read(inputStack.getTag().getCompound("textureBlock"));
					for ( int i = 0 ; i < result.size() ; i++ )
					{
						if (ArrayUtils.indexOf(mapping.replaceSlots, i) >= 0)
						{
							result.set(i, textureBlock.copy());
						}
					}
				}
			}

			return result;
		}


		@Override
		public void setInputStack(ItemStack stack) { this.inputStack = stack; }

		@Override
		public ItemStack getInputStack() { return this.inputStack; }

	}

}
