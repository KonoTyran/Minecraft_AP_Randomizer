package gg.archipelago.aprandomizer.managers.itemmanager;

import net.minecraft.world.effect.MobEffectInstance;

public class PermanentEffect implements PermanentInterface {
    MobEffectInstance effect;
    String key;

    public PermanentEffect(MobEffectInstance effect, String key) {
        this.effect = effect;
        this.key = key;
    }

    @Override
    public void applyEffect() {
        ItemManager.updateEffect(effect, key);
    }
}
