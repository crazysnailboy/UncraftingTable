package org.jglrxavpok.mods.decraft;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;
import org.jglrxavpok.mods.decraft.event.ItemUncraftedEvent;
import org.jglrxavpok.mods.decraft.event.UncraftingEvent;
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
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

/**
 * 
 * @author jglrxavpok
 *
 */
@SuppressWarnings("deprecation")
public class ContainerUncraftingTable extends Container
{

    public static enum UncraftingStatus
    {
        ERROR, 
        READY
    }

    public InventoryCrafting calculInput = new InventoryCrafting(this, 1, 1);
    public InventoryCrafting uncraftIn = new InventoryCrafting(this, 1, 1);
    public InventoryUncraftResult uncraftOut = new InventoryUncraftResult();
    public InventoryPlayer playerInventory;
    
    private World worldObj;
    
    public UncraftingStatus uncraftingStatus = UncraftingStatus.READY;
    public String uncraftingStatusText = I18n.translateToLocal("uncrafting.result.ready");
    
    public int uncraftingCost = ModConfiguration.standardLevel;
    
    

    public ContainerUncraftingTable(InventoryPlayer playerInventoryIn, World worldIn)
    {
        this.worldObj = worldIn;
        
    	// uncrafting output inventory
        for (int row = 0; row < 3; ++row)
        {
            for (int col = 0; col < 3; ++col)
            {
            	// arguments: inventory, slotIndex, xDisplayPosition, yDisplayPosition    
                this.addSlotToContainer(new Slot(this.uncraftOut, col + row * 3, 112 + col * 18, 17 + row * 18));
            }
        }
        
        // uncrafting book inventory for capturing enchantments (left standalone slot)
        this.addSlotToContainer(new Slot(this.calculInput, 0, 15, 35));

        // incrafting input inventory (right standalone slot)
        this.addSlotToContainer(new Slot(this.uncraftIn, 0, 30 + 15, 35));
        
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
     * Short story: fires a UncraftingEvent instance and look if the uncrafting is possible.
     * If possible, tries to do the uncrafting and fires a SuccessedUncraftingEvent if managed to do it.
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void onCraftMatrixChanged(IInventory inventory)
    {
//    	System.out.println("onCraftMatrixChanged");
        
        // if the left input slot changes
        if (inventory == calculInput)
        {
            // set the uncrafting cost to the default
        	uncraftingCost = ModConfiguration.standardLevel;
        	
        	// if the left slot is empty
            if (calculInput.getStackInSlot(0).isEmpty())
            {
                // if the right hand slot is empty
                if (uncraftIn.getStackInSlot(0).isEmpty())
                {
                	// set the status to ready
                    uncraftingStatus = UncraftingStatus.READY;
                    uncraftingStatusText = I18n.translateToLocal("uncrafting.result.ready");
                }
                return;
            }
            
            // if the left hand slot is not empty and the right hand slot is empty
            else if (uncraftIn.getStackInSlot(0).isEmpty())
            {
            	// get the uncrafting result for the item in the left hand slot
                List<NonNullList<ItemStack>> craftingGrids = UncraftingManager.getUncraftResults(calculInput.getStackInSlot(0));
                NonNullList<ItemStack> craftingGrid = (craftingGrids.size() > 0 ? craftingGrids.get(0) : null); // TODO: shouldn't be null

                // get the required number of items to uncraft the item in the left hand slot
                List<Integer> minStackSizes = UncraftingManager.getStackSizeNeeded(calculInput.getStackInSlot(0));
                int minStackSize = (minStackSizes.size() > 0 ? minStackSizes.get(0) : 1);
                
                // fire an uncrafting event
                // TODO (i can't see why this is needed. it's never handled anywhere)
                UncraftingEvent event = new UncraftingEvent(calculInput.getStackInSlot(0), craftingGrid, minStackSize, playerInventory.player);
                if (!MinecraftForge.EVENT_BUS.post(event))
                {
                	// read the minimum stack size back out of the event :|
                    int eventMinStackSize = event.getRequiredNumber();
                    
                    // if the minimum stack size is greater than the number of items in the slot
                    if (eventMinStackSize > calculInput.getStackInSlot(0).getCount())
                    {
                    	// set the uncrafting status as "error", with the not enough items message
                        uncraftingStatus = UncraftingStatus.ERROR;
                        uncraftingStatusText = I18n.translateToLocalFormatted("uncrafting.result.needMoreStacks", (eventMinStackSize - calculInput.getStackInSlot(0).getCount()));
                        return;
                    }
                    // if the item lookup didn't result in a crafting recipe being returned
                    else if (event.getOutput() == null)
                    {
                    	// set the uncrafting status as "error", with the not possible message
                        uncraftingStatus = UncraftingStatus.ERROR;
                        uncraftingStatusText = I18n.translateToLocal("uncrafting.result.impossible");
                        return;
                    }
                    // if the item is uncraftable, and there are enough items in the stack
                    else
                    {
                    	// set the uncrafting status to "ready"
                        uncraftingStatus = UncraftingStatus.READY;
                        uncraftingStatusText = I18n.translateToLocal("uncrafting.result.ready");
                    }
                    
                    // determine the xp cost for the uncrafting operation
            		uncraftingCost = UncraftingManager.getUncraftingXpCost(calculInput.getStackInSlot(0));

            		// if we don't have enough xp
            		if (!playerInventory.player.capabilities.isCreativeMode && playerInventory.player.experienceLevel < uncraftingCost)
            		{
            			// set the status to error, not enough xp and return
            			uncraftingStatus = UncraftingStatus.ERROR;
                    	uncraftingStatusText = I18n.translateToLocal("uncrafting.result.needMoreXP");
                    	return;
            		}
                
                }
            }
            // if the left hand slot is not empty and the right hand slot is not empty
            else
            {
            	// set the uncrafting status as "error", with the not possible message
            	// TODO this probably shouldn't be happening but the call to onCraftMatrixChanged for the right hand slot will override it
                uncraftingStatus = UncraftingStatus.ERROR;
                uncraftingStatusText = I18n.translateToLocal("uncrafting.result.impossible");
                return;
            }
        }
        
        
        // if the right input slot changes
        else if (inventory == uncraftIn)
        {
            // set the uncrafting cost to the default
            uncraftingCost = ModConfiguration.standardLevel;
            
            // if the right input slot is empty
            if (uncraftIn.getStackInSlot(0).isEmpty())
            {
            	// set the uncrafting status to "ready"
                uncraftingStatus = UncraftingStatus.READY;
                uncraftingStatusText = I18n.translateToLocal("uncrafting.result.ready");
                return;
            }
            
            
            // get the crafting grid which would result in the input item
            List<NonNullList<ItemStack>> craftingGrids = UncraftingManager.getUncraftResults(uncraftIn.getStackInSlot(0));
            NonNullList<ItemStack> craftingGrid = (craftingGrids.size() > 0 ? craftingGrids.get(0) : null); // TODO: shouldn't be null
            
            // get the minimum stack size needed to uncraft the input item
            List<Integer> minStackSizes = UncraftingManager.getStackSizeNeeded(uncraftIn.getStackInSlot(0));
            int minStackSize = (minStackSizes.size() > 0 ? minStackSizes.get(0) : 1);

            // fire an uncrafting event
            // TODO (i can't see why this is needed. it's never handled anywhere)
            UncraftingEvent event = new UncraftingEvent(uncraftIn.getStackInSlot(0), craftingGrid, minStackSize, playerInventory.player);
            if (!MinecraftForge.EVENT_BUS.post(event))
            {
            	// read the minimum stack size back out of the event :|
                int eventMinStackSize = event.getRequiredNumber();
                
                // if the minimum stack size is greater than the number of items in the slot
                if (eventMinStackSize > uncraftIn.getStackInSlot(0).getCount())
                {
                	// set the uncrafting status as "error", with the need more items message
                    uncraftingStatus = UncraftingStatus.ERROR;
                    uncraftingStatusText = I18n.translateToLocalFormatted("uncrafting.result.needMoreStacks", (eventMinStackSize - uncraftIn.getStackInSlot(0).getCount()));
                    return;
                }
                
                while (!uncraftIn.getStackInSlot(0).isEmpty() && eventMinStackSize <= uncraftIn.getStackInSlot(0).getCount())
                {
                	
                    // determine the xp cost for the uncrafting operation
            		uncraftingCost = UncraftingManager.getUncraftingXpCost(calculInput.getStackInSlot(0));
                    
            		// if we don't have enough xp
            		if (!playerInventory.player.capabilities.isCreativeMode && playerInventory.player.experienceLevel < uncraftingCost)
            		{
            			// set the status to error, not enough xp and return
            			uncraftingStatus = UncraftingStatus.ERROR;
                    	uncraftingStatusText = I18n.translateToLocal("uncrafting.result.needMoreXP");
                    	return;
            		}
            		// if we do have enough xp
                    if (!playerInventory.player.capabilities.isCreativeMode && playerInventory.player.experienceLevel >= uncraftingCost)
                    {
                    	// deduct the appropriate number of levels from the player
                    	playerInventory.player.experienceLevel -= uncraftingCost;
                    }
                    
                    
                    // if the item being uncrafted has enchantments, and there are books in the left hand slot
                    if (!EnchantmentHelper.getEnchantments(uncraftIn.getStackInSlot(0)).isEmpty() && calculInput.getStackInSlot(0) != ItemStack.EMPTY && calculInput.getStackInSlot(0).getItem() == Items.BOOK)
                    {
                    	// determine how many books are present in the left hand slot
                        int stackSize = calculInput.getStackInSlot(0).getCount();
                        
                        // build a map of the enchantments on the item in the right hand slot
                        Map itemEnchantments = EnchantmentHelper.getEnchantments(uncraftIn.getStackInSlot(0));
                        // initialise a list of itemstacks to hold enchanted books  
                        ArrayList<ItemStack> enchantedBooks = new ArrayList<ItemStack>();
                        
                        // if there's a single book in the left hand slot
                        if (stackSize == 1)
                        {
                        	// create an itemstack containing an enchanted book
                            ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK, 1);
                            // copy all of the enchantments from the map onto the book
                            EnchantmentHelper.setEnchantments(itemEnchantments, enchantedBook);
                            // add the book to the enchanted books collection
                            enchantedBooks.add(enchantedBook);
                        }
                        // if the left hand slot contains more than one book
                        else
                        {
                        	// iterate through the enchantments in the map
                        	Iterator<?> enchantmentIds = itemEnchantments.keySet().iterator();
                            while (enchantmentIds.hasNext())
                            {
                            	Enchantment bookEnchantment = (Enchantment)enchantmentIds.next();
                            	// create a new map of enchantments which will be applied to this book
                                Map<Enchantment, Integer> bookEnchantments = new LinkedHashMap<Enchantment, Integer>();
                                // copy the current enchantment into the map
                                bookEnchantments.put(bookEnchantment, (Integer)itemEnchantments.get(bookEnchantment));
                            	// create an itemstack containing an enchanted book
                                ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK, 1);
                                // place the enchantment onto the book
                                EnchantmentHelper.setEnchantments(bookEnchantments, enchantedBook);
                                // add the book to the enchanted books collection
                                enchantedBooks.add(enchantedBook);
                                // clear the book enchantments map
                                bookEnchantments.clear();
                            }
                        }
                        
                        // for each enchanted book itemstack in the itemstacks array
                        for (ItemStack enchantedBook : enchantedBooks)
                        {
                            stackSize-- ;
                            
                            // add the itemstack to the player inventory, or spawn in the world if the inventory is full
                            if (!playerInventory.addItemStackToInventory(enchantedBook))
                            {
                                EntityItem e = playerInventory.player.entityDropItem(enchantedBook, 0.5f);
                                e.posX = playerInventory.player.posX;
                                e.posY = playerInventory.player.posY;
                                e.posZ = playerInventory.player.posZ;
                            }
                            
                            // if we're at the end of the itemstacks array
                            if (stackSize <= 0)
                            {
                                calculInput.setInventorySlotContents(0, ItemStack.EMPTY);
                                break;
                            }
                            // otherwise decrement the stack size for the books in the left hand slot
                            calculInput.decrStackSize(0, 1);
                        }
                        
                    } // end of enchantment processing
                    
                    NonNullList<ItemStack> items = event.getOutput();
                    if (items == null)
                    {
                        uncraftingStatus = UncraftingStatus.ERROR;
                        uncraftingStatusText = I18n.translateToLocal("uncrafting.result.impossible");
                        return;
                    }
                    
                    if (!uncraftOut.isEmpty())
                    {
                        for (int i = 0; i < uncraftOut.getSizeInventory(); i++ )
                        {
                            ItemStack item = uncraftOut.getStackInSlot(i);
                            if ((item != ItemStack.EMPTY && items.get(i) != ItemStack.EMPTY && item.getItem() != items.get(i).getItem()))
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
                    
                    for (int i = 0; i < items.size(); i++ )
                    {
                        ItemStack s = items.get(i);
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
                    ItemUncraftedEvent sevent = new ItemUncraftedEvent(playerInventory.player, uncraftIn.getStackInSlot(0), items, event.getRequiredNumber());
                    if (!MinecraftForge.EVENT_BUS.post(sevent))
                    {
                        event.getPlayer().addStat(ModUncrafting.instance.uncraftedItemsStat, event.getRequiredNumber());
                        //event.getPlayer().triggerAchievement(ModUncrafting.instance.uncraftAny);
                    }
                    int i = uncraftIn.getStackInSlot(0).getCount() - event.getRequiredNumber();
                    ItemStack newStack = ItemStack.EMPTY;
                    if (i > 0)
                    {
                        newStack = new ItemStack(uncraftIn.getStackInSlot(0).getItem(), i, uncraftIn.getStackInSlot(0).getItemDamage());
                        //newStack = new ItemStack(uncraftIn.getStackInSlot(0).getItem(), i, 0);
                    }
                    else
                    {
                    	newStack = ItemStack.EMPTY;
                    }
                    uncraftIn.setInventorySlotContents(0, newStack);
                    this.onCraftMatrixChanged(calculInput);
                }
            }
            else
            {
                uncraftingStatus = UncraftingStatus.ERROR;
                uncraftingStatusText = I18n.translateToLocal("uncrafting.result.impossible");
            }
        }
        else
        {
            uncraftingStatus = UncraftingStatus.ERROR;
            uncraftingStatusText = I18n.translateToLocal("uncrafting.result.impossible");
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
