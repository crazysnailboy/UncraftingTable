package org.jglrxavpok.mods.decraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;

/**
 * 
 * @author jglrxavpok
 *
 */
public class InventoryUncraftResult implements IInventory
{

    private final NonNullList<ItemStack> stackList;
    
    
    public InventoryUncraftResult()
    {
    	int width = 3;
    	int height = 3;
        this.stackList = NonNullList.<ItemStack>withSize(width * height, ItemStack.EMPTY);
    }
    
    
    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getSizeInventory() {
        return 9;
    }

    /**
     * Returns the stack in the given slot.
     */
    @Override
    public ItemStack getStackInSlot(int index) {
        return index >= this.getSizeInventory() ? ItemStack.EMPTY : (ItemStack)this.stackList.get(index);
    }

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     */
    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        ItemStack itemstack = ItemStackHelper.getAndSplit(this.stackList, index, count);
        return itemstack;
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    @Override
    public ItemStack removeStackFromSlot(int index)
    {
        return ItemStackHelper.getAndRemove(this.stackList, index);
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        this.stackList.set(index, stack);
    }
    
	@Override
    public boolean isEmpty()
    {
        for (ItemStack itemstack : this.stackList)
        {
            if (!itemstack.isEmpty())
            {
                return false;
            }
        }
        return true;
    }
    
	/**
     * Returns the maximum stack size for a inventory slot.
     */
    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    @Override
    public void markDirty() {
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return true;
    }
    
	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}
    
    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasCustomName() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getField(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getFieldCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

}
