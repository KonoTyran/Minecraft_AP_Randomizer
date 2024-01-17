package gg.archipelago.aprandomizer.common;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Set;

public class DeathLinkDamage extends DamageSource {

    /**
     * Minecraft internally checks that the value is less than 3.4028235E37F,
     * which is less than Float.MAX_VALUE.
     * Decided to use a much lower value, in case Mojang decides to change it.
     */
    public static final float KILL_DAMAGE = 3.4028235E30F;

    public static ResourceKey<DamageType> DEATH_LINK = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(APRandomizer.MODID,"indirect_magic"));

    private static class DamageTypeWrapper {
        public Holder.Reference<DamageType> damageType;
        DamageTypeWrapper() {
            this.damageType = APRandomizer.getServer().registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DeathLinkDamage.DEATH_LINK);
            // Don't know if this is explicitly needed considering the damage value, but it can't hurt.
            this.damageType.bindTags(Set.of(
                    DamageTypeTags.BYPASSES_INVULNERABILITY,
                    DamageTypeTags.BYPASSES_COOLDOWN,
                    DamageTypeTags.BYPASSES_RESISTANCE,
                    DamageTypeTags.BYPASSES_ARMOR,
                    DamageTypeTags.BYPASSES_EFFECTS,
                    DamageTypeTags.BYPASSES_ENCHANTMENTS,
                    DamageTypeTags.BYPASSES_SHIELD,
                    DamageTypeTags.AVOIDS_GUARDIAN_THORNS
            ));
        }
    }
    private static final DamageTypeWrapper damageType = new DamageTypeWrapper();

    public DeathLinkDamage() {
        super(damageType.damageType);
    }

    @Override
    public @NotNull Component getLocalizedDeathMessage(LivingEntity pLivingEntity) {
        return Component.literal(pLivingEntity.getDisplayName().getString() +  "'s soul was linked to another's fate.");
    }
}
