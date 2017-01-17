package org.jglrxavpok.mods.decraft.item.uncrafting;

import java.util.ArrayList;
import java.util.HashMap;
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
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.oredict.OreDictionary;
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
			if (ItemStack.areItemsEqualIgnoreDurability(itemStack, recipeOutput)) // && recipeOutput.stackSize <= itemStack.stackSize)
			{
				RecipeHandler handler = getRecipeHandler(recipe);
				if (handler != null)
				{
					if (ModConfiguration.uncraftMethod == UncraftingMethod.JGLRXAVPOK && itemStack.isItemStackDamageable() && itemStack.isItemDamaged())
					{
						list.add(removeItemsFromOutputByDamage(itemStack, handler.getCraftingGrid(recipe)));
					}
					else
					{
						list.add(handler.getCraftingGrid(recipe));
					}
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
		if (recipe instanceof ShapelessRecipes) return new ShapelessRecipeHandler();
		if (recipe instanceof ShapedRecipes) return new ShapedRecipeHandler();
		// Forge Ore Dictionary recipe handlers
		if (recipe instanceof ShapelessOreRecipe) return new ShapelessOreRecipeHandler();
		if (recipe instanceof ShapedOreRecipe) return new ShapedOreRecipeHandler();
		
		// recipe handlers for reflected IRecipe types from other mods
		try
		{
			// ic2 recipes
			if (ShapedIC2RecipeHandler.recipeClass.isInstance(recipe)) return new ShapedIC2RecipeHandler();
			if (ShapelessIC2RecipeHandler.recipeClass.isInstance(recipe)) return new ShapelessIC2RecipeHandler();
			
			// mekanism recipes
			if (ShapedMekanismRecipeHandler.recipeClass.isInstance(recipe)) return new ShapedMekanismRecipeHandler();
			if (ShapelessMekanismRecipeHandler.recipeClass.isInstance(recipe)) return new ShapelessMekanismRecipeHandler();
			
		}
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
		if (uncraftingResult.minStackSizes.size() > 0 && itemStack.stackSize < uncraftingResult.getMinStackSize())
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
		// if one of more of the items in the crafting recipe have container items
		else if (recipeHasContainerItems(uncraftingResult.craftingGrids.get(uncraftingResult.selectedCraftingGrid)))
		{
			// set the result type as "need container items"
			uncraftingResult.resultType = ResultType.NEED_CONTAINER_ITEMS;
		}
		// otherwise, the uncrafting operation can be performed
		else
		{
			uncraftingResult.resultType = ResultType.VALID;
		}
		
		return uncraftingResult;
	}
	
	
	private static Boolean recipeHasContainerItems(ItemStack[] craftingGrid)
	{
		for ( ItemStack itemStack : craftingGrid )
		{
			if (itemStack != null && itemStack.getItem().hasContainerItem(null)) // the hasContainerItem parameter is ignored, and ItemStack internally calls the deprecated version without the parameter anyway...
			{
				return true;
			}
		}
		return false;
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
	
	
	private static ItemStack getNuggetForOre(ItemStack oreStack)
	{

//		// *** copied from com.jaquadro.minecraft.storagedrawers.config.OreDictRegistry ***
//		String[] oreTypes = { "ore", "block", "ingot", "nugget" };
//		String[] oreMaterials = { "Iron", "Gold", "Diamond", "Emerald", "Aluminum", "Aluminium", "Tin", "Copper", "Lead", "Silver", "Platinum", "Nickel", "Osmium", "Invar", "Bronze", "Electrum", "Enderium" };
//		// *** copied from com.jaquadro.minecraft.storagedrawers.config.OreDictRegistry ***
		
		
        String[] oreTypes = { "gem", "ingot" };
        String[] oreMaterials = { "Diamond", "Emerald", "Gold", "Iron" };
        
		
		int[] oreIds = OreDictionary.getOreIDs(oreStack);
		for ( int oreId : oreIds )
		{
			String oreName = OreDictionary.getOreName(oreId); // e.g. "gemDiamond"
			String[] oreNameParts = oreName.split("(?=\\p{Upper})"); // e.g. { "gem", "Diamond" }
			
			if (oreNameParts.length == 2 && ArrayUtils.indexOf(oreTypes, oreNameParts[0]) >= 0)
			{
				String nuggetName = "nugget" + oreNameParts[1]; // e.g. "nuggetDiamond"

				List<ItemStack> nuggetOres = OreDictionary.getOres(nuggetName);
				if (!nuggetOres.isEmpty())
				{
					ItemStack nuggetOre = nuggetOres.get(0);
					return nuggetOre;
				}
				
			}
			
		}
		return null;
	}
	
	
	
	public static ItemStack[] removeItemsFromOutputByDamage(ItemStack stack, ItemStack[] craftingGrid)
	{
		// calculate the percentage durability remaining on the item
		double damagePercentage = (100 * ((double)stack.getItemDamage() / (double)stack.getMaxDamage()));
		double durabilityPercentage = 100 - (100 * ((double)stack.getItemDamage() / (double)stack.getMaxDamage()));
		

		// iterate through the itemstacks in the crafting recipe to determine the unique materials used, and the total number of each item
		HashMap<String, Integer> materials = new HashMap<String, Integer>();
		for ( ItemStack recipeStack : craftingGrid )
		{
			if (recipeStack != null)
			{
				String key = Item.REGISTRY.getNameForObject(recipeStack.getItem()).toString();
				materials.put(key, (materials.containsKey(key) ? materials.get(key) : 0) + recipeStack.stackSize);
			}
		}
		
		// for each unique material in the crafting recipe...
		for ( String key : materials.keySet())
		{
			// get an itemstack of the material from it's registry name (TODO: probably don't need to condense this to a string in the first place...)
			ItemStack materialStack = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation(key)));

			// check the ore dictionary to see if this material has a matching nugget
			ItemStack nuggetStack = getNuggetForOre(materialStack);

			
			int amount = materials.get(key);
			
			int itemCount = 0;
			int nuggetCount = 0;
			
			// if we found a nugget item in the ore dictionary
			if (nuggetStack != null)
			{
				// calculate the number of full items and nuggets which most closely represent the percentage durability remaining on the item
				// rounding down to the nearest nugget
				itemCount = (int)Math.floor(amount * (durabilityPercentage / (double)100));
				nuggetCount = ((int)Math.floor((amount * 9) * (durabilityPercentage / (double)100))) - (itemCount * 9);
			}
			// if the stack contains sticks
			else if (ArrayUtils.contains(OreDictionary.getOreIDs(materialStack), OreDictionary.getOreID("stickWood")))
			{
				// calculate the total number of full items which most closely represent the percentage durability remaining on the item
				// rounding up to the nearest item
				itemCount = (int)Math.ceil(amount * (durabilityPercentage / (double)100));
			}
			
			// if there's no nugget for this item in the ore dictionary
			else
			{
				// calculate the total number of full items which most closely represent the percentage durability remaining on the item
				// rounding up or down to the nearest item
				itemCount = (int)Math.round(amount * (durabilityPercentage / (double)100));
			}
			
			// flip the item count to become items to remove instead of items to leave
			itemCount = amount - itemCount;
			

			for ( int i = 0 ; i < craftingGrid.length ; i++ )
			{
				if (craftingGrid[i] != null && craftingGrid[i].isItemEqual(materialStack))
				{
					if (itemCount > 0)
					{
						craftingGrid[i] = null;
						itemCount--;
					}
					if (itemCount == 0 && nuggetCount > 0)
					{
						craftingGrid[i] = new ItemStack(nuggetStack.getItem(), nuggetCount, nuggetStack.getItemDamage());
						nuggetCount = 0;
					}
				}
			}
			
		}
		
		return craftingGrid;
		
		
//		Map.Entry<String,Map.Entry<Integer,Integer>> m = new AbstractMap.SimpleEntry<String,Map.Entry<Integer,Integer>>("Hello World", new AbstractMap.SimpleEntry<Integer,Integer>(0, 0));		

		
		
//        if (ModConfiguration.uncraftMethod == 0)
//        {
//            int count = 0;
//            ItemStack s1 = uncraftIn.getStackInSlot(0);
//
//            int percent = (int) (((double) s1.getItemDamage() / (double) s1.getMaxDamage()) * 100);
//            for (int i = 0; i < items.length; i++ )
//            {
//                if (items[i] != null)
//                    count++ ;
//            }
//            int toRemove = Math.round((float) (percent * count) / 100f);
//            if (toRemove > 0)
//                for (int i = 0; i < items.length; i++ )
//                {
//                    if (items[i] != null)
//                    {
//                        toRemove-- ;
//                        items[i] = null;
//                        if (toRemove <= 0)
//                        {
//                            break;
//                        }
//                    }
//                }
//        }
		
	}
	
	
	public static void postInit()
	{
	}
	
}
