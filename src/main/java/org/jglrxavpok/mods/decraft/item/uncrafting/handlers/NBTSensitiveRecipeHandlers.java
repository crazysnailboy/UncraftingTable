package org.jglrxavpok.mods.decraft.item.uncrafting.handlers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.RecipeHandlers.RecipeHandler;

import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public final class NBTSensitiveRecipeHandlers
{

	/**
	 * Interface for a recipe handler where the recipe output depends on the NBT data of the input ItemStack
	 *
	 */
	public static abstract interface INBTSensitiveRecipeHandler
	{
		void setInputStack(ItemStack stack);

		ItemStack getInputStack();
	}


	/**
	 * Abstract base class extending the base recipe handler implementing the INBTSensitiveRecipeHandler interface
	 *
	 */
	public static abstract class NBTSensitiveRecipeHandler extends RecipeHandler implements INBTSensitiveRecipeHandler
	{
		protected ItemStack inputStack;

		@Override
		public void setInputStack(ItemStack stack){ inputStack = stack; }

		@Override
		public ItemStack getInputStack(){ return inputStack; }

	}


	/**
	 * Handler for fireworks recipes which utilise the RecipeFireworks IRecipe implementation
	 *
	 */
	public static class FireworksRecipeHandler extends NBTSensitiveRecipeHandler
	{

		private List<ItemStack> getFireworkStarItems()
		{
			List<ItemStack> recipeItems = new ArrayList<ItemStack>();

			// add the gunpowder
			recipeItems.add(new ItemStack(Items.gunpowder, 1));

			if (inputStack.hasTagCompound())
			{
				// read the nbt tag from the input item
				NBTTagCompound tag = inputStack.getTagCompound().getCompoundTag("Explosion");

				// add a dye item of the appropriate color for each value in the colors array
				for ( int color : tag.getIntArray("Colors"))
				{
					int index = ArrayUtils.indexOf(ItemDye.field_150922_c, color);
					if (index >= 0) recipeItems.add(new ItemStack(Items.dye, 1, index));
				}

				// add the item which corresponds to the exlosion type
				switch (tag.getInteger("Type"))
				{
					case 0: break;
					case 1: recipeItems.add(new ItemStack(Items.fire_charge, 1)); break;
					case 2: recipeItems.add(new ItemStack(Items.gold_nugget, 1)); break;
					case 3: recipeItems.add(new ItemStack(Items.skull, 1, 4)); break;
					case 4: recipeItems.add(new ItemStack(Items.feather, 1)); break;
				}

				// if the explosion has a trail, add a diamond
				if (tag.hasKey("Trail") && tag.getByte("Trail") == 1) recipeItems.add(new ItemStack(Items.diamond, 1));
				// if the explosion has a flicker, add a glowstone dust
				if (tag.hasKey("Flicker") && tag.getByte("Flicker") == 1) recipeItems.add(new ItemStack(Items.glowstone_dust, 1));
			}

			return recipeItems;
		}


		private List<ItemStack> getFireworkRocketItems()
		{
			List<ItemStack> recipeItems = new ArrayList<ItemStack>();

			if (inputStack.hasTagCompound())
			{
				// read the nbt tag from the input item
				NBTTagCompound tag = inputStack.getTagCompound().getCompoundTag("Fireworks");

				// add the gunpowder
				if (tag.hasKey("Flight")) recipeItems.add(new ItemStack(Items.gunpowder, tag.getInteger("Flight")));

				// add the paper
				recipeItems.add(new ItemStack(Items.paper, 1));

				// add a firework star for each explosion in the explosions array
				NBTTagList explosions = tag.getTagList("Explosions", 10);

				for ( int i = 0 ; i < explosions.tagCount() ; i++ )
				{
					NBTTagCompound explosion = explosions.getCompoundTagAt(i);

					ItemStack stack = new ItemStack(Items.firework_charge, 1);
					NBTTagCompound stackTag = new NBTTagCompound();
					stackTag.setTag("Explosion", explosion);
					stack.setTagCompound(stackTag);

					recipeItems.add(stack);
				}

			}

			return recipeItems;
		}


		@Override
		public ItemStack[] getCraftingGrid(IRecipe r)
		{
			List<ItemStack> recipeItems = new ArrayList<ItemStack>();

	        if (inputStack.getItem() == Items.firework_charge) recipeItems.addAll(getFireworkStarItems());
	        if (inputStack.getItem() == Items.fireworks) recipeItems.addAll(getFireworkRocketItems());

			return recipeItems.toArray(new ItemStack[9]);
		}
	}

}
