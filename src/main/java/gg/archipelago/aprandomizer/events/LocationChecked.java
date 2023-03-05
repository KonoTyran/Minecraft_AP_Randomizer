package gg.archipelago.aprandomizer.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.client.events.ArchipelagoEventListener;
import gg.archipelago.client.events.CheckedLocationsEvent;

public class LocationChecked {

    @ArchipelagoEventListener
    public static void onLocationChecked(CheckedLocationsEvent event) {
        event.checkedLocations.forEach(location -> APRandomizer.getAdvancementManager().addAdvancement(location));

    }
}
