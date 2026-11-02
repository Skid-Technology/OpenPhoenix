package xyz.refinedev.phoenix.profile.disguise;

import com.google.gson.annotations.SerializedName;

import xyz.refinedev.phoenix.profile.TrackedEntity;

import java.util.UUID;

public class DisguiseHistory extends TrackedEntity implements IDisguiseHistory {

    @SerializedName("_id")
    private final UUID id = UUID.randomUUID();

    @SerializedName("date")
    private final long date = System.currentTimeMillis();

    @SerializedName("player")
    private final UUID player;

    @SerializedName("rankId")
    private final UUID rankId;

    @SerializedName("name")
    private final String name;

    @SerializedName("skin")
    private final ISkin skin;

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(Object result) {
        if (this == result) {
            return true;
        } else if (result != null && this.getClass() == result.getClass()) {
            DisguiseHistory disguiseHistory = (DisguiseHistory) result;
            return this.id.equals(disguiseHistory.id);
        } else {
            return false;
        }
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public long getDate() {
        return this.date;
    }

    @Override
    public UUID getPlayer() {
        return this.player;
    }

    @Override
    public UUID getRankId() {
        return this.rankId;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ISkin getSkin() {
        return this.skin;
    }

    public DisguiseHistory(UUID player, UUID rankId, String name, ISkin skin2) {
        this.player = player;
        this.rankId = rankId;
        this.name = name;
        this.skin = skin2;
    }
}
