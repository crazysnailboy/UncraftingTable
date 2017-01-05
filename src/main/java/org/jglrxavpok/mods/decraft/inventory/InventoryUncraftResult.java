package org.jglrxavpok.mods.decraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IChatComponent;

/**
 * 
 * @author jglrxavpok
 *
 */
public class InventoryUncraftResult implements IInventory
{
    private ItemStack[] stackResult = new ItemStack[9];

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getSizeInventory() {
        return 9;
    }

    /**
     * Returns the stack in slot i
     */
    @Override
    public ItemStack getStackInSlot(int slotIn) {
        return this.stackResult[slotIn];
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    @Override
    public ItemStack decrStackSize(int index, int count) {
    	
        if (this.stackResult[index] != null)
        {
            ItemStack itemstack = this.stackResult[index];
            this.stackResult[index] = null;
            return itemstack;
        }
        else return null;
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
    	
        if (this.stackResult[index] != null)
        {
            ItemStack itemstack = this.stackResult[index];
            this.stackResult[index] = null;
            return itemstack;
        }
        else return null;
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.stackResult[index] = stack;
    }
    
    /**
     * Returns the name of the inventory
     */
	@Override
	public String getInventoryName() {
//      return "UncraftResult";
		return null;
	}
    
    /**
     * Returns if the inventory is named
     */
	@Override
	public boolean hasCustomInventoryName() {
		return false;
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
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }
    
	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}
    
    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }
    
    
    
    

    public boolean isEmpty()
    {
        for (int i = 0; i < this.stackResult.length; i++ )
        {
            if (stackResult[i] != null)
                return false;
        }
        return true;
    }


//    public String getName()
//    {
//        return "UncraftResult";
//    }

//    public boolean hasCustomName()
//    {
//        return false;
//    }

//    public IChatComponent getDisplayName()
//    {
//        return null;
//    }

//    public void openInventory(EntityPlayer playerIn)
//    {
//    }

//    public void closeInventory(EntityPlayer playerIn)
//    {
//    }

//    public int getField(int id)
//    {
//        // TODO Auto-generated method stub
//        return 0;
//    }

//    public void setField(int id, int value)
//    {
//        // TODO Auto-generated method stub
//
//    }

//    public int getFieldCount()
//    {
//        // TODO Auto-generated method stub
//        return 0;
//    }

//    public void clearInventory()
//    {
//        for (int i = 0;i<stackResult.length;i++)
//            stackResult[i] = null;
//    }



}
