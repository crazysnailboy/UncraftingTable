package org.jglrxavpok.mods.decraft;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;

import cpw.mods.fml.common.network.IGuiHandler;

/**
 * @author jglrxavpok
 */
public class ModGuiHandler implements IGuiHandler
{
    //private ContainerUncraftingTable lastServerContainer;
	
	public static final int GUI_TABLE = 0;

    /**
     * Returns a Server side Container to be displayed to the user.
     */
	@Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        if (world.getBlock(x,y,z) == ModUncrafting.instance.uncraftingTable)
        {
            if (id == GUI_TABLE)
            {
            	return new ContainerUncraftingTable(player.inventory, world, true);
            }
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
        if (world.getBlock(x,y,z) == ModUncrafting.instance.uncraftingTable)
        {
            if (id == GUI_TABLE)
            {
                return new GuiUncraftingTable(player.inventory, world, I18n.format("tile.uncrafting_table.name"), false);
            }
        }
        return null;
    }

}
