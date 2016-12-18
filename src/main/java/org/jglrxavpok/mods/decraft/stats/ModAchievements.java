package org.jglrxavpok.mods.decraft.stats;

import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.common.config.ModConfiguration.ConfigEventHandler;
import org.jglrxavpok.mods.decraft.event.ItemUncraftedEvent;
import org.jglrxavpok.mods.decraft.event.UncraftingEvent;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.MinecraftForge;

public class ModAchievements {

	private static AchievementEventHandler achievementEventHandler = new AchievementEventHandler();
	
    public static Achievement craftTable = new Achievement("createDecraftTable", "createDecraftTable", 1 - 2 - 2, -1 - 3, ModUncrafting.uncraftingTable, null);
    public static Achievement uncraftAny = new Achievement("uncraftAnything", "uncraftAnything", 2 - 2, -2 - 2, Items.DIAMOND_HOE, craftTable);
    public static Achievement uncraftDiamondHoe = new Achievement("uncraftDiamondHoe", "uncraftDiamondHoe", 2 - 2, 0 - 2, Items.DIAMOND_HOE, uncraftAny);
    public static Achievement uncraftJunk = new Achievement("uncraftJunk", "uncraftJunk", 1 - 2, -1 - 2, Items.LEATHER_BOOTS, uncraftAny);
    public static Achievement uncraftDiamondShovel = new Achievement("uncraftDiamondShovel", "uncraftDiamondShovel", 3 - 2, -1 - 2, Items.DIAMOND_SHOVEL, uncraftAny);
    public static Achievement porteManteauAchievement = new Achievement("porteManteauAchievement", "porteManteauAchievement", 3 - 2, -4 - 2, Blocks.OAK_FENCE, craftTable);

    
    
    public static void init(){
    	
		// register achievements
        craftTable.registerStat();
        uncraftAny.registerStat();
        uncraftDiamondHoe.registerStat();
        uncraftJunk.registerStat();
        uncraftDiamondShovel.registerStat();
        porteManteauAchievement.registerStat();
    	
        // register the acheivements page
        AchievementPage.registerAchievementPage(new AchievementPage("Uncrafting Table",
            new Achievement[]
            {
            	craftTable, uncraftAny, uncraftDiamondHoe, uncraftJunk, uncraftDiamondShovel, porteManteauAchievement
            })
        );
    	
    }
    
	public static void clientInit() {
		MinecraftForge.EVENT_BUS.register(achievementEventHandler);
//        FMLCommonHandler.instance().bus().register(achievementEventHandler);
	}
    
    
    
    
    public static class AchievementEventHandler
    {
    	
		@SubscribeEvent
        public void onItemCrafted(PlayerEvent.ItemCraftedEvent event)
        {    	
	    	Item item = event.crafting.getItem();
	    	
	    	if (item == Item.getItemFromBlock(ModUncrafting.uncraftingTable))
			{
	    		event.player.addStat(craftTable);
			}
        }
    	
	    @SubscribeEvent
	    public void onUncrafting(UncraftingEvent event)
	    {
	    }
    	
		@SubscribeEvent
        public void onItemUncrafted(ItemUncraftedEvent event)
    	{
            event.player.addStat(uncraftAny);
			
	        Item uncraftedItem = event.getUncrafted().getItem();
	        
	        if (uncraftedItem == Items.DIAMOND_HOE)
	        {
	            event.player.addStat(uncraftDiamondHoe);
	        }
	        else if (uncraftedItem == Items.DIAMOND_SHOVEL)
	        {
	            event.player.addStat(uncraftDiamondShovel);
	        }
	        else if (
	    		(uncraftedItem == Items.LEATHER_LEGGINGS) ||
	    		(uncraftedItem == Items.LEATHER_HELMET) ||
	    		(uncraftedItem == Items.LEATHER_BOOTS) ||
	    		(uncraftedItem == Items.LEATHER_CHESTPLATE) ||
	    		(uncraftedItem == Items.GLASS_BOTTLE)
	        )
	        {
	            event.player.addStat(uncraftJunk);
	        }
    	}
    	
    }
    
    
	
}
