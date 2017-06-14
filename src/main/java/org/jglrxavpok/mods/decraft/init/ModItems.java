package org.jglrxavpok.mods.decraft.init;

import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;
import org.jglrxavpok.mods.decraft.item.ItemNugget;
import org.jglrxavpok.mods.decraft.item.ItemNugget.EnumNuggetType;
import org.jglrxavpok.mods.decraft.item.crafting.RecipeHelper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;


public class ModItems
{

	public static final Item NUGGET = new ItemNugget();


	public static void registerItems()
	{
		if (ModConfiguration.registerNuggets)
		{
			// register the items
			GameRegistry.register(NUGGET);
		}
	}

	public static void registerInventoryModels()
	{
		if (ModConfiguration.registerNuggets)
		{
			// register the item models
			for (EnumNuggetType nuggetType : EnumNuggetType.values())
			{
				if (nuggetType.getRegistryName() != null)
				{
					ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation(ModUncrafting.MODID + ":" + nuggetType.getRegistryName(), "inventory");
					ModelLoader.setCustomModelResourceLocation(NUGGET, nuggetType.getMetadata(), itemModelResourceLocation);
				}
			}
		}
	}

	public static void registerOreDictionaryEntries()
	{
		if (ModConfiguration.registerNuggets)
		{
			// register the ore dictionary entries
			OreDictionary.registerOre("nuggetDiamond", new ItemStack(NUGGET, 1, EnumNuggetType.DIAMOND.getMetadata()));
			OreDictionary.registerOre("shardDiamond", new ItemStack(NUGGET, 1, EnumNuggetType.DIAMOND.getMetadata()));  // added for compatibility with Magic Bees
			OreDictionary.registerOre("nuggetEmerald", new ItemStack(NUGGET, 1, EnumNuggetType.EMERALD.getMetadata()));
			OreDictionary.registerOre("shardEmerald", new ItemStack(NUGGET, 1, EnumNuggetType.EMERALD.getMetadata()));  // added for compatibility with Magic Bees
			OreDictionary.registerOre("nuggetLeather", new ItemStack(NUGGET, 1, EnumNuggetType.LEATHER.getMetadata()));
		}
	}

	public static void registerCraftingRecipes()
	{
		if (ModConfiguration.registerNuggets)
		{
			// register crafting recipes
			// items to nuggets
			RecipeHelper.addShapelessRecipe(new ItemStack(ModItems.NUGGET, 9, EnumNuggetType.DIAMOND.getMetadata()), new Object[] { Items.DIAMOND });
			RecipeHelper.addShapelessRecipe(new ItemStack(ModItems.NUGGET, 9, EnumNuggetType.EMERALD.getMetadata()), new Object[] { Items.EMERALD });
			RecipeHelper.addShapelessRecipe(new ItemStack(ModItems.NUGGET, 9, EnumNuggetType.LEATHER.getMetadata()), new Object[] { Items.LEATHER });
			// nuggets to items
			RecipeHelper.addShapedRecipe(Items.DIAMOND, new Object[] { "FFF", "FFF", "FFF", 'F', new ItemStack(NUGGET, 1, EnumNuggetType.DIAMOND.getMetadata()) });
			RecipeHelper.addShapedRecipe(Items.EMERALD, new Object[] { "FFF", "FFF", "FFF", 'F', new ItemStack(NUGGET, 1, EnumNuggetType.EMERALD.getMetadata()) });
			RecipeHelper.addShapedRecipe(Items.LEATHER, new Object[] { "FFF", "FFF", "FFF", 'F', new ItemStack(NUGGET, 1, EnumNuggetType.LEATHER.getMetadata()) });
		}
	}

}
