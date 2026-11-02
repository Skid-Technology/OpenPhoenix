package xyz.refinedev.phoenix.profile.chatsnapshot;

import com.google.gson.annotations.SerializedName;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.utils.DateUtils;

import java.util.List;
import java.util.UUID;

public class ChatSnapshot implements IChatSnapshot {

    @SerializedName("_id")
    private final UUID uuid = UUID.randomUUID();

    @SerializedName("requestedBy")
    private final UUID requestedBy;

    @SerializedName("chat")
    private final List<IChatEntry> chat;

    @SerializedName("wholeChat")
    private final boolean wholeChat;

    @SerializedName("niceId")
    private final String niceId;

    @SerializedName("createdOn")
    private final long createdOn;

    @NotNull
    @Override
    public String getNiceRequestedBy() {
        IProfile profile;
        return (profile =
                                PlatformGetter.getInstance()
                                        .getProfileHandler()
                                        .getProfile(this.requestedBy))
                        == null
                ? this.requestedBy.toString()
                : profile.getNameWithColorNoDisguise();
    }

    @Override
    public String getNiceCreatedOn() {
        return DateUtils.getDate(this.createdOn);
    }

    public static String getRandomSaltedString(int value) {
        return IPunishment.getRandomSaltedString(value);
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
            ChatSnapshot chatSnapshot = (ChatSnapshot) result;
            return this.uuid.equals(chatSnapshot.uuid);
        } else {
            return false;
        }
    }

    @Override
    public UUID getUuid() {
        return this.uuid;
    }

    @Override
    public UUID getRequestedBy() {
        return this.requestedBy;
    }

    @Override
    public List<IChatEntry> getChat() {
        return this.chat;
    }

    @Override
    public boolean isWholeChat() {
        return this.wholeChat;
    }

    @Override
    public String getNiceId() {
        return this.niceId;
    }

    @Override
    public long getCreatedOn() {
        return this.createdOn;
    }

    public ChatSnapshot(UUID requestedBy, List<IChatEntry> chat, boolean wholeChat) {
        this.niceId = getRandomSaltedString(8);
        this.createdOn = System.currentTimeMillis();
        this.requestedBy = requestedBy;
        this.chat = chat;
        this.wholeChat = wholeChat;
    }
}
