package org.jglrxavpok.mods.decraft.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.jglrxavpok.mods.decraft.ModUncrafting;

public class GuiUncraftButton extends GuiButton {

    public static final ResourceLocation buttonTexture = new ResourceLocation(ModUncrafting.MODID, "textures/gui/uncraft_button.png");

    public GuiUncraftButton(int id, int x, int y) {
        super(id, x, y, 32, 32, "<no text>");
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        if (!visible)
        {
            return;
        }
        float minU = 0f;
        float minV = 0f;
        if(!enabled) {
            minU = 0.5f;
        } else if(isMouseOver()) {
                minV = 0.5f;
        }
        float maxU = minU + 0.5f;
        float maxV = minV + 0.5f;
        mc.renderEngine.bindTexture(buttonTexture);
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(xPosition, yPosition+height, 0).tex(minU, maxV).endVertex();
        vertexbuffer.pos(xPosition+width, yPosition+height, 0).tex(maxU, maxV).endVertex();
        vertexbuffer.pos(xPosition+width, yPosition, 0).tex(maxU, minV).endVertex();
        vertexbuffer.pos(xPosition, yPosition, 0).tex(minU, minV).endVertex();
        tessellator.draw();
    }
}
