package org.jglrxavpok.mods.decraft;

import java.io.File;
import java.util.Properties;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBasic;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import cpw.mods.fml.client.config.GuiConfigEntries.*;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;


import org.apache.logging.log4j.Logger;
import org.jglrxavpok.mods.decraft.client.config.CycleUncraftMethodEntry;


@Mod(modid = ModUncrafting.MODID, name = ModUncrafting.MODNAME, version = ModUncrafting.VERSION, guiFactory = ModUncrafting.GUIFACTORY)
public class ModUncrafting
{

    public static final String MODID = "uncraftingTable";
    public static final String MODNAME = "jglrxavpok's UncraftingTable";
    public static final String VERSION = "1.4.2-pre4";
    public static final String GUIFACTORY = "org.jglrxavpok.mods.decraft.client.config.UncraftingGuiFactory";
    
    
    @Instance("uncraftingTable")
    public static ModUncrafting instance;

    public Block uncraftingTable;
    
    public UnGuiHandler guiHandler = new UnGuiHandler();

    private Achievement craftTable;
    public Achievement uncraftAny;
    private Achievement uncraftDiamondHoe;
    private Achievement uncraftJunk;
    private Achievement uncraftDiamondShovel;
    public Achievement theHatStandAchievement;

    /**
     * Number of uncrafted items
     */
    public StatBasic uncraftedItemsStat;

    private File cfgFile;
    public int uncraftMethod;
    public static int maxUsedLevel;
    public static int standardLevel;
    public int minLvlServer;
    public int maxLvlServer;

    private Logger logger;
    public static Configuration config;

    public Logger getLogger()
    {
        return logger;
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, guiHandler);
        DefaultsRecipeHandlers.load();

        logger.info("Uncrafting Table has been correctly initialized!");
    }

    
    @SubscribeEvent
    public void onCrafting(PlayerEvent.ItemCraftedEvent event)
    {
    	ItemStack item = event.crafting;
    	EntityPlayer player = event.player;
    	if (item.getItem().getUnlocalizedName().equals(uncraftingTable.getUnlocalizedName())) 
    	{
    		player.triggerAchievement(instance.craftTable);
    	}
    }
    
    @SubscribeEvent
    public void onUncrafting(UncraftingEvent event)
    {

    }

    @SubscribeEvent
    public void onSuccessedUncrafting(SuccessedUncraftingEvent event)
    {
        Item craftedItem = event.getUncrafted().getItem();
        if (craftedItem == Items.diamond_hoe)
        {
            event.getPlayer().triggerAchievement(uncraftDiamondHoe);
        }
        else if (craftedItem == Items.diamond_shovel)
        {
            event.getPlayer().triggerAchievement(uncraftDiamondShovel);
        }
        if (craftedItem == Items.leather_leggings)
        {
            event.getPlayer().triggerAchievement(uncraftJunk);
        }
        else if (craftedItem == Items.leather_helmet)
        {
            event.getPlayer().triggerAchievement(uncraftJunk);
        }
        else if (craftedItem == Items.leather_boots)
        {
            event.getPlayer().triggerAchievement(uncraftJunk);
        }
        else if (craftedItem == Items.leather_chestplate)
        {
            event.getPlayer().triggerAchievement(uncraftJunk);
        }
        else if (craftedItem == Items.glass_bottle)
        {
            event.getPlayer().triggerAchievement(uncraftJunk);
        }
    }

    
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) 
    {
    	if (eventArgs.modID.equals(ModUncrafting.MODID))
    	{
    		saveProperties();
    	}
    }
    
    
    public void saveProperties()
    {
        try
        {
            config.save();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();

        // initialize mod config
        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        
        standardLevel = (int)config.get(Configuration.CATEGORY_GENERAL, "standardLevel", 5, "Minimum required level to uncraft an item", 0, 50)
    		.setLanguageKey("uncrafting.options.standardLevel")
    		.setConfigEntryClass(NumberSliderEntry.class)
    		.getInt();
        
        maxUsedLevel = (int)config.get(Configuration.CATEGORY_GENERAL, "maxUsedLevel", 30, "Maximum required level to uncraft an item", 0, 50)
    		.setLanguageKey("uncrafting.options.maxUsedLevel")
    		.setConfigEntryClass(NumberSliderEntry.class)
    		.getInt();
        
        uncraftMethod = config.get(Configuration.CATEGORY_GENERAL, "uncraftMethod", 0, "ID of the used uncrafting equation.")
        	.setLanguageKey("uncrafting.options.method")
        	.setValidValues(new String[] { "jglrxavpok", "Xell75 & zenen" })
        	.setConfigEntryClass(CycleUncraftMethodEntry.class)
        	.getInt();
        
        
        
        //standardLevel = config.getInt("standardLevel", Configuration.CATEGORY_GENERAL, 5, 0, 50, "Minimum required level to uncraft an item");
        //maxUsedLevel = config.getInt("maxUsedLevel", Configuration.CATEGORY_GENERAL, 30, 0, 50, "Maximum required level to uncraft an item");
        //uncraftMethod = config.getInt("uncraftMethod", Configuration.CATEGORY_GENERAL, 0, 0, 1, "ID of the used uncrafting equation.");
        minLvlServer = standardLevel;
        maxLvlServer = maxUsedLevel;
        config.save();

        // register for events
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
        
        // initialize the block
        uncraftingTable = new BlockUncraftingTable();
        GameRegistry.registerBlock(uncraftingTable, ItemBlock.class, "uncrafting_table");
        
        // create block crafting recipe
        GameRegistry.addShapedRecipe(new ItemStack(uncraftingTable), new Object[]
        {
                "SSS", "SXS", "SSS", 'X', Blocks.crafting_table, 'S', Blocks.cobblestone
        });
        
        // create the acheivements
        craftTable = new Achievement("createDecraftTable", "createDecraftTable", 1 - 2 - 2, -1 - 3, uncraftingTable, null).registerStat();
        uncraftAny = new Achievement("uncraftAnything", "uncraftAnything", 2 - 2, -2 - 2, Items.diamond_hoe, craftTable).registerStat();
        uncraftDiamondHoe = new Achievement("uncraftDiamondHoe", "uncraftDiamondHoe", 2 - 2, 0 - 2, Items.diamond_hoe, uncraftAny).registerStat();
        uncraftJunk = new Achievement("uncraftJunk", "uncraftJunk", 1 - 2, -1 - 2, Items.leather_boots, uncraftAny).registerStat();
        uncraftDiamondShovel = new Achievement("uncraftDiamondShovel", "uncraftDiamondShovel", 3 - 2, -1 - 2, Items.diamond_shovel, uncraftAny).registerStat();
        theHatStandAchievement = new Achievement("porteManteauAchievement", "porteManteauAchievement", 3 - 2, -4 - 2, Blocks.fence, craftTable).registerStat();

        // register the acheivements page
        AchievementPage.registerAchievementPage(new AchievementPage("Uncrafting Table",
            new Achievement[]
            {
                    craftTable, uncraftAny, uncraftDiamondHoe, uncraftJunk, uncraftDiamondShovel, theHatStandAchievement
            })
        );

        // initialize the statistics
        uncraftedItemsStat = (StatBasic)(new StatBasic("stat.uncrafteditems", new ChatComponentTranslation("stat.uncrafteditems", new Object[0])).registerStat());
    }

}
