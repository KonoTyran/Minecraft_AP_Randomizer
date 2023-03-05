package gg.archipelago.aprandomizer.events;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.common.DeathLinkDamage;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.client.events.ArchipelagoEventListener;
import gg.archipelago.client.events.BouncedEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.Vec3;

import java.util.concurrent.ThreadLocalRandom;

public class onDeathLink {

    @ArchipelagoEventListener
    public static void onBounce(BouncedEvent event) {
        if(event.tags.contains("DeathLink") && APRandomizer.getAP().getSlotData().deathlink) {
            if(event.getDouble("time") == APRandomizer.getLastDeathTimestamp())
                return;
            DeathLinkDamage deathLink = DeathLinkDamage.DEATH_LINK;
            GameRules.BooleanValue showDeathMessages = APRandomizer.getServer().getGameRules().getRule(GameRules.RULE_SHOWDEATHMESSAGES);
            boolean showDeaths = showDeathMessages.get();
            if(showDeaths) {
                String cause = event.getString("cause");
                if(cause != null && !cause.isBlank())
                    Utils.sendMessageToAll(event.getString("cause"));
                else
                    Utils.sendMessageToAll("This Death brought to you by "+ event.getString("source"));
            }
            showDeathMessages.set(false,APRandomizer.getServer());
            for (ServerPlayer player : APRandomizer.getServer().getPlayerList().getPlayers()) {
                player.hurt(deathLink , Float.MAX_VALUE);
            }
            showDeathMessages.set(showDeaths,APRandomizer.getServer());
        }
    }
}
