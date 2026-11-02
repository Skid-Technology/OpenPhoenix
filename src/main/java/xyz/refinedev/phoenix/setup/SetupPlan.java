package xyz.refinedev.phoenix.setup;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public final class SetupPlan {

    private SetupPlan() {}

    public static boolean matchesEnabled(
            Collection<UUID> values,
            UUID uUID,
            List<UUID> values2,
            UUID uUID2,
            Predicate<UUID> predicate) {
        Iterator iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            if (!((UUID) iterator2.next()).equals(uUID)) {
                return false;
            }
        }
        iterator2 = values2.iterator();
        while (iterator2.hasNext()) {
            UUID uUID3;
            if (!(uUID3 = (UUID) iterator2.next()).equals(uUID2) && predicate.test(uUID3)) {
                return false;
            }
        }
        return true;
    }
}
