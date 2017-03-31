package org.jglrxavpok.mods.decraft.init;

import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;
import org.jglrxavpok.mods.decraft.item.ItemNugget;
import org.jglrxavpok.mods.decraft.item.ItemNugget.EnumNuggetType;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;


public class ModItems
{

	public static final Item NUGGET = new ItemNugget();

	public static void preInit()
	{
		if (ModConfiguration.registerNuggets)
		{
			// register the items
			GameRegistry.register(NUGGET);
		}
	}

	public static void clientPreInit()
	{
		if (ModConfiguration.registerNuggets)
		{
			// register the item models
			for (ItemNugget.EnumNuggetType nuggetType : ItemNugget.EnumNuggetType.values())
			{
				String registryName = nuggetType.getRegistryName();
				int metadata = nuggetType.getMetadata();

				ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation(ModUncrafting.MODID + ":" + registryName, "inventory");
				ModelLoader.setCustomModelResourceLocation(NUGGET, metadata, itemModelResourceLocation);
			}
		}
	}


	public static void init()
	{
		if (ModConfiguration.registerNuggets)
		{
			// register the ore dictionary entries
			OreDictionary.registerOre("nuggetDiamond", new ItemStack(NUGGET, 1, EnumNuggetType.DIAMOND.getMetadata()));
			OreDictionary.registerOre("shardDiamond", new ItemStack(NUGGET, 1, EnumNuggetType.DIAMOND.getMetadata()));  // added for compatibility with Magic Bees
			OreDictionary.registerOre("nuggetEmerald", new ItemStack(NUGGET, 1, EnumNuggetType.EMERALD.getMetadata()));
			OreDictionary.registerOre("shardEmerald", new ItemStack(NUGGET, 1, EnumNuggetType.EMERALD.getMetadata()));  // added for compatibility with Magic Bees
			OreDictionary.registerOre("nuggetIron", new ItemStack(NUGGET, 1, EnumNuggetType.IRON.getMetadata()));
			OreDictionary.registerOre("nuggetLeather", new ItemStack(NUGGET, 1, EnumNuggetType.LEATHER.getMetadata()));

			// register crafting recipes
			// items to nuggets
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.NUGGET, 9, EnumNuggetType.DIAMOND.getMetadata()), new Object[] { Items.DIAMOND }));
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.NUGGET, 9, EnumNuggetType.EMERALD.getMetadata()), new Object[] { Items.EMERALD }));
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.NUGGET, 9, EnumNuggetType.IRON.getMetadata()), new Object[] { Items.IRON_INGOT }));
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.NUGGET, 9, EnumNuggetType.LEATHER.getMetadata()), new Object[] { Items.LEATHER }));
			// nuggets to items
			GameRegistry.addRecipe(new ShapedOreRecipe(Items.DIAMOND, new Object[] { "FFF", "FFF", "FFF", Character.valueOf('F'), "nuggetDiamond" }));
			GameRegistry.addRecipe(new ShapedOreRecipe(Items.EMERALD, new Object[] { "FFF", "FFF", "FFF", Character.valueOf('F'), "nuggetEmerald" }));
			GameRegistry.addRecipe(new ShapedOreRecipe(Items.IRON_INGOT, new Object[] { "FFF", "FFF", "FFF", Character.valueOf('F'), "nuggetIron" }));
			GameRegistry.addRecipe(new ShapedOreRecipe(Items.LEATHER, new Object[] { "FFF", "FFF", "FFF", Character.valueOf('F'), "nuggetLeather" }));
		}
	}

}
