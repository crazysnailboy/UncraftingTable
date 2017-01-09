package org.jglrxavpok.mods.decraft.inventory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;
import org.jglrxavpok.mods.decraft.event.ItemUncraftedEvent;
import org.jglrxavpok.mods.decraft.event.UncraftingEvent;
import org.jglrxavpok.mods.decraft.item.uncrafting.UncraftingManager;
import org.jglrxavpok.mods.decraft.item.uncrafting.UncraftingResult;
import org.jglrxavpok.mods.decraft.item.uncrafting.UncraftingResult.ResultType;
import org.jglrxavpok.mods.decraft.stats.ModAchievements;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

/**
 * 
 * @author jglrxavpok
 *
 */
public class ContainerUncraftingTable extends Container
{

    public InventoryCrafting calculInput = new InventoryCrafting(this, 1, 1);
    public InventoryCrafting uncraftIn = new InventoryCrafting(this, 1, 1);
    public InventoryUncraftResult uncraftOut = new InventoryUncraftResult();
    public InventoryPlayer playerInventory;
    
    private World worldObj;
    
    public UncraftingResult uncraftingResult = new UncraftingResult();
    

    public ContainerUncraftingTable(InventoryPlayer playerInventoryIn, World worldIn)
    {
        this.worldObj = worldIn;
        
    	// uncrafting output inventory
        int offsetX = 106; int offsetY = 17;
        for (int row = 0; row < 3; ++row)
        {
            for (int col = 0; col < 3; ++col)
            {
            	// arguments: inventory, slotIndex, xDisplayPosition, yDisplayPosition    
                this.addSlotToContainer(new SlotUncraftResult(this.uncraftOut, col + row * 3, offsetX + col * 18, offsetY + row * 18));
            }
        }
        
        // uncrafting book inventory for capturing enchantments (left standalone slot)
        this.addSlotToContainer(new Slot(this.calculInput, 0, 20, 35));

        // incrafting input inventory (right standalone slot)
        this.addSlotToContainer(new Slot(this.uncraftIn, 0, 45, 35));
        
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


    /**
     * Callback for when the crafting matrix is changed.
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void onCraftMatrixChanged(IInventory inventory)
    {
        // if the left input slot changes
        if (inventory == calculInput)
        {
        	// if the left slot is empty
            if (calculInput.getStackInSlot(0).isEmpty())
            {
                // if the right hand slot is empty
                if (uncraftIn.getStackInSlot(0).isEmpty())
                {
                	// clear the uncrafting result
                	this.uncraftingResult = new UncraftingResult();
                }
                return;
            }
            
            // if the left hand slot is not empty and the right hand slot is empty
            else if (uncraftIn.getStackInSlot(0).isEmpty())
            {
            	// create an uncrafting result based on the contents of the left hand slot
            	this.uncraftingResult = UncraftingManager.getUncraftingResult(playerInventory.player, calculInput.getStackInSlot(0));
        		return;
            }
            // if the left hand slot is not empty and the right hand slot is not empty
            else
            {
                return;
            }
        }
        
        
        // if the right input slot changes
        else if (inventory == uncraftIn)
        {
            // if the right input slot is empty
            if (uncraftIn.getStackInSlot(0).isEmpty())
            {
            	// clear the uncrafting result
            	this.uncraftingResult = new UncraftingResult();
                return;
            }
            
        	// create an uncrafting result based on the contents of the right hand slot
        	this.uncraftingResult = UncraftingManager.getUncraftingResult(playerInventory.player, uncraftIn.getStackInSlot(0));
        	if (UncraftingResult.ResultType.isError(uncraftingResult.resultType)) return;

        	
        	// --- TODO: this is all temporary code to match the uncraftingResult to existing variables
    		int minStackSize = (uncraftingResult.minStackSizes.size() > 0 ? uncraftingResult.minStackSizes.get(uncraftingResult.selectedCraftingGrid) : 1);
            NonNullList<ItemStack> craftingGrid = (uncraftingResult.craftingGrids.size() > 0 ? uncraftingResult.craftingGrids.get(uncraftingResult.selectedCraftingGrid) : null);
        	// --- end of temporary code
	        int multiplier = (uncraftIn.getStackInSlot(0).getCount() / minStackSize);
            
            
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
        	if (uncraftIn.getStackInSlot(0).isItemEnchanted() && calculInput.getStackInSlot(0) != ItemStack.EMPTY && calculInput.getStackInSlot(0).getItem() == Items.BOOK)
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
                
                
            // if the output grid isn't empty
            if (!uncraftOut.isEmpty())
            {
            	// for each slot in the output grid 
                for (int i = 0; i < uncraftOut.getSizeInventory(); i++ )
                {
                	// determine the item in the current slot
                    ItemStack item = uncraftOut.getStackInSlot(i);
                    if (item != ItemStack.EMPTY)
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
                        // clear the slot in the output grid
                        uncraftOut.setInventorySlotContents(i, ItemStack.EMPTY);
                    }
                }
            }
                
                
            // for each item stack in the uncrafting result
            for (int i = 0; i < craftingGrid.size(); i++ )
            {
                ItemStack s = craftingGrid.get(i);
                
                // if the current stack of the uncrafting result isn't empty
                if (s != ItemStack.EMPTY)
                {
                	// if the uncrafting result doesn't specify metadata for this item, use the default value
                    int metadata = s.getItemDamage();
                    if (metadata == Short.MAX_VALUE) metadata = 0;

                    // get the stack from the matching slot of the output grid
                    ItemStack currentStack = uncraftOut.getStackInSlot(i);
                    ItemStack newStack = ItemStack.EMPTY;
                    
                    // if the stack in the current slot of the output grid is not already at it's maximum stack size
                    if (!currentStack.isEmpty() && 1 + currentStack.getCount() <= s.getMaxStackSize())
                    {
                    	// create a new stack of the same item type and metadata, with more items in it than previously  
                        newStack = new ItemStack(s.getItem(), currentStack.getCount() + multiplier, metadata);
                    }
                    else
                    {
                    	// if the stack isn't empty it's full, so attempt to move it to the player's inventory
                        if (!currentStack.isEmpty() && !playerInventory.addItemStackToInventory(currentStack))
                        {
                        	// if the stack cannot be added to the player inventory, spawn the item in the world instead
                        	if (!worldObj.isRemote)
                        	{
                                EntityItem e = playerInventory.player.entityDropItem(currentStack, 0.5f);
                                e.posX = playerInventory.player.posX;
                                e.posY = playerInventory.player.posY;
                                e.posZ = playerInventory.player.posZ;
                        	}
                        }
                    	// create a new stack of the same item type and metadata  
                        newStack = new ItemStack(s.getItem(), multiplier, metadata);
                    }
                    // replace the stack in the output grid with the new stack
                    uncraftOut.setInventorySlotContents(i, newStack);
                }


                // fire an event indicating a successful uncrafting operation
                MinecraftForge.EVENT_BUS.post(new ItemUncraftedEvent(playerInventory.player, uncraftIn.getStackInSlot(0), craftingGrid, minStackSize));

            	// decrement the number of items in the input slot by the minimum stack size
                uncraftIn.decrStackSize(0, minStackSize);
            }
        }
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player)
    {    
        ItemStack itemStack = super.slotClick(slotId, dragType, clickTypeIn, player);
        
        if (inventorySlots.size() > slotId && slotId >= 0)
        {
            if (inventorySlots.get(slotId) != null)
            {
            	IInventory inventory = ((Slot)inventorySlots.get(slotId)).inventory;
            	
            	if (inventory == calculInput) // || inventory == playerInventory)
            	{
                    this.onCraftMatrixChanged(calculInput);
            	}
            	if (inventory == uncraftIn)
            	{
                    this.onCraftMatrixChanged(uncraftIn);
            	}
            }
        }
        return itemStack;
    }

    @Override
    public void onContainerClosed(EntityPlayer player)
    {
        if (playerInventory.getItemStack() != ItemStack.EMPTY)
        {
            player.entityDropItem(playerInventory.getItemStack(), 0.5f);
        }
        if (!this.worldObj.isRemote)
        {
            ItemStack itemstack = this.uncraftIn.removeStackFromSlot(0);
            if (itemstack != ItemStack.EMPTY)
            {
                player.entityDropItem(itemstack, 0.5f);
            }

            itemstack = this.calculInput.getStackInSlot(0);
            if (itemstack != ItemStack.EMPTY)
            {
                player.entityDropItem(itemstack, 0.5f);
            }
            for (int i = 0; i < uncraftOut.getSizeInventory(); i++ )
            {
                itemstack = this.uncraftOut.removeStackFromSlot(i);

                if (itemstack != ItemStack.EMPTY)
                {
                    player.entityDropItem(itemstack, 0.5f);
                }
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return true;
    }

    /**
     * Called when a player shift-clicks on a slot.
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        // get the slot specified by the index
        Slot slot = (Slot) this.inventorySlots.get(index);
        // if the slot is valid and contains a stack of items
        if (slot != null && slot.getHasStack())
        {
        	// if the slot belongs to the calculation inventory
            if (slot.inventory.equals(calculInput))
            {
            	// get the stack of items in the slot
                ItemStack itemstack1 = slot.getStack();
                // let the slot know the player has picked up the stack of items
                slot.onSlotChanged(); // slot.onPickupFromSlot(player, itemstack1); // in 1.9 & 1.10, onPickupFromSlot just called onSlotChanged with no parameters
                // attempt add the items to the player's inventory
                if (!playerInventory.addItemStackToInventory(itemstack1))
                {
                	// TODO: shouldn't this spawn items in the world if they can't be added to the player's inventory?
                    return ItemStack.EMPTY;
                }
                // clear the slot
                slot.putStack(ItemStack.EMPTY);
            }
        	// if the slot belongs to the input inventory
            else if (slot.inventory.equals(uncraftIn))
            {
            	// if the slot contains items
                if (slot.getHasStack())
                {
                    // attempt add the items to the player's inventory
                    if (!playerInventory.addItemStackToInventory(slot.getStack()))
                    {
                    	// TODO: shouldn't this spawn items in the world if they can't be added to the player's inventory?
                        return ItemStack.EMPTY;
                    }
                    // clear thr slot
                    slot.putStack(ItemStack.EMPTY);
                    // let the slot know that the contents have changed
                    slot.onSlotChanged();
                }
            }
        	// if the slot belongs to the player's inventory
            else if (slot.inventory.equals(playerInventory))
            {
                Slot calcInput = null;
                Slot uncraftSlot = null;
                // iterate through all the slots in this container's inventories
                for (Object s : inventorySlots)
                {
                    Slot s1 = (Slot) s;
                    // if the current slot belongs to the calculation inventory 
                    if (s1.inventory.equals(calculInput))
                    {
                    	// set the temporary slot variable for the calculation slot to reference this slot
                        calcInput = s1;
                    }
                    // if the current slot belongs to the input inventory
                    else if (s1.inventory.equals(uncraftIn))
                    {
                    	// set the temporary slot variable for the input slot to reference this slot
                        uncraftSlot = s1;
                    }
                }
                // if we matched to the calculation input slot
                if (calcInput != null)
                {
                	// if the calculation input slot doesn't contain items
                	if (!calcInput.getStack().isEmpty())
                    {
                    	// put the items from the clicked slot into the calculation slot
                        calcInput.putStack(slot.getStack());
                        // let the calculation slot know it's been changed
                        calcInput.onSlotChanged();
                        // clear the clicked slot
                        slot.putStack(ItemStack.EMPTY);
                    }
                    // if the calculation slot does contain items
                    else
                    {
                    	// if the clicked slot does contain items
                        if (!slot.getStack().isEmpty())
                        {
                        	// get the items currently in the clicked slot
                            ItemStack i = slot.getStack();
                            // tell the clicked slot that the player has picked up the items
                            slot.onSlotChanged(); // slot.onPickupFromSlot(player, slot.getStack()); // in 1.9 & 1.10, onPickupFromSlot just called onSlotChanged with no parameters
                            // put the items from the calculation slot into the player's inventory
                            slot.putStack(calcInput.getStack().copy());
                            // put the items that were in the player's inventory into the calculation slot
                            calcInput.putStack(i.copy());
                            // trigger the crafting matrix change for the calculation slot
                            this.onCraftMatrixChanged(calculInput);
                            // tell the calculation slot that it's changed
                            calcInput.onSlotChanged();
                        }
                        // if the clicked slot doesn't contain items
                        else
                        {
                        	// do nothing
                            return ItemStack.EMPTY;
                        }
                    }
                }
            }
        	// if the slot belongs to the uncrafting result grid
            else if (slot.inventory.equals(uncraftOut))
            {
            	// if the slot contains items
                if (slot.getHasStack())
                {
                	// attempt to add those items to the player's inventory
                    if (!playerInventory.addItemStackToInventory(slot.getStack()))
                    {
                    	// TODO: shouldn't this spawn items in the world if they can't be added to the player's inventory?
                        return ItemStack.EMPTY;
                    }
                    // clear the slot
                    slot.putStack(ItemStack.EMPTY);
                    // tell the slot that it's changed
                    slot.onSlotChanged();
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slotIn)
    {
        return !slotIn.inventory.equals(uncraftOut);
    }

    @Override
    public Slot getSlot(int slotId)
    {
        if (slotId >= this.inventorySlots.size())
        {
            slotId = this.inventorySlots.size() - 1;
        }
        return super.getSlot(slotId);
    }

}
