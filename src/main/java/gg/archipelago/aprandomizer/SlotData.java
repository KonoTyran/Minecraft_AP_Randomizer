package gg.archipelago.aprandomizer;

import archipelagoClient.com.google.gson.annotations.SerializedName;

public class SlotData extends gg.archipelago.APClient.SlotData {

    private int include_hard_advancements;
    private int include_insane_advancements;
    private int include_postgame_advancements;
    private int advancement_goal;
    private long minecraft_world_seed;
    private int client_version;

    @SerializedName("MC35")
    public boolean MC35 = false;

    @SerializedName("deathlink")
    public boolean deathlink = false;

    public int getInclude_hard_advancements() {
        return include_hard_advancements;
    }

    public int getClient_version() {
        return client_version;
    }

    public long getMinecraft_world_seed() {
        return minecraft_world_seed;
    }

    public int getAdvancement_goal() {
        return advancement_goal;
    }

    public int getInclude_postgame_advancements() {
        return include_postgame_advancements;
    }

    public int getInclude_insane_advancements() {
        return include_insane_advancements;
    }
}
