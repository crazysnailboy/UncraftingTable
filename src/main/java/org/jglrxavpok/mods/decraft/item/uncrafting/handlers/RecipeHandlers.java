package org.jglrxavpok.mods.decraft.item.uncrafting.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.NBTSensitiveRecipeHandlers.FireworksRecipeHandler;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.NBTSensitiveRecipeHandlers.TippedArrowRecipeHandler;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.external.AE2RecipeHandlers.ShapedAE2RecipeHandler;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.external.AE2RecipeHandlers.ShapelessAE2RecipeHandler;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.external.IC2RecipeHandlers.ShapedIC2RecipeHandler;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.external.IC2RecipeHandlers.ShapelessIC2RecipeHandler;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.external.MekanismRecipeHandlers.ShapedMekanismRecipeHandler;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.external.MekanismRecipeHandlers.ShapelessMekanismRecipeHandler;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.external.TinkersRecipeHandlers;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeFireworks;
import net.minecraft.item.crafting.RecipeTippedArrow;
import net.minecraft.item.crafting.RecipesMapExtending;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;


/**
 * Recipe Handlers return the "crafting grid" depending on a crafting recipe.
 *
 */
public final class RecipeHandlers
{

	public static class RecipeMap<T> extends HashMap<Class<? extends IRecipe>, T>
	{
		public T get(Class<? extends IRecipe> key)
		{
			T result = super.get(key);
			while (result == null && key.getSuperclass() != Object.class) // while (result == null && key.getSuperclass() != IRecipe.class)
			{
				key = (Class<? extends IRecipe>)key.getSuperclass();
				result = super.get(key);
			}
			return result;
		}
	}


	public static final RecipeMap<RecipeHandler> HANDLERS = new RecipeMap<RecipeHandler>();
	private static final RecipeMap<ItemStack[]> RECIPE_OUTPUTS = new RecipeMap<ItemStack[]>();


	public static void postInit()
	{
		buildHandlerMap();
		buildRecipeOutputMap();
	}


	private static void buildHandlerMap()
	{
		// RecipesMapExtending extends ShapedRecipes, and causes a crash when attempting to uncraft a map
		HANDLERS.put(RecipesMapExtending.class, null);

		// vanilla Minecraft recipe handlers
		HANDLERS.put(ShapedRecipes.class, new ShapedRecipeHandler());
		HANDLERS.put(ShapelessRecipes.class, new ShapelessRecipeHandler());
		HANDLERS.put(RecipeFireworks.class, new FireworksRecipeHandler());
		HANDLERS.put(RecipeTippedArrow.class, new TippedArrowRecipeHandler());

		// Forge Ore Dictionary recipe handlers
		HANDLERS.put(ShapedOreRecipe.class, new ShapedOreRecipeHandler());
		HANDLERS.put(ShapelessOreRecipe.class, new ShapelessOreRecipeHandler());

		// applied energistics 2 handlers
		if (ShapedAE2RecipeHandler.recipeClass != null) HANDLERS.put(ShapedAE2RecipeHandler.recipeClass, new ShapedAE2RecipeHandler());
		if (ShapelessAE2RecipeHandler.recipeClass != null) HANDLERS.put(ShapelessAE2RecipeHandler.recipeClass, new ShapelessAE2RecipeHandler());

		// industrialcraft 2 recipe handlers
		if (ShapedIC2RecipeHandler.recipeClass != null) HANDLERS.put(ShapedIC2RecipeHandler.recipeClass, new ShapedIC2RecipeHandler());
		if (ShapelessIC2RecipeHandler.recipeClass != null) HANDLERS.put(ShapelessIC2RecipeHandler.recipeClass, new ShapelessIC2RecipeHandler());

		// mekanism recipe handlers
		if (ShapedMekanismRecipeHandler.recipeClass != null) HANDLERS.put(ShapedMekanismRecipeHandler.recipeClass, new ShapedMekanismRecipeHandler());
		if (ShapelessMekanismRecipeHandler.recipeClass != null) HANDLERS.put(ShapelessMekanismRecipeHandler.recipeClass, new ShapelessMekanismRecipeHandler());

		// tinker's construct recipe handlers
		if (TinkersRecipeHandlers.TableRecipeHandler.recipeClass != null) HANDLERS.put(TinkersRecipeHandlers.TableRecipeHandler.recipeClass, new TinkersRecipeHandlers.TableRecipeHandler());
	}

	private static void buildRecipeOutputMap()
	{
		RECIPE_OUTPUTS.put(RecipeFireworks.class, new ItemStack[] { new ItemStack(Items.FIREWORK_CHARGE), new ItemStack(Items.FIREWORKS, 3) });
		RECIPE_OUTPUTS.put(RecipeTippedArrow.class, new ItemStack[] { new ItemStack(Items.TIPPED_ARROW, 8) });
	}


	/**
	 * Abstract base class extended by the different types of recipe handler
	 *
	 */
	public static abstract class RecipeHandler
	{
		public abstract ItemStack[] getCraftingGrid(IRecipe r);


		/**
		 * Checks a list of known output items from an IRecipe implementation to see if the input stack contains one of those items,
		 * and returns the input stack if present in the possible output list.
		 * Used for when an IRecipe implementation returns a null value from getRecipeOutput(),
		 */
		public static ItemStack getPossibleRecipeOutput(IRecipe r, ItemStack inputStack)
		{
			ItemStack[] outputStacks = RecipeHandlers.RECIPE_OUTPUTS.get(r.getClass());
			if (outputStacks != null && outputStacks.length > 0)
			{
				for ( ItemStack outputStack : outputStacks )
				{
					if (ItemStack.areItemsEqual(inputStack, outputStack))
					{
						return outputStack.copy();
					}
				}
			}
			return null;
		}


