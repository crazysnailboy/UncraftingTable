package org.jglrxavpok.mods.decraft.proxy;

import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;
import org.jglrxavpok.mods.decraft.stats.ModAchievements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;

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
		
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(
			Item.getItemFromBlock(ModUncrafting.uncraftingTable), 0, 
			new ModelResourceLocation(ModUncrafting.uncraftingTable.getRegistryName().toString(), "inventory")
		);
	}
	
	@Override
	public void postInit(){
		super.postInit();
	}

}
