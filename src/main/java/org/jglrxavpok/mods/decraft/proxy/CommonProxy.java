package org.jglrxavpok.mods.decraft.proxy;

import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.UncraftingManager;
import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;
import org.jglrxavpok.mods.decraft.stat.ModAchievements;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.stats.Achievement;

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
		ModAchievements.init();
	}
	
	/**
	 * Handle interaction with other mods, complete your setup based on this.
	 */
	public void postInit(){
		UncraftingManager.postInit();
	}

}
