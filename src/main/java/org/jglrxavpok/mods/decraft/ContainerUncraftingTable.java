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
    		int minStackSize = (uncraftingResult.minStackSizes.size() > 0 ? uncraftingResult.minStackSizes.get(0) : 1);
            NonNullList<ItemStack> craftingGrid = (uncraftingResult.craftingGrids.size() > 0 ? uncraftingResult.craftingGrids.get(0) : null);
        	// --- end of temporary code
            
            while (!uncraftIn.getStackInSlot(0).isEmpty() && minStackSize <= uncraftIn.getStackInSlot(0).getCount())
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
                
                
                if (!uncraftOut.isEmpty())
                {
                    for (int i = 0; i < uncraftOut.getSizeInventory(); i++ )
                    {
                        ItemStack item = uncraftOut.getStackInSlot(i);
                        if ((item != ItemStack.EMPTY && craftingGrid.get(i) != ItemStack.EMPTY && item.getItem() != craftingGrid.get(i).getItem()))
                        {
                            if (!playerInventory.addItemStackToInventory(item))
                            {
                            	if (!worldObj.isRemote)
                            	{
                                    EntityItem e = playerInventory.player.entityDropItem(item, 0.5f);
                                    e.posX = playerInventory.player.posX;
                                    e.posY = playerInventory.player.posY;
                                    e.posZ = playerInventory.player.posZ;
                            	}
                            }
                            uncraftOut.setInventorySlotContents(i, ItemStack.EMPTY);
                        }
                    }
                }
                
                for (int i = 0; i < craftingGrid.size(); i++ )
                {
                    ItemStack s = craftingGrid.get(i);
                    ItemStack currentStack = uncraftOut.getStackInSlot(i);
                    if (s != ItemStack.EMPTY)
                    {
                        int metadata = s.getItemDamage();
                        if (metadata == Short.MAX_VALUE) metadata = 0;

                        ItemStack newStack = ItemStack.EMPTY;
                        if (!currentStack.isEmpty() && 1 + currentStack.getCount() <= s.getMaxStackSize())
                        {
                            newStack = new ItemStack(s.getItem(), 1 + currentStack.getCount(), metadata);
                        }
                        else
                        {
                            if (!currentStack.isEmpty() && !playerInventory.addItemStackToInventory(currentStack))
                            {
                            	if (!worldObj.isRemote)
                            	{
                                    EntityItem e = playerInventory.player.entityDropItem(currentStack, 0.5f);
                                    e.posX = playerInventory.player.posX;
                                    e.posY = playerInventory.player.posY;
                                    e.posZ = playerInventory.player.posZ;
                            	}
                            }
                            newStack = new ItemStack(s.getItem(), 1, metadata);
                        }
                        uncraftOut.setInventorySlotContents(i, newStack);
                    }
                    else
                    {
                        uncraftOut.setInventorySlotContents(i, ItemStack.EMPTY);
                    }
                }
                ItemStack stack = uncraftIn.getStackInSlot(0);
                //    				int n = (stack.stackSize-nbrStacks);
                //    				if (n > 0)
                //    				{
                //    					ItemStack newStack = new ItemStack(stack.getItem(), n, stack.getItemDamageForDisplay());
                //    					if (!playerInv.addItemStackToInventory(newStack))
                //    					{
                //    						EntityItem e = playerInv.player.entityDropItem(newStack,0.5f);
                //    						e.posX = playerInv.player.posX;
                //    						e.posY = playerInv.player.posY;
                //    						e.posZ = playerInv.player.posZ;
                //    					}
                //    				}
                ItemUncraftedEvent sevent = new ItemUncraftedEvent(playerInventory.player, uncraftIn.getStackInSlot(0), craftingGrid, minStackSize);
                if (!MinecraftForge.EVENT_BUS.post(sevent))
                {
                    playerInventory.player.addStat(ModUncrafting.instance.uncraftedItemsStat, minStackSize);
                }
                int i = uncraftIn.getStackInSlot(0).getCount() - minStackSize;
                ItemStack newStack = ItemStack.EMPTY;
                if (i > 0)
                {
                    newStack = new ItemStack(uncraftIn.getStackInSlot(0).getItem(), i, uncraftIn.getStackInSlot(0).getItemDamage());
                }
                else
                {
                	newStack = ItemStack.EMPTY;
                }
                uncraftIn.setInventorySlotContents(0, newStack);
                this.onCraftMatrixChanged(calculInput);
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
        Slot slot = (Slot)this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack())
            if (slot.inventory.equals(calculInput))
            {
                ItemStack itemstack1 = slot.getStack();

//              slot.onPickupFromSlot(player, itemstack1);
                slot.onSlotChanged(); // in 1.9 / 1.10, onPickupFromSlot just called onSlotChanged with no parameters
                
                if (!playerInventory.addItemStackToInventory(itemstack1))
                {
                    return ItemStack.EMPTY;
                }
                slot.putStack(ItemStack.EMPTY);
            }
            else if (slot.inventory.equals(uncraftIn))
            {
                if (slot.getHasStack())
                {
                    if (!playerInventory.addItemStackToInventory(slot.getStack()))
                    {
                        return ItemStack.EMPTY;
                    }
                    slot.putStack(ItemStack.EMPTY);
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
                    if (!calcInput.getStack().isEmpty())
                    {
                        calcInput.putStack(slot.getStack());
                        calcInput.onSlotChanged();
                        slot.putStack(ItemStack.EMPTY);
                    }
                    else
                    {
                        if (slot.getStack().isEmpty())
                        {
                            ItemStack i = slot.getStack();
//                            slot.onPickupFromSlot(player, slot.getStack());
                            slot.onSlotChanged(); // in 1.9 / 1.10, onPickupFromSlot just called onSlotChanged with no parameters
                            slot.putStack(calcInput.getStack().copy());
                            calcInput.putStack(i.copy());
                            this.onCraftMatrixChanged(calculInput);
                            calcInput.onSlotChanged();
                        }
                        else
                        {
                            return ItemStack.EMPTY;
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
                        return ItemStack.EMPTY;
                    }
                    slot.putStack(ItemStack.EMPTY);
                    slot.onSlotChanged();
                }
            }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slotIn) //public boolean canMergeSlot(ItemStack stack, Slot slotIn)
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
