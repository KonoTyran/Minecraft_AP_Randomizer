package gg.archipelago.aprandomizer.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class WorldData {

    private String seedName = "";

    private int dragonState = DRAGON_ASLEEP;

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


    public static class WorldDataStorage implements Capability.IStorage<WorldData> {

        @Nullable
        @Override
        public INBT writeNBT(Capability<WorldData> capability, WorldData instance, Direction side) {
            CompoundNBT nbt = new CompoundNBT();
            nbt.putInt("dragonState", instance.dragonState);
            nbt.putString("seedName", instance.seedName);
            return nbt;
        }

        @Override
        public void readNBT(Capability<WorldData> capability, WorldData instance, Direction side, INBT nbt) {
            if(nbt.getType() == CompoundNBT.TYPE) {
                CompoundNBT read = (CompoundNBT) nbt;
                instance.setSeedName(read.getString("seedName"));
                instance.setDragonState(read.getInt("dragonState"));
            }
        }
    }
}
