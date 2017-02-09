package org.jglrxavpok.mods.decraft.inventory;

import org.jglrxavpok.mods.decraft.item.uncrafting.UncraftingResult.ResultType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;


public class InventoryUncraftResult implements IInventory
{
	
	public enum StackType
	{
		RECIPE,
		CONTAINER
	}
	
	
	private class ItemStackPair
	{
		private ItemStack recipeItem;
		private ItemStack containerItem;
	}
	
	private ItemStackPair[] stackResult = new ItemStackPair[9];
	private ContainerUncraftingTable eventHandler;

	
	public InventoryUncraftResult(ContainerUncraftingTable p_i1807_1_)
	{
		for ( int i = 0 ; i < stackResult.length ; i++ )
		{
			stackResult[i] = new ItemStackPair();
		}
		this.eventHandler = p_i1807_1_;
	}
	

	/**
	 * Returns the number of slots in the inventory.
	 */
	@Override
	public int getSizeInventory() 
	{
		return 9;
	}
	

	/**
	 * Returns the stack in the given slot.
	 */
	@Override
	public ItemStack getStackInSlot(int index) 
	{
		if (this.eventHandler.uncraftingResult.resultType == ResultType.INACTIVE)
		{
			return this.stackResult[index].containerItem;
		}
		else if (this.eventHandler.uncraftingResult.resultType == ResultType.NEED_CONTAINER_ITEMS)
		{		
			if (this.stackResult[index].recipeItem != null && stackResult[index].recipeItem.getItem().hasContainerItem(stackResult[index].recipeItem) && this.stackResult[index].containerItem != null) // the hasContainerItem parameter is usually ignored, but some mods (Immersive Engineering) need it to be there
			{
				return this.stackResult[index].recipeItem;
			}
			else
			{
				return null;
			}
		}
		else
		{
			return this.stackResult[index].recipeItem; 
		}
	}
	
	public ItemStack getStackInSlot(int index, StackType stackType)
	{
		switch (stackType)
		{
			case RECIPE: return this.stackResult[index].recipeItem;
			case CONTAINER: return this.stackResult[index].containerItem;
			default: return null;
		}
	}
	

	/**
	 * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
	 */
	@Override
	public ItemStack decrStackSize(int index, int count) 
	{
		// if the inventory has been modified by the user
		if ((this.eventHandler.uncraftingResult.resultType == ResultType.VALID) || (this.eventHandler.uncraftingResult.resultType == ResultType.UNCRAFTED))
		{
			// if there's a recipe item present in this slot
			if (this.stackResult[index].recipeItem != null)
			{
				// remove the recipe item from the slot, and return it
				ItemStack itemstack = this.stackResult[index].recipeItem;
				
				this.stackResult[index].recipeItem = null;
				this.stackResult[index].containerItem = null;
				
				this.eventHandler.onCraftMatrixChanged(this);
				return itemstack;
			}
		}
		// if the inventory hasn't been modified by the user
		else //if (this.eventHandler.uncraftingResult.resultType == ResultType.VALID)
		{
			// if there's a container item present in this slot
			if (this.stackResult[index].containerItem != null)
			{
				// remove the container item from the slot, and return it
				ItemStack itemstack = this.stackResult[index].containerItem;
				
				this.stackResult[index].containerItem = null;
				
				this.eventHandler.onCraftMatrixChanged(this);
				return itemstack;
			}
		}
		return null;
	}
	

	/**
	 * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
	 * like when you close a workbench GUI.
	 */
	@Override
	public ItemStack removeStackFromSlot(int index) 
	{
		// if the inventory has been modified by the user
		if (this.eventHandler.uncraftingResult.resultType == ResultType.UNCRAFTED)
		{
			// if there's a recipe item present in this slot
			if (this.stackResult[index].recipeItem != null)
			{
				// remove the recipe item from the slot, and return it
				ItemStack itemstack = this.stackResult[index].recipeItem;
				this.stackResult[index].recipeItem = null;
				return itemstack;
			}
		}
		// if the inventory hasn't been modified by the user
		else // if (this.eventHandler.uncraftingResult.resultType == ResultType.VALID) 
		{
			// if there's a container item present in this slot
			if (this.stackResult[index].containerItem != null)
			{
				// remove the container item from the slot, and return it
				ItemStack itemstack = this.stackResult[index].containerItem;
				this.stackResult[index].containerItem = null;
				return itemstack;
			}
		}
		return null;
	}
	

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
	 */
	@Override
	public void setInventorySlotContents(int index, ItemStack stack) 
	{
		// if the slot isn't empty, and the item in the slot requires a container item
		if (stack != null && this.stackResult[index].recipeItem != null && stackResult[index].recipeItem.getItem().hasContainerItem(stackResult[index].recipeItem)) // the hasContainerItem parameter is usually ignored, but some mods (Immersive Engineering) need it to be there
		{
			// get the container item for the recipe item
			Item recipeItem = stackResult[index].recipeItem.getItem();
			Item containerItem = recipeItem.getContainerItem(); if (containerItem == null) containerItem = recipeItem;  // some mods (e.g. IC2) use a null container item for some recipes
			
			// if the stack being passed in is the correct container item for the recipe item 
			if (stack.getItem() == containerItem)
			{
				// store the container item
				this.stackResult[index].containerItem = stack;

				// if the recipe item and the container items are equal
				if (recipeItem == containerItem)
				{
					// copy the container stack into the recipe stack
					ItemStack newStack = stack.copy();
					this.stackResult[index].recipeItem = newStack;
					
					// update the uncrafting recipe itself with the provided container item rather than the default
					// (this is for mods like tinker's construct where chisels vary based on nbt data)
					this.eventHandler.uncraftingResult.getCraftingGrid()[index] = newStack; // TODO: this probably shouldn't be happening in the inventory
				}
				
				this.eventHandler.onCraftMatrixChanged(this);
			}
		}
		// if the slot is empty, or the slot item doesn't require a container item, set the recipe item
		else
		{
			this.stackResult[index].recipeItem = stack;
			
			if (this.eventHandler.uncraftingResult.resultType == ResultType.INACTIVE && stack == null)
			{
				this.stackResult[index].containerItem = stack;
			}
			
			this.eventHandler.onCraftMatrixChanged(this);
		}
	}
	
