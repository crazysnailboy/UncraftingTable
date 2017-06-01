package org.jglrxavpok.mods.decraft.proxy;

import org.jglrxavpok.mods.decraft.init.ModBlocks;
import org.jglrxavpok.mods.decraft.init.ModItems;


public class ClientProxy extends CommonProxy
{

	@Override
	public void preInit()
	{
		super.preInit();

		this.registerItemInventoryModels();
	}

	@Override
	public void init()
	{
		super.init();

		this.registerBlockInventoryModels();
	}

	@Override
	public void postInit()
	{
		super.postInit();
	}


	private void registerBlockInventoryModels()
	{
		ModBlocks.registerInventoryModels();
	}

	private void registerItemInventoryModels()
	{
		ModItems.registerInventoryModels();
	}

}
