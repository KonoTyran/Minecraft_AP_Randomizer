package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.managers.itemmanager.ItemManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class onPlayerChangedDimension {

    @SubscribeEvent
    public static void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if(!(event.getEntity() instanceof ServerPlayer player)) return;
        ItemManager.refreshCompasses(player);
    }
}
