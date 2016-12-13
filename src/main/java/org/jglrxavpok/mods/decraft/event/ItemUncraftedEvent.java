package org.jglrxavpok.mods.decraft.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * 
 * @author jglrxavpok
 *
 */
public class ItemUncraftedEvent extends Event
{

	/**
	 * The player uncrafting the item
	 */
	public final EntityPlayer player;
	
	/**
	 * Uncrafted item
	 */
	public final ItemStack uncrafted;
	
	/**
	 * Output of the uncrafting
	 */
	private NonNullList<ItemStack>	out;
	/**
	 * Number of items required
	 */
	private int	nbr;
	/**
	 * When the event was fired (more like created, actually)
	 */
	private long when;

	public ItemUncraftedEvent(EntityPlayer player, ItemStack stack, NonNullList<ItemStack> output, int required)
	{
		this.player = player;
		uncrafted = stack;
		out = output;
		nbr = required;
		when = System.currentTimeMillis();
	}
	
	/**
	 * When the event was fired (more like created, actually)
	 */
	public long getWhen()
	{
		return when;
	}
	
	/**
	 * The player uncrafting the item
	 */
	public EntityPlayer getPlayer()
	{
		return player;
	}
	
	/**
	 * The number of required items in order to do the uncrafting
	 * @return
	 */
	public int getRequiredNumber()
	{
		return nbr;
	}
	
	public boolean isCancelable()
	{
		return true;
	}
	
	/**
	 * The uncrafted item 
	 */
	public ItemStack getUncrafted()
	{
		return uncrafted;
	}
	
	/**
	 * The output of the uncrafting
	 */
	public NonNullList<ItemStack> getOutput()
	{
		return out;
	}
}
