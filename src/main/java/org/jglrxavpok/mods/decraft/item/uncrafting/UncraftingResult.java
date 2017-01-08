package org.jglrxavpok.mods.decraft.item.uncrafting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.item.ItemStack;

public class UncraftingResult 
{
    
    public ResultType resultType;
    public List<Integer> minStackSizes;
    public List<ItemStack[]> craftingGrids;
    public int experienceCost;
    public ItemStack containerItems;
    public int selectedCraftingGrid;
    
    public UncraftingResult()
    {
    	this.resultType = ResultType.INACTIVE;
    	this.minStackSizes = new ArrayList<Integer>();
    	this.craftingGrids = new ArrayList<ItemStack[]>();
    	this.experienceCost = 0;
    	this.selectedCraftingGrid = 0;
    }
    
    

//    public ResultType getResultType() 
//    {
//        return resultType;
//    }

//    public List<Integer> getMinStackSizes() 
//    {
//        return minStackSizes;
//    }
    
    public int getMinStackSize()
    {
    	return (minStackSizes.size() > 0 ? Collections.min(minStackSizes) : 1);
    }

//    public List<ItemStack[]> getCraftingGrids() 
//    {
//        return craftingGrids;
//    }
    
//    public int getExperienceCost() 
//    {
//        return experienceCost;
//    }
    
//    public ItemStack getContainerItems()
//    {
//    	return containerItems;
//    }
    

    public enum ResultType 
    {
    	INACTIVE, NOT_UNCRAFTABLE, NOT_ENOUGH_ITEMS, NOT_ENOUGH_XP, NEED_CONTAINER_ITEMS, VALID;
    	
    	public static Boolean isError(ResultType value)
    	{
    		return (value == NOT_UNCRAFTABLE || value == ResultType.NOT_ENOUGH_ITEMS || value == ResultType.NOT_ENOUGH_XP || value == NEED_CONTAINER_ITEMS);
    	}
    }

}
