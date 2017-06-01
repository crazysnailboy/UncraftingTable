package org.jglrxavpok.mods.decraft.item.uncrafting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;


public class UncraftingResult
{

	public ResultType resultType;
	public List<Map.Entry<NonNullList<ItemStack>,Integer>> craftingGrids;
	public int experienceCost;
	public int selectedCraftingGrid;

	public UncraftingResult()
	{
		this.resultType = ResultType.INACTIVE;
		this.craftingGrids = new ArrayList<Map.Entry<NonNullList<ItemStack>,Integer>>();
		this.experienceCost = 0;
		this.selectedCraftingGrid = 0;
	}


//	public ResultType getResultType()
//	{
//		return resultType;
//	}

	public int getRecipeCount()
	{
		return craftingGrids.size();
	}

	public int getMinStackSize()
	{
		return (craftingGrids.size() > 0 ? craftingGrids.get(selectedCraftingGrid).getValue() : 1);
	}

	public NonNullList<ItemStack> getCraftingGrid()
	{
		return (craftingGrids.size() > 0 ? craftingGrids.get(selectedCraftingGrid).getKey() : NonNullList.<ItemStack>create());
	}


//	public int getExperienceCost()
//	{
//		return experienceCost;
//	}


	public boolean isError()
	{
		return ArrayUtils.indexOf(ResultType.IS_ERROR, this.resultType) >= 0;
	}

	public boolean canPopulateInventory()
	{
		return ArrayUtils.indexOf(ResultType.CAN_POPULATE_INVENTORY, this.resultType) >= 0;
	}

	public boolean renderBackgroundItems()
	{
		return ArrayUtils.indexOf(ResultType.RENDER_BACKGROUND_ITEMS, this.resultType) >= 0;
	}


	public enum ResultType
	{
		INACTIVE,
		NOT_UNCRAFTABLE,
		NOT_ENOUGH_ITEMS,
		NOT_ENOUGH_XP,
		NEED_CONTAINER_ITEMS,
		VALID,
		UNCRAFTED;

		private static final ResultType[] IS_ERROR = new ResultType[] { NOT_UNCRAFTABLE, NOT_ENOUGH_ITEMS, NOT_ENOUGH_XP, NEED_CONTAINER_ITEMS };
		private static final ResultType[] CAN_POPULATE_INVENTORY = new ResultType[] { VALID, NEED_CONTAINER_ITEMS };
		private static final ResultType[] RENDER_BACKGROUND_ITEMS = new ResultType[] { NOT_ENOUGH_ITEMS, NOT_ENOUGH_XP, NEED_CONTAINER_ITEMS };

	}

}
