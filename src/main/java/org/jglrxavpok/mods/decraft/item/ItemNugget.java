package org.jglrxavpok.mods.decraft.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class ItemNugget extends Item
{

	public ItemNugget()
	{
		super();
		this.setHasSubtypes(true);
		this.setUnlocalizedName("nugget");
		this.setRegistryName("nugget");
		this.setCreativeTab(CreativeTabs.tabMaterials);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		return "item." + EnumNuggetType.byMetadata(stack.getMetadata()).getUnlocalizedName();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		for (EnumNuggetType nuggetType : EnumNuggetType.values())
		{
			list.add(new ItemStack(item, 1, nuggetType.getMetadata()));
		}
	}

	public static enum EnumNuggetType
	{
		DIAMOND(0, "diamond"),
		EMERALD(1, "emerald"),
		IRON(2, "iron");

		private final int meta;
		private final String name;
		private static final EnumNuggetType[] META_LOOKUP = new EnumNuggetType[values().length];


		public int getMetadata()
		{
			return this.meta;
		}

		public String getUnlocalizedName()
		{
			return this.name + "Nugget";
		}

		public String getRegistryName()
		{
			return this.name + "_nugget";
		}

		public static EnumNuggetType byMetadata(int meta)
		{
			if (meta < 0 || meta >= META_LOOKUP.length) meta = 0;
			return META_LOOKUP[meta];
		}


		private EnumNuggetType(int meta, String name)
		{
			this.meta = meta;
			this.name = name;
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
