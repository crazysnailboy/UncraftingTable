package org.jglrxavpok.mods.decraft.item.uncrafting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;
import org.jglrxavpok.mods.decraft.item.uncrafting.RecipeHandlers.RecipeHandler;
import org.jglrxavpok.mods.decraft.item.uncrafting.RecipeHandlers.ShapedIC2RecipeHandler;
import org.jglrxavpok.mods.decraft.item.uncrafting.RecipeHandlers.ShapedMekanismRecipeHandler;
import org.jglrxavpok.mods.decraft.item.uncrafting.RecipeHandlers.ShapedOreRecipeHandler;
import org.jglrxavpok.mods.decraft.item.uncrafting.RecipeHandlers.ShapedRecipeHandler;
import org.jglrxavpok.mods.decraft.item.uncrafting.RecipeHandlers.ShapelessIC2RecipeHandler;
import org.jglrxavpok.mods.decraft.item.uncrafting.RecipeHandlers.ShapelessMekanismRecipeHandler;
import org.jglrxavpok.mods.decraft.item.uncrafting.RecipeHandlers.ShapelessOreRecipeHandler;
import org.jglrxavpok.mods.decraft.item.uncrafting.RecipeHandlers.ShapelessRecipeHandler;
import org.jglrxavpok.mods.decraft.item.uncrafting.UncraftingResult.ResultType;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
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
		String uniqueIdentifier = Item.REGISTRY.getNameForObject(itemStack.getItem()).toString();
		if (itemStack.getItemDamage() > 0) uniqueIdentifier += "," + Integer.toString(itemStack.getItemDamage()); 
		
		return ArrayUtils.indexOf(ModConfiguration.excludedItems, uniqueIdentifier) >= 0;
	}
	
	
	/**
	 * Determines the minimum number of items required for an uncrafting operation to be performed
	 * @param itemStack The ItemStack containing the target item
	 * @return A collection of the mininum required stack sizes - one element per recipe found
	 */
	public static List<Integer> getStackSizeNeeded(ItemStack itemStack)
	{
		List<Integer> list = new ArrayList<Integer>();
		if (isUncraftingDisabledForItem(itemStack)) return list;
		
		List<IRecipe> recipeList = CraftingManager.getInstance().getRecipeList();
		for ( IRecipe recipe : recipeList )
		{
			ItemStack recipeOutput = recipe.getRecipeOutput();
			if (recipeOutput != null)
			{
				if (ItemStack.areItemsEqualIgnoreDurability(itemStack, recipeOutput))
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
	public static List<ItemStack[]> findMatchingRecipes(ItemStack itemStack)
	{
		List<ItemStack[]> list = new ArrayList<ItemStack[]>();
		if (isUncraftingDisabledForItem(itemStack)) return list;
		
		List<IRecipe> recipeList = CraftingManager.getInstance().getRecipeList();
		for ( IRecipe recipe : recipeList )
		{
			ItemStack recipeOutput = recipe.getRecipeOutput();
			if (ItemStack.areItemsEqualIgnoreDurability(itemStack, recipeOutput) && recipeOutput.stackSize <= itemStack.stackSize)
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
		
		// recipe handlers for reflected IRecipe types from other mods
		try
		{
			Class c;
			
			// mekanism recipes
			c = Class.forName("mekanism.common.recipe.ShapedMekanismRecipe");
			if (c.isInstance(recipe)) return new ShapedMekanismRecipeHandler(c);

			c = Class.forName("mekanism.common.recipe.ShapelessMekanismRecipe");
			if (c.isInstance(recipe)) return new ShapelessMekanismRecipeHandler(c);
			
			// ic2 recipes
			c = Class.forName("ic2.core.recipe.AdvRecipe");
			if (c.isInstance(recipe)) return new ShapedIC2RecipeHandler(c);
			
			c = Class.forName("ic2.core.recipe.AdvShapelessRecipe");
			if (c.isInstance(recipe)) return new ShapelessIC2RecipeHandler(c);
			
			
		}
		catch(ClassNotFoundException ex) { }
		catch(Exception ex) { }

		return null;
	}
	
	
	public static UncraftingResult getUncraftingResult(EntityPlayer player, ItemStack itemStack)
	{
		
		UncraftingResult uncraftingResult = new UncraftingResult();
		
        // get the minimum stack sizes needed to uncraft the input item
		uncraftingResult.minStackSizes = getStackSizeNeeded(itemStack);
        // get the crafting grids which could result in the input item
		uncraftingResult.craftingGrids = findMatchingRecipes(itemStack);
        // determine the xp cost for the uncrafting operation
		uncraftingResult.experienceCost = getUncraftingXpCost(itemStack);
		
        // if the minimum stack size is greater than the number of items in the slot
		if (uncraftingResult.minStackSizes.size() > 0 && itemStack.stackSize < Collections.min(uncraftingResult.minStackSizes))
		{
			// set the result type as "not enough items"
			uncraftingResult.resultType = ResultType.NOT_ENOUGH_ITEMS;
		}
		// if no crafting recipe could be found
		else if (uncraftingResult.craftingGrids.size() == 0)
		{
			// set the result type as "not uncraftable"
			uncraftingResult.resultType = ResultType.NOT_UNCRAFTABLE;
		}
		// if the player is not in creative mode, and doesn't have enough XP levels 
		else if (!player.capabilities.isCreativeMode && player.experienceLevel < uncraftingResult.experienceCost)
		{
			// set the result type as "not enough xp"
			uncraftingResult.resultType = ResultType.NOT_ENOUGH_XP;
		}
		// otherwise, the uncrafting operation can be performed
		else
		{
			uncraftingResult.resultType = ResultType.VALID;
		}
		
		return uncraftingResult;
	}
	
	

	public static List<ItemStack> getItemEnchantments(ItemStack itemStack, ItemStack containerItems)
	{
        // initialise a list of itemstacks to hold enchanted books  
        ArrayList<ItemStack> enchantedBooks = new ArrayList<ItemStack>();
        
        // if the item being uncrafted has enchantments, and the container itemstack contains books
        if (itemStack.isItemEnchanted() && containerItems != null && containerItems.getItem() == Items.BOOK)
        {
            // build a map of the enchantments on the item in the input stack
            Map itemEnchantments = EnchantmentHelper.getEnchantments(itemStack);
            
            // if the item has more than one enchantment, and we have at least the same number of books as enchantments
            // create an itemstack of enchanted books with a single enchantment per book
            if (itemEnchantments.size() > 1 && itemEnchantments.size() <= containerItems.stackSize)
            {
            	// iterate through the enchantments in the map
                Iterator<?> enchantmentIds = itemEnchantments.keySet().iterator();
                while (enchantmentIds.hasNext())
                {
                	Enchantment bookEnchantment = (Enchantment)enchantmentIds.next();
                	// create a new map of enchantments which will be applied to this book
                    Map<Enchantment, Integer> bookEnchantments = new LinkedHashMap<Enchantment, Integer>();
                    // copy the current enchantment into the map
                    bookEnchantments.put(bookEnchantment, (Integer)itemEnchantments.get(bookEnchantment));
                	// create an itemstack containing an enchanted book
                    ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK, 1);
                    // place the enchantment onto the book
                    EnchantmentHelper.setEnchantments(bookEnchantments, enchantedBook);
                    // add the book to the enchanted books collection
                    enchantedBooks.add(enchantedBook);
                    // clear the book enchantments map
                    bookEnchantments.clear();
                }
            }
            
            // if there's a single enchantment, or fewer books than enchantments
            // copy all of the enchantments from the item onto a single book
            else
            {
            	// create an itemstack containing an enchanted book
                ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK, 1);
                // copy all of the enchantments from the map onto the book
                EnchantmentHelper.setEnchantments(itemEnchantments, enchantedBook);
                // add the book to the enchanted books collection
                enchantedBooks.add(enchantedBook);
            }
            
        }
        
        // return the list of enchanted books
        return enchantedBooks;
	}
	
	
	public static void postInit()
	{
	}
	
}
