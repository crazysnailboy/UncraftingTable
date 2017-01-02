package org.jglrxavpok.mods.decraft;

import java.awt.Color;

import org.jglrxavpok.mods.decraft.ContainerUncraftingTable.UncraftingStatus;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class GuiUncraftingTable extends GuiContainer
{
    private static final ResourceLocation uncraftingTableGuiTextures = new ResourceLocation(ModUncrafting.MODID + ":textures/gui/container/uncrafting_table.png");

    public ContainerUncraftingTable container;
    private World worldObj;
    private EntityPlayer player;

    public GuiUncraftingTable(InventoryPlayer playerInventory, World world)
    {
    	super(new ContainerUncraftingTable(playerInventory, world));
        this.container = (ContainerUncraftingTable)inventorySlots;
        this.worldObj = world;
        this.player = playerInventory.player;
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    
    private void drawUncraftingStatusMessage()
    {
        // get a message to display based on the status of the container
        String statusMessage = "";
        switch (container.uncraftingStatus)
        {
        	// if the uncrafting status in "inactive", display no message
	        case INACTIVE: 
	        	break;
	        
        	// if the uncrafting status is "ready", display the xp cost for the operation
	        case READY: 
	        	statusMessage = I18n.format("container.uncrafting.cost", container.uncraftingCost);
	        	break;
	        
        	// if the uncrafting status is "error"...
	        case ERROR: 
	        	
	        	switch (container.uncraftingStatusReason)
	        	{
	        		// if the item cannot be uncrafted, display a message to that effect
		        	case NOT_UNCRAFTABLE:
		        		statusMessage = I18n.format("uncrafting.result.impossible");
		        		break;
		        		
		        	// if there are not enough items in the item stack, display a message to that effect
		        	case NOT_ENOUGH_ITEMS: 
		        		statusMessage = I18n.format("uncrafting.result.needMoreStacks", container.minStackSize);
		        		break;
		        		
	            	// if the player does not have enough xp, display the xp cost for the operation
		        	case NOT_ENOUGH_XP: 
			        	statusMessage = I18n.format("container.uncrafting.cost", container.uncraftingCost);
		        		break;
	        	}
	        	break;
        }
        
        // if there is a message to display, render it
        if (!statusMessage.equals(""))
        {        
        	int textX = 8;
        	int textY = ySize - 96 + 2 - fontRendererObj.FONT_HEIGHT - 4; // 60
        	
        	// *** copied from GuiRepair ***
        	// determine the text and shadow colours based on the uncrafting status
            int textColor = (container.uncraftingStatus != UncraftingStatus.ERROR ? 8453920 : 16736352);  
            int shadowColor = -16777216 | (textColor & 16579836) >> 2 | textColor & -16777216;

            // render the string 4 times at different positions in different colours to achieve the desired effect
            this.fontRendererObj.drawString(statusMessage, textX, textY + 1, shadowColor);
            this.fontRendererObj.drawString(statusMessage, textX + 1, textY, shadowColor);
            this.fontRendererObj.drawString(statusMessage, textX + 1, textY + 1, shadowColor);
            this.fontRendererObj.drawString(statusMessage, textX, textY, textColor);
            // *** copied from GuiRepair ***
        }
    }
    
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        GL11.glDisable(GL11.GL_LIGHTING);
        
    	// render the block name at the top of the gui
        String title = I18n.format("container.uncrafting");
        fontRendererObj.drawString(title, xSize / 2 - fontRendererObj.getStringWidth(title) / 2, 6, 4210752);
        
        // write "inventory" above the player inventory
        fontRendererObj.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, 4210752); // y = 72

        // draw a status message in red or green if appropriate for the status of the uncrafting operation
    	drawUncraftingStatusMessage();
    	
            
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GL11.glPushMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        // bind the background gui texture
        this.mc.getTextureManager().bindTexture(uncraftingTableGuiTextures);
        
		int guiX = (this.width - this.xSize) / 2;
		int guiY = (this.height - this.ySize) / 2;
        
        // render the gui background
        this.drawTexturedModalRect(guiX, guiY, 0, 0, this.xSize, this.ySize);
        
		// if the uncrafting status of the container is "error", render the arrow with the cross over it
		if (container.uncraftingStatus == UncraftingStatus.ERROR)
		{
			this.drawTexturedModalRect(guiX + 71, guiY + 33, 176, 0, 28, 21);
		}
        
        GL11.glPopMatrix();
    }

}
