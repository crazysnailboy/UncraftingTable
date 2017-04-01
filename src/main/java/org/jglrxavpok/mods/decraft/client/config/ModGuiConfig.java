package org.jglrxavpok.mods.decraft.client.config;

import java.util.List;

import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiConfigEntries.CategoryEntry;
import net.minecraftforge.fml.client.config.IConfigElement;


public class ModGuiConfig extends GuiConfig
{
	public ModGuiConfig(GuiScreen parent)
	{
		super(parent,
 			getConfigElements(),
			ModUncrafting.MODID,
			false,
			false,
			ModUncrafting.MODNAME
		);
	}



	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static List<IConfigElement> getConfigElements()
	{
		Configuration config = ModConfiguration.getConfig();

		// top level settings
		List<IConfigElement> list = new ConfigElement(config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements();

		// second level settings
		list.add(new DummyCategoryElement("nuggetsConfigDummyElement", "uncrafting.options.nuggets", CategoryEntryNuggets.class));

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


	public static class CategoryEntryNuggets extends CategoryEntry
	{

		public CategoryEntryNuggets(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement)
		{
			super(owningScreen, owningEntryList, configElement);
		}

		@Override
		protected GuiScreen buildChildScreen()
		{
			Configuration configuration = ModConfiguration.getConfig();
			ConfigElement configurationCategory = new ConfigElement(configuration.getCategory(ModConfiguration.CATEGORY_NUGGETS));
			List<IConfigElement> propertiesOnThisScreen = configurationCategory.getChildElements();
			String windowTitle = I18n.format("uncrafting.options.nuggets");

			return new GuiConfig(this.owningScreen, propertiesOnThisScreen,
				this.owningScreen.modID,
				ModConfiguration.CATEGORY_NUGGETS,
				this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart,
				this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart,
				windowTitle
			);

		}
	}

}