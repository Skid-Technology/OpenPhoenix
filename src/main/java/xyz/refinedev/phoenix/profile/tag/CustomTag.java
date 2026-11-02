package xyz.refinedev.phoenix.profile.tag;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class CustomTag extends Tag implements ICustomTag {

    @SerializedName("creator")
    private final UUID creator;

    @SerializedName("createdOn")
    private final long createdOn;

    @SerializedName("status")
    private CustomTagStatus status = CustomTagStatus.UNDECIDED;

    public CustomTag(String text, UUID creator) {
        super(text);
        this.creator = creator;
        this.createdOn = System.currentTimeMillis();
        this.setPurchasable(false);
        this.setPrice(0);
    }

    @Override
    public boolean isAccepted() {
        return this.status == CustomTagStatus.ACCEPTED;
    }

    @Override
    public boolean isDenied() {
        return this.status == CustomTagStatus.DENIED;
    }

    @Override
    public UUID getCreator() {
        return this.creator;
    }

    @Override
    public long getCreatedOn() {
        return this.createdOn;
    }

    @Override
    public CustomTagStatus getStatus() {
        return this.status;
    }

    @Override
    public void setStatus(CustomTagStatus customTagStatus) {
        this.status = customTagStatus;
    }
}
