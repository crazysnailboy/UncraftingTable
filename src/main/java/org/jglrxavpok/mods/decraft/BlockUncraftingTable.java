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

import org.jglrxavpok.mods.decraft.ModUncrafting;
public class BlockUncraftingTable extends Block
{

    private Object redstonedBlockIcon;
	private Object topBlock;
	private Object front;
	private Object redstonedFront;
	private Object bottom;

	public BlockUncraftingTable()
    {
        super(Material.rock);
        setBlockName("uncrafting_table");
//        this.setBlockTextureName("uncrafting_table");
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    /*@SideOnly(Side.CLIENT)
    private IIcon topBlock;
    @SideOnly(Side.CLIENT)
    private IIcon front;
    private IIcon bottom;
    private IIcon redstonedBlockIcon;
    private IIcon redstonedFront;
    private IIcon blockIcon;*/
	
	@Override
	public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ)
    {
        if (!worldIn.isRemote)
        {
            //System.out.println("player clicked block.");
           /* ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
            DataOutputStream outputStream = new DataOutputStream(bos);
            try
            {
                outputStream.writeInt(n1);
                outputStream.writeInt(n2);
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }*/

            //			C17PacketCustomPayload packet = new C17PacketCustomPayload("Uncrafting",bos.toByteArray());
            //			PacketDispatcher.sendPacketToPlayer(packet, (Player)player);
        }
        	
    	player.openGui(ModUncrafting.instance, 0, worldIn, x, y, z);
    	//System.out.println("player clicked");
        return true;
        /**
         * @see org.jglrxavpok.mods.decraft.ModUncrafting
         */
    }

//    /**
//     * Called whenever the block is added into the world. Args: world, x, y, z
//     * This one is used to know if there is a redstone power near it.
//     * onBlockAdded
//     */
//    public void onBlockAdded(World worldIn, int x, int y, int z)
//    {
//        if (!worldIn.isRemote)
//        {
//            /*if (worldIn.getBlockMetadata(x, y, z) == 1 && !worldIn.isBlockIndirectlyGettingPowered(x, y, z))
//            {
//            	worldIn.scheduleBlockUpdate(x, y, z, this, 4);
//            }
//            else if (worldIn.getBlockMetadata(x, y, z) == 0 && worldIn.isBlockIndirectlyGettingPowered(x, y, z))
//            {
//            	worldIn.setBlock(x, y, z, this, 1, 2);
//            }*/
//        }
//    }

//    /**
//     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
//     * their own) Args: x, y, z, neighbor blockID
//     * This one is used to know if there is a redstone power near it.
//     */
//    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor)
//    {
//        if (!worldIn.isRemote)
//        {
//        	/*if (worldIn.getBlockMetadata(x, y, z) == 1 && !worldIn.isBlockIndirectlyGettingPowered(x, y, z))
//            {
//        		worldIn.scheduleBlockUpdate(x, y, z, this, 4);
//            }
//            else if (worldIn.getBlockMetadata(x, y, z) == 0 && worldIn.isBlockIndirectlyGettingPowered(x, y, z))
//            {
//            	worldIn.setBlock(x, y, z, this, 1, 2);
//            }*/
//        }
//    }

//    /**
//     * Ticks the block if it's been scheduled
//     * updateTick
//     */
//    public void updateTick(World worldIn, int x, int y, int z, Random random)
//    {
//    	/*if (!worldIn.isRemote && worldIn.getBlockMetadata(x, y, z) == 1 && !worldIn.isBlockIndirectlyGettingPowered(x, y, z))
//        {
//            worldIn.setBlock(x, y, z, this, 0, 2);
//        }*/
//    }

   

    @SideOnly(Side.CLIENT)
    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     * 
     * -> getIcon
     */
   public IIcon getIcon(int par1, int par2)
   {
       if (par2 == 0)
       {
          return (IIcon) (par1 == 1 ? this.topBlock : (par1 == 0 ? bottom : (par1 != 3 && par1 != 1 ? this.blockIcon : this.front)));
       }
       else
       {
            return (IIcon) (par1 == 1 ? this.bottom : (par1 == 0 ? topBlock : (par1 != 3 && par1 != 1 ? this.redstonedBlockIcon : this.redstonedFront)));
        }
    }
    
	@SideOnly(Side.CLIENT)
	@Override
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon("uncraftingTable:uncrafting_side");
        this.redstonedBlockIcon = par1IconRegister.registerIcon("uncraftingTable:uncrafting_side_redstoned");
        this.topBlock = par1IconRegister.registerIcon("uncraftingTable:uncrafting_top");
        this.front = par1IconRegister.registerIcon("uncraftingTable:uncrafting_front");
        this.redstonedFront = par1IconRegister.registerIcon("uncraftingTable:uncrafting_front_redstoned");
        this.bottom = par1IconRegister.registerIcon("uncraftingTable:uncrafting_bottom");
    }

}
