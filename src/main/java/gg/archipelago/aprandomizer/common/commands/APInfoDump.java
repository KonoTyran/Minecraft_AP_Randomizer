package gg.archipelago.aprandomizer.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import gg.archipelago.APClient.network.BouncePacket;
import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.advancements.Advancement;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

@Mod.EventBusSubscriber
public class APInfoDump {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    //build our command structure and submit it
    public static void Register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
            Commands.literal("ap") //base slash command is "connect"
                //first make sure its NOT a dedicated server (aka single player or hosted via in game client, OR user has an op level of 1)
                .requires((CommandSource) -> (!CommandSource.getServer().isDedicatedServer() || CommandSource.hasPermission(1)))
                //take the first argument as a string and name it "Address"
                .then(Commands.literal("infodump")
                        .executes(APInfoDump::infoDump)
                )
                .then(Commands.literal("beetrap")
                        .executes(APInfoDump::beeTrap)
                )
                .then(Commands.literal("compasses")
                        .executes(APInfoDump::compasses)
                )
                .then(Commands.literal("bounce")
                        .executes(APInfoDump::bounce)
                )
        );

    }

    private static int beeTrap(CommandContext<CommandSourceStack> source) {
        APRandomizer.getItemManager().giveItemToAll(45100);
        return 1;
    }

    private static int compasses(CommandContext<CommandSourceStack> source) {
        APRandomizer.getItemManager().giveItemToAll(45037);
        APRandomizer.getItemManager().giveItemToAll(45038);
        APRandomizer.getItemManager().giveItemToAll(45039);
        APRandomizer.getItemManager().giveItemToAll(45040);
        APRandomizer.getItemManager().giveItemToAll(45041);
        return 1;
    }

    private static int bounce(CommandContext<CommandSourceStack> source) {
        BouncePacket packet = new BouncePacket();
        packet.tags = new String[]{"MC35"};
        packet.setData(new HashMap<String, Object>(){{
            put("enemy", "minecraft:wither");
            put("source", APRandomizer.getAP().getSlot());
            put("nbt", "{NoAI:1}");
        }});
        APRandomizer.getAP().sendBounce(packet);
        return 1;
    }

    private static int infoDump(CommandContext<CommandSourceStack> source) {
        Collection<Advancement> advancements = source.getSource().getServer().getAdvancements().getAllAdvancements();

        try {
            FileOutputStream fileOut = new FileOutputStream("./APData/advancementdump");


            int i = 42000;
            Iterator<Advancement> itr = advancements.iterator();
            while (itr.hasNext()) {
                Advancement advancement = itr.next();
                if (advancement.getId().getPath().startsWith("recipes/")) {
                    itr.remove();
                }


                //advancement.getDisplay().getTitle().toString();
                fileOut.write(String.format("%d,%s\n", i++, advancement.getDisplay().getTitle().getString()).getBytes());
            }

            fileOut.close();


        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 1;
    }

    //wait for register commands event then register ourself as a command.
    @SubscribeEvent
    static void onRegisterCommandsEvent(RegisterCommandsEvent event) {
        APInfoDump.Register(event.getDispatcher());
    }
}
