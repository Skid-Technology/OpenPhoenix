package xyz.refinedev.phoenix.permissible;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.handler.IRankHandler;
import xyz.refinedev.phoenix.permission.PhoenixPermissibleBase;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.grant.IGrant;
import xyz.refinedev.phoenix.profile.permission.IProfilePermission;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.rank.permission.IPermission;
import xyz.refinedev.phoenix.server.IServerData;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.EncryptedPasteBuilder;
import xyz.refinedev.phoenix.utils.chat.Clickable;
import xyz.refinedev.phoenix.utils.general.Tasks;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

public final class PermissionTracer {

    private static final Comparator<PermissionTracer.PermissionTrace> comparator =
            Comparator.<PermissionTracer.PermissionTrace>comparingInt(result -> result.count)
                    .thenComparing(result -> result.enabled);

    private static final long time = TimeUnit.MINUTES.toMillis(10L);

    static final int count = 100000;

    private static final DateTimeFormatter dateTimeFormatter =
            DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    public static volatile boolean enabled;

    private static final Map<UUID, List<PermissionTracer.Session>> sessionsById =
            new ConcurrentHashMap<>();

    private static final Map<String, PermissionTracer.Session> sessionsByName =
            new ConcurrentHashMap<>();

    private static final Map<String, PermissionTracer.Session> sessionsByNameMap =
            new ConcurrentHashMap<>();

    private static final Map<String, String> stringsByName = new ConcurrentHashMap<>();

    private static Runnable runnable;

    private PermissionTracer() {}

    public static PermissionTracer.Entry findEntry(
            Phoenix phoenix, IProfile profile, String text, IServerData serverData) {
        boolean matches;
        String text4 =
                (matches =
                                serverData != null
                                        && !serverData
                                                .getServerName()
                                                .equalsIgnoreCase(GlobalConfig.SERVER.NAME))
                        ? serverData.getServerName()
                        : GlobalConfig.SERVER.NAME;
        Set<String> values = matches ? serverData.getServerGroups() : GlobalConfig.SERVER.GROUPS;
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(profile.getUuid());
        boolean matches2 = !matches && offlinePlayer.isOp() && SettingsConfig.SERVER.OP_ENABLED;
        PermissionTracer.Entry entry;
        (entry =
                                create(
                                        profile,
                                        text,
                                        text4,
                                        values,
                                        phoenix.getRankHandler(),
                                        matches2,
                                        GlobalConfig.SERVER.STAR_PERM_ENABLED,
                                        GlobalConfig.SERVER.PASSTHROUGH_CHILDREN))
                        .enabled =
                matches;
        if (matches) {
            return entry;
        }
        if (profile.isTwoFactor() && !SettingsConfig.AUTH.ALLOWED_PERMISSIONS.contains(text)) {
            entry.enabledEnabled = false;
            entry.stage = PermissionTracer.Stage.TWO_FACTOR;
            entry.permissionTrace = null;
            entry.text2 = "the player has not finished 2FA, so only AUTH.ALLOWED_PERMISSIONS pass";
            updateEntry(entry, GlobalConfig.SERVER.STAR_PERM_ENABLED);
        } else if (entry.stage == PermissionTracer.Stage.UNSET) {
            Permission permission = phoenix.getPermissionMap().get(text);
            String text5 = phoenix.childPermissionCache.formatString(text);
            if (permission != null && permission.getDefault().getValue(matches2)) {
                entry.enabledEnabled = true;
                entry.stage = PermissionTracer.Stage.PLUGIN_DEFAULT;
                entry.text2 =
                        "default: "
                                + permission.getDefault().name().toLowerCase()
                                + formatString(text);
            } else if (text5 != null && toLowerCase(entry, text5)) {
                entry.enabledEnabled = true;
                entry.stage = PermissionTracer.Stage.CHILD;
                entry.text3 = text5;
                entry.text2 = "child of " + text5 + ", which the player has";
            } else if (phoenix.getDefaultPermissionMap().booleanValue(text, matches2)) {
                entry.enabledEnabled = true;
                entry.stage = PermissionTracer.Stage.BUKKIT_DEFAULT;
                entry.text2 =
                        "granted to everyone"
                                + (matches2 ? " or ops" : "")
                                + " by a registered default";
            }
        }
        Player player;
        if ((player = Bukkit.getPlayer(profile.getUuid())) != null) {
            entry.enabledBoolean = player.hasPermission(text);
            if (entry.enabledBoolean != entry.enabledEnabled) {
                entry.strings.add(
                        "Live result differs (live: "
                                + (entry.enabledBoolean ? "TRUE" : "FALSE")
                                + "). This player's permissions may be stale.");
            }
        }
        return entry;
    }

