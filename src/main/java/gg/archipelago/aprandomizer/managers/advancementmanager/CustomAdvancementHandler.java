package gg.archipelago.aprandomizer.managers.advancementmanager;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CustomAdvancementHandler {

    public static void grantAdvancement(ServerPlayer player, ResourceLocation location) {
        Advancement advancement = APRandomizer.getServer().getAdvancements().getAdvancement(location);
        AdvancementProgress advancementprogress = player.getAdvancements().getOrStartProgress(advancement);
        if (!advancementprogress.isDone()) {
            for (String s : advancementprogress.getRemainingCriteria()) {
                player.getAdvancements().award(advancement, s);
            }
        }
    }

    @SubscribeEvent
    public static void onAnvilRepair(AnvilRepairEvent event) {
        ItemStack item = event.getOutput();
        if (item.is(Items.GOLDEN_PICKAXE)) {
            if (item.hasTag() && item.getTag().getBoolean("truepick")) {
                if (item.getDamageValue() == 0) {
                    grantAdvancement((ServerPlayer) event.getEntity(), new ResourceLocation(APRandomizer.MODID,"archipelago/true_cheese"));
                } else if(event.getLeft().getDamageValue() < item.getDamageValue()) {
                    grantAdvancement((ServerPlayer) event.getEntity(), new ResourceLocation(APRandomizer.MODID,"archipelago/cheese"));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onCraft(PlayerEvent.ItemCraftedEvent event) {
        ItemStack item = event.getCrafting();
        if (item.is(Items.GOLDEN_PICKAXE)) {
            if (!item.isEnchanted()) {
                grantAdvancement((ServerPlayer) event.getEntity(), new ResourceLocation(APRandomizer.MODID,"archipelago/craft_golden_pick"));
            }
        }
    }
}
