package org.jglrxavpok.mods.decraft.client.update;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraft.client.resources.I18n;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;


public class VersionChecker
{

    public static VersionCheckerTask versionCheckerTask = new VersionCheckerTask();
	private static VersionCheckEventHandler versionCheckEventHandler = new VersionCheckEventHandler();
    
    public static boolean haveWarnedVersionOutOfDate = false;

    
    public static void clientPostInit()
    {
    	if (ModConfiguration.checkForUpdates)
    	{
    		// register our event handler on the event bus
	        FMLCommonHandler.instance().bus().register(versionCheckEventHandler);
	        // create and run an instance of the version checker on a new thread
			Thread versionCheckThread = new Thread(versionCheckerTask, "Version Check");
			versionCheckThread.start();
    	}
    }
    

	public static class VersionCheckEventHandler
	{
		
		@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
		public void onPlayerTick(PlayerTickEvent event)
		{
			// if we haven't already notified of a new version...
		    if (event.player.worldObj.isRemote && !haveWarnedVersionOutOfDate)
		    {
		    	String chatMessageText = "";
		    	
		    	if (ModConfiguration.promptForLatest && !versionCheckerTask.isLatestVersion())
		    	{
		    		chatMessageText = I18n.format("chat.update.newlatest", ModUncrafting.MODNAME);
		    	}
		    	else if (ModConfiguration.promptForRecommended && !versionCheckerTask.isRecommendedVersion())
		    	{
		    		chatMessageText = I18n.format("chat.update.newrecommended", ModUncrafting.MODNAME);
		    	}
		    	
		    	if (!chatMessageText.equals(""))
		    	{
			        ClickEvent versionCheckChatClickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, versionCheckerTask.getHomepageUrl());
			        ChatStyle clickableChatStyle = new ChatStyle().setChatClickEvent(versionCheckChatClickEvent);
			        ChatComponentText versionWarningChatComponent = new ChatComponentText(chatMessageText);
			        versionWarningChatComponent.setChatStyle(clickableChatStyle);
			        
			        event.player.addChatMessage(versionWarningChatComponent);
			        
			        haveWarnedVersionOutOfDate = true;
		    		
		    	}
		    }
		}
		      	
	}

}
