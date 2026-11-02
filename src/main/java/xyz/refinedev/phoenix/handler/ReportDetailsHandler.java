package xyz.refinedev.phoenix.handler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.player.ClientVersion;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCustomReportDetails;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.version.ClientVersionUtil;
import xyz.refinedev.phoenix.utils.version.ServerVersion;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

public class ReportDetailsHandler {

    private static final ServerVersion serverVersion = ServerVersion.V_1_21;

    private static final ClientVersion clientVersion = ClientVersion.V_1_21;

    private final Phoenix plugin;

    private int count4;

    private boolean enabled;

    public ReportDetailsHandler(Phoenix phoenix) {
        this.plugin = phoenix;
    }

    public void setPlayer(Player player) {
        if (!this.enabled && SettingsConfig.MODERN.REPORT_DETAILS.ENABLED) {
            if (ClientVersionUtil.serverSupports(serverVersion)) {
                if (ClientVersionUtil.supports(player, clientVersion)) {
                    try {
                        Map<String, String> entries;
                        if ((entries = this.getMap()).isEmpty()) {
                            return;
                        }
                        if (ClientVersionUtil.sendPacket(
                                player, new WrapperPlayServerCustomReportDetails(entries))) {
                            this.count4 = 0;
                        } else {
                            this.updateString("the packet could not be sent", null);
                        }
                    } catch (Throwable throwable) {
                        this.updateString(
                                "an error occurred while building the details", throwable);
                    }
                }
            }
        }
    }

    public void initialize() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.setPlayer(player);
        }
    }

    private Map<String, String> getMap() {
        LinkedHashMap<String, String> entries = new LinkedHashMap<>();
        Iterator<String> iterator2 = SettingsConfig.MODERN.REPORT_DETAILS.DETAILS.iterator();
        while (iterator2.hasNext()) {
            String text;
            if ((text = (String) iterator2.next()) != null && !text.isEmpty()) {
                int value;
                if ((value = text.indexOf(124)) <= 0) {
                    this.plugin
                            .getLogger()
                            .warning("Report detail '" + text + "' is not KEY|VALUE, skipping it.");
                } else {
                    if (entries.size() >= 32) {
                        this.plugin
                                .getLogger()
                                .warning(
                                        "More than 32 report details are configured, the rest are skipped.");
                        break;
                    }
                    String text2 = text.substring(0, value).trim();
                    String text3 =
                            ChatColor.stripColor(
                                            Color.translate(
                                                    this.formatString(text.substring(value + 1))))
                                    .trim();
                    if (!text2.isEmpty() && !text3.isEmpty()) {
                        entries.put(this.substring(text2, 128), this.substring(text3, 4096));
                    }
                }
            }
        }
        return entries;
    }

    private String formatString(String text) {
        String displayName = this.plugin.getNetworkHandler().getDisplayName();
        return text.replace("<server>", displayName != null ? displayName : "");
    }

    private String substring(String text, int value) {
        return text.length() <= value ? text : text.substring(0, value);
    }

    private void updateString(String text, Throwable throwable) {
        if (++this.count4 >= 3) {
            this.enabled = true;
            this.plugin
                    .getLogger()
                    .log(
                            Level.WARNING,
                            "Report details have been disabled after 3 failures: "
                                    + text
                                    + ". Restart the server to try again.",
                            throwable);
        }
    }
}
