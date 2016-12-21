package org.jglrxavpok.mods.decraft.item.uncrafting.mekanism;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.jglrxavpok.mods.decraft.RecipeHandlers.RecipeHandler;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;

public class MekanismRecipeHandlers {

	
	public static class ShapelessMekanismRecipeHandler extends RecipeHandler
	{

		public ShapelessMekanismRecipeHandler(Class<? extends IRecipe> recipe) {
			super(recipe);
		}

		@Override
		public ItemStack[] getCraftingGrid(IRecipe s) {
			
			try
			{
				ItemStack[] stacks = new ItemStack[9];
				
				Class recipeClass = Class.forName("mekanism.common.recipe.ShapelessMekanismRecipe");
				Method getInput = recipeClass.getMethod("getInput", (Class[])null);
				
				List<Object> recipeItems = (List<Object>)getInput.invoke(s);
				
				for (int j = 0 ; j < recipeItems.size() ; j++)
				{
					if (recipeItems.get(j) instanceof ItemStack)
					{
						stacks[j] = (ItemStack)recipeItems.get(j);
					}
					else if (recipeItems.get(j) instanceof java.util.List)
					{
						Object o = ((java.util.List)recipeItems.get(j)).get(0);
						if (o instanceof ItemStack)
						{
							stacks[j] = (ItemStack)o;
						}
					}
				}
				return stacks;
				
			}
			catch(Exception ex)
			{
			}
			
			
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	
	
	public static class ShapedMekanismRecipeHandler extends RecipeHandler
	{

		public ShapedMekanismRecipeHandler(Class<? extends IRecipe> recipe) {
			super(recipe);
		}


		@Override
		public ItemStack[] getCraftingGrid(IRecipe s) 
		{
			try
			{
				ItemStack[] stacks = new ItemStack[9];
				
				Class recipeClass = Class.forName("mekanism.common.recipe.ShapedMekanismRecipe");
				Method getInput = recipeClass.getMethod("getInput", (Class[])null);

				Object[] recipeItems = (Object[])getInput.invoke(s);
				
				for (int j = 0 ; j < recipeItems.length ; j++)
				{
					if (recipeItems[j] instanceof ItemStack)
					{
						stacks[j] = (ItemStack) recipeItems[j];
					}
					else if (recipeItems[j] instanceof java.util.List)
					{
						Object o = ((java.util.List)recipeItems[j]).get(0);
						if (o instanceof ItemStack)
						{
							stacks[j] = (ItemStack)o;
						}
					}
				}
				return stacks;
				
				
				
				
//				return (ItemStack[])Arrays.copyOf(input, input.length, ItemStack[].class);
				
				
//				Object[] input = (Object[])(Class.forName("mekanism.common.recipe.ShapedMekanismRecipe").getField("input").get(null));
//				List<ItemStack> recipeStacks = Lists.newArrayList(Iterables.filter(Arrays.asList(input), ItemStack.class)); 
				
//				return (ItemStack[])(recipeStacks.toArray());
				
			}
			catch(Exception ex)
			{
				System.out.println("MekanismShapedRecipeHandler.getCraftingGrid: " + ex.getMessage());
				System.out.println(ex.getStackTrace());
				return null;
			}
			
		}
	}
	

}
