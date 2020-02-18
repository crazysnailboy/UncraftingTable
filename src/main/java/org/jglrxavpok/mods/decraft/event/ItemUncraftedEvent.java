package org.jglrxavpok.mods.decraft.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;


public class ItemUncraftedEvent extends Event
{

	public final PlayerEntity player;
	public final ItemStack stack;
	public final int quantity;

	public ItemUncraftedEvent(PlayerEntity player, ItemStack stack, int quantity)
	{
		this.player = player;
		this.stack = stack;
		this.quantity = quantity;
	}

}
