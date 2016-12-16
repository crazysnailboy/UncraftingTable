package org.jglrxavpok.mods.decraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.jglrxavpok.mods.decraft.RecipeHandlers.RecipeHandler;
import org.jglrxavpok.mods.decraft.RecipeHandlers.ShapedOreRecipeHandler;
import org.jglrxavpok.mods.decraft.RecipeHandlers.ShapedRecipeHandler;
import org.jglrxavpok.mods.decraft.RecipeHandlers.ShapelessOreRecipeHandler;
import org.jglrxavpok.mods.decraft.RecipeHandlers.ShapelessRecipeHandler;
import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipesMapExtending;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;


/**
 * Main part of the Uncrafting Table. The manager is used to parse the existing recipes and find the correct one depending on the given stack.
 * @author jglrxavpok
 */
public class UncraftingManager 
{

	private static Boolean canUncraftItem(ItemStack itemStack)
	{
		String uniqueIdentifier = GameRegistry.findUniqueIdentifierFor(itemStack.getItem()).toString();
		if (itemStack.getItemDamage() > 0) uniqueIdentifier += "," + Integer.toString(itemStack.getItemDamage()); 
		
		return ArrayUtils.indexOf(ModConfiguration.excludedItems, uniqueIdentifier) < 0;
	}
	
	
	public static List<Integer> getStackSizeNeeded(ItemStack item)
	{
//		System.out.println("\t" + "getStackSizeNeeded");
//		System.out.println("\t" + item.getItem().getUnlocalizedName());
//		System.out.println("\t" + item.getDisplayName());
		
		List<Integer> list = new ArrayList<Integer>();
		
		if (!canUncraftItem(item)) return list;
		
		List<?> recipeList = CraftingManager.getInstance().getRecipeList();

		
		for (int i = 0 ; i < recipeList.size() ; i++)
		{
			IRecipe recipe = (IRecipe)recipeList.get(i);
			if (recipe != null)
			{
				ItemStack recipeOutput = recipe.getRecipeOutput();
				if (recipeOutput != null)
				{
					if (recipeOutput.getItem() == item.getItem() && recipeOutput.getItemDamage() == item.getItemDamage())
					{
						RecipeHandler handler = getRecipeHandler(recipe);
						if (handler != null)
						{
							list.add(recipeOutput.stackSize);
						}
						else 
						{
							ModUncrafting.instance.getLogger().error("[Uncrafting Table] Unknown recipe type: "+recipe.getClass().getCanonicalName());
						}
					}
				}
			}
		}
//		System.out.println("\t" + "-----");

		return list;
	}
	
	public static List<ItemStack[]> getUncraftResults(ItemStack item)
	{
//		System.out.println("getUncraftResults");
//		System.out.println(item.getItem().getUnlocalizedName());
//		System.out.println(item.getDisplayName());
//		System.out.println("isDamageable: " + item.getItem().isDamageable());

		List<ItemStack[]> list = new ArrayList<ItemStack[]>();
		
		if (!canUncraftItem(item)) return list;
		
		
		List<?> recipeList = CraftingManager.getInstance().getRecipeList();
		
		for (int i = 0 ; i < recipeList.size() ; i++)
		{
			IRecipe recipe = (IRecipe)recipeList.get(i);
			if (recipe != null)
			{
				ItemStack recipeOutput = recipe.getRecipeOutput();
				if (recipeOutput != null)
				{
					if (
						(recipeOutput.getItem() == item.getItem() && recipeOutput.stackSize <= item.stackSize && item.getItem().isDamageable() == false && recipeOutput.getItemDamage() == item.getItemDamage())
						||
						(recipeOutput.getItem() == item.getItem() && recipeOutput.stackSize <= item.stackSize && item.getItem().isDamageable() == true)
					)
					{
						RecipeHandler handler = getRecipeHandler(recipe);
						if (handler != null)
						{
							list.add(handler.getCraftingGrid(recipe));
						}
						else
						{
							ModUncrafting.instance.getLogger().error("[Uncrafting Table] Unknown recipe type: "+recipe.getClass().getCanonicalName());
						}
					}
				}
			}
		}
		
//		System.out.println("-----");
		
		return list;
	}
	
	
	private static RecipeHandler getRecipeHandler(IRecipe recipe)
	{
		if (recipe instanceof RecipesMapExtending) return null;
		if (recipe instanceof ShapelessRecipes) return new ShapelessRecipeHandler(ShapelessRecipes.class);
		if (recipe instanceof ShapedRecipes) return new ShapedRecipeHandler(ShapedRecipes.class);
		if (recipe instanceof ShapelessOreRecipe) return new ShapelessOreRecipeHandler(ShapelessOreRecipe.class);
		if (recipe instanceof ShapedOreRecipe) return new ShapedOreRecipeHandler(ShapedOreRecipe.class);
		
		return null;
	}
	
	
	public static void postInit()
	{
	}
	
	
}
