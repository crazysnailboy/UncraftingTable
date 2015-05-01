package org.jglrxavpok.mods.decraft;

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
    public int getSizeInventory()
    {
        return 9;
    }

    /**
     * Returns the stack in slot i
     */
    public ItemStack getStackInSlot(int par1)
    {
        return this.stackResult[par1];
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number
     * (second arg) of items and returns them in a new stack.
     */
    public ItemStack decrStackSize(int par1, int par2)
    {
        if(this.stackResult[par1] != null)
        {
            ItemStack itemstack = this.stackResult[par1];
            this.stackResult[par1] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    /**
     * When some containers are closed they call this on each slot, then drop
     * whatever it returns as an EntityItem - like when you close a workbench
     * GUI.
     */
    public ItemStack getStackInSlotOnClosing(int par1)
    {
        if(this.stackResult[par1] != null)
        {
            ItemStack itemstack = this.stackResult[par1];
            this.stackResult[par1] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be
     * crafting or armor sections).
     */
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
    {
        this.stackResult[par1] = par2ItemStack;
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be
     * 64, possibly will be extended. *Isn't this more of a set than a get?*
     */
    public int getInventoryStackLimit()
    {
        return 1;
    }

    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        return true;
    }

    public boolean isItemValidForSlot(int par1, ItemStack par2ItemStack)
    {
        return true;
    }

    public boolean isEmpty()
    {
        for(int i = 0; i < this.stackResult.length; i++ )
        {
            if(stackResult[i] != null)
                return false;
        }
        return true;
    }

    @Override
    public void markDirty()
    {

    }

    public String getName()
    {
        return "UncraftResult";
    }

    public boolean hasCustomName()
    {
        return false;
    }

    public IChatComponent getDisplayName()
    {
        return null;
    }

    public void openInventory(EntityPlayer playerIn)
    {

    }

    public void closeInventory(EntityPlayer playerIn)
    {

    }

    public int getField(int id)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public void setField(int id, int value)
    {
        // TODO Auto-generated method stub

    }

    public int getFieldCount()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public void clearInventory()
    {
        for(int i = 0;i<stackResult.length;i++)
            stackResult[i] = null;
    }

	@Override
	public String getInventoryName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasCustomInventoryName() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void openInventory() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeInventory() {
		// TODO Auto-generated method stub
		
	}
}
