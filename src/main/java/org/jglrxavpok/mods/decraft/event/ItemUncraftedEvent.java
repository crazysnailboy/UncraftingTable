package org.jglrxavpok.mods.decraft.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;


public class ItemUncraftedEvent extends Event
{

	public final EntityPlayer player;
	public final ItemStack stack;
	public final int quantity;

	public ItemUncraftedEvent(EntityPlayer player, ItemStack stack, int quantity)
	{
		this.player = player;
		this.stack = stack;
		this.quantity = quantity;
	}
	
}
