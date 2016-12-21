package org.jglrxavpok.mods.decraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.jglrxavpok.mods.decraft.RecipeHandlers.RecipeHandler;
import org.jglrxavpok.mods.decraft.RecipeHandlers.ShapedOreRecipeHandler;
import org.jglrxavpok.mods.decraft.RecipeHandlers.ShapedRecipeHandler;
import org.jglrxavpok.mods.decraft.RecipeHandlers.ShapelessOreRecipeHandler;
import org.jglrxavpok.mods.decraft.RecipeHandlers.ShapelessRecipeHandler;
import org.jglrxavpok.mods.decraft.common.config.ModConfiguration;
import org.jglrxavpok.mods.decraft.item.uncrafting.mekanism.MekanismRecipeHandlers.ShapedMekanismRecipeHandler;
import org.jglrxavpok.mods.decraft.item.uncrafting.mekanism.MekanismRecipeHandlers.ShapelessMekanismRecipeHandler;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipesMapExtending;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/**
 * Main part of the Uncrafting Table. The manager is used to parse the existing recipes and find the correct one depending on the given stack.
 * @author jglrxavpok
 */
public class UncraftingManager 
{
	
//	private static List<IRecipe> vanillaRecipeList;
//	
//	private static List<IRecipe> reflectedRecipeList;
	
	
//	private static List<IRecipe> getVanillaRecipeList()
//	{
//		List<IRecipe> recipeList = CraftingManager.getInstance().getRecipeList();
//		
//		List<IRecipe> vanillaRecipeList = new ArrayList<IRecipe>();
//		vanillaRecipeList.addAll(Lists.newArrayList(Iterables.filter(recipeList, ShapedRecipes.class)));
//		vanillaRecipeList.addAll(Lists.newArrayList(Iterables.filter(recipeList, ShapedOreRecipe.class)));
//		vanillaRecipeList.addAll(Lists.newArrayList(Iterables.filter(recipeList, ShapelessRecipes.class)));
//		vanillaRecipeList.addAll(Lists.newArrayList(Iterables.filter(recipeList, ShapelessOreRecipe.class)));
//		
//		return vanillaRecipeList;
//	}
//	
//	private static List<IRecipe> getReflectedRecipeList()
//	{
//		List<IRecipe> recipeList = CraftingManager.getInstance().getRecipeList();
//		List<IRecipe> reflectedRecipeList = new ArrayList<IRecipe>();
//		
//		String[] classNames = new String[] { 
//			"mekanism.common.recipe.ShapedMekanismRecipe"
//		};
//		
//		for ( String className : classNames )
//		{
//			try
//			{
//				Class c = Class.forName(className);
//				reflectedRecipeList.addAll(Lists.newArrayList(Iterables.filter(recipeList, c)));
//			}
//			catch(Exception ex) { }
//		}
//		
//		return reflectedRecipeList;
//	}
	
	

