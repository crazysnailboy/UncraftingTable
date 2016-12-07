package org.jglrxavpok.mods.decraft.proxy;

import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;

public class CommonProxy {
	
	/**
	 * Run before anything else. Read your config, create blocks, items, etc, and register them with the GameRegistry
	 */
	public void preInit(){
	    ModConfiguration.preInit();
	}
	
	/**
	 * Do your mod setup. Build whatever data structures you care about. Register recipes,
	 * send FMLInterModComms messages to other mods.
	 */
	public void init(){
	}
	
	/**
	 * Handle interaction with other mods, complete your setup based on this.
	 */
	public void postInit(){
	}

}
