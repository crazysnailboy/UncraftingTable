package org.jglrxavpok.mods.decraft.client.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jglrxavpok.mods.decraft.ModUncrafting;

import cpw.mods.fml.client.config.ConfigGuiType;
import cpw.mods.fml.client.config.DummyConfigElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.client.config.GuiConfigEntries.NumberSliderEntry;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionSlider;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

public class UncraftingGuiConfig extends GuiConfig
{
    public UncraftingGuiConfig(GuiScreen parent) 
    {
//    	super(parent, 
//			new ConfigElement(ModUncrafting.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), 
//			ModUncrafting.MODID, 
//			false, 
//			false, 
//			"Uncrafting Table"
//		);
    	
    	super(parent, 
 			getConfigElements(), 
			ModUncrafting.MODID, 
			false, 
			false, 
			"Uncrafting Table"
		);
    	
		//titleLine2 = MagicBeans.configFile.getAbsolutePath();
    }
    
    
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static List<IConfigElement> getConfigElements()
    {
//        List<IConfigElement> list = new ArrayList<IConfigElement>();
    	
//		list.add(new ConfigElement<Integer>(ModUncrafting.config.get(Configuration.CATEGORY_GENERAL, "standardLevel", 5)));
//		list.add(new ConfigElement<Integer>(ModUncrafting.config.get(Configuration.CATEGORY_GENERAL, "maxUsedLevel", 30)));
//		list.add(new ConfigElement<Integer>(ModUncrafting.config.get(Configuration.CATEGORY_GENERAL, "uncraftMethod", 0)));
          	
    	
    	List<IConfigElement> list = new ConfigElement(ModUncrafting.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements();
                
        return list;
    }
    
    
    @Override
    public void initGui()
    {
        // You can add buttons and initialize fields here
        super.initGui();
        
    }

    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        // You can do things like create animations, draw additional elements, etc. here
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        // You can process any additional buttons you may have added here
        super.actionPerformed(button);
    }

}