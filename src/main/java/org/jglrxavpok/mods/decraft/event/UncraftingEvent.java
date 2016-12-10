package org.jglrxavpok.mods.decraft.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.eventhandler.Event;

/**
 * This event is used to change the output/number of required items or even cancel an "uncrafting recipe".
 * @author jglrxavpok
 *
 */
public class UncraftingEvent extends Event
{

	/**
	 * Uncrafted item
	 */
	private ItemStack	item;
	
	/**
	 * When the event was fired (actually, it's created)
	 */
	private long	when;
	
	/**
	 * The output of the recipe
	 */
	private ItemStack[]	out;
	
	/**
	 * Number of required items in order to uncraft the item.
	 */
	private int	required;
	
	/**
	 * The player uncrafting the item
	 */
	private EntityPlayer	p;

	/**
	 * 
	 * @param uncrafted : Uncrafted item. Shouldn't be null.
	 * @param output : Output of the uncrafting. It's usually null when the given stack has not an uncrafting recipe.
	 * @param required : The number of items required in order to do this uncrafting.
	 * @param player : The player uncrafting the item.
	 */
	public UncraftingEvent(ItemStack uncrafted, ItemStack[] output, int required, EntityPlayer player)
	{
		item = uncrafted;
		when = System.currentTimeMillis();
		this.required = required;
		this.out = output;
		this.p = player;
	}
	
	/**
	 * Returns the output of the recipe. May be null if the item can't be uncrafted
	 */
	public ItemStack[] getOutput()
	{
		return out;
	}
	
	/**
	 * Return the player uncrafting the item.
	 */
	public EntityPlayer getPlayer()
	{
		return p;
	}
	
	/**
	 * Change the output of the uncrafting.
	 * @param out : May be null in order to cancel the event. <code>setCanceled(true);</code> is better in this case.
	 */
	public void setOutput(ItemStack[] out)
	{
		this.out = out;
	}

	/**
	 * Returns hen the event was fired (actually, it's created)
	 */
	public long getWhen()
	{
		return when;
	}
	
	/**
	 * Returns the uncrafted stack
	 */
	public ItemStack getUncraftedStack()
	{
		return item;
	}
	
	public boolean isCancelable()
	{
		return true;
	}

	/**
	 * Returns the required amount of items in order to do the uncrafting.
	 */
	public int getRequiredNumber()
	{
		return required;
	}

	/**
	 * Set the required amount of items in order to do this uncrafting.
	 */
	public void setRequiredNumber(int required)
	{
		this.required = required;
	}
}
