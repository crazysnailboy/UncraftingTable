package org.jglrxavpok.mods.decraft;

import java.util.*;

import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;
import org.jglrxavpok.mods.decraft.event.ItemUncraftedEvent;
import org.jglrxavpok.mods.decraft.event.UncraftingEvent;
import org.jglrxavpok.mods.decraft.stats.ModAchievements;

import net.minecraft.client.resources.*;
import net.minecraft.enchantment.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.world.*;
import net.minecraftforge.common.*;

/**
 * 
 * @author jglrxavpok
 *
 */
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
    public String uncraftingStatusText = I18n.format("uncrafting.result.ready");
    
    public int uncraftingCost = 0 - ModConfiguration.standardLevel;
    
    
    
    

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
//        	System.out.println("inventory == calculInput: " + (inventory == calculInput));
        	
        	// if the left slot is empty
            if (calculInput.getStackInSlot(0) == null)
            {
                uncraftingCost = 0;
                
                // if the right hand slot is empty
                if (uncraftIn.getStackInSlot(0) == null)
                {
                	// set the status to ready
                    uncraftingStatus = UncraftingStatus.READY;
                    uncraftingStatusText = I18n.format("uncrafting.result.ready");
                    
                    // set the uncrafting cost to the default
                	uncraftingCost = 0 - ModConfiguration.standardLevel; // TODO is this necessary?
                }
                return;
            }
            
            // if the left hand slot is not empty and the right hand slot is empty
            // i.e. calculation mode
            else if (uncraftIn.getStackInSlot(0) == null)
            {
            	// get the uncrafting result for the item in the left hand slot
                List<ItemStack[]> list1 = UncraftingManager.getUncraftResults(calculInput.getStackInSlot(0));
                ItemStack[] output = null;
                if (list1.size() > 0) output = list1.get(0);

                // get the required number of items to uncraft the item in the left hand slot
                List<Integer> needs = UncraftingManager.getStackSizeNeeded(calculInput.getStackInSlot(0));
                int required = 1;
                if (needs.size() > 0) required = needs.get(0);
                
                // fire an uncrafting event
                // TODO (i can't see why this is needed. it's never handled anywhere)
                UncraftingEvent event = new UncraftingEvent(calculInput.getStackInSlot(0), output, required, playerInventory.player);
                if (!MinecraftForge.EVENT_BUS.post(event))
                {
                	// read the minimum stack size back out of the event :|
                    int eventMinStackSize = event.getRequiredNumber();
                    if (eventMinStackSize > calculInput.getStackInSlot(0).stackSize)
                    {
                    	// set the uncrafting status as "error", with the need more items message
                        uncraftingStatus = UncraftingStatus.ERROR;
                        uncraftingStatusText = I18n.format("uncrafting.result.needMoreStacks", (eventMinStackSize - calculInput.getStackInSlot(0).stackSize));
                        uncraftingCost = 0 - ModConfiguration.standardLevel;
                        return;
                    }
                    // if the item lookup didn't result in a crafting recipe being returned
                    else if (event.getOutput() == null)
                    {
                    	// set the uncrafting status as "error", with the not possible message
                        uncraftingStatus = UncraftingStatus.ERROR;
                        uncraftingStatusText = I18n.format("uncrafting.result.impossible");
                        uncraftingCost = 0 - ModConfiguration.standardLevel;
                        return;
                    }
                    // if the item is uncraftable, and there are enough items in the stack
                    else
                    {
                    	// set the uncrafting status to "ready"
                        uncraftingStatus = UncraftingStatus.READY;
                        uncraftingStatusText = I18n.format("uncrafting.result.ready");
                    }
                    
                    // determine the xp cost for the uncrafting operation
                    if (ModConfiguration.uncraftMethod == 0)
                    {
                    	// utilise either the standard cost if we're using jglrxavpok's uncrafting method
                        uncraftingCost = 0;
                    }
                    else if (ModConfiguration.uncraftMethod == 1)
                    {
                        // or calculate a cost based on the damage percentage of the item if we're using Xell75's & Zenen's uncrafting method
                        ItemStack s1 = calculInput.getStackInSlot(0);
                        
                        if (s1.getItem().isDamageable())
                        {
                        	int damagePercentage = (int)(((double)s1.getItemDamage() / (double)s1.getMaxDamage()) * 100);
                        	uncraftingCost = (ModConfiguration.maxUsedLevel * damagePercentage) / 100;
                        }
                        else
                        {
                        	uncraftingCost = 0;
                        }
                        
                    }
                }
            }
            // if the left hand slot is not empty and the right hand slot is not empty
            else
            {
            	// set the uncrafting status as "error", with the not possible message
            	// TODO this probably shouldn't be happening but the call to onCraftMatrixChanged for the right hand slot will override it
                uncraftingStatus = UncraftingStatus.ERROR;
                uncraftingStatusText = I18n.format("uncrafting.result.impossible");
                uncraftingCost = 0 - ModConfiguration.standardLevel;
                return;
            }
        }
        
        
        // if the right input slot changes
        else if (inventory == uncraftIn)
        {
            uncraftingCost = 0;
            
            // if the right input slot is empty
            if (uncraftIn.getStackInSlot(0) == null)
            {
            	// set the uncrafting status to "ready"
                uncraftingStatus = UncraftingStatus.READY;
                uncraftingStatusText = I18n.format("uncrafting.result.ready");
                return;
            }
            
            // get the crafting grid which would result in the input item
            List<ItemStack[]> craftingGrids = UncraftingManager.getUncraftResults(uncraftIn.getStackInSlot(0));
            ItemStack[] craftingGrid = (craftingGrids.size() > 0 ? craftingGrids.get(0) : null);
            
            // get the minimum stack size needed to uncraft the input item
            List<Integer> mineStackSizes = UncraftingManager.getStackSizeNeeded(uncraftIn.getStackInSlot(0));
            int minStackSize = (mineStackSizes.size() > 0 ? mineStackSizes.get(0) : 1);

            // fire an uncrafting event
            // TODO (i can't see why this is needed. it's never handled anywhere)
            UncraftingEvent event = new UncraftingEvent(uncraftIn.getStackInSlot(0), craftingGrid, minStackSize, playerInventory.player);
            if (!MinecraftForge.EVENT_BUS.post(event))
            {
            	// read the minimum stack size back out of the event :|
                int eventMinStackSize = event.getRequiredNumber();
                
                // if the minimum stack size is greater than the number of items in the slot
                if (eventMinStackSize > uncraftIn.getStackInSlot(0).stackSize)
                {
                	// set the uncrafting status as "error", with the need more items message
                    uncraftingStatus = UncraftingStatus.ERROR;
                    uncraftingStatusText = I18n.format("uncrafting.result.needMoreStacks", (eventMinStackSize - uncraftIn.getStackInSlot(0).stackSize));
                    return;
                }
                
                while (uncraftIn.getStackInSlot(0) != null && eventMinStackSize <= uncraftIn.getStackInSlot(0).stackSize)
                {
                    EntityPlayer player = playerInventory.player;
                    int lvl = player.experienceLevel;
                    uncraftingCost = 0;
                    
                    // determine the xp cost for the uncrafting operation
                    if (!player.capabilities.isCreativeMode)
                    {
                    	// if we're using Xell75's & Zenen's uncrafting method, and the item is enchantable (i.e. damageable)
                    	// otherwise use the standard cost
                    	if (uncraftIn.getStackInSlot(0).getItem().getItemEnchantability() > 0 && ModConfiguration.uncraftMethod == 1)
                    	{
                            // calculate a cost based on the damage percentage of the item 
                        	ItemStack s1 = uncraftIn.getStackInSlot(0);
                        	int percent = (int)(((double)s1.getItemDamage() / (double)s1.getMaxDamage()) * 100);
                        	uncraftingCost = (ModConfiguration.maxUsedLevel * percent) / 100;
                    	}
                    	
                        // if the player's xp level is unsufficient to perform the operation
                        if (lvl < ModConfiguration.standardLevel + uncraftingCost && !player.capabilities.isCreativeMode)
                        {
                        	// set the status to "error" with the needs more xp message
                        	uncraftingStatus = UncraftingStatus.ERROR;
                        	uncraftingStatusText = I18n.format("uncrafting.result.needMoreXP");
                        	return;
                        }
                    }
                    	
                    
                    if (!EnchantmentHelper.getEnchantments(uncraftIn.getStackInSlot(0)).isEmpty() && calculInput.getStackInSlot(0) != null && calculInput.getStackInSlot(0).getItem() == Items.book)
                    {
                        int stackSize = calculInput.getStackInSlot(0).stackSize;
                        
                        Map itemEnchantments = EnchantmentHelper.getEnchantments(uncraftIn.getStackInSlot(0));
                        ArrayList<ItemStack> enchantedBooks = new ArrayList<ItemStack>();
                        if (stackSize == 1)
                        {
                            ItemStack enchantedBook = new ItemStack(Items.enchanted_book, 1);
                            EnchantmentHelper.setEnchantments(itemEnchantments, enchantedBook);
                            enchantedBooks.add(enchantedBook);
                        }
                        else
                        {
                            Iterator<?> enchantmentIds = itemEnchantments.keySet().iterator();
                            while (enchantmentIds.hasNext())
                            {
                                int enchantmentId = (Integer)enchantmentIds.next();
                                Map<Integer, Integer> bookEnchantments = new LinkedHashMap<Integer, Integer>();
                                bookEnchantments.put(enchantmentId, (Integer)itemEnchantments.get(enchantmentId));
                                ItemStack enchantedBook = new ItemStack(Items.enchanted_book, 1);
                                EnchantmentHelper.setEnchantments(bookEnchantments, enchantedBook);
                                enchantedBooks.add(enchantedBook);
                                bookEnchantments.clear();
                            }
                        }
                    	
                        for (ItemStack enchantedBook : enchantedBooks)
                        {
                            stackSize-- ;
                            if (!playerInventory.addItemStackToInventory(enchantedBook))
                            {
                                EntityItem e = playerInventory.player.entityDropItem(enchantedBook, 0.5f);
                                e.posX = playerInventory.player.posX;
                                e.posY = playerInventory.player.posY;
                                e.posZ = playerInventory.player.posZ;
                            }
                            if (stackSize <= 0)
                            {
                                calculInput.setInventorySlotContents(0, null);
                                break;
                            }
                            
                            calculInput.decrStackSize(0, 1);
                        }
                    }
                    
                    ItemStack[] items = event.getOutput();
                    if (items == null)
                    {
                        String r = I18n.format("uncrafting.result.impossible");
                        uncraftingStatusText = r;
                        uncraftingStatus = UncraftingStatus.ERROR;
                        return;
                    }
                    
                    if (!playerInventory.player.capabilities.isCreativeMode && uncraftIn.getStackInSlot(0).getItem().getItemEnchantability() > 0)
                    {
                        if (ModConfiguration.uncraftMethod == 0)
                        {
                            int count = 0;
                            ItemStack s1 = uncraftIn.getStackInSlot(0);

                            int percent = (int) (((double) s1.getItemDamage() / (double) s1.getMaxDamage()) * 100);
                            for (int i = 0; i < items.length; i++ )
                            {
                                if (items[i] != null)
                                    count++ ;
                            }
                            int toRemove = Math.round((float) (percent * count) / 100f);
                            if (toRemove > 0)
                                for (int i = 0; i < items.length; i++ )
                                {
                                    if (items[i] != null)
                                    {
                                        toRemove-- ;
                                        items[i] = null;
                                        if (toRemove <= 0)
                                        {
                                            break;
                                        }
                                    }
                                }
                        }
                        else if (ModConfiguration.uncraftMethod == 1)
                        {
                            ItemStack s1 = uncraftIn.getStackInSlot(0);
                            int percent = (int)(((double)s1.getItemDamage() / (double)s1.getMaxDamage()) * 100);
                            uncraftingCost = (ModConfiguration.maxUsedLevel * percent) / 100;
                        }
                    }
                    if (lvl < ModConfiguration.standardLevel + uncraftingCost && !player.capabilities.isCreativeMode)
                    {
                        String r = I18n.format("uncrafting.result.needMoreXP");
                        uncraftingStatusText = r;
                        uncraftingStatus = UncraftingStatus.ERROR;
                        return;
                    }
                    else if (lvl >= ModConfiguration.standardLevel + uncraftingCost && !player.capabilities.isCreativeMode)
                    {
                        player.experienceLevel -= ModConfiguration.standardLevel + uncraftingCost;
                    }
                    if (!uncraftOut.isEmpty())
                    {
                        for (int i = 0; i < uncraftOut.getSizeInventory(); i++ )
                        {
                            ItemStack item = uncraftOut.getStackInSlot(i);
                            if ((item != null && items[i] != null && item.getItem() != items[i].getItem()))
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
                                uncraftOut.setInventorySlotContents(i, null);
                            }
                        }
                    }
                    
//                    System.out.println("items.length: " + items.length);
                    

                    for (int i = 0; i < items.length; i++ )
                    {
                        ItemStack s = items[i];
                        ItemStack currentStack = uncraftOut.getStackInSlot(i);
                        if (s != null)
                        {
                            int metadata = s.getItemDamage();
                            if (metadata == 32767)
                            {
                                metadata = 0;
                            }
                            ItemStack newStack = null;
                            if (currentStack != null && 1 + currentStack.stackSize <= s.getMaxStackSize())
                            {
                                newStack = new ItemStack(s.getItem(), 1 + currentStack.stackSize, metadata);
                            }
                            else
                            {
                                if (currentStack != null && !playerInventory.addItemStackToInventory(currentStack))
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
                            uncraftOut.setInventorySlotContents(i, null);
                        }
                    }
                    ItemStack stack = uncraftIn.getStackInSlot(0);
                    //    				int n = (stack.stackSize-nbrStacks);
                    //    				if (n > 0)
                    //    				{
                    //    					ItemStack newStack = new ItemStack(stack.getItem(), n, stack.getItemDamageForDisplay());
                    //    //					toReturn = newStack;
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
                    int i = uncraftIn.getStackInSlot(0).stackSize - event.getRequiredNumber();
                    ItemStack newStack = null;
                    if (i > 0)
                    {
                        newStack = new ItemStack(uncraftIn.getStackInSlot(0).getItem(), i, uncraftIn.getStackInSlot(0).getItemDamage());
                        //newStack = new ItemStack(uncraftIn.getStackInSlot(0).getItem(), i, 0);
                    }
                    uncraftIn.setInventorySlotContents(0, newStack);
                    this.onCraftMatrixChanged(calculInput);
                }
            }
            else
            {
                String r = I18n.format("uncrafting.result.impossible");
                uncraftingStatusText = r;
                uncraftingStatus = UncraftingStatus.ERROR;
            }
        }
        else
        {
            String r = I18n.format("uncrafting.result.impossible");
            uncraftingStatusText = r;
            uncraftingStatus = UncraftingStatus.ERROR;
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
            	
            	if (inventory == calculInput) // || inventory == playerInventory)
            	{
                    this.onCraftMatrixChanged(calculInput);
            	}
            	if (inventory == uncraftIn)
            	{
                    this.onCraftMatrixChanged(uncraftIn);
            	}
            	
//                if (
//    				((Slot)inventorySlots.get(slotId)).inventory == calculInput 
//					|| 
//					((Slot)inventorySlots.get(slotId)).inventory == playerInventory
//            	)
//                {
//                    this.onCraftMatrixChanged(calculInput);
//                }
//                else if (((Slot) inventorySlots.get(slotId)).inventory == uncraftIn)
//                {
//                    this.onCraftMatrixChanged(uncraftIn);
//                }
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
