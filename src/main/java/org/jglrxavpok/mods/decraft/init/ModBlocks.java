package org.jglrxavpok.mods.decraft.init;

import org.jglrxavpok.mods.decraft.block.BlockUncraftingTable;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;


public class ModBlocks
{

	public static final BlockUncraftingTable uncrafting_table = new BlockUncraftingTable();


	public static void preInit()
	{
		// register the block
		GameRegistry.registerBlock(uncrafting_table, ItemBlock.class, uncrafting_table.getUnlocalizedName().substring(5));
	}

	public static void init()
	{
		// create block crafting recipe
		GameRegistry.addShapedRecipe(new ItemStack(uncrafting_table), new Object[]
		{
			"SSS", "SXS", "SSS", 'X', Blocks.crafting_table, 'S', Blocks.cobblestone
		});
	}

}
