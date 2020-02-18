package org.jglrxavpok.mods.decraft.item.uncrafting.handlers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.RecipeHandlers.RecipeHandler;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.PotionUtils;
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

			if (inputStack.hasTag())
			{
				// read the nbt tag from the input item
				CompoundNBT tag = inputStack.getTag().getCompound("Explosion");

				// add a dye item of the appropriate color for each value in the colors array
				for ( int color : tag.getIntArray("Colors"))
				{
					DyeColor dyeColor = DyeColor.byId(color);
					if (dyeColor != null) recipeItems.add(new ItemStack(DyeItem.getItem(dyeColor), 1));
				}

				// add the item which corresponds to the exlosion type
				switch (tag.getInt("Type"))
				{
					case 0: break;
					case 1: recipeItems.add(new ItemStack(Items.FIRE_CHARGE, 1)); break;
					case 2: recipeItems.add(new ItemStack(Items.GOLD_NUGGET, 1)); break;
					case 3: recipeItems.add(new ItemStack(Items.SKELETON_SKULL, 1)); break;
					case 4: recipeItems.add(new ItemStack(Items.FEATHER, 1)); break;
				}

				// if the explosion has a trail, add a diamond
				if (tag.contains("Trail") && tag.getByte("Trail") == 1) recipeItems.add(new ItemStack(Items.DIAMOND, 1));
				// if the explosion has a flicker, add a glowstone dust
				if (tag.contains("Flicker") && tag.getByte("Flicker") == 1) recipeItems.add(new ItemStack(Items.GLOWSTONE_DUST, 1));
			}

			return recipeItems;
		}


		private List<ItemStack> getFireworkRocketItems()
		{
			List<ItemStack> recipeItems = new ArrayList<ItemStack>();

			if (inputStack.hasTag())
			{
				// read the nbt tag from the input item
				CompoundNBT tag = inputStack.getTag().getCompound("Fireworks");

				// add the gunpowder
				if (tag.contains("Flight")) recipeItems.add(new ItemStack(Items.GUNPOWDER, tag.getInt("Flight")));

				// add the paper
				recipeItems.add(new ItemStack(Items.PAPER, 1));

				// add a firework star for each explosion in the explosions array
				ListNBT explosions = tag.getList("Explosions", 10);

				for ( int i = 0 ; i < explosions.size() ; i++ )
				{
					CompoundNBT explosion = explosions.getCompound(i);

					ItemStack stack = new ItemStack(Items.FIREWORK_STAR, 1);
					CompoundNBT stackTag = new CompoundNBT();
					stackTag.put("Explosion", explosion);
					stack.setTag(stackTag);

					recipeItems.add(stack);
				}

			}

			return recipeItems;
		}


		@Override
		public NonNullList<NonNullList<ItemStack>> getCraftingGrids(IRecipe r)
		{
			NonNullList<ItemStack> recipeItems = NonNullList.<ItemStack>create();

	        if (inputStack.getItem() == Items.FIREWORK_STAR) recipeItems.addAll(getFireworkStarItems());
	        if (inputStack.getItem() == Items.FIREWORK_ROCKET) recipeItems.addAll(getFireworkRocketItems());

	        return NonNullList.withSize(1, recipeItems);
		}
	}


	/**
	 * Handler for tipped arrow recipes which utilise the RecipeTippedArrow IRecipe implementation
	 *
	 */
	public static class TippedArrowRecipeHandler extends NBTSensitiveRecipeHandler
	{
		@Override
		public NonNullList<NonNullList<ItemStack>> getCraftingGrids(IRecipe r)
		{
			NonNullList<ItemStack> recipeItems = NonNullList.<ItemStack>create();

			for ( int i = 0 ; i < 9 ; i++ )
			{
				if (i != 4)
				{
					recipeItems.add(new ItemStack(Items.ARROW, 1));
				}
				else
				{
					ItemStack stack = new ItemStack(Items.LINGERING_POTION, 1);
		            PotionUtils.addPotionToItemStack(stack, PotionUtils.getPotionFromItem(inputStack));
		            PotionUtils.appendEffects(stack, PotionUtils.getFullEffectsFromItem(inputStack));
					recipeItems.add(stack);
				}
			}

			return NonNullList.withSize(1, recipeItems);
		}
	}

}
