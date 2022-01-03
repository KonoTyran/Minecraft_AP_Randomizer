package gg.archipelago.aprandomizer.capability.data;

public class PlayerData {

    private int index;

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

}