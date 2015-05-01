package org.jglrxavpok.mods.decraft;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.client.config.GuiSlider;
import cpw.mods.fml.client.config.GuiSlider.ISlider;

import org.lwjgl.opengl.GL11;

public class GuiUncraftOptions extends GuiScreen implements ISlider
{

    GuiSlider         maxLevel;
    private GuiSlider minLevel;

    public GuiUncraftOptions()
    {
        super();
    }

    public void initGui()
    {
        String methodName = I18n.format("uncrafting.options.method.jglr.switchto");
        if(methodName == null || "uncrafting.options.method.jglr.switchto".equals(methodName))
        {
            methodName = "Switch to jglrxavpok's uncrafting method";
        }
        GuiButton uncraftMethod0 = new GuiButton(0, width / 2 - 250 / 2, 40, 250, 20, methodName);
        buttonList.add(uncraftMethod0);
        methodName = I18n.format("uncrafting.options.method.xell75zenen.switchto");
        if(methodName == null || "uncrafting.options.method.xell75zenen.switchto".equals(methodName))
        {
            methodName = "Switch to Xell75 and zenen's uncrafting method";
        }
        GuiButton uncraftMethod1 = new GuiButton(1, width / 2 - 250 / 2, 70, 250, 20, methodName);
        buttonList.add(uncraftMethod1);

        minLevel = new GuiSlider(2, width / 2 - 250 / 2 - 150 / 2, 175, 150, 20, "Min Level: ", "", 0, 50, ModUncrafting.instance.minLvlServer, true, true, this);
        minLevel.precision = 0;
        maxLevel = new GuiSlider(3, width / 2 + 250 / 2 - 150 + 150 / 2, 175, 150, 20, "Max Level: ", "", 0, 50, ModUncrafting.instance.maxLvlServer, true, true, this);
        maxLevel.precision = 0;

        minLevel.updateSlider();
        maxLevel.updateSlider();

        buttonList.add(maxLevel);
        buttonList.add(minLevel);
    }

    public void drawScreen(int par1, int par2, float f)
    {
        this.drawBackground(0);
        if(ModUncrafting.instance.uncraftMethod == 0)
        {
            maxLevel.visible = false;
        }
        else
        {
            maxLevel.visible = true;
        }
        String methodName = null;
        String methodImg = null;
        if(ModUncrafting.instance.uncraftMethod == 0)
        {
            methodName = I18n.format("uncrafting.options.method.jglr");
            methodImg = "jglrxavpoksmethod";
        }
        else
        {
            methodName = I18n.format("uncrafting.options.method.xell75zenen");
            methodImg = "Xell75s&zenens";
        }

        if(methodImg != null)
        {
            this.mc.renderEngine.bindTexture(new ResourceLocation("uncraftingTable:textures/gui/" + methodImg + ".png"));
            GL11.glPushMatrix();
            GL11.glScalef(0.5f, 0.25f, 1);
            this.drawTexturedModalRect(width - 125, 190 * 2 + 50, 0, 0, 255, 250);
            GL11.glPopMatrix();
        }

        super.drawScreen(par1, par2, f);
        String optionsLabel = I18n.format("uncrafting.options");
        fontRendererObj.drawString(EnumChatFormatting.WHITE + optionsLabel, width / 2 - fontRendererObj.getStringWidth(optionsLabel) / 2, 15, 0);

        String getBackText = I18n.format("uncrafting.options.getback");
        fontRendererObj.drawString(EnumChatFormatting.WHITE + getBackText, 1, 1, 0);

        String using = I18n.format("uncrafting.options.method.using");
        fontRendererObj.drawString(EnumChatFormatting.WHITE + using + ": " + EnumChatFormatting.GOLD + methodName, width / 2 - 250 / 2, 95, 0);

    }

    public void actionPerformed(GuiButton button)
    {
        if(button.id == 0)
        {
            ModUncrafting.instance.uncraftMethod = 0;
            ModUncrafting.instance.saveProperties();
        }
        else if(button.id == 1)
        {
            ModUncrafting.instance.uncraftMethod = 1;
            ModUncrafting.instance.saveProperties();
        }
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider)
    {
        if(slider == minLevel)
        {
            if(maxLevel.getValue() < slider.getValue())
            {
                maxLevel.setValue(slider.getValue());
                maxLevel.updateSlider();
            }
        }
        else if(slider == maxLevel)
        {
            if(minLevel.getValue() > slider.getValue())
            {
                minLevel.setValue(slider.getValue());
                minLevel.updateSlider();
            }
        }

        ModUncrafting.instance.minLvlServer = minLevel.getValueInt();
        ModUncrafting.instance.maxLvlServer = maxLevel.getValueInt();
    }
}