    public static PermissionTracer.Entry create(
            IProfile profile,
            String text3,
            String text4,
            Set<String> values,
            IRankHandler rankHandler,
            boolean matches,
            boolean matches2,
            boolean matches3) {
        PermissionTracer.Entry entry;
        (entry = new PermissionTracer.Entry()).text = text3;
        entry.textString = text4;
        List<xyz.refinedev.phoenix.permissible.PermissionTracer.PermissionTrace> values2 =
                findList(profile, text4, values, rankHandler, matches3, entry.strings);
        ArrayList values3 = new ArrayList();
        Iterator iterator2 = values2.iterator();
        while (iterator2.hasNext()) {
            PermissionTracer.PermissionTrace permissionTrace2;
            if ((permissionTrace2 = (PermissionTracer.PermissionTrace) iterator2.next()).text2
                    == null) {
                values3.add(permissionTrace2);
            }
        }
        values3.sort(comparator);
        for (PermissionTracer.PermissionTrace permissionTrace3 :
                (Iterable<PermissionTracer.PermissionTrace>) ((Iterable) values3)) {
            entry.permissionTracesByName.put(permissionTrace3.text, permissionTrace3);
        }
        String text5;
        PermissionTracer.PermissionTrace permissionTrace4 =
                findPermissionTrace(text5 = text3.toLowerCase(), entry.permissionTracesByName);
        PermissionTracer.PermissionTrace permissionTrace5 = entry.permissionTracesByName.get("*");
        if (permissionTrace4 != null && permissionTrace4.enabled) {
            updateEntry(entry, false, PermissionTracer.Stage.EXACT_NEGATION, permissionTrace4);
        } else if (matches) {
            updateEntry(entry, true, PermissionTracer.Stage.OP, null);
            entry.text2 = "the player is op and OP_ENABLED is on";
        } else if (matches2 && permissionTrace5 != null && !permissionTrace5.enabled) {
            updateEntry(entry, true, PermissionTracer.Stage.STAR, permissionTrace5);
        } else if (permissionTrace4 != null) {
            updateEntry(entry, true, PermissionTracer.Stage.EXACT, permissionTrace4);
        } else {
            updateEntry(entry, false, PermissionTracer.Stage.UNSET, null);
            for (int index = 0; index < text5.length(); index++) {
                PermissionTracer.PermissionTrace permissionTrace6;
                if (text5.charAt(index) == '.'
                        && (permissionTrace6 =
                                        findPermissionTrace(
                                                text5.substring(0, index) + ".*",
                                                entry.permissionTracesByName))
                                != null) {
                    updateEntry(
                            entry,
                            !permissionTrace6.enabled,
                            PermissionTracer.Stage.WILDCARD,
                            permissionTrace6);
                    break;
                }
            }
        }
        if (entry.permissionTrace != null
                && (entry.stage == PermissionTracer.Stage.EXACT
                        || entry.stage == PermissionTracer.Stage.EXACT_NEGATION)
                && !entry.permissionTrace.text.equals(text5)) {
            entry.text2 =
                    entry.permissionTrace.text
                            + " ("
                            + entry.permissionTrace.getString()
                            + ") outweighs the listed "
                            + text5;
        }
        Iterator iterator3 = values2.iterator();
        while (iterator3.hasNext()) {
            PermissionTracer.PermissionTrace permissionTrace7;
            if (matchesEnabled(
                    (permissionTrace7 = (PermissionTracer.PermissionTrace) iterator3.next()).text,
                    text5)) {
                entry.permissionTraces.add(permissionTrace7);
            }
        }
        PermissionTracer.Entry entry2 = entry;
        entry.permissionTraces.sort(
                (result, result2) -> {
                    if (result == entry2.permissionTrace) {
                        return -1;
                    } else if (result2 == entry2.permissionTrace) {
                        return 1;
                    } else if (result.text2 == null != (result2.text2 == null)) {
                        return result.text2 == null ? -1 : 1;
                    } else {
                        return Integer.compare(result2.count, result.count);
                    }
                });
        updateEntry(entry, matches2);
        if (!entry.enabledEnabled && entry.stage == PermissionTracer.Stage.UNSET) {
            applyEntry(entry, text5, values3);
        }
        return entry;
    }

    private static List<PermissionTracer.PermissionTrace> findList(
            IProfile profile,
            String text,
            Set<String> values,
            IRankHandler rankHandler,
            boolean matches,
            List<String> values2) {
        ArrayList<PermissionTracer.PermissionTrace> values3 = new ArrayList<>();
        Iterator iterator2 = profile.getGrants().iterator();
        while (iterator2.hasNext()) {
            IGrant grant;
            if ((grant = (IGrant) iterator2.next()).isActive()) {
                IRank rank;
                if ((rank = rankHandler.getRank(grant.getRankId())) == null) {
                    values2.add(
                            "Grant "
                                    + formatString(grant.getUuid())
                                    + " points to a deleted rank.");
                } else {
                    String text2 = null;
                    if (!grant.isScope(text, values)) {
                        text2 = "grant scoped to " + formatString(grant.getNiceScopes());
                    } else if (!rank.isScope(text, values)) {
                        text2 = "rank scoped to " + formatString(rank.getNiceScopes());
                    }
                    applyList(
                            values3,
                            rankHandler,
                            rank,
                            PermissionTracer.Source.GRANT,
                            grant,
                            rank.getName(),
                            true,
                            new HashSet<>(),
                            matches,
                            text,
                            values,
                            text2);
                }
            }
        }
        for (IRank rank2 : rankHandler.getDefaultRanks()) {
            applyList(
                    values3,
                    rankHandler,
                    rank2,
                    PermissionTracer.Source.DEFAULT_RANK,
                    null,
                    rank2.getName(),
                    true,
                    new HashSet<>(),
                    matches,
                    text,
                    values,
                    null);
        }
        iterator2 = profile.getPermissions().iterator();
        while (iterator2.hasNext()) {
            IProfilePermission profilePermission;
            if (!(profilePermission = (IProfilePermission) iterator2.next()).hasExpired()) {
                String text3 =
                        profilePermission.isScope(text, values)
                                ? null
                                : "scoped to " + formatString(profilePermission.getNiceScopes());
                values3.add(
                        new PermissionTracer.PermissionTrace(
                                profilePermission.getPermission().toLowerCase(),
                                Integer.MAX_VALUE,
                                PermissionTracer.Source.PLAYER,
                                null,
                                null,
                                null,
                                profilePermission,
                                text3));
            }
        }
        return values3;
    }

