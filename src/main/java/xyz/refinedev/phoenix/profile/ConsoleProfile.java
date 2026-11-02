package xyz.refinedev.phoenix.profile;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.profile.cooldown.ICooldown;
import xyz.refinedev.phoenix.profile.login.ILogin;
import xyz.refinedev.phoenix.profile.notes.INote;
import xyz.refinedev.phoenix.profile.permission.IProfilePermission;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.tag.ITag;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ConsoleProfile extends RedisProfile {

    public ConsoleProfile() {
        super(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        this.setName(GlobalConfig.SERVER.CONSOLE_NAME);
    }

    @Override
    public void saveProfile() {}

    @Override
    public void saveAndUpdate(boolean matches) {}

    @Override
    public void saveAndUpdate() {}

    @Override
    public ITag getTag() {
        return null;
    }

    @Override
    public void setTag(ITag tag) {}

    @Override
    public boolean shouldSendEvadeAlert() {
        return false;
    }

    @Override
    public void updatePermissions() {}

    public void applyList(List<UUID> values) {}

    @Override
    public boolean isVanished() {
        return false;
    }

    @NotNull
    @Override
    public String getFirstLogin(List<ILogin> values) {
        return "N/A";
    }

    @NotNull
    @Override
    public String getLastLogin(List<ILogin> values) {
        return "N/A";
    }

    @Override
    public ILogin getMostRecentLogin(List<ILogin> values) {
        return null;
    }

    @NotNull
    @Override
    public String getLastSeenAgo(List<ILogin> values) {
        return "N/A";
    }

    @Override
    public long getPlayTime(List<ILogin> values) {
        return 0L;
    }

    @Override
    public void loadAlts() {}

    @Override
    public void loadAlts(boolean matches) {}

    @Override
    public boolean isIgnoring(UUID uUID) {
        return false;
    }

    @NotNull
    @Override
    public List<String> getNiceIgnoreNames() {
        return Collections.emptyList();
    }

    @NotNull
    @Override
    public String getChatChannelName() {
        return "Default";
    }

    @Override
    public String getSetting(String text) {
        return null;
    }

    @Override
    public boolean hasMetadata(String text) {
        return false;
    }

    @Deprecated
    @NotNull
    @Override
    public String getMetadata(String text, String text2) {
        return text2;
    }

    @Override
    public String getSetting(String text, String text2) {
        return text2;
    }

    @Override
    public int getIntSetting(String text) {
        return 0;
    }

    @Override
    public int getIntSetting(String text, int value) {
        return value;
    }

    @Override
    public double getDoubleSetting(String text) {
        return 0.0;
    }

    @Override
    public double getDoubleSetting(String text, double value) {
        return value;
    }

    @Override
    public boolean isSetting(String text) {
        return true;
    }

    @Override
    public boolean isMetadata(String text) {
        return true;
    }

    @Override
    public boolean isMetadata(String text, boolean matches) {
        return matches;
    }

    @Override
    public void updateMetadata(String text, Object result, boolean matches) {}

    @Override
    public boolean hasAccessToPermission(String text) {
        return true;
    }

    @Override
    public boolean hasPrivatePermission(String text) {
        return false;
    }

    @Override
    public void removePermission(String text) {}

    @Override
    public IProfilePermission getPermission(String text) {
        return null;
    }

    @NotNull
    @Override
    public List<IPunishment> getAltPunishments() {
        return Collections.emptyList();
    }

    @Override
    public long getGlobalRemaining(String text) {
        return 0L;
    }

    @Override
    public ICooldown getGlobalCooldown(String text) {
        return null;
    }

    @Override
    public boolean hasGlobalCooldown(String text) {
        return false;
    }

    @Override
    public long getLocalRemaining(String text) {
        return 0L;
    }

    @Override
    public boolean hasLocalCooldown(String text) {
        return false;
    }

    @Override
    public INote getNote(UUID uUID) {
        return null;
    }

    @Override
    public void postProcess() {}

    @NotNull
    @Override
    public String getNameWithColorCoverDisguise(String text) {
        return GlobalConfig.SERVER.CONSOLE_NAME;
    }

    @NotNull
    @Override
    public String getNameWithColor() {
        return GlobalConfig.SERVER.CONSOLE_NAME;
    }

    @NotNull
    @Override
    public String getNameWithColorNoDisguise() {
        return GlobalConfig.SERVER.CONSOLE_NAME;
    }

    @NotNull
    @Override
    public String getPlayerName() {
        return GlobalConfig.SERVER.CONSOLE_NAME;
    }
}
