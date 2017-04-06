package org.jglrxavpok.mods.decraft.client.update;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

	private static VersionCheckerTask versionCheckerTask = new VersionCheckerTask();
	private static VersionCheckEventHandler versionCheckEventHandler = new VersionCheckEventHandler();

	private static boolean haveWarnedVersionOutOfDate = false;


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


	public static class VersionCheckerTask implements Runnable
	{

		private static String homepageUrl = "";

		private static String latestVersion = "";
		private static String recommendedVersion = "";

		private static boolean isLatestVersion = false;
		private static boolean isRecommendedVersion = false;


		@Override
		public void run()
		{
			InputStream inputStream = null;

			try
			{
				// read the url into the input stream
				inputStream = new URL(ModUncrafting.UPDATEJSON).openStream();
			}
			catch (MalformedURLException ex) { ex.printStackTrace(); }
			catch (IOException ex) { ex.printStackTrace(); }

			try
			{
				// read the stream contents as a string
				String jsonText = IOUtils.toString(inputStream, Charset.defaultCharset());

				// parse the string as JSON
				JsonObject updateObject = new JsonParser().parse(jsonText).getAsJsonObject();

				// get the homepage url
				homepageUrl = updateObject.get("homepage").getAsString();

				// get the latest and recommended versions of the mod for this minecraft version
				JsonObject promosObject = updateObject.getAsJsonObject("promos");
				latestVersion = promosObject.get(cpw.mods.fml.common.Loader.MC_VERSION + "-latest").getAsString();
				recommendedVersion = promosObject.get(cpw.mods.fml.common.Loader.MC_VERSION + "-recommended").getAsString();

			}
			catch (IOException ex) { ex.printStackTrace(); }
			finally { IOUtils.closeQuietly(inputStream); }

			isLatestVersion = ModUncrafting.VERSION.equals(latestVersion);
			isRecommendedVersion = ModUncrafting.VERSION.equals(recommendedVersion);

		}

		public boolean isLatestVersion()
		{
			return isLatestVersion;
		}

		public boolean isRecommendedVersion()
		{
			return isRecommendedVersion;
		}

		public String getLatestVersion()
		{
			return latestVersion;
		}

		public String getRecommendedVersion()
		{
			return recommendedVersion;
		}

		public String getHomepageUrl()
		{
			return homepageUrl;
		}

	}


	public static class VersionCheckEventHandler
	{

		@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
		public void onPlayerTick(PlayerTickEvent event)
		{
			// if we haven't already notified of a new version...
			if (!haveWarnedVersionOutOfDate && event.player.worldObj.isRemote)
			{
				String chatMessageText = "";

				// if we're not running the latest version, and the user wishes to be notified for latest versions
				if (ModConfiguration.promptForLatest && !versionCheckerTask.isLatestVersion())
				{
					// build a message string to diaplay in chat for the new latest version
					chatMessageText = I18n.format("chat.update.newlatest", ModUncrafting.MODNAME);
				}
				// if we're not running the latest version or the recommended version, and the user wishes to be notified for recommended versions
				else if (ModConfiguration.promptForRecommended && !versionCheckerTask.isLatestVersion() && !versionCheckerTask.isRecommendedVersion())
				{
					// build a message string to diaplay in chat for the new recommended version
					chatMessageText = I18n.format("chat.update.newrecommended", ModUncrafting.MODNAME);
				}

				// if we build a message string
				if (!chatMessageText.equals(""))
				{
					// create a clickable chat event
					ClickEvent versionCheckChatClickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, versionCheckerTask.getHomepageUrl());
					ChatStyle clickableChatStyle = new ChatStyle().setChatClickEvent(versionCheckChatClickEvent);
					ChatComponentText versionWarningChatComponent = new ChatComponentText(chatMessageText);
					versionWarningChatComponent.setChatStyle(clickableChatStyle);

					// display the message in chat
					event.player.addChatMessage(versionWarningChatComponent);
				}

				// store the fact that we've done the version check
				haveWarnedVersionOutOfDate = true;

				// unregister from the event bus
				FMLCommonHandler.instance().bus().unregister(versionCheckEventHandler);
			}
		}

	}

}