    private static void applyList(
            List<PermissionTracer.PermissionTrace> values,
            IRankHandler rankHandler,
            IRank rank,
            PermissionTracer.Source source,
            IGrant grant,
            String text,
            boolean matches,
            Set<UUID> values2,
            boolean matches2,
            String text2,
            Set<String> values3,
            String text3) {
        if (rank != null && values2.add(rank.getUuid())) {
            for (IPermission permission : rank.getPermissions()) {
                String text4 = text3;
                if (text3 == null && !permission.isScope(text2, values3)) {
                    text4 = "node scoped to " + formatString(permission.getNiceScopes());
                }
                values.add(
                        new PermissionTracer.PermissionTrace(
                                permission.getPermission().toLowerCase(),
                                rank.getPriority(),
                                source,
                                rank,
                                text,
                                grant,
                                null,
                                text4));
            }
            if (matches) {
                for (UUID uUID : rank.getInheritance()) {
                    IRank rank2;
                    if ((rank2 = rankHandler.getRank(uUID)) != null) {
                        String text5 = text3;
                        Set<UUID> values4 = values2;
                        if (text5 == null && !rank2.isScope(text2, values3)) {
                            text5 =
                                    "rank "
                                            + rank2.getName()
                                            + " scoped to "
                                            + formatString(rank2.getNiceScopes());
                            values4 = new HashSet(values2);
                        }
                        applyList(
                                values,
                                rankHandler,
                                rank2,
                                source,
                                grant,
                                text + " \u2192 " + rank2.getName(),
                                matches2,
                                values4,
                                matches2,
                                text2,
                                values3,
                                text5);
                    }
                }
            }
        }
    }

    private static PermissionTracer.PermissionTrace findPermissionTrace(
            String text, Map<String, PermissionTracer.PermissionTrace> entries) {
        PermissionTracer.PermissionTrace permissionTrace;
        if ((permissionTrace = (PermissionTracer.PermissionTrace) entries.get(text)) == null) {
            return null;
        }
        PermissionTracer.PermissionTrace permissionTrace2 = permissionTrace;
        for (int index = 0; index < text.length(); index++) {
            String text2;
            PermissionTracer.PermissionTrace permissionTrace3;
            if (text.charAt(index) == '.'
                    && !(text2 = text.substring(0, index) + ".*").equals(text)
                    && (permissionTrace3 = (PermissionTracer.PermissionTrace) entries.get(text2))
                            != null
                    && comparator.compare(permissionTrace3, permissionTrace2) > 0) {
                permissionTrace2 = permissionTrace3;
            }
        }
        return permissionTrace2;
    }

    private static boolean toLowerCase(PermissionTracer.Entry entry, String text) {
        PermissionTracer.PermissionTrace permissionTrace;
        return (permissionTrace =
                                findPermissionTrace(
                                        text.toLowerCase(), entry.permissionTracesByName))
                        != null
                && !permissionTrace.enabled;
    }

    private static void updateEntry(
            PermissionTracer.Entry entry,
            boolean matches,
            PermissionTracer.Stage stage2,
            PermissionTracer.PermissionTrace permissionTrace2) {
        entry.enabledEnabled = matches;
        entry.stage = stage2;
        entry.permissionTrace = permissionTrace2;
    }

    private static boolean matchesEnabled(String text, String text2) {
        if (text.equals(text2) || text.equals("*")) {
            return true;
        }
        if (!text.endsWith(".*")) {
            return false;
        }
        String text3 = text.substring(0, text.length() - 1);
        return text2.startsWith(text3) && !text2.equals(text);
    }

    private static void updateEntry(PermissionTracer.Entry entry, boolean matches) {
        Iterator<xyz.refinedev.phoenix.permissible.PermissionTracer.PermissionTrace> iterator2 =
                entry.permissionTraces.iterator();
        while (iterator2.hasNext()) {
            PermissionTracer.PermissionTrace permissionTrace2;
            if ((permissionTrace2 = (PermissionTracer.PermissionTrace) iterator2.next()).text2
                    != null) {
                permissionTrace2.text3 = "inactive: " + permissionTrace2.text2;
            } else {
                PermissionTracer.PermissionTrace permissionTrace3 =
                        entry.permissionTracesByName.get(permissionTrace2.text);
                PermissionTracer.PermissionTrace permissionTrace4 =
                        findPermissionTrace(permissionTrace2.text, entry.permissionTracesByName);
                if (permissionTrace2 == entry.permissionTrace) {
                    permissionTrace2.text3 = "winner";
                } else if (permissionTrace2 != permissionTrace3) {
                    if (permissionTrace3.count > permissionTrace2.count) {
                        permissionTrace2.text3 =
                                "overridden: lower weight than "
                                        + permissionTrace3.append()
                                        + " ("
                                        + permissionTrace3.getString()
                                        + ")";
                    } else if (permissionTrace3.enabled != permissionTrace2.enabled) {
                        permissionTrace2.text3 = "overridden: negation wins at equal weight";
                    } else {
                        permissionTrace2.text3 = "duplicate of " + formatString(permissionTrace3);
                    }
                } else if (permissionTrace4 != permissionTrace2) {
                    permissionTrace2.text3 =
                            "overridden: heavier "
                                    + permissionTrace4.append()
                                    + " ("
                                    + permissionTrace4.getString()
                                    + ")";
                } else if (permissionTrace2.text.equals("*")) {
                    if (!matches) {
                        permissionTrace2.text3 = "ignored: STAR_PERM_ENABLED is off";
                    } else if (permissionTrace2.enabled) {
                        permissionTrace2.text3 = "not used: -* only denies the literal * node";
                    } else {
                        permissionTrace2.text3 = "not used: decided by " + entry.stage.label;
                    }
                } else if (entry.stage == PermissionTracer.Stage.WILDCARD
                        && permissionTrace2.text.endsWith(".*")) {
                    permissionTrace2.text3 =
                            "not used: shallower "
                                    + entry.permissionTrace.text
                                    + " is checked first";
                } else {
                    permissionTrace2.text3 = "not used: decided by " + entry.stage.label;
                }
            }
        }
    }

