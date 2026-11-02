package xyz.refinedev.phoenix.utils;

import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Predicate;

public final class BedrockUtils {

    private static volatile Predicate<UUID> predicate = result -> false;

    private BedrockUtils() {}

    public static void setPredicate(Predicate<UUID> predicate2) {
        if (predicate2 != null) {
            predicate = predicate2;
        }
    }

    public static boolean getUniqueId(Player player) {
        return player != null && test(player.getUniqueId());
    }

    public static boolean test(UUID uUID) {
        if (uUID == null) {
            return false;
        }
        try {
            return predicate.test(uUID);
        } catch (Throwable throwable) {
            return false;
        }
    }

    public static boolean getMostSignificantBits(UUID uUID) {
        return uUID != null && uUID.getMostSignificantBits() == 0L;
    }
}
