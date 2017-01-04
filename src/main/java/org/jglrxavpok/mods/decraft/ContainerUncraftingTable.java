package org.jglrxavpok.mods.decraft;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;
import org.jglrxavpok.mods.decraft.event.ItemUncraftedEvent;
import org.jglrxavpok.mods.decraft.event.UncraftingEvent;
import org.jglrxavpok.mods.decraft.item.uncrafting.UncraftingResult;
import org.jglrxavpok.mods.decraft.item.uncrafting.UncraftingResult.ResultType;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
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
                this.addSlotToContainer(new Slot(this.uncraftOut, col + row * 3, offsetX + col * 18, offsetY + row * 18));
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
    @Override
    public void onCraftMatrixChanged(IInventory inventory)
    {
        // if the left input slot changes
        if (inventory == calculInput)
        {
        	
        	// if the left slot is empty
            if (calculInput.getStackInSlot(0) == null)
            {
                // if the right hand slot is empty
                if (uncraftIn.getStackInSlot(0) == null)
                {
                	// clear the uncrafting result
                	this.uncraftingResult = new UncraftingResult();
                }
                return;
            }
            
            // if the left hand slot is not empty and the right hand slot is empty
            // i.e. calculation mode
            else if (uncraftIn.getStackInSlot(0) == null)
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
            if (uncraftIn.getStackInSlot(0) == null)
            {
            	// clear the uncrafting result
            	this.uncraftingResult = new UncraftingResult();
                return;
            }
            
        	// create an uncrafting result based on the contents of the right hand slot
        	this.uncraftingResult = UncraftingManager.getUncraftingResult(playerInventory.player, uncraftIn.getStackInSlot(0));
        	
        	if (UncraftingResult.ResultType.isError(uncraftingResult.resultType))
            {
            	return;
            }
        	
        	// --- TODO: this is all temporary code to match the uncraftingResult to existing variables
    		int minStackSize = (uncraftingResult.minStackSizes.size() > 0 ? uncraftingResult.minStackSizes.get(0) : 1);
            ItemStack[] craftingGrid = (uncraftingResult.craftingGrids.size() > 0 ? uncraftingResult.craftingGrids.get(0) : null);
        	// --- end of temporary code
            
            
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

        	
            while (uncraftIn.getStackInSlot(0) != null && minStackSize <= uncraftIn.getStackInSlot(0).stackSize)
            {
                // if the item being uncrafted has enchantments, and there are books in the left hand slot
            	if (uncraftIn.getStackInSlot(0).isItemEnchanted() && calculInput.getStackInSlot(0) != null && calculInput.getStackInSlot(0).getItem() == Items.book)
            	{
            		// copy the item enchantments onto one or more books
                    List<ItemStack> enchantedBooks = UncraftingManager.getItemEnchantments(uncraftIn.getStackInSlot(0), calculInput.getStackInSlot(0));
                    
                	// determine how many books are present in the left hand slot
            		int stackSize = calculInput.getStackInSlot(0).stackSize;
            		
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
                        // if the item in the slot does not match the item in the same slot in the uncrafting result
                        if ((item != null && craftingGrid[i] != null && item.getItem() != craftingGrid[i].getItem()))
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
                            uncraftOut.setInventorySlotContents(i, null);
                        }
                    }
                }

                
                // for each item stack in the uncrafting result
                for (int i = 0; i < craftingGrid.length; i++ )
                {
                    ItemStack s = craftingGrid[i];
                    
                    // if the current stack of the uncrafting result isn't empty
                    if (s != null)
                    {
                    	// if the uncrafting result doesn't specify metadata for this item, use the default value
                        int metadata = s.getItemDamage();
                        if (metadata == Short.MAX_VALUE) metadata = 0;

                        // get the stack from the matching slot of the output grid
                        ItemStack currentStack = uncraftOut.getStackInSlot(i);
                        ItemStack newStack = null;
                        
                        // if the stack in the current slot of the output grid is not already at it's maximum stack size
                        if (currentStack != null && currentStack.stackSize < s.getMaxStackSize())
                        {
                        	// create a new stack of the same item type and metadata, with one more item in it than previously  
                            newStack = new ItemStack(s.getItem(), currentStack.stackSize + 1, metadata);
                        }
                        else
                        {
                        	// if the stack isn't empty it's full, so attempt to move it to the player's inventory
                            if (currentStack != null && !playerInventory.addItemStackToInventory(currentStack))
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
                        	// create a new stack of the same item type and metadata, and a single item  
                            newStack = new ItemStack(s.getItem(), 1, metadata);
                        }
                        // replace the stack in the output grid with the new stack
                        uncraftOut.setInventorySlotContents(i, newStack);
                    }
                    
                    // if the current stack of the uncrafting result is empty
                    else
                    {
                    	// clear the matching slot in the output grid
                        uncraftOut.setInventorySlotContents(i, null);
                        // ***
                        // TODO: this is a bug. 
                        // if the result of the previous uncrafting operation is still in the grid, the items will be destroyed.
                        // ***
                    }
                }

                // fire an event indicating a successful uncrafting operation
                MinecraftForge.EVENT_BUS.post(new ItemUncraftedEvent(playerInventory.player, uncraftIn.getStackInSlot(0), craftingGrid, minStackSize));

            	// decrement the number of items in the input slot by the minimum stack size
                uncraftIn.decrStackSize(0, minStackSize);
            }

        }

    }

    @Override
    public ItemStack slotClick(int slotId, int clickedButton, int mode, EntityPlayer player)
    {
        ItemStack itemStack = super.slotClick(slotId, clickedButton, mode, player);
        
        if (inventorySlots.size() > slotId && slotId >= 0)
        {
            if (inventorySlots.get(slotId) != null)
            {
            	IInventory inventory = ((Slot)inventorySlots.get(slotId)).inventory;
            	if (inventory == calculInput) this.onCraftMatrixChanged(calculInput);
            	if (inventory == uncraftIn) this.onCraftMatrixChanged(uncraftIn);
            }
        }
        return itemStack;
    }

    @Override
    public void onContainerClosed(EntityPlayer player)
    {
        if (playerInventory.getItemStack() != null)
        {
            player.entityDropItem(playerInventory.getItemStack(), 0.5f);
        }
        if (!this.worldObj.isRemote)
        {
            ItemStack itemstack = this.uncraftIn.getStackInSlotOnClosing(0);
            if (itemstack != null)
            {
                player.entityDropItem(itemstack, 0.5f);
            }

            itemstack = this.calculInput.getStackInSlotOnClosing(0);
            if (itemstack != null)
            {
                player.entityDropItem(itemstack, 0.5f);
            }
            for (int i = 0; i < uncraftOut.getSizeInventory(); i++ )
            {
                itemstack = this.uncraftOut.getStackInSlotOnClosing(i);

                if (itemstack != null)
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
        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack())
            if (slot.inventory.equals(calculInput))
            {
                ItemStack itemstack1 = slot.getStack();
                slot.onPickupFromSlot(player, itemstack1);
                if (!playerInventory.addItemStackToInventory(itemstack1))
                {
                    return null;
                }
                slot.putStack(null);
            }
            else if (slot.inventory.equals(uncraftIn))
            {
                if (slot.getHasStack())
                {
                    if (!playerInventory.addItemStackToInventory(slot.getStack()))
                    {
                        return null;
                    }
                    slot.putStack(null);
                    slot.onSlotChanged();
                }
            }
            else if (slot.inventory.equals(playerInventory))
            {
                Slot calcInput = null;
                Slot uncraftSlot = null;
                for (Object s : inventorySlots)
                {
                    Slot s1 = (Slot) s;
                    if (s1.inventory.equals(calculInput))
                    {
                        calcInput = s1;
                    }
                    else if (s1.inventory.equals(uncraftIn))
                    {
                        uncraftSlot = s1;
                    }
                }
                if (calcInput != null)
                {
                    if (calcInput.getStack() == null)
                    {
                        calcInput.putStack(slot.getStack());
                        calcInput.onSlotChanged();
                        slot.putStack(null);
                    }
                    else
                    {
                        if (slot.getStack() != null)
                        {
                            ItemStack i = slot.getStack();
                            slot.onPickupFromSlot(player, slot.getStack());
                            slot.putStack(calcInput.getStack().copy());
                            calcInput.putStack(i.copy());
                            this.onCraftMatrixChanged(calculInput);
                            calcInput.onSlotChanged();
                        }
                        else
                        {
                            return null;
                        }
                    }
                }
            }
            else if (slot.inventory.equals(uncraftOut))
            {
                if (slot.getHasStack())
                {
                    if (!playerInventory.addItemStackToInventory(slot.getStack()))
                    {
                        return null;
                    }
                    slot.putStack(null);
                    slot.onSlotChanged();
                }
            }
        return null;
    }

    @Override
    public boolean func_94530_a(ItemStack p_94530_1_, Slot p_94530_2_) //public boolean canMergeSlot(ItemStack stack, Slot slotIn)
    {
        return !p_94530_2_.inventory.equals(uncraftOut);
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
