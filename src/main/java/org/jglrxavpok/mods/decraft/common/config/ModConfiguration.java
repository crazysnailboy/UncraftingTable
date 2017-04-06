package org.jglrxavpok.mods.decraft.common.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.client.config.ModGuiConfigEntries;
import org.jglrxavpok.mods.decraft.common.network.message.ConfigSyncMessage;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.GuiConfigEntries.NumberSliderEntry;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;


public class ModConfiguration
{

	private static Configuration config = null;

	public static final String CATEGORY_NUGGETS = "nuggets";


	public static int standardLevel = 5;
	public static int maxUsedLevel = 30;
	public static int enchantmentCost = 1;
	public static int uncraftMethod = 0;
	public static String[] excludedItems = new String[] { };

	public static boolean useNuggets = true;
	public static boolean registerNuggets = true;
	public static boolean useRabbitHide = false;
	public static boolean ensureReturn = true;


	public static void preInit()
	{
		File configFile = new File(Loader.instance().getConfigDir(), ModUncrafting.MODID + ".cfg");

		config = new Configuration(configFile);
		config.load();

		syncFromFile();

		MinecraftForge.EVENT_BUS.register(new ConfigEventHandler());
	}

	public static void clientPreInit()
	{
		MinecraftForge.EVENT_BUS.register(new ClientConfigEventHandler());
	}

	public static Configuration getConfig()
	{
		return config;
	}


	public static void syncFromFile()
	{
		syncConfig(true, true);
	}

	public static void syncFromGUI()
	{
		syncConfig(false, true);
	}

	public static void syncFromFields()
	{
		syncConfig(false, false);
	}


