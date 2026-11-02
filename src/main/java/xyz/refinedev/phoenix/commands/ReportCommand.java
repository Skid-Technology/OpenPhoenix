package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Optional;
import xyz.refinedev.lib.commands.annotation.parameter.Text;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.commands.api.annotations.PlayerNames;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.dialog.impl.StaffReportDialog;
import xyz.refinedev.phoenix.menu.impl.ReportMenu;
import xyz.refinedev.phoenix.menu.impl.report.ReportCategoryMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.report.IReportCategory;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;

@Register(name = "report")
@Requires("core.command.report")
public class ReportCommand {

    private final Phoenix plugin;

    @Command(name = "", async = true)
    public void execute(
            @Sender Player player,
            @PlayerNames @Named("player") String text,
            @Optional @Named("reason") @Text String text2) {
        IProfile profile = this.plugin.getProfileHandler().getProfile(player.getUniqueId());
        IProfile profile2;
        if ((profile2 = BukkitAPI.getProfileByName(text)) == null) {
            throw new ExitMessage(
                    LocaleUtil.tr(player, MessageConfig.ERROR.PROFILE_DOESNT_EXIST)
                            .replace("<player>", text));
        }
        boolean matches =
                profile2.getDisguiseData().isDisguised()
                        && profile2.getDisguiseData().getDisguiseName().equalsIgnoreCase(text);
        if (this.plugin.getReportHandler().isReportMute(player.getUniqueId())) {
            throw new ExitMessage(LocaleUtil.tr(player, MessageConfig.REPORT.MUTED));
        }
        if (profile.hasLocalCooldown("report") && !player.hasPermission("core.cooldowns.bypass")) {
            throw new ExitMessage(
                    LocaleUtil.tr(player, MessageConfig.COOLDOWNS.REPORT_COOLDOWN)
                            .replace(
                                    "<remaining>",
                                    DateUtils.formatTimeMillis(
                                            profile.getLocalRemaining("report"))));
        }
        if (profile2.getName().equalsIgnoreCase(player.getName())) {
            throw new ExitMessage(LocaleUtil.tr(player, MessageConfig.REPORT.CANT_REPORT_SELF));
        }
        if (!SettingsConfig.REPORTS.REPORT_STAFF
                && profile2.isStaff()
                && !profile2.getDisguiseData().isDisguised()) {
            throw new ExitMessage(LocaleUtil.tr(player, MessageConfig.REPORT.CANT_REPORT_STAFF));
        }
        if (text2 != null && !text2.trim().isEmpty()) {
            if (!SettingsConfig.REPORTS.CUSTOM_REASON.ENABLED) {
                throw new ExitMessage(LocaleUtil.tr(player, MessageConfig.REPORT.CUSTOM_DISABLED));
            }
            IReportCategory reportCategory;
            if ((reportCategory = this.plugin.getReportHandler().getReportCategory()) == null) {
                this.plugin
                        .getLogger()
                        .warning(
                                "Custom report reasons are on, but no category is named '"
                                        + SettingsConfig.REPORTS.CUSTOM_REASON.CATEGORY
                                        + "'.");
                throw new ExitMessage(
                        LocaleUtil.tr(player, MessageConfig.REPORT.CUSTOM_MISSING_CATEGORY));
            }
            this.plugin.getReportHandler().sendReport(player, profile2, reportCategory, text2);
        } else if (!SettingsConfig.MODERN.DIALOGS.REPORT
                || !this.plugin
                        .getDialogHandler()
                        .findPhoenixDialog(StaffReportDialog.class)
                        .matchesEnabled(player, profile2)) {
            if (MenuConfig.REPORT_MENU.CUSTOM_LAYOUT) {
                Phoenix.getInstance()
                        .getMenuHandler()
                        .openMenuSync(new ReportCategoryMenu(profile2, matches), player);
            } else {
                Phoenix.getInstance()
                        .getMenuHandler()
                        .openMenuSync(new ReportMenu(profile2, matches), player);
            }
        }
    }

    public ReportCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
