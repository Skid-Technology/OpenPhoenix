package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.grant.IGrant;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.repository.IGrantRepository;
import xyz.refinedev.phoenix.scope.IScope;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IGrantHandler {

    void grant(
            UUID uUID,
            IProfile profile,
            long value,
            boolean matches,
            String text,
            IRank rank,
            List<IScope> values);

    void ungrant(UUID uUID, IProfile profile, IGrant grant, String text);

    @NotNull
    IRank getHighestRank(UUID uUID);

    @NotNull
    IRank getGeneralRank(UUID uUID);

    void updateGrants(UUID uUID);

    List<IGrant> updateGrants(UUID uUID, boolean matches);

    CompletableFuture<List<IGrant>> updateGrantsAsync(UUID uUID);

    @NotNull
    CompletableFuture<List<IGrant>> getDatabaseGrants(UUID uUID);

    @NotNull
    List<IGrant> getDatabaseGrantsSync(UUID uUID);

    @NotNull
    List<IGrant> getGrants(UUID uUID);

    @NotNull
    List<IGrant> getActiveGrants(UUID uUID);

    @NotNull
    List<IRank> getActiveRanks(UUID uUID);

    CompletableFuture<List<IGrant>> findByIssuedBy(UUID uUID);

    IGrantRepository getRepository();
}
