package org.jglrxavpok.mods.decraft.init;

import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;
import org.jglrxavpok.mods.decraft.item.ItemNugget;
import org.jglrxavpok.mods.decraft.item.ItemNugget.EnumNuggetType;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;


@EventBusSubscriber(modid = ModUncrafting.MODID)
public class ModItems
{

	public static final Item NUGGET = new ItemNugget().setRegistryName("nugget").setUnlocalizedName("nugget");


	@SubscribeEvent
	public static void registerItems(final RegistryEvent.Register<Item> event)
	{
		if (!ModConfiguration.registerNuggets) return;

		// register the items
		event.getRegistry().register(NUGGET);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onModelRegistry(final ModelRegistryEvent event)
	{
		if (!ModConfiguration.registerNuggets) return;

		// register the inventory models
		for (EnumNuggetType nuggetType : EnumNuggetType.usedValues())
		{
			ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation(ModUncrafting.MODID + ":" + nuggetType.getRegistryName(), "inventory");
			ModelLoader.setCustomModelResourceLocation(NUGGET, nuggetType.getMetadata(), itemModelResourceLocation);
		}
	}

	public static void registerOreDictionaryEntries()
	{
		if (!ModConfiguration.registerNuggets) return;

		// register the ore dictionary entries
		OreDictionary.registerOre("nuggetDiamond", new ItemStack(NUGGET, 1, EnumNuggetType.DIAMOND.getMetadata()));
		OreDictionary.registerOre("shardDiamond", new ItemStack(NUGGET, 1, EnumNuggetType.DIAMOND.getMetadata()));  // added for compatibility with Magic Bees
		OreDictionary.registerOre("nuggetEmerald", new ItemStack(NUGGET, 1, EnumNuggetType.EMERALD.getMetadata()));
		OreDictionary.registerOre("shardEmerald", new ItemStack(NUGGET, 1, EnumNuggetType.EMERALD.getMetadata()));  // added for compatibility with Magic Bees
		OreDictionary.registerOre("nuggetLeather", new ItemStack(NUGGET, 1, EnumNuggetType.LEATHER.getMetadata()));
	}

}