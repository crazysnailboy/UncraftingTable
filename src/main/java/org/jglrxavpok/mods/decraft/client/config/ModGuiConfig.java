package org.jglrxavpok.mods.decraft.client.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;

import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.config.GuiConfigEntries.NumberSliderEntry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionSlider;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;


public class ModGuiConfig extends GuiConfig
{
	public ModGuiConfig(GuiScreen parent) 
	{
		super(parent, 
 			getConfigElements(), 
			ModUncrafting.MODID, 
			false, 
			false, 
			"Uncrafting Table"
		);
	}
	
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static List<IConfigElement> getConfigElements()
	{
		Configuration config = ModConfiguration.getConfig();
		
		// top level settings
		List<IConfigElement> list = new ConfigElement(config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements();
		
		return list;
	}
	
	
	@Override
	public void initGui()
	{
		super.initGui();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		super.actionPerformed(button);
	}

}