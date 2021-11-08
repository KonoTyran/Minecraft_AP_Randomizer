package gg.archipelago.aprandomizer.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import gg.archipelago.APClient.network.BouncePacket;
import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.EntitySummonArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

@Mod.EventBusSubscriber
public class BounceCommand {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    //build our command structure and submit it
    public static void Register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(Commands.literal("bounce") //base slash command is "connect"
                // first make sure its NOT a dedicated server (aka single player or hosted via in game client, OR user has an op level of 1)
                .requires((CommandSource) -> (!CommandSource.getServer().isDedicatedServer() || CommandSource.hasPermission(1)))
                //take the first argument as a string and name it "Address"
                .then(Commands.argument("entity", EntitySummonArgument.id())
                        .suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                        .executes(context ->
                                bounceEntity(context.getSource(),
                                        EntitySummonArgument.getSummonableEntity(context, "entity"),
                                        new CompoundTag()
                                )
                        )
                        .then(Commands.argument("nbt", CompoundTagArgument.compoundTag())
                                .executes(context ->
                                        bounceEntity(
                                                context.getSource(),
                                                EntitySummonArgument.getSummonableEntity(context,"entity"),
                                                CompoundTagArgument.getCompoundTag(context,"nbt")
                                        )
                                )
                        )
                )


        );

    }

    private static int bounceEntity(CommandSourceStack commandSource, ResourceLocation entity, CompoundTag nbt) {
        BouncePacket packet = new BouncePacket();
        packet.tags = new String[]{"MC35"};
        packet.setData(new HashMap<String, Object>(){{
            put("enemy", entity.toString());
            put("source", APRandomizer.getAP().getSlot());
            put("nbt", nbt.toString());
        }});
        APRandomizer.getAP().sendBounce(packet);
        return 1;
    }

    //wait for register commands event then register ourself as a command.
    @SubscribeEvent
    static void onRegisterCommandsEvent(RegisterCommandsEvent event) {
        BounceCommand.Register(event.getDispatcher());
    }
}
