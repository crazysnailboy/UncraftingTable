package org.jglrxavpok.mods.decraft.proxy;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.UncraftingManager;
import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;
import org.jglrxavpok.mods.decraft.network.UncraftingRequest;
import org.jglrxavpok.mods.decraft.network.UncraftingResult;
import org.jglrxavpok.mods.decraft.stats.ModAchievements;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy {
	
	/**
	 * Run before anything else. Read your config, create blocks, items, etc, and register them with the GameRegistry
	 */
	public void preInit(){
		
		// initialize the configuration
	    ModConfiguration.preInit();
	    
        // register the block
	    GameRegistry.register(ModUncrafting.uncraftingTable); //, ModUncrafting.uncraftingTable.getRegistryName());

	    ItemBlock itemBlock = new ItemBlock(ModUncrafting.uncraftingTable);
	    itemBlock.setRegistryName(ModUncrafting.uncraftingTable.getRegistryName());
	    
		GameRegistry.register(itemBlock);

		SimpleNetworkWrapper network = ModUncrafting.instance.getNetwork();
		network.registerMessage(UncraftingRequest.Handler.class, UncraftingRequest.class, 0, Side.SERVER);
		network.registerMessage(UncraftingResult.Handler.class, UncraftingResult.class, 1, Side.CLIENT);
	}
	
	/**
	 * Do your mod setup. Build whatever data structures you care about. Register recipes,
	 * send FMLInterModComms messages to other mods.
	 */
	public void init(){
		
        // create block crafting recipe
        GameRegistry.addShapedRecipe(new ItemStack(ModUncrafting.uncraftingTable), new Object[]
        {
            "SSS", "SXS", "SSS", 'X', Blocks.CRAFTING_TABLE, 'S', Blocks.COBBLESTONE
        });
        
        // initialize the achievements
		ModAchievements.init();
		
	}
	
	/**
	 * Handle interaction with other mods, complete your setup based on this.
	 */
	public void postInit(){
		UncraftingManager.postInit();
	}

}
