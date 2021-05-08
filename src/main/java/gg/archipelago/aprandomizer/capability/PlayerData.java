package gg.archipelago.aprandomizer.capability;

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class PlayerData {

    private int index = 0;

    public PlayerData() {
        this(0);
    }

    public PlayerData(int initialIndex) {
        this.index = initialIndex;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public static class PlayerDataStorage implements Capability.IStorage<PlayerData> {

        @Nullable
        @Override
        public INBT writeNBT(Capability<PlayerData> capability, PlayerData instance, Direction side) {
            return IntNBT.valueOf(instance.getIndex());
        }

        @Override
        public void readNBT(Capability<PlayerData> capability, PlayerData instance, Direction side, INBT nbt) {
            int index = 0;
            if (nbt.getType() == IntNBT.TYPE) {
                index = ((IntNBT)nbt).getAsInt();
            }
            instance.setIndex(index);
        }

    }
}
