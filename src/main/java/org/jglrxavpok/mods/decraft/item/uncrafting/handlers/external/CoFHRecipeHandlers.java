package org.jglrxavpok.mods.decraft.item.uncrafting.handlers.external;

import java.util.ArrayList;
import java.util.List;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.NBTSensitiveRecipeHandlers.INBTSensitiveRecipeHandler;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.RecipeHandlers.RecipeHandler;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
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
		public ItemStack[] getCraftingGrid(IRecipe r)
		{
			List<ItemStack> recipeStacks = new ArrayList<ItemStack>();

			recipeStacks.add(new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("thermaldynamics:ThermalDynamics_48")), 1));

			NBTTagCompound tag = this.inputStack.getTagCompound();
			recipeStacks.add(new ItemStack(Block.REGISTRY.getObject(new ResourceLocation(tag.getString("Block"))), 1, tag.getInteger("Meta")));

			return recipeStacks.toArray(new ItemStack[9]);
		}

		@Override
		public void setInputStack(ItemStack stack) { this.inputStack = stack; }

		@Override
		public ItemStack getInputStack() { return this.inputStack; }

	}
}
