package gg.archipelago.aprandomizer.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.advancements.Advancement;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Mod.EventBusSubscriber
public class APInfoDump {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    //build our command structure and submit it
    public static void Register(CommandDispatcher<CommandSource> dispatcher) {

        dispatcher.register(
                Commands.literal("ap") //base slash command is "connect"
                        //first make sure its NOT a dedicated server (aka single player or hosted via in game client, OR user has an op level of 1)
                        .requires((CommandSource) -> (!CommandSource.getServer().isDedicatedServer() || CommandSource.hasPermission(1)))
                        //take the first argument as a string and name it "Address"
                        .then(Commands.argument("infodump", StringArgumentType.string())
                                .executes(APInfoDump::infoDump)
                        )
        );

    }


    private static int infoDump(CommandContext<CommandSource> source) {
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