    private static void applyEntry(
            PermissionTracer.Entry entry,
            String text2,
            List<PermissionTracer.PermissionTrace> values) {
        TreeSet<String> treeSet = new TreeSet<>();
        for (PermissionTracer.PermissionTrace permissionTrace : values) {
            treeSet.add(permissionTrace.text);
        }
        int value = Math.max(2, text2.length() / 4);
        ArrayList<String> values2 = new ArrayList<>();
        Iterator iterator2 = treeSet.iterator();
        while (iterator2.hasNext()) {
            String text3;
            if (calculateCount(text3 = (String) iterator2.next(), text2) <= value) {
                values2.add(text3);
            }
        }
        values2.sort(Comparator.comparingInt(result -> calculateCount(result, text2)));
        entry.stringsList.addAll(values2.subList(0, Math.min(3, values2.size())));
    }

    private static int calculateCount(String text, String text2) {
        int[] values = new int[text2.length() + 1];
        int[] values2 = new int[text2.length() + 1];
        int value = 0;
        while (value <= text2.length()) {
            values[value] = value++;
        }
        for (int index = 1; index <= text.length(); index++) {
            values2[0] = index;
            for (int index2 = 1; index2 <= text2.length(); index2++) {
                int value2 = text.charAt(index - 1) == text2.charAt(index2 - 1) ? 0 : 1;
                values2[index2] =
                        Math.min(
                                Math.min(values2[index2 - 1] + 1, values[index2] + 1),
                                values[index2 - 1] + value2);
            }
            int[] values3 = values;
            values = values2;
            values2 = values3;
        }
        return values[text2.length()];
    }

    private static String formatString(String text) {
        Plugin[] plugins;
        int value = (plugins = Bukkit.getPluginManager().getPlugins()).length;
        for (int index = 0; index < value; index++) {
            Plugin plugin;
            Iterator<Permission> iterator2 =
                    (plugin = plugins[index]).getDescription().getPermissions().iterator();
            while (iterator2.hasNext()) {
                if (((Permission) iterator2.next()).getName().equalsIgnoreCase(text)) {
                    return " from " + plugin.getName();
                }
            }
        }
        return "";
    }

    private static String formatString(List<String> values) {
        return values != null && !values.isEmpty() ? String.join(", ", values) : "nowhere";
    }

    private static String formatString(UUID uUID) {
        return "#" + uUID.toString().substring(0, 8);
    }

    private static String formatString(PermissionTracer.PermissionTrace permissionTrace) {
        switch (permissionTrace.source) {
            case DEFAULT_RANK:
                return "default rank " + permissionTrace.textString;
            case PLAYER:
                return "player permission";
            default:
                return "rank " + permissionTrace.textString;
        }
    }

    public static void sendPlayer(
            CommandSender sender, IProfile profile, PermissionTracer.Entry entry) {
        String text4 = Phoenix.MAIN_COLOR;
        String text5 = Phoenix.MIDDLE_COLOR;
        String text6 = Phoenix.SECONDARY_COLOR;
        String text7 = entry.enabledEnabled ? "&a\u2714 TRUE" : "&c\u2718 FALSE";
        if (entry.stage == PermissionTracer.Stage.UNSET && entry.enabled) {
            text7 = "&7? UNSET";
        }
        sender.sendMessage(
                Color.translate(
                        text4
                                + entry.text
                                + text5
                                + " for "
                                + text4
                                + profile.getName()
                                + text5
                                + " on "
                                + text4
                                + entry.textString
                                + (entry.enabled ? text5 + " (remote)" : "")
                                + text5
                                + " \u2192 "
                                + text7));
        String text8 = entry.stage.label;
        if (entry.text2 != null) {
            text8 = text8 + " (" + entry.text2 + ")";
        } else if (entry.permissionTrace != null) {
            text8 =
                    text8
                            + " ("
                            + entry.permissionTrace.append()
                            + " from "
                            + formatString(entry.permissionTrace)
                            + ")";
        }
        sender.sendMessage(Color.translate(text5 + "Decided by: " + text6 + text8));
        if ((entry.stage == PermissionTracer.Stage.OP
                        || entry.stage == PermissionTracer.Stage.TWO_FACTOR
                        || entry.stage == PermissionTracer.Stage.PLUGIN_DEFAULT
                        || entry.stage == PermissionTracer.Stage.CHILD
                        || entry.stage == PermissionTracer.Stage.BUKKIT_DEFAULT)
                && !entry.permissionTraces.isEmpty()) {
            sender.sendMessage(Color.translate("&7Rank nodes below were not what decided it."));
        }
        Iterator<xyz.refinedev.phoenix.permissible.PermissionTracer.PermissionTrace> iterator2 =
                entry.permissionTraces.iterator();
        while (iterator2.hasNext()) {
            PermissionTracer.PermissionTrace permissionTrace2;
            String text9 =
                    (permissionTrace2 = (PermissionTracer.PermissionTrace) iterator2.next()).text2
                                    != null
                            ? "&8\u00b7"
                            : (permissionTrace2.enabled ? "&c\u2718" : "&a\u2714");
            String text10 =
                    permissionTrace2 == entry.permissionTrace
                            ? "&f"
                            : (permissionTrace2.text2 != null ? "&8" : "&7");
            String text11 =
                    permissionTrace2.source == PermissionTracer.Source.PLAYER
                            ? "player"
                            : permissionTrace2.textString;
            String text12 =
                    permissionTrace2 == entry.permissionTrace
                            ? "&e\u2190 winner"
                            : "&7" + permissionTrace2.text3;
            Clickable clickable;
            (clickable = new Clickable())
                    .add(
                            " "
                                    + text9
                                    + " "
                                    + text10
                                    + permissionTrace2.append()
                                    + " &7"
                                    + text11
                                    + " &8"
                                    + permissionTrace2.getString()
                                    + " "
                                    + text12,
                            formatStringBySource(permissionTrace2));
            clickable.sendToPlayer(sender);
        }
        if (entry.stage == PermissionTracer.Stage.CHILD) {
            new Clickable(
                            "&7Trace the parent: &f" + entry.text3,
                            "&7Click to run /phoenix why for " + entry.text3,
                            "/phoenix why " + profile.getName() + " " + entry.text3)
                    .sendToPlayer(sender);
        }
        if (entry.stage == PermissionTracer.Stage.UNSET) {
            String text13 = entry.text.contains(".") ? " or any wildcard above it" : "";
            sender.sendMessage(
                    Color.translate(
                            "&7No rank, grant or player node matches "
                                    + entry.text.toLowerCase()
                                    + text13
                                    + "."));
            if (!entry.stringsList.isEmpty()) {
                sender.sendMessage(
                        Color.translate(
                                "&7Did you mean: &f"
                                        + String.join("&7, &f", entry.stringsList)
                                        + "&7?"));
            }
        }
        if (entry.enabled) {
            sender.sendMessage(
                    Color.translate(
                            "&7Plugin defaults and child permissions on "
                                    + entry.textString
                                    + " aren't visible from here"
                                    + (entry.stage == PermissionTracer.Stage.UNSET
                                            ? ", so that server's plugins decide."
                                            : ".")));
        }
        for (String text14 : entry.strings) {
            sender.sendMessage(Color.translate("&6\u26a0 " + text14));
        }
    }

