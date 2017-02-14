package org.jglrxavpok.mods.decraft.proxy;

import org.jglrxavpok.mods.decraft.client.update.VersionChecker;
import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;
import org.jglrxavpok.mods.decraft.stats.ModAchievementList;


public class ClientProxy extends CommonProxy
{

	@Override
	public void preInit()
	{
		super.preInit();

		// initialize the configuration
		ModConfiguration.clientPreInit();
	}

	@Override
	public void init()
	{
		super.init();

		// initialize the achievement event handlers
		ModAchievementList.clientInit();
	}

	@Override
	public void postInit()
	{
		super.postInit();

		// initalize the version checker
		VersionChecker.clientPostInit();
	}

}
