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
 */
public final class RecipeHandlers
{
	
	/**
	 * Abstract base class extended by the different types of recipe handler
	 *
	 */
	public static abstract class RecipeHandler
	{
//		private Class<? extends IRecipe> recipeType;

		public RecipeHandler(Class<? extends IRecipe> recipe)
		{
//			this.recipeType = recipe;
		}
		
//		public Class<? extends IRecipe> getType()
//		{
//			return recipeType;
//		}
		
		public abstract NonNullList<ItemStack> getCraftingGrid(IRecipe s);
	}
	
	

	/**
	 * Handler for vanilla Minecraft shaped recipes
	 *
	 */
	public static class ShapedRecipeHandler extends RecipeHandler
	{
		public ShapedRecipeHandler(Class<? extends IRecipe> recipe)
		{
			super(recipe);
		}

		@Override
		public NonNullList<ItemStack> getCraftingGrid(IRecipe r)
		{
			NonNullList<ItemStack> stacks = NonNullList.<ItemStack>withSize(9, ItemStack.EMPTY);
			ShapedRecipes shaped = (ShapedRecipes)r;
			for (int j = 0;j<shaped.recipeItems.length;j++)
			{
				stacks.set(j, shaped.recipeItems[j]);
			}
			return stacks;
		}
	}
	
	
	/**
	 * Handler for vanilla Minecraft shapeless recipes
	 *
	 */
	public static class ShapelessRecipeHandler extends RecipeHandler
	{
		public ShapelessRecipeHandler(Class<? extends IRecipe> recipe)
		{
			super(recipe);
		}

		@Override
		public NonNullList<ItemStack> getCraftingGrid(IRecipe r)
		{
			NonNullList<ItemStack> stacks = NonNullList.<ItemStack>withSize(9, ItemStack.EMPTY);
			ShapelessRecipes shaped = (ShapelessRecipes)r;
			for (int j = 0;j<shaped.recipeItems.size();j++)
			{
				stacks.set(j, (ItemStack)shaped.recipeItems.get(j));
			}
			return stacks;
		}
	}
	
	
	/**
	 * Handler for shaped recipes which utilise the Forge Ore Dictionary
	 *
	 */
	public static class ShapedOreRecipeHandler extends RecipeHandler
	{
		public ShapedOreRecipeHandler(Class<? extends IRecipe> recipe)
		{
			super(recipe);
		}

		@Override
		public NonNullList<ItemStack> getCraftingGrid(IRecipe r)
		{
			NonNullList<ItemStack> stacks = NonNullList.<ItemStack>withSize(9, ItemStack.EMPTY);
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
	
	
	/**
	 * Handler for shapeless recipes which utilise the Forge Ore Dictionary
	 *
	 */
	public static class ShapelessOreRecipeHandler extends RecipeHandler
	{
		public ShapelessOreRecipeHandler(Class<? extends IRecipe> recipe)
		{
			super(recipe);
		}

		@Override
		public NonNullList<ItemStack> getCraftingGrid(IRecipe r)
		{
			NonNullList<ItemStack> stacks = NonNullList.<ItemStack>withSize(9, ItemStack.EMPTY);
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
	
}
