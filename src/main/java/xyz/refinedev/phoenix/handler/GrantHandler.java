package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.editor.panel.PanelSessions;
import xyz.refinedev.phoenix.packet.impl.profile.GrantMessagePacket;
import xyz.refinedev.phoenix.packet.impl.profile.GrantsUpdatePacket;
import xyz.refinedev.phoenix.packet.impl.profile.SecurityExecutePunishmentPacket;
import xyz.refinedev.phoenix.packet.impl.security.SecurityAlertPacket;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.grant.Grant;
import xyz.refinedev.phoenix.profile.grant.IGrant;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.repository.IGrantRepository;
import xyz.refinedev.phoenix.scope.IScope;
import xyz.refinedev.phoenix.utils.repository.RepositoryFactory;
import xyz.refinedev.phoenix.utils.repository.filters.api.Filters;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class GrantHandler implements IGrantHandler {

    private final IGrantRepository repository;

    private final CommonPlatform platform;

    public GrantHandler(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
        this.repository = new RepositoryFactory(commonPlatform).grants();
    }

    @Override
    public void grant(
            UUID uUID,
            IProfile profile,
            long value,
            boolean matches,
            String text,
            IRank rank,
            List<IScope> values) {
        long now = System.currentTimeMillis();
        if (GlobalConfig.SECURITY.ENABLED
                && this.platform.getSecurityHandler().getRankIdsToTrigger().contains(rank.getUuid())
                && !this.platform.getSecurityHandler().hasSecurity(uUID)) {
            IProfile cachedProfile = this.platform.getProfileHandler().getCachedProfile(uUID);
            this.platform.getSecurityHandler().recordTrip("grant");
            this.platform
                    .getPacketHandler()
                    .send(
                            new SecurityAlertPacket(
                                    cachedProfile == null
                                            ? uUID.toString()
                                            : cachedProfile.getNameWithColorNoDisguise(),
                                    profile.getNameWithColorNoDisguise(),
                                    rank.getDisplayName(),
                                    GlobalConfig.SERVER.NAME));
            if (GlobalConfig.SECURITY.PUNISH_SENDER) {
                this.platform
                        .getPacketHandler()
                        .send(new SecurityExecutePunishmentPacket(uUID, GlobalConfig.SERVER.NAME));
            }
            this.platform
                    .getPacketHandler()
                    .send(
                            new SecurityExecutePunishmentPacket(
                                    profile.getUuid(), GlobalConfig.SERVER.NAME));
        } else {
            Grant grant2;
            (grant2 = new Grant()).setIssuedOn(GlobalConfig.SERVER.NAME);
            grant2.setTarget(profile.getUniqueId());
            grant2.setRankId(rank.getUuid());
            grant2.setActive(true);
            grant2.setIssuedAt(System.currentTimeMillis());
            grant2.setIssuedBy(uUID);
            grant2.setDuration(value);
            grant2.setPermanent(matches);
            grant2.setReason(text);
            grant2.setScopes(values);
            IGrant grant3;
            if ((grant3 = profile.getGrantAllScopes(rank)) != null) {
                this.platform.getGrantHandler().getRepository().delete(grant3.getUuid());
                if (!grant2.isPermanent() && !grant3.isPermanent()) {
                    grant2.setDuration(grant3.getRemainingDuration() + grant2.getDuration());
                }
            }
            CompletableFuture<Boolean> completableFuture =
                    this.platform.getGrantHandler().getRepository().save(grant2.getUuid(), grant2);
            Grant grant4 = grant2;
            completableFuture.whenCompleteAsync(
                    (result, result2) -> {
                        this.platform
                                .getPacketHandler()
                                .sendSync(new GrantsUpdatePacket(grant4.getTarget()));
                        this.platform
                                .getPacketHandler()
                                .sendSync(new GrantMessagePacket(grant4, false));
                    });
            if (this.platform.isDebugMode()) {
                this.platform
                        .getLogger()
                        .info(
                                "Granted "
                                        + rank.getName()
                                        + " to "
                                        + profile.getName()
                                        + " in "
                                        + (System.currentTimeMillis() - now)
                                        + "ms.");
            }
        }
    }

    @Override
    public void ungrant(UUID uUID, IProfile profile, IGrant grant, String text) {
        long now = System.currentTimeMillis();
        grant.remove(uUID, text);
        CompletableFuture<Boolean> completableFuture =
                this.platform.getGrantHandler().getRepository().save(grant.getUuid(), grant);
        completableFuture.whenCompleteAsync(
                (result, result2) -> {
                    this.platform
                            .getPacketHandler()
                            .sendSync(new GrantsUpdatePacket(grant.getTarget()));
                    this.platform.getPacketHandler().sendSync(new GrantMessagePacket(grant, true));
                    PanelSessions.applyCommonPlatform(this.platform, grant.getTarget());
                });
        if (this.platform.isDebugMode()) {
            this.platform
                    .getLogger()
                    .info(
                            "Removed grant "
                                    + grant.getUuid()
                                    + " from "
                                    + profile.getName()
                                    + " in "
                                    + (System.currentTimeMillis() - now)
                                    + "ms.");
        }
    }

    @NotNull
    @Override
    public IRank getHighestRank(UUID uUID) {
        IRank defaultRank = this.platform.getRankHandler().getDefaultRank();
        ArrayList<IRank> values = new ArrayList<>();
        Iterator<IRank> iterator2 = this.platform.getGrantHandler().getActiveRanks(uUID).iterator();
        while (iterator2.hasNext()) {
            IRank rank;
            if ((rank = (IRank) iterator2.next()).isVisible()) {
                values.add(rank);
            }
        }
        values.sort(Comparator.comparingInt(IRank::getPriority).reversed());
        return !values.isEmpty() ? (IRank) values.get(0) : defaultRank;
    }

    @NotNull
    @Override
    public IRank getGeneralRank(UUID uUID) {
        IRank defaultRank = this.platform.getRankHandler().getDefaultRank();
        ArrayList values;
        (values = new ArrayList<>(this.platform.getGrantHandler().getActiveRanks(uUID)))
                .sort(Comparator.comparingInt(IRank::getPriority).reversed());
        return !values.isEmpty() ? (IRank) values.get(0) : defaultRank;
    }

    @Override
    public void updateGrants(UUID uUID) {
        this.updateGrants(uUID, false);
    }

    @Override
    public List<IGrant> updateGrants(UUID uUID, boolean matches) {
        long now = System.currentTimeMillis();
        List<IGrant> databaseGrantsSync = this.getDatabaseGrantsSync(uUID);
        this.repository.putInCache(uUID, databaseGrantsSync);
        if (this.repository.isInOnlineCache(uUID) || matches) {
            this.repository.addToOnlineCache(uUID, databaseGrantsSync);
        }
        if (this.platform.isDebugMode()) {
            this.platform
                    .getLogger()
                    .info(
                            "Loaded "
                                    + uUID
                                    + "'s Grants in "
                                    + (System.currentTimeMillis() - now)
                                    + "ms.");
        }
        return databaseGrantsSync;
    }

    @Override
    public CompletableFuture<List<IGrant>> updateGrantsAsync(UUID uUID) {
        long now = System.currentTimeMillis();
        CompletableFuture<List<IGrant>> databaseGrants = this.getDatabaseGrants(uUID);
        return databaseGrants.handleAsync(
                (result, result2) -> {
                    this.repository.putInCache(uUID, (List<IGrant>) result);
                    if (this.repository.isInOnlineCache(uUID)) {
                        this.repository.addToOnlineCache(uUID, (List<IGrant>) result);
                    }
                    if (this.platform.isDebugMode()) {
                        this.platform
                                .getLogger()
                                .info(
                                        "(Async) Loaded "
                                                + uUID
                                                + "'s Grants in "
                                                + (System.currentTimeMillis() - now)
                                                + "ms.");
                    }
                    return (List<IGrant>) result;
                });
    }

    @NotNull
    @Override
    public CompletableFuture<List<IGrant>> getDatabaseGrants(UUID uUID) {
        return this.repository.fetchAll(Filters.eq("target", uUID.toString()));
    }

    @NotNull
    @Override
    public List<IGrant> getDatabaseGrantsSync(UUID uUID) {
        return this.repository.fetchAllSync(Filters.eq("target", uUID.toString()));
    }

    @NotNull
    @Override
    public List<IGrant> getGrants(UUID uUID) {
        List<IGrant> values;
        if ((values = this.repository.getListFromCache(uUID)) != null) {
            return values;
        }
        values = this.getDatabaseGrantsSync(uUID);
        this.repository.putInCache(uUID, values);
        return values;
    }

    @NotNull
    @Override
    public List<IGrant> getActiveGrants(UUID uUID) {
        ArrayList<IGrant> values = new ArrayList<>();
        Iterator<IGrant> iterator2 = this.getGrants(uUID).iterator();
        while (iterator2.hasNext()) {
            IGrant grant;
            if ((grant = (IGrant) iterator2.next()).getRank() != null && grant.isActive()) {
                values.add(grant);
            }
        }
        return values;
    }

    @NotNull
    @Override
    public List<IRank> getActiveRanks(UUID uUID) {
        ArrayList<IRank> values = new ArrayList<>();
        Iterator<IGrant> iterator2 = this.getActiveGrants(uUID).iterator();
        while (iterator2.hasNext()) {
            IRank rank;
            if ((rank = ((IGrant) iterator2.next()).getRank()).isScope()
                    && !rank.isSubscription()) {
                values.add(rank);
            }
        }
        return values;
    }

    @Override
    public CompletableFuture<List<IGrant>> findByIssuedBy(UUID uUID) {
        return this.repository.fetchAll(Filters.eq("issuedBy", uUID.toString()));
    }

    @Override
    public IGrantRepository getRepository() {
        return this.repository;
    }
}
