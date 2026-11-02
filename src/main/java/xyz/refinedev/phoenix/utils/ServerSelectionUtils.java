package xyz.refinedev.phoenix.utils;

import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.profile.IProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

public final class ServerSelectionUtils {

    private static final String text = "This is a utility class and cannot be instantiated";

    public static Map<String, List<String>> findMap(
            Player player, Predicate<IProfile> predicate, Function<IProfile, String> function) {
        Phoenix instance = Phoenix.getInstance();
        HashMap<String, List<String>> entries = new HashMap<>();
        for (UUID uUID : instance.getNetworkHandler().getAllUuids()) {
            IProfile profile;
            if ((profile = instance.getProfileHandler().getProfile(uUID)) != null
                    && predicate.test(profile)
                    && instance.getVisibilityHandler().canSee(player.getUniqueId(), uUID)) {
                String displayName =
                        instance.getNetworkHandler().getPresence(uUID).getDisplayName();
                entries.computeIfAbsent(displayName, result -> new ArrayList<>())
                        .add((String) function.apply(profile));
            }
        }
        return entries;
    }

    private ServerSelectionUtils() {
        throw new UnsupportedOperationException(text);
    }
}
