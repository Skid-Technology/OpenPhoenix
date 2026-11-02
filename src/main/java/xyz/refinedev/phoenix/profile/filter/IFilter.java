package xyz.refinedev.phoenix.profile.filter;

import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadder;

public interface IFilter {

    boolean hasPunishmentLadder();

    IPunishmentLadder getPunishmentLadder();

    @Override
    int hashCode();

    @Override
    boolean equals(Object result);

    String getName();

    String getFilter();

    FilterType getFilterType();

    boolean isHard();

    String getPunishmentLadderId();

    boolean isAutoPunish();

    void setFilterType(FilterType filterType);

    void setHard(boolean matches);

    void setPunishmentLadderId(String text);

    void setAutoPunish(boolean matches);
}
