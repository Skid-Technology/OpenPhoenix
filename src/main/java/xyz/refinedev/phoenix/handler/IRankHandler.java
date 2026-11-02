package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.repository.IRankRepository;
import xyz.refinedev.phoenix.scope.IScope;

import java.util.List;
import java.util.UUID;

public interface IRankHandler {

    @Nullable
    IRank getRank(String text);

    @Nullable
    IRank getRank(UUID uUID);

    @Nullable
    IRank getDefaultRank();

    @NotNull
    List<IRank> getDefaultRanks();

    @NotNull
    List<IRank> getAvailableRanks();

    void deleteRank(IRank rank);

    void updateRank(IRank rank);

    void updateRank(IRank rank, boolean matches);

    IRank createRank(String text);

    IScope createScope(String text) throws IllegalArgumentException;

    IScope getGlobalScope();

    void requestPermissionsUpdate(IRank rank);

    @NotNull
    List<IRank> getSortedRanks();

    @NotNull
    List<IRank> getSortedRanksReversed();

    void createDefaultRank();

    boolean canGrant(IProfile profile, IRank rank);

    IRankRepository getRepository();
}
