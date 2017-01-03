package org.jglrxavpok.mods.decraft;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.inventory.*;
import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;
import org.jglrxavpok.mods.decraft.event.ItemUncraftedEvent;
import org.jglrxavpok.mods.decraft.event.UncraftingEvent;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
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
    private InventoryCrafting bookInput = new InventoryCrafting(this, 1, 1);
    private InventoryCrafting uncraftIn = new InventoryCrafting(this, 1, 1);
    private InventoryUncraftResult uncraftOut = new InventoryUncraftResult();
    private InventoryPlayer playerInventory;
    private World worldObj;

    private final Slot uncraftSlot;
    private final Slot bookSlot;

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
        bookSlot = new Slot(this.bookInput, 0, 15, 35);
        this.addSlotToContainer(bookSlot);

        // incrafting input inventory (right standalone slot)
        uncraftSlot = new Slot(this.uncraftIn, 0, 30 + 15, 35);
        this.addSlotToContainer(uncraftSlot);

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

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player)
    {    
        ItemStack itemStack = super.slotClick(slotId, dragType, clickTypeIn, player);
        
        if (inventorySlots.size() > slotId && slotId >= 0)
        {
            if (inventorySlots.get(slotId) != null)
            {
            	IInventory inventory = ((Slot)inventorySlots.get(slotId)).inventory;
            	
            	if (inventory == bookInput) // || inventory == playerInventory)
            	{
                    this.onCraftMatrixChanged(bookInput);
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

            itemstack = this.bookInput.getStackInSlot(0);
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
            if (slot.inventory.equals(bookInput))
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
                    if (s1.inventory.equals(bookInput))
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
                            this.onCraftMatrixChanged(bookInput);
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

    public void setOutput(NonNullList<ItemStack> output) {
        uncraftOut.clear();
        for (int i = 0; i < output.size(); i++) {
            uncraftOut.setInventorySlotContents(i, output.get(i));
        }
    }

    /**
     * Checks that the output is clear (no items remaining from previous operations)
     */
    public boolean isReadyToUncraft() {
        for (int i = 0; i < uncraftOut.getSizeInventory(); i++) {
            ItemStack s = uncraftOut.getStackInSlot(i);
            if(!s.isEmpty()) { // if not empty
                return false;
            }
        }
        return true;
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

    public Slot getUncraftSlot() {
        return uncraftSlot;
    }

    public Slot getBookSlot() {
        return bookSlot;
    }
}
