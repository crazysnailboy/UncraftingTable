package org.jglrxavpok.mods.decraft.stats;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import org.jglrxavpok.mods.decraft.ModUncrafting;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class UncraftItemTrigger implements ICriterionTrigger<UncraftItemTrigger.Instance> {
    
    public static final ResourceLocation ID = new ResourceLocation(ModUncrafting.MODID, "uncraft");
    private final Map<PlayerAdvancements, UncraftItemTrigger.Listeners> listeners = Maps.newHashMap();

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<UncraftItemTrigger.Instance> listener) {
        UncraftItemTrigger.Listeners thisListeners = this.listeners.get(playerAdvancementsIn);
        if (thisListeners == null) {
            thisListeners = new UncraftItemTrigger.Listeners(playerAdvancementsIn);
            this.listeners.put(playerAdvancementsIn, thisListeners);
        }

        thisListeners.add(listener);
    }

    public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<UncraftItemTrigger.Instance> listener) {
        UncraftItemTrigger.Listeners thisListeners = this.listeners.get(playerAdvancementsIn);
        if (thisListeners != null) {
            thisListeners.remove(listener);
            if (thisListeners.isEmpty()) {
                this.listeners.remove(playerAdvancementsIn);
            }
        }

    }

    public void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {
        this.listeners.remove(playerAdvancementsIn);
    }

    @Override
    public Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        ItemPredicate[] itemPredicates = ItemPredicate.deserializeArray(json.get("items"));
        return new Instance(itemPredicates);
    }

    public void trigger(ServerPlayerEntity player, ItemStack stack) {
        UncraftItemTrigger.Listeners listeners = this.listeners.get(player.getAdvancements());
        if (listeners != null) {
            listeners.trigger(player, stack);
        }
    }

    public static class Instance extends CriterionInstance {

        private ItemPredicate[] itemPredicates;

        public Instance(ItemPredicate[] itemPredicates) {
            super(UncraftItemTrigger.ID);
            this.itemPredicates = itemPredicates;
        }

        /**
         * Check if meets the conditions
         * @param player
         * @param stack
         * @return
         */
        public boolean test(ServerPlayerEntity player, ItemStack stack) {
            if(itemPredicates.length == 0)
                return true;
            for (int i = 0; i < itemPredicates.length; i++) {
                ItemPredicate predicate = itemPredicates[i];
                if(predicate.test(stack)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public JsonElement serialize() {
            JsonObject obj = new JsonObject();
            JsonArray predicateArray = new JsonArray();
            for (int i = 0; i < itemPredicates.length; i++) {
                predicateArray.add(itemPredicates[i].serialize());
            }
            obj.add("items", predicateArray);
            return obj;
        }
    }

    static class Listeners {
        private final PlayerAdvancements playerAdvancements;
        private final Set<Listener<UncraftItemTrigger.Instance>> listeners = Sets.newHashSet();

        public Listeners(PlayerAdvancements p_i47439_1_) {
            this.playerAdvancements = p_i47439_1_;
        }

        public boolean isEmpty() {
            return this.listeners.isEmpty();
        }

        public void add(ICriterionTrigger.Listener<UncraftItemTrigger.Instance> listener) {
            this.listeners.add(listener);
        }

        public void remove(ICriterionTrigger.Listener<UncraftItemTrigger.Instance> listener) {
            this.listeners.remove(listener);
        }

        public void trigger(ServerPlayerEntity player, ItemStack stack) {
            List<Listener<UncraftItemTrigger.Instance>> list = null;

            for(ICriterionTrigger.Listener<UncraftItemTrigger.Instance> listener : this.listeners) {
                if (listener.getCriterionInstance().test(player, stack)) {
                    if (list == null) {
                        list = Lists.newArrayList();
                    }

                    list.add(listener);
                }
            }

            if (list != null) {
                for(ICriterionTrigger.Listener<UncraftItemTrigger.Instance> listener1 : list) {
                    listener1.grantCriterion(this.playerAdvancements);
                }
            }

        }
    }
}
