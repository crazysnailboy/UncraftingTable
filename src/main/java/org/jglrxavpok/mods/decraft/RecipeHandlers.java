package org.jglrxavpok.mods.decraft;


import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/**
 * Recipe Handlers return the "crafting grid" depending on a crafting recipe.
 * @author jglrxavpok
 *
 */
public final class RecipeHandlers
{
	
	public static abstract class RecipeHandler
	{
		private Class<? extends IRecipe> recipeType;

		public RecipeHandler(Class<? extends IRecipe> recipe)
		{
			this.recipeType = recipe;
		}
		
		public Class<? extends IRecipe> getType()
		{
			return recipeType;
		}
		
		/**
		 * Returns the "crafting grid" depending on the given Recipe
		 */
		public abstract NonNullList<ItemStack> getCraftingGrid(IRecipe s);
	}
	
	

	public static class ShapedRecipeHandler extends RecipeHandler
	{
		public ShapedRecipeHandler(Class<? extends IRecipe> recipe)
		{
			super(recipe);
		}

		@Override
		public NonNullList<ItemStack> getCraftingGrid(IRecipe r)
		{
			NonNullList<ItemStack> stacks = NonNullList.<ItemStack>func_191197_a(9, ItemStack.field_190927_a);
			ShapedRecipes shaped = (ShapedRecipes)r;
			for (int j = 0;j<shaped.recipeItems.length;j++)
			{
				stacks.set(j, shaped.recipeItems[j]);
			}
			return stacks;
		}
	}
	
	public static class ShapelessOreRecipeHandler extends RecipeHandler
	{
		public ShapelessOreRecipeHandler(Class<? extends IRecipe> recipe)
		{
			super(recipe);
		}

		@Override
		public NonNullList<ItemStack> getCraftingGrid(IRecipe r)
		{
			NonNullList<ItemStack> stacks = NonNullList.<ItemStack>func_191197_a(9, ItemStack.field_190927_a);
			ShapelessOreRecipe shaped = (ShapelessOreRecipe)r;
			for (int j = 0;j<shaped.getInput().size();j++)
			{
				if (shaped.getInput().get(j) instanceof ItemStack)
				{
					stacks.set(j, (ItemStack)shaped.getInput().get(j));
				}
				else if (shaped.getInput().get(j) instanceof java.util.List)
				{
					Object o = ((java.util.List)shaped.getInput().get(j)).get(0);
					if (o instanceof ItemStack)
					{
						stacks.set(j, (ItemStack)o);
					}
				}
			}
			return stacks;
		}
	}
	
	public static class ShapedOreRecipeHandler extends RecipeHandler
	{
		public ShapedOreRecipeHandler(Class<? extends IRecipe> recipe)
		{
			super(recipe);
		}

		@Override
		public NonNullList<ItemStack> getCraftingGrid(IRecipe r)
		{
			NonNullList<ItemStack> stacks = NonNullList.<ItemStack>func_191197_a(9, ItemStack.field_190927_a);
			ShapedOreRecipe shaped = (ShapedOreRecipe)r;
			for (int j = 0;j<shaped.getInput().length;j++)
			{
				if (shaped.getInput()[j] instanceof ItemStack)
				{
					stacks.set(j, (ItemStack)shaped.getInput()[j]);
				}
				else if (shaped.getInput()[j] instanceof java.util.List)
				{
					Object o = ((java.util.List)shaped.getInput()[j]).get(0);
					if (o instanceof ItemStack)
					{
						stacks.set(j, (ItemStack)o);
					}
				}
			}
			return stacks;
		}
	}
	
	public static class ShapelessRecipeHandler extends RecipeHandler
	{
		public ShapelessRecipeHandler(Class<? extends IRecipe> recipe)
		{
			super(recipe);
		}

		@Override
		public NonNullList<ItemStack> getCraftingGrid(IRecipe r)
		{
			NonNullList<ItemStack> stacks = NonNullList.<ItemStack>func_191197_a(9, ItemStack.field_190927_a);
			ShapelessRecipes shaped = (ShapelessRecipes)r;
			for (int j = 0;j<shaped.recipeItems.size();j++)
			{
				stacks.set(j, (ItemStack)shaped.recipeItems.get(j));
			}
			return stacks;
		}
	}

//	/**
//	 * Default Recipe Handlers
//	 */
//	public static final RecipeHandler DEFAULT_SHAPELESS_RECIPE_HANDLER = new ShapelessRecipeHandler(ShapelessRecipes.class);
//	public static final RecipeHandler DEFAULT_SHAPED_RECIPE_HANDLER = new ShapedRecipeHandler(ShapedRecipes.class);
//	public static final RecipeHandler DEFAULT_SHAPELESS_ORE_RECIPE_HANDLER = new ShapelessOreRecipeHandler(ShapelessOreRecipe.class);
//	public static final RecipeHandler DEFAULT_SHAPED_ORE_RECIPE_HANDLER = new ShapedOreRecipeHandler(ShapedOreRecipe.class);
//	
//	/**
//	 * Set the default Recipe Handlers
//	 */
//	public static void load()
//	{
//		UncraftingManager.setRecipeHandler(ShapelessRecipes.class, DEFAULT_SHAPELESS_RECIPE_HANDLER);
//		UncraftingManager.setRecipeHandler(ShapedRecipes.class, DEFAULT_SHAPED_RECIPE_HANDLER);
//		UncraftingManager.setRecipeHandler(ShapelessOreRecipe.class, DEFAULT_SHAPELESS_ORE_RECIPE_HANDLER);
//		UncraftingManager.setRecipeHandler(ShapedOreRecipe.class, DEFAULT_SHAPED_ORE_RECIPE_HANDLER);
//	}
}
