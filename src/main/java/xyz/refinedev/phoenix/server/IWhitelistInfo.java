package xyz.refinedev.phoenix.server;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.whitelist.WhitelistAccess;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IWhitelistInfo {

    @Nullable
    IRank getRank();

    @NotNull
    List<String> getNiceAccessTypes();

    boolean isWhitelisted();

    Set<WhitelistAccess> getAccessTypes();

    UUID getRankId();

    List<String> getUuids();
}
