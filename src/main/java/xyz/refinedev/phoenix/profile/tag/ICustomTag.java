package xyz.refinedev.phoenix.profile.tag;

import java.util.UUID;

public interface ICustomTag extends ITag {

    boolean isAccepted();

    boolean isDenied();

    UUID getCreator();

    long getCreatedOn();

    CustomTagStatus getStatus();

    void setStatus(CustomTagStatus customTagStatus);
}
