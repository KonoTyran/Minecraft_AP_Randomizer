package gg.archipelago.aprandomizer.common.Utils;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import gg.archipelago.APClient.Print.APPrint;
import gg.archipelago.APClient.Print.APPrintColor;
import gg.archipelago.APClient.Print.APPrintPart;
import gg.archipelago.APClient.Print.APPrintType;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.capability.CapabilityWorldData;
import gg.archipelago.aprandomizer.capability.WorldData;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Utils
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    /**
     *
     * @param source command source to send the message.
     * @param Message Message to send
     *                send a message to whoever ran the command.
     */

    private static MinecraftServer server = APRandomizer.getServer();

    public static void SendMessage(CommandSource source, String Message) {
        try {
            ServerPlayerEntity player = source.getPlayerOrException();
            player.sendMessage(new StringTextComponent(Message), Util.NIL_UUID);
        } catch (CommandSyntaxException e) {
            source.getServer().sendMessage(new StringTextComponent(Message), Util.NIL_UUID);
        }
    }

    public static void sendMessageToAll(String message) {
        sendMessageToAll(new StringTextComponent(message));
    }

    public static void sendMessageToAll(ITextComponent message) {
        server.getPlayerList().broadcastMessage(message,ChatType.SYSTEM, Util.NIL_UUID);
    }

    public static void sendFancyMessageToAll(APPrint apPrint) {
        LOGGER.trace("in send fancy");
        ITextComponent message = Utils.apPrintToTextComponent(apPrint);
        LOGGER.trace("found " + apPrint.parts.length + " parts");

        server.getPlayerList().broadcastMessage(message,ChatType.SYSTEM, Util.NIL_UUID);

    }

    public static ITextComponent apPrintToTextComponent(APPrint apPrint) {
        StringTextComponent message = new StringTextComponent("");
        for (int i = 0; apPrint.parts.length > i; ++i) {
            APPrintPart part = apPrint.parts[i];
            LOGGER.trace("part["+i+"]: " + part.text + ", "+part.color+", "+part.type);
            Style style = Style.EMPTY;
            if (part.color == null) {
                if (APRandomizer.getAP().getMyName().equals(part.text)) {
                    style = Style.EMPTY.withColor(Color.fromRgb(APPrintColor.gold.value)).withBold(true);
                }
                else if (part.type == APPrintType.playerID) {
                    style = Style.EMPTY.withColor(Color.fromRgb(APPrintColor.yellow.value));
                }
                else if (part.type == APPrintType.locationID) {
                    style = Style.EMPTY.withColor(Color.fromRgb(APPrintColor.green.value));
                }
                else if (part.type == APPrintType.itemID) {
                    style = Style.EMPTY.withColor(Color.fromRgb(APPrintColor.cyan.value));
                }
            }
            else if (part.color == APPrintColor.underline)
                style = Style.EMPTY.withUnderlined(true);
            else if (part.color == APPrintColor.bold)
                style = Style.EMPTY.withBold(true);
            else
                style = Style.EMPTY.withColor(Color.fromRgb(part.color.value));

            message.append(new StringTextComponent(part.text).withStyle(style));
        }
        return message;
    }

    public static void sendTitle(ITextComponent title, ITextComponent subTitle, int fadeIn, int stay, int fadeOut) {

        TitleUtils.setTimes(server.getPlayerList().getPlayers(), fadeIn, stay, fadeOut);


        TitleUtils.showTitle(server.getPlayerList().getPlayers(), subTitle, STitlePacket.Type.SUBTITLE);
        TitleUtils.showTitle(server.getPlayerList().getPlayers(), title, STitlePacket.Type.TITLE);

    }

    public static void sendActionBar(String actionBarMessage, int fadeIn, int stay, int fadeOut) {

        TitleUtils.setTimes(server.getPlayerList().getPlayers(), fadeIn, stay, fadeOut);

        ITextComponent subTitleMessage = new StringTextComponent(actionBarMessage);

        TitleUtils.showTitle(server.getPlayerList().getPlayers(), subTitleMessage, STitlePacket.Type.ACTIONBAR);

    }

    public static void PlaySoundToAll(SoundEvent sound) {
        for (ServerPlayerEntity player : server.getPlayerList().getPlayers()) {
            player.playNotifySound(sound,SoundCategory.MASTER,1,1);
        }
    }

    public static void SpawnDragon(ServerWorld end) {
        end.dragonFight.spawnExitPortal(false);
        end.dragonFight.findOrCreateDragon();
        end.dragonFight.dragonKilled = false;
        end.dragonFight.previouslyKilled = false;
        end.getCapability(CapabilityWorldData.CAPABILITY_WORLD_DATA).orElseThrow(AssertionError::new).setDragonState(WorldData.DRAGON_SPAWNED);
    }
}
