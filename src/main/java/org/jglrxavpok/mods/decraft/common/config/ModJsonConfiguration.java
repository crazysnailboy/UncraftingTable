package org.jglrxavpok.mods.decraft.common.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.util.FileUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ModJsonConfiguration
{

	public static class ItemMapping
	{
		public boolean singleRecipe = false;
		public String recipeType = null;
		public int[] replaceSlots = null;
	}



	public static HashMap<String, ItemMapping> itemMappings;

	public static void preInit()
	{
		loadItemMappings();
	}

	public static void init()
	{
	}

	public static void postInit()
	{
	}




	private static void loadItemMappings()
	{
		itemMappings = new HashMap<String, ItemMapping>();

		String fileContents = FileUtils.readFileContentsFromMod("assets/" + ModUncrafting.MODID.toLowerCase() + "/data/item-mappings.json");

		JsonObject jsonObject = new JsonParser().parse(fileContents).getAsJsonObject();
		Gson gson = new Gson();

		Set<Map.Entry<String, JsonElement>> entries = jsonObject.entrySet();
		for (Map.Entry<String, JsonElement> entry : entries)
		{
			ItemMapping itemMapping = new ItemMapping();

			String itemName = entry.getKey();
			JsonObject jsonMapping = entry.getValue().getAsJsonObject();

			if (jsonMapping.has("singleRecipe")) itemMapping.singleRecipe = jsonMapping.get("singleRecipe").getAsBoolean();
			if (jsonMapping.has("recipeType")) itemMapping.recipeType = jsonMapping.get("recipeType").getAsString();
			if (jsonMapping.has("replaceSlots")) itemMapping.replaceSlots = gson.fromJson(jsonMapping.get("replaceSlots").getAsJsonArray(), int[].class);

			itemMappings.put(itemName, itemMapping);
		}
	}

}
