package gg.archipelago.aprandomizer.managers.itemmanager.traps;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.LinkedList;
import java.util.List;

public class GoonTrap implements Trap {

    private final int numberOfGoons;
    List<Zombie> zombies = new LinkedList<>();
    int timer = 20 * 30;

    public GoonTrap() {
        this(3);
    }

    public GoonTrap(int numberOfGoons) {
        MinecraftForge.EVENT_BUS.register(this);
        this.numberOfGoons = numberOfGoons;
    }
    @Override
    public void trigger(ServerPlayer player) {
        ItemStack fish = new ItemStack(Items.SALMON);
        fish.enchant(Enchantments.KNOCKBACK,3);

        APRandomizer.getServer().execute(() -> {
            ServerLevel world = player.getLevel();
            Vec3 pos = player.position();
            for (int i = 0; i < numberOfGoons; i++) {
                Zombie goon = EntityType.ZOMBIE.create(world);
                if(goon == null)
                    continue;
                goon.setItemInHand(InteractionHand.MAIN_HAND,fish.copy());
                goon.setTarget(player);
                Vec3 offset = Utils.getRandomPosition(pos, 5);
                goon.moveTo(offset);
                zombies.add(goon);
                world.addFreshEntity(goon);
            }
        });
    }

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
        if (--timer > 0)
            return;

        for (Zombie zombie : zombies) {
            zombie.kill();
        }
        MinecraftForge.EVENT_BUS.unregister(this);
    }
}
