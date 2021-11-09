package gg.archipelago.aprandomizer.common;

import net.minecraft.world.damagesource.DamageSource;

public class DeathLinkDamage extends DamageSource {

    public static final DeathLinkDamage DEATH_LINK = (DeathLinkDamage) (new DeathLinkDamage("DeathLink")).bypassArmor();

    DeathLinkDamage(String source) {
        super(source);
    }
}
