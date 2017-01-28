package org.jglrxavpok.mods.decraft.init;

import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.item.ItemNugget;

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
		// register the items
		GameRegistry.register(NUGGET);
	}
	
	public static void clientPreInit() 
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
	
	
	public static void init()
	{
		// register the ore dictionary entries
		OreDictionary.registerOre("nuggetDiamond", new ItemStack(NUGGET, 1, 0));  
		OreDictionary.registerOre("shardDiamond", new ItemStack(NUGGET, 1, 0));  // added for compatibility with Magic Bees
		OreDictionary.registerOre("nuggetEmerald", new ItemStack(NUGGET, 1, 1));
		OreDictionary.registerOre("shardEmerald", new ItemStack(NUGGET, 1, 1));  // added for compatibility with Magic Bees
//		OreDictionary.registerOre("nuggetIron", new ItemStack(NUGGET, 1, 2));
		
		
		// register crafting recipes
		// gems and ingots to nuggets
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.NUGGET, 9, 0), new Object[] { Items.DIAMOND }));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.NUGGET, 9, 1), new Object[] { Items.EMERALD }));
//		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.NUGGET, 9, 2), new Object[] { Items.IRON_INGOT }));
		// nuggets to gems and ingots
		GameRegistry.addRecipe(new ShapedOreRecipe(Items.DIAMOND, new Object[] { "FFF", "FFF", "FFF", Character.valueOf('F'), "nuggetDiamond" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(Items.EMERALD, new Object[] { "FFF", "FFF", "FFF", Character.valueOf('F'), "nuggetEmerald" }));
//		GameRegistry.addRecipe(new ShapedOreRecipe(Items.IRON_INGOT, new Object[] { "FFF", "FFF", "FFF", Character.valueOf('F'), "nuggetIron" }));
	}
	
}
