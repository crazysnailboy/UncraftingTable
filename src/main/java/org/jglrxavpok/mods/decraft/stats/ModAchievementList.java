package org.jglrxavpok.mods.decraft.stats;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jglrxavpok.mods.decraft.ModUncrafting;
import org.jglrxavpok.mods.decraft.event.ItemUncraftedEvent;
import net.minecraft.advancements.Advancement;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.jglrxavpok.mods.decraft.init.ModBlocks;

@EventBusSubscriber(modid = ModUncrafting.MODID)
public class ModAchievementList
{

	// achievements
	public static final Advancement PORTEMANTEAU = null; //new Achievement("porteManteauAchievement", "porteManteauAchievement", 3 - 2, -4 - 2, Blocks.OAK_FENCE, CRAFT_TABLE).registerStat();
	private static UncraftItemTrigger UNCRAFT_TRIGGER;

	// stats
	// FIXME
//	public static final StatBasic uncraftedItemsStat = (StatBasic)(new StatBasic("stat.uncrafteditems", new TextComponentTranslation("stat.uncrafteditems", new Object[0])).registerStat());


	public static void registerTriggers()
	{
		UNCRAFT_TRIGGER = CriteriaTriggers.register(new UncraftItemTrigger());
	}

	/**
	 * Event handler for a successful uncrafting operation
	 * @param event
	 */
	@SubscribeEvent
	public static void onItemUncrafted(ItemUncraftedEvent event)
	{
		if(event.player instanceof ServerPlayerEntity)
		{
			UNCRAFT_TRIGGER.trigger((ServerPlayerEntity) event.player, event.stack);
		}

		// increment the stat counter for the number of uncrafted items
// FIXME			event.player.addStat(uncraftedItemsStat, event.quantity);
	}
}
