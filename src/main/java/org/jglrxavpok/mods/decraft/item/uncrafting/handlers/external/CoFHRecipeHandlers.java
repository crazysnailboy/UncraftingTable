package org.jglrxavpok.mods.decraft.item.uncrafting.handlers.external;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.registries.ForgeRegistries;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.NBTSensitiveRecipeHandlers.INBTSensitiveRecipeHandler;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.RecipeHandlers.RecipeHandler;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;


/**
 * Handlers for IRecipe implementations from the Team CoFH suite of mods
 *
 */
public class CoFHRecipeHandlers
{

	/**
	 * Handler for Thermal Dynamics Covers
	 *
	 */
	public static class CoverRecipeHandler extends RecipeHandler implements INBTSensitiveRecipeHandler
	{

		public static final Class<? extends IRecipe> recipeClass = getRecipeClass("cofh.thermaldynamics.util.RecipeCover");

		private ItemStack inputStack;


		@Override
		public NonNullList<NonNullList<ItemStack>> getCraftingGrids(IRecipe r)
		{
			NonNullList<ItemStack> recipeStacks = NonNullList.<ItemStack>withSize(9, ItemStack.EMPTY);

			recipeStacks.set(0, new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("thermaldynamics:duct_48")), 1));

			CompoundNBT tag = this.inputStack.getTag();
			recipeStacks.set(1, new ItemStack(ForgeRegistries.BLOCKS.getValue((new ResourceLocation(tag.getString("Block"))))));

			return NonNullList.withSize(1, recipeStacks);
		}

		@Override
		public void setInputStack(ItemStack stack) { this.inputStack = stack; }

		@Override
		public ItemStack getInputStack() { return this.inputStack; }

	}
}
