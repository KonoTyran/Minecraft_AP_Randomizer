package gg.archipelago.aprandomizer.managers.itemmanager.powers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.HashSet;

@Mod.EventBusSubscriber
public class ExcavationPower implements Power {

    private static final HashMap<Player, Direction> faces = new HashMap<>();

    public static int level = 0;

    @SubscribeEvent
    public static void detectBlockFace(PlayerInteractEvent event) {
        faces.put(event.getEntity(), event.getFace());
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        BlockPos pos = event.getPos();
        Player player = event.getPlayer();
        Level world = event.getPlayer().getLevel();
        BlockState baseBlock = player.getLevel().getBlockState(pos);
        if (player.isCrouching())
            return;

        HashSet<BlockPos> positions = new HashSet<>();
        switch (faces.get(player)) {
            case DOWN, UP -> {
                if (level < 1)
                    break;
                if ((player.getYHeadRot() > 45 && player.getYHeadRot() < 135) ||
                        (player.getYHeadRot() > -135 && player.getYHeadRot() < -45)) {
                    positions.add(pos.north());
                    positions.add(pos.south());
                } else {
                    positions.add(pos.east());
                    positions.add(pos.west());
                }
                if (level < 2)
                    break;
                positions.add(pos.north());
                positions.add(pos.south());
                positions.add(pos.east());
                positions.add(pos.west());
                if (level < 3)
                    break;
                positions.add(pos.north().west());
                positions.add(pos.north().east());
                positions.add(pos.south().west());
                positions.add(pos.south().east());
            }
            case EAST, WEST -> {
                if (level < 1)
                    break;
                positions.add(pos.north());
                positions.add(pos.south());
                if (level < 2)
                    break;
                positions.add(pos.above());
                positions.add(pos.below());
                if (level < 3)
                    break;
                positions.add(pos.above().north());
                positions.add(pos.above().south());
                positions.add(pos.below().north());
                positions.add(pos.below().south());
            }
            case NORTH, SOUTH -> {
                if (level < 1)
                    break;
                positions.add(pos.east());
                positions.add(pos.west());
                if (level < 2)
                    break;
                positions.add(pos.above());
                positions.add(pos.below());
                if (level < 3)
                    break;
                positions.add(pos.above().east());
                positions.add(pos.above().west());
                positions.add(pos.below().east());
                positions.add(pos.below().west());
            }
        }

        for (BlockPos excavatePosition : positions) {
            if (player.getMainHandItem().isCorrectToolForDrops(world.getBlockState(excavatePosition)) &&
                    baseBlock.getDestroySpeed(world, pos) >= world.getBlockState(excavatePosition).getDestroySpeed(world, excavatePosition)
            )
                world.destroyBlock(excavatePosition, true);
        }

    }

    @Override
    public void grantPower() {
        level++;
    }
}
