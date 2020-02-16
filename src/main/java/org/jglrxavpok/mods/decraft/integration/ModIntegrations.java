package org.jglrxavpok.mods.decraft.integration;

import net.minecraftforge.fml.ModList;
import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.integration.crafttweaker.CraftTweakerIntegration;


public class ModIntegrations
{

	public static void registerIntegrations()
	{
		registerCraftTweakerIntegration();
	}


	private static void registerCraftTweakerIntegration()
	{
		if (ModList.get().isLoaded("crafttweaker"))
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
