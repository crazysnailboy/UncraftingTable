package org.jglrxavpok.mods.decraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.common.MinecraftForge;
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
import net.minecraft.item.crafting.RecipesMapExtending;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import org.jglrxavpok.mods.decraft.event.ItemUncraftedEvent;
import org.jglrxavpok.mods.decraft.event.UncraftingEvent;
import org.jglrxavpok.mods.decraft.network.UncraftingResult;

/**
 * Main part of the Uncrafting Table. The manager is used to parse the existing recipes and find the correct one depending on the given stack.
 * @author jglrxavpok
 */
public class UncraftingManager 
{

    public static final UncraftingResult INVALID_ITEM = new UncraftingResult(UncraftingResult.ResultType.INVALID);
    public static final UncraftingResult INVALID_COUNT = new UncraftingResult(UncraftingResult.ResultType.NOT_ENOUGH_ITEMS);
    public static final UncraftingResult INVALID_LEVEL = new UncraftingResult(UncraftingResult.ResultType.NOT_ENOUGH_EXPERIENCE);

    private static Boolean canUncraftItem(ItemStack itemStack)
	{
		String uniqueIdentifier = Item.REGISTRY.getNameForObject(itemStack.getItem()).toString();
		if (itemStack.getItemDamage() > 0) uniqueIdentifier += "," + Integer.toString(itemStack.getItemDamage()); 
		
		return ArrayUtils.indexOf(ModConfiguration.excludedItems, uniqueIdentifier) < 0;
	}
	
	
	public static List<Integer> getStackSizeNeeded(ItemStack item)
	{
        if(item.getItem().isDamageable())
        {
            item = item.copy();
            item.setItemDamage(0);
        }
//		System.out.println("\t" + "getStackSizeNeeded");
//		System.out.println("\t" + item.getItem().getUnlocalizedName());
//		System.out.println("\t" + item.getDisplayName());
		
		List<Integer> list = new ArrayList<Integer>();
		
		if (!canUncraftItem(item)) return list;
		
		List<?> recipeList = CraftingManager.getInstance().getRecipeList();

		
		for (int i = 0 ; i < recipeList.size() ; i++)
		{
			IRecipe recipe = (IRecipe)recipeList.get(i);
			if (recipe != null)
			{
				ItemStack recipeOutput = recipe.getRecipeOutput();
				if (recipeOutput != null)
				{
					if (recipeOutput.getItem() == item.getItem() && recipeOutput.getItemDamage() == item.getItemDamage())
					{
						RecipeHandler handler = getRecipeHandler(recipe);
						if (handler != null)
						{
							list.add(recipeOutput.func_190916_E());
						}
						else 
						{
							ModUncrafting.instance.getLogger().error("[Uncrafting Table] Unknown recipe type: "+recipe.getClass().getCanonicalName());
						}
					}
				}
			}
		}
//		System.out.println("\t" + "-----");

		return list;
	}
	
	public static List<NonNullList<ItemStack>> getUncraftResults(ItemStack item)
	{
	    // allow uncrafting of damaged items
		if(item.getItem().isDamageable())
		{
		    item = item.copy();
		    item.setItemDamage(0);
        }
//		System.out.println("getUncraftResults");
//		System.out.println(item.getItem().getUnlocalizedName());
//		System.out.println(item.getDisplayName());
//		System.out.println("isDamageable: " + item.getItem().isDamageable());

		
		List<NonNullList<ItemStack>> list = new ArrayList<NonNullList<ItemStack>>();
		
		if (!canUncraftItem(item)) return list;
		
		List<?> recipeList = CraftingManager.getInstance().getRecipeList();
		
		for (int i = 0 ; i < recipeList.size() ; i++)
		{
			IRecipe recipe = (IRecipe)recipeList.get(i);
			if (recipe != null)
			{
				ItemStack recipeOutput = recipe.getRecipeOutput();
				if (recipeOutput != null)
				{
					if (
						(recipeOutput.getItem() == item.getItem() && recipeOutput.func_190916_E() <= item.func_190916_E() && item.getItem().isDamageable() == false && recipeOutput.getItemDamage() == item.getItemDamage())
						||
						(recipeOutput.getItem() == item.getItem() && recipeOutput.func_190916_E() <= item.func_190916_E() && item.getItem().isDamageable() == true)
					)
					{
						RecipeHandler handler = getRecipeHandler(recipe);
//						RecipeHandler handler = uncraftingHandlers.get(recipe.getClass());
						if (handler != null)
						{
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
	
	private static RecipeHandler getRecipeHandler(IRecipe recipe)
	{
		if (recipe instanceof RecipesMapExtending) return null;
		if (recipe instanceof ShapelessRecipes) return new ShapelessRecipeHandler(ShapelessRecipes.class);
		if (recipe instanceof ShapedRecipes) return new ShapedRecipeHandler(ShapedRecipes.class);
		if (recipe instanceof ShapelessOreRecipe) return new ShapelessOreRecipeHandler(ShapelessOreRecipe.class);
		if (recipe instanceof ShapedOreRecipe) return new ShapedOreRecipeHandler(ShapedOreRecipe.class);
		
		return null;
	}
	
	
	public static void postInit()
	{
	}

	public static UncraftingResult uncraft(ItemStack toUncraft, ItemStack book, EntityPlayer player)
    {
        toUncraft = toUncraft.copy();
        book = book.copy();
		List<Integer> needs = UncraftingManager.getStackSizeNeeded(toUncraft);
        List<NonNullList<ItemStack>> recipe = UncraftingManager.getUncraftResults(toUncraft);
		if (needs.isEmpty() || recipe.isEmpty()) { // no recipe
            return INVALID_ITEM;
        }
        final int selectedRecipe = 0; // TODO: Make it possible to choose the items to uncraft into ?
        int required = needs.get(selectedRecipe);
        NonNullList<ItemStack> output = NonNullList.func_191196_a();
        for (ItemStack stack : recipe.get(selectedRecipe)) {
            ItemStack s = stack.copy();
            int metadata = s.getItemDamage();
            if (metadata == 32767)
            {
                metadata = 0;
            }
            s.setItemDamage(metadata);
            output.add(s);
        }
        UncraftingEvent event = new UncraftingEvent(toUncraft, output, required, player);
        if (MinecraftForge.EVENT_BUS.post(event)) {
            return INVALID_ITEM; // canceled
        }

        // check if there are enough items (checking AFTER posting the event as event handlers can choose this count
        if(event.getRequiredNumber() > toUncraft.func_190916_E()) {
            return INVALID_COUNT;
        }

        int requiredExp = computeXP(toUncraft);
        if(!player.capabilities.isCreativeMode && requiredExp > player.experienceLevel) {
            return INVALID_LEVEL;
        }

        int consumedBooks = 0;
        if(book.getItem() == Items.BOOK) {
            consumedBooks = Math.min(book.func_190916_E(), required);
        }
        return new UncraftingResult(UncraftingResult.ResultType.VALID, event.getRequiredNumber(), requiredExp, consumedBooks, output);
	}

    private static int computeXP(ItemStack toUncraft) {
        // Xell75 & zenen method
        // TODO: Drop other methods?
        int percent = (int)(((double) toUncraft.getItemDamage() / (double) toUncraft.getMaxDamage()) * 100);
        return (ModConfiguration.maxUsedLevel * percent) / 100 + ModConfiguration.standardLevel;
    }
}
