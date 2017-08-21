package org.jglrxavpok.mods.decraft.proxy;

import org.jglrxavpok.mods.decraft.client.update.VersionChecker;
import org.jglrxavpok.mods.decraft.stats.ModAchievementList;


public class ClientProxy extends CommonProxy
{

	@Override
	public void preInit()
	{
		super.preInit();
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

		// initialize the version checker
		VersionChecker.clientPostInit();
	}

}
