package org.jglrxavpok.mods.decraft;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.stats.ModAchievements;
public class BlockUncraftingTable extends Block
{

    private Object redstonedBlockIcon;
	private Object topBlock;
	private Object front;
	private Object redstonedFront;
	private Object bottom;

	public BlockUncraftingTable()
    {
        super(Material.rock);
        setUnlocalizedName("uncrafting_table");
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

	
	@Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {	
//        if (!worldIn.isRemote)
//        {
//        }
        playerIn.openGui(ModUncrafting.instance, ModGuiHandler.GUI_TABLE, worldIn, pos.getX(), pos.getY(), pos.getZ());
		checkForPorteManteau(playerIn, worldIn, pos);
        return true;
    }
	


   
	private void checkForPorteManteau(EntityPlayer playerIn, World worldIn, BlockPos pos)
	{
		boolean furnace = false;
		boolean chest = false;
		boolean workbench = false;
		
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
				blockNorth == Blocks.chest || 
				blockSouth == Blocks.chest || 
				blockEast == Blocks.chest || 
				blockWest == Blocks.chest
			)
			{
				chest = true;
			}
			
			// check if one of the adjacent blocks is a crafting table
			if (
				blockNorth == Blocks.crafting_table || 
				blockSouth == Blocks.crafting_table || 
				blockEast == Blocks.crafting_table || 
				blockWest == Blocks.crafting_table
			)
			{
				workbench = true;
			}
			
			// if the block is adjacent to all three, trigger the achievement
			if ((furnace) && (chest) && (workbench)) 
			{
				playerIn.triggerAchievement(ModAchievements.porteManteauAchievement);
			}
		}
	}

}
