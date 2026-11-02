package xyz.refinedev.phoenix.whitelist;

import xyz.refinedev.phoenix.PlatformGetter;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.server.IServerData;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public enum WhitelistAccess {
    UUID(
            "UUID",
            (result, result2) ->
                    PlatformGetter.getInstance().getWhitelistHandler().isNameBased()
                            ? result2.getWhitelistInfo()
                                    .getUuids()
                                    .contains(result.getName().toLowerCase())
                            : result2.getWhitelistInfo()
                                    .getUuids()
                                    .contains(result.getUniqueId().toString())),
    RANK(
            "Rank",
            (result, result2) -> {
                IRank rank;
                return (rank = result2.getWhitelistInfo().getRank()) == null
                        ? true
                        : result.getGeneralRank().getPriority() >= rank.getPriority();
            });

    private final String displayName;

    private final BiFunction<IProfile, IServerData, Boolean> function;

    WhitelistAccess(String displayName, BiFunction<IProfile, IServerData, Boolean> biFunction) {
        this.displayName = displayName;
        this.function = biFunction;
    }

    public static WhitelistAccess getWhitelistAccessByName(String text) {
        Stream<xyz.refinedev.phoenix.whitelist.WhitelistAccess> stream2 = Arrays.stream(values());
        return stream2.filter(result -> result.name().equalsIgnoreCase(text))
                .findFirst()
                .orElse(null);
    }

    public static Set<WhitelistAccess> getTypesFromList(List<String> values) {
        HashSet<WhitelistAccess> values2 = new HashSet<>();
        for (String text : values) {
            values2.add(getWhitelistAccessByName(text));
        }
        return values2;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public BiFunction<IProfile, IServerData, Boolean> getFunction() {
        return this.function;
    }
}
