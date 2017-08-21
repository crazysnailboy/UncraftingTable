package org.jglrxavpok.mods.decraft.init;

import org.jglrxavpok.mods.decraft.block.BlockUncraftingTable;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@EventBusSubscriber
public class ModBlocks
{

	public static final Block UNCRAFTING_TABLE = new BlockUncraftingTable().setRegistryName("uncrafting_table").setUnlocalizedName("uncrafting_table");


	@SubscribeEvent
	public static void registerBlocks(final RegistryEvent.Register<Block> event)
	{
		// register the block
		event.getRegistry().register(UNCRAFTING_TABLE);
	}

	@SubscribeEvent
	public static void registerItems(final RegistryEvent.Register<Item> event)
	{
		// register the itemblock
		event.getRegistry().register(new ItemBlock(UNCRAFTING_TABLE).setRegistryName(UNCRAFTING_TABLE.getRegistryName()));
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void registerModels(final ModelRegistryEvent event)
	{
		// register the inventory model
		Item item = Item.getItemFromBlock(UNCRAFTING_TABLE);
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}

	public static void registerCraftingRecipes()
	{
		// create block crafting recipe
		GameRegistry.addShapedRecipe(new ItemStack(UNCRAFTING_TABLE), new Object[]
		{
			"SSS", "SXS", "SSS", 'X', Blocks.CRAFTING_TABLE, 'S', Blocks.COBBLESTONE
		});
	}

}