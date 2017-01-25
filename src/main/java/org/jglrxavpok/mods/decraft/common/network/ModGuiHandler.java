package org.jglrxavpok.mods.decraft.common.network;

import org.jglrxavpok.mods.decraft.block.BlockUncraftingTable;
import org.jglrxavpok.mods.decraft.client.gui.inventory.GuiUncraftingTable;
import org.jglrxavpok.mods.decraft.inventory.ContainerUncraftingTable;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;


public class ModGuiHandler implements IGuiHandler
{

	public static final int GUI_TABLE = 0;

	/**
	 * Returns a Server side Container to be displayed to the user.
	 */
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		if (id == GUI_TABLE && world.getBlock(x, y, z) instanceof BlockUncraftingTable)
		{
			return new ContainerUncraftingTable(player.inventory, world);
		}
		return null;
	}

	/**
	 * Returns a Container to be displayed to the user. On the client side, this
	 * needs to return a instance of GuiScreen On the server side, this needs to
	 * return a instance of Container
	 */
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		if (id == GUI_TABLE && world.getBlock(x, y, z) instanceof BlockUncraftingTable)
		{
			return new GuiUncraftingTable(player.inventory, world);
		}
		return null;
	}

}
