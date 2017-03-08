package org.jglrxavpok.mods.decraft.item.uncrafting;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;
import org.jglrxavpok.mods.decraft.item.uncrafting.UncraftingResult.ResultType;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.NBTSensitiveRecipeHandlers.INBTSensitiveRecipeHandler;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.RecipeHandlers;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.RecipeHandlers.RecipeHandler;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;


/**
 * Main part of the Uncrafting Table. The manager is used to parse the existing recipes and find the correct one depending on the given stack.
 *
 */
public class UncraftingManager
{

	/**
	 * Performs the recipe lookup, XP cost calculation and other associated checks for an uncrafting operation.
	 * @param player The player performing the uncrafting operation
	 * @param itemStack The stack of items being uncrafted
	 * @return An object containing the details of the operation to be performed
	 */
	public static UncraftingResult getUncraftingResult(EntityPlayer player, ItemStack itemStack)
	{

		UncraftingResult uncraftingResult = new UncraftingResult();

		// get the crafting grids and minimum stack sizes which could result in the input item
		uncraftingResult.craftingGrids = findMatchingRecipes(itemStack);
		// determine the xp cost for the uncrafting operation
		uncraftingResult.experienceCost = getUncraftingXpCost(itemStack);

		// if the minimum stack size is greater than the number of items in the slot
		if (uncraftingResult.getRecipeCount() > 0 && itemStack.getCount() < uncraftingResult.getMinStackSize())
		{
			// set the result type as "not enough items"
			uncraftingResult.resultType = ResultType.NOT_ENOUGH_ITEMS;
		}
		// if no crafting recipe could be found
		else if (uncraftingResult.getRecipeCount() == 0)
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
		else
		{
			// check to see if one of more of the items in the crafting recipe have container items
			for ( ItemStack recipeStack : uncraftingResult.getCraftingGrid() )
			{
				if (recipeStack != ItemStack.EMPTY && recipeStack.getItem().hasContainerItem(recipeStack)) // the hasContainerItem parameter is usually ignored, but some mods (Immersive Engineering) need it to be there
				{
					uncraftingResult.resultType = ResultType.NEED_CONTAINER_ITEMS;
					break;
				}
			}

			// if no container items are present, and all the other checks pass
			if (uncraftingResult.resultType != ResultType.NEED_CONTAINER_ITEMS)
			{
				// the uncrafting operation can be performed
				uncraftingResult.resultType = ResultType.VALID;
			}

		}
		return uncraftingResult;
	}


	public static void recalculateResultType(UncraftingResult uncraftingResult, EntityPlayer player, ItemStack itemStack)
	{
		uncraftingResult.resultType = ResultType.INACTIVE;

		// if the minimum stack size is greater than the number of items in the slot
		if (uncraftingResult.getRecipeCount() > 0 && itemStack.getCount() < uncraftingResult.getMinStackSize())
		{
			// set the result type as "not enough items"
			uncraftingResult.resultType = ResultType.NOT_ENOUGH_ITEMS;
		}
		// if no crafting recipe could be found
		else if (uncraftingResult.getRecipeCount() == 0)
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
		else
		{
			// check to see if one of more of the items in the crafting recipe have container items
			for ( ItemStack recipeStack : uncraftingResult.getCraftingGrid() )
			{
				if (recipeStack != ItemStack.EMPTY && recipeStack.getItem().hasContainerItem(recipeStack)) // the hasContainerItem parameter is usually ignored, but some mods (Immersive Engineering) need it to be there
				{
					uncraftingResult.resultType = ResultType.NEED_CONTAINER_ITEMS;
					break;
				}
			}

			// if no container items are present, and all the other checks pass
			if (uncraftingResult.resultType != ResultType.NEED_CONTAINER_ITEMS)
			{
				// the uncrafting operation can be performed
				uncraftingResult.resultType = ResultType.VALID;
			}

		}
	}


