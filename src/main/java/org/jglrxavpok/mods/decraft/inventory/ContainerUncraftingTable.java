package org.jglrxavpok.mods.decraft.inventory;

import java.util.List;

import org.jglrxavpok.mods.decraft.event.ItemUncraftedEvent;
import org.jglrxavpok.mods.decraft.item.uncrafting.UncraftingManager;
import org.jglrxavpok.mods.decraft.item.uncrafting.UncraftingResult;
import org.jglrxavpok.mods.decraft.item.uncrafting.UncraftingResult.ResultType;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;


public class ContainerUncraftingTable extends Container
{
	public InventoryBasic calculInput = new InventoryBasic(null, false, 1);
	public InventoryCrafting uncraftIn = new InventoryCrafting(this, 1, 1);
	public InventoryUncraftResult uncraftOut = new InventoryUncraftResult(this);
	public InventoryPlayer playerInventory;
	
	private World worldObj;
	
	public UncraftingResult uncraftingResult = new UncraftingResult();
	

	public ContainerUncraftingTable(InventoryPlayer playerInventoryIn, World worldIn)
	{
		this.worldObj = worldIn;
		
		// uncrafting book inventory for capturing enchantments (left standalone slot)
		this.addSlotToContainer(new Slot(this.calculInput, 0, 20, 35));

		// incrafting input inventory (right standalone slot)
		this.addSlotToContainer(new SlotUncrafting(this.uncraftIn, 0, 45, 35, this));
		
		// uncrafting output inventory
		int offsetX = 106; int offsetY = 17;
		for (int row = 0; row < 3; ++row)
		{
			for (int col = 0; col < 3; ++col)
			{
				this.addSlotToContainer(new SlotUncraftResult(this.uncraftOut, col + row * 3, offsetX + col * 18, offsetY + row * 18));
			}
		}
		
		// player inventory
		for (int row = 0; row < 3; ++row)
		{
			for (int col = 0; col < 9; ++col)
			{
				this.addSlotToContainer(new Slot(playerInventoryIn, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
			}
		}
		
		// player hotbar inventory
		for (int col = 0; col < 9; ++col)
		{
			this.addSlotToContainer(new Slot(playerInventoryIn, col, 8 + col * 18, 142));
		}
		
		playerInventory = playerInventoryIn;
	}
	
	
	private void populateOutputInventory()
	{
		// get the minimum stack size and the crafting grid from the uncrafting result
		int minStackSize = uncraftingResult.getMinStackSize();
		ItemStack[] craftingGrid = uncraftingResult.getCraftingGrid();

		// calculate a multipler to use when adding new items to the uncrafting inventory
		int multiplier = (uncraftIn.getStackInSlot(0).stackSize / minStackSize);
		
		// for each slot in the selected uncrafting result grid
		for ( int index = 0 ; index < craftingGrid.length ; index++ )
		{
			// if the slot in the result grid isn't empty
			if (craftingGrid[index] != null)
			{
				// populate the slot in the output inventory with the correct number of items
				if (
					uncraftingResult.resultType == ResultType.VALID
					||
					(uncraftingResult.resultType == ResultType.NEED_CONTAINER_ITEMS && craftingGrid[index].getItem().hasContainerItem(null))
				)
				{
					
					// determine how many items we need to place in the inventory slot
					int amount = craftingGrid[index].stackSize * multiplier;
					if (amount > craftingGrid[index].getMaxStackSize()) amount = craftingGrid[index].getMaxStackSize(); 
					
					// create the new itemstack to place in the uncrafting inventory
					ItemStack newStack = new ItemStack(craftingGrid[index].getItem(), amount, craftingGrid[index].getItemDamage());
					
					// if the crafting recipe item has NBT data, copy that onto the new itemstack
					if (craftingGrid[index].hasTagCompound())
					{
						newStack.setTagCompound(craftingGrid[index].getTagCompound());
					}

					// add the new itemstack to the inventory
					uncraftOut.setInventorySlotRecipeStack(index, newStack);
				}
			}
			// if the slot in the result grid is empty, clear the corresponding slot in the inventory
			else uncraftOut.setInventorySlotRecipeStack(index, null);
		}
	}
	
	
	private void doUncraft()
	{
		// if we're not in creative mode
		if (!playerInventory.player.capabilities.isCreativeMode)
		{
			// if we don't have enough xp
			if (playerInventory.player.experienceLevel < uncraftingResult.experienceCost)
			{
				// set the status to error, not enough xp and return
				uncraftingResult.resultType = ResultType.NOT_ENOUGH_XP;
				return;
			}
			
			// deduct the appropriate number of levels from the player
			playerInventory.player.experienceLevel -= uncraftingResult.experienceCost;
		}
		
		// if the item being uncrafted has enchantments, and there are books in the left hand slot
		if (uncraftIn.getStackInSlot(0).isItemEnchanted() && calculInput.getStackInSlot(0) != null && calculInput.getStackInSlot(0).getItem() == Items.book)
		{
			// copy the item enchantments onto one or more books
			List<ItemStack> enchantedBooks = UncraftingManager.getItemEnchantments(uncraftIn.getStackInSlot(0), calculInput.getStackInSlot(0));
			
			// for each enchanted book
			for (ItemStack enchantedBook : enchantedBooks)
			{
				// add the itemstack to the player inventory, or spawn in the world if the inventory is full
				if (!playerInventory.addItemStackToInventory(enchantedBook))
				{
					EntityItem e = playerInventory.player.entityDropItem(enchantedBook, 0.5f);
					e.posX = playerInventory.player.posX;
					e.posY = playerInventory.player.posY;
					e.posZ = playerInventory.player.posZ;
				}
			}
			// decrement the stack size for the books in the left hand slot
			calculInput.decrStackSize(0, enchantedBooks.size());
			
		} // end of enchantment processing
		

		// get the minimum stack size and the crafting grid from the uncrafting result
		int minStackSize = uncraftingResult.getMinStackSize();
		ItemStack[] craftingGrid = uncraftingResult.getCraftingGrid();
		
		// calculate a multipler to determine how many items we've uncrafted
		int multiplier = (uncraftIn.getStackInSlot(0).stackSize / minStackSize);

		// fire an event indicating a successful uncrafting operation
		MinecraftForge.EVENT_BUS.post(new ItemUncraftedEvent(playerInventory.player, uncraftIn.getStackInSlot(0), (minStackSize * multiplier)));

		
		// change the status to uncrafted
		this.uncraftingResult.resultType = ResultType.UNCRAFTED;

		// decrement the number of items in the input slot
		uncraftIn.decrStackSize(0, (minStackSize * multiplier));
	}
	
	
	public void switchRecipe()
	{
		// remove the recipe items from the grid, if present
		uncraftOut.clearRecipeItems();
		
		// if the grid isn't empty, it has container items in it
		if (!this.uncraftOut.isEmpty())
		{
			// so move those back into the player inventory
			
			// for each slot in the output grid 
			for (int i = 0; i < uncraftOut.getSizeInventory(); i++ )
			{
				// determine the item in the current slot
				ItemStack item = uncraftOut.getStackInSlot(i);
				if (item != null)
				{
					// move the item currently in the output into the player inventory 
					if (!playerInventory.addItemStackToInventory(item))
					{
						// if the item cannot be added to the player inventory, spawn the item in the world instead
						if (!worldObj.isRemote)
						{
							EntityItem e = playerInventory.player.entityDropItem(item, 0.5f);
							e.posX = playerInventory.player.posX;
							e.posY = playerInventory.player.posY;
							e.posZ = playerInventory.player.posZ;
						}
					}
				}
			}
		}
		
		uncraftOut.clearContainerItems();
		
		if (uncraftIn.getStackInSlot(0).stackSize < uncraftingResult.getMinStackSize())
		{
			uncraftingResult.resultType = ResultType.NOT_ENOUGH_ITEMS;
		}
		else if (!playerInventory.player.capabilities.isCreativeMode && playerInventory.player.experienceLevel < uncraftingResult.experienceCost)
		{		
			uncraftingResult.resultType = ResultType.NOT_ENOUGH_XP;
		}
		else
		{
			// check to see if one of more of the items in the crafting recipe have container items
			for ( ItemStack recipeStack : uncraftingResult.getCraftingGrid() )
			{
				if (recipeStack != null && recipeStack.getItem().hasContainerItem(null)) // the hasContainerItem parameter is ignored, and ItemStack internally calls the deprecated version without the parameter anyway...
				{
					uncraftingResult.resultType = ResultType.NEED_CONTAINER_ITEMS;
					break;
				}
			}

			// if no container items are present, and all the other checks pass
			if (uncraftingResult.resultType != ResultType.NEED_CONTAINER_ITEMS)
			{
				// the uncrafting operation can be performed
				uncraftingResult.resultType = ResultType.VALID;
			}
		}
		
		if (uncraftingResult.canPopulateInventory())
		{
			populateOutputInventory();
		}
	}
	

	/**
	 * Callback for when the crafting matrix is changed.
	 */
	@Override
	public void onCraftMatrixChanged(IInventory inventory)
	{
		// if the right input slot changes
		if (inventory == uncraftIn)
		{
			// get the stack in the input inventory slot
			ItemStack inputStack = uncraftIn.getStackInSlot(0);
			
			// if the stack is empty
			if (inputStack == null)
			{
				// if the slot is empty because we've finished uncrafting something
				if (this.uncraftingResult.resultType == ResultType.UNCRAFTED)
				{
					if (uncraftOut.isEmpty())
					{
						this.uncraftingResult = new UncraftingResult();
					}
				}
				if (this.uncraftingResult.resultType != ResultType.UNCRAFTED)
				{
					uncraftOut.clearRecipeItems();
					this.uncraftingResult = new UncraftingResult();
					
				}
			}
			// if the stack is not empty
			else
			{
				if (this.uncraftingResult.resultType != ResultType.UNCRAFTED)
				{
					// populate the uncrafting result based on the contents of the right hand slot
					this.uncraftingResult = UncraftingManager.getUncraftingResult(playerInventory.player, inputStack);
					
					// if the item in the input stack can be uncrafted...
					if (this.uncraftingResult.canPopulateInventory())
					{
						populateOutputInventory();
					}
				}
			}
			return;
		}
		
		// if the uncrafting result inventory changes
		else if (inventory == uncraftOut)
		{
			if (this.uncraftingResult.resultType == ResultType.NEED_CONTAINER_ITEMS && !uncraftOut.missingContainerItems())
			{
				this.uncraftingResult.resultType = ResultType.VALID;
				populateOutputInventory();
			}
			
			else if (this.uncraftingResult.resultType == ResultType.VALID)
			{
				doUncraft();
			}
			
			else if (this.uncraftingResult.resultType == ResultType.UNCRAFTED)
			{
				//uncraftOut.clearContainerItems();
			}
			
			if (uncraftOut.isEmpty())
			{
				this.uncraftingResult = new UncraftingResult();
				if (uncraftIn.getStackInSlot(0) != null) this.onCraftMatrixChanged(uncraftIn);
			}

		}
	}
	

	/**
	 * Called when the container is closed.
	 */
	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		super.onContainerClosed(player);

		if (!this.worldObj.isRemote)
		{
			ItemStack itemstack;
			
			// if there's an itemstack in the calculation slot, drop the stack into the world
			itemstack = this.calculInput.getStackInSlotOnClosing(0);
			if (itemstack != null)
			{
				player.dropPlayerItemWithRandomChoice(itemstack, false);
			}
			
			// if there's an itemstack in the input slot, drop the stack into the world
			itemstack = this.uncraftIn.getStackInSlotOnClosing(0);
			if (itemstack != null)
			{
				player.dropPlayerItemWithRandomChoice(itemstack, false);
			}

			// if there are itemstacks in the uncrafting grid, drop the stacks into the world
			for (int i = 0; i < uncraftOut.getSizeInventory(); i++ )
			{
				itemstack = this.uncraftOut.getStackInSlotOnClosing(i);
				if (itemstack != null)
				{
					player.dropPlayerItemWithRandomChoice(itemstack, false);
				}
			}
		}
	}


	/**
	 * Called when a player shift-clicks on a slot.
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index)
	{
		// get the slot specified by the index
		Slot slot = (Slot)this.inventorySlots.get(index);
		
		// if the slot is valid and contains a stack of items
		if (slot != null && slot.getHasStack())
		{
			// if the slot belongs to the calculation inventory
			if (slot.inventory.equals(calculInput))
			{
				// attempt to add the stack to the player's inventory
				if (!playerInventory.addItemStackToInventory(slot.getStack()))
				{
					// TODO: shouldn't this spawn items in the world if they can't be added to the player's inventory?
					return null;
				}
				// clear the slot
				slot.putStack(null);
			}
			
			// if the slot belongs to the input inventory
			else if (slot.inventory.equals(uncraftIn))
			{
				// attempt to add the stack to the player's inventory
				if (!playerInventory.addItemStackToInventory(slot.getStack()))
				{
					// TODO: shouldn't this spawn items in the world if they can't be added to the player's inventory?
					return null;
				}
				// clear thr slot
				slot.putStack(null);
			}
			
			// if the slot belongs to the uncrafting result grid
			else if (slot.inventory.equals(uncraftOut))
			{
				if (this.uncraftingResult.resultType == ResultType.VALID)
				{
					doUncraft();
				}
				
				if (uncraftOut.isEmpty())
				{
					this.uncraftingResult = new UncraftingResult();
					if (uncraftIn.getStackInSlot(0) != null) this.onCraftMatrixChanged(uncraftIn);
				}
				
				// attempt to add those items to the player's inventory
				if (!playerInventory.addItemStackToInventory(slot.getStack()))
				{
					// TODO: shouldn't this spawn items in the world if they can't be added to the player's inventory?
					return null;
				}
				// clear the slot
				slot.putStack(null);
			}
			
			// if the slot belongs to the player's inventory
			else if (slot.inventory.equals(playerInventory))
			{
				// the target slot is the left slot if the player is shift clicking on a book, otherwise it's the right slot
				int targetIndex = (slot.getStack().getItem() == Items.book ? 0 : 1);
				Slot targetSlot = (Slot)inventorySlots.get(slot.getStack().getItem() == Items.book ? 0 : 1);
				
				// if the target input slot doesn't contain items
				if (targetSlot.getStack() == null)
				{
					// put the items from the clicked slot into the calculation slot
					targetSlot.putStack(slot.getStack());
					// clear the clicked slot
					slot.putStack(null);
				}
				// if the calculation slot does contain items
				else
				{
					ItemStack itemstack1 = slot.getStack();
					ItemStack itemstack = itemstack1.copy();

					if (!mergeItemStack(itemstack1, targetIndex, targetIndex + 1, false))
					{
						return null;
					}
					
					if (itemstack1.stackSize == 0)
					{
						slot.putStack(null);
					}
					else
					{
						slot.onSlotChanged();
					}

					if (itemstack1.stackSize == itemstack.stackSize)
					{
						return null;
					}

					slot.onPickupFromSlot(player, itemstack1);

					return itemstack;
				}
			}
		}
		return null;
	}


	@Override
	public void putStackInSlot(int slotId, ItemStack stack)
	{
		// TODO: i assume there's a reason that Container does this, but this container seems to work without it!
		// this.getSlot(slotId).putStack(stack);
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return true;
	}
	
	@Override
	public boolean func_94530_a(ItemStack p_94530_1_, Slot p_94530_2_) //public boolean canMergeSlot(ItemStack stack, Slot slotIn)
	{
		return !p_94530_2_.inventory.equals(uncraftOut);
	}

}
