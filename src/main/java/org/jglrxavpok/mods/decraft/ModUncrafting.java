package org.jglrxavpok.mods.decraft;

import org.apache.logging.log4j.Logger;
import org.jglrxavpok.mods.decraft.block.BlockUncraftingTable;
import org.jglrxavpok.mods.decraft.common.network.ModGuiHandler;
import org.jglrxavpok.mods.decraft.proxy.CommonProxy;

import net.minecraft.stats.StatBasic;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;


@Mod(modid = ModUncrafting.MODID, name = ModUncrafting.MODNAME, version = ModUncrafting.VERSION, guiFactory = ModUncrafting.GUIFACTORY, updateJSON = ModUncrafting.UPDATEJSON)
public class ModUncrafting
{

	// constants
    public static final String MODID = "uncraftingtable";
    public static final String MODNAME = "jglrxavpok's UncraftingTable";
    public static final String VERSION = "1.7.1-pre1";
    public static final String GUIFACTORY = "org.jglrxavpok.mods.decraft.client.config.ModGuiFactory";
    public static final String UPDATEJSON = "https://raw.githubusercontent.com/crazysnailboy/uncraftingTable/1.11/update.json";
    
	private static final String CLIENT_PROXY_CLASS = "org.jglrxavpok.mods.decraft.proxy.ClientProxy";
	private static final String SERVER_PROXY_CLASS = "org.jglrxavpok.mods.decraft.proxy.CommonProxy";

	// mod instance
    @Instance(ModUncrafting.MODID)
    public static ModUncrafting instance;
    
    // proxy
	@SidedProxy(clientSide = CLIENT_PROXY_CLASS, serverSide = SERVER_PROXY_CLASS)
    public static CommonProxy proxy;
    
    // blocks
    public static BlockUncraftingTable uncraftingTable = new BlockUncraftingTable();

    // guis
    public ModGuiHandler guiHandler = new ModGuiHandler();

   
    public StatBasic uncraftedItemsStat;
    
    // logger
    private Logger logger;

    public Logger getLogger()
    {
        return logger;
    }
    
    // network
    private static SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(ModUncrafting.MODID);
    
    public SimpleNetworkWrapper getNetwork()
    {
        return network;
    }
    
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	proxy.preInit();
        logger = event.getModLog();

        // initialize the statistics
        uncraftedItemsStat = (StatBasic)(new StatBasic("stat.uncrafteditems", new TextComponentTranslation("stat.uncrafteditems", new Object[0])).registerStat());
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	proxy.init();
    	
    	// register the gui handler
        NetworkRegistry.INSTANCE.registerGuiHandler(ModUncrafting.instance, guiHandler);
    }
    
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	proxy.postInit();
    }

}
