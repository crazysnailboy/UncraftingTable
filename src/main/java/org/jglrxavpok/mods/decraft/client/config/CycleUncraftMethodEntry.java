package org.jglrxavpok.mods.decraft.client.config;

import cpw.mods.fml.client.config.GuiConfigEntries.ButtonEntry;

import java.util.Map;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.IConfigElement;
import net.minecraft.client.resources.I18n;

//public class CycleUncraftMethodEntry extends cpw.mods.fml.client.config.GuiConfigEntries.CycleValueEntry {
	
public class CycleUncraftMethodEntry extends cpw.mods.fml.client.config.GuiConfigEntries.ButtonEntry
{
	
    protected final int beforeIndex;
    protected final int defaultIndex;
    protected int       currentIndex;

    public CycleUncraftMethodEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement<String> configElement)
    {
        super(owningScreen, owningEntryList, configElement);

        beforeIndex = Integer.valueOf((String)configElement.get());
        defaultIndex = Integer.valueOf((String)configElement.getDefault());
        currentIndex = beforeIndex;
        this.btnValue.enabled = enabled();
        updateValueButtonText();
    }


    @Override
    public void updateValueButtonText()
    {
    	this.btnValue.displayString = configElement.getValidValues()[currentIndex];
    }

    @Override
    public void valueButtonPressed(int slotIndex)
    {
        if (enabled())
        {
            if (++this.currentIndex >= configElement.getValidValues().length)
                this.currentIndex = 0;
            updateValueButtonText();
        }
    }

    @Override
    public boolean isDefault()
    {
        return currentIndex == defaultIndex;
    }

    @Override
    public void setToDefault()
    {
        if (enabled())
        {
            currentIndex = defaultIndex;
            updateValueButtonText();
        }
    }

    @Override
    public boolean isChanged()
    {
        return currentIndex != beforeIndex;
    }

    @Override
    public void undoChanges()
    {
        if (enabled())
        {
            currentIndex = beforeIndex;
            updateValueButtonText();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean saveConfigElement()
    {
        if (enabled() && isChanged())
        {
            configElement.set(currentIndex);
            //configElement.set(configElement.getValidValues()[currentIndex]);
            return configElement.requiresMcRestart();
        }
        return false;
    }

    @Override
    public String getCurrentValue()
    {
    	return configElement.get().toString();
        //return configElement.getValidValues()[currentIndex];
    }

    @Override
    public String[] getCurrentValues()
    {
        return new String[] { getCurrentValue() };
    }
}
