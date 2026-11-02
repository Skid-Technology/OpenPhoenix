package xyz.refinedev.phoenix.profile.punishment;

public enum PunishmentType {
    BAN("Bans", "Ban", "banned", "unbanned", true),
    BLACKLIST("Blacklists", "Blacklist", "blacklisted", "unblacklisted", true),
    MUTE("Mutes", "Mute", "muted", "unmuted", true),
    KICK("Kicks", "Kick", "kicked", "", false),
    COMPETITIVE(
            "Competitive Bans",
            "Competitive Ban",
            "competitive banned",
            "competitive unbanned",
            true),
    WARN("Warns", "Warn", "warned", "unwarned", true);

    private final String name;

    private final String niceName;

    private final String punished;

    private final String removed;

    private final boolean revokable;

    public String getName() {
        return this.name;
    }

    public String getNiceName() {
        return this.niceName;
    }

    public String getPunished() {
        return this.punished;
    }

    public String getRemoved() {
        return this.removed;
    }

    public boolean isRevokable() {
        return this.revokable;
    }

    PunishmentType(
            String name, String niceName, String punished, String removed, boolean revokable) {
        this.name = name;
        this.niceName = niceName;
        this.punished = punished;
        this.removed = removed;
        this.revokable = revokable;
    }
}
