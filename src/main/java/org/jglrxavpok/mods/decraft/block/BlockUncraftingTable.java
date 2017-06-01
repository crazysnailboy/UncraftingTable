package org.jglrxavpok.mods.decraft.block;

import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.common.network.ModGuiHandler;
import org.jglrxavpok.mods.decraft.stats.ModAchievementList;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class BlockUncraftingTable extends Block
{

	public BlockUncraftingTable()
	{
		super(Material.ROCK);
		setRegistryName("uncrafting_table");
		setUnlocalizedName("uncrafting_table");
		setHardness(3.5F);
		setSoundType(SoundType.STONE);
		this.setCreativeTab(CreativeTabs.DECORATIONS);
	}


	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
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
				(blockNorth == Blocks.FURNACE || blockNorth == Blocks.LIT_FURNACE) ||
				(blockSouth == Blocks.FURNACE || blockSouth == Blocks.LIT_FURNACE) ||
				(blockEast == Blocks.FURNACE || blockEast == Blocks.LIT_FURNACE) ||
				(blockWest == Blocks.FURNACE || blockWest == Blocks.LIT_FURNACE)
			)
			{
				furnace = true;
			}

			// check if one of the adjacent blocks is a chest
			if (
				(blockNorth == Blocks.CHEST || blockNorth == Blocks.TRAPPED_CHEST || blockNorth == Blocks.ENDER_CHEST) ||
				(blockSouth == Blocks.CHEST || blockSouth == Blocks.TRAPPED_CHEST || blockSouth == Blocks.ENDER_CHEST) ||
				(blockEast == Blocks.CHEST || blockEast == Blocks.TRAPPED_CHEST || blockEast == Blocks.ENDER_CHEST) ||
				(blockWest == Blocks.CHEST || blockWest == Blocks.TRAPPED_CHEST || blockWest == Blocks.ENDER_CHEST)
			)
			{
				chest = true;
			}

			// check if one of the adjacent blocks is a crafting table
			if (
				(blockNorth == Blocks.CRAFTING_TABLE) ||
				(blockSouth == Blocks.CRAFTING_TABLE) ||
				(blockEast == Blocks.CRAFTING_TABLE) ||
				(blockWest == Blocks.CRAFTING_TABLE)
			)
			{
				workbench = true;
			}

			// if the block is adjacent to all three, trigger the achievement
			if (furnace && chest && workbench)
			{
				playerIn.addStat(ModAchievementList.PORTEMANTEAU);
			}
		}
	}

}
