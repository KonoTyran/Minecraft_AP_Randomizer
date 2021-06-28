package gg.archipelago.aprandomizer.mixin;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Raid.class)
public abstract class MixinRaid {

    @Shadow
    private ServerWorld level;

    @Shadow public abstract BlockPos getCenter();

    @Inject(method = "findRandomSpawnPos(II)Lnet/minecraft/util/math/BlockPos;", at = @At(value = "HEAD"), cancellable = true)
    protected void onFindRandomSpawnPos(int p_221298_1_, int p_221298_2_, CallbackInfoReturnable<BlockPos> cir) {
        if (this.level.dimension() == World.NETHER) {
            cir.setReturnValue(getCenter());
        }
    }
}
