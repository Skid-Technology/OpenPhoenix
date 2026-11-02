package xyz.refinedev.phoenix.profile.punishment.ladder;

import com.google.gson.annotations.SerializedName;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.profile.TrackedEntity;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class PunishmentLadder extends TrackedEntity implements IPunishmentLadder {

    @SerializedName("_id")
    private final String id;

    @SerializedName("reason")
    private final String reason;

    @SerializedName("requireChatSnapshot")
    private final boolean requireChatSnapshot;

    @SerializedName("priority")
    private int priority = 0;

    @SerializedName("hidden")
    private boolean hidden = false;

    @SerializedName("ladder")
    private List<IPunishmentLadderType> ladder = new ArrayList<>();

    @NotNull
    @Override
    public List<IPunishmentLadderType> getSortedLadders() {
        return this.ladder.stream()
                .sorted(Comparator.comparingInt(IPunishmentLadderType::getOrder))
                .collect(Collectors.toList());
    }

    @Nullable
    @Override
    public IPunishmentLadderType getByOrder(int value) {
        Iterator<xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadderType> iterator2 =
                this.ladder.iterator();
        while (iterator2.hasNext()) {
            IPunishmentLadderType punishmentLadderType;
            if ((punishmentLadderType = (IPunishmentLadderType) iterator2.next()).getOrder()
                    == value) {
                return punishmentLadderType;
            }
        }
        return null;
    }

    @Override
    public int getPunishedTimes(List<IPunishment> values) {
        int value = 0;
        Iterator<IPunishment> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            IPunishment punishment;
            if (!(punishment = (IPunishment) iterator2.next()).isVoided()
                    && punishment.getReason().equalsIgnoreCase(this.reason)) {
                value++;
            }
        }
        return value;
    }

    @Override
    public long getLastPunishDate(List<IPunishment> values) {
        long now = System.currentTimeMillis();
        Iterator<IPunishment> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            IPunishment punishment;
            if (!(punishment = (IPunishment) iterator2.next()).isVoided()
                    && punishment.getReason().equalsIgnoreCase(this.reason)
                    && now >= punishment.getIssuedAt()) {
                now = punishment.getIssuedAt();
            }
        }
        return now;
    }

    @Override
    public IPunishmentLadderType getNextLadder(List<IPunishment> values) {
        if (this.ladder.isEmpty()) {
            return null;
        }
        int value;
        if ((value = this.getPunishedTimes(values)) == 0) {
            return this.getByOrder(1);
        }
        IPunishmentLadderType punishmentLadderType;
        if ((punishmentLadderType =
                        value + 1 > this.ladder.size()
                                ? this.ladder.get(this.ladder.size() - 1)
                                : this.getByOrder(value + 1))
                == null) {
            return this.getByOrder(1);
        }
        if (punishmentLadderType.getDecay() > 0L) {
            long lastPunishDate = this.getLastPunishDate(values);
            if (System.currentTimeMillis() - lastPunishDate > punishmentLadderType.getDecay()) {
                return this.getByOrder(value);
            }
        }
        return punishmentLadderType;
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
            PunishmentLadder punishmentLadder = (PunishmentLadder) result;
            return this.id.equals(punishmentLadder.id);
        } else {
            return false;
        }
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getReason() {
        return this.reason;
    }

    @Override
    public boolean isRequireChatSnapshot() {
        return this.requireChatSnapshot;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public boolean isHidden() {
        return this.hidden;
    }

    @Override
    public List<IPunishmentLadderType> getLadder() {
        return this.ladder;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public void setLadder(List<IPunishmentLadderType> ladder) {
        this.ladder = ladder;
    }

    public PunishmentLadder(String id, String reason, boolean requireChatSnapshot) {
        this.id = id;
        this.reason = reason;
        this.requireChatSnapshot = requireChatSnapshot;
    }
}
