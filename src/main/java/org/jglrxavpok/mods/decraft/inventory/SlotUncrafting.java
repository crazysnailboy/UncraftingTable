package org.jglrxavpok.mods.decraft.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;


public class SlotUncrafting extends Slot 
{

	private ItemStack stack;
	private ContainerUncraftingTable container;
	

	public SlotUncrafting(IInventory inventoryIn, int index, int xPosition, int yPosition, ContainerUncraftingTable containerIn) 
	{
		super(inventoryIn, index, xPosition, yPosition);
		this.container = containerIn;
	}
	
	
	@Override
	public void onSlotChanged()
	{
		super.onSlotChanged();

		// ensure that the container's onCraftMatrixChanged method is called when the number of items in the slot is increased
		ItemStack stack = this.getStack();
		if (stack != null && this.stack != null && stack.isItemEqual(this.stack) && stack.stackSize > this.stack.stackSize)
		{
			this.container.onCraftMatrixChanged(this.inventory);
		}
		this.stack = (stack == null ? null : stack.copy());
	}
	
}