		/**
		 * Takes a list of ItemStacks from a shaped recipe and correctly positions them according to the recipe width and height
		 */
		protected static ItemStack[] reshapeRecipe(List<ItemStack> recipeItems, int recipeWidth, int recipeHeight)
		{
			ItemStack[] stacks = new ItemStack[9];
			for ( int row = 0 ; row < recipeHeight ; row++ )
			{
				for (int col = 0 ; col < recipeWidth ; col++ )
				{
					stacks[(row * 3) + col] = recipeItems.get(col + row * recipeWidth);
				}
			}
			return stacks;
		}


		/**
		 * Converts a collection of OreDictionary recipe items into a list of ItemStacks
		 */
		protected static List<ItemStack> getOreRecipeItems(List<Object> itemObjects)
		{
			List<ItemStack> itemStacks = new ArrayList<ItemStack>();
			for ( Object itemObject : itemObjects)
			{
				ItemStack itemStack;

				if (itemObject instanceof ItemStack)
				{
					itemStack = (ItemStack)itemObject;
				}
				else if (itemObject instanceof List)
				{
					List list = (List)itemObject;

					if (list.isEmpty()) // this happens if there's an ore dictionary recipe registered, but no items registered for that dictionary entry
					{
						// abort parsing this recipe and return an empty list
						itemStacks.clear();
						break;
					}

					itemStack = ((List<ItemStack>)itemObject).get(0);
				}
				else itemStack = null;

				itemStacks.add(itemStack);
			}
			return itemStacks;
		}


		/**
		 * Copies the ItemStacks in a list to a new list, whilst normalising the item damage for the OreDictionary wildcard value
		 */
		protected static List<ItemStack> copyRecipeStacks(List<ItemStack> inputStacks)
		{
			List<ItemStack> outputStacks = new ArrayList<ItemStack>();

			for ( ItemStack inputStack : inputStacks )
			{
				if (inputStack != null)
				{
					ItemStack outputStack = inputStack.copy();
					if (outputStack.getItemDamage() == Short.MAX_VALUE) outputStack.setItemDamage(0);
					outputStacks.add(outputStack);
				}
				else outputStacks.add(null);
			}

			return outputStacks;
		}

	}


	/**
	 * Handler for vanilla Minecraft shaped recipes
	 *
	 */
	public static class ShapedRecipeHandler extends RecipeHandler
	{
		@Override
		public ItemStack[] getCraftingGrid(IRecipe r)
		{
			// cast the IRecipe instance
			ShapedRecipes shapedRecipe = (ShapedRecipes)r;

			// get a copy of the recipe items with normalised metadata
			List<ItemStack> recipeItems = copyRecipeStacks(Arrays.asList(shapedRecipe.recipeItems));

			// get the recipe dimensions
			int recipeWidth = shapedRecipe.recipeWidth;
			int recipeHeight = shapedRecipe.recipeHeight;

			// rearrange the itemstacks according to the recipe width and height
			return reshapeRecipe(recipeItems, recipeWidth, recipeHeight);
		}
	}


	/**
	 * Handler for vanilla Minecraft shapeless recipes
	 *
	 */
	public static class ShapelessRecipeHandler extends RecipeHandler
	{
		@Override
		public ItemStack[] getCraftingGrid(IRecipe r)
		{
			// cast the IRecipe instance
			ShapelessRecipes shapelessRecipe = (ShapelessRecipes)r;

			// get a copy of the recipe items with normalised metadata
			List<ItemStack> recipeItems = copyRecipeStacks(shapelessRecipe.recipeItems);

			// convert the itemstack list to an array
			return recipeItems.toArray(new ItemStack[9]);
		}
	}


	/**
	 * Handler for shaped recipes which utilise the Forge Ore Dictionary
	 *
	 */
	public static class ShapedOreRecipeHandler extends RecipeHandler
	{
		@Override
		public ItemStack[] getCraftingGrid(IRecipe r)
		{
			// cast the IRecipe instance
			ShapedOreRecipe shapedRecipe = (ShapedOreRecipe)r;

			// get a copy of the recipe items with normalised metadata
			List<ItemStack> recipeItems = copyRecipeStacks(getOreRecipeItems(Arrays.asList(shapedRecipe.getInput())));

			if (!recipeItems.isEmpty())
			{
				// get the recipe dimensions
				int recipeWidth = ((Integer)(ObfuscationReflectionHelper.getPrivateValue(ShapedOreRecipe.class, shapedRecipe, "width"))).intValue();
				int recipeHeight = ((Integer)(ObfuscationReflectionHelper.getPrivateValue(ShapedOreRecipe.class, shapedRecipe, "height"))).intValue();

				// rearrange the itemstacks according to the recipe width and height
				return reshapeRecipe(recipeItems, recipeWidth, recipeHeight);
			}
			else return new ItemStack[0];
		}
	}


	/**
	 * Handler for shapeless recipes which utilise the Forge Ore Dictionary
	 *
	 */
	public static class ShapelessOreRecipeHandler extends RecipeHandler
	{
		@Override
		public ItemStack[] getCraftingGrid(IRecipe r)
		{
			// cast the IRecipe instance
			ShapelessOreRecipe shapelessRecipe = (ShapelessOreRecipe)r;

			// get a copy of the recipe items with normalised metadata
			List<ItemStack> recipeItems = copyRecipeStacks(getOreRecipeItems(shapelessRecipe.getInput()));

			if (!recipeItems.isEmpty())
			{
				// convert the itemstack list to an array
				return recipeItems.toArray(new ItemStack[9]);
			}
			else return new ItemStack[0];
		}
	}

}