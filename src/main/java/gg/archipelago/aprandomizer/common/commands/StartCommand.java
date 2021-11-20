package gg.archipelago.aprandomizer.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import gg.archipelago.APClient.ClientStatus;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber
public class StartCommand {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    //build our command structure and submit it
    public static void Register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("start") //base slash command is "start"
                .executes(StartCommand::Start)
        );

    }

    private static int Start(CommandContext<CommandSourceStack> commandSourceCommandContext) {
        if(!APRandomizer.getAP().isConnected()) {
            commandSourceCommandContext.getSource().sendFailure(new TextComponent("Please connect to the Archipelago server before starting."));
            return 1;
        }
        if(!APRandomizer.isJailPlayers()) {
            commandSourceCommandContext.getSource().sendFailure(new TextComponent("The game has already started! what are you doing? START PLAYING!"));
            return 1;
        }

        Utils.sendMessageToAll("GO!");
        APRandomizer.getAP().setGameState(ClientStatus.CLIENT_PLAYING);
        APRandomizer.setJailPlayers(false);
        MinecraftServer server = APRandomizer.getServer();
        server.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(true, server);
        server.getGameRules().getRule(GameRules.RULE_WEATHER_CYCLE).set(true, server);
        server.getGameRules().getRule(GameRules.RULE_DOFIRETICK).set(true, server);
        server.getGameRules().getRule(GameRules.RULE_RANDOMTICKING).value = 3;
        server.getGameRules().getRule(GameRules.RULE_RANDOMTICKING).onChanged(server);
        server.getGameRules().getRule(GameRules.RULE_DO_PATROL_SPAWNING).set(true,server);
        server.getGameRules().getRule(GameRules.RULE_DO_TRADER_SPAWNING).set(true,server);
        server.getGameRules().getRule(GameRules.RULE_MOBGRIEFING).set(true,server);
        server.getGameRules().getRule(GameRules.RULE_DOMOBSPAWNING).set(true,server);
        server.getGameRules().getRule(GameRules.RULE_DO_IMMEDIATE_RESPAWN).set(false,server);
        server.getGameRules().getRule(GameRules.RULE_DOMOBLOOT).set(true,server);
        server.getGameRules().getRule(GameRules.RULE_DOENTITYDROPS).set(true,server);
        server.execute(() -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                player.getFoodData().eat(20,20);
                player.setHealth(20);
                player.getInventory().clearContent();
                player.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
                BlockPos spawn = server.getLevel(Level.OVERWORLD).getSharedSpawnPos();
                player.teleportTo(spawn.getX(),spawn.getY(),spawn.getZ());

                APRandomizer.getItemManager().catchUpPlayer(player);
                for (ItemStack iStack : APRandomizer.getAP().getSlotData().startingItemStacks) {
                    Utils.giveItemToPlayer(player, iStack.copy());
                }
            }
        });
        return 1;
    }
    //wait for register commands event then register ourself as a command.
    @SubscribeEvent
    static void onRegisterCommandsEvent(RegisterCommandsEvent event) {
        StartCommand.Register(event.getDispatcher());
    }
}
