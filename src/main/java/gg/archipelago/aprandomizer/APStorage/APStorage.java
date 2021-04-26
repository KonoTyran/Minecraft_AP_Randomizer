package gg.archipelago.aprandomizer.APStorage;


import java.util.ArrayList;

public class APStorage implements APStorageHandler {

    ArrayList<Integer> receivedItems = new ArrayList<>();

    @Override
    public ArrayList<Integer> getReceivedItems() {
        return receivedItems;
    }

    @Override
    public void addItem(int itemID) {
        receivedItems.add(itemID);
    }
}