	/**
	 * Copies enchantments from an item onto a collection of enchanted books.
	 * @param itemStack The item which has the enchantments to copy
	 * @param containerItems One or more empty books onto which to place the enchantments
	 * @return A collection of itemstacks of enchanted books
	 */
	public static List<ItemStack> getItemEnchantments(ItemStack itemStack, ItemStack containerItems)
	{
		// initialise a list of itemstacks to hold enchanted books
		ArrayList<ItemStack> enchantedBooks = new ArrayList<ItemStack>();

		// if the item being uncrafted has enchantments, and the container itemstack contains books
		if (itemStack.isItemEnchanted() && !containerItems.isEmpty() && containerItems.getItem() == Items.BOOK)
		{
			// build a map of the enchantments on the item in the input stack
			Map itemEnchantments = EnchantmentHelper.getEnchantments(itemStack);

			// if the item has more than one enchantment, and we have at least the same number of books as enchantments
			// create an itemstack of enchanted books with a single enchantment per book
			if (itemEnchantments.size() > 1 && itemEnchantments.size() <= containerItems.getCount())
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


	/**
	 * Returns the available crafting recipes and associated minimum stack sizes which can be used to perform an uncrafting operation
	 * @param itemStack The ItemStack containing the target item
	 * @return A collection of the ItemStack arrays representing the crafting recipe - one element per recipe found
	 */
	private static List<Map.Entry<NonNullList<ItemStack>,Integer>> findMatchingRecipes(ItemStack itemStack)
	{
		// initialize a list of tuples to hold the crafting grid and stack sizes
		List<Map.Entry<NonNullList<ItemStack>,Integer>> list = new ArrayList<Map.Entry<NonNullList<ItemStack>,Integer>>();

		// if uncrafting of this item is disabled in config, return the empty list
		String itemName = Item.REGISTRY.getNameForObject(itemStack.getItem()).toString();
		String itemNameWithDamage = itemName + (itemStack.getItemDamage() > 0 ? "," + Integer.toString(itemStack.getItemDamage()) : "");

		if (ArrayUtils.indexOf(ModConfiguration.excludedItems, itemName) >= 0) return list;
		if (ArrayUtils.indexOf(ModConfiguration.excludedItems, itemNameWithDamage) >= 0) return list;


		// iterate over all the crafting recipes known to the crafting manager
		List<IRecipe> recipeList = CraftingManager.getInstance().getRecipeList();
		for ( IRecipe recipe : recipeList )
		{
			// if the current recipe can be used to craft the item
			ItemStack recipeOutput = recipe.getRecipeOutput();
			if (recipeOutput.isEmpty()) recipeOutput = RecipeHandler.getPossibleRecipeOutput(recipe, itemStack);

			if (ItemStack.areItemsEqualIgnoreDurability(itemStack, recipeOutput))
			{
				// get an instance of the appropriate handler class for the IRecipe type of the crafting recipe
				RecipeHandler handler = RecipeHandlers.HANDLERS.get(recipe.getClass());
				if (handler != null)
				{
					// if the recipe is nbt sensitive, copy the input itemstack into the recipe handler
					if (handler instanceof INBTSensitiveRecipeHandler) ((INBTSensitiveRecipeHandler)handler).setInputStack(itemStack.copy());

					// get the minimum stack size required to uncraft, and the itemstacks that comprise the crafting ingredients
					int minStackSize = recipeOutput.getCount();
					NonNullList<ItemStack> craftingGrid = handler.getCraftingGrid(recipe);

					if (!craftingGrid.isEmpty())
					{
						// if the recipe output contains the input item, disallow use of this recipe for uncrafting (e.g. white wool -> white wool + bonemeal)
						if (craftingGridContainsInputItem(itemStack, craftingGrid)) continue;

						// if we're doing a partial material return on a damaged item, remove items from the crafting grid as appropriate
						if (ModConfiguration.uncraftMethod == UncraftingMethod.JGLRXAVPOK && itemStack.isItemStackDamageable() && itemStack.isItemDamaged())
						{
							craftingGrid = removeItemsFromOutputByDamage(itemStack, craftingGrid);
						}

						// add the stack size and the crafting grid to the results list
						Map.Entry<NonNullList<ItemStack>,Integer> pair = new AbstractMap.SimpleEntry<NonNullList<ItemStack>,Integer>(craftingGrid, minStackSize);
						list.add(pair);
					}
				}
				// if we couldn't find a handler class for this IRecipe implementation, write some details to the log for debugging.
				else ModUncrafting.instance.getLogger().error("findMatchingRecipes :: Unknown IRecipe implementation " + recipe.getClass().getCanonicalName() + " for item " + itemName);
			}
		}

		return list;
	}


	/**
	 * Determines whether the crafting grid contains the input item
	 * @param stack The item being uncrafted
	 * @param craftingGrid The unmodified crafting recipe of the damageable item
	 * @return True if one or more item from crafting grid matches the item being uncrafted
	 */
	private static boolean craftingGridContainsInputItem(ItemStack stack, NonNullList<ItemStack> craftingGrid)
	{
		for ( ItemStack recipeStack : craftingGrid )
		{
			if (ItemStack.areItemsEqual(stack, recipeStack))
			{
				return true;
			}
		}
		return false;
	}


	/**
	 * Determines the XP cost of the uncrafting operation
	 * @param itemStack The ItemStack containing the target item
	 * @return The number of XP levels required to complete the operation
	 */
	private static int getUncraftingXpCost(ItemStack itemStack)
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
	 * Modifies the crafting recipe for a damageable item to return some of the ingredients depending on the damage of the input item.
	 * @param stack The damaged item
	 * @param craftingGrid The unmodified crafting recipe of the damageable item
	 * @return The modified crafting recipe with some ingredients removed
	 */
	private static NonNullList<ItemStack> removeItemsFromOutputByDamage(ItemStack stack, NonNullList<ItemStack> craftingGrid)
	{
		// calculate the percentage durability remaining on the item
		double damagePercentage = (100 * ((double)stack.getItemDamage() / (double)stack.getMaxDamage()));
		double durabilityPercentage = 100 - (100 * ((double)stack.getItemDamage() / (double)stack.getMaxDamage()));


		// iterate through the itemstacks in the crafting recipe to determine the unique materials used, and the total number of each item
		HashMap<String, Map.Entry<ItemStack,Integer>> materials = new HashMap<String, Map.Entry<ItemStack,Integer>>();
		for ( ItemStack recipeStack : craftingGrid )
		{
			if (!recipeStack.isEmpty())
			{
				// get the unique identifier string for the item in the current stack, and append a metadata value if appropriate
				String key = Item.REGISTRY.getNameForObject(recipeStack.getItem()).toString();
				if (recipeStack.getItemDamage() != 0) key += "," + recipeStack.getItemDamage();

				// if the map already contains a stack for this item, increment the number of items in the map
				if (materials.containsKey(key))
				{
					materials.get(key).setValue(materials.get(key).getValue() + recipeStack.getCount());
				}
				// if the map doesn't already contain a stack for this item, add the stack and the number of items in the stack
				else
				{
					Map.Entry<ItemStack,Integer> value = new AbstractMap.SimpleEntry<ItemStack,Integer>(recipeStack.copy(), recipeStack.getCount());
					value.getKey().setCount(1);
					materials.put(key, value);
				}
			}
		}

		// for each unique material in the crafting recipe...
		for ( String key : materials.keySet())
		{
			// get the itemstack of the material from the materials map
			ItemStack materialStack = materials.get(key).getKey();

			// check the ore dictionary to see if this material has a matching nugget
			ItemStack nuggetStack = getNuggetForOre(materialStack);


			int amount = materials.get(key).getValue();

			int itemCount = 0;
			int nuggetCount = 0;

			// if we found a nugget item in the ore dictionary
			if (nuggetStack != ItemStack.EMPTY)
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
				// rounding down to the nearest item
				itemCount = (int)Math.floor(amount * (durabilityPercentage / (double)100));
			}

			// flip the item count to become items to remove instead of items to leave
			itemCount = amount - itemCount;


			for ( int i = 0 ; i < craftingGrid.size() ; i++ )
			{
				if (craftingGrid.get(i) != null && craftingGrid.get(i).isItemEqual(materialStack))
				{
					if (itemCount > 0)
					{
						craftingGrid.set(i, ItemStack.EMPTY);
						itemCount--;
					}
					if (itemCount == 0 && nuggetCount > 0)
					{
						craftingGrid.set(i, new ItemStack(nuggetStack.getItem(), nuggetCount, nuggetStack.getItemDamage()));
						nuggetCount = 0;
					}
				}
			}

		}

		return craftingGrid;
	}


	/**
	 * Helper method to check the ore dictionary for nuggets of the same material type as a gem or an ingot.
	 * @param oreStack The ItemStack containing ingots or gems we want to match
	 * @return An ItemStack containing the nugget item if one was found.
	 */
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
		return ItemStack.EMPTY;
	}


	/**
	 * Constants to identify the different uncrafting algorithms
	 */
	private static class UncraftingMethod
	{
		public static final int JGLRXAVPOK = 0;
		public static final int XELL75_ZENEN = 1;
	}

}
