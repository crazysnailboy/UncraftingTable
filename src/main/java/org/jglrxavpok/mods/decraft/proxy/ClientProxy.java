package org.jglrxavpok.mods.decraft.proxy;

import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(){
		super.preInit();
		ModConfiguration.clientPreInit();
	}
	
	@Override
	public void init(){
		super.init();
	}
	
	@Override
	public void postInit(){
		super.postInit();
	}

}
