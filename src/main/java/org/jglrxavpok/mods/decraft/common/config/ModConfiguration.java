package org.jglrxavpok.mods.decraft.common.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.common.network.message.ConfigSyncMessage;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

public class ModConfiguration
{

	private static class DefaultValues
	{
		private static final int standardLevel = 5;
		private static final int maxUsedLevel = 30;
		private static final int enchantmentCost = 1;
		private static final int uncraftMethod = 0;
		private static final String[] excludedItems = new String[] { };

		private static final boolean useNuggets = true;
		private static final boolean registerNuggets = true;
		private static final boolean useRabbitHide = false;
		private static final boolean ensureReturn = true;
	}

	private static Configuration config = null;


	public static final String CATEGORY_GENERAL = Configuration.CATEGORY_GENERAL;
	public static final String CATEGORY_NUGGETS = "nuggets";


	public static int standardLevel = DefaultValues.standardLevel;
	public static int maxUsedLevel = DefaultValues.maxUsedLevel;
	public static int enchantmentCost = DefaultValues.enchantmentCost;
	public static int uncraftMethod = DefaultValues.uncraftMethod;
	public static String[] excludedItems = DefaultValues.excludedItems;

	public static boolean useNuggets = DefaultValues.useNuggets;
	public static boolean registerNuggets = DefaultValues.registerNuggets;
	public static boolean useRabbitHide = DefaultValues.useRabbitHide;
	public static boolean ensureReturn = DefaultValues.ensureReturn;


	public static Configuration getConfig()
	{
		return config;
	}


	public static void initializeConfiguration()
	{
		File configFile = new File(Loader.instance().getConfigDir(), ModUncrafting.MODID + ".cfg");

		config = new Configuration(configFile);
		config.load();

		syncFromFile();
	}


	public static void syncFromFile()
	{
		syncConfig(true, true);
	}

	public static void syncFromGUI()
	{
		syncConfig(false, true);
	}

	private static void syncConfig(boolean loadConfigFromFile, boolean readFieldsFromConfig)
	{

		if (loadConfigFromFile) config.load();

		Property propStandardLevel = config.get(CATEGORY_GENERAL, "standardLevel", DefaultValues.standardLevel, "Minimum required level to uncraft an item", 0, 50);
		propStandardLevel.setLanguageKey("uncrafting.options.standardLevel");
		propStandardLevel.setRequiresMcRestart(false);

		Property propMaxLevel = config.get(CATEGORY_GENERAL, "maxUsedLevel", DefaultValues.maxUsedLevel, "Maximum required level to uncraft an item", 0, 50);
		propMaxLevel.setLanguageKey("uncrafting.options.maxUsedLevel");
		propMaxLevel.setRequiresMcRestart(false);

		Property propEnchantmentCost = config.get(CATEGORY_GENERAL, "enchantmentCost", DefaultValues.enchantmentCost, "Cost per enchantment when removing enchantments onto books", 0, 10);
		propEnchantmentCost.setLanguageKey("uncrafting.options.enchantmentCost");
		propEnchantmentCost.setRequiresMcRestart(false);

		Property propUncraftMethod = config.get(CATEGORY_GENERAL, "uncraftMethod", DefaultValues.uncraftMethod, "ID of the used uncrafting equation.");
		propUncraftMethod.setLanguageKey("uncrafting.options.method");
		propUncraftMethod.setValidValues(new String[] { "jglrxavpok", "Xell75 & zenen" });
		propUncraftMethod.setRequiresMcRestart(false);

		Property propExcludedItems = config.get(CATEGORY_GENERAL, "excludedItems", DefaultValues.excludedItems, "List of items which cannot be uncrafted");
		propExcludedItems.setLanguageKey("uncrafting.options.excludedItems");
		propExcludedItems.setRequiresMcRestart(false);


		Property propUseNuggets = config.get(CATEGORY_NUGGETS, "useNuggets", DefaultValues.useNuggets, "Use available nuggets for partial returns of damaged items");
		propUseNuggets.setLanguageKey("uncrafting.options.nuggets.useNuggets");
		propUseNuggets.setRequiresMcRestart(false);

		Property propRegisterNuggets = config.get(CATEGORY_NUGGETS, "registerNuggets", DefaultValues.registerNuggets, "Register additional nuggets to use for partial returns of damaged Vanilla items");
		propRegisterNuggets.setLanguageKey("uncrafting.options.nuggets.registerNuggets");
		propRegisterNuggets.setRequiresMcRestart(true);

		Property propUseRabbitHide = config.get(CATEGORY_NUGGETS, "useRabbitHide", DefaultValues.useRabbitHide, "Use Rabbit Hide for partial returns of damaged Leather items");
		propUseRabbitHide.setLanguageKey("uncrafting.options.nuggets.useRabbitHide");
		propUseRabbitHide.setRequiresMcRestart(false);

		Property propEnsureReturn = config.get(CATEGORY_NUGGETS, "ensureReturn", DefaultValues.ensureReturn, "Ensure that at least one nugget is returned, even for items with 0% durability");
		propEnsureReturn.setLanguageKey("uncrafting.options.nuggets.ensureReturn");
		propEnsureReturn.setRequiresMcRestart(false);


		List<String> propOrderGeneral = new ArrayList<String>();
		propOrderGeneral.add(propStandardLevel.getName());
		propOrderGeneral.add(propMaxLevel.getName());
		propOrderGeneral.add(propEnchantmentCost.getName());
		propOrderGeneral.add(propUncraftMethod.getName());
		propOrderGeneral.add(propExcludedItems.getName());
		config.setCategoryPropertyOrder(CATEGORY_GENERAL, propOrderGeneral);

		List<String> propOrderNuggets = new ArrayList<String>();
		propOrderNuggets.add(propUseNuggets.getName());
		propOrderNuggets.add(propRegisterNuggets.getName());
		propOrderNuggets.add(propUseRabbitHide.getName());
		propOrderNuggets.add(propEnsureReturn.getName());
		config.setCategoryPropertyOrder(CATEGORY_NUGGETS, propOrderNuggets);


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



	@EventBusSubscriber
	public static class ConfigEventHandler
	{

		@SubscribeEvent
		public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
		{
			if (!event.getPlayer().world.isRemote)
			{
				ModUncrafting.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)event.getPlayer()), new ConfigSyncMessage());
			}
		}

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
		{
			if (event.getModID().equals(ModUncrafting.MODID))
			{
				if (!event.isWorldRunning() || Minecraft.getInstance().isSingleplayer())
				{
					syncFromGUI();
					if (event.isWorldRunning() && Minecraft.getInstance().isSingleplayer())
					{
						ModUncrafting.NETWORK.sendToServer(new ConfigSyncMessage());
					}
				}
			}
		}

	}

}