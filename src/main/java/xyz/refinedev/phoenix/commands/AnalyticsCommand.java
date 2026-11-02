package xyz.refinedev.phoenix.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Help;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.menu.impl.AnalyticsMenu;
import xyz.refinedev.phoenix.server.IServerData;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.MetricsReporter;
import xyz.refinedev.phoenix.utils.Utilities;
import xyz.refinedev.phoenix.utils.general.Tasks;

import java.util.Map;
import java.util.Map.Entry;

@Help
@Register(name = "analytics", aliases = "serveranalytics")
@Requires("core.command.analytics")
public class AnalyticsCommand {

    private final Phoenix plugin;

    @Command(name = "total", async = true)
    public void total(@Sender CommandSender sender) {
        MetricsReporter.applyString("analytics_command");
        sender.sendMessage(Color.translate(LocaleUtil.tr(sender, MessageConfig.ANALYTICS.LOADING)));
        Tasks.runAsync(
                () -> {
                    int totalPeakPlayers = this.plugin.getAnalyticsHandler().getTotalPeakPlayers();
                    int totalPeakPlayers2 =
                            this.plugin.getAnalyticsHandler().getTotalPeakPlayers(1);
                    int peakPlayers = this.plugin.getAnalyticsHandler().getPeakPlayers(7);
                    long logins = this.plugin.getAnalyticsHandler().getLogins(1);
                    long logins2 = this.plugin.getAnalyticsHandler().getLogins(2);
                    long logins3 = this.plugin.getAnalyticsHandler().getLogins(8);
                    long logins4 = this.plugin.getAnalyticsHandler().getLogins(31);
                    long totalServerSwitches =
                            this.plugin.getAnalyticsHandler().getTotalServerSwitches(0);
                    long totalServerSwitches2 =
                            this.plugin.getAnalyticsHandler().getTotalServerSwitches(1);
                    Map<String, String> retentionPlaceholders =
                            this.plugin.getAnalyticsHandler().getRetentionPlaceholders();
                    for (String text : LocaleUtil.tr(sender, MessageConfig.ANALYTICS.MESSAGE)) {
                        for (Entry<String, String> entry : retentionPlaceholders.entrySet()) {
                            text =
                                    text.replace(
                                            (CharSequence) entry.getKey(),
                                            (CharSequence) entry.getValue());
                        }
                        sender.sendMessage(
                                Color.translate(
                                        text.replace(
                                                        "<online>",
                                                        String.valueOf(
                                                                this.plugin
                                                                        .getNetworkHandler()
                                                                        .getAllOnline()))
                                                .replace("<peak>", String.valueOf(totalPeakPlayers))
                                                .replace(
                                                        "<peakYesterday>",
                                                        String.valueOf(totalPeakPlayers2))
                                                .replace("<peakWeek>", String.valueOf(peakPlayers))
                                                .replace(
                                                        "<peakYesterdayDiff>",
                                                        totalPeakPlayers >= totalPeakPlayers2
                                                                ? "&a+"
                                                                        + (totalPeakPlayers
                                                                                - totalPeakPlayers2)
                                                                : "&c"
                                                                        + (totalPeakPlayers
                                                                                - totalPeakPlayers2))
                                                .replace(
                                                        "<peakWeekDiff>",
                                                        totalPeakPlayers >= peakPlayers
                                                                ? "&a+"
                                                                        + (totalPeakPlayers
                                                                                - peakPlayers)
                                                                : "&c"
                                                                        + (totalPeakPlayers
                                                                                - peakPlayers))
                                                .replace(
                                                        "<playtime>",
                                                        this.plugin
                                                                .getAnalyticsHandler()
                                                                .getTotalAveragePlayTime(0))
                                                .replace(
                                                        "<playtimeYesterday>",
                                                        this.plugin
                                                                .getAnalyticsHandler()
                                                                .getTotalAveragePlayTime(1))
                                                .replace(
                                                        "<playtimeWeek>",
                                                        this.plugin
                                                                .getAnalyticsHandler()
                                                                .getAveragePlayTime(7))
                                                .replace(
                                                        "<playtimeYesterdayDiff>",
                                                        this.plugin
                                                                .getAnalyticsHandler()
                                                                .getTotalAveragePlayTimeDifference(
                                                                        1))
                                                .replace(
                                                        "<playtimeWeekDiff>",
                                                        this.plugin
                                                                .getAnalyticsHandler()
                                                                .getTotalAveragePlayTimeDifference(
                                                                        7))
                                                .replace("<logins>", Utilities.formatNumber(logins))
                                                .replace(
                                                        "<loginsYesterday>",
                                                        Utilities.formatNumber(logins2))
                                                .replace(
                                                        "<loginsWeek>",
                                                        Utilities.formatNumber(logins3))
                                                .replace(
                                                        "<loginsMonth>",
                                                        Utilities.formatNumber(logins4))
                                                .replace(
                                                        "<loginsYesterdayDiff>",
                                                        logins >= logins2
                                                                ? "&a+"
                                                                        + Utilities.formatNumber(
                                                                                logins - logins2)
                                                                : "&c"
                                                                        + Utilities
                                                                                .formatNumberNegative(
                                                                                        logins
                                                                                                - logins2))
                                                .replace(
                                                        "<switches>",
                                                        Utilities.formatNumber(totalServerSwitches))
                                                .replace(
                                                        "<switchesYesterday>",
                                                        Utilities.formatNumber(
                                                                totalServerSwitches2))));
                    }
                });
    }

    @Command(name = "view")
    public void view(@Sender Player player, @Named("server") IServerData serverData) {
        this.plugin.getMenuHandler().openMenu(new AnalyticsMenu(serverData), player);
    }

    public AnalyticsCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
