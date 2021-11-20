package gg.archipelago.aprandomizer.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber
public class APInfoDump {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    //build our command structure and submit it
    public static void Register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
            Commands.literal("ap") //base slash command is "ap"
                //First sub-command to set/retreive deathlink status
                .then(Commands.literal("deathlink")
                        .executes(APInfoDump::queryDeathLink)
                        .then(Commands.argument("value", BoolArgumentType.bool())
                                .executes(APInfoDump::setDeathLink)
                        )
                )
                //Second sub-command to set/retreive MC35 status
                .then(Commands.literal("mc35")
                        .executes(APInfoDump::queryMC35)
                        .then(Commands.argument("value", BoolArgumentType.bool())
                                .executes(APInfoDump::setMC35)
                        )
                )

        );

    }

    private static int queryDeathLink(CommandContext<CommandSourceStack> source) {
        if(!APRandomizer.isConnected()) {
            source.getSource().sendFailure(new TextComponent("Must be connected to an AP server to use this command"));
            return 0;
        }

        String enabled = (APRandomizer.getAP().getSlotData().deathlink) ? "enabled" : "disabled";
        source.getSource().sendSuccess(new TextComponent("DeathLink is "+ enabled),false);
        return 1;
    }

    private static int setDeathLink(CommandContext<CommandSourceStack> source) {
        if(!APRandomizer.isConnected()) {
            source.getSource().sendFailure(new TextComponent("Must be connected to an AP server to use this command"));
            return 0;
        }

        APRandomizer.getAP().getSlotData().deathlink = BoolArgumentType.getBool(source, "value");
        boolean deathlink = APRandomizer.getAP().getSlotData().deathlink;
        if (deathlink) {
            APRandomizer.getAP().addTag("DeathLink");
        } else {
            APRandomizer.getAP().removeTag("DeathLink");
        }

        String enabled = (APRandomizer.getAP().getSlotData().deathlink) ? "enabled" : "disabled";
        source.getSource().sendSuccess(new TextComponent("DeathLink is "+ enabled),false);
        return 1;
    }

    private static int queryMC35(CommandContext<CommandSourceStack> source) {
        if(!APRandomizer.isConnected()) {
            source.getSource().sendFailure(new TextComponent("Must be connected to an AP server to use this command"));
            return 0;
        }

        String enabled = (APRandomizer.getAP().getSlotData().MC35) ? "enabled" : "disabled";
        source.getSource().sendSuccess(new TextComponent("MC35 is "+ enabled),false);
        return 1;
    }

    private static int setMC35(CommandContext<CommandSourceStack> source) {
        if(!APRandomizer.isConnected()) {
            source.getSource().sendFailure(new TextComponent("Must be connected to an AP server to use this command"));
            return 0;
        }

        APRandomizer.getAP().getSlotData().MC35 = BoolArgumentType.getBool(source, "value");
        boolean mc35 = APRandomizer.getAP().getSlotData().MC35;
        if (mc35) {
            APRandomizer.getAP().addTag("MC35");
        } else {
            APRandomizer.getAP().removeTag("MC35");
        }
        
        String enabled = (APRandomizer.getAP().getSlotData().MC35) ? "enabled" : "disabled";
        source.getSource().sendSuccess(new TextComponent("MC35 is "+ enabled),false);
        return 1;
    }

    //wait for register commands event then register ourself as a command.
    @SubscribeEvent
    static void onRegisterCommandsEvent(RegisterCommandsEvent event) {
        APInfoDump.Register(event.getDispatcher());
    }
}