    private static String formatStringBySource(PermissionTracer.PermissionTrace permissionTrace) {
        StringBuilder stringBuilder = new StringBuilder();
        switch (permissionTrace.source) {
            case DEFAULT_RANK:
                stringBuilder.append("&fDefault rank &7(everyone has it)");
                break;
            case PLAYER:
                stringBuilder.append("&fPlayer permission");
                IProfilePermission profilePermission2 = permissionTrace.profilePermission;
                stringBuilder
                        .append("\n&7Expires: &f")
                        .append(
                                profilePermission2.isPermanent()
                                        ? "never"
                                        : "in "
                                                + DateUtils.formatTimeMillis(
                                                        profilePermission2.getExpiration()
                                                                - System.currentTimeMillis()));
                stringBuilder
                        .append("\n&7Scope: &f")
                        .append(formatString(profilePermission2.getNiceScopes()));
                break;
            default:
                stringBuilder.append("&fGranted rank");
                IGrant grant2 = permissionTrace.grant;
                stringBuilder.append("\n&7Grant: &f").append(formatString(grant2.getUuid()));
                stringBuilder.append("\n&7Issued by: &f").append(grant2.getNiceIssuedBy());
                if (grant2.getReason() != null && !grant2.getReason().isEmpty()) {
                    stringBuilder.append("\n&7Reason: &f").append(grant2.getReason());
                }
                stringBuilder
                        .append("\n&7Expires: &f")
                        .append(
                                grant2.isPermanent()
                                        ? "never"
                                        : "in "
                                                + DateUtils.formatTimeMillis(
                                                        grant2.getRemainingDuration()));
                stringBuilder
                        .append("\n&7Grant scope: &f")
                        .append(formatString(grant2.getNiceScopes()));
        }
        if (permissionTrace.textString != null && permissionTrace.textString.contains("\u2192")) {
            stringBuilder.append("\n&7Inherited via: &f").append(permissionTrace.textString);
        }
        if (permissionTrace.rank != null) {
            stringBuilder.append("\n&7Rank weight: &f").append(permissionTrace.rank.getPriority());
        }
        return stringBuilder.toString();
    }

    public static String formatString(CommandSender sender) {
        return sender instanceof Player
                ? ((Player) sender).getUniqueId().toString()
                : "console:" + sender.getName();
    }

    public static synchronized PermissionTracer.Session create(
            CommandSender sender, Player player, String text2) {
        PermissionTracer.Session session;
        if ((session = sessionsByName.get(formatString(sender))) != null) {
            updateSession(session, "replaced by a new session");
        }
        if (runnable == null) {
            runnable = Tasks.runCancellableTimer(PermissionTracer::initialize, 20L, 20L);
        }
        PermissionTracer.Session session2 =
                new PermissionTracer.Session(sender, player.getUniqueId(), player.getName(), text2);
        sessionsByName.put(session2.text, session2);
        sessionsById
                .computeIfAbsent(session2.id, result -> new CopyOnWriteArrayList<>())
                .add(session2);
        enabled = true;
        return session2;
    }

    public static PermissionTracer.Session findSession(CommandSender sender) {
        return sessionsByName.get(formatString(sender));
    }

    public static PermissionTracer.Session findSessionBySessionsByNameMap(CommandSender sender) {
        return sessionsByNameMap.get(formatString(sender));
    }

    public static synchronized void updateSession(PermissionTracer.Session session, String text2) {
        if (sessionsByName.remove(session.text, session)) {
            List<xyz.refinedev.phoenix.permissible.PermissionTracer.Session> values;
            if ((values = sessionsById.get(session.id)) != null) {
                values.remove(session);
                if (values.isEmpty()) {
                    sessionsById.remove(session.id);
                }
            }
            if (sessionsByName.isEmpty()) {
                enabled = false;
                if (runnable != null) {
                    runnable.run();
                    runnable = null;
                }
            }
            session.timeTime = System.currentTimeMillis();
            sessionsByNameMap.put(session.text, session);
            applySession(session, text2);
        }
    }

