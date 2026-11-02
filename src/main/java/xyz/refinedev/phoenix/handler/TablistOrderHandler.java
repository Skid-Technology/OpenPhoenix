package xyz.refinedev.phoenix.handler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.PacketEventsAPI;
import xyz.refinedev.lib.com.github.retrooper.packetevents.event.PacketListenerAbstract;
import xyz.refinedev.lib.com.github.retrooper.packetevents.event.PacketListenerPriority;
import xyz.refinedev.lib.com.github.retrooper.packetevents.event.PacketSendEvent;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Server;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.player.ClientVersion;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate.Action;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate.PlayerInfo;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.utils.version.ClientVersionUtil;
import xyz.refinedev.phoenix.utils.version.ServerVersion;

import java.util.Iterator;
import java.util.UUID;
import java.util.logging.Level;

public class TablistOrderHandler {

    private static final ServerVersion serverVersion = ServerVersion.V_1_21_2;

    private static final ClientVersion clientVersion = ClientVersion.V_1_21_2;

    private final Phoenix plugin;

    private int countCount;

    private boolean enabled;

    public TablistOrderHandler(Phoenix phoenix) {
        this.plugin = phoenix;
        try {
            PacketEventsAPI packetEventsAPI;
            if ((packetEventsAPI = phoenix.getPacketEvents()) != null
                    && ClientVersionUtil.serverSupports(serverVersion)) {
                packetEventsAPI
                        .getEventManager()
                        .registerListener(new TablistOrderHandler.TabListOrderListener());
            }
        } catch (Throwable throwable) {
            phoenix.getLogger()
                    .log(
                            Level.WARNING,
                            "Tab list ordering will not apply, the packet listener did not register.",
                            throwable);
        }
    }

    public void applyPlayer(Player player) {
        if (this.serverSupports()) {
            try {
                PlayerInfo playerInfo = this.create(player);
                Iterator iterator2 = Bukkit.getOnlinePlayers().iterator();
                while (iterator2.hasNext()) {
                    Player player2;
                    if (ClientVersionUtil.supports(
                            player2 = (Player) iterator2.next(), clientVersion)) {
                        this.updatePlayer(
                                player2,
                                new WrapperPlayServerPlayerInfoUpdate(
                                        Action.UPDATE_LIST_ORDER, new PlayerInfo[] {playerInfo}));
                    }
                }
            } catch (Throwable throwable) {
                this.updateString("an error occurred while sending an order update", throwable);
            }
        }
    }

    public void initialize() {
        if (this.serverSupports()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                this.applyPlayer(player);
            }
        }
    }

    private boolean serverSupports() {
        return !this.enabled
                && SettingsConfig.MODERN.TABLIST_ORDER.ENABLED
                && ClientVersionUtil.serverSupports(serverVersion);
    }

    private PlayerInfo create(Player player) {
        PlayerInfo playerInfo;
        (playerInfo = new PlayerInfo(player.getUniqueId()))
                .setListOrder(this.calculateCount(player.getUniqueId()));
        return playerInfo;
    }

    private int calculateCount(UUID uUID) {
        IProfile profile;
        IRank rank;
        int value =
                (rank =
                                        (profile =
                                                                this.plugin
                                                                        .getProfileHandler()
                                                                        .getCachedProfile(uUID))
                                                        == null
                                                ? null
                                                : profile.getHighestRank())
                                == null
                        ? 0
                        : rank.getPriority();
        return SettingsConfig.MODERN.TABLIST_ORDER.INVERT ? -value : value;
    }

    private void updatePlayer(
            Player player, WrapperPlayServerPlayerInfoUpdate wrapperPlayServerPlayerInfoUpdate) {
        if (ClientVersionUtil.sendPacket(player, wrapperPlayServerPlayerInfoUpdate)) {
            this.countCount = 0;
        } else {
            this.updateString("the packet could not be sent", null);
        }
    }

    private void updateString(String text, Throwable throwable) {
        if (++this.countCount >= 3) {
            this.enabled = true;
            this.plugin
                    .getLogger()
                    .log(
                            Level.WARNING,
                            "Tab list ordering has been disabled after 3 failures: "
                                    + text
                                    + ". Restart the server to try again.",
                            throwable);
        }
    }

    class TabListOrderListener extends PacketListenerAbstract {

        private static final String text = "Failed to apply the tab list order.";

        private TabListOrderListener() {
            super(PacketListenerPriority.LOW);
        }

        public void onPacketSend(PacketSendEvent packetSendEvent) {
            if (packetSendEvent.getPacketType() == Server.PLAYER_INFO_UPDATE) {
                if (TablistOrderHandler.this.serverSupports()) {
                    try {
                        WrapperPlayServerPlayerInfoUpdate wrapperPlayServerPlayerInfoUpdate;
                        if (!(wrapperPlayServerPlayerInfoUpdate =
                                        new WrapperPlayServerPlayerInfoUpdate(packetSendEvent))
                                .getActions()
                                .contains(Action.UPDATE_LIST_ORDER)) {
                            return;
                        }
                        Iterator<PlayerInfo> iterator2 =
                                wrapperPlayServerPlayerInfoUpdate.getEntries().iterator();
                        while (iterator2.hasNext()) {
                            PlayerInfo playerInfo;
                            (playerInfo = (PlayerInfo) iterator2.next())
                                    .setListOrder(
                                            TablistOrderHandler.this.calculateCount(
                                                    playerInfo.getProfileId()));
                        }
                        packetSendEvent.markForReEncode(true);
                    } catch (Throwable throwable) {
                        if (TablistOrderHandler.this.plugin.isDebugMode()) {
                            TablistOrderHandler.this
                                    .plugin
                                    .getLogger()
                                    .log(Level.WARNING, text, throwable);
                        }
                    }
                }
            }
        }
    }
}
