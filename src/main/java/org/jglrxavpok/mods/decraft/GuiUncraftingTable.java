package org.jglrxavpok.mods.decraft;

import java.awt.*;

import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.client.resources.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

import org.jglrxavpok.mods.decraft.ContainerUncraftingTable.State;
import org.lwjgl.input.*;
import org.lwjgl.opengl.*;

public class GuiUncraftingTable extends GuiContainer
{

    public ContainerUncraftingTable container;
    private String blockName;
    private boolean inverted;
    private World worldObj;
    private int x;
    private int z;
    private int y;
    private EntityPlayer player;

    public GuiUncraftingTable(InventoryPlayer playerInventory, World world, String blockName, boolean inverted, int x, int y, int z, int min, int max)
    {
        super(new ContainerUncraftingTable(playerInventory, world, inverted, x, y, z, min, max));
        container = (ContainerUncraftingTable) inventorySlots;
        this.blockName = blockName;
        this.inverted = inverted;
        this.worldObj = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.player = playerInventory.player;
    }

//    public void actionPerformed(GuiButton button)
//    {
//    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);
        boolean op = false;
        if (Keyboard.isKeyDown(Keyboard.KEY_O) && op)
        {
        	this.player.openGui(ModUncrafting.instance, UnGuiHandler.GUI_OPTIONS, this.worldObj, this.x, this.y, this.z);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        GL11.glDisable(GL11.GL_LIGHTING);
        int xSize = this.xSize;
        int ySize = this.ySize;
        
        if (!inverted)
        {
            fontRendererObj.drawString(blockName, xSize / 2 - fontRendererObj.getStringWidth(blockName) / 2 + 1, 5, 4210752);
            fontRendererObj.drawString(I18n.format("container.inventory"), 6, ySize - 96 + 2, 4210752);

            Color darkGreen = new Color(75, 245, 75);
            String string1 = I18n.format("uncrafting.compute") + ":";
            
            fontRendererObj.drawString(EnumChatFormatting.DARK_GRAY + string1 + EnumChatFormatting.RESET, 24 - fontRendererObj.getStringWidth(string1) / 2 + 1, 22, 0);
            fontRendererObj.drawString(EnumChatFormatting.GRAY + string1 + EnumChatFormatting.RESET, 24 - fontRendererObj.getStringWidth(string1) / 2, 21, 0);
            fontRendererObj.drawString(EnumChatFormatting.DARK_GRAY + "" + EnumChatFormatting.UNDERLINE + "" + (ModUncrafting.standardLevel + container.xp) + " levels" + EnumChatFormatting.RESET, xSize / 2 - fontRendererObj.getStringWidth((ModUncrafting.standardLevel + container.xp) + " levels") / 2 + 1, ySize - 126 - 10, 0);
            fontRendererObj.drawString(EnumChatFormatting.UNDERLINE + "" + (ModUncrafting.standardLevel + container.xp) + " levels" + EnumChatFormatting.RESET, xSize / 2 - fontRendererObj.getStringWidth((ModUncrafting.standardLevel + container.xp) + " levels") / 2, ySize - 127 - 10, darkGreen.getRGB());

            String string = container.result;
            if (string != null)
            {
                State msgType = container.type;
                EnumChatFormatting format = EnumChatFormatting.GREEN;
                EnumChatFormatting shadowFormat = EnumChatFormatting.DARK_GRAY;
                if (msgType == ContainerUncraftingTable.State.ERROR)
                {
                    format = EnumChatFormatting.WHITE;
                    shadowFormat = EnumChatFormatting.DARK_RED;
                }
                fontRendererObj.drawString(shadowFormat + string + EnumChatFormatting.RESET, 6 + 1, ySize - 95 + 2 - fontRendererObj.FONT_HEIGHT, 0);
                fontRendererObj.drawString(format + string + EnumChatFormatting.RESET, 6, ySize - 96 + 2 - fontRendererObj.FONT_HEIGHT, 0);
            }
        }
        else
        {
            int height = 166 - 8;
            
            fontRendererObj.drawString(blockName, xSize / 2 - fontRendererObj.getStringWidth(blockName) / 2 + 1, height - 5, 4210752);
            fontRendererObj.drawString(I18n.format("container.inventory"), 6, height - ySize - 96 + 2, 4210752);

            Color darkGreen = new Color(75, 245, 75);
            String string1 = "Calculs:";
            
            fontRendererObj.drawString(EnumChatFormatting.DARK_GRAY + string1 + EnumChatFormatting.RESET, 24 - fontRendererObj.getStringWidth(string1) / 2 + 1, height - 22, 0);
            fontRendererObj.drawString(EnumChatFormatting.GRAY + string1 + EnumChatFormatting.RESET, 24 - fontRendererObj.getStringWidth(string1) / 2, height - 21, 0);
            fontRendererObj.drawString(EnumChatFormatting.DARK_GRAY + "" + EnumChatFormatting.UNDERLINE + "" + (ModUncrafting.standardLevel + container.xp) + " levels" + EnumChatFormatting.RESET, xSize / 2 - fontRendererObj.getStringWidth((ModUncrafting.standardLevel + container.xp) + " levels") / 2 + 1, height - (ySize - 126 - 10), 0);
            fontRendererObj.drawString(EnumChatFormatting.UNDERLINE + "" + (ModUncrafting.standardLevel + container.xp) + " levels" + EnumChatFormatting.RESET, xSize / 2 - fontRendererObj.getStringWidth((ModUncrafting.standardLevel + container.xp) + " levels") / 2, height - (ySize - 127 - 10), darkGreen.getRGB());

            String string = container.result;
            if (string != null)
            {
                State msgType = container.type;
                EnumChatFormatting format = EnumChatFormatting.GREEN;
                EnumChatFormatting shadowFormat = EnumChatFormatting.DARK_GRAY;
                if (msgType == ContainerUncraftingTable.State.ERROR)
                {
                    format = EnumChatFormatting.WHITE;
                    shadowFormat = EnumChatFormatting.DARK_RED;
                }
                fontRendererObj.drawString(shadowFormat + string + EnumChatFormatting.RESET, 6 + 1, height - (ySize - 95 + 2 - fontRendererObj.FONT_HEIGHT), 0);
                fontRendererObj.drawString(format + string + EnumChatFormatting.RESET, 6, height - (ySize - 96 + 2 - fontRendererObj.FONT_HEIGHT), 0);
            }
        }
        boolean op = false;
        String optionsText = I18n.format("uncrafting.options.hit");
        if (op) {
            fontRendererObj.drawString(EnumChatFormatting.UNDERLINE + optionsText, xSize - fontRendererObj.getStringWidth(optionsText) - 4, ySize - 96 + 2, 0);
        }
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GL11.glPushMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        if (this.inverted) {
            this.mc.renderEngine.bindTexture(new ResourceLocation("uncraftingTable:textures/gui/container/uncrafting_gui_redstoned.png"));
        } else {
            this.mc.renderEngine.bindTexture(new ResourceLocation("uncraftingTable:textures/gui/container/uncrafting_gui.png"));
        }
        int k = this.width / 2 - this.xSize / 2;
        int l = this.height / 2 - this.ySize / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
        GL11.glPopMatrix();
    }

}