    public static void applyPlayer(Player player, String text, boolean matches, boolean matches2) {
        List<xyz.refinedev.phoenix.permissible.PermissionTracer.Session> values;
        if ((values = sessionsById.get(player.getUniqueId())) != null) {
            PermissionTracer.Stage stage = null;
            Iterator iterator2 = values.iterator();
            while (iterator2.hasNext()) {
                PermissionTracer.Session session;
                if ((session = (PermissionTracer.Session) iterator2.next()).matchesEnabled(text)) {
                    if (stage == null) {
                        IProfile profile;
                        stage =
                                (profile =
                                                        Phoenix.getInstance()
                                                                .getProfileHandler()
                                                                .getCachedProfile(
                                                                        player.getUniqueId()))
                                                == null
                                        ? PermissionTracer.Stage.UNSET
                                        : findStage(profile, text, matches, matches2);
                    }
                    session.updateString(text, matches, stage, Thread.currentThread().getName());
                }
            }
        }
    }

    static PermissionTracer.Stage findStage(
            IProfile profile, String text, boolean matches, boolean matches2) {
        if (profile.isTwoFactor() && !SettingsConfig.AUTH.ALLOWED_PERMISSIONS.contains(text)) {
            return PermissionTracer.Stage.TWO_FACTOR;
        }
        Map<String, Boolean> attachmentPermissions = profile.getAttachmentPermissions();
        String text2 = text.toLowerCase();
        Boolean matches3;
        if ((matches3 = (Boolean) attachmentPermissions.get(text2)) != null && !matches3) {
            return PermissionTracer.Stage.EXACT_NEGATION;
        }
        if (matches2) {
            return PermissionTracer.Stage.OP;
        }
        if (GlobalConfig.SERVER.STAR_PERM_ENABLED
                && Boolean.TRUE.equals(attachmentPermissions.get("*"))) {
            return PermissionTracer.Stage.STAR;
        }
        if (matches3 != null) {
            return PermissionTracer.Stage.EXACT;
        }
        for (int index = 0; index < text2.length(); index++) {
            if (text2.charAt(index) == '.'
                    && attachmentPermissions.containsKey(text2.substring(0, index) + ".*")) {
                return PermissionTracer.Stage.WILDCARD;
            }
        }
        Permission permission;
        Phoenix phoenix;
        if ((permission = (phoenix = Phoenix.getInstance()).getPermissionMap().get(text)) != null
                && permission.getDefault().getValue(matches2)) {
            return PermissionTracer.Stage.PLUGIN_DEFAULT;
        } else if (phoenix.childPermissionCache.hasAccessToPermission(profile, text)) {
            return PermissionTracer.Stage.CHILD;
        } else {
            return matches ? PermissionTracer.Stage.BUKKIT_DEFAULT : PermissionTracer.Stage.UNSET;
        }
    }

    private static String getString() {
        String name = PermissionTracer.class.getName();
        String name2 = PhoenixPermissibleBase.class.getName();
        StackTraceElement[] stackTraceElements;
        int value = (stackTraceElements = Thread.currentThread().getStackTrace()).length;
        for (int index = 0; index < value; index++) {
            String text;
            String text2;
            if (!(text = stackTraceElements[index].getClassName()).equals(name)
                    && !text.startsWith(name + "$")
                    && !text.equals(name2)
                    && !startsWith(text)
                    && !(text2 =
                                    stringsByName.computeIfAbsent(
                                            text, PermissionTracer::formatStringByString))
                            .isEmpty()) {
                return text2;
            }
        }
        return "server";
    }

    private static boolean startsWith(String text) {
        return text.startsWith("java.")
                || text.startsWith("javax.")
                || text.startsWith("jdk.")
                || text.startsWith("sun.")
                || text.startsWith("com.sun.")
                || text.startsWith("org.bukkit.")
                || text.startsWith("net.minecraft.")
                || text.startsWith("io.papermc.")
                || text.startsWith("com.destroystokyo.")
                || text.startsWith("org.spigotmc.")
                || text.startsWith("io.netty.")
                || text.startsWith("ca.spottedleaf.");
    }

    static String formatStringByString(String text) {
        if (Bukkit.getServer() == null) {
            return "";
        }
        String text2 = "";
        int value = 0;
        String[] texts = text.split("\\.");
        Plugin[] plugins;
        int value2 = (plugins = Bukkit.getPluginManager().getPlugins()).length;
        for (int index = 0; index < value2; index++) {
            Plugin plugin;
            String[] texts2;
            int value3 =
                    (texts2 = (plugin = plugins[index]).getDescription().getMain().split("\\."))
                                    .length
                            - 1;
            int value4 = 0;
            while (value4 < value3
                    && value4 < texts.length - 1
                    && texts2[value4].equals(texts[value4])) {
                value4++;
            }
            if (value4 >= Math.min(3, value3) && value4 > value) {
                text2 = plugin.getName();
                value = value4;
            }
        }
        return text2;
    }

    private static void initialize() {
        long now = System.currentTimeMillis();
        Iterator iterator2 = new ArrayList<>(sessionsByName.values()).iterator();
        while (iterator2.hasNext()) {
            PermissionTracer.Session session;
            if ((session = (PermissionTracer.Session) iterator2.next()).commandSender
                            instanceof Player
                    && !((Player) session.commandSender).isOnline()) {
                updateSession(session, "you left");
            } else if (Bukkit.getPlayer(session.id) == null) {
                updateSession(session, session.textString + " left this server");
            } else {
                PermissionTracer.TraceSession traceSession;
                if (now - session.time >= time) {
                    updateSession(session, "10 minute limit");
                } else {
                    for (int index = 0;
                            index < 15 && (traceSession = session.queue.poll()) != null;
                            index++) {
                        applySession(session, traceSession, null);
                    }
                }
            }
        }
    }

