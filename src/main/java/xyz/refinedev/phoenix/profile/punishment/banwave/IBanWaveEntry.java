package xyz.refinedev.phoenix.profile.punishment.banwave;

import xyz.refinedev.phoenix.profile.punishment.IProof;
import xyz.refinedev.phoenix.profile.punishment.PunishmentType;

import java.util.List;
import java.util.UUID;

public interface IBanWaveEntry {

    UUID getTarget();

    String getTargetName();

    PunishmentType getType();

    long getDuration();

    String getReason();

    UUID getStagedBy();

    long getStagedAt();

    boolean isIp();

    void setIp(boolean matches);

    boolean isShadow();

    void setShadow(boolean matches);

    List<IProof> getProof();

    boolean isPermanent();
}
