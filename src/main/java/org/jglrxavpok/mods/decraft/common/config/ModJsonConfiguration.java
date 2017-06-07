package org.jglrxavpok.mods.decraft.common.config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.jglrxavpok.mods.decraft.ModUncrafting;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.item.ItemStack;

public class ModJsonConfiguration
{

	public static class ItemMapping
	{
		public boolean singleRecipe = false;
		public String recipeType = null;
		public int[] replaceSlots = null;

		public boolean matchTag = false;
		public String tagName = null;

		public boolean matchField = false;
		public String[] fieldNames = null;
	}

	public static class ItemMappingMap extends HashMap<String, ItemMapping>
	{
		public ItemMapping get(ItemStack stack)
		{
			String registryName = stack.getItem().getRegistryName().toString();
			int meta = stack.getMetadata();

			ItemMapping result = this.get(registryName + "," + meta);
			if (result == null) result = this.get(registryName);

			return result;
		}

	}

	public static final ItemMappingMap ITEM_MAPPINGS = new ItemMappingMap();


	public static void loadItemMappings()
	{
		String fileContents = readFileContentsFromMod("assets/" + ModUncrafting.MODID + "/data/item-mappings.json");

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

			if (jsonMapping.has("matchTag")) itemMapping.matchTag = jsonMapping.get("matchTag").getAsBoolean();
			if (jsonMapping.has("tagName")) itemMapping.tagName = jsonMapping.get("tagName").getAsString();

			if (jsonMapping.has("matchField")) itemMapping.matchField = jsonMapping.get("matchField").getAsBoolean();
			if (jsonMapping.has("fieldNames")) itemMapping.fieldNames = gson.fromJson(jsonMapping.get("fieldNames").getAsJsonArray(), String[].class);

			ITEM_MAPPINGS.put(itemName, itemMapping);
		}
	}


	private static String readFileContentsFromMod(String fileName)
	{
		String fileContents = "";
		try
		{
			InputStream stream = ModUncrafting.instance.getClass().getClassLoader().getResourceAsStream(fileName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));

			fileContents = IOUtils.toString(stream);

			reader.close();
			stream.close();

		}
		catch(Exception ex){ ModUncrafting.LOGGER.catching(ex); }
		return fileContents;
	}

}
