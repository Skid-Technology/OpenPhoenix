package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.profile.grant.IGrant;
import xyz.refinedev.phoenix.repository.ISecurityRepository;

import java.util.List;
import java.util.UUID;

public interface ISecurityHandler {

    boolean hasSecurity(UUID uUID, boolean matches);

    boolean hasSecurity(UUID uUID);

    void addSecurity(UUID uUID, UUID uUID2, boolean matches);

    void removeSecurity(UUID uUID);

    void recordTrip(String text);

    @NotNull
    List<UUID> getRankIdsToTrigger();

    @Nullable
    IGrant getGrantInTrigger(UUID uUID);

    ISecurityRepository getRepository();
}
