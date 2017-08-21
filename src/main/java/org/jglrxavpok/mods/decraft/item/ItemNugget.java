package org.jglrxavpok.mods.decraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class ItemNugget extends Item
{

	public ItemNugget()
	{
		super();
		this.setHasSubtypes(true);
		this.setCreativeTab(CreativeTabs.MATERIALS);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		return "item." + EnumNuggetType.byMetadata(stack.getMetadata()).getUnlocalizedName();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list)
	{
		if (this.isInCreativeTab(tab))
		{
			for (EnumNuggetType nuggetType : EnumNuggetType.values())
			{
				if (nuggetType.getRegistryName() != null) list.add(new ItemStack(this, 1, nuggetType.getMetadata()));
			}
		}
	}

	public static enum EnumNuggetType
	{
		DIAMOND(0, "diamondNugget", "diamond_nugget"),
		EMERALD(1, "emeraldNugget", "emerald_nugget"),
		IRON(2, null, null), // IRON(2, "ironNugget", "iron_nugget"),
		LEATHER(3, "leatherStrip", "leather_strip");

		private final int meta;
		private final String unlocalizedName;
		private final String registryName;

		private static final EnumNuggetType[] META_LOOKUP = new EnumNuggetType[values().length];


		public int getMetadata()
		{
			return this.meta;
		}

		public String getUnlocalizedName()
		{
			return this.unlocalizedName;
		}

		public String getRegistryName()
		{
			return this.registryName;
		}

		public static EnumNuggetType byMetadata(int meta)
		{
			if (meta < 0 || meta >= META_LOOKUP.length) meta = 0;
			return META_LOOKUP[meta];
		}

		public static EnumNuggetType[] usedValues()
		{
			return new EnumNuggetType[] { DIAMOND, EMERALD, LEATHER };
		}


		private EnumNuggetType(int meta, String unlocalizedName, String registryName)
		{
			this.meta = meta;
			this.unlocalizedName = unlocalizedName;
			this.registryName = registryName;
		}

		static
		{
			for (EnumNuggetType value : values())
			{
				META_LOOKUP[value.getMetadata()] = value;
			}
		}
	}

}
