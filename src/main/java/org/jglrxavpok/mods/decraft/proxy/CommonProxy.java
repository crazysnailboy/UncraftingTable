package org.jglrxavpok.mods.decraft.proxy;

import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;
import org.jglrxavpok.mods.decraft.common.network.ModGuiHandler;
import org.jglrxavpok.mods.decraft.common.network.message.ConfigSyncMessage;
import org.jglrxavpok.mods.decraft.common.network.message.RecipeNavigationMessage;
import org.jglrxavpok.mods.decraft.init.ModItems;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.RecipeHandlers;
import org.jglrxavpok.mods.decraft.stats.ModAchievementList;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;


public class CommonProxy
{

	public void preInit()
	{
		this.initializeConfig();
		this.registerNetworkMessages();
	}

	public void init()
	{
		this.registerOreDictionaryEntries();
		this.registerGuiHandler();
		this.registerAchievements();
	}

	public void postInit()
	{
		this.initializeRecipeHandlers();
	}


	private void initializeConfig()
	{
		ModConfiguration.preInit();
	}

	private void initializeRecipeHandlers()
	{
		RecipeHandlers.postInit();
	}


	private void registerAchievements()
	{
		ModAchievementList.registerAchievementPage();
	}

	private void registerGuiHandler()
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(ModUncrafting.instance, new ModGuiHandler());
	}

	private void registerOreDictionaryEntries()
	{
		ModItems.registerOreDictionaryEntries();
	}

	private void registerNetworkMessages()
	{
		ModUncrafting.instance.getNetwork().registerMessage(RecipeNavigationMessage.MessageHandler.class, RecipeNavigationMessage.class, 0, Side.SERVER);
		ModUncrafting.instance.getNetwork().registerMessage(ConfigSyncMessage.MessageHandler.class, ConfigSyncMessage.class, 1, Side.CLIENT);
		ModUncrafting.instance.getNetwork().registerMessage(ConfigSyncMessage.MessageHandler.class, ConfigSyncMessage.class, 2, Side.SERVER);
	}

}
