package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.packet.impl.security.SecurityRemovePacket;
import xyz.refinedev.phoenix.packet.impl.security.SecurityUserUpdatePacket;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.grant.IGrant;
import xyz.refinedev.phoenix.profile.security.ISecurityUser;
import xyz.refinedev.phoenix.profile.security.SecurityUser;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.repository.ISecurityRepository;
import xyz.refinedev.phoenix.utils.repository.RepositoryFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SecurityHandler implements ISecurityHandler {

    private final CommonPlatform platform;

    private final ISecurityRepository repository;

    public SecurityHandler(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
        this.repository = new RepositoryFactory(commonPlatform).security();
        this.repository
                .fetchAll()
                .whenCompleteAsync(
                        (result, result2) -> {
                            for (ISecurityUser securityUser :
                                    (Iterable<ISecurityUser>) ((Iterable) result)) {
                                this.repository.addToCache(securityUser.getUuid(), securityUser);
                            }
                        });
        this.repository.addToCache(
                IProfile.getConsoleUuid(),
                new SecurityUser(IProfile.getConsoleUuid(), IProfile.getConsoleUuid(), true));
    }

    @Override
    public boolean hasSecurity(UUID uUID, boolean matches) {
        Optional<ISecurityUser> fromCache = this.repository.getFromCache(uUID);
        return fromCache.filter(result -> !matches || result.isAdmin()).isPresent();
    }

    @Override
    public boolean hasSecurity(UUID uUID) {
        return this.hasSecurity(uUID, false);
    }

    @Override
    public void addSecurity(UUID uUID, UUID uUID2, boolean matches) {
        SecurityUser securityUser = new SecurityUser(uUID, uUID2, matches);
        this.repository.save(uUID, securityUser);
        this.platform.getPacketHandler().send(new SecurityUserUpdatePacket(securityUser, false));
        this.platform
                .getMetricsHandler()
                .metric("phoenix_security_event")
                .findMetric("action", "add")
                .findMetric("admin", String.valueOf(matches))
                .initialize();
    }

    @Override
    public void removeSecurity(UUID uUID) {
        this.repository.delete(uUID);
        this.platform.getPacketHandler().send(new SecurityRemovePacket(uUID));
        this.platform
                .getMetricsHandler()
                .metric("phoenix_security_event")
                .findMetric("action", "remove")
                .initialize();
    }

    @Override
    public void recordTrip(String text) {
        this.platform
                .getMetricsHandler()
                .metric("phoenix_security_event")
                .findMetric("action", "trigger")
                .findMetric("kind", text)
                .initialize();
    }

    @NotNull
    @Override
    public List<UUID> getRankIdsToTrigger() {
        ArrayList<UUID> values = new ArrayList<>();
        for (String text : GlobalConfig.SECURITY.RANKS_TO_TRIGGER) {
            IRank rank;
            if ((rank = this.platform.getRankHandler().getRank(text)) != null) {
                values.add(rank.getUuid());
            }
        }
        return values;
    }

    @Nullable
    @Override
    public IGrant getGrantInTrigger(UUID uUID) {
        List<UUID> rankIdsToTrigger = this.getRankIdsToTrigger();
        for (IGrant grant : this.platform.getGrantHandler().getActiveGrants(uUID)) {
            if (rankIdsToTrigger.contains(grant.getRankId())) {
                return grant;
            }
        }
        return null;
    }

    public CommonPlatform getPlatform() {
        return this.platform;
    }

    @Override
    public ISecurityRepository getRepository() {
        return this.repository;
    }
}
