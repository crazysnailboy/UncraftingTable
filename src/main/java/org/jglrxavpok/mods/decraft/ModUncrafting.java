package org.jglrxavpok.mods.decraft;

import org.apache.logging.log4j.Logger;
import org.jglrxavpok.mods.decraft.proxy.CommonProxy;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
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


@Mod(modid = ModUncrafting.MODID, name = ModUncrafting.MODNAME, version = ModUncrafting.VERSION, guiFactory = ModUncrafting.GUIFACTORY)
public class ModUncrafting
{

	// constants
    public static final String MODID = "uncraftingTable";
    public static final String MODNAME = "jglrxavpok's UncraftingTable";
    public static final String VERSION = "1.4.2-pre5";
    public static final String GUIFACTORY = "org.jglrxavpok.mods.decraft.client.config.ModGuiFactory";
    
	private static final String CLIENT_PROXY_CLASS = "org.jglrxavpok.mods.decraft.proxy.ClientProxy";
	private static final String SERVER_PROXY_CLASS = "org.jglrxavpok.mods.decraft.proxy.CommonProxy";

	// mod instance
    @Instance("uncraftingTable")
    public static ModUncrafting instance;
    
    // proxy
	@SidedProxy(clientSide = CLIENT_PROXY_CLASS, serverSide = SERVER_PROXY_CLASS)
    public static CommonProxy proxy;
    
    // blocks
    public Block uncraftingTable;

    // guis
    public UnGuiHandler guiHandler = new UnGuiHandler();

    // achievements and stats
    private Achievement craftTable;
    public Achievement uncraftAny;
    private Achievement uncraftDiamondHoe;
    private Achievement uncraftJunk;
    private Achievement uncraftDiamondShovel;
    public Achievement theHatStandAchievement;
    
    public StatBasic uncraftedItemsStat;
    
    // logger
    private Logger logger;

    public Logger getLogger()
    {
        return logger;
    }
    
    
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	proxy.preInit();
    	
        logger = event.getModLog();


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

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	proxy.init();
    	
        NetworkRegistry.INSTANCE.registerGuiHandler(this, guiHandler);
        RecipeHandlers.load();

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

}
