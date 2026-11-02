package xyz.refinedev.phoenix.profile.hop;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class ServerHop implements IServerHop {

    @SerializedName("_id")
    private final UUID id = UUID.randomUUID();

    @SerializedName("target")
    private final UUID target;

    @SerializedName("from")
    private final String from;

    @SerializedName("to")
    private final String to;

    @SerializedName("timestamp")
    private final long timestamp = System.currentTimeMillis();

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public UUID getTarget() {
        return this.target;
    }

    @Override
    public String getFrom() {
        return this.from;
    }

    @Override
    public String getTo() {
        return this.to;
    }

    @Override
    public long getTimestamp() {
        return this.timestamp;
    }

    public ServerHop(UUID target, String from, String to) {
        this.target = target;
        this.from = from;
        this.to = to;
    }
}
