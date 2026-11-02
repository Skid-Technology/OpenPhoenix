package xyz.refinedev.phoenix.handler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.PacketEventsAPI;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.color.Color;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.player.ClientVersion;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.world.waypoint.EmptyWaypointInfo;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.world.waypoint.TrackedWaypoint;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.world.waypoint.Vec3iWaypointInfo;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.world.waypoint.WaypointIcon;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.world.waypoint.WaypointInfo;
import xyz.refinedev.lib.com.github.retrooper.packetevents.util.Either;
import xyz.refinedev.lib.com.github.retrooper.packetevents.util.Vector3i;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWaypoint;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWaypoint.Operation;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.listener.packet.InteractionPacketListener;
import xyz.refinedev.phoenix.utils.version.ClientVersionUtil;
import xyz.refinedev.phoenix.utils.version.ServerVersion;

import java.util.Iterator;
import java.util.UUID;
import java.util.logging.Level;

public class WaypointHandler {

    private static final ServerVersion MINIMUM_SERVER = ServerVersion.V_1_21_6;

    private static final ClientVersion MINIMUM_CLIENT = ClientVersion.V_1_21_6;

    private static final int MAX_FAILURES = 3;

    private final Phoenix plugin;

    private int failures;

    private boolean disabled;

    public WaypointHandler(Phoenix phoenix) {
        this.plugin = phoenix;
        try {
            PacketEventsAPI packetEventsAPI;
            if ((packetEventsAPI = phoenix.getPacketEvents()) != null) {
                packetEventsAPI
                        .getEventManager()
                        .registerListener(new InteractionPacketListener(this));
            }
        } catch (Throwable throwable) {
            phoenix.getLogger()
                    .log(
                            Level.WARNING,
                            "The locator bar filter is unavailable, the packet listener did not register.",
                            throwable);
        }
    }

    public boolean isActive() {
        return !this.disabled
                && SettingsConfig.MODERN.LOCATOR_BAR.ENABLED
                && ClientVersionUtil.serverSupports(MINIMUM_SERVER);
    }

    public boolean shouldHide(Player player, TrackedWaypoint trackedWaypoint) {
        if (SettingsConfig.MODERN.LOCATOR_BAR.DISABLE) {
            return true;
        }
        if (!SettingsConfig.MODERN.LOCATOR_BAR.HIDE_VANISHED) {
            return false;
        }
        if (trackedWaypoint != null && trackedWaypoint.getIdentifier().isLeft()) {
            UUID uUID;
            if ((uUID = (UUID) trackedWaypoint.getIdentifier().getLeft())
                    .equals(player.getUniqueId())) {
                return false;
            }
            Player player2;
            return (player2 = Bukkit.getPlayer(uUID)) == null
                    ? false
                    : !this.plugin.getVisibilityHandler().canSee(player, player2);
        } else {
            return false;
        }
    }

    public void handleVisibilityChange(Player player) {
        if (this.isActive()) {
            if (!SettingsConfig.MODERN.LOCATOR_BAR.DISABLE
                    && SettingsConfig.MODERN.LOCATOR_BAR.HIDE_VANISHED) {
                try {
                    Iterator iterator2 = Bukkit.getOnlinePlayers().iterator();
                    while (iterator2.hasNext()) {
                        Player player2;
                        if (!(player2 = (Player) iterator2.next())
                                        .getUniqueId()
                                        .equals(player.getUniqueId())
                                && ClientVersionUtil.supports(player2, MINIMUM_CLIENT)) {
                            if (!this.plugin.getVisibilityHandler().canSee(player2, player)) {
                                this.send(
                                        player2,
                                        Operation.UNTRACK,
                                        this.playerWaypoint(player, EmptyWaypointInfo.EMPTY));
                            } else if (player2.getWorld().equals(player.getWorld())) {
                                Location location = player.getLocation();
                                Vector3i vector3i =
                                        new Vector3i(
                                                location.getBlockX(),
                                                location.getBlockY(),
                                                location.getBlockZ());
                                this.send(
                                        player2,
                                        Operation.TRACK,
                                        this.playerWaypoint(
                                                player, new Vec3iWaypointInfo(vector3i)));
                            }
                        }
                    }
                } catch (Throwable throwable) {
                    this.fail(
                            "an error occurred while reconciling waypoints after a visibility change",
                            throwable);
                }
            }
        }
    }

    public boolean track(Player player, String text, Location location, String text2) {
        if (!this.isActive() || SettingsConfig.MODERN.LOCATOR_BAR.DISABLE) {
            return false;
        }
        if (!ClientVersionUtil.supports(player, MINIMUM_CLIENT)) {
            return false;
        }
        Color color = this.parseColor(text2);
        Vector3i vector3i =
                new Vector3i(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        TrackedWaypoint trackedWaypoint =
                new TrackedWaypoint(
                        Either.createRight(text),
                        new WaypointIcon(WaypointIcon.ICON_STYLE_DEFAULT, color),
                        new Vec3iWaypointInfo(vector3i));
        return this.send(player, Operation.TRACK, trackedWaypoint);
    }

    public boolean untrack(Player player, String text) {
        if (!this.isActive()) {
            return false;
        }
        if (!ClientVersionUtil.supports(player, MINIMUM_CLIENT)) {
            return false;
        }
        TrackedWaypoint trackedWaypoint =
                new TrackedWaypoint(
                        Either.createRight(text),
                        new WaypointIcon(WaypointIcon.ICON_STYLE_DEFAULT, null),
                        EmptyWaypointInfo.EMPTY);
        return this.send(player, Operation.UNTRACK, trackedWaypoint);
    }

    private TrackedWaypoint playerWaypoint(Player player, WaypointInfo waypointInfo) {
        return new TrackedWaypoint(
                Either.createLeft(player.getUniqueId()),
                new WaypointIcon(WaypointIcon.ICON_STYLE_DEFAULT, null),
                waypointInfo);
    }

    private Color parseColor(String text) {
        if (text != null && !text.isEmpty()) {
            try {
                return new Color(Integer.parseInt(text.replace("#", ""), 16));
            } catch (NumberFormatException numberFormatException) {
                return null;
            }
        } else {
            return null;
        }
    }

    private boolean send(Player player, Operation operation, TrackedWaypoint trackedWaypoint) {
        if (ClientVersionUtil.sendPacket(
                player, new WrapperPlayServerWaypoint(operation, trackedWaypoint))) {
            this.failures = 0;
            return true;
        } else {
            this.fail("the packet could not be sent", null);
            return false;
        }
    }

    private void fail(String text, Throwable throwable) {
        if (++this.failures >= 3) {
            this.disabled = true;
            this.plugin
                    .getLogger()
                    .log(
                            Level.WARNING,
                            "The locator bar filter has been disabled after 3 failures: "
                                    + text
                                    + ". Restart the server to try again.",
                            throwable);
        }
    }
}
