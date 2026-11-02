package xyz.refinedev.phoenix.listener.packet;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.event.PacketListenerAbstract;
import xyz.refinedev.lib.com.github.retrooper.packetevents.event.PacketListenerPriority;
import xyz.refinedev.lib.com.github.retrooper.packetevents.event.PacketSendEvent;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Server;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWaypoint;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWaypoint.Operation;
import xyz.refinedev.phoenix.handler.WaypointHandler;

public class InteractionPacketListener extends PacketListenerAbstract {

    private final WaypointHandler waypointHandler;

    public InteractionPacketListener(WaypointHandler waypointHandler2) {
        super(PacketListenerPriority.NORMAL);
        this.waypointHandler = waypointHandler2;
    }

    public void onPacketSend(PacketSendEvent packetSendEvent) {
        if (packetSendEvent.getPacketType() == Server.WAYPOINT) {
            if (this.waypointHandler.isActive()) {
                Object result;
                if ((result = packetSendEvent.getPlayer()) instanceof Player) {
                    WrapperPlayServerWaypoint wrapperPlayServerWaypoint;
                    if ((wrapperPlayServerWaypoint = new WrapperPlayServerWaypoint(packetSendEvent))
                                    .getOperation()
                            != Operation.UNTRACK) {
                        if (this.waypointHandler.shouldHide(
                                (Player) result, wrapperPlayServerWaypoint.getWaypoint())) {
                            packetSendEvent.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
}
