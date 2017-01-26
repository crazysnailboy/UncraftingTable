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
    //disable SideOnly Client because it's effecting the serverside 
    //@SideOnly(Side.CLIENT)
	private IIcon[] icons = new IIcon[3];
	
	
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
		switch(stack.getItemDamage())
		{
			case 0: return "item.diamondNugget";
			case 1: return "item.emeraldNugget";
			case 2: return "item.ironNugget";
			default: return this.getUnlocalizedName();
		}
	}	
	
    @SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) 
	{
    	for ( int i = 0 ; i < icons.length ; i++ )
    	{
            list.add(new ItemStack(item, 1, i));
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
        this.icons[0] = reg.registerIcon(ModUncrafting.MODID + ":diamond_nugget");
        this.icons[1] = reg.registerIcon(ModUncrafting.MODID + ":emerald_nugget");
        this.icons[2] = reg.registerIcon(ModUncrafting.MODID + ":iron_nugget");
	}	
    
}
