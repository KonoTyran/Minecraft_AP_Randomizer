package gg.archipelago.aprandomizer.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import gg.archipelago.APClient.network.BouncePacket;
import gg.archipelago.aprandomizer.APClient;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.APStorage.APMCData;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntitySummonArgument;
import net.minecraft.command.arguments.NBTCompoundTagArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
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
    public static void Register(CommandDispatcher<CommandSource> dispatcher) {

        dispatcher.register(Commands.literal("bounce") //base slash command is "connect"
                // first make sure its NOT a dedicated server (aka single player or hosted via in game client, OR user has an op level of 1)
                .requires((CommandSource) -> (!CommandSource.getServer().isDedicatedServer() || CommandSource.hasPermission(1)))
                //take the first argument as a string and name it "Address"
                .then(Commands.argument("entity", EntitySummonArgument.id())
                        .suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                        .executes(context ->
                                bounceEntity(context.getSource(),
                                        EntitySummonArgument.getSummonableEntity(context, "entity"),
                                        new CompoundNBT()
                                )
                        )
                        .then(Commands.argument("nbt", NBTCompoundTagArgument.compoundTag())
                                .executes(context ->
                                        bounceEntity(
                                                context.getSource(),
                                                EntitySummonArgument.getSummonableEntity(context,"entity"),
                                                NBTCompoundTagArgument.getCompoundTag(context,"nbt")
                                        )
                                )
                        )
                )


        );

    }

    private static int bounceEntity(CommandSource commandSource, ResourceLocation entity, CompoundNBT nbt) {
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
