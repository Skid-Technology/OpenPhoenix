package xyz.refinedev.phoenix.profile.punishment;

import com.google.gson.annotations.SerializedName;

import xyz.refinedev.phoenix.profile.log.Logable;
import xyz.refinedev.phoenix.utils.Processable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Punishment extends Logable implements IPunishment, Processable {

    @SerializedName("punishmentType")
    private PunishmentType punishmentType;

    @SerializedName("silent")
    private boolean silent;

    @SerializedName("removedSilent")
    private boolean removedSilent;

    @SerializedName("ip")
    private boolean ip = false;

    @SerializedName("voided")
    private boolean voided = false;

    @SerializedName("shadow")
    private boolean shadow = false;

    @SerializedName("punishmentID")
    private String punishmentID;

    @SerializedName("address")
    private String address;

    @SerializedName("proof")
    private List<IProof> proof;

    public Punishment() {
        this.punishmentID = formatString(6).toUpperCase();
        this.address = "";
        this.proof = new ArrayList<>();
    }

    public static String formatString(int value) {
        StringBuilder stringBuilder = new StringBuilder();
        while (stringBuilder.length() < value) {
            float value2 = new Random().nextFloat();
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".length();
            int value3 = (int) (value2 * 36.0F);
            stringBuilder.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".charAt(value3));
        }
        return stringBuilder.toString();
    }

    @Override
    public boolean isActive() {
        return this.voided ? false : super.isActive();
    }

    @Override
    public boolean isntActive() {
        return !this.isActive();
    }

    @Override
    public boolean isBan() {
        return this.punishmentType == PunishmentType.BAN;
    }

    @Override
    public boolean isIPBan() {
        return this.punishmentType == PunishmentType.BAN && this.ip;
    }

    @Override
    public boolean isBlacklist() {
        return this.punishmentType == PunishmentType.BLACKLIST;
    }

    @Override
    public boolean isMute() {
        return this.punishmentType == PunishmentType.MUTE;
    }

    @Override
    public void postProcess() {
        if (this.proof == null) {
            this.proof = new ArrayList<>();
        }
    }

    @Override
    public PunishmentType getPunishmentType() {
        return this.punishmentType;
    }

    @Override
    public boolean isSilent() {
        return this.silent;
    }

    @Override
    public boolean isRemovedSilent() {
        return this.removedSilent;
    }

    @Override
    public boolean isIp() {
        return this.ip;
    }

    @Override
    public boolean isVoided() {
        return this.voided;
    }

    @Override
    public boolean isShadow() {
        return this.shadow;
    }

    @Override
    public String getPunishmentID() {
        return this.punishmentID;
    }

    @Override
    public String getAddress() {
        return this.address;
    }

    @Override
    public List<IProof> getProof() {
        return this.proof;
    }

    @Override
    public void setPunishmentType(PunishmentType punishmentType2) {
        this.punishmentType = punishmentType2;
    }

    @Override
    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    @Override
    public void setRemovedSilent(boolean removedSilent) {
        this.removedSilent = removedSilent;
    }

    @Override
    public void setIp(boolean ip) {
        this.ip = ip;
    }

    @Override
    public void setVoided(boolean voided) {
        this.voided = voided;
    }

    @Override
    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    @Override
    public void setPunishmentID(String punishmentID) {
        this.punishmentID = punishmentID;
    }

    @Override
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public void setProof(List<IProof> proof) {
        this.proof = proof;
    }
}
