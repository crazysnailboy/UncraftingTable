package org.jglrxavpok.mods.decraft.item.uncrafting.handlers.external;

import org.apache.commons.lang3.ArrayUtils;
import org.jglrxavpok.mods.decraft.common.config.ModJsonConfiguration;
import org.jglrxavpok.mods.decraft.common.config.ModJsonConfiguration.ItemMapping;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.RecipeHandlers.INBTSensitiveRecipeHandler;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.RecipeHandlers.ShapedOreRecipeHandler;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

public class TinkersRecipeHandlers
{

	public static class TableRecipeHandler extends ShapedOreRecipeHandler implements INBTSensitiveRecipeHandler
	{

		private ItemStack inputStack;

		public static Class<? extends IRecipe> recipeClass;

		static
		{
			try
			{
				recipeClass = Class.forName("slimeknights.tconstruct.tools.common.TableRecipe").asSubclass(IRecipe.class);
			}
			catch(ClassNotFoundException ex) { }
		}


		@Override
		public ItemStack[] getCraftingGrid(IRecipe r)
		{
			ItemStack[] result = super.getCraftingGrid(r);


			String itemName = Item.REGISTRY.getNameForObject(inputStack.getItem()).toString();
			if (ModJsonConfiguration.itemMappings.containsKey(itemName))
			{
				ItemMapping mapping = ModJsonConfiguration.itemMappings.get(itemName);

				if (mapping.replaceSlots != null)
				{
					ItemStack textureBlock = ItemStack.loadItemStackFromNBT(inputStack.getTagCompound().getCompoundTag("textureBlock"));
					for ( int i = 0 ; i < result.length ; i++ )
					{
						if (ArrayUtils.indexOf(mapping.replaceSlots, i) >= 0)
						{
							result[i] = textureBlock.copy();
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
