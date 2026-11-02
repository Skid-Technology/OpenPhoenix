package xyz.refinedev.phoenix.notification;

import com.google.gson.annotations.SerializedName;

import xyz.refinedev.phoenix.profile.TrackedEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Notification extends TrackedEntity implements INotification {

    @SerializedName("_id")
    private final String id;

    @SerializedName("createdBy")
    private final UUID createdBy;

    @SerializedName("createdOn")
    private final long createdOn = System.currentTimeMillis();

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private List<String> description = new ArrayList<>();

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(Object result) {
        if (this == result) {
            return true;
        } else if (result != null && this.getClass() == result.getClass()) {
            Notification notification = (Notification) result;
            return this.id.equals(notification.id);
        } else {
            return false;
        }
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public UUID getCreatedBy() {
        return this.createdBy;
    }

    @Override
    public long getCreatedOn() {
        return this.createdOn;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public List<String> getDescription() {
        return this.description;
    }

    public Notification(String id, UUID createdBy) {
        this.id = id;
        this.createdBy = createdBy;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void setDescription(List<String> description) {
        this.description = description;
    }
}
