package xyz.refinedev.phoenix.editor.dto;

import com.google.gson.annotations.SerializedName;

import xyz.refinedev.phoenix.alt.AltLinkStatus;

import java.util.UUID;

public class AltSummary {

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

    @SerializedName("status")
    private final AltLinkStatus status;

    @SerializedName("score")
    private final int score;

    @SerializedName("sharedAddresses")
    private final int sharedAddresses;

    @SerializedName("independentSubnets")
    private final int independentSubnets;

    @SerializedName("firstLinked")
    private final long firstLinked;

    @SerializedName("lastSharedLogin")
    private final long lastSharedLogin;

    @SerializedName("banned")
    private final boolean banned;

    @SerializedName("muted")
    private final boolean muted;

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

    public AltLinkStatus getStatus() {
        return this.status;
    }

    public int getScore() {
        return this.score;
    }

    public int getSharedAddresses() {
        return this.sharedAddresses;
    }

    public int getIndependentSubnets() {
        return this.independentSubnets;
    }

    public long getFirstLinked() {
        return this.firstLinked;
    }

    public long getLastSharedLogin() {
        return this.lastSharedLogin;
    }

    public boolean isBanned() {
        return this.banned;
    }

    public boolean isMuted() {
        return this.muted;
    }

    public AltSummary(
            UUID uuid,
            String name,
            String rankName,
            String rankColor,
            boolean online,
            String server,
            AltLinkStatus altLinkStatus,
            int score,
            int sharedAddresses,
            int independentSubnets,
            long firstLinked,
            long lastSharedLogin,
            boolean banned,
            boolean muted) {
        this.uuid = uuid;
        this.name = name;
        this.rankName = rankName;
        this.rankColor = rankColor;
        this.online = online;
        this.server = server;
        this.status = altLinkStatus;
        this.score = score;
        this.sharedAddresses = sharedAddresses;
        this.independentSubnets = independentSubnets;
        this.firstLinked = firstLinked;
        this.lastSharedLogin = lastSharedLogin;
        this.banned = banned;
        this.muted = muted;
    }
}
