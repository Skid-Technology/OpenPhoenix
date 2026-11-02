package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.profile.punishment.IProof;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.ProofType;
import xyz.refinedev.phoenix.profile.punishment.PunishmentType;
import xyz.refinedev.phoenix.repository.IPunishmentRepository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IPunishmentHandler {

    void executePunishment(IPunishment punishment);

    void executeRevokePunishment(IPunishment punishment);

    @NotNull
    CompletableFuture<List<IPunishment>> getAllPunishments();

    @NotNull
    List<IPunishment> getPunishments(UUID uUID);

    @NotNull
    List<IPunishment> getDatabasePunishmentsSync(UUID uUID);

    @NotNull
    CompletableFuture<List<IPunishment>> getDatabasePunishments(UUID uUID);

    void updatePunishments(UUID uUID);

    void updatePunishments(UUID uUID, boolean matches);

    @NotNull
    CompletableFuture<List<IPunishment>> updatePunishmentsAsync(UUID uUID);

    @NotNull
    List<IPunishment> getPunishments(UUID uUID, PunishmentType punishmentType);

    @NotNull
    List<IPunishment> getSortedPunishments(UUID uUID, PunishmentType punishmentType);

    @NotNull
    List<IPunishment> getActivePunishments(UUID uUID);

    boolean hasActivePunishment(UUID uUID, PunishmentType punishmentType);

    @Nullable
    IPunishment getActivePunishment(UUID uUID, PunishmentType punishmentType);

    @Nullable
    IPunishment getActiveIPPunishment(UUID uUID, PunishmentType punishmentType);

    @NotNull
    CompletableFuture<List<IPunishment>> findByIssuedBy(UUID uUID);

    @NotNull
    CompletableFuture<List<IPunishment>> findByRemovedBy(UUID uUID);

    @Nullable
    IPunishment findByPunishmentID(String text);

    @NotNull
    IPunishment createEmptyPunishment();

    @NotNull
    IProof createProof(ProofType proofType, String text, UUID uUID);

    IPunishmentRepository getRepository();
}
