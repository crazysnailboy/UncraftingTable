package org.jglrxavpok.mods.decraft.init;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.block.BlockUncraftingTable;


@EventBusSubscriber(modid = ModUncrafting.MODID)
public class ModBlocks
{

	public static final Block UNCRAFTING_TABLE = new BlockUncraftingTable().setRegistryName(ModUncrafting.MODID, "uncrafting_table");


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
		event.getRegistry().register(new BlockItem(UNCRAFTING_TABLE, new Item.Properties().group(ItemGroup.DECORATIONS)).setRegistryName(UNCRAFTING_TABLE.getRegistryName()));
	}

}