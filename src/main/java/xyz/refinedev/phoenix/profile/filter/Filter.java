package xyz.refinedev.phoenix.profile.filter;

import com.google.gson.annotations.SerializedName;

import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.profile.TrackedEntity;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadder;

public class Filter extends TrackedEntity implements IFilter {

    @SerializedName("_id")
    private final String name;

    @SerializedName("filter")
    private final String filter;

    @SerializedName("filterType")
    private FilterType filterType;

    @SerializedName("hard")
    private boolean hard;

    @SerializedName("punishmentLadderId")
    private String punishmentLadderId;

    @SerializedName("autoPunish")
    private boolean autoPunish = false;

    public Filter(String name, String filter, FilterType filterType2) {
        this.name = name;
        this.filter = filter;
        this.filterType = filterType2;
    }

    @Override
    public boolean hasPunishmentLadder() {
        return this.getPunishmentLadder() != null;
    }

    @Override
    public IPunishmentLadder getPunishmentLadder() {
        return this.punishmentLadderId == null
                ? null
                : PlatformGetter.getInstance()
                        .getPunishmentLadderHandler()
                        .getByName(this.punishmentLadderId);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object result) {
        if (this == result) {
            return true;
        } else if (result != null && this.getClass() == result.getClass()) {
            Filter filter = (Filter) result;
            return this.name.equals(filter.name);
        } else {
            return false;
        }
    }

    @Override
    public void setFilterType(FilterType filterType2) {
        this.filterType = filterType2;
    }

    @Override
    public void setHard(boolean hard) {
        this.hard = hard;
    }

    @Override
    public void setPunishmentLadderId(String punishmentLadderId) {
        this.punishmentLadderId = punishmentLadderId;
    }

    @Override
    public void setAutoPunish(boolean autoPunish) {
        this.autoPunish = autoPunish;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getFilter() {
        return this.filter;
    }

    @Override
    public FilterType getFilterType() {
        return this.filterType;
    }

    @Override
    public boolean isHard() {
        return this.hard;
    }

    @Override
    public String getPunishmentLadderId() {
        return this.punishmentLadderId;
    }

    @Override
    public boolean isAutoPunish() {
        return this.autoPunish;
    }

    public Filter(String name, String filter) {
        this.name = name;
        this.filter = filter;
    }
}
