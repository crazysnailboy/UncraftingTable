package org.jglrxavpok.mods.decraft.item.uncrafting;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.ArrayUtils;
import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;
import org.jglrxavpok.mods.decraft.common.config.ModJsonConfiguration;
import org.jglrxavpok.mods.decraft.common.config.ModJsonConfiguration.ItemMapping;
import org.jglrxavpok.mods.decraft.item.uncrafting.UncraftingResult.ResultType;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.NBTSensitiveRecipeHandlers.INBTSensitiveRecipeHandler;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.RecipeHandlers;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.RecipeHandlers.RecipeHandler;

import java.lang.reflect.Field;
import java.util.*;


/**
 * Main part of the Uncrafting Table. The manager is used to parse the existing recipes and find the correct one depending on the given stack.
 *
 */
public class UncraftingManager
{

	public static final List<IRecipe<?>> hardCodedRecipes = Lists.newArrayList();
	public static final List<IRecipe<?>> blockedRecipes = Lists.newArrayList();
	public static final List<ItemStack> blockedItems = Lists.<ItemStack>newArrayList();
	public static final List<ItemStack> blockedIngredients = Lists.<ItemStack>newArrayList();
	public static final List<ItemStack> removedIngredients = Lists.<ItemStack>newArrayList();
	
	public static void addUncraftingRecipe(IRecipe recipe)
	{
		hardCodedRecipes.add(recipe);
	}

