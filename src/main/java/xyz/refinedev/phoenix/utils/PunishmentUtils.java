package xyz.refinedev.phoenix.utils;

import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.PunishmentType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public final class PunishmentUtils {

    private static final String text = "This is a utility class and cannot be instantiated";

    public static List<IPunishment> findList(
            List<IPunishment> values, PunishmentType punishmentType) {
        ArrayList<IPunishment> values2 = new ArrayList<>();
        Iterator<IPunishment> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            IPunishment punishment;
            if ((punishment = (IPunishment) iterator2.next())
                            .getPunishmentType()
                            .equals(punishmentType)
                    || punishmentType == PunishmentType.BAN
                            && punishment.getPunishmentType() == PunishmentType.COMPETITIVE) {
                values2.add(punishment);
            }
        }
        return values2;
    }

    public static int calculateCount(List<IPunishment> values) {
        int value = 0;
        Iterator<IPunishment> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            if (((IPunishment) iterator2.next()).isActive()) {
                value++;
            }
        }
        return value;
    }

    public static int calculateCountByCOMPETITIVE(List<IPunishment> values) {
        int value = 0;
        Iterator<IPunishment> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            if (((IPunishment) iterator2.next()).getPunishmentType()
                    == PunishmentType.COMPETITIVE) {
                value++;
            }
        }
        return value;
    }

    public static int calculateCount(List<IPunishment> values, PunishmentType punishmentType) {
        HashSet<java.util.UUID> values2 = new HashSet<>();
        Iterator<IPunishment> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            IPunishment punishment;
            if ((punishment = (IPunishment) iterator2.next())
                    .getPunishmentType()
                    .equals(punishmentType)) {
                values2.add(punishment.getTarget());
            }
        }
        return values2.size();
    }

    private PunishmentUtils() {
        throw new UnsupportedOperationException(text);
    }
}
