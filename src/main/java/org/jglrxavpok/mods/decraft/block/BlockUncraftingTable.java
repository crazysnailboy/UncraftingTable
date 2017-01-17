package org.jglrxavpok.mods.decraft.block;

import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.common.network.ModGuiHandler;
import org.jglrxavpok.mods.decraft.stats.ModAchievementList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;


public class BlockUncraftingTable extends Block
{

	public BlockUncraftingTable()
    {
        super(Material.rock);
        setUnlocalizedName("uncrafting_table");
        setHardness(3.5F);
        setStepSound(soundTypePiston);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

	
	@Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {	
        if (!worldIn.isRemote)
        {
            playerIn.openGui(ModUncrafting.instance, ModGuiHandler.GUI_TABLE, worldIn, pos.getX(), pos.getY(), pos.getZ());
    		checkForPorteManteau(playerIn, worldIn, pos);
        }
        return true;
    }
	


   
	private void checkForPorteManteau(EntityPlayer playerIn, World worldIn, BlockPos pos)
	{
		boolean furnace = false;
		boolean chest = false;
		boolean workbench = false;
		
		// if the block beneath is a fence...
		if (worldIn.getBlockState(pos.down()).getBlock() instanceof net.minecraft.block.BlockFence)
		{
			Block blockEast = worldIn.getBlockState(pos.east()).getBlock();
			Block blockWest = worldIn.getBlockState(pos.west()).getBlock();
			Block blockNorth = worldIn.getBlockState(pos.north()).getBlock();
			Block blockSouth = worldIn.getBlockState(pos.south()).getBlock();
			
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
				playerIn.triggerAchievement(ModAchievementList.porteManteau);
			}
		}
	}

}
