package org.jglrxavpok.mods.decraft.client;

import java.awt.Color;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jglrxavpok.mods.decraft.ContainerUncraftingTable;
import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.UncraftingManager;
import org.jglrxavpok.mods.decraft.client.GuiUncraftButton;
import org.jglrxavpok.mods.decraft.network.UncraftingRequest;
import org.jglrxavpok.mods.decraft.network.UncraftingResult;
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
    private Item lastItem;
    private ContainerUncraftingTable container;
    private String blockName;
    private boolean wasReady;
    private int xpCost;
    private int lastItemCount;

    public GuiUncraftingTable(InventoryPlayer playerInventory, World world, String blockName)
    {
    	super(new ContainerUncraftingTable(playerInventory, world));
    	
        container = (ContainerUncraftingTable)inventorySlots;
        lastItem = container.getUncraftSlot().getStack().getItem();
        this.blockName = blockName;

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
            if(container.isReadyToUncraft()) {
                UncraftingRequest request = new UncraftingRequest(toUncraft, book);
                ModUncrafting.instance.getNetwork().sendToServer(request);
            }
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        ItemStack toUncraft = container.getUncraftSlot().getStack();
        if (container.isReadyToUncraft()) {
            if (lastItemCount != toUncraft.getCount() || lastItem != toUncraft.getItem() || !wasReady) { // if item was just put in the slot
                UncraftingResult result = UncraftingManager.uncraft(toUncraft, container.getBookSlot().getStack(), Minecraft.getMinecraft().player);
                xpCost = result.getRequiredExpLevels();
                uncraftButton.enabled = result.getResultType() == UncraftingResult.ResultType.VALID;
            }
            lastItem = toUncraft.getItem();
            lastItemCount = toUncraft.getCount();
        } else {
            xpCost = 0;
            uncraftButton.enabled = false;
        }
        wasReady = container.isReadyToUncraft();
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
        fontRendererObj.drawString(blockName, (xSize - fontRendererObj.getStringWidth(blockName)) / 2 + 1, 5, 4210752);

        // write "inventory" above the player inventory
        fontRendererObj.drawString(I18n.format("container.inventory"), 6, ySize - 96 + 2, 4210752);

        // write "compute:" above the input slots
        String compute = I18n.format("uncrafting.book") + ":";
        fontRendererObj.drawString(TextFormatting.GRAY + compute + TextFormatting.RESET, 24 - fontRendererObj.getStringWidth(compute) / 2, 21, 0, true);

        // write the xp cost above the arrow
        if(xpCost > 0)
            fontRendererObj.drawString(TextFormatting.UNDERLINE + "" + (xpCost) + " levels" + TextFormatting.RESET, (xSize - fontRendererObj.getStringWidth((xpCost) + " levels")) / 2, ySize - 127 - 20, darkGreen.getRGB(), true);
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
