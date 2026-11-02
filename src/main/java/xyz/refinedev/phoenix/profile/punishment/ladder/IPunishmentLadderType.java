package xyz.refinedev.phoenix.profile.punishment.ladder;

import xyz.refinedev.phoenix.profile.punishment.PunishmentType;

public interface IPunishmentLadderType {

    int getOrder();

    PunishmentType getType();

    long getDuration();

    boolean isIp();

    boolean isShadow();

    long getDecay();

    void setOrder(int value);

    void setType(PunishmentType punishmentType);

    void setDuration(long value);

    void setIp(boolean matches);

    void setShadow(boolean matches);

    void setDecay(long value);
}
