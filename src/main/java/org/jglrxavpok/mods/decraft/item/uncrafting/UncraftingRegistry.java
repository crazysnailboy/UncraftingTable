package org.jglrxavpok.mods.decraft.item.uncrafting;

import java.util.List;
import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;

public class UncraftingRegistry
{
	private static final UncraftingRegistry INSTANCE = new UncraftingRegistry();

	public static final List<IRecipe> recipes = Lists.<IRecipe>newArrayList();
	public static final List<IRecipe> blockedRecipes = Lists.<IRecipe>newArrayList();
	public static final List<ItemStack> blockedItems = Lists.<ItemStack>newArrayList();
	public static final List<ItemStack> blockedIngredients = Lists.<ItemStack>newArrayList();
	public static final List<ItemStack> removedIngredients = Lists.<ItemStack>newArrayList();


	public static UncraftingRegistry getInstance()
	{
		return INSTANCE;
	}


	public List<IRecipe> getRecipeList()
	{
		return this.recipes;
	}

	public void addUncraftingRecipe(IRecipe recipe)
	{
		this.recipes.add(recipe);
	}


	public boolean isRecipeBlocked(NonNullList<ItemStack> craftingGrid)
	{
		InventoryCrafting craftMatrix = new InventoryCrafting(craftingGrid);
		for (IRecipe irecipe : this.blockedRecipes)
		{
			if (irecipe.matches(craftMatrix, null)) return true;
		}
		return false;
	}

	public boolean isOutputBlocked(ItemStack stack)
	{
		for ( ItemStack stackB : this.blockedItems )
		{
			if (ItemStack.areItemsEqualIgnoreDurability(stack, stackB) && ItemStack.areItemStackTagsEqual(stack, stackB)) return true;
		}
		return false;
	}

	public boolean isIngredientBlocked(ItemStack stack)
	{
		for ( ItemStack stackB : this.blockedIngredients )
		{
			if (ItemStack.areItemsEqualIgnoreDurability(stack, stackB) && ItemStack.areItemStackTagsEqual(stack, stackB)) return true;
		}
		return false;
	}

	public boolean recipeContainsBlockedItems(NonNullList<ItemStack> craftingGrid)
	{
		for ( ItemStack stack : craftingGrid )
		{
			if (isIngredientBlocked(stack)) return true;
		}
		return false;
	}

	public boolean shouldIngredientBeRemoved(ItemStack stack)
	{
		for ( ItemStack stackB : this.removedIngredients )
		{
			if (ItemStack.areItemsEqualIgnoreDurability(stack, stackB) && ItemStack.areItemStackTagsEqual(stack, stackB)) return true;
		}
		return false;
	}



	private static class InventoryCrafting extends net.minecraft.inventory.InventoryCrafting
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

}
