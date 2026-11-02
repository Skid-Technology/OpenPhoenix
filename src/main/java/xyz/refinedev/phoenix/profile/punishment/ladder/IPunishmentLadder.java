package xyz.refinedev.phoenix.profile.punishment.ladder;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;

import java.util.List;

public interface IPunishmentLadder {

    @NotNull
    List<IPunishmentLadderType> getSortedLadders();

    @Nullable
    IPunishmentLadderType getByOrder(int value);

    int getPunishedTimes(List<IPunishment> values);

    long getLastPunishDate(List<IPunishment> values);

    IPunishmentLadderType getNextLadder(List<IPunishment> values);

    @Override
    int hashCode();

    @Override
    boolean equals(Object result);

    String getId();

    String getReason();

    boolean isRequireChatSnapshot();

    int getPriority();

    boolean isHidden();

    List<IPunishmentLadderType> getLadder();

    void setPriority(int value);

    void setHidden(boolean matches);

    void setLadder(List<IPunishmentLadderType> values);
}
