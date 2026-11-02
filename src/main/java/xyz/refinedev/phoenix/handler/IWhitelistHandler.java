package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.whitelist.WhitelistAccess;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IWhitelistHandler {

    void initialize();

    void setEnabled(boolean matches);

    void updateRank(IRank rank);

    boolean isWhitelisted(String text, boolean matches);

    void whitelistPlayer(String text, boolean matches);

    void unwhitelistPlayer(String text);

    void addAccessType(WhitelistAccess whitelistAccess);

    void removeAccessType(WhitelistAccess whitelistAccess);

    @NotNull
    List<String> getAccessTypesAsStrings();

    boolean hasAccessType(WhitelistAccess whitelistAccess);

    @Nullable
    IRank getWhitelistRank();

    @Nullable
    UUID getWhitelistRankUuid();

    List<String> getWhitelistedUsers();

    List<String> getTokenUsers();

    boolean isWhitelisted();

    Set<WhitelistAccess> getAccessTypes();

    boolean isNameBased();
}
