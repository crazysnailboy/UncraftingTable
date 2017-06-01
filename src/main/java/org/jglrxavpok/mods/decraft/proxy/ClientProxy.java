package org.jglrxavpok.mods.decraft.proxy;

import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;
import org.jglrxavpok.mods.decraft.init.ModBlocks;
import org.jglrxavpok.mods.decraft.init.ModItems;
import org.jglrxavpok.mods.decraft.stats.ModAchievementList;


public class ClientProxy extends CommonProxy
{

	@Override
	public void preInit()
	{
		super.preInit();

		this.initializeConfig();
		this.registerItemInventoryModels();
	}

	@Override
	public void init()
	{
		super.init();

		this.registerBlockInventoryModels();
		this.registerAchievements();
	}

	@Override
	public void postInit()
	{
		super.postInit();
	}


	private void initializeConfig()
	{
		ModConfiguration.clientPreInit();
	}


	private void registerAchievements()
	{
		ModAchievementList.registerEventHandler();
	}

	private void registerBlockInventoryModels()
	{
		ModBlocks.registerInventoryModels();
	}

	private void registerItemInventoryModels()
	{
		ModItems.registerInventoryModels();
	}

}
