package org.jglrxavpok.mods.decraft.client.update;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.jglrxavpok.mods.decraft.ModUncrafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.client.Minecraft;


public class VersionCheckerTask implements Runnable
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
		catch (MalformedURLException e) { e.printStackTrace(); } 
		catch (IOException e) { e.printStackTrace(); }
		
		try 
		{
			// read the stream contents as a string
			String jsonText = IOUtils.toString(inputStream, Charset.defaultCharset());
			
			// parse the string as JSON
			JsonObject updateObject = new JsonParser().parse(jsonText).getAsJsonObject();
			
			// get the homepage url
			homepageUrl = updateObject.get("homepage").getAsString();
			System.out.println(homepageUrl);

			// get the latest and recommended versions of the mod for this minecraft version
			JsonObject promosObject = updateObject.getAsJsonObject("promos");
			latestVersion = promosObject.get(cpw.mods.fml.common.Loader.MC_VERSION + "-latest").getAsString();
			recommendedVersion = promosObject.get(cpw.mods.fml.common.Loader.MC_VERSION + "-recommended").getAsString(); 

		} 
		catch (IOException e) { e.printStackTrace(); }
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
