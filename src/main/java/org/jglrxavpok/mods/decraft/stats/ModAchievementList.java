package org.jglrxavpok.mods.decraft.stats;

import org.jglrxavpok.mods.decraft.event.ItemUncraftedEvent;
import org.jglrxavpok.mods.decraft.init.ModBlocks;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBasic;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;


public class ModAchievementList
{

	// event handler
	private static AchievementEventHandler achievementEventHandler = new AchievementEventHandler();

	// achievements
	public static Achievement craftTable = new Achievement("createDecraftTable", "createDecraftTable", 1 - 2 - 2, -1 - 3, ModBlocks.uncrafting_table, null).registerStat();
	public static Achievement uncraftAny = new Achievement("uncraftAnything", "uncraftAnything", 2 - 2, -2 - 2, Items.diamond_hoe, craftTable).registerStat();
	public static Achievement uncraftDiamondHoe = new Achievement("uncraftDiamondHoe", "uncraftDiamondHoe", 2 - 2, 0 - 2, Items.diamond_hoe, uncraftAny).registerStat();
	public static Achievement uncraftJunk = new Achievement("uncraftJunk", "uncraftJunk", 1 - 2, -1 - 2, Items.leather_boots, uncraftAny).registerStat();
	public static Achievement uncraftDiamondShovel = new Achievement("uncraftDiamondShovel", "uncraftDiamondShovel", 3 - 2, -1 - 2, Items.diamond_shovel, uncraftAny).registerStat();
	public static Achievement porteManteau = new Achievement("porteManteauAchievement", "porteManteauAchievement", 3 - 2, -4 - 2, Blocks.oak_fence, craftTable).registerStat();

	// stats
	public static StatBasic uncraftedItemsStat = (StatBasic)(new StatBasic("stat.uncrafteditems", new ChatComponentTranslation("stat.uncrafteditems", new Object[0])).registerStat());


	public static void init()
	{
		// register the acheivements page
		AchievementPage.registerAchievementPage(new AchievementPage("Uncrafting Table",
			new Achievement[]
			{
				craftTable, uncraftAny, uncraftDiamondHoe, uncraftJunk, uncraftDiamondShovel, porteManteau
			})
		);
	}

	public static void clientInit()
	{
		// register the event handlers with the event bus
		MinecraftForge.EVENT_BUS.register(achievementEventHandler);
	}



	public static class AchievementEventHandler
	{

		@SubscribeEvent
		public void onItemCrafted(PlayerEvent.ItemCraftedEvent event)
		{
			Item item = event.crafting.getItem();

			if (item == Item.getItemFromBlock(ModBlocks.uncrafting_table))
			{
				event.player.triggerAchievement(craftTable);
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
			event.player.triggerAchievement(uncraftAny);

			// if the uncrafted item was one of those with a specific achievement associated with it, trigegr that achievement
			Item uncraftedItem = event.stack.getItem();
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

			// increment the stat counter for the number of uncrafted items
			event.player.addStat(uncraftedItemsStat, event.quantity);
		}

	}

}
