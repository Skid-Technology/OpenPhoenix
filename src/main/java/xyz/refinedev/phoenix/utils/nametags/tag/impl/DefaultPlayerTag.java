package xyz.refinedev.phoenix.utils.nametags.tag.impl;

import lombok.Generated;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.NameTagVisibility;

import xyz.refinedev.phoenix.utils.nametags.tag.PlayerTag;

public class DefaultPlayerTag implements PlayerTag {

    private String prefix = "";

    private String suffix = "";

    private int priority = 0;

    private ChatColor color = null;

    private NameTagVisibility visibility = NameTagVisibility.ALWAYS;

    public DefaultPlayerTag() {}

    public DefaultPlayerTag(String prefix) {
        this.prefix = prefix;
    }

    public DefaultPlayerTag(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public DefaultPlayerTag(String prefix, String suffix, int priority) {
        this.prefix = prefix;
        this.suffix = suffix;
        this.priority = priority;
    }

    public DefaultPlayerTag(
            String prefix, String suffix, int priority, NameTagVisibility nameTagVisibility) {
        this.prefix = prefix;
        this.suffix = suffix;
        this.priority = priority;
        this.visibility = nameTagVisibility;
    }

    public DefaultPlayerTag(String prefix, ChatColor chatColor) {
        this.prefix = prefix;
        this.color = chatColor;
    }

    public DefaultPlayerTag(String prefix, String suffix, ChatColor chatColor) {
        this.prefix = prefix;
        this.suffix = suffix;
        this.color = chatColor;
    }

    public DefaultPlayerTag(String prefix, String suffix, int priority, ChatColor chatColor) {
        this.prefix = prefix;
        this.suffix = suffix;
        this.color = chatColor;
        this.priority = priority;
    }

    public DefaultPlayerTag(
            String prefix,
            String suffix,
            ChatColor chatColor,
            int priority,
            NameTagVisibility nameTagVisibility) {
        this.prefix = prefix;
        this.suffix = suffix;
        this.priority = priority;
        this.color = chatColor;
        this.visibility = nameTagVisibility;
    }

    @Override
    public String toString() {
        return "DefaultPlayerTag{prefix='"
                + this.prefix
                + '\''
                + ", suffix='"
                + this.suffix
                + '\''
                + ", priority="
                + this.priority
                + '}';
    }

    @Generated
    @Override
    public String prefix() {
        return this.prefix;
    }

    @Generated
    @Override
    public String suffix() {
        return this.suffix;
    }

    @Generated
    @Override
    public int priority() {
        return this.priority;
    }

    @Generated
    @Override
    public ChatColor color() {
        return this.color;
    }

    @Generated
    @Override
    public NameTagVisibility visibility() {
        return this.visibility;
    }

    @Generated
    public DefaultPlayerTag prefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    @Generated
    public DefaultPlayerTag suffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    @Generated
    public DefaultPlayerTag priority(int priority) {
        this.priority = priority;
        return this;
    }

    @Generated
    public DefaultPlayerTag color(ChatColor chatColor) {
        this.color = chatColor;
        return this;
    }

    @Generated
    public DefaultPlayerTag visibility(NameTagVisibility nameTagVisibility) {
        this.visibility = nameTagVisibility;
        return this;
    }
}
