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

    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        if (world.getBlock(x,y,z) == ModUncrafting.instance.uncraftingTable)
        {
            if (id == 0)
            {
                ContainerUncraftingTable c = new ContainerUncraftingTable(player.inventory, world, /*world.getBlockMetadata(x, y, z) == 1*/ true, x, y, z, ModUncrafting.standardLevel, ModUncrafting.maxUsedLevel);
                lastServerContainer = c;
                return c;
            }
            else if (id == 1)
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
            if (id == 0)
            {
                String name = I18n.format("tile.uncrafting_table.name");
                return new GuiUncraftingTable(player.inventory, world, name, /*world.getBlockMetadata(x, y, z) == 1*/ false, x, y, z, ModUncrafting.instance.minLvlServer, ModUncrafting.instance.maxLvlServer);
            }
            /*else if (id == 1)
            {
                return new GuiUncraftOptions();
            }*/
        }
        return null;
    }

}
