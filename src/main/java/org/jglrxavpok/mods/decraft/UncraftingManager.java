package org.jglrxavpok.mods.decraft;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.jglrxavpok.mods.decraft.RecipeHandlers.RecipeHandler;
import org.jglrxavpok.mods.decraft.RecipeHandlers.ShapedOreRecipeHandler;
import org.jglrxavpok.mods.decraft.RecipeHandlers.ShapedRecipeHandler;
import org.jglrxavpok.mods.decraft.RecipeHandlers.ShapelessOreRecipeHandler;
import org.jglrxavpok.mods.decraft.RecipeHandlers.ShapelessRecipeHandler;
import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipesMapExtending;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
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
		String uniqueIdentifier = Item.REGISTRY.getNameForObject(itemStack.getItem()).toString();
		if (itemStack.getItemDamage() > 0) uniqueIdentifier += "," + Integer.toString(itemStack.getItemDamage()); 
		
		return ArrayUtils.indexOf(ModConfiguration.excludedItems, uniqueIdentifier) < 0;
	}
	
	
	public static List<Integer> getStackSizeNeeded(ItemStack item)
	{
		List<Integer> list = new ArrayList<Integer>();
		if (!canUncraftItem(item)) return list;
		
		List<IRecipe> recipeList = CraftingManager.getInstance().getRecipeList();
		for ( IRecipe recipe : recipeList )
		{
			ItemStack recipeOutput = recipe.getRecipeOutput();
			if (recipeOutput != null)
			{
				if (ItemStack.areItemsEqualIgnoreDurability(item, recipeOutput))
				{
					RecipeHandler handler = getRecipeHandler(recipe);
					if (handler != null)
					{
						list.add(recipeOutput.getCount());
						break;
					}
					else 
					{
						ModUncrafting.instance.getLogger().error("[Uncrafting Table] Unknown recipe type: " + recipe.getClass().getCanonicalName());
					}
				}
			}
		}

		return list;
	}
	
	public static List<NonNullList<ItemStack>> getUncraftResults(ItemStack item)
	{
		List<NonNullList<ItemStack>> list = new ArrayList<NonNullList<ItemStack>>();
		if (!canUncraftItem(item)) return list;
		
		List<IRecipe> recipeList = CraftingManager.getInstance().getRecipeList();
		for ( IRecipe recipe : recipeList )
		{
			ItemStack recipeOutput = recipe.getRecipeOutput();
			if (ItemStack.areItemsEqualIgnoreDurability(item, recipeOutput) && recipeOutput.getCount() <= item.getCount())
			{
				RecipeHandler handler = getRecipeHandler(recipe);
				if (handler != null)
				{
					list.add(handler.getCraftingGrid(recipe));
					break;
				}
				else 
				{
					ModUncrafting.instance.getLogger().error("[Uncrafting Table] Unknown recipe type: " + recipe.getClass().getCanonicalName());
				}
			}
		}
		
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