    private static void applySession(
            PermissionTracer.Session session,
            PermissionTracer.TraceSession traceSession,
            String text2) {
        String text3 = traceSession.enabled ? "&a\u2714" : "&c\u2718";
        String text4 =
                traceSession.counter.get() > 1L ? " &7\u00d7" + traceSession.counter.get() : "";
        String text5 = text2 != null ? text2 : "&8[verbose] &7" + session.textString + " ";
        new Clickable(
                        text5
                                + text3
                                + " &f"
                                + traceSession.text
                                + text4
                                + " &8"
                                + traceSession.stage.name()
                                + " &7"
                                + traceSession.textString,
                        "&7Click to trace this check",
                        "/phoenix why " + session.textString + " " + traceSession.text)
                .sendToPlayer(session.commandSender);
    }

    private static void applySession(PermissionTracer.Session session, String text) {
        CommandSender sender = session.commandSender;
        if (!(session.commandSender instanceof Player) || ((Player) sender).isOnline()) {
            String text2 = Phoenix.MAIN_COLOR;
            String text3 = Phoenix.MIDDLE_COLOR;
            long value = (session.timeTime - session.time) / 1000L;
            sender.sendMessage(
                    Color.translate(
                            text3
                                    + "Verbose for "
                                    + text2
                                    + session.textString
                                    + text3
                                    + " stopped ("
                                    + text
                                    + "). "
                                    + text2
                                    + session.counter.get()
                                    + text3
                                    + " checks, "
                                    + text2
                                    + session.traceSessionsByName.size()
                                    + text3
                                    + " distinct permissions in "
                                    + text2
                                    + DateUtils.formatTimeMillis(value * 1000L).trim()
                                    + text3
                                    + "."));
            List<xyz.refinedev.phoenix.permissible.PermissionTracer.TraceSession> values;
            if (!(values = findList(session, 15)).isEmpty()) {
                sender.sendMessage(Color.translate(text3 + "Top permissions (denied first):"));
            }
            for (PermissionTracer.TraceSession traceSession : values) {
                applySession(session, traceSession, " ");
            }
            if (session.counter.get() != 0L) {
                if (sender instanceof Player) {
                    new Clickable(
                                    "&e[Upload full log]",
                                    "&7Upload every recorded check and get a link",
                                    "/phoenix verbose upload")
                            .sendToPlayer(sender);
                } else {
                    sender.sendMessage(
                            Color.translate("&7Run /phoenix verbose upload for the full log."));
                }
            }
        }
    }

    static List<PermissionTracer.TraceSession> findList(
            PermissionTracer.Session session, int value) {
        ArrayList values2;
        (values2 = new ArrayList<>(session.traceSessionsByName.values()))
                .sort(
                        Comparator.<PermissionTracer.TraceSession, Boolean>comparing(
                                        result -> !result.enabledEnabled)
                                .thenComparing(result -> -result.counter.get()));
        return new ArrayList<>(values2.subList(0, Math.min(value, values2.size())));
    }

    public static void applyCommandSender(CommandSender sender, PermissionTracer.Session session) {
        ArrayList<String> values2;
        synchronized (session.strings) {
            values2 = new ArrayList<>(session.strings);
        }
        StringBuilder stringBuilder;
        (stringBuilder = new StringBuilder())
                .append("Phoenix verbose for ")
                .append(session.textString)
                .append(" on ")
                .append(GlobalConfig.SERVER.NAME)
                .append(session.text2 == null ? "" : " (filter: " + session.text2 + ")")
                .append('\n');
        stringBuilder
                .append(session.counter.get())
                .append(" checks, ")
                .append(session.traceSessionsByName.size())
                .append(" distinct permissions\n\n");
        ArrayList<PermissionTracer.TraceSession> values3;
        (values3 = new ArrayList<>(session.traceSessionsByName.values()))
                .sort(Comparator.comparing(result -> -result.counter.get()));
        for (PermissionTracer.TraceSession traceSession : values3) {
            stringBuilder
                    .append(traceSession.enabled ? "TRUE  " : "FALSE ")
                    .append(traceSession.stage.name())
                    .append(' ')
                    .append(traceSession.text)
                    .append(" x")
                    .append(traceSession.counter.get())
                    .append(" [")
                    .append(traceSession.textString)
                    .append("]\n");
        }
        stringBuilder.append('\n');
        for (String text3 : values2) {
            stringBuilder.append(text3).append('\n');
        }
        if (session.counter.get() > 100000L) {
            stringBuilder.append("... log stopped at ").append(100000).append(" lines\n");
        }
        StringBuilder stringBuilder2 = stringBuilder;
        Tasks.runAsync(
                () -> {
                    try {
                        String string =
                                new EncryptedPasteBuilder()
                                        .findEncryptedPasteBuilderByTextString(
                                                stringBuilder2.toString())
                                        .getEncryptedPasteBuilder()
                                        .getString();
                        Clickable clickable;
                        (clickable = new Clickable())
                                .addUrl(
                                        Phoenix.SECONDARY_COLOR + "Link: " + string,
                                        "&7Click to open",
                                        string);
                        clickable.sendToPlayer(sender);
                    } catch (Exception exception) {
                        sender.sendMessage(Color.translate("&cCould not upload the verbose log."));
                        Phoenix.getInstance()
                                .getLogger()
                                .log(Level.SEVERE, "Failed to upload a verbose log:", exception);
                    }
                });
    }

    public static final class Entry {

        String text;

        String textString;

        boolean enabled;

        boolean enabledEnabled;

        PermissionTracer.Stage stage;

        String text2;

        String text3;

        PermissionTracer.PermissionTrace permissionTrace;

        Boolean enabledBoolean;

        final List<PermissionTracer.PermissionTrace> permissionTraces = new ArrayList<>();

        final List<String> strings = new ArrayList<>();

