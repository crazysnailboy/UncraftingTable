package org.jglrxavpok.mods.decraft.common.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.util.FileUtils;
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


	public static ItemMappingMap itemMappings;

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
		itemMappings = new ItemMappingMap();

		String fileContents = FileUtils.readFileContentsFromMod("assets/" + ModUncrafting.MODID + "/data/item-mappings.json");

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

			itemMappings.put(itemName, itemMapping);
		}
	}

}