	public void setInventorySlotContents(int index, ItemStack stack, StackType stackType)
	{
		switch (stackType)
		{
			case RECIPE: this.stackResult[index].recipeItem = stack; break;
			case CONTAINER: this.stackResult[index].containerItem = stack; break;
		}
	}
	
	
	
	/**
	 * Returns the name of the inventory
	 */
	@Override
	public String getName() 
	{
		return null;
	}
	
	
	/**
	 * Returns if the inventory is named
	 */
	@Override
	public boolean hasCustomName() 
	{
		return false;
	}
	
	
	/**
	 * Get the formatted ChatComponent that will be used for the sender's username in chat
	 */
	@Override
	public ITextComponent getDisplayName() 
	{
		return null;
	}
	

	/**
	 * Returns the maximum stack size for a inventory slot.
	 */
	@Override
	public int getInventoryStackLimit() 
	{
		return 64;
	}
	

	/**
	 * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
	 * hasn't changed and skip it.
	 */
	@Override
	public void markDirty() 
	{
	}
	

	/**
	 * Do not make give this method the name canInteractWith because it clashes with Container
	 */
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) 
	{
		return true;
	}
	
	
	@Override
	public void openInventory(EntityPlayer player) 
	{
	}
	

	@Override
	public void closeInventory(EntityPlayer player) 
	{
	}
	
	
	/**
	 * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
	 */
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) 
	{
		// if the recipe item has a container item
		if (stackResult[index].recipeItem != null && stackResult[index].recipeItem.getItem().hasContainerItem(stackResult[index].recipeItem)) // the hasContainerItem parameter is usually ignored, but some mods (Immersive Engineering) need it to be there
		{
			// get the container item for the recipe item
			Item recipeItem = stackResult[index].recipeItem.getItem();
			Item containerItem = recipeItem.getContainerItem(); if (containerItem == null) containerItem = recipeItem;  // some mods (e.g. IC2) use a null container item for some recipes

			// the input item is valid if it matches the container item			
			return (stack.getItem() == containerItem);
		}
		else return false;
	}
	
	
	@Override
	public int getField(int id) 
	{
		return 0;
	}
	

	@Override
	public void setField(int id, int value) 
	{
	}
	

	@Override
	public int getFieldCount() 
	{
		return 0;
	}

	
	
	public boolean isEmpty()
	{
		for (int i = 0; i < this.stackResult.length; i++ )
		{
			if (stackResult[i].recipeItem != null || stackResult[i].containerItem != null) return false;
		}
		return true;
	}
	

	@Override
	public void clear() 
	{
		for (int i = 0; i < this.stackResult.length; ++i)
		{
			this.stackResult[i].recipeItem = null;
			this.stackResult[i].containerItem = null;
		}
	}
	
	public void clear(StackType stackType)
	{
		switch (stackType)
		{
			case RECIPE: 
				for (int i = 0; i < this.stackResult.length; ++i)
				{
					this.stackResult[i].recipeItem = null;
				}
				break;

			case CONTAINER: 
				for (int i = 0; i < this.stackResult.length; ++i)
				{
					this.stackResult[i].containerItem = null;
				}
				break;
		}
	}
	
	
	public boolean missingContainerItems()
	{
		for (int index = 0; index < this.stackResult.length; ++index)
		{
			if (stackResult[index].recipeItem != null && stackResult[index].recipeItem.getItem().hasContainerItem(stackResult[index].recipeItem) && this.stackResult[index].containerItem == null) // the hasContainerItem parameter is usually ignored, but some mods (Immersive Engineering) need it to be there
			{
				return true;
			}
		}
		return false;
	}

	public int missingContainerItemCount()
	{
		int count = 0;
		for (int index = 0; index < this.stackResult.length; ++index)
		{
			if (stackResult[index].recipeItem != null && stackResult[index].recipeItem.getItem().hasContainerItem(stackResult[index].recipeItem) && this.stackResult[index].containerItem == null) // the hasContainerItem parameter is usually ignored, but some mods (Immersive Engineering) need it to be there
			{
				count++;
			}
		}
		return count;
	}

}
