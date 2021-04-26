package gg.archipelago.aprandomizer.APStorage;

import java.util.ArrayList;

public interface APStorageHandler {

    ArrayList<Integer> getReceivedItems();

    void addItem(int itemID);

}
