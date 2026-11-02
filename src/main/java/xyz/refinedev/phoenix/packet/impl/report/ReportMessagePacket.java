package xyz.refinedev.phoenix.packet.impl.report;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.packet.api.Packet;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.report.IReport;
import xyz.refinedev.phoenix.report.IReportCategory;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.chat.Clickable;

import java.util.UUID;

public class ReportMessagePacket implements Packet {

    private UUID profileUuid;

    private UUID targetUuid;

    private IReport report;

    private IReportCategory category;

    private String targetServer;

    private String playerServer;

    @Override
    public void onReceive() {
        IProfile profile;
        if ((profile = Phoenix.getInstance().getProfileHandler().getProfile(this.profileUuid))
                != null) {
            IProfile profile2;
            if ((profile2 = Phoenix.getInstance().getProfileHandler().getProfile(this.targetUuid))
                    != null) {
                int value =
                        Phoenix.getInstance()
                                        .getReportHandler()
                                        .getReportCount(profile2.getUniqueId())
                                + 1;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    IProfile profile3;
                    if ((profile3 =
                                            Phoenix.getInstance()
                                                    .getProfileHandler()
                                                    .getCachedProfile(player.getUniqueId()))
                                    != null
                            && player.hasPermission("core.staff")
                            && profile3.isSetting("reports")) {
                        this.render(player, profile, profile2, value).sendToPlayer(player);
                    }
                }
                this.render(Bukkit.getConsoleSender(), profile, profile2, value)
                        .sendToPlayer(Bukkit.getConsoleSender());
            }
        }
    }

    private Clickable render(CommandSender sender, IProfile profile, IProfile profile2, int value) {
        Clickable clickable;
        (clickable = new Clickable())
                .add(
                        LocaleUtil.tr(sender, MessageConfig.STAFF.REPORT_ALERT)
                                .replace("<player>", profile.getNameWithColorNoDisguise())
                                .replace("<target>", profile2.getNameWithColorNoDisguise())
                                .replace("<reason>", this.report.getNiceReason())
                                .replace("<player_server>", this.playerServer)
                                .replace("<target_server>", this.targetServer)
                                .replace("<reportCount>", String.valueOf(value))
                                .replace(
                                        "<disguised>",
                                        profile2.getDisguiseData().isDisguised()
                                                ? LocaleUtil.tr(
                                                        sender,
                                                        MessageConfig.STAFF.DISGUISED_SUFFIX)
                                                : ""),
                        "&aClick to jump to " + profile.getNameWithColor() + "&a's server.",
                        "/jump " + profile.getName());
        return clickable;
    }

    public ReportMessagePacket(
            UUID profileUuid,
            UUID targetUuid,
            IReport report2,
            IReportCategory reportCategory,
            String targetServer,
            String playerServer) {
        this.profileUuid = profileUuid;
        this.targetUuid = targetUuid;
        this.report = report2;
        this.category = reportCategory;
        this.targetServer = targetServer;
        this.playerServer = playerServer;
    }

    public ReportMessagePacket() {}
}
