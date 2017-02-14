package org.jglrxavpok.mods.decraft.inventory;

import org.jglrxavpok.mods.decraft.inventory.InventoryUncraftResult.StackType;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;


public class SlotUncraftResult extends Slot
{

	public SlotUncraftResult(InventoryUncraftResult inventoryIn, int index, int xPosition, int yPosition)
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

    /**
     * Returns the maximum stack size for a given slot
     */
	@Override
    public int getSlotStackLimit()
    {
		// the stack limit is the number of required container items for the recipe item (usually one), or zero
		ItemStack stack = ((InventoryUncraftResult)this.inventory).getStackInSlot(this.getSlotIndex(), StackType.RECIPE);
		return (stack != null && stack.getItem().hasContainerItem(stack) ? stack.stackSize : 0);
    }

}
