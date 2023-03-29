package gg.archipelago.aprandomizer.managers.itemmanager;

import java.util.ArrayList;

public class ProgressiveList<E> extends ArrayList<E> {

    int index = 0;

    public E getNext() {
        if (this.size() <= index)
            return this.get(this.size()-1);

        return this.get(index++);
    }

}
