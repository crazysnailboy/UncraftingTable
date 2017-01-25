package org.jglrxavpok.mods.decraft.client.config;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.GuiConfigEntries.ArrayEntry;
import cpw.mods.fml.client.config.GuiConfigEntries.ButtonEntry;
import cpw.mods.fml.client.config.GuiUtils;
import cpw.mods.fml.client.config.IConfigElement;
import net.minecraft.client.resources.I18n;


public class ModGuiConfigEntries 
{

	public static class ExcludedItemsArrayEntry extends ArrayEntry 
	{

		public ExcludedItemsArrayEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) 
		{
			super(owningScreen, owningEntryList, configElement);
		}
		
		@Override
		public void updateValueButtonText()
		{
			this.btnValue.displayString = currentValues.length + " item(s)";
		}
		
	}
	
	
	public static class BooleanEntry extends ButtonEntry
	{
		
		protected final boolean beforeValue;
		protected boolean currentValue;

		public BooleanEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement<Boolean> configElement)
		{
			super(owningScreen, owningEntryList, configElement);
			this.beforeValue = Boolean.valueOf(configElement.get().toString());
			this.currentValue = beforeValue;
			this.btnValue.enabled = enabled();
			updateValueButtonText();
		}

		@Override
		public void updateValueButtonText()
		{
			this.btnValue.displayString = I18n.format(String.valueOf(currentValue));
			btnValue.packedFGColour = currentValue ? GuiUtils.getColorCode('2', true) : GuiUtils.getColorCode('4', true);
		}

		@Override
		public void valueButtonPressed(int slotIndex)
		{
			if (enabled()) currentValue = !currentValue;
		}

		@Override
		public boolean isDefault()
		{
			return currentValue == Boolean.valueOf(configElement.getDefault().toString());
		}

		@Override
		public void setToDefault()
		{
			if (enabled())
			{
				currentValue = Boolean.valueOf(configElement.getDefault().toString());
				updateValueButtonText();
			}
		}

		@Override
		public boolean isChanged()
		{
			return currentValue != beforeValue;
		}

		@Override
		public void undoChanges()
		{
			if (enabled())
			{
				currentValue = beforeValue;
				updateValueButtonText();
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean saveConfigElement()
		{
			if (enabled() && isChanged())
			{
				configElement.set(currentValue);
				return configElement.requiresMcRestart();
			}
			return false;
		}

		@Override
		public Boolean getCurrentValue()
		{
			return currentValue;
		}

		@Override
		public Boolean[] getCurrentValues()
		{
			return new Boolean[] { getCurrentValue() };
		}
	}
	
	
	public static class UncraftingMethodCycleEntry extends ButtonEntry
	{
		
		protected final int beforeIndex;
		protected final int defaultIndex;
		protected int currentIndex;

		public UncraftingMethodCycleEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement<String> configElement)
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
				if (++this.currentIndex >= configElement.getValidValues().length) this.currentIndex = 0;
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
				return configElement.requiresMcRestart();
			}
			return false;
		}

		@Override
		public String getCurrentValue()
		{
			return configElement.get().toString();
		}

		@Override
		public String[] getCurrentValues()
		{
			return new String[] { getCurrentValue() };
		}
	}
	
}
