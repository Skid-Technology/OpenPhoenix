package xyz.refinedev.phoenix.profile.punishment;

import java.util.Comparator;

public class PunishmentComparator implements Comparator<IPunishment> {

    public int compare(IPunishment punishment, IPunishment punishment2) {
        if (punishment.isActive() == punishment2.isActive()) {
            return punishment.getIssuedAt() > punishment2.getIssuedAt() ? -1 : 1;
        } else if (punishment.isActive() && !punishment2.isActive()) {
            return -1;
        } else {
            return !punishment.isActive() && punishment2.isActive() ? 1 : 0;
        }
    }
}