	/**
	 * Performs the recipe lookup, XP cost calculation and other associated checks for an uncrafting operation.
	 * @param player The player performing the uncrafting operation
	 * @param itemStack The stack of items being uncrafted
	 * @return An object containing the details of the operation to be performed
	 */
	public static UncraftingResult getUncraftingResult(PlayerEntity player, ItemStack itemStack)
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
		else if (!player.isCreative() && player.experienceLevel < uncraftingResult.experienceCost)
		{
			// set the result type as "not enough xp"
			uncraftingResult.resultType = ResultType.NOT_ENOUGH_XP;
		}
		else
		{
			// check to see if one of more of the items in the crafting recipe have container items
			for ( ItemStack recipeStack : uncraftingResult.getCraftingGrid() )
			{
				if (!recipeStack.isEmpty() && recipeStack.getItem().hasContainerItem(recipeStack)) // the hasContainerItem parameter is usually ignored, but some mods (Immersive Engineering) need it to be there
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


	public static void recalculateResultType(UncraftingResult uncraftingResult, PlayerEntity player, ItemStack itemStack)
	{
		uncraftingResult.resultType = ResultType.INACTIVE;
		if (itemStack.isEmpty()) return;

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
		else if (!player.isCreative() && player.experienceLevel < uncraftingResult.experienceCost)
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
			if (itemStack == null)
			{
				return 0;
			}
			// if the item isn't damageable
			else if (!itemStack.getItem().isDamageable())
			{
				// the xp cost is the standard cost
				return ModConfiguration.standardLevel;
			}
			// if the item is damageable, but isn't damaged
			else if (itemStack.getItem().isDamageable() && itemStack.getDamage() == 0)
			{
				// the xp cost is the standard cost
				return ModConfiguration.standardLevel;
			}
			// if the item is damageable and is damaged
			else
			{
				// the xp cost is standard level + (damage percentage * the max level)
				int damagePercentage = (int)(((double)itemStack.getDamage() / (double)itemStack.getMaxDamage()) * 100);
				return ((ModConfiguration.maxUsedLevel * damagePercentage) / 100);
			}
		}

		return -1; // return ModConfiguration.standardLevel;
	}


	public static int recalculateExperienceCost(ItemStack inputStack, ItemStack bookStack)
	{
		int experienceCost = getUncraftingXpCost(inputStack);

		if (!bookStack.isEmpty() && !inputStack.isEmpty() && inputStack.isEnchanted())
		{
			int enchantmentCount = EnchantmentHelper.getEnchantments(inputStack).size();
			experienceCost += (enchantmentCount * ModConfiguration.enchantmentCost);
		}

		return Math.min(experienceCost, ModConfiguration.maxUsedLevel);
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

		// if uncrafting of this item is disabled either in config or via crafttweaker, return the empty list
		if (isOutputBlocked(itemStack)) return list;

		// iterate over all the crafting recipes known to the crafting manager
		List<IRecipe<?>> recipeList = getAllRecipes();
		recipeList.addAll(hardCodedRecipes);

		for ( IRecipe recipe : recipeList )
		{
			// if the current recipe can be used to craft the item
			ItemStack recipeOutput = recipe.getRecipeOutput();
			if (recipeOutput.isEmpty()) recipeOutput = RecipeHandler.getPossibleRecipeOutput(recipe, itemStack);

			if (ItemStack.areItemsEqualIgnoreDurability(itemStack, recipeOutput))
			{
				// load any custom mapping data we have for this item
				ItemMapping mapping = ModJsonConfiguration.ITEM_MAPPINGS.get(itemStack);

				// if we have mapping data...
				if (mapping != null)
				{
					// if the mapping data specifies a particular IRecipe instance, and the current recipe does not match, continue
					if ((mapping.recipeType != null) && (!recipe.getClass().getName().equals(mapping.recipeType))) continue;
					// if the mapping data specifies a match on NBT data, and the data does not match, continue
					if ((mapping.matchTag == true) && (!areItemStackSubTagsEqual(itemStack, recipeOutput, mapping.tagName))) continue;
					// if the mapping data specifies a match on a private field, and the field values do not match, continue
					if ((mapping.matchField == true) && (!arePrivateFieldValuesEqual(itemStack, recipeOutput, mapping.fieldNames))) continue;
				}


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

						// if the recipe is disallowed for uncrafting, continue
						if (isRecipeBlocked(craftingGrid)) continue; // recipe is blocked by CraftTweaker
						if (recipeContainsBlockedItems(craftingGrid)) continue; // recipe contains items blocked by CraftTweaker
						if (craftingGridContainsInputItem(itemStack, craftingGrid)) continue; // recipe output contains the input item (e.g. white wool -> white wool + bonemeal)


						// if the recipe contains items blocked by crafttweaker, remove them from the crafting grid
						craftingGrid = removeItemsFromOutputBecauseCraftTweaker(craftingGrid);

						// if we're doing a partial material return on a damaged item, remove items from the crafting grid as appropriate
						if (ModConfiguration.uncraftMethod == UncraftingMethod.JGLRXAVPOK && itemStack.isDamageable() && itemStack.isDamaged())
						{
							craftingGrid = removeItemsFromOutputByDamage(itemStack, craftingGrid);
						}

						// add the stack size and the crafting grid to the results list
						if (countFilledSlotsInCraftingGrid(craftingGrid) > 0)
						{
							Map.Entry<NonNullList<ItemStack>,Integer> pair = new AbstractMap.SimpleEntry<NonNullList<ItemStack>,Integer>(craftingGrid, minStackSize);
							list.add(pair);
						}

						// if we have custom mapping data which specifies a single recipe
						if (mapping != null && mapping.singleRecipe == true)
						{
							// we've found that recipe, so break out of the loop
							break;
						}
					}
				}
				// if we couldn't find a handler class for this IRecipe implementation, write some details to the log for debugging.
				else ModUncrafting.LOGGER.error("findMatchingRecipes :: Unknown IRecipe implementation " + recipe.getClass().getCanonicalName() + " for item " + itemStack.getItem().getRegistryName());
			}
		}

