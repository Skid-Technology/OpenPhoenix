package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.packet.impl.punishments.PunishmentPacket;
import xyz.refinedev.phoenix.packet.impl.punishments.UnPunishPacket;
import xyz.refinedev.phoenix.profile.punishment.IProof;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.Proof;
import xyz.refinedev.phoenix.profile.punishment.ProofType;
import xyz.refinedev.phoenix.profile.punishment.Punishment;
import xyz.refinedev.phoenix.profile.punishment.PunishmentType;
import xyz.refinedev.phoenix.profile.punishment.VoidReason;
import xyz.refinedev.phoenix.repository.IPunishmentRepository;
import xyz.refinedev.phoenix.utils.repository.RepositoryFactory;
import xyz.refinedev.phoenix.utils.repository.filters.api.Filters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PunishmentHandler implements IPunishmentHandler {

    private final CommonPlatform platform;

    private final IPunishmentRepository repository;

    public PunishmentHandler(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
        this.repository = new RepositoryFactory(commonPlatform).punishments();
    }

    @Override
    public void executePunishment(IPunishment punishment) {
        this.platform.getPacketHandler().sendSync(new PunishmentPacket(punishment));
        long value = punishment.isPermanent() ? -1L : punishment.getDuration() / 1000L;
        this.platform
                .getMetricsHandler()
                .metric("phoenix_punishment_issued")
                .findMetric("type", punishment.getPunishmentType().name())
                .findMetric("permanent", String.valueOf(punishment.isPermanent()))
                .findMetric("ip", String.valueOf(punishment.isIp()))
                .findMetric("silent", String.valueOf(punishment.isSilent()))
                .findMetric("count", 1L)
                .findMetric("duration_seconds", value)
                .initializeByMetricsHandler();
    }

    @Override
    public void executeRevokePunishment(IPunishment punishment) {
        this.platform.getPacketHandler().sendSync(new UnPunishPacket(punishment));
        String removedReason = punishment.getRemovedReason();
        this.platform
                .getMetricsHandler()
                .metric("phoenix_punishment_revoked")
                .findMetric("type", punishment.getPunishmentType().name())
                .findMetric(
                        "reason",
                        removedReason != null && !removedReason.isEmpty()
                                ? removedReason
                                : "unspecified")
                .initialize();
        if (punishment.isVoided()) {
            this.applyPunishment(punishment);
        }
    }

    private void applyPunishment(IPunishment punishment) {
        VoidReason voidReason = punishment.getVoidReason();
        this.platform
                .getMetricsHandler()
                .metric("phoenix_punishment_voided")
                .findMetric("type", punishment.getPunishmentType().name())
                .findMetric("reason", voidReason == null ? "UNRECORDED" : voidReason.name())
                .findMetric(
                        "staff_fault",
                        String.valueOf(voidReason != null && voidReason.isStaffFault()))
                .findMetric("issued_by", String.valueOf(punishment.getIssuedBy()))
                .findMetric("count", 1L)
                .initializeByMetricsHandler();
    }

    @NotNull
    @Override
    public CompletableFuture<List<IPunishment>> getAllPunishments() {
        return this.repository.fetchAll();
    }

    @NotNull
    @Override
    public List<IPunishment> getPunishments(UUID uUID) {
        List<IPunishment> values;
        if ((values = this.repository.getListFromCache(uUID)) != null) {
            return values;
        }
        values = this.getDatabasePunishmentsSync(uUID);
        this.repository.putInCache(uUID, values);
        return values;
    }

    @NotNull
    @Override
    public List<IPunishment> getDatabasePunishmentsSync(UUID uUID) {
        return this.repository.fetchAllSync(Filters.eq("target", uUID.toString()));
    }

    @NotNull
    @Override
    public CompletableFuture<List<IPunishment>> getDatabasePunishments(UUID uUID) {
        return this.repository.fetchAll(Filters.eq("target", uUID.toString()));
    }

    @Override
    public void updatePunishments(UUID uUID) {
        this.updatePunishments(uUID, false);
    }

    @Override
    public void updatePunishments(UUID uUID, boolean matches) {
        long now = System.currentTimeMillis();
        List<IPunishment> databasePunishmentsSync = this.getDatabasePunishmentsSync(uUID);
        this.repository.putInCache(uUID, databasePunishmentsSync);
        if (this.repository.isInOnlineCache(uUID) || matches) {
            this.repository.addToOnlineCache(uUID, databasePunishmentsSync);
        }
        if (this.platform.isDebugMode()) {
            this.platform
                    .getLogger()
                    .info(
                            "Loaded "
                                    + uUID
                                    + "'s Punishments in "
                                    + (System.currentTimeMillis() - now)
                                    + "ms.");
        }
    }

    @NotNull
    @Override
    public CompletableFuture<List<IPunishment>> updatePunishmentsAsync(UUID uUID) {
        long now = System.currentTimeMillis();
        CompletableFuture<List<IPunishment>> databasePunishments =
                this.getDatabasePunishments(uUID);
        return databasePunishments.handleAsync(
                (result, result2) -> {
                    this.repository.putInCache(uUID, (List<IPunishment>) result);
                    if (this.repository.isInOnlineCache(uUID)) {
                        this.repository.addToOnlineCache(uUID, (List<IPunishment>) result);
                    }
                    if (this.platform.isDebugMode()) {
                        this.platform
                                .getLogger()
                                .info(
                                        "Loaded "
                                                + uUID
                                                + "'s Punishments in "
                                                + (System.currentTimeMillis() - now)
                                                + "ms.");
                    }
                    return (List<IPunishment>) result;
                });
    }

    @NotNull
    @Override
    public List<IPunishment> getPunishments(UUID uUID, PunishmentType punishmentType) {
        ArrayList<IPunishment> values = new ArrayList<>();
        Iterator<IPunishment> iterator2 = this.getPunishments(uUID).iterator();
        while (iterator2.hasNext()) {
            IPunishment punishment;
            if ((punishment = (IPunishment) iterator2.next())
                    .getPunishmentType()
                    .equals(punishmentType)) {
                values.add(punishment);
            }
        }
        return values;
    }

    @NotNull
    @Override
    public List<IPunishment> getSortedPunishments(UUID uUID, PunishmentType punishmentType) {
        ArrayList<IPunishment> values = new ArrayList<>();
        Iterator<IPunishment> iterator2 = this.getPunishments(uUID).iterator();
        while (iterator2.hasNext()) {
            IPunishment punishment;
            if ((punishment = (IPunishment) iterator2.next())
                    .getPunishmentType()
                    .equals(punishmentType)) {
                values.add(punishment);
            }
        }
        values.sort(IPunishment.COMPARATOR);
        return values;
    }

    @NotNull
    @Override
    public List<IPunishment> getActivePunishments(UUID uUID) {
        ArrayList<IPunishment> values = new ArrayList<>();
        Iterator<IPunishment> iterator2 = this.getPunishments(uUID).iterator();
        while (iterator2.hasNext()) {
            IPunishment punishment;
            if ((punishment = (IPunishment) iterator2.next()).isActive()) {
                values.add(punishment);
            }
        }
        return values;
    }

    @Override
    public boolean hasActivePunishment(UUID uUID, PunishmentType punishmentType) {
        return this.getActivePunishment(uUID, punishmentType) != null;
    }

    @Nullable
    @Override
    public IPunishment getActivePunishment(UUID uUID, PunishmentType punishmentType) {
        Iterator<IPunishment> iterator2 = this.getPunishments(uUID).iterator();
        while (iterator2.hasNext()) {
            IPunishment punishment;
            if ((punishment = (IPunishment) iterator2.next()).getPunishmentType() == punishmentType
                    && !punishment.isntActive()) {
                return punishment;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public IPunishment getActiveIPPunishment(UUID uUID, PunishmentType punishmentType) {
        Iterator<IPunishment> iterator2 = this.getPunishments(uUID).iterator();
        while (iterator2.hasNext()) {
            IPunishment punishment;
            if ((punishment = (IPunishment) iterator2.next()).isIp()
                    && punishment.getPunishmentType() == punishmentType
                    && !punishment.isntActive()) {
                return punishment;
            }
        }
        return null;
    }

    @NotNull
    @Override
    public CompletableFuture<List<IPunishment>> findByIssuedBy(UUID uUID) {
        return this.repository.fetchAll(Filters.eq("issuedBy", uUID.toString()));
    }

    @NotNull
    @Override
    public CompletableFuture<List<IPunishment>> findByRemovedBy(UUID uUID) {
        return this.repository.fetchAll(Filters.eq("removedBy", uUID.toString()));
    }

    @Nullable
    @Override
    public IPunishment findByPunishmentID(String text) {
        return this.repository.fetchSync(Filters.eq("punishmentID", text)).orElse(null);
    }

    @NotNull
    @Override
    public IPunishment createEmptyPunishment() {
        return new Punishment();
    }

    @NotNull
    @Override
    public IProof createProof(ProofType proofType, String text, UUID uUID) {
        return new Proof(proofType, text, uUID);
    }

    public CommonPlatform getPlatform() {
        return this.platform;
    }

    @Override
    public IPunishmentRepository getRepository() {
        return this.repository;
    }
}