	private static void syncConfig(boolean loadConfigFromFile, boolean readFieldsFromConfig)
	{

		if (loadConfigFromFile) config.load();

		Property propStandardLevel = config.get(Configuration.CATEGORY_GENERAL, "standardLevel", standardLevel, "Minimum required level to uncraft an item", 0, 50);
		propStandardLevel.setLanguageKey("uncrafting.options.standardLevel");
		propStandardLevel.setRequiresMcRestart(false);

		Property propMaxLevel = config.get(Configuration.CATEGORY_GENERAL, "maxUsedLevel", maxUsedLevel, "Maximum required level to uncraft an item", 0, 50);
		propMaxLevel.setLanguageKey("uncrafting.options.maxUsedLevel");
		propMaxLevel.setRequiresMcRestart(false);

		Property propEnchantmentCost = config.get(Configuration.CATEGORY_GENERAL, "enchantmentCost", enchantmentCost, "Cost per enchantment when removing enchantments onto books", 0, 10);
		propEnchantmentCost.setLanguageKey("uncrafting.options.enchantmentCost");
		propEnchantmentCost.setRequiresMcRestart(false);

		Property propUncraftMethod = config.get(Configuration.CATEGORY_GENERAL, "uncraftMethod", uncraftMethod, "ID of the used uncrafting equation.");
		propUncraftMethod.setLanguageKey("uncrafting.options.method");
		propUncraftMethod.setValidValues(new String[] { "jglrxavpok", "Xell75 & zenen" });
		propUncraftMethod.setRequiresMcRestart(false);

		Property propExcludedItems = config.get(Configuration.CATEGORY_GENERAL, "excludedItems", excludedItems, "List of items which cannot be uncrafted");
		propExcludedItems.setLanguageKey("uncrafting.options.excludedItems");
		propExcludedItems.setRequiresMcRestart(false);


		Property propUseNuggets = config.get(ModConfiguration.CATEGORY_NUGGETS, "useNuggets", useNuggets, "Use available nuggets for partial returns of damaged items");
		propUseNuggets.setLanguageKey("uncrafting.options.nuggets.useNuggets");
		propUseNuggets.setRequiresMcRestart(false);

		Property propRegisterNuggets = config.get(ModConfiguration.CATEGORY_NUGGETS, "registerNuggets", registerNuggets, "Register additional nuggets to use for partial returns of damaged Vanilla items");
		propRegisterNuggets.setLanguageKey("uncrafting.options.nuggets.registerNuggets");
		propRegisterNuggets.setRequiresMcRestart(true);

		Property propUseRabbitHide = config.get(ModConfiguration.CATEGORY_NUGGETS, "useRabbitHide", useRabbitHide, "Use Rabbit Hide for partial returns of damaged Leather items");
		propUseRabbitHide.setLanguageKey("uncrafting.options.nuggets.useRabbitHide");
		propUseRabbitHide.setRequiresMcRestart(false);

		Property propEnsureReturn = config.get(ModConfiguration.CATEGORY_NUGGETS, "ensureReturn", ensureReturn, "Ensure that at least one nugget is returned, even for items with 0% durability");
		propEnsureReturn.setLanguageKey("uncrafting.options.nuggets.ensureReturn");
		propEnsureReturn.setRequiresMcRestart(false);



		try
		{
			propStandardLevel.setConfigEntryClass(NumberSliderEntry.class);
			propMaxLevel.setConfigEntryClass(NumberSliderEntry.class);
			propEnchantmentCost.setConfigEntryClass(NumberSliderEntry.class);
			propUncraftMethod.setConfigEntryClass(ModGuiConfigEntries.UncraftingMethodCycleEntry.class);
			propExcludedItems.setConfigEntryClass(ModGuiConfigEntries.ExcludedItemsArrayEntry.class);

			propUseNuggets.setConfigEntryClass(ModGuiConfigEntries.BooleanEntry.class);
			propRegisterNuggets.setConfigEntryClass(ModGuiConfigEntries.BooleanEntry.class);
			propUseRabbitHide.setConfigEntryClass(ModGuiConfigEntries.BooleanEntry.class);
			propEnsureReturn.setConfigEntryClass(ModGuiConfigEntries.BooleanEntry.class);

			List<String> propOrderGeneral = new ArrayList<String>();
			propOrderGeneral.add(propStandardLevel.getName());
			propOrderGeneral.add(propMaxLevel.getName());
			propOrderGeneral.add(propEnchantmentCost.getName());
			propOrderGeneral.add(propUncraftMethod.getName());
			propOrderGeneral.add(propExcludedItems.getName());
			config.setCategoryPropertyOrder(Configuration.CATEGORY_GENERAL, propOrderGeneral);

			List<String> propOrderNuggets = new ArrayList<String>();
			propOrderNuggets.add(propUseNuggets.getName());
			propOrderNuggets.add(propRegisterNuggets.getName());
			propOrderNuggets.add(propUseRabbitHide.getName());
			propOrderNuggets.add(propEnsureReturn.getName());
			config.setCategoryPropertyOrder(ModConfiguration.CATEGORY_NUGGETS, propOrderNuggets);

		}
		catch(NoClassDefFoundError e) { }


		if (readFieldsFromConfig)
		{
			standardLevel = propStandardLevel.getInt();
			maxUsedLevel = propMaxLevel.getInt();
			enchantmentCost = propEnchantmentCost.getInt();
			uncraftMethod = propUncraftMethod.getInt();
			excludedItems = propExcludedItems.getStringList();

			useNuggets = propUseNuggets.getBoolean();
			registerNuggets = propRegisterNuggets.getBoolean();
			useRabbitHide = propUseRabbitHide.getBoolean();
			ensureReturn = propEnsureReturn.getBoolean();
		}


		propStandardLevel.set(standardLevel);
		propMaxLevel.set(maxUsedLevel);
		propEnchantmentCost.set(enchantmentCost);
		propUncraftMethod.set(uncraftMethod);
		propExcludedItems.set(excludedItems);

		propUseNuggets.set(useNuggets);
		propRegisterNuggets.set(registerNuggets);
		propUseRabbitHide.set(useRabbitHide);
		propEnsureReturn.set(ensureReturn);


		if (config.hasChanged()) config.save();
	}



	public static class ConfigEventHandler
	{
		@SubscribeEvent
		public void onPlayerLoggedIn(PlayerLoggedInEvent event)
		{
			if (!event.player.worldObj.isRemote)
			{
				ModUncrafting.instance.getNetwork().sendTo(new ConfigSyncMessage(), (EntityPlayerMP)event.player);
			}
		}
	}

	public static class ClientConfigEventHandler
	{
		@SubscribeEvent
		public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
		{
			if (ModUncrafting.MODID.equals(event.modID))
			{
				if (!event.isWorldRunning || Minecraft.getMinecraft().isSingleplayer())
				{
					syncFromGUI();
					if (event.isWorldRunning && Minecraft.getMinecraft().isSingleplayer())
					{
						ModUncrafting.instance.getNetwork().sendToServer(new ConfigSyncMessage());
					}
				}
			}
		}
	}

}
