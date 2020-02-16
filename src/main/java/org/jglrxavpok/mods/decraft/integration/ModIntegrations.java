package org.jglrxavpok.mods.decraft.integration;

import net.minecraftforge.fml.ModList;

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
			// FIXME: do later
		}
	}

}
