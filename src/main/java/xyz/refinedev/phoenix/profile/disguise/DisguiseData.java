package xyz.refinedev.phoenix.profile.disguise;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DisguiseData implements IDisguiseData {

    @SerializedName("realName")
    private String realName;

    @SerializedName("disguiseName")
    private String disguiseName;

    @SerializedName("rankId")
    private UUID rankId;

    @SerializedName("realSkin")
    private ISkin realSkin;

    @SerializedName("disguiseSkin")
    private ISkin disguiseSkin;

    @SerializedName("disguiseUuid")
    private UUID disguiseUuid;

    @SerializedName("rollbackData")
    private Map<String, String> rollbackData = new HashMap<>();

    @Override
    public boolean isRankDisguised() {
        return this.rankId != null;
    }

    @Override
    public boolean isDisguised() {
        return this.realName != null
                && this.realSkin != null
                && this.rankId != null
                && this.disguiseName != null
                && this.disguiseSkin != null;
    }

    @Override
    public String getRealName() {
        return this.realName;
    }

    @Override
    public String getDisguiseName() {
        return this.disguiseName;
    }

    @Override
    public UUID getRankId() {
        return this.rankId;
    }

    @Override
    public ISkin getRealSkin() {
        return this.realSkin;
    }

    @Override
    public ISkin getDisguiseSkin() {
        return this.disguiseSkin;
    }

    @Override
    public UUID getDisguiseUuid() {
        return this.disguiseUuid;
    }

    @Override
    public Map<String, String> getRollbackData() {
        return this.rollbackData;
    }

    @Override
    public void setRealName(String realName) {
        this.realName = realName;
    }

    @Override
    public void setDisguiseName(String disguiseName) {
        this.disguiseName = disguiseName;
    }

    @Override
    public void setRankId(UUID rankId) {
        this.rankId = rankId;
    }

    @Override
    public void setRealSkin(ISkin skin) {
        this.realSkin = skin;
    }

    @Override
    public void setDisguiseSkin(ISkin skin) {
        this.disguiseSkin = skin;
    }

    @Override
    public void setDisguiseUuid(UUID disguiseUuid) {
        this.disguiseUuid = disguiseUuid;
    }

    @Override
    public void setRollbackData(Map<String, String> entries) {
        this.rollbackData = entries;
    }
}
