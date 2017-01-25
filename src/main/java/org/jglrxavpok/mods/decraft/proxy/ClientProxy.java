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
		
		// initialize the configuration
		ModConfiguration.clientPreInit();
		
		// register the item models
		ModItems.clientPreInit();
	}
	
	@Override
	public void init()
	{
		super.init();
		
		// register the block model
		ModBlocks.clientInit();
		
		// initialize the achievement event handlers
		ModAchievementList.clientInit();
	}
	
	@Override
	public void postInit()
	{
		super.postInit();
	}

}
