package gg.archipelago.aprandomizer.APStorage;

import com.google.gson.annotations.SerializedName;

public class APMCData {

    @SerializedName("world_seed")
    public long world_seed;
    @SerializedName("seed_name")
    public String seed_name;
    @SerializedName("player_name")
    public String player_name;
    @SerializedName("player_id")
    public int player_id;
    @SerializedName("client_version")
    public int client_version;
    @SerializedName("race")
    public boolean race = false;

    @SerializedName("server")
    public String server;

    @SerializedName("port")
    public int port;

    public State state = State.VALID;

    public enum State {
        VALID, MISSING, INVALID_VERSION, INVALID_SEED
    }

}
