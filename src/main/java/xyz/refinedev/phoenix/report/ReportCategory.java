package xyz.refinedev.phoenix.report;

import com.google.gson.annotations.SerializedName;

import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.profile.TrackedEntity;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadder;

import java.util.ArrayList;
import java.util.List;

public class ReportCategory extends TrackedEntity implements IReportCategory {

    @SerializedName("_id")
    private final String id;

    @SerializedName("displayName")
    private String displayName;

    @SerializedName("description")
    private List<String> description = new ArrayList<>();

    @SerializedName("punishmentLadderId")
    private String punishmentLadderId;

    @SerializedName("materialName")
    private String materialName;

    public ReportCategory(
            String id, String displayName, String punishmentLadderId, String materialName) {
        this.id = id;
        this.displayName = displayName;
        this.punishmentLadderId = punishmentLadderId;
        this.materialName = materialName;
    }

    @Override
    public IPunishmentLadder getPunishmentLadder() {
        return PlatformGetter.getInstance()
                .getPunishmentLadderHandler()
                .getByName(this.punishmentLadderId);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(Object result) {
        if (this == result) {
            return true;
        } else if (result != null && this.getClass() == result.getClass()) {
            ReportCategory reportCategory = (ReportCategory) result;
            return this.id.equals(reportCategory.id);
        } else {
            return false;
        }
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public List<String> getDescription() {
        return this.description;
    }

    @Override
    public String getPunishmentLadderId() {
        return this.punishmentLadderId;
    }

    @Override
    public String getMaterialName() {
        return this.materialName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public void setDescription(List<String> description) {
        this.description = description;
    }

    @Override
    public void setPunishmentLadderId(String punishmentLadderId) {
        this.punishmentLadderId = punishmentLadderId;
    }

    @Override
    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }
}
