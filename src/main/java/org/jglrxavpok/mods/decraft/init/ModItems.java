package org.jglrxavpok.mods.decraft.init;

import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;
import org.jglrxavpok.mods.decraft.item.ItemNugget;
import org.jglrxavpok.mods.decraft.item.ItemNugget.EnumNuggetType;

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
		if (ModConfiguration.registerNuggets)
		{
			// register the items
			GameRegistry.registerItem(nugget, nugget.getUnlocalizedName());
		}
	}


	public static void init()
	{
		OreDictionary.registerOre("leather", Items.leather);

		if (ModConfiguration.registerNuggets)
		{
			// register the ore dictionary entries
			OreDictionary.registerOre("nuggetDiamond", new ItemStack(nugget, 1, EnumNuggetType.DIAMOND.getMetadata()));
			OreDictionary.registerOre("shardDiamond", new ItemStack(nugget, 1, EnumNuggetType.DIAMOND.getMetadata()));  // added for compatibility with Magic Bees
			OreDictionary.registerOre("nuggetEmerald", new ItemStack(nugget, 1, EnumNuggetType.EMERALD.getMetadata()));
			OreDictionary.registerOre("shardEmerald", new ItemStack(nugget, 1, EnumNuggetType.EMERALD.getMetadata()));  // added for compatibility with Magic Bees
			OreDictionary.registerOre("nuggetIron", new ItemStack(nugget, 1, EnumNuggetType.IRON.getMetadata()));
			OreDictionary.registerOre("nuggetLeather", new ItemStack(nugget, 1, EnumNuggetType.LEATHER.getMetadata()));

			// register crafting recipes
			// items to nuggets
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.nugget, 9, EnumNuggetType.DIAMOND.getMetadata()), new Object[] { Items.diamond }));
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.nugget, 9, EnumNuggetType.EMERALD.getMetadata()), new Object[] { Items.emerald }));
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.nugget, 9, EnumNuggetType.IRON.getMetadata()), new Object[] { Items.iron_ingot }));
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.nugget, 9, EnumNuggetType.LEATHER.getMetadata()), new Object[] { Items.leather }));
			// nuggets to items
			GameRegistry.addRecipe(new ShapedOreRecipe(Items.diamond, new Object[] { "FFF", "FFF", "FFF", Character.valueOf('F'), "nuggetDiamond" }));
			GameRegistry.addRecipe(new ShapedOreRecipe(Items.emerald, new Object[] { "FFF", "FFF", "FFF", Character.valueOf('F'), "nuggetEmerald" }));
			GameRegistry.addRecipe(new ShapedOreRecipe(Items.iron_ingot, new Object[] { "FFF", "FFF", "FFF", Character.valueOf('F'), "nuggetIron" }));
			GameRegistry.addRecipe(new ShapedOreRecipe(Items.leather, new Object[] { "FFF", "FFF", "FFF", Character.valueOf('F'), "nuggetLeather" }));
		}
	}

}
