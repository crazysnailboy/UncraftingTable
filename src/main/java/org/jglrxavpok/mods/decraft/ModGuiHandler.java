package org.jglrxavpok.mods.decraft;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;

import net.minecraftforge.fml.common.network.IGuiHandler;

/**
 * @author jglrxavpok
 */
public class ModGuiHandler implements IGuiHandler
{
	
	public static final int GUI_TABLE = 0;

    /**
     * Returns a Server side Container to be displayed to the user.
     */
	@Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        if (world.getBlockState(new BlockPos(x, y, z)).getBlock() == ModUncrafting.instance.uncraftingTable)
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
        if (world.getBlockState(new BlockPos(x, y, z)).getBlock() == ModUncrafting.instance.uncraftingTable)
        {
            if (id == GUI_TABLE)
            {
                String name = I18n.format("tile.uncrafting_table.name");
                return new GuiUncraftingTable(player.inventory, world, name, false);
            }
        }
        return null;
    }

}
