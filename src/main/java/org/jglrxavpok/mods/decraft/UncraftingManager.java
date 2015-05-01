package org.jglrxavpok.mods.decraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
		List<?> crafts = CraftingManager.getInstance().getRecipeList();
		List<Integer> list = new ArrayList<Integer>();
		for(int i = 0;i<crafts.size();i++)
		{
			IRecipe r = (IRecipe) crafts.get(i);
			if(r != null)
			{
				ItemStack s = r.getRecipeOutput();
				if(s!=null)
				{
					if(s.getItem() == item.getItem() && s.getItemDamage() == item.getItemDamage())
					{
						list.add(s.stackSize);
					}
				}
			}
		}
		return list;
	}
	
	public static List<ItemStack[]> getUncraftResults(ItemStack item)
	{
		List<?> crafts = CraftingManager.getInstance().getRecipeList();
		List<ItemStack[]> list = new ArrayList<ItemStack[]>();
		for(int i = 0;i<crafts.size();i++)
		{
			IRecipe r = (IRecipe) crafts.get(i);
			if(r != null)
			{
				ItemStack s = r.getRecipeOutput();
				if(s!=null)
				{
					if(s.getItem() == item.getItem() && s.stackSize <= item.stackSize && s.getItem() == item.getItem())
					{
						RecipeHandler handler = uncraftingHandlers.get(r.getClass());
						if(handler != null)
						{
							list.add(handler.getCraftingGrid(r));
						}
						else 
						{
							ModUncrafting.instance.getLogger().error("[Uncrafting Table] Unknown recipe type: "+r.getClass().getCanonicalName());
						}
					}
				}
			}
		}
		return list;
	}
	
	public static void setRecipeHandler(Class<? extends IRecipe> recipe, RecipeHandler handler)
	{
		uncraftingHandlers.put(recipe, handler);
	}
	
}
