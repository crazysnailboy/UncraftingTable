package org.jglrxavpok.mods.decraft.proxy;

import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;
import org.jglrxavpok.mods.decraft.item.ModItems;
import org.jglrxavpok.mods.decraft.stats.ModAchievements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(){
		super.preInit();
		ModConfiguration.clientPreInit();
		ModItems.clientPreInit();
	}
	
	@Override
	public void init(){
		super.init();
		ModAchievements.clientInit();
		
		Item item = Item.getItemFromBlock(ModUncrafting.uncraftingTable);
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(ModUncrafting.MODID + ":" + item.getUnlocalizedName().substring(5), "inventory"));
	}
	
	@Override
	public void postInit(){
		super.postInit();
	}

}
