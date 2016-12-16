package org.jglrxavpok.mods.decraft;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import org.jglrxavpok.mods.decraft.stats.ModAchievements;
public class BlockUncraftingTable extends Block
{

    @SideOnly(Side.CLIENT)
    private IIcon topBlock;
    
    @SideOnly(Side.CLIENT)
    private IIcon front;
    private IIcon bottom;
    private IIcon blockIcon;
	

	public BlockUncraftingTable()
    {
        super(Material.rock);
        setBlockName("uncrafting_table");
        setHardness(3.5F);
        setStepSound(soundTypePiston);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

	@Override
	public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ)
    {
        if (!worldIn.isRemote)
        {
        }
    	player.openGui(ModUncrafting.instance, ModGuiHandler.GUI_TABLE, worldIn, x, y, z);
		checkForPorteManteau(player, worldIn, x, y, z);
        return true;
    }

   
	private void checkForPorteManteau(EntityPlayer player, World worldIn, int x, int y, int z)
	{
		boolean furnace = false;
		boolean chest = false;
		boolean workbench = false;
		
		// if the block beneath is a fence...
		if (worldIn.getBlock(x, y - 1, z) == Blocks.fence)
		{
			// check if one of the adjacent blocks is a furnace
			if (
				(worldIn.getBlock(x + 1, y, z) == Blocks.furnace) || (worldIn.getBlock(x + 1, y, z) == Blocks.lit_furnace) || 
				(worldIn.getBlock(x - 1, y, z) == Blocks.furnace) || (worldIn.getBlock(x - 1, y, z) == Blocks.lit_furnace) || 
				(worldIn.getBlock(x, y, z + 1) == Blocks.furnace) || (worldIn.getBlock(x, y, z + 1) == Blocks.lit_furnace) || 
				(worldIn.getBlock(x, y, z - 1) == Blocks.furnace) || (worldIn.getBlock(x, y, z - 1) == Blocks.lit_furnace)
			) 
			{
				furnace = true;
			}
			
			// check if one of the adjacent blocks is a chest
			if (
				(worldIn.getBlock(x + 1, y, z) == Blocks.chest) || 
				(worldIn.getBlock(x - 1, y, z) == Blocks.chest) || 
				(worldIn.getBlock(x, y, z + 1) == Blocks.chest) || 
				(worldIn.getBlock(x, y, z - 1) == Blocks.chest)
			) 
			{
				chest = true;
			}
			
			// check if one of the adjacent blocks is a crafting table
			if (
				(worldIn.getBlock(x + 1, y, z) == Blocks.crafting_table) || 
				(worldIn.getBlock(x - 1, y, z) == Blocks.crafting_table) || 
				(worldIn.getBlock(x, y, z + 1) == Blocks.crafting_table) || 
				(worldIn.getBlock(x, y, z - 1) == Blocks.crafting_table)
			) 
			{
				workbench = true;
			}
			
			// if the block is adjacent to all three, trigger the achievement
			if ((furnace) && (chest) && (workbench)) 
			{
				player.triggerAchievement(ModAchievements.porteManteauAchievement);
			}
		}
	}
	  
    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
    	return (IIcon) (side == 1 ? this.topBlock : (side == 0 ? bottom : (side != 3 && side != 1 ? this.blockIcon : this.front)));
    }
    
	@SideOnly(Side.CLIENT)
	@Override
    public void registerBlockIcons(IIconRegister reg)
    {
        this.blockIcon = reg.registerIcon("uncraftingTable:uncrafting_side");
        this.topBlock = reg.registerIcon("uncraftingTable:uncrafting_top");
        this.front = reg.registerIcon("uncraftingTable:uncrafting_front");
        this.bottom = reg.registerIcon("uncraftingTable:uncrafting_bottom");
    }

}
