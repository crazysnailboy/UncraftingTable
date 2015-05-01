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

    public static enum State
    {
        ERROR, READY
    }

    public InventoryCrafting      uncraftIn   = new InventoryCrafting(this, 1, 1);
    public InventoryUncraftResult uncraftOut  = new InventoryUncraftResult();
    public InventoryCrafting      calculInput = new InventoryCrafting(this, 1, 1);
    private World                 worldObj;
    public InventoryPlayer        playerInv;
    public String                 result      = I18n.format("uncrafting.result.ready");
    public State                  type        = State.READY;
    public int                    xp          = -ModUncrafting.standardLevel;
    public int                    x           = 0;
    public int                    y           = 0;
    public int                    z           = 0;
    private int                   minLvl;
    private int                   maxLvl;
    private ItemStack             toReturn;

    public ContainerUncraftingTable(InventoryPlayer par1PlayerInventory, World world, boolean inverted, int x, int y, int z, int minLvl, int maxLvl)
    {
        this.minLvl = minLvl;
        this.maxLvl = maxLvl;
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldObj = world;
        int l;
        int i1;
        int i2;
        if(!inverted)
        {
            for(l = 0; l < 3; ++l)
            {
                for(i1 = 0; i1 < 3; ++i1)
                {
                    this.addSlotToContainer(new Slot(this.uncraftOut, i1 + l * 3, 112 + i1 * 18, 17 + l * 18));
                }
            }
            this.addSlotToContainer(new Slot(this.uncraftIn, 0, 30 + 15, 35));
            this.addSlotToContainer(new Slot(this.calculInput, 0, 15, 35));

            for(l = 0; l < 3; ++l)
            {
                for(i1 = 0; i1 < 9; ++i1)
                {
                    this.addSlotToContainer(new Slot(par1PlayerInventory, i1 + l * 9 + 9, 8 + i1 * 18, 84 + l * 18));
                }
            }
            for(l = 0; l < 9; ++l)
            {
                this.addSlotToContainer(new Slot(par1PlayerInventory, l, 8 + l * 18, 142));
            }
        }
        else
        {
            int height = 166 - 16;
            for(l = 0; l < 3; ++l)
            {
                for(i1 = 0; i1 < 3; ++i1)
                {
                    this.addSlotToContainer(new Slot(this.uncraftOut, i1 + l * 3, 112 + i1 * 18, height - (17 + l * 18)));
                }
            }

            this.addSlotToContainer(new Slot(this.uncraftIn, 0, 30 + 15, height - 35));
            this.addSlotToContainer(new Slot(this.calculInput, 0, 15, height - 35));

            for(l = 0; l < 3; ++l)
            {
                for(i1 = 0; i1 < 9; ++i1)
                {
                    this.addSlotToContainer(new Slot(par1PlayerInventory, i1 + l * 9 + 9, 8 + i1 * 18, height - (84 + l * 18)));
                }
            }

            for(l = 0; l < 9; ++l)
            {
                this.addSlotToContainer(new Slot(par1PlayerInventory, l, 8 + l * 18, height - 142));
            }
        }
        playerInv = par1PlayerInventory;
    }

    /**
     * Short story: fires a UncraftingEvent instance and look if the uncrafting is possible.
     * If possible, tries to do the uncrafting and fires a SuccessedUncraftingEvent if managed to do it.
     */
    @SuppressWarnings("rawtypes")
    public void onCraftMatrixChanged(IInventory inventory)
    {
        toReturn = null;
        if(inventory == calculInput)
        {
            if(calculInput.getStackInSlot(0) == null)
            {
                xp = 0;
                if(uncraftIn.getStackInSlot(0) == null)
                {
                    String r = I18n.format("uncrafting.result.ready");
                    result = r;
                    type = State.READY;
                    xp = -ModUncrafting.standardLevel;
                }
                return;
            }
            else if(uncraftIn.getStackInSlot(0) == null)
            {
                List<ItemStack[]> list1 = UncraftingManager.getUncraftResults(calculInput.getStackInSlot(0));
                ItemStack[] output = null;
                if(list1.size() > 0)
                    output = list1.get(0);
                List<Integer> needs = UncraftingManager.getStackSizeNeeded(calculInput.getStackInSlot(0));
                int required = 1;
                if(needs.size() > 0)
                {
                    required = needs.get(0);
                }
                UncraftingEvent event = new UncraftingEvent(calculInput.getStackInSlot(0), output, required, playerInv.player);
                if(!MinecraftForge.EVENT_BUS.post(event))
                {
                    int nbrStacks = event.getRequiredNumber();
                    if(nbrStacks > calculInput.getStackInSlot(0).stackSize)
                    {

                        String r = I18n.format("uncrafting.result.needMoreStacks", (nbrStacks - calculInput.getStackInSlot(0).stackSize));
                        result = r;
                        type = State.ERROR;
                        xp = -minLvl;
                        return;
                    }
                    else if(event.getOutput() == null)
                    {
                        String r = I18n.format("uncrafting.result.impossible");
                        result = r;
                        type = State.ERROR;
                        xp = -minLvl;
                        return;
                    }
                    else
                    {
                        String r = I18n.format("uncrafting.result.ready");
                        result = r;
                        type = State.READY;
                    }
                    if(ModUncrafting.instance.uncraftMethod == 0)
                    {
                        xp = 0;
                    }
                    else if(ModUncrafting.instance.uncraftMethod == 1)
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
                result = r;
                type = State.ERROR;
                xp = -minLvl;
                return;
            }
        }
        else if(inventory == uncraftIn)
        {
            xp = 0;
            if(uncraftIn.getStackInSlot(0) == null)
            {
                result = I18n.format("uncrafting.result.ready");
                if(calculInput.getStackInSlot(0) == null)
                {
                    xp = 0;
                }
                type = State.READY;
                return;
            }
            List<ItemStack[]> list1 = UncraftingManager.getUncraftResults(uncraftIn.getStackInSlot(0));
            ItemStack[] output = null;
            if(list1.size() > 0)
                output = list1.get(0);
            List<Integer> needs = UncraftingManager.getStackSizeNeeded(uncraftIn.getStackInSlot(0));
            int required = 1;
            if(needs.size() > 0)
            {
                required = needs.get(0);
            }
            UncraftingEvent event = new UncraftingEvent(uncraftIn.getStackInSlot(0), output, required, playerInv.player);
            if(!MinecraftForge.EVENT_BUS.post(event))
            {
                int nbrStacks = event.getRequiredNumber();
                if(nbrStacks > uncraftIn.getStackInSlot(0).stackSize)
                {
                    String r = I18n.format("uncrafting.result.needMoreStacks", (nbrStacks - uncraftIn.getStackInSlot(0).stackSize));
                    result = r;
                    type = State.ERROR;
                    return;
                }
                while(uncraftIn.getStackInSlot(0) != null && nbrStacks <= uncraftIn.getStackInSlot(0).stackSize)
                {
                    EntityPlayer player = playerInv.player;
                    int lvl = player.experienceLevel;
                    xp = 0;
                    if(!EnchantmentHelper.getEnchantments(uncraftIn.getStackInSlot(0)).isEmpty() && calculInput.getStackInSlot(0) != null && calculInput.getStackInSlot(0).getItem() == Items.book)
                    {
                        Map enchantsMap = EnchantmentHelper.getEnchantments(uncraftIn.getStackInSlot(0));
                        Iterator<?> i = enchantsMap.keySet().iterator();
                        Map<Integer, Integer> tmpMap = new LinkedHashMap<Integer, Integer>();
                        ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
                        while(i.hasNext())
                        {
                            int id = (Integer) i.next();
                            tmpMap.put(id, (Integer) enchantsMap.get(id));
                            ItemStack stack = new ItemStack(Items.enchanted_book, 1);
                            EnchantmentHelper.setEnchantments(tmpMap, stack);
                            stacks.add(stack);
                            tmpMap.clear();
                        }
                        int nbr = calculInput.getStackInSlot(0).stackSize;
                        for(ItemStack s : stacks)
                        {
                            nbr-- ;
                            if(!playerInv.addItemStackToInventory(s))
                            {
                                EntityItem e = playerInv.player.entityDropItem(s, 0.5f);
                                e.posX = playerInv.player.posX;
                                e.posY = playerInv.player.posY;
                                e.posZ = playerInv.player.posZ;
                            }
                            if(nbr <= 0)
                            {
                                break;
                            }
                        }
                        calculInput.setInventorySlotContents(0, null);
                    }
                    ItemStack[] items = event.getOutput();
                    if(items == null)
                    {
                        String r = I18n.format("uncrafting.result.impossible");
                        result = r;
                        type = State.ERROR;
                        return;
                    }
                    if(!playerInv.player.capabilities.isCreativeMode && uncraftIn.getStackInSlot(0).getItem().getItemEnchantability() > 0)
                    {
                        if(ModUncrafting.instance.uncraftMethod == 0)
                        {
                            int count = 0;
                            ItemStack s1 = uncraftIn.getStackInSlot(0);

                            int percent = (int) (((double) s1.getItemDamage() / (double) s1.getMaxDamage()) * 100);
                            for(int i = 0; i < items.length; i++ )
                            {
                                if(items[i] != null)
                                    count++ ;
                            }
                            int toRemove = Math.round((float) (percent * count) / 100f);
                            if(toRemove > 0)
                                for(int i = 0; i < items.length; i++ )
                                {
                                    if(items[i] != null)
                                    {
                                        toRemove-- ;
                                        items[i] = null;
                                        if(toRemove <= 0)
                                        {
                                            break;
                                        }
                                    }
                                }
                        }
                        else if(ModUncrafting.instance.uncraftMethod == 1)
                        {
                            ItemStack s1 = uncraftIn.getStackInSlot(0);
                            int percent = (int) (((double) s1.getItemDamage() / (double) s1.getMaxDamage()) * 100);
                            xp = (maxLvl * percent) / 100;
                        }
                    }
                    if(lvl < ModUncrafting.standardLevel + xp && !player.capabilities.isCreativeMode)
                    {
                        String r = I18n.format("uncrafting.result.needMoreXP");
                        result = r;
                        type = State.ERROR;
                        return;
                    }
                    else if(lvl >= ModUncrafting.standardLevel + xp && !player.capabilities.isCreativeMode)
                    {
                        player.experienceLevel -= ModUncrafting.standardLevel + xp;
                    }
                    if(!uncraftOut.isEmpty())
                    {
                        for(int i = 0; i < uncraftOut.getSizeInventory(); i++ )
                        {
                            ItemStack item = uncraftOut.getStackInSlot(i);
                            if((item != null && items[i] != null && item.getItem() != items[i].getItem()))
                            {
                                if(!playerInv.addItemStackToInventory(item))
                                {
                                    EntityItem e = playerInv.player.entityDropItem(item, 0.5f);
                                    e.posX = playerInv.player.posX;
                                    e.posY = playerInv.player.posY;
                                    e.posZ = playerInv.player.posZ;
                                }
                                uncraftOut.setInventorySlotContents(i, null);
                            }
                        }
                    }

                    for(int i = 0; i < items.length; i++ )
                    {
                        ItemStack s = items[i];
                        ItemStack currentStack = uncraftOut.getStackInSlot(i);
                        if(s != null)
                        {
                            int metadata = s.getItemDamage();
                            if(metadata == 32767)
                            {
                                metadata = 0;
                            }
                            ItemStack newStack = null;
                            if(currentStack != null && 1 + currentStack.stackSize <= s.getMaxStackSize())
                            {
                                newStack = new ItemStack(s.getItem(), 1 + currentStack.stackSize, metadata);
                            }
                            else
                            {
                                if(currentStack != null && !playerInv.addItemStackToInventory(currentStack))
                                {
                                    EntityItem e = playerInv.player.entityDropItem(currentStack, 0.5f);
                                    e.posX = playerInv.player.posX;
                                    e.posY = playerInv.player.posY;
                                    e.posZ = playerInv.player.posZ;
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
                    //    				if(n > 0)
                    //    				{
                    //    					ItemStack newStack = new ItemStack(stack.getItem(), n, stack.getItemDamageForDisplay());
                    //    //					toReturn = newStack;
                    //    					if(!playerInv.addItemStackToInventory(newStack))
                    //    					{
                    //    						EntityItem e = playerInv.player.entityDropItem(newStack,0.5f);
                    //    						e.posX = playerInv.player.posX;
                    //    						e.posY = playerInv.player.posY;
                    //    						e.posZ = playerInv.player.posZ;
                    //    					}
                    //    				}
                    SuccessedUncraftingEvent sevent = new SuccessedUncraftingEvent(uncraftIn.getStackInSlot(0), items, event.getRequiredNumber(), playerInv.player);
                    if(!MinecraftForge.EVENT_BUS.post(sevent))
                    {
                        event.getPlayer().addStat(ModUncrafting.instance.uncraftedItemsStat, event.getRequiredNumber());
                        event.getPlayer().triggerAchievement(ModUncrafting.instance.uncraftAny);
                    }
                    int i = uncraftIn.getStackInSlot(0).stackSize - event.getRequiredNumber();
                    ItemStack newStack = null;
                    if(i > 0)
                    {
                        newStack = new ItemStack(uncraftIn.getStackInSlot(0).getItem(), i, 0);
                    }
                    uncraftIn.setInventorySlotContents(0, newStack);
                    this.onCraftMatrixChanged(calculInput);
                }
            }
            else
            {
                String r = I18n.format("uncrafting.result.impossible");
                result = r;
                type = State.ERROR;
            }
        }
        else
        {
            String r = I18n.format("uncrafting.result.impossible");
            result = r;
            type = State.ERROR;
        }
    }

    public ItemStack slotClick(int par1, int par2, int par3, EntityPlayer player)
    {
        ItemStack r = super.slotClick(par1, par2, par3, player);
        if(inventorySlots.size() > par1 && par1 >= 0)
        {
            if(inventorySlots.get(par1) != null)
            {
                if((((Slot) inventorySlots.get(par1)).inventory == calculInput || ((Slot) inventorySlots.get(par1)).inventory == playerInv))
                    this.onCraftMatrixChanged(calculInput);
                else if(((Slot) inventorySlots.get(par1)).inventory == uncraftIn)
                {
                    this.onCraftMatrixChanged(uncraftIn);
                }
            }
        }
        return r;
    }

    /**
     * Callback for when the crafting gui is closed.
     */
    public void onContainerClosed(EntityPlayer par1EntityPlayer)
    {
        if(playerInv.getItemStack() != null)
        {
            par1EntityPlayer.entityDropItem(playerInv.getItemStack(), 0.5f);
        }
        if(!this.worldObj.isRemote)
        {
            ItemStack itemstack = this.uncraftIn.getStackInSlotOnClosing(0);
            if(itemstack != null)
            {
                par1EntityPlayer.entityDropItem(itemstack, 0.5f);
            }

            itemstack = this.calculInput.getStackInSlotOnClosing(0);
            if(itemstack != null)
            {
                par1EntityPlayer.entityDropItem(itemstack, 0.5f);
            }
            for(int i = 0; i < uncraftOut.getSizeInventory(); i++ )
            {
                itemstack = this.uncraftOut.getStackInSlotOnClosing(i);

                if(itemstack != null)
                {
                    par1EntityPlayer.entityDropItem(itemstack, 0.5f);
                }
            }
        }
    }

    public boolean canInteractWith(EntityPlayer player)
    {
        return true;
    }

    /**
     * Called when a player shift-clicks on a slot.
     */
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(par2);
        if(slot != null && slot.getHasStack())
            if(slot.inventory.equals(calculInput))
            {
                ItemStack itemstack1 = slot.getStack();
                slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
                if(!playerInv.addItemStackToInventory(itemstack1))
                {
                    return null;
                }
                slot.putStack(null);
            }
            else if(slot.inventory.equals(uncraftIn))
            {
                if(slot.getHasStack())
                {
                    if(!playerInv.addItemStackToInventory(slot.getStack()))
                    {
                        return null;
                    }
                    slot.putStack(null);
                    slot.onSlotChanged();
                }
            }
            else if(slot.inventory.equals(playerInv))
            {
                Slot calcInput = null;
                Slot uncraftSlot = null;
                for(Object s : inventorySlots)
                {
                    Slot s1 = (Slot) s;
                    if(s1.inventory.equals(calculInput))
                    {
                        calcInput = s1;
                    }
                    else if(s1.inventory.equals(uncraftIn))
                    {
                        uncraftSlot = s1;
                    }
                }
                if(calcInput != null)
                {
                    if(calcInput.getStack() == null)
                    {
                        calcInput.putStack(slot.getStack());
                        calcInput.onSlotChanged();
                        slot.putStack(null);
                    }
                    else
                    {
                        if(slot.getStack() != null)
                        {
                            ItemStack i = slot.getStack();
                            slot.onPickupFromSlot(par1EntityPlayer, slot.getStack());
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
            else if(slot.inventory.equals(uncraftOut))
            {
                if(slot.getHasStack())
                {
                    if(!playerInv.addItemStackToInventory(slot.getStack()))
                    {
                        return null;
                    }
                    slot.putStack(null);
                    slot.onSlotChanged();
                }
            }
        return null;
    }

    public boolean func_94530_a(ItemStack par1ItemStack, Slot par2Slot)
    {
        return !par2Slot.inventory.equals(uncraftOut);
    }

    public Slot getSlot(int par1)
    {
        if(par1 >= this.inventorySlots.size())
            par1 = this.inventorySlots.size() - 1;
        return super.getSlot(par1);
    }

}
