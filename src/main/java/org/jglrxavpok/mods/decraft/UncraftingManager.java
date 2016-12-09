package org.jglrxavpok.mods.decraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jglrxavpok.mods.decraft.RecipeHandlers.RecipeHandler;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;

/**
 * Main part of the Uncrafting Table. The manager is used to parse the existing recipes and find the correct one depending on the given stack.
 * @author jglrxavpok
 */
public class UncraftingManager 
{

	private static HashMap<Class<? extends IRecipe>, RecipeHandler>	uncraftingHandlers = new HashMap<Class<? extends IRecipe>, RecipeHandler>();

	
	public static List<Integer> getStackSizeNeeded(ItemStack item)
	{
//		System.out.println("\t" + "getStackSizeNeeded");
//		System.out.println("\t" + item.getItem().getUnlocalizedName());
//		System.out.println("\t" + item.getDisplayName());
		
		List<?> recipeList = CraftingManager.getInstance().getRecipeList();
		List<Integer> list = new ArrayList<Integer>();
		
		for (int i = 0 ; i < recipeList.size() ; i++)
		{
			IRecipe r = (IRecipe)recipeList.get(i);
			if (r != null)
			{
				ItemStack s = r.getRecipeOutput();
				if (s != null)
				{
					if (s.getItem() == item.getItem() && s.getItemDamage() == item.getItemDamage())
					{
						RecipeHandler handler = uncraftingHandlers.get(r.getClass());
						if (handler != null)
						{
							list.add(s.stackSize);
						}
//						else 
//						{
//							ModUncrafting.instance.getLogger().error("[Uncrafting Table] Unknown recipe type: "+r.getClass().getCanonicalName());
//						}
					}
				}
			}
		}
//		System.out.println("\t" + "-----");

		return list;
	}
	
	public static List<ItemStack[]> getUncraftResults(ItemStack item)
	{
//		System.out.println("getUncraftResults");
//		System.out.println(item.getItem().getUnlocalizedName());
//		System.out.println(item.getDisplayName());
//		System.out.println("isDamageable: " + item.getItem().isDamageable());
		
		List<?> recipeList = CraftingManager.getInstance().getRecipeList();
		List<ItemStack[]> list = new ArrayList<ItemStack[]>();
		
		for (int i = 0 ; i < recipeList.size() ; i++)
		{
			IRecipe r = (IRecipe)recipeList.get(i);
			if (r != null)
			{
				ItemStack s = r.getRecipeOutput();
				if (s != null)
				{
					if (
						(s.getItem() == item.getItem() && s.stackSize <= item.stackSize && item.getItem().isDamageable() == false && s.getItemDamage() == item.getItemDamage())
						||
						(s.getItem() == item.getItem() && s.stackSize <= item.stackSize && item.getItem().isDamageable() == true)
					)
					{
						RecipeHandler handler = uncraftingHandlers.get(r.getClass());
						if (handler != null)
						{
							list.add(handler.getCraftingGrid(r));
						}
//						else 
//						{
//							ModUncrafting.instance.getLogger().error("[Uncrafting Table] Unknown recipe type: "+r.getClass().getCanonicalName());
//						}
					}
				}
			}
		}
		
//		System.out.println("-----");
		
		return list;
	}
	
	public static void setRecipeHandler(Class<? extends IRecipe> recipe, RecipeHandler handler)
	{
		uncraftingHandlers.put(recipe, handler);
	}
	
}
