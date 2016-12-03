package org.jglrxavpok.mods.decraft;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

/**
 * @author jglrxavpok
 */
public class UnGuiHandler implements IGuiHandler
{
    private ContainerUncraftingTable lastServerContainer;
	
	public static final int GUI_TABLE = 0;
	public static final int GUI_OPTIONS = 1;

    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        if (world.getBlock(x,y,z) == ModUncrafting.instance.uncraftingTable)
        {
            if (id == GUI_TABLE)
            {
                ContainerUncraftingTable c = new ContainerUncraftingTable(player.inventory, world, /*world.getBlockMetadata(x, y, z) == 1*/ true, x, y, z, ModUncrafting.standardLevel, ModUncrafting.maxUsedLevel);
                lastServerContainer = c;
                return c;
            }
            if (id == GUI_OPTIONS)
            {
                if (lastServerContainer != null)
                {
                    lastServerContainer.onContainerClosed(player);
                    lastServerContainer = null;
                }
            }
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        if (world.getBlock(x,y,z) == ModUncrafting.instance.uncraftingTable)
        {
            if (id == GUI_TABLE)
            {
                String name = I18n.format("tile.uncrafting_table.name");
                return new GuiUncraftingTable(player.inventory, world, name, /*world.getBlockMetadata(x, y, z) == 1*/ false, x, y, z, ModUncrafting.instance.minLvlServer, ModUncrafting.instance.maxLvlServer);
            }
            if (id == GUI_OPTIONS)
            {
            	return new GuiUncraftOptions();
        	}
        }
        return null;
    }

}
