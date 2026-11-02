package xyz.refinedev.phoenix.listener.packet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;

import xyz.refinedev.lib.com.github.retrooper.packetevents.event.PacketListenerAbstract;
import xyz.refinedev.lib.com.github.retrooper.packetevents.event.PacketListenerPriority;
import xyz.refinedev.lib.com.github.retrooper.packetevents.event.PacketSendEvent;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Server;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.player.User;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo.Action;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo.PlayerData;
import xyz.refinedev.phoenix.Phoenix;

import java.util.ArrayList;
import java.util.logging.Level;

public class ClientSettingsPacketListener extends PacketListenerAbstract {

    private final Phoenix plugin;

    private static final String text = "Error whilst intercepting packets for vanish: ";

    public ClientSettingsPacketListener(Phoenix phoenix) {
        super(PacketListenerPriority.HIGHEST);
        this.plugin = phoenix;
    }

    public void onPacketSend(PacketSendEvent packetSendEvent) {
        if (packetSendEvent.getPacketType() == Server.PLAYER_INFO) {
            try {
                WrapperPlayServerPlayerInfo wrapperPlayServerPlayerInfo;
                if ((wrapperPlayServerPlayerInfo = new WrapperPlayServerPlayerInfo(packetSendEvent))
                                        .getAction()
                                == Action.REMOVE_PLAYER
                        || wrapperPlayServerPlayerInfo.getAction() == Action.ADD_PLAYER) {
                    return;
                }
                ArrayList values = new ArrayList(wrapperPlayServerPlayerInfo.getPlayerDataList());
                User user = packetSendEvent.getUser();
                UnmodifiableIterator unmodifiableIterator = ImmutableList.copyOf(values).iterator();
                while (unmodifiableIterator.hasNext()) {
                    PlayerData playerData = (PlayerData) unmodifiableIterator.next();
                    if (!Phoenix.getInstance()
                            .getVisibilityHandler()
                            .canSee(user.getUUID(), playerData.getUser().getUUID())) {
                        values.remove(playerData);
                    }
                }
                if (values.isEmpty()) {
                    packetSendEvent.setCancelled(true);
                }
                wrapperPlayServerPlayerInfo.setPlayerDataList(values);
                packetSendEvent.markForReEncode(true);
            } catch (Exception exception) {
                this.plugin.getLogger().log(Level.SEVERE, text, exception);
            }
        }
    }
}
