package xyz.refinedev.phoenix.profile.punishment;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.profile.log.ILogable;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public interface IPunishment extends ILogable {

    PunishmentComparator COMPARATOR = new PunishmentComparator();

    static String getRandomSaltedString(int value) {
        StringBuilder stringBuilder = new StringBuilder();
        while (stringBuilder.length() < value) {
            float value2 = new Random().nextFloat();
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890".length();
            int value3 = (int) (value2 * 62.0F);
            stringBuilder.append(
                    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890"
                            .charAt(value3));
        }
        return stringBuilder.toString();
    }

    @Override
    boolean isActive();

    boolean isntActive();

    boolean isBan();

    boolean isIPBan();

    boolean isBlacklist();

    boolean isMute();

    PunishmentType getPunishmentType();

    boolean isSilent();

    boolean isRemovedSilent();

    boolean isIp();

    boolean isVoided();

    default void voidAs(UUID uUID, VoidReason voidReason, String text) {
        this.setVoided(true);
        this.setRemovedSilent(true);
        this.remove(uUID, voidReason.format(text));
    }

    @Nullable
    default VoidReason getVoidReason() {
        return this.isVoided() ? VoidReason.parse(this.getRemovedReason()) : null;
    }

    @NotNull
    default String getVoidNote() {
        return this.isVoided() ? VoidReason.noteOf(this.getRemovedReason()) : "";
    }

    boolean isShadow();

    String getPunishmentID();

    String getAddress();

    List<IProof> getProof();

    void setPunishmentType(PunishmentType punishmentType);

    void setSilent(boolean matches);

    void setRemovedSilent(boolean matches);

    void setIp(boolean matches);

    void setVoided(boolean matches);

    void setShadow(boolean matches);

    void setPunishmentID(String text);

    void setAddress(String text);

    void setProof(List<IProof> values);
}
