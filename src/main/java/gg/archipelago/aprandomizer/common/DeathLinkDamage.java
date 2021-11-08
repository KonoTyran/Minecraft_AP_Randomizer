package gg.archipelago.aprandomizer.common;

import net.minecraft.world.damagesource.DamageSource;

public class DeathLinkDamage extends DamageSource {

    public static final DamageSource DEATH_LINK = (new DeathLinkDamage("DeathLink")).bypassArmor();

    DeathLinkDamage(String source) {
        super(source);
    }
}
