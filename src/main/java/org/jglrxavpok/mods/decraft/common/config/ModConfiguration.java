package org.jglrxavpok.mods.decraft.common.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.client.config.ModGuiConfigEntries;
import org.jglrxavpok.mods.decraft.common.network.message.ConfigSyncMessage;

import cpw.mods.fml.client.config.GuiConfigEntries.NumberSliderEntry;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;


public class ModConfiguration
{

	private static Configuration config = null;

	public static final String CATEGORY_UPDATES = "updates";
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

	public static boolean checkForUpdates = true;
	public static boolean promptForLatest = true;
	public static boolean promptForRecommended = true;



	public static void preInit()
	{
		File configFile = new File(Loader.instance().getConfigDir(), ModUncrafting.MODID + ".cfg");

		config = new Configuration(configFile);
		config.load();

		syncFromFile();

		FMLCommonHandler.instance().bus().register(new ConfigEventHandler());
	}

	public static void clientPreInit()
	{
		FMLCommonHandler.instance().bus().register(new ClientConfigEventHandler());
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

		Property propUseRabbitHide = config.get(ModConfiguration.CATEGORY_NUGGETS, "useRabbitHide", useRabbitHide, "Use Rabbit Hide for partial returns of damaged Leather items (Requires Et Futurum)");
		propUseRabbitHide.setLanguageKey("uncrafting.options.nuggets.useRabbitHide");
		propUseRabbitHide.setRequiresMcRestart(false);

		Property propEnsureReturn = config.get(ModConfiguration.CATEGORY_NUGGETS, "ensureReturn", ensureReturn, "Ensure that at least one nugget is returned, even for items with 0% durability");
		propEnsureReturn.setLanguageKey("uncrafting.options.nuggets.ensureReturn");
		propEnsureReturn.setRequiresMcRestart(false);


		Property propCheckForUpdates = config.get(ModConfiguration.CATEGORY_UPDATES, "checkForUpdates", checkForUpdates, "Should the mod check for updates on startup");
		propCheckForUpdates.setLanguageKey("uncrafting.options.updates.checkForUpdates");
		propCheckForUpdates.setRequiresMcRestart(true);

		Property propPromptForLatest = config.get(ModConfiguration.CATEGORY_UPDATES, "promptForLatest", promptForLatest, "Alert the user when there is a new version");
		propPromptForLatest.setLanguageKey("uncrafting.options.updates.promptForLatest");
		propPromptForLatest.setRequiresMcRestart(true);

		Property propPromptForRecommended = config.get(ModConfiguration.CATEGORY_UPDATES, "promptForRecommended", promptForRecommended, "Alert the user when there is a new recommended version");
		propPromptForRecommended.setLanguageKey("uncrafting.options.updates.promptForRecommended");
		propPromptForRecommended.setRequiresMcRestart(true);



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

			propCheckForUpdates.setConfigEntryClass(ModGuiConfigEntries.BooleanEntry.class);
			propPromptForLatest.setConfigEntryClass(ModGuiConfigEntries.BooleanEntry.class);
			propPromptForRecommended.setConfigEntryClass(ModGuiConfigEntries.BooleanEntry.class);

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

			List<String> propOrderUpdates = new ArrayList<String>();
			propOrderUpdates.add(propCheckForUpdates.getName());
			propOrderUpdates.add(propPromptForLatest.getName());
			propOrderUpdates.add(propPromptForRecommended.getName());
			config.setCategoryPropertyOrder(ModConfiguration.CATEGORY_UPDATES, propOrderUpdates);

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

			checkForUpdates = propCheckForUpdates.getBoolean();
			promptForLatest = propPromptForLatest.getBoolean();
			promptForRecommended = propPromptForRecommended.getBoolean();
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

		propCheckForUpdates.set(checkForUpdates);
		propPromptForLatest.set(promptForLatest);
		propPromptForRecommended.set(promptForRecommended);


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
			if (ModUncrafting.MODID.equals(event.modID) && !event.isWorldRunning)
			{
				syncFromGUI();
			}
		}
	}

}