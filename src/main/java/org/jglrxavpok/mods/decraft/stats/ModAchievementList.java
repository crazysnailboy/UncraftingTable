package org.jglrxavpok.mods.decraft.stats;

import org.jglrxavpok.mods.decraft.event.ItemUncraftedEvent;
import org.jglrxavpok.mods.decraft.init.ModBlocks;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBasic;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class ModAchievementList {

	// event handler
	private static AchievementEventHandler achievementEventHandler = new AchievementEventHandler();
	
	// achievements
    public static final Achievement CRAFT_TABLE = new Achievement("createDecraftTable", "createDecraftTable", 1 - 2 - 2, -1 - 3, ModBlocks.UNCRAFTING_TABLE, null).registerStat();
    public static final Achievement UNCRAFT_ANY = new Achievement("uncraftAnything", "uncraftAnything", 2 - 2, -2 - 2, Items.DIAMOND_HOE, CRAFT_TABLE).registerStat();
    public static final Achievement UNCRAFT_DIAMOND_HOE = new Achievement("uncraftDiamondHoe", "uncraftDiamondHoe", 2 - 2, 0 - 2, Items.DIAMOND_HOE, UNCRAFT_ANY).registerStat();
    public static final Achievement UNCRAFT_JUNK = new Achievement("uncraftJunk", "uncraftJunk", 1 - 2, -1 - 2, Items.LEATHER_BOOTS, UNCRAFT_ANY).registerStat();
    public static final Achievement UNCRAFT_DIAMOND_SHOVEL = new Achievement("uncraftDiamondShovel", "uncraftDiamondShovel", 3 - 2, -1 - 2, Items.DIAMOND_SHOVEL, UNCRAFT_ANY).registerStat();
    public static final Achievement PORTEMANTEAU = new Achievement("porteManteauAchievement", "porteManteauAchievement", 3 - 2, -4 - 2, Blocks.OAK_FENCE, CRAFT_TABLE).registerStat();

    // stats
    public static final StatBasic uncraftedItemsStat = (StatBasic)(new StatBasic("stat.uncrafteditems", new TextComponentTranslation("stat.uncrafteditems", new Object[0])).registerStat());
    
    
    public static void init(){
    	
        // register the acheivements page
        AchievementPage.registerAchievementPage(new AchievementPage("Uncrafting Table",
            new Achievement[]
            {
            	CRAFT_TABLE, UNCRAFT_ANY, UNCRAFT_DIAMOND_HOE, UNCRAFT_JUNK, UNCRAFT_DIAMOND_SHOVEL, PORTEMANTEAU
            })
        );
    	
    }
    
	public static void clientInit() {
		
		// register the event handlers with the event bus
        MinecraftForge.EVENT_BUS.register(achievementEventHandler);
	}
    
    
    
    
    public static class AchievementEventHandler
    {
    	
		@SubscribeEvent
        public void onItemCrafted(PlayerEvent.ItemCraftedEvent event)
        {    	
	    	Item item = event.crafting.getItem();
	    	
	    	if (item == Item.getItemFromBlock(ModBlocks.UNCRAFTING_TABLE))
			{
	    		event.player.addStat(CRAFT_TABLE);
			}
        }
    	
		/**
		 * Event handler for a successful uncrafting operation
		 * @param event
		 */
		@SubscribeEvent
        public void onItemUncrafted(ItemUncraftedEvent event)
    	{
			// trigger the "uncrafted anything" achievement
            event.player.addStat(UNCRAFT_ANY);
			
            // if the uncrafted item was one of those with a specific achievement associated with it, trigegr that achievement
	        Item uncraftedItem = event.stack.getItem();
	        if (uncraftedItem == Items.DIAMOND_HOE)
	        {
	            event.player.addStat(UNCRAFT_DIAMOND_HOE);
	        }
	        else if (uncraftedItem == Items.DIAMOND_SHOVEL)
	        {
	            event.player.addStat(UNCRAFT_DIAMOND_SHOVEL);
	        }
	        else if (
	    		(uncraftedItem == Items.LEATHER_LEGGINGS) ||
	    		(uncraftedItem == Items.LEATHER_HELMET) ||
	    		(uncraftedItem == Items.LEATHER_BOOTS) ||
	    		(uncraftedItem == Items.LEATHER_CHESTPLATE) ||
	    		(uncraftedItem == Items.GLASS_BOTTLE)
	        )
	        {
	            event.player.addStat(UNCRAFT_JUNK);
	        }
	        
	        // increment the stat counter for the number of uncrafted items
	        event.player.addStat(uncraftedItemsStat, event.quantity);
    	}
    	
    }
    
    
	
}
