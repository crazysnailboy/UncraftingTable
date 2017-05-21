package org.jglrxavpok.mods.decraft.proxy;

import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;
import org.jglrxavpok.mods.decraft.common.config.ModJsonConfiguration;
import org.jglrxavpok.mods.decraft.common.network.ModGuiHandler;
import org.jglrxavpok.mods.decraft.common.network.message.ConfigSyncMessage;
import org.jglrxavpok.mods.decraft.common.network.message.RecipeNavigationMessage;
import org.jglrxavpok.mods.decraft.init.ModBlocks;
import org.jglrxavpok.mods.decraft.init.ModItems;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.RecipeHandlers;
import org.jglrxavpok.mods.decraft.stats.ModAchievementList;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;


public class CommonProxy
{

	/**
	 * Run before anything else. Read your config, create blocks, items, etc, and register them with the GameRegistry.
	 */
	public void preInit()
	{
		// initialize the configuration
		ModConfiguration.preInit();
		ModJsonConfiguration.preInit();

		// register the blocks and items
		ModBlocks.preInit();
		ModItems.preInit();

		// register the network messages
		ModUncrafting.instance.getNetwork().registerMessage(RecipeNavigationMessage.MessageHandler.class, RecipeNavigationMessage.class, 0, Side.SERVER);
		ModUncrafting.instance.getNetwork().registerMessage(ConfigSyncMessage.MessageHandler.class, ConfigSyncMessage.class, 1, Side.CLIENT);
		ModUncrafting.instance.getNetwork().registerMessage(ConfigSyncMessage.MessageHandler.class, ConfigSyncMessage.class, 2, Side.SERVER);
	}


	/**
	 * Do your mod setup. Build whatever data structures you care about. Register recipes, send FMLInterModComms messages to other mods.
	 */
	public void init()
	{
		// create the crafting recipes
		ModBlocks.init();
		ModItems.init();

		// register the gui handler
		NetworkRegistry.INSTANCE.registerGuiHandler(ModUncrafting.instance, new ModGuiHandler());

		// initialize the achievements
		ModAchievementList.init();
	}


	/**
	 * Handle interaction with other mods, complete your setup based on this.
	 */
	public void postInit()
	{
		// initalize the recipe handlers
		RecipeHandlers.postInit();
	}

}
