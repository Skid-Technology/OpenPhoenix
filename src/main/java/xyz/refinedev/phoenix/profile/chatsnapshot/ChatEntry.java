package xyz.refinedev.phoenix.profile.chatsnapshot;

import com.google.gson.annotations.SerializedName;

import xyz.refinedev.phoenix.utils.DateUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ChatEntry implements IChatEntry {

    @SerializedName("uuid")
    private final UUID uuid;

    @SerializedName("time")
    private final long time = System.currentTimeMillis();

    @SerializedName("message")
    private final String message;

    private static final String text = " ago";

    @Override
    public String getNiceTime() {
        return System.currentTimeMillis() - this.time > TimeUnit.DAYS.toMillis(1L)
                ? DateUtils.getDate(this.time)
                : DateUtils.formatDateDiff(this.time) + text;
    }

    @Override
    public int hashCode() {
        return this.uuid.hashCode();
    }

    @Override
    public boolean equals(Object result) {
        if (this == result) {
            return true;
        } else if (result != null && this.getClass() == result.getClass()) {
            ChatEntry chatEntry = (ChatEntry) result;
            return this.uuid.equals(chatEntry.uuid);
        } else {
            return false;
        }
    }

    @Override
    public UUID getUuid() {
        return this.uuid;
    }

    @Override
    public long getTime() {
        return this.time;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public ChatEntry(UUID uuid, String message) {
        this.uuid = uuid;
        this.message = message;
    }
}
