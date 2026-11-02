package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.utils.FastStreams;
import xyz.refinedev.phoenix.whitelist.WhitelistAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class WhitelistHandler implements IWhitelistHandler {

    private final CommonPlatform platform;

    private final List<String> whitelistedUsers = new ArrayList<>();

    private final List<String> tokenUsers = new ArrayList<>();

    private boolean whitelisted = false;

    private Set<WhitelistAccess> accessTypes;

    private String text;

    public WhitelistHandler(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }

    @Override
    public void initialize() {
        this.accessTypes = GlobalConfig.WHITELIST.ACCESS_TYPES;
        this.whitelisted = GlobalConfig.WHITELIST.ENABLED;
        this.whitelistedUsers.addAll(GlobalConfig.WHITELIST.PLAYERS);
        this.tokenUsers.addAll(GlobalConfig.WHITELIST.TOKEN_PLAYERS);
        this.text = GlobalConfig.WHITELIST.ACCESS_RANK;
    }

    @Override
    public void setEnabled(boolean whitelisted) {
        this.whitelisted = whitelisted;
        GlobalConfig.WHITELIST.ENABLED = whitelisted;
        GlobalConfig.INSTANCE.save();
    }

    @Override
    public void updateRank(IRank rank) {
        this.text = rank.getName();
        GlobalConfig.WHITELIST.ACCESS_RANK = rank.getName();
        GlobalConfig.INSTANCE.save();
    }

    @Override
    public boolean isWhitelisted(String text, boolean matches) {
        return !matches
                ? this.whitelistedUsers.contains(text.toLowerCase())
                : this.whitelistedUsers.contains(text.toLowerCase())
                        && GlobalConfig.WHITELIST.PLAYERS.contains(text.toLowerCase());
    }

    @Override
    public void whitelistPlayer(String text, boolean matches) {
        this.whitelistedUsers.add(text.toLowerCase());
        if (matches) {
            GlobalConfig.WHITELIST.PLAYERS.add(text.toLowerCase());
            GlobalConfig.INSTANCE.save();
        }
    }

    @Override
    public void unwhitelistPlayer(String text) {
        this.whitelistedUsers.remove(text.toLowerCase());
        GlobalConfig.WHITELIST.PLAYERS.remove(text.toLowerCase());
        GlobalConfig.INSTANCE.save();
    }

    @Override
    public void addAccessType(WhitelistAccess whitelistAccess) {
        this.accessTypes.add(whitelistAccess);
        GlobalConfig.WHITELIST.ACCESS_TYPES.add(whitelistAccess);
        GlobalConfig.INSTANCE.save();
    }

    @Override
    public void removeAccessType(WhitelistAccess whitelistAccess) {
        this.accessTypes.remove(whitelistAccess);
        GlobalConfig.WHITELIST.ACCESS_TYPES.remove(whitelistAccess);
        GlobalConfig.INSTANCE.save();
    }

    @NotNull
    @Override
    public List<String> getAccessTypesAsStrings() {
        return FastStreams.mapArrayList(this.accessTypes, WhitelistAccess::getDisplayName);
    }

    @Override
    public boolean hasAccessType(WhitelistAccess whitelistAccess) {
        return this.accessTypes.contains(whitelistAccess);
    }

    @Nullable
    @Override
    public IRank getWhitelistRank() {
        IRank rank;
        return (rank = this.platform.getRankHandler().getRank(this.text)) != null
                ? rank
                : this.platform.getRankHandler().getDefaultRank();
    }

    @Nullable
    @Override
    public UUID getWhitelistRankUuid() {
        IRank rank;
        if ((rank = this.platform.getRankHandler().getRank(this.text)) != null) {
            return rank.getUuid();
        }
        IRank rank2;
        return (rank2 = this.platform.getRankHandler().getDefaultRank()) != null
                ? rank2.getUuid()
                : null;
    }

    @Override
    public boolean isNameBased() {
        return GlobalConfig.WHITELIST.NAME_BASED;
    }

    public CommonPlatform getPlatform() {
        return this.platform;
    }

    @Override
    public List<String> getWhitelistedUsers() {
        return this.whitelistedUsers;
    }

    @Override
    public List<String> getTokenUsers() {
        return this.tokenUsers;
    }

    @Override
    public boolean isWhitelisted() {
        return this.whitelisted;
    }

    @Override
    public Set<WhitelistAccess> getAccessTypes() {
        return this.accessTypes;
    }
}
