package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.packet.impl.ranks.PermissionsUpdatePacket;
import xyz.refinedev.phoenix.packet.impl.ranks.RankUpdatePacket;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.rank.Rank;
import xyz.refinedev.phoenix.rank.scopeable.IScopeable;
import xyz.refinedev.phoenix.repository.IRankRepository;
import xyz.refinedev.phoenix.scope.IScope;
import xyz.refinedev.phoenix.scope.Scope;
import xyz.refinedev.phoenix.utils.FastStreams;
import xyz.refinedev.phoenix.utils.repository.RepositoryFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class RankHandler implements IRankHandler {

    private final CommonPlatform platform;

    private final IRankRepository repository;

    public RankHandler(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
        this.repository = new RepositoryFactory(commonPlatform).ranks();
        List<IRank> values = this.repository.fetchAllSync();
        this.repository.getCache().addAll(values);
        if (this.getDefaultRank() == null) {
            this.createDefaultRank();
        }
        commonPlatform.getWhitelistHandler().initialize();
    }

    @Nullable
    @Override
    public IRank getRank(String text) {
        Iterator<IRank> iterator2 = this.repository.getCache().iterator();
        while (iterator2.hasNext()) {
            IRank rank;
            if (((rank = (IRank) iterator2.next()).isScope()
                            || !GlobalConfig.SERVER.ONLY_AVAILABLE_RANKS_ON_SCOPE)
                    && rank.getName().equalsIgnoreCase(text)) {
                return rank;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public IRank getRank(UUID uUID) {
        return this.repository.getFromCache(uUID).orElse(null);
    }

    @Nullable
    @Override
    public IRank getDefaultRank() {
        ArrayList values;
        (values =
                        FastStreams.filterArrayList(
                                this.repository.getCache(),
                                result ->
                                        result.isDefaultRank()
                                                && result.isScope()
                                                && result.isVisible()))
                .sort(Comparator.comparingInt(IRank::getPriority).reversed());
        return values.isEmpty() ? null : (IRank) values.get(0);
    }

    @NotNull
    @Override
    public List<IRank> getDefaultRanks() {
        ArrayList<IRank> values = new ArrayList<>();
        Iterator<IRank> iterator2 = this.repository.getCache().iterator();
        while (iterator2.hasNext()) {
            IRank rank;
            if ((rank = (IRank) iterator2.next()).isDefaultRank() && rank.isScope()) {
                values.add(rank);
            }
        }
        return values;
    }

    @NotNull
    @Override
    public List<IRank> getAvailableRanks() {
        return !GlobalConfig.SERVER.ONLY_AVAILABLE_RANKS_ON_SCOPE
                ? this.repository.getCache()
                : FastStreams.filterArrayList(this.repository.getCache(), IScopeable::isScope);
    }

    @Override
    public void deleteRank(IRank rank) {
        this.repository.removeFromCache(rank);
        this.repository.delete(rank);
        this.platform.getPacketHandler().send(new RankUpdatePacket(rank, true));
        Iterator<IRank> iterator2 = this.repository.getCache().iterator();
        while (iterator2.hasNext()) {
            IRank rank2;
            (rank2 = (IRank) iterator2.next()).removeInheritance(rank);
            this.repository.save(rank2);
            this.platform.getPacketHandler().send(new RankUpdatePacket(rank2, false));
        }
    }

    @Override
    public void updateRank(IRank rank) {
        this.updateRank(rank, false);
    }

    @Override
    public void updateRank(IRank rank, boolean matches) {
        this.repository.save(rank);
        this.platform.getPacketHandler().send(new RankUpdatePacket(rank, false));
    }

    @Override
    public IRank createRank(String text) {
        return new Rank(text);
    }

    @Override
    public IScope createScope(String text) throws IllegalArgumentException {
        return new Scope(text);
    }

    @Override
    public IScope getGlobalScope() {
        return Scope.scopeScope;
    }

    @Override
    public void requestPermissionsUpdate(IRank rank) {
        this.platform.getPacketHandler().send(new PermissionsUpdatePacket());
    }

    @NotNull
    @Override
    public List<IRank> getSortedRanks() {
        ArrayList<IRank> values;
        (values = new ArrayList<>(this.getAvailableRanks()))
                .sort(Comparator.comparingInt(IRank::getPriority));
        return values;
    }

    @NotNull
    @Override
    public List<IRank> getSortedRanksReversed() {
        ArrayList<IRank> values;
        (values = new ArrayList<>(this.getAvailableRanks()))
                .sort(Comparator.comparingInt(IRank::getPriority).reversed());
        return values;
    }

    @Override
    public void createDefaultRank() {
        IRank rank;
        if ((rank = this.getRank("Default")) != null) {
            rank.setDefaultRank(true);
            this.repository.save(rank);
            this.platform.getPacketHandler().send(new RankUpdatePacket(rank, false));
        } else {
            Rank rank2;
            (rank2 = new Rank(UUID.randomUUID())).setName("Default");
            rank2.setDisplayName("&aDefault");
            rank2.setPlayerListPrefix("&a&lDEFAULT &a");
            rank2.setPrefix("&a");
            rank2.setSuffix("");
            rank2.setVisible(true);
            rank2.setDefaultRank(true);
            rank2.setColor("&a");
            rank2.setPriority(1);
            rank2.setScopes(Collections.singletonList(Scope.scopeScope));
            this.repository.save(rank2);
            this.repository.addToCache(rank2);
            this.platform.getPacketHandler().send(new RankUpdatePacket(rank2, false));
        }
    }

    @Override
    public boolean canGrant(IProfile profile, IRank rank) {
        if (!rank.isGrantable() && !profile.hasRank(rank)) {
            return false;
        } else {
            return !profile.hasAccessToPermission("core.grant.*")
                            && !profile.hasAccessToPermission("core.grant." + rank.getName())
                    ? false
                    : profile.getGeneralRank().getPriority() >= rank.getPriority();
        }
    }

    public CommonPlatform getPlatform() {
        return this.platform;
    }

    @Override
    public IRankRepository getRepository() {
        return this.repository;
    }
}
