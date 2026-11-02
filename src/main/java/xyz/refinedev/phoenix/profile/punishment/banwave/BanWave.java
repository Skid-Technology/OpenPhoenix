package xyz.refinedev.phoenix.profile.punishment.banwave;

import com.google.gson.annotations.SerializedName;

import xyz.refinedev.phoenix.profile.TrackedEntity;
import xyz.refinedev.phoenix.utils.Processable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class BanWave extends TrackedEntity implements IBanWave, Processable {

    @SerializedName("_id")
    private final UUID id = UUID.randomUUID();

    @SerializedName("name")
    private final String name;

    @SerializedName("createdBy")
    private final UUID createdBy;

    @SerializedName("createdAt")
    private final long createdAt;

    @SerializedName("releaseAt")
    private long releaseAt;

    @SerializedName("summarised")
    private boolean summarised = true;

    @SerializedName("released")
    private boolean released;

    @SerializedName("releasedAt")
    private long releasedAt;

    @SerializedName("releasedBy")
    private UUID releasedBy;

    @SerializedName("entries")
    private List<IBanWaveEntry> entries = new ArrayList<>();

    @SerializedName("issued")
    private List<UUID> issued = new ArrayList<>();

    @Override
    public List<UUID> getIssued() {
        if (this.issued == null) {
            this.issued = new ArrayList<>();
        }
        return this.issued;
    }

    @Override
    public boolean isDue() {
        return !this.released
                && this.releaseAt > 0L
                && this.releaseAt <= System.currentTimeMillis();
    }

    @Override
    public boolean holds(UUID uUID) {
        Iterator<xyz.refinedev.phoenix.profile.punishment.banwave.IBanWaveEntry> iterator2 =
                this.getEntries().iterator();
        while (iterator2.hasNext()) {
            if (((IBanWaveEntry) iterator2.next()).getTarget().equals(uUID)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void postProcess() {
        if (this.entries == null) {
            this.entries = new ArrayList<>();
        }
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public UUID getCreatedBy() {
        return this.createdBy;
    }

    @Override
    public long getCreatedAt() {
        return this.createdAt;
    }

    @Override
    public long getReleaseAt() {
        return this.releaseAt;
    }

    @Override
    public boolean isSummarised() {
        return this.summarised;
    }

    @Override
    public boolean isReleased() {
        return this.released;
    }

    @Override
    public long getReleasedAt() {
        return this.releasedAt;
    }

    @Override
    public UUID getReleasedBy() {
        return this.releasedBy;
    }

    @Override
    public List<IBanWaveEntry> getEntries() {
        return this.entries;
    }

    @Override
    public void setReleaseAt(long releaseAt) {
        this.releaseAt = releaseAt;
    }

    @Override
    public void setSummarised(boolean summarised) {
        this.summarised = summarised;
    }

    @Override
    public void setReleased(boolean released) {
        this.released = released;
    }

    @Override
    public void setReleasedAt(long releasedAt) {
        this.releasedAt = releasedAt;
    }

    @Override
    public void setReleasedBy(UUID releasedBy) {
        this.releasedBy = releasedBy;
    }

    public void setEntries(List<IBanWaveEntry> entries) {
        this.entries = entries;
    }

    public void setIssued(List<UUID> issued) {
        this.issued = issued;
    }

    public BanWave(String name, UUID createdBy, long createdAt) {
        this.name = name;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }
}
