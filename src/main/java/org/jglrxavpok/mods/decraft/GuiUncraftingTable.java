package org.jglrxavpok.mods.decraft;

import java.awt.Color;

import org.jglrxavpok.mods.decraft.ContainerUncraftingTable.UncraftingStatus;
import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class GuiUncraftingTable extends GuiContainer
{
    private static final ResourceLocation UNCRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation(ModUncrafting.MODID + ":textures/gui/container/uncrafting_gui.png");

    public ContainerUncraftingTable container;
    private World worldObj;
    private EntityPlayer player;

    public GuiUncraftingTable(InventoryPlayer playerInventory, World world)
    {
    	super(new ContainerUncraftingTable(playerInventory, world));
    	
        container = (ContainerUncraftingTable)inventorySlots;
        this.worldObj = world;
        this.player = playerInventory.player;
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        GL11.glDisable(GL11.GL_LIGHTING);
        int xSize = this.xSize;
        int ySize = this.ySize;
        
    	// fontRendererObj.drawString:
    	// Args: string, x, y, color, dropShadow
    	
    	
    	// render the block name at the top of the gui
        String blockName = I18n.format("tile.uncrafting_table.name");
        fontRendererObj.drawString(blockName, xSize / 2 - fontRendererObj.getStringWidth(blockName) / 2 + 1, 5, 4210752);
        
        // write "inventory" above the player inventory
        fontRendererObj.drawString(I18n.format("container.inventory"), 6, ySize - 96 + 2, 4210752);

        // write "compute:" above the input slots
        String compute = I18n.format("uncrafting.compute") + ":";
        fontRendererObj.drawString(TextFormatting.DARK_GRAY + compute + TextFormatting.RESET, 24 - fontRendererObj.getStringWidth(compute) / 2 + 1, 22, 0);
        fontRendererObj.drawString(TextFormatting.GRAY + compute + TextFormatting.RESET, 24 - fontRendererObj.getStringWidth(compute) / 2, 21, 0);
        
        // write the xp cost above the arrow
        Color darkGreen = new Color(75, 245, 75);
        fontRendererObj.drawString(TextFormatting.DARK_GRAY + "" + TextFormatting.UNDERLINE + "" + (ModConfiguration.standardLevel + container.uncraftingCost) + " levels" + TextFormatting.RESET, xSize / 2 - fontRendererObj.getStringWidth((ModConfiguration.standardLevel + container.uncraftingCost) + " levels") / 2 + 1, ySize - 126 - 10, 0);
        fontRendererObj.drawString(TextFormatting.UNDERLINE + "" + (ModConfiguration.standardLevel + container.uncraftingCost) + " levels" + TextFormatting.RESET, xSize / 2 - fontRendererObj.getStringWidth((ModConfiguration.standardLevel + container.uncraftingCost) + " levels") / 2, ySize - 127 - 10, darkGreen.getRGB());

        
        String string = container.uncraftingStatusText;
        if (string != null)
        {
            UncraftingStatus msgType = container.uncraftingStatus;
            TextFormatting format = TextFormatting.GREEN;
            TextFormatting shadowFormat = TextFormatting.DARK_GRAY;
            if (msgType == ContainerUncraftingTable.UncraftingStatus.ERROR)
            {
                format = TextFormatting.WHITE;
                shadowFormat = TextFormatting.DARK_RED;
            }
            fontRendererObj.drawString(shadowFormat + string + TextFormatting.RESET, 6 + 1, ySize - 95 + 2 - fontRendererObj.FONT_HEIGHT, 0);
            fontRendererObj.drawString(format + string + TextFormatting.RESET, 6, ySize - 96 + 2 - fontRendererObj.FONT_HEIGHT, 0);
        }

        GL11.glEnable(GL11.GL_LIGHTING);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GL11.glPushMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        this.mc.getTextureManager().bindTexture(UNCRAFTING_TABLE_GUI_TEXTURES);

        int k = this.width / 2 - this.xSize / 2;
        int l = this.height / 2 - this.ySize / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
        GL11.glPopMatrix();
    }

}
