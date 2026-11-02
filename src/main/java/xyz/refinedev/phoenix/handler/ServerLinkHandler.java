package xyz.refinedev.phoenix.handler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.player.ClientVersion;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.common.server.WrapperCommonServerServerLinks.KnownType;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.common.server.WrapperCommonServerServerLinks.ServerLink;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerServerLinks;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.scope.ScopeExpression;
import xyz.refinedev.phoenix.scope.ScopeParser;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.version.ClientVersionUtil;
import xyz.refinedev.phoenix.utils.version.ServerVersion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

public class ServerLinkHandler {

    public static final String text = "server-links";

    private static final ServerVersion serverVersion = ServerVersion.V_1_21;

    private static final ClientVersion clientVersion = ClientVersion.V_1_21;

    private final Phoenix plugin;

    private int countCount;

    private boolean enabled;

    public ServerLinkHandler(Phoenix phoenix) {
        this.plugin = phoenix;
    }

    public void setPlayer(Player player) {
        if (!this.enabled && SettingsConfig.MODERN.SERVER_LINKS.ENABLED) {
            if (ClientVersionUtil.serverSupports(serverVersion)) {
                if (ClientVersionUtil.supports(player, clientVersion)) {
                    try {
                        List<ServerLink> values;
                        if ((values = this.findList(player)).isEmpty()) {
                            return;
                        }
                        if (ClientVersionUtil.sendPacket(
                                player, new WrapperPlayServerServerLinks(values))) {
                            this.countCount = 0;
                        } else {
                            this.updateString("the packet could not be sent", null);
                        }
                    } catch (Throwable throwable) {
                        this.updateString("an error occurred while building the links", throwable);
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

    private List<ServerLink> findList(Player player) {
        List<ScopeExpression> values =
                ScopeParser.addAll(
                        ScopeParser.findList(SettingsConfig.MODERN.SERVER_LINKS.LINKS),
                        ScopeParser.findList(this.formatString(player)));
        ArrayList<ServerLink> values2 = new ArrayList<>();
        Iterator iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            ScopeExpression scopeExpression;
            String text;
            if (!(text =
                            Color.replace(
                                            (scopeExpression = (ScopeExpression) iterator2.next())
                                                    .getText2())
                                    .trim())
                    .isEmpty()) {
                if (scopeExpression.isEnabled()) {
                    values2.add(
                            new ServerLink(
                                    Color.translateComponent(scopeExpression.getTextString()),
                                    text));
                } else {
                    try {
                        values2.add(
                                new ServerLink(KnownType.valueOf(scopeExpression.getText()), text));
                    } catch (IllegalArgumentException illegalArgumentException) {
                        this.plugin
                                .getLogger()
                                .warning(
                                        "Unknown server link type '"
                                                + scopeExpression.getText()
                                                + "', skipping it.");
                    }
                }
            }
        }
        return values2;
    }

    private String formatString(Player player) {
        IProfile profile;
        if ((profile = this.plugin.getProfileHandler().getCachedProfile(player.getUniqueId()))
                == null) {
            return null;
        }
        IRank rank;
        return (rank = profile.getHighestRank()) == null ? null : rank.getMetadata("server-links");
    }

    private void updateString(String text, Throwable throwable) {
        if (++this.countCount >= 3) {
            this.enabled = true;
            this.plugin
                    .getLogger()
                    .log(
                            Level.WARNING,
                            "Server links have been disabled after 3 failures: "
                                    + text
                                    + ". Restart the server to try again.",
                            throwable);
        }
    }
}
