package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;

@Mod.EventBusSubscriber
public class onCommand {

    private static final ArrayList<String> allowedCommands = new ArrayList<>() {{
        add("/connect");
        add("/sync");
        add("/start");
        add("/stop");
        add("/kick");
        add("/ban");
        add("/ban-ip");
        add("/pardon");
        add("/pardon-ip");
        add("/whitelist");
        add("/me");
        add("/say");
    }};

    @SubscribeEvent
    static void onPlayerLoginEvent(CommandEvent event) {
        if (!APRandomizer.isRace()) {
            return;
        }
        CommandSourceStack source = event.getParseResults().getContext().getSource();
        String command = event.getParseResults().getReader().getRead();
        for (String allowedCommand : allowedCommands)
            if (command.startsWith(allowedCommand))
                return;

        event.setCanceled(true);
        source.sendFailure(new TextComponent("Non-essential commands are disabled in race mode."));
    }
}
