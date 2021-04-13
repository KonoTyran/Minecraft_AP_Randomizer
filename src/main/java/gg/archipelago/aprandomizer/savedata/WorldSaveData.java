package gg.archipelago.aprandomizer.savedata;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.WorldSavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldSaveData extends WorldSavedData {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String DATA_NAME = "aprandomizer_roomdata";
    private final int intType = 4;
    private final int stringType = 9;
    SaveData saveData;
    private CompoundNBT delayLoad;


    public WorldSaveData() {
        super(DATA_NAME);
    }

    public void loadSaveData(SaveData saveData) {
        this.saveData = saveData;
        if (this.delayLoad != null) {
            this.load(this.delayLoad);
        }
    }

    @Override
    public void load(CompoundNBT nbtData) {
        if (this.saveData == null) {
            this.delayLoad = nbtData;
        } else {
                saveData.id = nbtData.getString("roomID");
                saveData.index = nbtData.getInt("index");
                saveData.slotID = nbtData.getInt("slotID");

            }
    }

    @Override
    public CompoundNBT save(CompoundNBT data) {
        if (this.saveData == null) {
            LOGGER.warn("Tried to save data without having data to save...");
            return data;
        }
        data.putString("roomID", saveData.id);
        data.putInt("index", saveData.index);
        data.putInt("slotID", saveData.slotID);
        return data;
    }
}
