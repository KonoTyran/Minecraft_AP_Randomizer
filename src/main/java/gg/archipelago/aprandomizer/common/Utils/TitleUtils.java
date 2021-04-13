package gg.archipelago.aprandomizer.common.Utils;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.util.text.ITextComponent;

import java.util.Collection;

public class TitleUtils {

    static void resetTitle(Collection<ServerPlayerEntity> players) {
        STitlePacket stitlepacket = new STitlePacket(STitlePacket.Type.RESET, (ITextComponent)null);

        for(ServerPlayerEntity serverplayerentity : players) {
            serverplayerentity.connection.send(stitlepacket);
        }
    }

    static void showTitle(Collection<ServerPlayerEntity> players, ITextComponent textComponent, STitlePacket.Type titlePacketType) {
        for(ServerPlayerEntity serverplayerentity : players) {
            serverplayerentity.connection.send(new STitlePacket(titlePacketType, textComponent));
        }
    }

    static void setTimes(Collection<ServerPlayerEntity> players, int fadeIn, int stay, int fadeOut) {
        STitlePacket stitlepacket = new STitlePacket(fadeIn, stay, fadeOut);

        for(ServerPlayerEntity serverplayerentity : players) {
            serverplayerentity.connection.send(stitlepacket);
        }

    }

}
