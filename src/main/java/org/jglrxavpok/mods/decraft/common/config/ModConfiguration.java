package org.jglrxavpok.mods.decraft.common.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.client.config.GuiConfigEntries.ArrayEntry;
import cpw.mods.fml.client.config.GuiConfigEntries.NumberSliderEntry;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.client.config.UncraftingMethodCycleEntry;
import org.jglrxavpok.mods.decraft.client.config.UncraftableItemsArrayEntry;


public class ModConfiguration {
	
	private static Configuration config = null;
	private static ConfigEventHandler configEventHandler = new ConfigEventHandler();

    public static int uncraftMethod;
    public static int maxUsedLevel;
    public static int standardLevel;
    public static String[] uncraftableItems;

    
    public static void preInit(){
    	
		File configFile = new File(Loader.instance().getConfigDir(), ModUncrafting.MODID + ".cfg");
    	
		config = new Configuration(configFile);
		config.load();
    	
		syncFromFile();
    }
    
	public static void clientPreInit() {
		
		//MinecraftForge.EVENT_BUS.register(new ConfigEventHandler());
		FMLCommonHandler.instance().bus().register(configEventHandler);
		
	}
	
	public static Configuration getConfig() {
		return config;
	}
	
	
	public static void syncFromFile() {
		syncConfig(true, true);
	}

	public static void syncFromGUI() {
		syncConfig(false, true);
	}

	public static void syncFromFields() {
		syncConfig(false, false);
	}
	
	
	private static void syncConfig(boolean loadConfigFromFile, boolean readFieldsFromConfig) {	
		
		if (loadConfigFromFile) {
			config.load();
		}
		
		Property propStandardLevel = config.get(Configuration.CATEGORY_GENERAL, "standardLevel", 5, "Minimum required level to uncraft an item", 0, 50);
		propStandardLevel.setLanguageKey("uncrafting.options.standardLevel");
		propStandardLevel.setConfigEntryClass(NumberSliderEntry.class);
		propStandardLevel.setRequiresMcRestart(false);
		
		Property propMaxLevel = config.get(Configuration.CATEGORY_GENERAL, "maxUsedLevel", 30, "Maximum required level to uncraft an item", 0, 50);
		propMaxLevel.setLanguageKey("uncrafting.options.maxUsedLevel");
		propMaxLevel.setConfigEntryClass(NumberSliderEntry.class);
		propMaxLevel.setRequiresMcRestart(false);

		Property propUncraftMethod = config.get(Configuration.CATEGORY_GENERAL, "uncraftMethod", 0, "ID of the used uncrafting equation.");
		propUncraftMethod.setLanguageKey("uncrafting.options.method");
		propUncraftMethod.setValidValues(new String[] { "jglrxavpok", "Xell75 & zenen" });
		propUncraftMethod.setConfigEntryClass(UncraftingMethodCycleEntry.class);
		propUncraftMethod.setRequiresMcRestart(false);

		Property propUncraftableItems = config.get(Configuration.CATEGORY_GENERAL, "uncraftableItems", new String[] { }, "List of IDs for uncraftable items");
		propUncraftableItems.setLanguageKey("uncrafting.options.uncraftableItems");
		propUncraftableItems.setConfigEntryClass(UncraftableItemsArrayEntry.class);
		propUncraftableItems.setRequiresMcRestart(false);
		
		
		List<String> propOrderGeneral = new ArrayList<String>();
		propOrderGeneral.add(propStandardLevel.getName());
		propOrderGeneral.add(propMaxLevel.getName());
		propOrderGeneral.add(propUncraftMethod.getName());
		propOrderGeneral.add(propUncraftableItems.getName());
		config.setCategoryPropertyOrder(Configuration.CATEGORY_GENERAL, propOrderGeneral);
		
		
		
		if (readFieldsFromConfig) {
			
			standardLevel = propStandardLevel.getInt();
			maxUsedLevel = propMaxLevel.getInt();
			uncraftMethod = propUncraftMethod.getInt();
			uncraftableItems = propUncraftableItems.getStringList();
					
		}
		
		
		propStandardLevel.set(standardLevel);
		propMaxLevel.set(maxUsedLevel);
		propUncraftMethod.set(uncraftMethod);
		propUncraftableItems.set(uncraftableItems);
		
		
		if (config.hasChanged()) {
			config.save();
		}
		
	}

	
	
	
	public static class ConfigEventHandler 
	{
		@SubscribeEvent //(priority = EventPriority.NORMAL)
		public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) 
		{
			if (ModUncrafting.MODID.equals(event.modID) && !event.isWorldRunning)
			{
				syncFromGUI();
			}
		}
	}
	
    
}
