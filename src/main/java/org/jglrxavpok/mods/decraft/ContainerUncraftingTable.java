package org.jglrxavpok.mods.decraft;

import java.util.*;

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

    public InventoryCrafting uncraftIn = new InventoryCrafting(this, 1, 1);
    public InventoryUncraftResult uncraftOut = new InventoryUncraftResult();
    public InventoryCrafting calculInput = new InventoryCrafting(this, 1, 1);
    private World worldObj;
    public InventoryPlayer playerInventory;
    
    public UncraftingStatus uncraftingStatus = UncraftingStatus.READY;
    public String uncraftingStatusText = I18n.format("uncrafting.result.ready");
    
    public int xp = 0 - ModUncrafting.standardLevel;
    public int x = 0;
    public int y = 0;
    public int z = 0;
    
    private int minLvl;
    private int maxLvl;
    
    private ItemStack toReturn;
    
    
    

    public ContainerUncraftingTable(InventoryPlayer playerInventoryIn, World worldIn, boolean inverted, int x, int y, int z, int minLvl, int maxLvl)
    {
        this.minLvl = minLvl;
        this.maxLvl = maxLvl;
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldObj = worldIn;
        int l;
        int i1;
        int i2;
        
        if (!inverted) // inverted: false if called from client, true if called from server
        {
            for (l = 0; l < 3; ++l)
            {
                for (i1 = 0; i1 < 3; ++i1)
                {
                    this.addSlotToContainer(new Slot(this.uncraftOut, i1 + l * 3, 112 + i1 * 18, 17 + l * 18));
                }
            }
            this.addSlotToContainer(new Slot(this.uncraftIn, 0, 30 + 15, 35));
            this.addSlotToContainer(new Slot(this.calculInput, 0, 15, 35));

            for (l = 0; l < 3; ++l)
            {
                for (i1 = 0; i1 < 9; ++i1)
                {
                    this.addSlotToContainer(new Slot(playerInventoryIn, i1 + l * 9 + 9, 8 + i1 * 18, 84 + l * 18));
                }
            }
            for (l = 0; l < 9; ++l)
            {
                this.addSlotToContainer(new Slot(playerInventoryIn, l, 8 + l * 18, 142));
            }
        }
        else
        {
            int height = 166 - 16;
            for (l = 0; l < 3; ++l)
            {
                for (i1 = 0; i1 < 3; ++i1)
                {
                    this.addSlotToContainer(new Slot(this.uncraftOut, i1 + l * 3, 112 + i1 * 18, height - (17 + l * 18)));
                }
            }

            this.addSlotToContainer(new Slot(this.uncraftIn, 0, 30 + 15, height - 35));
            this.addSlotToContainer(new Slot(this.calculInput, 0, 15, height - 35));

            for (l = 0; l < 3; ++l)
            {
                for (i1 = 0; i1 < 9; ++i1)
                {
                    this.addSlotToContainer(new Slot(playerInventoryIn, i1 + l * 9 + 9, 8 + i1 * 18, height - (84 + l * 18)));
                }
            }

            for (l = 0; l < 9; ++l)
            {
                this.addSlotToContainer(new Slot(playerInventoryIn, l, 8 + l * 18, height - 142));
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
    	System.out.println("onCraftMatrixChanged");
    	
        toReturn = null;
        if (inventory == calculInput)
        {
        	System.out.println("inventory == calculInput: " + (inventory == calculInput));
        	
        	
            if (calculInput.getStackInSlot(0) == null)
            {
                xp = 0;
                if (uncraftIn.getStackInSlot(0) == null)
                {
                    uncraftingStatusText = I18n.format("uncrafting.result.ready");
                    uncraftingStatus = UncraftingStatus.READY;
                    xp = -ModUncrafting.standardLevel;
                }
                return;
            }
            
            else if (uncraftIn.getStackInSlot(0) == null)
            {
                List<ItemStack[]> list1 = UncraftingManager.getUncraftResults(calculInput.getStackInSlot(0));
                ItemStack[] output = null;
                if (list1.size() > 0) output = list1.get(0);

                List<Integer> needs = UncraftingManager.getStackSizeNeeded(calculInput.getStackInSlot(0));
                int required = 1;
                if (needs.size() > 0) required = needs.get(0);
                
                UncraftingEvent event = new UncraftingEvent(calculInput.getStackInSlot(0), output, required, playerInventory.player);
                if (!MinecraftForge.EVENT_BUS.post(event))
                {
                    int nbrStacks = event.getRequiredNumber();
                    if (nbrStacks > calculInput.getStackInSlot(0).stackSize)
                    {
                        uncraftingStatus = UncraftingStatus.ERROR;
                        uncraftingStatusText = I18n.format("uncrafting.result.needMoreStacks", (nbrStacks - calculInput.getStackInSlot(0).stackSize));
                        xp = -minLvl;
                        return;
                    }
                    else if (event.getOutput() == null)
                    {
                        uncraftingStatus = UncraftingStatus.ERROR;
                        uncraftingStatusText = I18n.format("uncrafting.result.impossible");
                        xp = -minLvl;
                        return;
                    }
                    else
                    {
                        uncraftingStatus = UncraftingStatus.READY;
                        uncraftingStatusText = I18n.format("uncrafting.result.ready");
                    }
                    if (ModUncrafting.instance.uncraftMethod == 0)
                    {
                        xp = 0;
                    }
                    else if (ModUncrafting.instance.uncraftMethod == 1)
                    {
                        ItemStack s1 = calculInput.getStackInSlot(0);
                        int percent = (int) (((double) s1.getItemDamage() / (double) s1.getMaxDamage()) * 100);
                        xp = (maxLvl * percent) / 100;
                    }
                }
            }
            else
            {
                String r = I18n.format("uncrafting.result.impossible");
                uncraftingStatusText = r;
                uncraftingStatus = UncraftingStatus.ERROR;
                xp = -minLvl;
                return;
            }
        }
        
        
        else if (inventory == uncraftIn)
        {
        	System.out.println("inventory == uncraftIn: " + (inventory == uncraftIn));
        	
            xp = 0;
            if (uncraftIn.getStackInSlot(0) == null)
            {
                uncraftingStatus = UncraftingStatus.READY;
                uncraftingStatusText = I18n.format("uncrafting.result.ready");
                return;
            }
            
//        	System.out.println("onCraftMatrixChanged");
//    		System.out.println("isRemote: " + worldObj.isRemote);
//    		System.out.println("displayName: " + uncraftIn.getStackInSlot(0).getDisplayName());
//    		System.out.println("stackSize: " + uncraftIn.getStackInSlot(0).stackSize);
            
            
            List<ItemStack[]> list1 = UncraftingManager.getUncraftResults(uncraftIn.getStackInSlot(0));
            ItemStack[] output = null;
            if (list1.size() > 0) output = list1.get(0);
            
            List<Integer> needs = UncraftingManager.getStackSizeNeeded(uncraftIn.getStackInSlot(0));
            int required = 1;
            if (needs.size() > 0) required = needs.get(0);

            UncraftingEvent event = new UncraftingEvent(uncraftIn.getStackInSlot(0), output, required, playerInventory.player);
            if (!MinecraftForge.EVENT_BUS.post(event))
            {
                int nbrStacks = event.getRequiredNumber();
                if (nbrStacks > uncraftIn.getStackInSlot(0).stackSize)
                {
                    uncraftingStatus = UncraftingStatus.ERROR;
                    uncraftingStatusText = I18n.format("uncrafting.result.needMoreStacks", (nbrStacks - uncraftIn.getStackInSlot(0).stackSize));
                    return;
                }
                
                while (uncraftIn.getStackInSlot(0) != null && nbrStacks <= uncraftIn.getStackInSlot(0).stackSize)
                {
                    EntityPlayer player = playerInventory.player;
                    int lvl = player.experienceLevel;
                    xp = 0;
                    
                    if (!EnchantmentHelper.getEnchantments(uncraftIn.getStackInSlot(0)).isEmpty() && calculInput.getStackInSlot(0) != null && calculInput.getStackInSlot(0).getItem() == Items.book)
                    {
                        Map enchantsMap = EnchantmentHelper.getEnchantments(uncraftIn.getStackInSlot(0));
                        Iterator<?> i = enchantsMap.keySet().iterator();
                        Map<Integer, Integer> tmpMap = new LinkedHashMap<Integer, Integer>();
                        ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
                        while (i.hasNext())
                        {
                            int id = (Integer) i.next();
                            tmpMap.put(id, (Integer) enchantsMap.get(id));
                            ItemStack stack = new ItemStack(Items.enchanted_book, 1);
                            EnchantmentHelper.setEnchantments(tmpMap, stack);
                            stacks.add(stack);
                            tmpMap.clear();
                        }
                        int nbr = calculInput.getStackInSlot(0).stackSize;
                        for (ItemStack s : stacks)
                        {
                            nbr-- ;
                            if (!playerInventory.addItemStackToInventory(s))
                            {
                                EntityItem e = playerInventory.player.entityDropItem(s, 0.5f);
                                e.posX = playerInventory.player.posX;
                                e.posY = playerInventory.player.posY;
                                e.posZ = playerInventory.player.posZ;
                            }
                            if (nbr <= 0)
                            {
                                break;
                            }
                        }
                        calculInput.setInventorySlotContents(0, null);
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
                        if (ModUncrafting.instance.uncraftMethod == 0)
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
                        else if (ModUncrafting.instance.uncraftMethod == 1)
                        {
                            ItemStack s1 = uncraftIn.getStackInSlot(0);
                            int percent = (int) (((double) s1.getItemDamage() / (double) s1.getMaxDamage()) * 100);
                            xp = (maxLvl * percent) / 100;
                        }
                    }
                    if (lvl < ModUncrafting.standardLevel + xp && !player.capabilities.isCreativeMode)
                    {
                        String r = I18n.format("uncrafting.result.needMoreXP");
                        uncraftingStatusText = r;
                        uncraftingStatus = UncraftingStatus.ERROR;
                        return;
                    }
                    else if (lvl >= ModUncrafting.standardLevel + xp && !player.capabilities.isCreativeMode)
                    {
                        player.experienceLevel -= ModUncrafting.standardLevel + xp;
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
                    
                    System.out.println("items.length: " + items.length);
                    

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
                    SuccessedUncraftingEvent sevent = new SuccessedUncraftingEvent(uncraftIn.getStackInSlot(0), items, event.getRequiredNumber(), playerInventory.player);
                    if (!MinecraftForge.EVENT_BUS.post(sevent))
                    {
                        event.getPlayer().addStat(ModUncrafting.instance.uncraftedItemsStat, event.getRequiredNumber());
                        event.getPlayer().triggerAchievement(ModUncrafting.instance.uncraftAny);
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

    /**
     * Called when the container is closed.
     */
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
