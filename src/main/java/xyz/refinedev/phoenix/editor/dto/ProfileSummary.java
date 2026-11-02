package xyz.refinedev.phoenix.editor.dto;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class ProfileSummary {

    @SerializedName("uuid")
    private final UUID uuid;

    @SerializedName("name")
    private final String name;

    @SerializedName("rankName")
    private final String rankName;

    @SerializedName("rankColor")
    private final String rankColor;

    @SerializedName("online")
    private final boolean online;

    @SerializedName("server")
    private final String server;

    public UUID getUuid() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public String getRankName() {
        return this.rankName;
    }

    public String getRankColor() {
        return this.rankColor;
    }

    public boolean isOnline() {
        return this.online;
    }

    public String getServer() {
        return this.server;
    }

    public ProfileSummary(
            UUID uuid,
            String name,
            String rankName,
            String rankColor,
            boolean online,
            String server) {
        this.uuid = uuid;
        this.name = name;
        this.rankName = rankName;
        this.rankColor = rankColor;
        this.online = online;
        this.server = server;
    }
}
