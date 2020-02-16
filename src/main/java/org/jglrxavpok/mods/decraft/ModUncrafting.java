package org.jglrxavpok.mods.decraft;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jglrxavpok.mods.decraft.init.ModBlocks;
import org.jglrxavpok.mods.decraft.init.ModContainers;
import org.jglrxavpok.mods.decraft.init.ModItems;
import org.jglrxavpok.mods.decraft.proxy.ClientProxy;
import org.jglrxavpok.mods.decraft.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;

@Mod(ModUncrafting.MODID)
public class ModUncrafting
{

	public static final String MODID = "uncraftingtable";
	public static final String NAME = "jglrxavpok's Uncrafting Table";
	public static final String GUIFACTORY = "org.jglrxavpok.mods.decraft.client.config.ModGuiFactory";
	public static final String UPDATEJSON = "https://raw.githubusercontent.com/crazysnailboy/UncraftingTable/1.12/update.json";

	private static final String CLIENT_PROXY_CLASS = "org.jglrxavpok.mods.decraft.proxy.ClientProxy";
	private static final String SERVER_PROXY_CLASS = "org.jglrxavpok.mods.decraft.proxy.CommonProxy";

	public static ModUncrafting instance;

	public static CommonProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new CommonProxy());

	public static final Logger LOGGER = LogManager.getLogger(MODID);

	private static final String NETWORK_VERSION = "1.0";
	public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, "network"), () -> NETWORK_VERSION, NETWORK_VERSION::equalsIgnoreCase, NETWORK_VERSION::equalsIgnoreCase);

	public ModUncrafting()
	{
		instance = this;
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
	}

	public void clientSetup(FMLClientSetupEvent event)
	{
		ClientProxy.setup(event);
	}

	public void setup(FMLCommonSetupEvent event)
	{
		proxy.preInit();
		proxy.init();
		proxy.postInit();
	}

}