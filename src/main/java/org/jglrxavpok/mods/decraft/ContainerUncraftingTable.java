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

import net.minecraft.client.resources.I18n;
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
    
    

    public ContainerUncraftingTable(InventoryPlayer playerInventoryIn, World worldIn, boolean inverted)
    {
        this.worldObj = worldIn;
        
        if (!inverted)
        {
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
        }
        else
        {
            int height = 166 - 16;
            
        	// uncrafting output inventory
            for (int row = 0; row < 3; ++row)
            {
                for (int col = 0; col < 3; ++col)
                {
                    this.addSlotToContainer(new Slot(this.uncraftOut, col + row * 3, 112 + col * 18, height - (17 + row * 18)));
                }
            }

            // uncrafting book inventory for capturing enchantments (left standalone slot)
            this.addSlotToContainer(new Slot(this.calculInput, 0, 15, height - 35));

            // incrafting input inventory (right standalone slot)
            this.addSlotToContainer(new Slot(this.uncraftIn, 0, 30 + 15, height - 35));

            // player inventory
            for (int row = 0; row < 3; ++row)
            {
                for (int col = 0; col < 9; ++col)
                {
                    this.addSlotToContainer(new Slot(playerInventoryIn, col + row * 9 + 9, 8 + col * 18, height - (84 + row * 18)));
                }
            }

            // player hotbar inventory
            for (int col = 0; col < 9; ++col)
            {
                this.addSlotToContainer(new Slot(playerInventoryIn, col, 8 + col * 18, height - 142));
            }
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
            if (calculInput.getStackInSlot(0).func_190926_b())
            {
                uncraftingCost = 0;
                
                // if the right hand slot is empty
                if (uncraftIn.getStackInSlot(0).func_190926_b())
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
            else if (uncraftIn.getStackInSlot(0).func_190926_b())
            {
            	// get the uncrafting result for the item in the left hand slot
                List<NonNullList<ItemStack>> list1 = UncraftingManager.getUncraftResults(calculInput.getStackInSlot(0));
                NonNullList<ItemStack> output = null;
                if (list1.size() > 0) output = list1.get(0);

                // get the required number of items to uncraft the item in the left hand slot
                List<Integer> needs = UncraftingManager.getStackSizeNeeded(calculInput.getStackInSlot(0));
                int required = 1;
                if (needs.size() > 0) required = needs.get(0);
                
                
                UncraftingEvent event = new UncraftingEvent(calculInput.getStackInSlot(0), output, required, playerInventory.player);
                if (!MinecraftForge.EVENT_BUS.post(event))
                {
                    int nbrStacks = event.getRequiredNumber();
                    if (nbrStacks > calculInput.getStackInSlot(0).getMaxStackSize())
                    {
                        uncraftingStatus = UncraftingStatus.ERROR;
                        uncraftingStatusText = I18n.format("uncrafting.result.needMoreStacks", (nbrStacks - calculInput.getStackInSlot(0).getMaxStackSize()));
                        uncraftingCost = 0 - ModConfiguration.standardLevel;
                        return;
                    }
                    else if (event.getOutput() == null)
                    {
                        uncraftingStatus = UncraftingStatus.ERROR;
                        uncraftingStatusText = I18n.format("uncrafting.result.impossible");
                        uncraftingCost = 0 - ModConfiguration.standardLevel;
                        return;
                    }
                    else
                    {
                        uncraftingStatus = UncraftingStatus.READY;
                        uncraftingStatusText = I18n.format("uncrafting.result.ready");
                    }
                    
                    if (ModConfiguration.uncraftMethod == 0)
                    {
                        uncraftingCost = 0;
                    }
                    else if (ModConfiguration.uncraftMethod == 1)
                    {
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
            else
            {
                uncraftingStatus = UncraftingStatus.ERROR;
                uncraftingStatusText = I18n.format("uncrafting.result.impossible");
                uncraftingCost = 0 - ModConfiguration.standardLevel;
                return;
            }
        }
        
        
        else if (inventory == uncraftIn)
        {
        	System.out.println("inventory == uncraftIn: " + (inventory == uncraftIn));
        	
            uncraftingCost = 0;
            if (uncraftIn.getStackInSlot(0).func_190926_b())
            {
                uncraftingStatus = UncraftingStatus.READY;
                uncraftingStatusText = I18n.format("uncrafting.result.ready");
                return;
            }
            
//        	System.out.println("onCraftMatrixChanged");
//    		System.out.println("isRemote: " + worldObj.isRemote);
//    		System.out.println("displayName: " + uncraftIn.getStackInSlot(0).getDisplayName());
//    		System.out.println("stackSize: " + uncraftIn.getStackInSlot(0).stackSize);
            
            
            List<NonNullList<ItemStack>> list1 = UncraftingManager.getUncraftResults(uncraftIn.getStackInSlot(0));
            NonNullList<ItemStack> output = null;
            if (list1.size() > 0) output = list1.get(0);
            
            List<Integer> needs = UncraftingManager.getStackSizeNeeded(uncraftIn.getStackInSlot(0));
            int required = 1;
            if (needs.size() > 0) required = needs.get(0);

            UncraftingEvent event = new UncraftingEvent(uncraftIn.getStackInSlot(0), output, required, playerInventory.player);
            if (!MinecraftForge.EVENT_BUS.post(event))
            {
                int nbrStacks = event.getRequiredNumber();
                if (nbrStacks > uncraftIn.getStackInSlot(0).getMaxStackSize())
                {
                    uncraftingStatus = UncraftingStatus.ERROR;
                    uncraftingStatusText = I18n.format("uncrafting.result.needMoreStacks", (nbrStacks - uncraftIn.getStackInSlot(0).getMaxStackSize()));
                    return;
                }
                
                while (!uncraftIn.getStackInSlot(0).func_190926_b() && nbrStacks <= uncraftIn.getStackInSlot(0).getMaxStackSize())
                {
                    EntityPlayer player = playerInventory.player;
                    int lvl = player.experienceLevel;
                    uncraftingCost = 0;
                    
                    if (!EnchantmentHelper.getEnchantments(uncraftIn.getStackInSlot(0)).isEmpty() && calculInput.getStackInSlot(0) != null && calculInput.getStackInSlot(0).getItem() == Items.BOOK)
                    {
                        int stackSize = calculInput.getStackInSlot(0).getMaxStackSize();
                        
                        Map itemEnchantments = EnchantmentHelper.getEnchantments(uncraftIn.getStackInSlot(0));
                        ArrayList<ItemStack> enchantedBooks = new ArrayList<ItemStack>();
                        if (stackSize == 1)
                        {
                            ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK, 1);
                            EnchantmentHelper.setEnchantments(itemEnchantments, enchantedBook);
                            enchantedBooks.add(enchantedBook);
                        }
                        else
                        {
                        	Iterator<?> enchantmentIds = itemEnchantments.keySet().iterator();
                            while (enchantmentIds.hasNext())
                            {
                            	Enchantment bookEnchantment = (Enchantment)enchantmentIds.next();
                                Map<Enchantment, Integer> bookEnchantments = new LinkedHashMap<Enchantment, Integer>();
                                bookEnchantments.put(bookEnchantment, (Integer)itemEnchantments.get(bookEnchantment));
                                ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK, 1);
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
                                calculInput.setInventorySlotContents(0, ItemStack.field_190927_a);
                                break;
                            }
                            
                            calculInput.decrStackSize(0, 1);
                        }
                    }
                    
                    NonNullList<ItemStack> items = event.getOutput();
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
                            for (int i = 0; i < items.size(); i++ )
                            {
                                if (items.get(i) != null)
                                    count++ ;
                            }
                            int toRemove = Math.round((float) (percent * count) / 100f);
                            if (toRemove > 0)
                            {
                                for (int i = 0; i < items.size(); i++ )
                                {
                                    if (items.get(i) != ItemStack.field_190927_a)
                                    {
                                        toRemove-- ;
                                        items.set(i, ItemStack.field_190927_a);
                                        if (toRemove <= 0)
                                        {
                                            break;
                                        }
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
                            if ((item != ItemStack.field_190927_a && items.get(i) != ItemStack.field_190927_a && item.getItem() != items.get(i).getItem()))
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
                                uncraftOut.setInventorySlotContents(i, ItemStack.field_190927_a);
                            }
                        }
                    }
                    
//                    System.out.println("items.length: " + items.length);
                    

                    for (int i = 0; i < items.size(); i++ )
                    {
                        ItemStack s = items.get(i);
                        ItemStack currentStack = uncraftOut.getStackInSlot(i);
                        if (s != ItemStack.field_190927_a)
                        {
                        	
//                        	System.out.println("displayName: " + s.getDisplayName());
//                        	System.out.println("metaData: " + s.getMetadata());
                        	
                            int metadata = s.getItemDamage();
                            if (metadata == 32767)
                            {
                                metadata = 0;
                            }
                            ItemStack newStack = ItemStack.field_190927_a;
                            if (!currentStack.func_190926_b() && 1 + currentStack.getMaxStackSize() <= s.getMaxStackSize())
                            {
                                newStack = new ItemStack(s.getItem(), 1 + currentStack.getMaxStackSize(), metadata);
                            }
                            else
                            {
                                if (!currentStack.func_190926_b() && !playerInventory.addItemStackToInventory(currentStack))
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
                            uncraftOut.setInventorySlotContents(i, ItemStack.field_190927_a);
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
                    int i = uncraftIn.getStackInSlot(0).getMaxStackSize() - event.getRequiredNumber();
                    ItemStack newStack = ItemStack.field_190927_a;
                    if (i > 0)
                    {
                        newStack = new ItemStack(uncraftIn.getStackInSlot(0).getItem(), i, uncraftIn.getStackInSlot(0).getItemDamage());
                        //newStack = new ItemStack(uncraftIn.getStackInSlot(0).getItem(), i, 0);
                    }
                    else
                    {
                    	newStack = ItemStack.field_190927_a;
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
        if (playerInventory.getItemStack() != ItemStack.field_190927_a)
        {
            player.entityDropItem(playerInventory.getItemStack(), 0.5f);
        }
        if (!this.worldObj.isRemote)
        {
            ItemStack itemstack = this.uncraftIn.removeStackFromSlot(0);
            if (itemstack != ItemStack.field_190927_a)
            {
                player.entityDropItem(itemstack, 0.5f);
            }

            itemstack = this.calculInput.getStackInSlot(0);
            if (itemstack != ItemStack.field_190927_a)
            {
                player.entityDropItem(itemstack, 0.5f);
            }
            for (int i = 0; i < uncraftOut.getSizeInventory(); i++ )
            {
                itemstack = this.uncraftOut.removeStackFromSlot(i);

                if (itemstack != ItemStack.field_190927_a)
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
        ItemStack itemstack = ItemStack.field_190927_a;
        Slot slot = (Slot)this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack())
            if (slot.inventory.equals(calculInput))
            {
                ItemStack itemstack1 = slot.getStack();

//              slot.onPickupFromSlot(player, itemstack1);
                slot.onSlotChanged(); // in 1.9 / 1.10, onPickupFromSlot just called onSlotChanged with no parameters
                
                if (!playerInventory.addItemStackToInventory(itemstack1))
                {
                    return ItemStack.field_190927_a;
                }
                slot.putStack(ItemStack.field_190927_a);
            }
            else if (slot.inventory.equals(uncraftIn))
            {
                if (slot.getHasStack())
                {
                    if (!playerInventory.addItemStackToInventory(slot.getStack()))
                    {
                        return ItemStack.field_190927_a;
                    }
                    slot.putStack(ItemStack.field_190927_a);
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
                    if (!calcInput.getStack().func_190926_b())
                    {
                        calcInput.putStack(slot.getStack());
                        calcInput.onSlotChanged();
                        slot.putStack(ItemStack.field_190927_a);
                    }
                    else
                    {
                        if (slot.getStack().func_190926_b())
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
                            return ItemStack.field_190927_a;
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
                        return ItemStack.field_190927_a;
                    }
                    slot.putStack(ItemStack.field_190927_a);
                    slot.onSlotChanged();
                }
            }
        return ItemStack.field_190927_a;
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
