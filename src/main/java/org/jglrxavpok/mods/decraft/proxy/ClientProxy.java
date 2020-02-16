package org.jglrxavpok.mods.decraft.proxy;

import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jglrxavpok.mods.decraft.client.gui.inventory.GuiUncraftingTable;
import org.jglrxavpok.mods.decraft.init.ModContainers;

public class ClientProxy extends CommonProxy
{

	public static void setup(FMLClientSetupEvent event)
	{
		ScreenManager.registerFactory(ModContainers.UNCRAFTING_CONTAINER, GuiUncraftingTable::new);
	}

	@Override
	public void preInit()
	{
		super.preInit();
	}

	@Override
	public void init()
	{
		super.init();
	}

	@Override
	public void postInit()
	{
		super.postInit();
	}

}