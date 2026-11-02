package xyz.refinedev.phoenix.profile.chatsnapshot;

import com.google.gson.annotations.SerializedName;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.utils.DateUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ChatLog implements IChatLog {

    @SerializedName("_id")
    private final String id;

    @SerializedName("timestamp")
    private final long timestamp;

    @SerializedName("sender")
    private final UUID sender;

    @SerializedName("message")
    private final String message;

    private static final String text = " ago";

    private static final long timeTime = 12L;

    @NotNull
    @Override
    public String getNiceSender() {
        IProfile profile;
        return (profile = PlatformGetter.getInstance().getProfileHandler().getProfile(this.sender))
                        == null
                ? this.sender.toString()
                : profile.getNameWithColorNoDisguise();
    }

    @Override
    public String getNiceCreatedOn() {
        return System.currentTimeMillis() - this.timestamp < TimeUnit.HOURS.toMillis(timeTime)
                ? DateUtils.formatTimeMillis(System.currentTimeMillis() - this.timestamp) + text
                : DateUtils.getDate(this.timestamp);
    }

    public static String getRandomSaltedString(int value) {
        return IPunishment.getRandomSaltedString(value);
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
            ChatLog chatLog = (ChatLog) result;
            return this.id.equals(chatLog.id);
        } else {
            return false;
        }
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public long getTimestamp() {
        return this.timestamp;
    }

    @Override
    public UUID getSender() {
        return this.sender;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public ChatLog(UUID sender, String message) {
        this.id = getRandomSaltedString(8);
        this.timestamp = System.currentTimeMillis();
        this.sender = sender;
        this.message = message;
    }
}
