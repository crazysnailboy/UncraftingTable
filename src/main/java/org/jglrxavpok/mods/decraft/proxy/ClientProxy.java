package org.jglrxavpok.mods.decraft.proxy;

import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;
import org.jglrxavpok.mods.decraft.stat.ModAchievements;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(){
		super.preInit();
		ModConfiguration.clientPreInit();
	}
	
	@Override
	public void init(){
		super.init();
		ModAchievements.clientInit();
	}
	
	@Override
	public void postInit(){
		super.postInit();
	}

}
