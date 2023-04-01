package gg.archipelago.aprandomizer.managers.advancementmanager;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
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
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getLevel().getBlockState(event.getPos()).is(Blocks.MANGROVE_LOG)) {
            grantAdvancement((ServerPlayer) event.getPlayer(), new ResourceLocation(APRandomizer.MODID, "archipelago/punch_tree"));
        }
    }

    @SubscribeEvent
    public static void onAnvilRepair(AnvilRepairEvent event) {
        ItemStack item = event.getOutput();
        if (item.is(Items.GOLDEN_PICKAXE)) {
            if (item.hasTag() && item.getTag().getBoolean("truepick")) {
                if (item.getDamageValue() == 0) {
                    grantAdvancement((ServerPlayer) event.getEntity(), new ResourceLocation(APRandomizer.MODID, "archipelago/true_cheese"));
                } else if (event.getLeft().getDamageValue() < item.getDamageValue()) {
                    grantAdvancement((ServerPlayer) event.getEntity(), new ResourceLocation(APRandomizer.MODID, "archipelago/cheese"));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onCraft(PlayerEvent.ItemCraftedEvent event) {
        ItemStack item = event.getCrafting();
        if (item.is(Items.GOLDEN_PICKAXE)) {
            if (!item.isEnchanted()) {
                grantAdvancement((ServerPlayer) event.getEntity(), new ResourceLocation(APRandomizer.MODID, "archipelago/craft_golden_pick"));
            }
        }
    }

    @SubscribeEvent
    public static void onKill(LivingDeathEvent event) {
        if (event.getSource().is(DamageTypeTags.IS_EXPLOSION)) {
            if (event.getEntity() instanceof ServerPlayer && event.getSource().getEntity() instanceof ServerPlayer) {
                if (event.getEntity().equals(event.getSource().getEntity())) {
                    CustomAdvancementHandler.grantAdvancement((ServerPlayer) event.getEntity(), new ResourceLocation(APRandomizer.MODID, "archipelago/tnt_kill_self"));

                } else {
                    CustomAdvancementHandler.grantAdvancement((ServerPlayer)event.getSource().getEntity(), new ResourceLocation(APRandomizer.MODID, "archipelago/tnt_kill_other"));
                }
            }
        }
    }
}
