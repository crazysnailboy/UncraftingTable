package org.jglrxavpok.mods.decraft.block;

import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.common.network.ModGuiHandler;
import org.jglrxavpok.mods.decraft.stats.ModAchievementList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockUncraftingTable extends Block
{

    @SideOnly(Side.CLIENT)
    private IIcon topBlock;
    
    @SideOnly(Side.CLIENT)
    private IIcon front;
    private IIcon bottom;
    private IIcon blockIcon;
	

	public BlockUncraftingTable()
    {
        super(Material.rock);
        setBlockName("uncrafting_table");
        setHardness(3.5F);
        setStepSound(soundTypePiston);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

	@Override
	public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ)
    {
        if (!worldIn.isRemote)
        {
        	player.openGui(ModUncrafting.instance, ModGuiHandler.GUI_TABLE, worldIn, x, y, z);
    		checkForPorteManteau(player, worldIn, x, y, z);
        }
        return true;
    }

   
	private void checkForPorteManteau(EntityPlayer player, World worldIn, int x, int y, int z)
	{
		boolean furnace = false;
		boolean chest = false;
		boolean workbench = false;
		
		// if the block beneath is a fence...
		if (worldIn.getBlock(x, y - 1, z) == Blocks.fence)
		{
			
			Block blockEast = worldIn.getBlock(x + 1, y, z);
			Block blockWest = worldIn.getBlock(x - 1, y, z);
			Block blockNorth = worldIn.getBlock(x, y, z - 1);
			Block blockSouth = worldIn.getBlock(x, y, z + 1);
			
			// check if one of the adjacent blocks is a furnace
			if (
				(blockNorth == Blocks.furnace || blockNorth == Blocks.lit_furnace) ||
				(blockSouth == Blocks.furnace || blockSouth == Blocks.lit_furnace) ||
				(blockEast == Blocks.furnace || blockEast == Blocks.lit_furnace) ||
				(blockWest == Blocks.furnace || blockWest == Blocks.lit_furnace)
			) 
			{
				furnace = true;
			}
			
			// check if one of the adjacent blocks is a chest
			if (
				(blockNorth == Blocks.chest || blockNorth == Blocks.trapped_chest || blockNorth == Blocks.ender_chest) ||
				(blockSouth == Blocks.chest || blockSouth == Blocks.trapped_chest || blockSouth == Blocks.ender_chest) || 
				(blockEast == Blocks.chest || blockEast == Blocks.trapped_chest || blockEast == Blocks.ender_chest) ||
				(blockWest == Blocks.chest || blockWest == Blocks.trapped_chest || blockWest == Blocks.ender_chest)
			) 
			{
				chest = true;
			}
			
			// check if one of the adjacent blocks is a crafting table
			if (
				(blockNorth == Blocks.crafting_table) || 
				(blockSouth == Blocks.crafting_table) || 
				(blockEast == Blocks.crafting_table) || 
				(blockWest == Blocks.crafting_table)
			) 
			{
				workbench = true;
			}
			
			// if the block is adjacent to all three, trigger the achievement
			if (furnace && chest && workbench) 
			{
				player.triggerAchievement(ModAchievementList.porteManteau);
			}
		}
	}
	  
    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
    	return (IIcon) (side == 1 ? this.topBlock : (side == 0 ? bottom : (side != 3 && side != 1 ? this.blockIcon : this.front)));
    }
    
	@SideOnly(Side.CLIENT)
	@Override
    public void registerBlockIcons(IIconRegister reg)
    {
        this.blockIcon = reg.registerIcon("uncraftingTable:uncrafting_side");
        this.topBlock = reg.registerIcon("uncraftingTable:uncrafting_top");
        this.front = reg.registerIcon("uncraftingTable:uncrafting_front");
        this.bottom = reg.registerIcon("uncraftingTable:uncrafting_bottom");
    }

}
