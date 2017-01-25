package org.jglrxavpok.mods.decraft.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;


public class SlotUncraftResult extends Slot 
{

	public SlotUncraftResult(IInventory inventoryIn, int index, int xPosition, int yPosition) 
	{
		super(inventoryIn, index, xPosition, yPosition);
	}

	/**
	 * Check if the stack is a valid item for this slot.
	 */
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		// an item will only be valid if it's a container item for an item already in the inventory
		return this.inventory.isItemValidForSlot(this.getSlotIndex(), stack);
	}

}
