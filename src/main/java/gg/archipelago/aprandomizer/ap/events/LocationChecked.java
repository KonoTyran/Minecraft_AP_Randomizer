package gg.archipelago.aprandomizer.ap.events;

import gg.archipelago.aprandomizer.APRandomizer;
import dev.koifysh.archipelago.events.ArchipelagoEventListener;
import dev.koifysh.archipelago.events.CheckedLocationsEvent;

public class LocationChecked {

    @ArchipelagoEventListener
    public static void onLocationChecked(CheckedLocationsEvent event) {
        event.checkedLocations.forEach(location -> APRandomizer.getAdvancementManager().addAdvancement(location));

    }
}
