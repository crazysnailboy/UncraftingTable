package org.jglrxavpok.mods.decraft.block;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import org.jglrxavpok.mods.decraft.inventory.ContainerUncraftingTable;

import javax.annotation.Nullable;


public class BlockUncraftingTable extends Block
{

	private static final INamedContainerProvider CONTAINER_SUPPLIER = new INamedContainerProvider() {
		@Override
		public ITextComponent getDisplayName() {
			return new TranslationTextComponent("gui.uncrafting_table.table.title");
		}

		@Nullable
		@Override
		public Container createMenu(int index, PlayerInventory playerInv, PlayerEntity player) {
			return new ContainerUncraftingTable(index, playerInv, player.world);
		}
	};

	public BlockUncraftingTable()
	{
		super(Block.Properties.create(Material.ROCK).hardnessAndResistance(3f).sound(SoundType.STONE));
	}

	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
	{
		if (!world.isRemote)
		{
			NetworkHooks.openGui((ServerPlayerEntity)player, CONTAINER_SUPPLIER, pos);
			checkForPorteManteau(player, world, pos);
		}
		return true;
	}

	private void checkForPorteManteau(PlayerEntity player, World world, BlockPos pos)
	{
		boolean furnace = false;
		boolean chest = false;
		boolean workbench = false;

		// if the block beneath is a fence...
		if (world.getBlockState(pos.down()).getBlock() instanceof FenceBlock)
		{
			Block blockEast = world.getBlockState(pos.east()).getBlock();
			Block blockWest = world.getBlockState(pos.west()).getBlock();
			Block blockNorth = world.getBlockState(pos.north()).getBlock();
			Block blockSouth = world.getBlockState(pos.south()).getBlock();

			// check if one of the adjacent blocks is a furnace
			if (
				blockNorth == Blocks.FURNACE ||
				blockSouth == Blocks.FURNACE ||
				blockEast == Blocks.FURNACE ||
				blockWest == Blocks.FURNACE
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
// FIXME				player.addStat(ModAchievementList.PORTEMANTEAU);
			}
		}
	}

}