        final List<String> stringsList = new ArrayList<>();

        Map<String, PermissionTracer.PermissionTrace> permissionTracesByName = new HashMap<>();

        public boolean isEnabledEnabled() {
            return this.enabledEnabled;
        }

        public PermissionTracer.Stage getStage() {
            return this.stage;
        }

        public PermissionTracer.PermissionTrace getPermissionTrace() {
            return this.permissionTrace;
        }

        public List<PermissionTracer.PermissionTrace> getPermissionTraces() {
            return this.permissionTraces;
        }

        public List<String> getStrings() {
            return this.strings;
        }

        public List<String> getStringsList() {
            return this.stringsList;
        }
    }

    public static final class PermissionTrace {

        final String text;

        final boolean enabled;

        final int count;

        final PermissionTracer.Source source;

        final IRank rank;

        final String textString;

        final IGrant grant;

        final IProfilePermission profilePermission;

        final String text2;

        String text3;

        private static final String PLAYER_TEXT = "player";

        PermissionTrace(
                String text3,
                int count,
                PermissionTracer.Source source2,
                IRank rank2,
                String textString,
                IGrant grant2,
                IProfilePermission profilePermission2,
                String text2) {
            this.enabled = text3.startsWith("-");
            this.text = this.enabled ? text3.substring(1) : text3;
            this.count = count;
            this.source = source2;
            this.rank = rank2;
            this.textString = textString;
            this.grant = grant2;
            this.profilePermission = profilePermission2;
            this.text2 = text2;
        }

        public String getText() {
            return this.text;
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public String getText3() {
            return this.text3;
        }

        String append() {
            return (this.enabled ? "-" : "") + this.text;
        }

        String getString() {
            return this.source == PermissionTracer.Source.PLAYER ? PLAYER_TEXT : "w" + this.count;
        }
    }

    public static final class Session {

        final CommandSender commandSender;

        final String text;

        final UUID id;

        final String textString;

        final String text2;

        final long time = System.currentTimeMillis();

        long timeTime;

        final Map<String, PermissionTracer.TraceSession> traceSessionsByName =
                new ConcurrentHashMap<>();

        final Queue<PermissionTracer.TraceSession> queue = new ConcurrentLinkedQueue<>();

        final List<String> strings = Collections.synchronizedList(new ArrayList<>());

        final AtomicLong counter = new AtomicLong();

        private static final long time3 = 100000L;

        Session(CommandSender sender, UUID id, String textString, String text4) {
            this.commandSender = sender;
            this.text = PermissionTracer.formatString(sender);
            this.id = id;
            this.textString = textString;
            this.text2 = text4 != null && !text4.isEmpty() ? text4.toLowerCase() : null;
        }

        boolean matchesEnabled(String text) {
            return this.text2 == null || text.toLowerCase().contains(this.text2);
        }

        void updateString(
                String text, boolean matches, PermissionTracer.Stage stage2, String text2) {
            PermissionTracer.TraceSession traceSession;
            if ((traceSession = this.traceSessionsByName.get(text)) == null
                    && this.traceSessionsByName.size() < 5000) {
                PermissionTracer.TraceSession traceSession2 =
                        new PermissionTracer.TraceSession(text, PermissionTracer.getString());
                if ((traceSession = this.traceSessionsByName.putIfAbsent(text, traceSession2))
                        == null) {
                    traceSession = traceSession2;
                    traceSession2.enabled = matches;
                    traceSession.stage = stage2;
                    this.queue.add(traceSession2);
                }
            }
            long value = this.counter.incrementAndGet();
            if (traceSession != null) {
                traceSession.counter.incrementAndGet();
                traceSession.enabled = matches;
                traceSession.stage = stage2;
                if (!matches) {
                    traceSession.enabledEnabled = true;
                }
            }
            if (value <= time3) {
                this.strings.add(
                        PermissionTracer.dateTimeFormatter.format(LocalTime.now())
                                + " "
                                + (matches ? "TRUE " : "FALSE")
                                + " "
                                + stage2.name()
                                + " "
                                + text
                                + " ["
                                + (traceSession == null ? "?" : traceSession.textString)
                                + "] ("
                                + text2
                                + ")");
            }
        }
    }

    public enum Source {
        GRANT,
        DEFAULT_RANK,
        PLAYER;

        private static final PermissionTracer.Source[] $VALUES = $values();

        private static PermissionTracer.Source[] $values() {
            return new PermissionTracer.Source[] {GRANT, DEFAULT_RANK, PLAYER};
        }
    }

    public enum Stage {
        TWO_FACTOR("pending 2FA"),
        EXACT_NEGATION("negated node"),
        OP("op"),
        STAR("* node"),
        EXACT("exact node"),
        WILDCARD("wildcard"),
        PLUGIN_DEFAULT("plugin.yml default"),
        CHILD("child permission"),
        BUKKIT_DEFAULT("Bukkit default"),
        UNSET("not set");

        final String label;

        private static final PermissionTracer.Stage[] $VALUES = $values();

        Stage(String label) {
            this.label = label;
        }

        private static PermissionTracer.Stage[] $values() {
            return new PermissionTracer.Stage[] {
                TWO_FACTOR,
                EXACT_NEGATION,
                OP,
                STAR,
                EXACT,
                WILDCARD,
                PLUGIN_DEFAULT,
                CHILD,
                BUKKIT_DEFAULT,
                UNSET
            };
        }
    }

    public static final class TraceSession {

        final String text;

        final String textString;

        final AtomicLong counter = new AtomicLong();

        volatile boolean enabled;

        volatile boolean enabledEnabled;

        volatile PermissionTracer.Stage stage;

        TraceSession(String text, String textString) {
            this.text = text;
            this.textString = textString;
        }
    }
}
