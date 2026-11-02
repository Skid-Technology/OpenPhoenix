package xyz.refinedev.phoenix.profile.tag;

import com.google.gson.annotations.SerializedName;

import xyz.refinedev.phoenix.profile.TrackedEntity;

public class Tag extends TrackedEntity implements ITag {

    @SerializedName("name")
    private final String name;

    @SerializedName("prefix")
    private String prefix = "";

    @SerializedName("color")
    private String color;

    @SerializedName("priority")
    private int priority;

    @SerializedName("price")
    private int price;

    @SerializedName("purchasable")
    private boolean purchasable;

    @SerializedName("activeFrom")
    private String activeFrom;

    @SerializedName("activeUntil")
    private String activeUntil;

    private static final String text = "&f";

    @Override
    public String getDisplayName() {
        return this.color + this.prefix;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object result) {
        if (this == result) {
            return true;
        } else if (result != null && this.getClass() == result.getClass()) {
            Tag tag = (Tag) result;
            return this.name.equals(tag.name);
        } else {
            return false;
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getPrefix() {
        return this.prefix;
    }

    @Override
    public String getColor() {
        return this.color;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public int getPrice() {
        return this.price;
    }

    @Override
    public boolean isPurchasable() {
        return this.purchasable;
    }

    @Override
    public String getActiveFrom() {
        return this.activeFrom;
    }

    @Override
    public String getActiveUntil() {
        return this.activeUntil;
    }

    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public void setPurchasable(boolean purchasable) {
        this.purchasable = purchasable;
    }

    @Override
    public void setActiveFrom(String activeFrom) {
        this.activeFrom = activeFrom;
    }

    @Override
    public void setActiveUntil(String activeUntil) {
        this.activeUntil = activeUntil;
    }

    public Tag(String name) {
        this.color = text;
        this.priority = 0;
        this.price = 800;
        this.purchasable = true;
        this.name = name;
    }
}
