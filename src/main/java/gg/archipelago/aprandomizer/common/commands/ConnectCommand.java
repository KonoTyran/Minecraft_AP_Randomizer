package gg.archipelago.aprandomizer.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import gg.archipelago.aprandomizer.APClient;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.APStorage.APMCData;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber
public class ConnectCommand {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    //build our command structure and submit it
    public static void Register(CommandDispatcher<CommandSource> dispatcher) {

        dispatcher.register(
                Commands.literal("connect") //base slash command is "connect"
                //first make sure its NOT a dedicated server (aka single player or hosted via in game client, OR user has an op level of 1)
                .requires((CommandSource) -> (!CommandSource.getServer().isDedicatedServer() || CommandSource.hasPermission(1)))
                //take the first argument as a string and name it "Address"
                .then(Commands.argument("Address", StringArgumentType.string())
                        .executes(context -> connectToAPServer(
                                context,
                                StringArgumentType.getString(context,"Address"),
                                38281,
                                null
                        ))
                        .then(Commands.argument("Port", IntegerArgumentType.integer())
                                .executes(context -> connectToAPServer(
                                        context,
                                        StringArgumentType.getString(context,"Address"),
                                        IntegerArgumentType.getInteger(context,"Port"),
                                        null
                                ))
                                .then(Commands.argument("Password", StringArgumentType.string())
                                        .executes(context -> connectToAPServer(
                                                context,
                                                StringArgumentType.getString(context,"Address"),
                                                IntegerArgumentType.getInteger(context,"Port"),
                                                StringArgumentType.getString(context,"Password")
                                        ))
                                )
                        )
                )
        );

    }

    private static int connectToAPServer(CommandContext<CommandSource> commandContext, String hostname, int port, String password) {
        APMCData data = APRandomizer.getApmcData();
        if(data.state == APMCData.State.VALID) {

            APClient apClient = APRandomizer.getAP();
            apClient.setName(data.player_name);
            apClient.setPassword(password);
            String address = hostname.concat(":" + port);
            Utils.sendMessageToAll("Connecting to Archipelago server at " + address);
            apClient.connect(address);
        }
        else if(data.state == APMCData.State.MISSING)
            Utils.sendMessageToAll("no .apmc file found. please stop the server,  place .apmc file in './APData/', delete the world folder, then relaunch the server.");
        else if(data.state == APMCData.State.INVALID_VERSION)
            Utils.sendMessageToAll("APMC data file wrong version.");
        else if(data.state == APMCData.State.INVALID_SEED)
            Utils.sendMessageToAll("Current Minecraft world has been used for a previous game. please stop server, delete the world and relaunch the server.");

        return 1;
    }

    //wait for register commands event then register ourself as a command.
    @SubscribeEvent
    static void onRegisterCommandsEvent(RegisterCommandsEvent event) {
        ConnectCommand.Register(event.getDispatcher());
    }
}
