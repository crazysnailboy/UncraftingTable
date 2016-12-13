package org.jglrxavpok.mods.decraft.client.config;

import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.util.Set;


public class ModGuiFactory implements IModGuiFactory 
{
    @Override
    public void initialize(Minecraft minecraftInstance) 
    {
 
    }
 
    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() 
    {
        return ModGuiConfig.class;
    }
 
    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() 
    {
        return null;
    }
 
    /**
     * Responsible for painting the mod specific section of runtime options GUI for a particular category
     *
     * @author cpw
     *
     * TODO remove in 1.11 - this was never fully implemented and will be removed
     */
    @SuppressWarnings("deprecation")
	@Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) 
    {
        return null;
    }
}
