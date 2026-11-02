package xyz.refinedev.phoenix.notification;

import java.util.List;
import java.util.UUID;

public interface INotification {

    @Override
    int hashCode();

    @Override
    boolean equals(Object result);

    String getId();

    UUID getCreatedBy();

    long getCreatedOn();

    String getTitle();

    List<String> getDescription();

    void setTitle(String text);

    void setDescription(List<String> values);
}
