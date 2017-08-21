package org.jglrxavpok.mods.decraft.integration;

import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.integration.crafttweaker.CraftTweakerIntegration;
import net.minecraftforge.fml.common.Loader;


public class ModIntegrations
{

	public static void registerIntegrations()
	{
		registerCraftTweakerIntegration();
	}


	private static void registerCraftTweakerIntegration()
	{
		if (Loader.isModLoaded("crafttweaker"))
		{
			try
			{
				CraftTweakerIntegration.register();
			}
			catch (Exception ex)
			{
				ModUncrafting.LOGGER.catching(ex);
			}
		}
	}

}