	private static Boolean canUncraftItem(ItemStack itemStack)
	{
		String uniqueIdentifier = Item.REGISTRY.getNameForObject(itemStack.getItem()).toString();
		if (itemStack.getItemDamage() > 0) uniqueIdentifier += "," + Integer.toString(itemStack.getItemDamage()); 
		
		return ArrayUtils.indexOf(ModConfiguration.excludedItems, uniqueIdentifier) < 0;
	}
	
	
	public static List<Integer> getStackSizeNeeded(ItemStack item)
	{
//		System.out.println("\t" + "getStackSizeNeeded");
//		System.out.println("\t" + item.getItem().getUnlocalizedName());
//		System.out.println("\t" + item.getDisplayName());
		
		List<Integer> list = new ArrayList<Integer>();
		if (!canUncraftItem(item)) return list;
		
		for ( IRecipe recipe : CraftingManager.getInstance().getRecipeList())
		{
			ItemStack recipeOutput = recipe.getRecipeOutput();
			if (recipeOutput != null)
			{
				if (ItemStack.areItemsEqualIgnoreDurability(item, recipeOutput))
				{
					RecipeHandler handler = getRecipeHandler(recipe);
					if (handler != null)
					{
						list.add(recipeOutput.stackSize);
						break;
					}
					else 
					{
						ModUncrafting.instance.getLogger().error("[Uncrafting Table] Unknown recipe type: " + recipe.getClass().getCanonicalName());
					}
				}
			}
		}

		return list;
	}
	
	
	
	
	public static List<ItemStack[]> getUncraftResults(ItemStack item)
	{
//		System.out.println("getUncraftResults");
//		System.out.println(item.getItem().getUnlocalizedName());
//		System.out.println(item.getDisplayName());
//		System.out.println("isDamageable: " + item.getItem().isDamageable());

		List<ItemStack[]> list = new ArrayList<ItemStack[]>();
		if (!canUncraftItem(item)) return list;
		
		for ( IRecipe recipe : CraftingManager.getInstance().getRecipeList() )
		{
			ItemStack recipeOutput = recipe.getRecipeOutput();
			if (ItemStack.areItemsEqualIgnoreDurability(item, recipeOutput) && recipeOutput.stackSize <= item.stackSize)
			{
				RecipeHandler handler = getRecipeHandler(recipe);
				if (handler != null)
				{
					list.add(handler.getCraftingGrid(recipe));
					break;
				}
				else 
				{
					ModUncrafting.instance.getLogger().error("[Uncrafting Table] Unknown recipe type: " + recipe.getClass().getCanonicalName());
				}
			}
		}
		
		return list;
	}
	
	
	
	private static RecipeHandler getRecipeHandler(IRecipe recipe)
	{
		if (recipe instanceof RecipesMapExtending) return null;
		if (recipe instanceof ShapelessRecipes) return new ShapelessRecipeHandler(ShapelessRecipes.class);
		if (recipe instanceof ShapedRecipes) return new ShapedRecipeHandler(ShapedRecipes.class);
		if (recipe instanceof ShapelessOreRecipe) return new ShapelessOreRecipeHandler(ShapelessOreRecipe.class);
		if (recipe instanceof ShapedOreRecipe) return new ShapedOreRecipeHandler(ShapedOreRecipe.class);
		
		try
		{
//			Class c = Class.forName("mekanism.common.recipe.ShapedMekanismRecipe");
//			if (c.isInstance(recipe)) return new ShapedMekanismRecipeHandler(c);

			Class c;
			
			c = Class.forName("mekanism.common.recipe.ShapedMekanismRecipe");
			if (c.isInstance(recipe)) return new ShapedMekanismRecipeHandler(c);

			c = Class.forName("mekanism.common.recipe.ShapelessMekanismRecipe");
			if (c.isInstance(recipe)) return new ShapelessMekanismRecipeHandler(c);
			
			
			
//			HashMap<String, Class> reflectedHandlers = new HashMap<String, Class>();
//			reflectedHandlers.put("mekanism.common.recipe.ShapedMekanismRecipe", ShapedMekanismRecipeHandler.class);
//			reflectedHandlers.put("mekanism.common.recipe.ShapelessMekanismRecipe", ShapelessMekanismRecipeHandler.class);
//			
//			for ( String className : reflectedHandlers.keySet())
//			{
//				try
//				{
//					Class recipeClass = Class.forName(className);
//					Class handlerClass = reflectedHandlers.get(className);
//					
//					if (recipeClass.isInstance(recipe))
//					{
//						return (RecipeHandler)(handlerClass.getConstructor(IRecipe.class).newInstance(recipeClass));
//					}
//				}
//				catch(Exception ex) { }
//			}
			
		}
		catch(Exception ex) { 
		}
		
		return null;
	}
	
	
	public static void postInit()
	{
//		vanillaRecipeList = getVanillaRecipeList();
//		reflectedRecipeList = getReflectedRecipeList();
	}
	
}
