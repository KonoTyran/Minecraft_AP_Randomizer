package gg.archipelago.aprandomizer.capability.providers;

import gg.archipelago.aprandomizer.capability.APCapabilities;
import gg.archipelago.aprandomizer.capability.data.WorldData;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WorldDataProvider implements ICapabilitySerializable<Tag> {


    private final WorldData worldData = new WorldData();

    /**
     * Asks the Provider if it has the given capability
     *
     * @param capability<T> capability to be checked for
     * @param facing        the side of the provider being checked (null = no particular side)
     * @param <T>           The interface instance that is used
     * @return a lazy-initialisation supplier of the interface instance that is used to access this capability
     * In this case, we don't actually use lazy initialisation because the instance is very quick to create.
     * See CapabilityProviderFlowerBag for an example of lazy initialisation
     */
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        if (APCapabilities.WORLD_DATA == capability) {
            return (LazyOptional<T>) LazyOptional.of(() -> worldData);
            // why are we using a lambda?  Because LazyOptional.of() expects a NonNullSupplier interface.  The lambda automatically
            //   conforms itself to that interface.  This save me having to define an inner class implementing NonNullSupplier.
            // The explicit cast to LazyOptional<T> is required because our CAPABILITY_ELEMENTAL_FIRE can't be typed.  Our code has
            //   checked that the requested capability matches, so the explicit cast is safe (unless you have made a mistake and mixed them up!)
        }
        return LazyOptional.empty();
        // Note that if you are implementing getCapability in a derived class which implements ICapabilityProvider
        // eg you have added a new MyEntity which has the method MyEntity::getCapability instead of using AttachCapabilitiesEvent to attach a
        // separate class, then you should call
        // return super.getCapability(capability, facing);
        //   instead of
        // return LazyOptional.empty();
    }

    @Override
    public Tag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("dragonState", worldData.getDragonState());
        nbt.putString("seedName", worldData.getSeedName());
        nbt.putBoolean("jailPlayers", worldData.getJailPlayers());
        nbt.putLong("index", worldData.getIndex());
        return nbt;
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        if (nbt.getType() == CompoundTag.TYPE) {
            CompoundTag read = (CompoundTag) nbt;
            worldData.setSeedName(read.getString("seedName"));
            worldData.setDragonState(read.getInt("dragonState"));
            worldData.setJailPlayers(read.getBoolean("jailPlayers"));
            worldData.setIndex(read.getLong("index"));
        }
    }
}