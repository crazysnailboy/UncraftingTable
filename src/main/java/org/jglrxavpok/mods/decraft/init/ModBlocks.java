package org.jglrxavpok.mods.decraft.init;

import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.block.BlockUncraftingTable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModBlocks {

    public static final BlockUncraftingTable UNCRAFTING_TABLE = new BlockUncraftingTable();

    
	public static void preInit()
	{    
        // register the block
	    GameRegistry.register(UNCRAFTING_TABLE);
	    GameRegistry.register(new ItemBlock(UNCRAFTING_TABLE).setRegistryName(UNCRAFTING_TABLE.getRegistryName()));
	}
    
	public static void init()
	{
        // create block crafting recipe
        GameRegistry.addShapedRecipe(new ItemStack(UNCRAFTING_TABLE), new Object[]
        {
            "SSS", "SXS", "SSS", 'X', Blocks.CRAFTING_TABLE, 'S', Blocks.COBBLESTONE
        });
	}
	
	public static void clientInit()
	{
		// register the block model
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(
			Item.getItemFromBlock(UNCRAFTING_TABLE), 0, 
			new ModelResourceLocation(UNCRAFTING_TABLE.getRegistryName().toString(), "inventory")
		);
	}

}
