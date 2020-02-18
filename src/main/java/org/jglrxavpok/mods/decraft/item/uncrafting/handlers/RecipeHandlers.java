package org.jglrxavpok.mods.decraft.item.uncrafting.handlers;


import java.util.HashMap;
import java.util.List;

import net.minecraft.item.Items;
import net.minecraft.item.crafting.*;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.NBTSensitiveRecipeHandlers.FireworksRecipeHandler;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.NBTSensitiveRecipeHandlers.TippedArrowRecipeHandler;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.external.CoFHRecipeHandlers;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.external.IC2RecipeHandlers.ShapedIC2RecipeHandler;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.external.IC2RecipeHandlers.ShapelessIC2RecipeHandler;
import org.jglrxavpok.mods.decraft.item.uncrafting.handlers.external.TinkersRecipeHandlers;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

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
		// RecipesMapExtending extends ShapedRecipe, and causes a crash when attempting to uncraft a map
		HANDLERS.put(MapExtendingRecipe.class, null);

		// vanilla Minecraft recipe handlers
		HANDLERS.put(ShapedRecipe.class, new ShapedRecipeHandler());
		HANDLERS.put(ShapelessRecipe.class, new ShapelessRecipeHandler());
		HANDLERS.put(FireworkStarRecipe.class, new FireworksRecipeHandler());
		HANDLERS.put(TippedArrowRecipe.class, new TippedArrowRecipeHandler());

		// cofh recipe handlers
		if (CoFHRecipeHandlers.CoverRecipeHandler.recipeClass != null) HANDLERS.put(CoFHRecipeHandlers.CoverRecipeHandler.recipeClass, new CoFHRecipeHandlers.CoverRecipeHandler());

		// industrialcraft 2 recipe handlers
		if (ShapedIC2RecipeHandler.recipeClass != null) HANDLERS.put(ShapedIC2RecipeHandler.recipeClass, new ShapedIC2RecipeHandler());
		if (ShapelessIC2RecipeHandler.recipeClass != null) HANDLERS.put(ShapelessIC2RecipeHandler.recipeClass, new ShapelessIC2RecipeHandler());

		// tinker's construct recipe handlers
		if (TinkersRecipeHandlers.TableRecipeHandler.recipeClass != null) HANDLERS.put(TinkersRecipeHandlers.TableRecipeHandler.recipeClass, new TinkersRecipeHandlers.TableRecipeHandler());
	}

	private static void buildRecipeOutputMap()
	{
		RECIPE_OUTPUTS.put(FireworkStarRecipe.class, new ItemStack[] { new ItemStack(Items.FIRE_CHARGE), new ItemStack(Items.FIREWORK_ROCKET, 3) });
		RECIPE_OUTPUTS.put(TippedArrowRecipe.class, new ItemStack[] { new ItemStack(Items.TIPPED_ARROW, 8) });
	}


	/**
	 * Abstract base class extended by the different types of recipe handler
	 *
	 */
	public static abstract class RecipeHandler
	{

		public abstract NonNullList<NonNullList<ItemStack>> getCraftingGrids(IRecipe r);


		/**
		 * Used by subclasses referencing external IRecipe implementations
		 */
		protected static Class<? extends IRecipe> getRecipeClass(String className)
		{
			try
			{
				return Class.forName(className).asSubclass(IRecipe.class);
			}
			catch(ClassNotFoundException ex)
			{
				return null;
			}
		}


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
			return ItemStack.EMPTY;
		}


		/**
		 * Takes a list of ItemStacks from a shaped recipe and correctly positions them according to the recipe width and height
		 */
		protected static NonNullList<ItemStack> reshapeRecipe(List<ItemStack> recipeItems, int recipeWidth, int recipeHeight)
		{
			NonNullList<ItemStack> stacks = NonNullList.<ItemStack>withSize(9, ItemStack.EMPTY);
			for ( int row = 0 ; row < recipeHeight ; row++ )
			{
				for (int col = 0 ; col < recipeWidth ; col++ )
				{
					stacks.set((row * 3) + col, recipeItems.get(col + row * recipeWidth));
				}
			}
			return stacks;
		}


		/**
		 * Converts a collection of OreDictionary recipe items into a list of ItemStacks
		 */