		return list;
	}

	private static List<IRecipe<?>> getAllRecipes() {
		RecipeManager recipeManager = Minecraft.getInstance().world.getRecipeManager();
		return new LinkedList<>(recipeManager.getRecipes());
	}


	private static boolean areItemStackSubTagsEqual(ItemStack stackA, ItemStack stackB, String tagName)
	{
		final String regex = "(?<=\\d+)[bdfsL](?=[,}])"; // type indicators ("b", "f", etc) preceded by digits and followed by "," or "}"

		CompoundNBT tagA = stackA.getTag();
		CompoundNBT tagB = stackB.getTag();

		if (tagA != null && tagB != null)
		{
			INBT subTagA = tagA.get(tagName);
			INBT subTagB = tagB.get(tagName);

			return (subTagA.equals(subTagB) || subTagA.toString().replaceAll(regex, "").equals(subTagB.toString().replaceAll(regex, "")));
		}

		return false;
	}

	private static boolean arePrivateFieldValuesEqual(ItemStack stackA, ItemStack stackB, String[] fieldNames)
	{
		Field field = null;
		for (String fieldNameA : fieldNames) {
			try {
				field = ObfuscationReflectionHelper.findField(ItemStack.class, fieldNameA);
				break; // found our field
			} catch (ObfuscationReflectionHelper.UnableToFindFieldException e) {
				// silence it
			}
		}
		try {
			field.setAccessible(true);
			Object oA = field.get(stackA);
			Object oB = field.get(stackB);
			return ((oA != null) == (oB != null)) && oA.equals(oB);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return false;
	}


	private static int countFilledSlotsInCraftingGrid(NonNullList<ItemStack> craftingGrid)
	{
		int result = 0;
		for ( int i = 0 ; i < craftingGrid.size() ; i++ )
		{
			if (!craftingGrid.get(i).isEmpty()) result++;
		}
		return result;
	}


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
	 * Modifies the crafting recipe for a damageable item to return some of the ingredients depending on the damage of the input item.
	 * @param stack The damaged item
	 * @param craftingGrid The unmodified crafting recipe of the damageable item
	 * @return The modified crafting recipe with some ingredients removed
	 */
	private static NonNullList<ItemStack> removeItemsFromOutputByDamage(ItemStack stack, NonNullList<ItemStack> craftingGrid)
	{
		// calculate the percentage durability remaining on the item
		double damagePercentage = (100 * ((double)stack.getDamage() / (double)stack.getMaxDamage()));
		double durabilityPercentage = 100 - (100 * ((double)stack.getDamage() / (double)stack.getMaxDamage()));

		// iterate through the itemstacks in the crafting recipe to determine the unique materials used, and the total number of each item
		HashMap<String, Map.Entry<ItemStack,Integer>> materials = new HashMap<String, Map.Entry<ItemStack,Integer>>();
		for ( ItemStack recipeStack : craftingGrid )
		{
			if (!recipeStack.isEmpty())
			{
				// get the unique identifier string for the item in the current stack, and append a metadata value if appropriate
				String key = ForgeRegistries.ITEMS.getKey(recipeStack.getItem()).toString();
				if (recipeStack.getDamage() != 0) key += "," + recipeStack.getDamage();

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

			// if the stack contains sticks
			if (materialStack.getItem().isIn(Tags.Items.RODS_WOODEN))
			{
				// calculate the total number of full items which most closely represent the percentage durability remaining on the item
				// rounding up to the nearest item
				itemCount = (int)Math.ceil(amount * (durabilityPercentage / (double)100));
			}
			// if the stack contains leather and we should use rabbit hide
			else if (ModConfiguration.useRabbitHide && materialStack.getItem().isIn(Tags.Items.LEATHER))
			{
				nuggetStack = new ItemStack(Items.RABBIT_HIDE, 1);

				// calculate the number of pieces of leather and pieces of rabbit hide which most closely represent the percentage durability remaining on the item
				// rounding down to the nearest piece of rabbit hide
				itemCount = (int)Math.floor(amount * (durabilityPercentage / 100));
				nuggetCount = ((int)Math.floor((amount * 4) * (durabilityPercentage / 100))) - (itemCount * 4);
			}
			// if we found a nugget item in the ore dictionary
			else if (nuggetStack != ItemStack.EMPTY)
			{
				// calculate the number of full items and nuggets which most closely represent the percentage durability remaining on the item
				// rounding down to the nearest nugget
				itemCount = (int)Math.floor(amount * (durabilityPercentage / (double)100));
				nuggetCount = ((int)Math.floor((amount * 9) * (durabilityPercentage / (double)100))) - (itemCount * 9);
			}
			// if there's no nugget for this item in the ore dictionary
			else
			{
				// calculate the total number of full items which most closely represent the percentage durability remaining on the item
				// rounding down to the nearest item
				itemCount = (int)Math.floor(amount * (durabilityPercentage / (double)100));
			}

			// ensure that at least one nugget is returned regardless of durability
			if (ModConfiguration.ensureReturn && itemCount == 0 && nuggetCount == 0 && nuggetStack != null) nuggetCount = 1;

			// flip the item count to become items to remove instead of items to leave
			itemCount = amount - itemCount;


			// remove the items from the crafting grid until we've removed the appropriate number.
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
						craftingGrid.set(i, new ItemStack(nuggetStack.getItem(), nuggetCount));
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
		Tag[] oreTypes = { Tags.Items.GEMS, Tags.Items.NUGGETS };

		Tag[] oreIds = oreStack.getItem().getTags().stream().map(rl -> ItemTags.getCollection().get(rl)).toArray(size -> new Tag[size]);
		for ( Tag oreId : oreIds )
		{
			String oreName = oreId.getId().getPath(); // e.g. "gems/diamond"
			String[] oreNameParts = oreName.split("/"); // e.g. { "gem", "diamond" }

			if ((oreNameParts.length == 1) || (oreNameParts.length == 2 && ArrayUtils.indexOf(oreTypes, oreNameParts[0]) >= 0))
			{
				String oreNamePart = oreNameParts[oreNameParts.length - 1];

				String nuggetName = "nugget" + oreNamePart; // e.g. "nuggetDiamond"

				if(!oreId.getAllElements().isEmpty()) {
					Item nuggetOres = (Item) oreId.getAllElements().iterator().next(); // get first
					return new ItemStack(nuggetOres);
				}
			}

		}
		return ItemStack.EMPTY;
	}


	private static NonNullList<ItemStack> removeItemsFromOutputBecauseCraftTweaker(NonNullList<ItemStack> craftingGrid)
	{
		for ( int i = 0 ; i < craftingGrid.size() ; i++ )
		{
			if (shouldIngredientBeRemoved(craftingGrid.get(i)))
			{
				craftingGrid.set(i, ItemStack.EMPTY);
			}
		}
		return craftingGrid;
	}


	private static boolean isRecipeBlocked(NonNullList<ItemStack> craftingGrid)
	{
		InventoryCrafting craftMatrix = new InventoryCrafting(craftingGrid);
		for (IRecipe irecipe : blockedRecipes)
		{
			if (irecipe.matches(craftMatrix, null)) return true;
		}
		return false;
	}

	private static boolean isOutputBlocked(ItemStack stack)
	{
		// first check to see if the output is blocked by config
		String registryName = stack.getItem().getRegistryName().toString();
		if (ArrayUtils.indexOf(ModConfiguration.excludedItems, registryName) >= 0) return true;
		if (ArrayUtils.indexOf(ModConfiguration.excludedItems, registryName + "," + Integer.toString(stack.getDamage())) >= 0) return true;

		// then check to see if it's blocked by crafttweaker
		for ( ItemStack stackB : blockedItems )
		{
			// if the items are equal...
			if (ItemStack.areItemsEqualIgnoreDurability(stack, stackB))
			{
				// if the blocked stack doesn't have an NBT tag, consider this a match regardless of the NBT data on the test stack.
				// if it does, then it's a match if the two tags match.
				if (!stackB.hasTag())
				{
					return true;
				}
				else if (ItemStack.areItemStackTagsEqual(stack, stackB))
				{
					return true;
				}
				else if (stack.hasTag())
				{
					for (String key : stackB.getTag().keySet())
					{
						if (areItemStackSubTagsEqual(stack, stackB, key))
						{
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private static boolean isIngredientBlocked(ItemStack stack)
	{
		for ( ItemStack stackB : blockedIngredients )
		{
			if (ItemStack.areItemsEqualIgnoreDurability(stack, stackB) && ItemStack.areItemStackTagsEqual(stack, stackB)) return true;
		}
		return false;
	}

	private static boolean recipeContainsBlockedItems(NonNullList<ItemStack> craftingGrid)
	{
		for ( ItemStack stack : craftingGrid )
		{
			if (isIngredientBlocked(stack)) return true;
		}
		return false;
	}

	private static boolean shouldIngredientBeRemoved(ItemStack stack)
	{
		for ( ItemStack stackB : removedIngredients )
		{
			if (ItemStack.areItemsEqualIgnoreDurability(stack, stackB) && ItemStack.areItemStackTagsEqual(stack, stackB)) return true;
		}
		return false;
	}


	/**
	 * extended InventoryCrafting class used for comparing recipe outputs
	 *
	 */
	private static class InventoryCrafting extends net.minecraft.inventory.CraftingInventory
	{

		public InventoryCrafting(NonNullList<ItemStack> craftingGrid)
		{
			super(null, 3, 3);
			for ( int i = 0 ; i < craftingGrid.size() ; i++ ) this.setInventorySlotContents(i, craftingGrid.get(i));
		}

	    @Override
		public void setInventorySlotContents(int index, ItemStack stack)
	    {
			// the super class will throw an exception when attempting to call "this.eventHandler.onCraftMatrixChanged(this);", but we don't care
	    	try
	    	{
	    		super.setInventorySlotContents(index, stack);
	    	}
	    	catch(NullPointerException ex){}
	    }

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
