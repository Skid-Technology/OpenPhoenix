package xyz.refinedev.phoenix.profile;

import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.dialog.impl.StaffReportsDialog;
import xyz.refinedev.phoenix.dialog.impl.StaffWatchlistDialog;
import xyz.refinedev.phoenix.menu.impl.ReportsMenu;
import xyz.refinedev.phoenix.menu.impl.staff.WatchlistMenu;
import xyz.refinedev.phoenix.report.IReport;
import xyz.refinedev.phoenix.utils.FastStreams;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public final class WatchlistUtils {

    public static void applyPhoenix(Phoenix phoenix, Player player) {
        CompletableFuture<List<IReport>> allReports = phoenix.getReportHandler().getAllReports();
        allReports.whenCompleteAsync(
                (result, result2) -> {
                    result.sort(Comparator.comparingLong(IReport::getIssuedAt).reversed());
                    if (!SettingsConfig.MODERN.DIALOGS.REPORTS
                            || !phoenix.getDialogHandler()
                                    .findPhoenixDialog(StaffReportsDialog.class)
                                    .matchesEnabled(player, null, result)) {
                        Phoenix.getInstance()
                                .getMenuHandler()
                                .openMenuAsync(new ReportsMenu((List<IReport>) result), player);
                    }
                });
    }

    public static void applyPhoenix(Phoenix phoenix, Phoenix phoenix2, Player player) {
        List<String> values;
        if ((values =
                        phoenix2.getDatabaseHandler()
                                .runRedisCommand(result -> result.keys("watchlist:*")))
                == null) {
            values = Collections.emptyList();
        }
        Set<UUID> values2 =
                FastStreams.mapArrayListToSet(
                        values, result -> UUID.fromString(result.replaceAll("watchlist:", "")));
        ConcurrentHashMap entries = new ConcurrentHashMap();
        for (UUID uUID : values2) {
            IProfile profile;
            if ((profile = phoenix2.getProfileHandler().getProfile(uUID)) != null) {
                entries.put(profile, phoenix.getReportHandler().getReportCount(uUID));
            }
        }
        if (!SettingsConfig.MODERN.DIALOGS.WATCHLIST
                || !phoenix.getDialogHandler()
                        .findPhoenixDialog(StaffWatchlistDialog.class)
                        .matchesEnabled(player, entries)) {
            Phoenix.getInstance()
                    .getMenuHandler()
                    .openMenuAsync(new WatchlistMenu(entries), player);
        }
    }

    private WatchlistUtils() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}
