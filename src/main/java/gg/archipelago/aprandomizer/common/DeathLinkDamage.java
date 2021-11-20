package gg.archipelago.aprandomizer.common;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class DeathLinkDamage extends DamageSource {

    public static final DeathLinkDamage DEATH_LINK = (DeathLinkDamage) (new DeathLinkDamage("DeathLink")).bypassArmor();

    DeathLinkDamage(String source) {
        super(source);
    }

    @Override
    public Component getLocalizedDeathMessage(LivingEntity pLivingEntity) {
        return new TextComponent(pLivingEntity.getDisplayName().getString() +  "'s soul was linked to another's fate.");
    }
}
