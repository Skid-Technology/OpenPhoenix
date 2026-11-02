package xyz.refinedev.phoenix.commands;

import org.bukkit.command.CommandSender;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Help;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Option;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.login.ILogin;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.VoidReason;
import xyz.refinedev.phoenix.report.IReportStatistics;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.SessionTimeline;

import java.util.Iterator;
import java.util.List;

@Help
@Register(
        name = "staffactivity",
        aliases = {"staffstats", "sa"})
@Requires("core.command.staffactivity")
public class StaffactivityCommand {

    private final Phoenix plugin;

    @Command(name = "", async = true)
    public void execute(
            @Sender CommandSender sender, @Option({"disguise", "multiple"}) IProfile profile) {
        List<ILogin> logins = this.plugin.getLoginHandler().getLogins(profile.getUuid());
        List<xyz.refinedev.phoenix.profile.hop.IServerHop> hops =
                this.plugin.getServerHopHandler().getHops(profile.getUuid());
        long playTime = profile.getPlayTime(logins);
        List<IPunishment> values = profile.getPunishmentsIssued().join();
        List<IPunishment> values2 =
                this.plugin.getPunishmentHandler().findByRemovedBy(profile.getUuid()).join();
        int value = 0;
        int value2 = 0;
        int value3 = 0;
        int value4 = 0;
        int value5 = 0;
        for (IPunishment punishment : values) {
            switch (punishment.getPunishmentType()) {
                case BAN:
                    value++;
                    break;
                case MUTE:
                    value2++;
                    break;
                case KICK:
                    value3++;
                    break;
                case WARN:
                    value4++;
                    break;
                case BLACKLIST:
                    value5++;
            }
        }
        int value6 = 0;
        Iterator iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            IPunishment punishment2;
            VoidReason voidReason;
            if ((punishment2 = (IPunishment) iterator2.next()).isVoided()
                    && ((voidReason = punishment2.getVoidReason()) == null
                            || voidReason.isStaffFault())) {
                value6++;
            }
        }
        String text =
                values.isEmpty() ? "N/A" : String.format("%.1f%%", value6 * 100.0 / values.size());
        IReportStatistics reportStatistics;
        int handledBy =
                (reportStatistics =
                                this.plugin
                                        .getReportHandler()
                                        .getStatistics(SettingsConfig.REPORTS.ACTIVITY_WINDOW)
                                        .join())
                        .getHandledBy(profile.getUuid());
        long averageResolutionBy = reportStatistics.getAverageResolutionBy(profile.getUuid());
        ILogin mostRecentLogin = profile.getMostRecentLogin(logins);
        boolean online = this.plugin.getNetworkHandler().isOnline(profile.getUuid());
        String text2 =
                mostRecentLogin != null ? DateUtils.getDate(mostRecentLogin.getLogin()) : "N/A";
        String text3 =
                online
                        ? "Currently Online"
                        : (mostRecentLogin != null && mostRecentLogin.hasLoggedOut()
                                ? DateUtils.getDate(mostRecentLogin.getLogout())
                                : "N/A");
        long value7 = SessionTimeline.calculateTime(hops);
        String text4 = SessionTimeline.formatString(logins, hops);
        String text5 = DateUtils.formatHourRange(SessionTimeline.calculateCount(logins, hops));
        for (String text6 : LocaleUtil.tr(sender, MessageConfig.STAFF_ACTIVITY.FORMAT)) {
            sender.sendMessage(
                    Color.translate(
                            text6.replace("<player>", profile.getNameWithColorNoDisguise())
                                    .replace("<playtime>", DateUtils.formatTimeMillis(playTime))
                                    .replace("<punishments>", String.valueOf(values.size()))
                                    .replace("<voided>", String.valueOf(value6))
                                    .replace("<voidrate>", text)
                                    .replace("<bans>", String.valueOf(value))
                                    .replace("<mutes>", String.valueOf(value2))
                                    .replace("<kicks>", String.valueOf(value3))
                                    .replace("<warns>", String.valueOf(value4))
                                    .replace("<blacklists>", String.valueOf(value5))
                                    .replace("<removed>", String.valueOf(values2.size()))
                                    .replace("<reportshandled>", String.valueOf(handledBy))
                                    .replace(
                                            "<reportspeed>",
                                            averageResolutionBy < 0L
                                                    ? "N/A"
                                                    : DateUtils.formatTimeMillis(
                                                            averageResolutionBy))
                                    .replace(
                                            "<afk>",
                                            DateUtils.formatTimeMillis(profile.getAfkTime()))
                                    .replace("<switches>", String.valueOf(value7))
                                    .replace("<activeserver>", text4 != null ? text4 : "N/A")
                                    .replace("<peakonline>", text5)
                                    .replace("<logins>", String.valueOf(logins.size()))
                                    .replace("<lastlogin>", text2)
                                    .replace("<lastleave>", text3)));
        }
    }

    public StaffactivityCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
