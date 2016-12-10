package org.jglrxavpok.mods.decraft.client.config;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.IConfigElement;

public class UncraftableItemsArrayEntry extends cpw.mods.fml.client.config.GuiConfigEntries.ArrayEntry {

	public UncraftableItemsArrayEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
		super(owningScreen, owningEntryList, configElement);
	}
	
    @Override
    public void updateValueButtonText()
    {
        this.btnValue.displayString = currentValues.length + " item(s)";
    }
	

}