//		protected static NonNullList<ItemStack> getOreRecipeItems(List<Object> itemObjects)
//		{
//			NonNullList<ItemStack> itemStacks = NonNullList.<ItemStack>withSize(9, ItemStack.EMPTY);
//			for ( int i = 0 ; i < itemObjects.size() ; i++ )
//			{
//				Object itemObject = itemObjects.get(i);
//				ItemStack itemStack;
//
//				if (itemObject instanceof ItemStack)
//				{
//					itemStack = (ItemStack)itemObject;
//				}
//				else if (itemObject instanceof List)
//				{
//					List list = (List)itemObject;
//
//					if (list.isEmpty()) // this happens if there's an ore dictionary recipe registered, but no items registered for that dictionary entry
//					{
//						// abort parsing this recipe and return an empty list
//						return NonNullList.<ItemStack>create();
//					}
//
//					itemStack = ((List<ItemStack>)itemObject).get(0);
//				}
//				else itemStack = ItemStack.EMPTY;
//
//				itemStacks.set(i, itemStack);
//			}
//			return itemStacks;
//		}


		/**
		 * Copies the ItemStacks from a list of Ingredients to a new list
		 */
		protected static NonNullList<NonNullList<ItemStack>> copyRecipeStacks(NonNullList<Ingredient> inputStacks)
		{
			int resultCount = inputStacks.stream().mapToInt(i -> i.getMatchingStacks().length).max().getAsInt();
			NonNullList<NonNullList<ItemStack>> grids = NonNullList.create();

			// we use the maximum length as the limit so we don't have every single combination possible.
			// This choice also allows to keep the same item for a given #tag (always suggest the same wood plank type for a crafting table for instance)
			for (int tagIndex = 0; tagIndex < resultCount; tagIndex++)
			{
				NonNullList<ItemStack> outputStacks = NonNullList.<ItemStack>withSize(9, ItemStack.EMPTY);
				for ( int i = 0 ; i < inputStacks.size() ; i++ )
				{
					ItemStack[] matchingStacks = inputStacks.get(i).getMatchingStacks();
					ItemStack outputStack = (matchingStacks.length > 0 ? matchingStacks[tagIndex % matchingStacks.length].copy() : ItemStack.EMPTY);
					outputStacks.set(i, outputStack);
				}
				grids.add(outputStacks);
			}

			return grids;
		}

		/**
		 * Copies the ItemStacks in a list to a new list
		 */
		protected static NonNullList<ItemStack> copyRecipeStacks(List<ItemStack> inputStacks)
		{
			NonNullList<ItemStack> outputStacks = NonNullList.<ItemStack>withSize(9, ItemStack.EMPTY);

			for ( int i = 0 ; i < inputStacks.size() ; i++ )
			{
				ItemStack outputStack = (inputStacks.get(i) == null ? ItemStack.EMPTY : inputStacks.get(i)).copy();
				outputStacks.set(i, outputStack);
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
		public NonNullList<NonNullList<ItemStack>> getCraftingGrids(IRecipe r)
		{
			// cast the IRecipe instance
			ShapedRecipe shapedRecipe = (ShapedRecipe)r;

			// get a copy of the recipe items with normalised metadata
			NonNullList<NonNullList<ItemStack>> grids = copyRecipeStacks(shapedRecipe.getIngredients());

			// get the recipe dimensions
			int recipeWidth = shapedRecipe.getRecipeWidth();
			int recipeHeight = shapedRecipe.getRecipeHeight();

			// rearrange the itemstacks according to the recipe width and height
			NonNullList<NonNullList<ItemStack>> result = NonNullList.create();
			for(NonNullList<ItemStack> ingredients : grids) {
				result.add(reshapeRecipe(ingredients, recipeWidth, recipeHeight));
			}
			return result;
		}
	}


	/**
	 * Handler for vanilla Minecraft shapeless recipes
	 *
	 */
	public static class ShapelessRecipeHandler extends RecipeHandler
	{
		@Override
		public NonNullList<NonNullList<ItemStack>> getCraftingGrids(IRecipe r)
		{
			// cast the IRecipe instance
			ShapelessRecipe shapelessRecipe = (ShapelessRecipe)r;

			// get a copy of the recipe items with normalised metadata
			NonNullList<NonNullList<ItemStack>> recipeStacks = copyRecipeStacks(shapelessRecipe.getIngredients());

			// return the itemstacks
			return recipeStacks;
		}
	}
}
