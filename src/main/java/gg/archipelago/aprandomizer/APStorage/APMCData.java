package gg.archipelago.aprandomizer.APStorage;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class APMCData {

    @SerializedName("world_seed")
    public long world_seed;
    @SerializedName("structures")
    public HashMap<String,String> structures;

}
