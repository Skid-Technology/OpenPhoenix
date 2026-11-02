package xyz.refinedev.phoenix.profile.punishment;

import com.google.gson.annotations.SerializedName;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.profile.IProfile;

import java.util.UUID;

public class Proof implements IProof {

    @SerializedName("type")
    private ProofType type;

    @SerializedName("proof")
    private String proof;

    @SerializedName("addedBy")
    private UUID addedBy;

    @NotNull
    @Override
    public String getNiceAddedBy() {
        IProfile profile;
        return (profile = PlatformGetter.getInstance().getProfileHandler().getProfile(this.addedBy))
                        == null
                ? this.addedBy.toString()
                : profile.getNameWithColorNoDisguise();
    }

    @Override
    public ProofType getType() {
        return this.type;
    }

    @Override
    public String getProof() {
        return this.proof;
    }

    @Override
    public UUID getAddedBy() {
        return this.addedBy;
    }

    public Proof(ProofType proofType, String proof, UUID addedBy) {
        this.type = proofType;
        this.proof = proof;
        this.addedBy = addedBy;
    }
}
