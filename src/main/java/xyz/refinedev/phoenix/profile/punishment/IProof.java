package xyz.refinedev.phoenix.profile.punishment;

import xyz.refinedev.lib.jetbrains.NotNull;

import java.util.UUID;

public interface IProof {

    @NotNull
    String getNiceAddedBy();

    ProofType getType();

    String getProof();

    UUID getAddedBy();
}
