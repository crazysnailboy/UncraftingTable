package org.jglrxavpok.mods.decraft.integration.crafttweaker;

import java.util.List;
import java.util.UUID;
import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.integration.crafttweaker.mtlib.BaseListAddition;
import org.jglrxavpok.mods.decraft.item.uncrafting.UncraftingManager;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.recipes.IRecipeAction;
import crafttweaker.api.recipes.IRecipeFunction;
import crafttweaker.api.recipes.ShapedRecipe;
import crafttweaker.api.recipes.ShapelessRecipe;
import crafttweaker.mc1120.item.MCItemStack;
import crafttweaker.mc1120.recipes.RecipeConverter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;


public class CraftTweakerIntegration
{

	public static void register()
	{
		CraftTweakerAPI.registerClass(RecipeIntegration.class);
		CraftTweakerAPI.registerClass(ItemIntegration.class);
	}

	@ZenClass("mods.UncraftingTable.recipes")
	public static class RecipeIntegration
	{

		@ZenMethod
		public static void addShaped(IItemStack output, IIngredient[][] ingredients, @Optional IRecipeFunction function, @Optional IRecipeAction action)
		{
			ShapedRecipe recipe = new ShapedRecipe(output, ingredients, function, action, false);
			IRecipe irecipe = RecipeConverter.convert(recipe, randomResourceLocation());
			CraftTweakerAPI.apply(new ActionAddRecipe(irecipe, UncraftingManager.hardCodedRecipes, "Added Shaped Recipe"));
		}

		@ZenMethod
		public static void addShapeless(IItemStack output, IIngredient[] ingredients, @Optional IRecipeFunction function, @Optional IRecipeAction action)
		{
			ShapelessRecipe recipe = new ShapelessRecipe(output, ingredients, function, action);
			IRecipe irecipe = RecipeConverter.convert(recipe, randomResourceLocation());

			CraftTweakerAPI.apply(new ActionAddRecipe(irecipe, UncraftingManager.hardCodedRecipes, "Added Shapeless Recipe"));
		}

		@ZenMethod
		public static void blockShaped(IItemStack output, IIngredient[][] ingredients, @Optional IRecipeFunction function, @Optional IRecipeAction action)
		{
			ShapedRecipe recipe = new ShapedRecipe(output, ingredients, function, action, false);
			IRecipe irecipe = RecipeConverter.convert(recipe, randomResourceLocation());

			CraftTweakerAPI.apply(new ActionAddRecipe(irecipe, UncraftingManager.blockedRecipes, "Blocked Shaped Recipe"));
		}

		@ZenMethod
		public static void blockShapeless(IItemStack output, IIngredient[] ingredients, @Optional IRecipeFunction function, @Optional IRecipeAction action)
		{
			ShapelessRecipe recipe = new ShapelessRecipe(output, ingredients, function, action);
			IRecipe irecipe = RecipeConverter.convert(recipe, randomResourceLocation());

			CraftTweakerAPI.apply(new ActionAddRecipe(irecipe, UncraftingManager.blockedRecipes, "Blocked Shapeless Recipe"));
		}
	}

	@ZenClass("mods.UncraftingTable.items")
	public static class ItemIntegration
	{

		private static ItemStack getItemStack(IIngredient ingredient)
		{
			ItemStack stack = CraftTweakerMC.getItemStack(ingredient).copy();
			stack.setCount(1);
			return stack;
		}


		@ZenMethod
		public static void blockUncrafting(IIngredient ingredient)
		{
			CraftTweakerAPI.apply(new ActionAddItemStack(getItemStack(ingredient), UncraftingManager.blockedItems, "Blocked Item"));
		}

		@ZenMethod
		public static void blockIngredient(IIngredient ingredient)
		{
			CraftTweakerAPI.apply(new ActionAddItemStack(getItemStack(ingredient), UncraftingManager.blockedIngredients, "Blocked Ingredient"));
		}

		@ZenMethod
		public static void removeIngredient(IIngredient ingredient)
		{
			CraftTweakerAPI.apply(new ActionAddItemStack(getItemStack(ingredient), UncraftingManager.removedIngredients, "Removed Ingredient"));
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

	private static ResourceLocation randomResourceLocation()
	{
		return new ResourceLocation(ModUncrafting.MODID, UUID.randomUUID().toString());
	}

}
