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
import net.minecraft.util.NonNullList;


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
			recipeItems.add(new ItemStack(Items.GUNPOWDER, 1));

			if (inputStack.hasTagCompound())
			{
				// read the nbt tag from the input item
				NBTTagCompound tag = inputStack.getTagCompound().getCompoundTag("Explosion");

				// add a dye item of the appropriate color for each value in the colors array
				for ( int color : tag.getIntArray("Colors"))
				{
					int index = ArrayUtils.indexOf(ItemDye.DYE_COLORS, color);
					if (index >= 0) recipeItems.add(new ItemStack(Items.DYE, 1, index));
				}

				// add the item which corresponds to the exlosion type
				switch (tag.getInteger("Type"))
				{
					case 0: break;
					case 1: recipeItems.add(new ItemStack(Items.FIRE_CHARGE, 1)); break;
					case 2: recipeItems.add(new ItemStack(Items.GOLD_NUGGET, 1)); break;
					case 3: recipeItems.add(new ItemStack(Items.SKULL, 1, 4)); break;
					case 4: recipeItems.add(new ItemStack(Items.FEATHER, 1)); break;
				}

				// if the explosion has a trail, add a diamond
				if (tag.hasKey("Trail") && tag.getByte("Trail") == 1) recipeItems.add(new ItemStack(Items.DIAMOND, 1));
				// if the explosion has a flicker, add a glowstone dust
				if (tag.hasKey("Flicker") && tag.getByte("Flicker") == 1) recipeItems.add(new ItemStack(Items.GLOWSTONE_DUST, 1));
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
				if (tag.hasKey("Flight")) recipeItems.add(new ItemStack(Items.GUNPOWDER, tag.getInteger("Flight")));

				// add the paper
				recipeItems.add(new ItemStack(Items.PAPER, 1));

				// add a firework star for each explosion in the explosions array
				NBTTagList explosions = tag.getTagList("Explosions", 10);

				for ( int i = 0 ; i < explosions.tagCount() ; i++ )
				{
					NBTTagCompound explosion = explosions.getCompoundTagAt(i);

					ItemStack stack = new ItemStack(Items.FIREWORK_CHARGE, 1);
					NBTTagCompound stackTag = new NBTTagCompound();
					stackTag.setTag("Explosion", explosion);
					stack.setTagCompound(stackTag);

					recipeItems.add(stack);
				}

			}

			return recipeItems;
		}


		@Override
		public NonNullList<ItemStack> getCraftingGrid(IRecipe r)
		{
			NonNullList<ItemStack> recipeItems = NonNullList.<ItemStack>create();

	        if (inputStack.getItem() == Items.FIREWORK_CHARGE) recipeItems.addAll(getFireworkStarItems());
	        if (inputStack.getItem() == Items.FIREWORKS) recipeItems.addAll(getFireworkRocketItems());

	        return recipeItems;
		}
	}

}
