package org.jglrxavpok.mods.decraft.integration.crafttweaker;

import java.util.List;
import org.jglrxavpok.mods.decraft.integration.crafttweaker.mtlib.BaseListAddition;
import org.jglrxavpok.mods.decraft.item.uncrafting.UncraftingManager;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import minetweaker.api.recipes.IRecipeAction;
import minetweaker.api.recipes.IRecipeFunction;
import minetweaker.api.recipes.ShapedRecipe;
import minetweaker.api.recipes.ShapelessRecipe;
import minetweaker.mc1112.item.MCItemStack;
import minetweaker.mc1112.recipes.RecipeConverter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

public class CraftTweakerIntegration
{

	public static void register()
	{
		MineTweakerAPI.registerClass(RecipeIntegration.class);
		MineTweakerAPI.registerClass(ItemIntegration.class);
	}


	@ZenClass("mods.UncraftingTable.recipes")
	public static class RecipeIntegration
	{

		@ZenMethod
		public static void addShaped(IItemStack output, IIngredient[][] ingredients, @Optional IRecipeFunction function, @Optional IRecipeAction action)
		{
			ShapedRecipe recipe = new ShapedRecipe(output, ingredients, function, action, false);
			IRecipe irecipe = RecipeConverter.convert(recipe);

			MineTweakerAPI.apply(new ActionAddRecipe(irecipe, UncraftingManager.recipes, "Added Shaped Recipe"));
		}

		@ZenMethod
		public static void addShapeless(IItemStack output, IIngredient[] ingredients, @Optional IRecipeFunction function, @Optional IRecipeAction action)
		{
			ShapelessRecipe recipe = new ShapelessRecipe(output, ingredients, function, action);
			IRecipe irecipe = RecipeConverter.convert(recipe);

			MineTweakerAPI.apply(new ActionAddRecipe(irecipe, UncraftingManager.recipes, "Added Shapeless Recipe"));
		}

		@ZenMethod
		public static void blockShaped(IItemStack output, IIngredient[][] ingredients, @Optional IRecipeFunction function, @Optional IRecipeAction action)
		{
			ShapedRecipe recipe = new ShapedRecipe(output, ingredients, function, action, false);
			IRecipe irecipe = RecipeConverter.convert(recipe);

			MineTweakerAPI.apply(new ActionAddRecipe(irecipe, UncraftingManager.blockedRecipes, "Blocked Shaped Recipe"));
		}

		@ZenMethod
		public static void blockShapeless(IItemStack output, IIngredient[] ingredients, @Optional IRecipeFunction function, @Optional IRecipeAction action)
		{
			ShapelessRecipe recipe = new ShapelessRecipe(output, ingredients, function, action);
			IRecipe irecipe = RecipeConverter.convert(recipe);

			MineTweakerAPI.apply(new ActionAddRecipe(irecipe, UncraftingManager.blockedRecipes, "Blocked Shapeless Recipe"));
		}

	}

	@ZenClass("mods.UncraftingTable.items")
	public static class ItemIntegration
	{

		private static ItemStack getItemStack(IIngredient ingredient)
		{
			ItemStack stack = MineTweakerMC.getItemStack(ingredient).copy();
			stack.setCount(1);
			return stack;
		}


		@ZenMethod
		public static void blockUncrafting(IIngredient ingredient)
		{
			MineTweakerAPI.apply(new ActionAddItemStack(getItemStack(ingredient), UncraftingManager.blockedItems, "Blocked Item"));
		}

		@ZenMethod
		public static void blockIngredient(IIngredient ingredient)
		{
			MineTweakerAPI.apply(new ActionAddItemStack(getItemStack(ingredient), UncraftingManager.blockedIngredients, "Blocked Ingredient"));
		}

		@ZenMethod
		public static void removeIngredient(IIngredient ingredient)
		{
			MineTweakerAPI.apply(new ActionAddItemStack(getItemStack(ingredient), UncraftingManager.removedIngredients, "Removed Ingredient"));
		}

	}



	private static class ActionAddRecipe extends BaseListAddition<IRecipe>
	{
		public ActionAddRecipe(IRecipe recipe, List<IRecipe> recipeList, String listName)
		{
			super(listName, recipeList);
			recipes.add(recipe);
		}

		@Override
		public String getRecipeInfo(IRecipe recipe)
		{
			return new MCItemStack(recipe.getRecipeOutput()).toString();
		}
	}

	private static class ActionAddItemStack extends BaseListAddition<ItemStack>
	{
		public ActionAddItemStack(ItemStack stack, List<ItemStack> stackList, String listName)
		{
			super(listName, stackList);
			recipes.add(stack);
		}
		@Override
		public String getRecipeInfo(ItemStack stack)
		{
			return new MCItemStack(stack).toString();
		}
	}


}
