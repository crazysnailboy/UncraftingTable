package org.jglrxavpok.mods.decraft;

import java.io.File;
import java.util.Properties;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBasic;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

import org.apache.logging.log4j.Logger;

@Mod(modid = ModUncrafting.MODID, name = "jglrxavpok's UncraftingTable", version = ModUncrafting.VERSION)

public class ModUncrafting
{

    public static final String  MODID      = "uncraftingTable";
    public static final String  VERSION    = "1.4.2-pre2";
    @Instance("uncraftingTable")
    public static ModUncrafting instance;

    /**
     * The block. Obviously :)
     */
    public Block                uncraftingTable;
    public UnGuiHandler         guiHandler = new UnGuiHandler();

    public Achievement          craftTable;
    public Achievement          uncraftAny;
    private Achievement         uncraftDiamondHoe;
    private Achievement         uncraftJunk;
    private Achievement         uncraftDiamondShovel;
    public Achievement          theHatStandAchievement;

    /**
     * Number of uncrafted items
     */
    public StatBasic            uncraftedItemsStat;

    private File                cfgFile;
    public int                  uncraftMethod;
    public static int           maxUsedLevel;
    public static int           standardLevel;
    private Properties          props;
    public int                  minLvlServer;
    public int                  maxLvlServer;

    private Logger              logger;
    private Configuration       config;

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

        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        standardLevel = config.getInt("standardLevel", Configuration.CATEGORY_GENERAL, 5, 0, 50, "Minimum required level to uncraft an item");
        maxUsedLevel = config.getInt("maxUsedLevel", Configuration.CATEGORY_GENERAL, 30, 0, 50, "Maximum required level to uncraft an item");
        uncraftMethod = config.getInt("uncraftMethod", Configuration.CATEGORY_GENERAL, 0, 0, 1, "ID of the used uncrafting equation.");
        minLvlServer = standardLevel;
        maxLvlServer = maxUsedLevel;
        config.save();

        MinecraftForge.EVENT_BUS.register(this);
        uncraftingTable = new BlockUncraftingTable();
        GameRegistry.registerBlock(uncraftingTable, ItemUncraftingTableBlock.class, "uncrafting_table");
        GameRegistry.addShapedRecipe(new ItemStack(uncraftingTable), new Object[]
        {
                "SSS", "SXS", "SSS", 'X', Blocks.crafting_table, 'S', Blocks.cobblestone
        });
        craftTable = (Achievement) new Achievement("createDecraftTable", "createDecraftTable", 1 - 2 - 2, -1 - 3, uncraftingTable, null).registerStat();
        uncraftAny = (Achievement) new Achievement("uncraftAnything", "uncraftAnything", 2 - 2, -2 - 2, Items.diamond_hoe, craftTable).registerStat();
        uncraftDiamondHoe = (Achievement) new Achievement("uncraftDiamondHoe", "uncraftDiamondHoe", 2 - 2, 0 - 2, Items.diamond_hoe, uncraftAny).registerStat();
        uncraftJunk = (Achievement) new Achievement("uncraftJunk", "uncraftJunk", 1 - 2, -1 - 2, Items.leather_boots, uncraftAny).registerStat();
        uncraftDiamondShovel = (Achievement) new Achievement("uncraftDiamondShovel", "uncraftDiamondShovel", 3 - 2, -1 - 2, Items.diamond_shovel, uncraftAny).registerStat();
        theHatStandAchievement = (Achievement) new Achievement("porteManteauAchievement", "porteManteauAchievement", 3 - 2, -4 - 2, Blocks.fence, craftTable).registerStat();
        AchievementPage.registerAchievementPage(new AchievementPage("Uncrafting Table",
                new Achievement[]
                {
                        craftTable, uncraftAny, uncraftDiamondHoe, uncraftJunk, uncraftDiamondShovel, theHatStandAchievement
                }));

        uncraftedItemsStat = (StatBasic) (new StatBasic("stat.uncrafteditems", new ChatComponentTranslation("stat.uncrafteditems", new Object[0])).registerStat());
    }

}
