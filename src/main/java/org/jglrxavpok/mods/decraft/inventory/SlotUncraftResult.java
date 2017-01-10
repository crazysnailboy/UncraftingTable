package org.jglrxavpok.mods.decraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
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
     * Fired when the player removes the item from the slot.
     */
    @Override
    public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack)
    {
    	// i don't want to use inventory.markDirty as it's called in too many other places
    	((InventoryUncraftResult)this.inventory).setIsDirty(); // super.onPickupFromSlot(playerIn, stack);
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
	
    /**
     * Helper method to put a stack in the slot.
     */
	@Override
    public void putStack(ItemStack stack)
    {
		// don't trigger onSlotChanged unless the slot actually changes
		if (this.getStack() != stack) super.putStack(stack);
    }
	
}
