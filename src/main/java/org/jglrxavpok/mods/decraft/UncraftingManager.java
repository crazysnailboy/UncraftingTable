package org.jglrxavpok.mods.decraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang3.ArrayUtils;
import org.jglrxavpok.mods.decraft.RecipeHandlers.RecipeHandler;
import org.jglrxavpok.mods.decraft.RecipeHandlers.ShapedOreRecipeHandler;
import org.jglrxavpok.mods.decraft.RecipeHandlers.ShapedRecipeHandler;
import org.jglrxavpok.mods.decraft.RecipeHandlers.ShapelessOreRecipeHandler;
import org.jglrxavpok.mods.decraft.RecipeHandlers.ShapelessRecipeHandler;
import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import net.minecraftforge.fml.common.registry.GameRegistry;
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
 * 
 */
public class UncraftingManager 
{

    /**
     * Constants to identify the different uncrafting algorithms
     */
	private static class UncraftingMethod
	{
		public static final int JGLRXAVPOK = 0;
		public static final int XELL75_ZENEN = 1;
	}

	
	/**
	 * Checks whether uncrafting of the target item is disabled via config
	 * @param itemStack The ItemStack containing the target item
	 * @return True if the item is in the excluded items list, otherwise false
	 */
	private static Boolean isUncraftingDisabledForItem(ItemStack itemStack)
	{
		String uniqueIdentifier = Item.itemRegistry.getNameForObject(itemStack.getItem()).toString();
		if (itemStack.getItemDamage() > 0) uniqueIdentifier += "," + Integer.toString(itemStack.getItemDamage()); 
		
		return ArrayUtils.indexOf(ModConfiguration.excludedItems, uniqueIdentifier) >= 0;
	}
	
	
	/**
	 * Determines the minimum number of items required for an uncrafting operation to be performed
	 * @param itemStack The ItemStack containing the target item
	 * @return A collection of the mininum required stack sizes - one element per recipe found
	 */
	public static List<Integer> getStackSizeNeeded(ItemStack item)
	{
		List<Integer> list = new ArrayList<Integer>();
		if (isUncraftingDisabledForItem(item)) return list;
		
		List<IRecipe> recipeList = CraftingManager.getInstance().getRecipeList();
		for ( IRecipe recipe : recipeList )
		{
			ItemStack recipeOutput = recipe.getRecipeOutput();
			if (recipeOutput != null)
			{
				if (ItemStackHelper.areItemsEqualIgnoreDurability(item, recipeOutput))
				{
					RecipeHandler handler = getRecipeHandler(recipe);
					if (handler != null)
					{
						list.add(recipeOutput.stackSize);
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
	
	
	/**
	 * Returns the available crafting recipes which can be used to perform an uncrafting operation 
	 * @param itemStack The ItemStack containing the target item
	 * @return A collection of the ItemStack arrays representing the crafting recipe - one element per recipe found
	 */
	public static List<ItemStack[]> getUncraftResults(ItemStack item)
	{
		List<ItemStack[]> list = new ArrayList<ItemStack[]>();
		if (isUncraftingDisabledForItem(item)) return list;
		
		List<IRecipe> recipeList = CraftingManager.getInstance().getRecipeList();
		for ( IRecipe recipe : recipeList )
		{
			ItemStack recipeOutput = recipe.getRecipeOutput();
			if (ItemStackHelper.areItemsEqualIgnoreDurability(item, recipeOutput) && recipeOutput.stackSize <= item.stackSize)
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
	
	
	/**
	 * Determines the XP cost of the uncrafting operation
	 * @param itemStack The ItemStack containing the target item
	 * @return The number of XP levels required to complete the operation
	 */
	public static int getUncraftingXpCost(ItemStack itemStack)
	{
    	// if we're using jglrxavpok's uncrafting method...
		if (ModConfiguration.uncraftMethod == UncraftingMethod.JGLRXAVPOK)
		{
			// the xp cost is the standard cost
			return ModConfiguration.standardLevel;
		}
		
        // if we're using Xell75's & Zenen's uncrafting method...
        if (ModConfiguration.uncraftMethod == UncraftingMethod.XELL75_ZENEN)
        {
        	// if the item isn't damageable
        	if (!itemStack.getItem().isDamageable())
        	{
    			// the xp cost is the standard cost
    			return ModConfiguration.standardLevel;
        	}
        	// if the item is damageable, but isn't damaged
        	else if (itemStack.getItem().isDamageable() && itemStack.getItemDamage() == 0)
        	{
    			// the xp cost is the standard cost
    			return ModConfiguration.standardLevel;
        	}
        	// if the item is damageable and is damaged
        	else
        	{
        		// the xp cost is standard level + (damage percentage * the max level)
            	int damagePercentage = (int)(((double)itemStack.getItemDamage() / (double)itemStack.getMaxDamage()) * 100);
            	return ((ModConfiguration.maxUsedLevel * damagePercentage) / 100);
        	}
        }

        return -1; // return ModConfiguration.standardLevel;
	}

	
	/**
	 * Creates an uncrafting recipe handler capable of uncrafting the given IRecipe instance
	 * @param recipe The IRecipe instance of the crafting recipe 
	 * @return The RecipeHandler instance which can be used to uncraft the IRecipe
	 */
	private static RecipeHandler getRecipeHandler(IRecipe recipe)
	{
		// RecipesMapExtending extends ShapedRecipes, and causes a crash when attempting to uncraft a map
		if (recipe instanceof RecipesMapExtending) return null;
		// vanilla Minecraft recipe handlers
		if (recipe instanceof ShapelessRecipes) return new ShapelessRecipeHandler(ShapelessRecipes.class);
		if (recipe instanceof ShapedRecipes) return new ShapedRecipeHandler(ShapedRecipes.class);
		// Forge Ore Dictionary recipe handlers
		if (recipe instanceof ShapelessOreRecipe) return new ShapelessOreRecipeHandler(ShapelessOreRecipe.class);
		if (recipe instanceof ShapedOreRecipe) return new ShapedOreRecipeHandler(ShapedOreRecipe.class);
		
		return null;
	}
	
	
	public static void postInit()
	{
	}

	
    /**
     * ItemStack helper methods to replicate functionality from the 1.9+ ItemStack class
     */
	private static class ItemStackHelper 
	{

	    /**
	     * Compares two ItemStack instances to determine whether the items are the same, ignoring any difference in durability
	     */
	    public static boolean areItemsEqualIgnoreDurability(@Nullable ItemStack stackA, @Nullable ItemStack stackB)
	    {
	        return stackA == stackB ? true : (stackA != null && stackB != null ? (!stackA.isItemStackDamageable() ? stackA.isItemEqual(stackB) : stackB != null && stackA.getItem() == stackB.getItem()) : false);
	    }
	    
	}
	
	
}
