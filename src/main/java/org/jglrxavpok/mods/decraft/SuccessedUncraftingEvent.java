package org.jglrxavpok.mods.decraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.eventhandler.Event;

/**
 * 
 * @author jglrxavpok
 *
 */
public class SuccessedUncraftingEvent extends Event
{

	/**
	 * Uncrafted item
	 */
	private ItemStack	uncrafted;
	/**
	 * Output of the uncrafting
	 */
	private ItemStack[]	out;
	/**
	 * Number of items required
	 */
	private int	nbr;
	/**
	 * The player uncrafting the item
	 */
	private EntityPlayer	p;
	/**
	 * When the event was fired (more like created, actually)
	 */
	private long	when;

	public SuccessedUncraftingEvent(ItemStack stack, ItemStack[] output, int required, EntityPlayer player)
	{
		uncrafted = stack;
		out = output;
		nbr = required;
		p = player;
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
		return p;
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
	public ItemStack[] getOutput()
	{
		return out;
	}
}
