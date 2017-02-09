package org.jglrxavpok.mods.decraft.inventory;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

/**
 * ItemStack helper methods to replicate functionality from the 1.9+ ItemStack class
 */
public class ItemStackHelper 
{

	/**
	 * Compares two ItemStack instances to determine whether the items are the same, ignoring any difference in durability
	 */
	public static boolean areItemsEqualIgnoreDurability(@Nullable ItemStack stackA, @Nullable ItemStack stackB)
	{
		return stackA == stackB ? true : (stackA != null && stackB != null ? (!stackA.isItemStackDamageable() ? stackA.isItemEqual(stackB) : stackB != null && stackA.getItem() == stackB.getItem()) : false);
	}

}
