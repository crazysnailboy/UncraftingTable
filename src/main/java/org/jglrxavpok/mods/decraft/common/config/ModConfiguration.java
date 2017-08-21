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
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;


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

		private static final boolean checkForUpdates = true;
		private static final boolean promptForLatest = true;
		private static final boolean promptForRecommended = true;
	}

	private static Configuration config = null;


	public static final String CATEGORY_GENERAL = Configuration.CATEGORY_GENERAL;
	public static final String CATEGORY_UPDATES = "updates";
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

	public static boolean checkForUpdates = DefaultValues.checkForUpdates;
	public static boolean promptForLatest = DefaultValues.promptForLatest;
	public static boolean promptForRecommended = DefaultValues.promptForRecommended;


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

		FMLCommonHandler.instance().bus().register(new ConfigEventHandler());
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

		Property propUseRabbitHide = config.get(CATEGORY_NUGGETS, "useRabbitHide", DefaultValues.useRabbitHide, "Use Rabbit Hide for partial returns of damaged Leather items (Requires Et Futurum)");
		propUseRabbitHide.setLanguageKey("uncrafting.options.nuggets.useRabbitHide");
		propUseRabbitHide.setRequiresMcRestart(false);

		Property propEnsureReturn = config.get(CATEGORY_NUGGETS, "ensureReturn", DefaultValues.ensureReturn, "Ensure that at least one nugget is returned, even for items with 0% durability");
		propEnsureReturn.setLanguageKey("uncrafting.options.nuggets.ensureReturn");
		propEnsureReturn.setRequiresMcRestart(false);


		Property propCheckForUpdates = config.get(CATEGORY_UPDATES, "checkForUpdates", DefaultValues.checkForUpdates, "Should the mod check for updates on startup");
		propCheckForUpdates.setLanguageKey("uncrafting.options.updates.checkForUpdates");
		propCheckForUpdates.setRequiresMcRestart(true);

		Property propPromptForLatest = config.get(CATEGORY_UPDATES, "promptForLatest", DefaultValues.promptForLatest, "Alert the user when there is a new version");
		propPromptForLatest.setLanguageKey("uncrafting.options.updates.promptForLatest");
		propPromptForLatest.setRequiresMcRestart(true);

		Property propPromptForRecommended = config.get(CATEGORY_UPDATES, "promptForRecommended", DefaultValues.promptForRecommended, "Alert the user when there is a new recommended version");
		propPromptForRecommended.setLanguageKey("uncrafting.options.updates.promptForRecommended");
		propPromptForRecommended.setRequiresMcRestart(true);


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

		List<String> propOrderUpdates = new ArrayList<String>();
		propOrderUpdates.add(propCheckForUpdates.getName());
		propOrderUpdates.add(propPromptForLatest.getName());
		propOrderUpdates.add(propPromptForRecommended.getName());
		config.setCategoryPropertyOrder(CATEGORY_UPDATES, propOrderUpdates);


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

		@SideOnly(Side.CLIENT)
		@SubscribeEvent
		public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
		{
			if (event.modID.equals(ModUncrafting.MODID) && !event.isWorldRunning)
			{
				syncFromGUI();
			}
		}
	}

}