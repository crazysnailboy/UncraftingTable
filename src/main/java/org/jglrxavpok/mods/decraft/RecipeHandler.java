package org.jglrxavpok.mods.decraft;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

/**
 * Recipe Handlers return the "crafting grid" depending on a crafting recipe.
 * @author jglrxavpok
 *
 */
public abstract class RecipeHandler
{
	private Class<? extends IRecipe>	recipeType;

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
	public abstract ItemStack[] getCraftingGrid(IRecipe s);
}
