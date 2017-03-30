package org.jglrxavpok.mods.decraft.item;

import java.util.List;

import org.jglrxavpok.mods.decraft.ModUncrafting;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;


public class ItemNugget extends Item
{

	@SideOnly(Side.CLIENT)
	private IIcon[] icons;


	public ItemNugget()
	{
		super();
		this.setHasSubtypes(true);
		this.setUnlocalizedName("nugget");
		this.setCreativeTab(CreativeTabs.tabMaterials);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		return "item." + EnumNuggetType.byMetadata(stack.getItemDamage()).getUnlocalizedName();
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

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int meta)
	{
		if (meta > (icons.length - 1)) meta = 0;
		return this.icons[meta];
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister reg)
	{
		this.icons = new IIcon[EnumNuggetType.values().length];
		for (EnumNuggetType nuggetType : EnumNuggetType.values())
		{
			this.icons[nuggetType.getMetadata()] = reg.registerIcon(ModUncrafting.MODID + ":" + nuggetType.getRegistryName());
		}
	}

	public static enum EnumNuggetType
	{
		DIAMOND(0, "diamondNugget", "diamond_nugget"),
		EMERALD(1, "emeraldNugget", "emerald_nugget"),
		IRON(2, "ironNugget", "iron_nugget"),
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
