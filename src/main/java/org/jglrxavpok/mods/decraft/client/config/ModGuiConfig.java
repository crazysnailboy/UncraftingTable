package org.jglrxavpok.mods.decraft.client.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;

import cpw.mods.fml.client.config.ConfigGuiType;
import cpw.mods.fml.client.config.DummyConfigElement;
import cpw.mods.fml.client.config.DummyConfigElement.DummyCategoryElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.client.config.GuiConfigEntries.CategoryEntry;
import cpw.mods.fml.client.config.GuiConfigEntries.NumberSliderEntry;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionSlider;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
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

    	// 
		list.add(new DummyCategoryElement("updateConfigDummyElement", "uncrafting.options.updates", CategoryEntryUpdates.class));
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
    
    
	public static class CategoryEntryUpdates extends CategoryEntry
	{

		public CategoryEntryUpdates(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) 
		{
			super(owningScreen, owningEntryList, configElement);
		}    
		
		@Override
		protected GuiScreen buildChildScreen()
		{
	    	Configuration configuration = ModConfiguration.getConfig();
	        ConfigElement configurationCategory = new ConfigElement(configuration.getCategory(ModConfiguration.CATEGORY_UPDATES));
	        List<IConfigElement> propertiesOnThisScreen = configurationCategory.getChildElements();
	        String windowTitle = I18n.format("uncrafting.options.updates");
	        
	        return new GuiConfig(this.owningScreen, propertiesOnThisScreen,
				this.owningScreen.modID,
				ModConfiguration.CATEGORY_UPDATES,
				this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart,
				this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart,
				windowTitle
			);
	        
		}
	}
    

}