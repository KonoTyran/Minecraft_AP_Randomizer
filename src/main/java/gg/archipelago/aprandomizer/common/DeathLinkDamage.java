package gg.archipelago.aprandomizer.common;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class DeathLinkDamage extends DamageSource {

    /**
     * Minecraft internally checks that the value is less than 3.4028235E37F,
     * since they multiply it by 10 when awarding the stat to the player who dealt the damage.
     * Just setting this to some large number in case the code is changed or if some multiplications
     * occur before that guard is reached.
     */
    public static final float KILL_DAMAGE = 3.4028235E20F;

    public static ResourceKey<DamageType> DEATH_LINK = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(APRandomizer.MODID,"death_link"));

    private static final Holder<DamageType> damageType = APRandomizer.getServer().registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DeathLinkDamage.DEATH_LINK);

    public DeathLinkDamage() {
        super(damageType);
    }

    @Override
    public @NotNull Component getLocalizedDeathMessage(LivingEntity pLivingEntity) {
        return Component.literal(pLivingEntity.getDisplayName().getString() +  "'s soul was linked to another's fate");
    }
}
