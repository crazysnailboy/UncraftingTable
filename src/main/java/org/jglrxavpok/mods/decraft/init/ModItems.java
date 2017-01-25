package org.jglrxavpok.mods.decraft.init;

import org.jglrxavpok.mods.decraft.item.ItemNugget;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;


public class ModItems 
{
	
	public static final Item nugget = new ItemNugget();
	
	
	public static void preInit()
	{
		// register the items
		GameRegistry.registerItem(nugget, nugget.getUnlocalizedName());
	}
	
	
	public static void init()
	{
		// register the ore dictionary entries
		OreDictionary.registerOre("nuggetDiamond", new ItemStack(nugget, 1, 0));  
		OreDictionary.registerOre("shardDiamond", new ItemStack(nugget, 1, 0));  // added for compatibility with Magic Bees
		OreDictionary.registerOre("nuggetEmerald", new ItemStack(nugget, 1, 1));
		OreDictionary.registerOre("shardEmerald", new ItemStack(nugget, 1, 1));  // added for compatibility with Magic Bees
		OreDictionary.registerOre("nuggetIron", new ItemStack(nugget, 1, 2));
		
		
		// register crafting recipes
		// gems and ingots to nuggets
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.nugget, 9, 0), new Object[] { Items.diamond }));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.nugget, 9, 1), new Object[] { Items.emerald }));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.nugget, 9, 2), new Object[] { Items.iron_ingot }));
		// nuggets to gems and ingots
		GameRegistry.addRecipe(new ShapedOreRecipe(Items.diamond, new Object[] { "FFF", "FFF", "FFF", Character.valueOf('F'), "nuggetDiamond" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(Items.emerald, new Object[] { "FFF", "FFF", "FFF", Character.valueOf('F'), "nuggetEmerald" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(Items.iron_ingot, new Object[] { "FFF", "FFF", "FFF", Character.valueOf('F'), "nuggetIron" }));
		
	}
	
}
