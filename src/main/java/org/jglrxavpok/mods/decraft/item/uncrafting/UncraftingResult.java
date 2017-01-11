package org.jglrxavpok.mods.decraft.item.uncrafting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.item.ItemStack;

public class UncraftingResult 
{
    
    public ResultType resultType;
    public List<Integer> minStackSizes;
    public List<ItemStack[]> craftingGrids;
    public int experienceCost;
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
    	return (minStackSizes.size() > 0 ? minStackSizes.get(selectedCraftingGrid) : 1);
    }
    
    public ItemStack[] getCraftingGrid()
    {
    	return (craftingGrids.size() > 0 ? craftingGrids.get(selectedCraftingGrid) : null);
    }

//    public List<ItemStack[]> getCraftingGrids() 
//    {
//        return craftingGrids;
//    }
    
//    public int getExperienceCost() 
//    {
//        return experienceCost;
//    }
    

    public boolean canPopulateInventory()
    {
		return ArrayUtils.indexOf(ResultType.CAN_POPULATE_INVENTORY, this.resultType) >= 0;
    }
    
    public boolean isError()
    {
		return ArrayUtils.indexOf(ResultType.IS_ERROR, this.resultType) >= 0;
    }
    
    
    public enum ResultType 
    {
    	INACTIVE, VALID,
    	NOT_UNCRAFTABLE, NOT_ENOUGH_ITEMS, NOT_ENOUGH_XP, NEED_CONTAINER_ITEMS;
    	
    	private static final ResultType[] CAN_POPULATE_INVENTORY = new ResultType[] { VALID, NEED_CONTAINER_ITEMS };
    	private static final ResultType[] IS_ERROR = new ResultType[] { NOT_UNCRAFTABLE, NOT_ENOUGH_ITEMS, NOT_ENOUGH_XP, NEED_CONTAINER_ITEMS };

    }

}
