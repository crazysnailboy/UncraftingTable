package org.jglrxavpok.mods.decraft.init;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;
import org.jglrxavpok.mods.decraft.inventory.ContainerUncraftingTable;
import org.jglrxavpok.mods.decraft.item.ItemNugget;
import org.jglrxavpok.mods.decraft.item.ItemNugget.EnumNuggetType;


@EventBusSubscriber(modid = ModUncrafting.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModContainers
{

	public static ContainerType<ContainerUncraftingTable> UNCRAFTING_CONTAINER;

	@SubscribeEvent
	public static void registerContainers(RegistryEvent.Register<ContainerType<?>> event) {
		UNCRAFTING_CONTAINER = IForgeContainerType.create((windowId, inv, data) -> new ContainerUncraftingTable(windowId, inv, inv.player.world));
		UNCRAFTING_CONTAINER.setRegistryName("uncrafting_table");
		event.getRegistry().registerAll(UNCRAFTING_CONTAINER);
	}

}