package xyz.refinedev.phoenix.profile.punishment.banwave;

import java.util.List;
import java.util.UUID;

public interface IBanWave {

    UUID getId();

    String getName();

    UUID getCreatedBy();

    long getCreatedAt();

    long getReleaseAt();

    void setReleaseAt(long value);

    boolean isSummarised();

    void setSummarised(boolean matches);

    boolean isReleased();

    void setReleased(boolean matches);

    long getReleasedAt();

    void setReleasedAt(long value);

    UUID getReleasedBy();

    void setReleasedBy(UUID uUID);

    List<IBanWaveEntry> getEntries();

    List<UUID> getIssued();

    boolean isDue();

    boolean holds(UUID uUID);
}
