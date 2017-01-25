package org.jglrxavpok.mods.decraft.init;

import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.block.BlockUncraftingTable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModBlocks 
{
	
	public static final BlockUncraftingTable uncrafting_table = new BlockUncraftingTable();

	
	public static void preInit()
	{	
		// register the block
		GameRegistry.registerBlock(uncrafting_table, ItemBlock.class, "uncrafting_table");
	}
	
	public static void init()
	{
		// create block crafting recipe
		GameRegistry.addShapedRecipe(new ItemStack(uncrafting_table), new Object[]
		{
			"SSS", "SXS", "SSS", 'X', Blocks.crafting_table, 'S', Blocks.cobblestone
		});
	}
	
	public static void clientInit()
	{
		// register the block model
		Item item = Item.getItemFromBlock(uncrafting_table);
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(ModUncrafting.MODID + ":" + item.getUnlocalizedName().substring(5), "inventory"));
	}
	
}
