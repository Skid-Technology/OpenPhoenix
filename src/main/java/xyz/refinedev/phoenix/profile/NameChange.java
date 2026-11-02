package xyz.refinedev.phoenix.profile;

import com.google.gson.annotations.SerializedName;

public class NameChange implements INameChange {

    @SerializedName("name")
    private final String name;

    @SerializedName("changedAt")
    private final long changedAt;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public long getChangedAt() {
        return this.changedAt;
    }

    public NameChange(String name, long changedAt) {
        this.name = name;
        this.changedAt = changedAt;
    }
}
