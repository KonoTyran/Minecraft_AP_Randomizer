package gg.archipelago.aprandomizer.capability;

import com.google.common.collect.Lists;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class WorldData {

    private String seedName = "";

    private int dragonState = DRAGON_ASLEEP;

    private boolean jailPlayers = true;

    private Set<Integer> locations = new HashSet<>();

    public static final int DRAGON_KILLED = 30;
    public static final int DRAGON_SPAWNED = 20;
    public static final int DRAGON_WAITING = 15;
    public static final int DRAGON_ASLEEP = 10;

    public void setSeedName(String seedName) {
        this.seedName = seedName;
    }

    public String getSeedName() {
        return seedName;
    }

    public void setDragonState(int dragonState) {
        this.dragonState = dragonState;
    }

    public int getDragonState() {
        return dragonState;
    }

    public boolean getJailPlayers() {
        return jailPlayers;
    }

    public void setJailPlayers(boolean jailPlayers) {
        this.jailPlayers = jailPlayers;
    }

    public void addLocation(int location) {
        this.locations.add(location);
    }
    public void setLocations(int[] locations) {
        this.locations.addAll(Lists.newArrayList(Arrays.stream(locations).iterator()));
    }

    public Set<Integer> getLocations() {
        return locations;
    }


    public static class WorldDataStorage implements Capability.IStorage<WorldData> {

        @Nullable
        @Override
        public INBT writeNBT(Capability<WorldData> capability, WorldData instance, Direction side) {
            CompoundNBT nbt = new CompoundNBT();
            nbt.putInt("dragonState", instance.dragonState);
            nbt.putString("seedName", instance.seedName);
            nbt.putBoolean("jailPlayers", instance.jailPlayers);
            nbt.putIntArray("locations", Lists.newArrayList(instance.locations));
            return nbt;
        }

        @Override
        public void readNBT(Capability<WorldData> capability, WorldData instance, Direction side, INBT nbt) {
            if (nbt.getType() == CompoundNBT.TYPE) {
                CompoundNBT read = (CompoundNBT) nbt;
                instance.setSeedName(read.getString("seedName"));
                instance.setDragonState(read.getInt("dragonState"));
                instance.setJailPlayers(read.getBoolean("jailPlayers"));
                instance.setLocations(read.getIntArray("locations"));
            }
        }
    }
}
