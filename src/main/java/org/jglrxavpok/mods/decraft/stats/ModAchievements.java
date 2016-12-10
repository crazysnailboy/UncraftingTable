package org.jglrxavpok.mods.decraft.stats;

import org.jglrxavpok.mods.decraft.ModUncrafting;
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

public class ModAchievements {

	private static AchievementEventHandler achievementEventHandler = new AchievementEventHandler();
	
    public static Achievement craftTable = new Achievement("createDecraftTable", "createDecraftTable", 1 - 2 - 2, -1 - 3, ModUncrafting.uncraftingTable, null);
    public static Achievement uncraftAny = new Achievement("uncraftAnything", "uncraftAnything", 2 - 2, -2 - 2, Items.diamond_hoe, craftTable);
    public static Achievement uncraftDiamondHoe = new Achievement("uncraftDiamondHoe", "uncraftDiamondHoe", 2 - 2, 0 - 2, Items.diamond_hoe, uncraftAny);
    public static Achievement uncraftJunk = new Achievement("uncraftJunk", "uncraftJunk", 1 - 2, -1 - 2, Items.leather_boots, uncraftAny);
    public static Achievement uncraftDiamondShovel = new Achievement("uncraftDiamondShovel", "uncraftDiamondShovel", 3 - 2, -1 - 2, Items.diamond_shovel, uncraftAny);
    public static Achievement porteManteauAchievement = new Achievement("porteManteauAchievement", "porteManteauAchievement", 3 - 2, -4 - 2, Blocks.oak_fence, craftTable);

    
    
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
        FMLCommonHandler.instance().bus().register(achievementEventHandler);
	}
    
    
    
    
    public static class AchievementEventHandler
    {
    	
		@SubscribeEvent
        public void onItemCrafted(PlayerEvent.ItemCraftedEvent event)
        {    	
	    	Item item = event.crafting.getItem();
	    	
	    	if (item == Item.getItemFromBlock(ModUncrafting.uncraftingTable))
			{
	    		event.player.triggerAchievement(craftTable);
			}
        }
    	
	    @SubscribeEvent
	    public void onUncrafting(UncraftingEvent event)
	    {
	    }
    	
		@SubscribeEvent
        public void onItemUncrafted(ItemUncraftedEvent event)
    	{
            event.player.triggerAchievement(uncraftAny);
			
	        Item uncraftedItem = event.getUncrafted().getItem();
	        
	        if (uncraftedItem == Items.diamond_hoe)
	        {
	            event.player.triggerAchievement(uncraftDiamondHoe);
	        }
	        else if (uncraftedItem == Items.diamond_shovel)
	        {
	            event.player.triggerAchievement(uncraftDiamondShovel);
	        }
	        else if (
	    		(uncraftedItem == Items.leather_leggings) ||
	    		(uncraftedItem == Items.leather_helmet) ||
	    		(uncraftedItem == Items.leather_boots) ||
	    		(uncraftedItem == Items.leather_chestplate) ||
	    		(uncraftedItem == Items.glass_bottle)
	        )
	        {
	            event.player.triggerAchievement(uncraftJunk);
	        }
    	}
    	
    }
    
    
	
}
