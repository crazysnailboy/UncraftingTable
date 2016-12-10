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

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
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

	private static HashMap<Class<? extends IRecipe>, RecipeHandler>	uncraftingHandlers = new HashMap<Class<? extends IRecipe>, RecipeHandler>();

	
//	private List<IRecipe> recipeList = Lists.newArrayList(Iterables.filter(CraftingManager.getInstance().getRecipeList(), IRecipe.class));
	
	
	public static List<Integer> getStackSizeNeeded(ItemStack item)
	{
//		System.out.println("\t" + "getStackSizeNeeded");
//		System.out.println("\t" + item.getItem().getUnlocalizedName());
//		System.out.println("\t" + item.getDisplayName());
		
		List<Integer> list = new ArrayList<Integer>();
		
		Boolean canUncraftItem = ArrayUtils.indexOf(
			ModConfiguration.uncraftableItems, 
			Item.REGISTRY.getNameForObject(item.getItem()).toString()
		) < 0;
			
		if (!canUncraftItem) return list;
		
		List<?> recipeList = CraftingManager.getInstance().getRecipeList();

		
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
						else 
						{
							ModUncrafting.instance.getLogger().error("[Uncrafting Table] Unknown recipe type: "+r.getClass().getCanonicalName());
						}
					}
				}
			}
		}
//		System.out.println("\t" + "-----");

		return list;
	}
	
	public static List<ItemStack[]> getUncraftResults(ItemStack item)
	{
		System.out.println("getUncraftResults");
		System.out.println(item.getItem().getUnlocalizedName());
//		System.out.println(item.getDisplayName());
//		System.out.println("isDamageable: " + item.getItem().isDamageable());

		List<ItemStack[]> list = new ArrayList<ItemStack[]>();
		
		Boolean canUncraftItem = ArrayUtils.indexOf(
			ModConfiguration.uncraftableItems, 
			Item.REGISTRY.getNameForObject(item.getItem()).toString()
		) < 0;
		
		if (!canUncraftItem) return list;
		
		
		List<?> recipeList = CraftingManager.getInstance().getRecipeList();
		
		for (int i = 0 ; i < recipeList.size() ; i++)
		{
			IRecipe recipe = (IRecipe)recipeList.get(i);
			if (recipe != null)
			{
				ItemStack recipeOutput = recipe.getRecipeOutput();
				if (recipeOutput != null)
				{
					if (recipeOutput.isItemEqual(item))
					{
						System.out.println("Found item equal to " + recipeOutput.getItem().getUnlocalizedName()  + ": " + item.getItem().getUnlocalizedName());
						
						System.out.println("Recipe class: " + recipe.getClass().getName());
						
						
						
						RecipeHandler handler = uncraftingHandlers.get(recipe.getClass());
						if (handler != null)
						{
							System.out.println("Have recipe handler");
							
							
							ItemStack[] itemStacks = handler.getCraftingGrid(recipe);
							for (ItemStack itemStack : itemStacks )
							{
								if (itemStack != null)
								{
									Item item2 = itemStack.getItem();
									if (item2 != null)
									{
										System.out.println(item.getUnlocalizedName());
									}
								}
							}
						}
					}
					
					
					if (
						(recipeOutput.getItem() == item.getItem() && recipeOutput.stackSize <= item.stackSize && item.getItem().isDamageable() == false && recipeOutput.getItemDamage() == item.getItemDamage())
						||
						(recipeOutput.getItem() == item.getItem() && recipeOutput.stackSize <= item.stackSize && item.getItem().isDamageable() == true)
					)
					{
						RecipeHandler handler = uncraftingHandlers.get(recipe.getClass());
						if (handler != null)
						{
							ItemStack[] itemStacks = handler.getCraftingGrid(recipe);
							
							for (ItemStack itemStack : itemStacks )
							{
								if (itemStack != null)
								{
									Item item2 = itemStack.getItem();
									if (item2 != null)
									{
										System.out.println(item.getUnlocalizedName());
									}
								}
							}
							
							
							list.add(handler.getCraftingGrid(recipe));
						}
						else
						{
							ModUncrafting.instance.getLogger().error("[Uncrafting Table] Unknown recipe type: "+recipe.getClass().getCanonicalName());
						}
					}
				}
			}
		}
		
//		System.out.println("-----");
		
		return list;
	}
	
//	public static void setRecipeHandler(Class<? extends IRecipe> recipe, RecipeHandler handler)
//	{
//		uncraftingHandlers.put(recipe, handler);
//	}
	
	
	public static void postInit(){
		
		// build the hash map of uncrafting handlers for the different recipe types
		uncraftingHandlers.put(ShapelessRecipes.class, new ShapelessRecipeHandler(ShapelessRecipes.class));
		uncraftingHandlers.put(ShapedRecipes.class, new ShapedRecipeHandler(ShapedRecipes.class));
		uncraftingHandlers.put(ShapelessOreRecipe.class, new ShapelessOreRecipeHandler(ShapelessOreRecipe.class));
		uncraftingHandlers.put(ShapedOreRecipe.class, new ShapedOreRecipeHandler(ShapedOreRecipe.class));
		
	}
	
}
