package org.jglrxavpok.mods.decraft;

import java.awt.Color;
import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import org.jglrxavpok.mods.decraft.ContainerUncraftingTable.UncraftingStatus;
import org.jglrxavpok.mods.decraft.client.GuiUncraftButton;
import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;
import org.jglrxavpok.mods.decraft.network.UncraftingRequest;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class GuiUncraftingTable extends GuiContainer {

    private final static Color darkGreen = new Color(75, 245, 75);
    private final GuiUncraftButton uncraftButton;
    public ContainerUncraftingTable container;
    private String blockName;
    private World worldObj;
    private EntityPlayer player;

    public GuiUncraftingTable(InventoryPlayer playerInventory, World world, String blockName)
    {
    	super(new ContainerUncraftingTable(playerInventory, world));
    	
        container = (ContainerUncraftingTable)inventorySlots;
        this.blockName = blockName;
        this.worldObj = world;
        this.player = playerInventory.player;

        // avoid recreating the same object over and over when resizing
        uncraftButton = new GuiUncraftButton(0, 0, 0);
        uncraftButton.enabled = false;
    }

    @Override
    public void initGui()
    {
        super.initGui();

        int offsetX = 70;
        int offsetY = 27;
        float scale = xSize/176f; // 176 is the width of the background image
        float x = (this.width - this.xSize) / 2f + offsetX * scale;
        float y = (this.height - this.ySize) / 2f + offsetY * scale;
        uncraftButton.xPosition = (int)x;
        uncraftButton.yPosition = (int)y;
        addButton(uncraftButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if(button.id == 0) {
            // send uncrafting packet
            ItemStack toUncraft = container.getUncraftSlot().getStack();
            ItemStack book = container.getBookSlot().getStack();
            UncraftingRequest request = new UncraftingRequest(toUncraft, book);
            ModUncrafting.instance.getNetwork().sendToServer(request);
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        ItemStack toUncraft = container.getUncraftSlot().getStack();
        uncraftButton.enabled = !toUncraft.func_190926_b();
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
        fontRendererObj.drawString(blockName, xSize / 2 - fontRendererObj.getStringWidth(blockName) / 2 + 1, 5, 4210752);

        // write "inventory" above the player inventory
        fontRendererObj.drawString(I18n.format("container.inventory"), 6, ySize - 96 + 2, 4210752);

        // write "compute:" above the input slots
        String compute = I18n.format("uncrafting.compute") + ":";
        fontRendererObj.drawString(TextFormatting.GRAY + compute + TextFormatting.RESET, 24 - fontRendererObj.getStringWidth(compute) / 2, 21, 0, true);

        // write the xp cost above the arrow
        fontRendererObj.drawString(TextFormatting.UNDERLINE + "" + (ModConfiguration.standardLevel + container.uncraftingCost) + " levels" + TextFormatting.RESET, xSize / 2 - fontRendererObj.getStringWidth((ModConfiguration.standardLevel + container.uncraftingCost) + " levels") / 2, ySize - 127 - 20, darkGreen.getRGB(), true);
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        this.mc.renderEngine.bindTexture(new ResourceLocation(ModUncrafting.MODID + ":textures/gui/container/uncrafting_gui.png"));
        int x = this.width / 2 - this.xSize / 2;
        int y = this.height / 2 - this.ySize / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
    }

}
