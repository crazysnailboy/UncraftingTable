package org.jglrxavpok.mods.decraft.client.config;

import org.jglrxavpok.mods.decraft.ModUncrafting;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.DefaultGuiFactory;


public class ModGuiFactory extends DefaultGuiFactory
{

	public ModGuiFactory()
	{
		super(ModUncrafting.MODID, ModUncrafting.NAME);
	}

	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen)
	{
		return new ModGuiConfig(parentScreen);
	}


}
