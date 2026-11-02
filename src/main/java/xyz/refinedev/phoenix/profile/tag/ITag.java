package xyz.refinedev.phoenix.profile.tag;

import xyz.refinedev.phoenix.utils.ScheduleWindow;

public interface ITag {

    String getDisplayName();

    @Override
    int hashCode();

    @Override
    boolean equals(Object result);

    String getName();

    String getPrefix();

    String getColor();

    int getPriority();

    int getPrice();

    boolean isPurchasable();

    void setPrefix(String text);

    void setColor(String text);

    void setPriority(int value);

    void setPrice(int value);

    void setPurchasable(boolean matches);

    default String getActiveFrom() {
        return null;
    }

    default String getActiveUntil() {
        return null;
    }

    default void setActiveFrom(String text) {}

    default void setActiveUntil(String text) {}

    default boolean isScheduleActive() {
        return ScheduleWindow.isActive(this.getActiveFrom(), this.getActiveUntil());
    }
}
