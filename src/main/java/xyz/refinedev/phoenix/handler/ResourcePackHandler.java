package xyz.refinedev.phoenix.handler;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.PacketEventsAPI;
import xyz.refinedev.lib.com.github.retrooper.packetevents.event.PacketListenerAbstract;
import xyz.refinedev.lib.com.github.retrooper.packetevents.event.PacketListenerPriority;
import xyz.refinedev.lib.com.github.retrooper.packetevents.event.PacketReceiveEvent;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Client;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.player.ClientVersion;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientResourcePackStatus;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientResourcePackStatus.Result;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerResourcePackRemove;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerResourcePackSend;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.general.Tasks;
import xyz.refinedev.phoenix.utils.version.ClientVersionUtil;
import xyz.refinedev.phoenix.utils.version.ServerVersion;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class ResourcePackHandler {

    private static final ServerVersion serverVersion = ServerVersion.V_1_20_3;

    private static final ClientVersion clientVersion = ClientVersion.V_1_20_3;

    private static final String text = "\\|";

    private final Phoenix plugin;

    private final Map<UUID, Set<UUID>> uUIDsById = new ConcurrentHashMap<>();

    public ResourcePackHandler(Phoenix phoenix) {
        this.plugin = phoenix;
        try {
            PacketEventsAPI packetEventsAPI;
            if ((packetEventsAPI = phoenix.getPacketEvents()) != null) {
                packetEventsAPI
                        .getEventManager()
                        .registerListener(new ResourcePackHandler.ClientLinksListener());
            }
        } catch (Throwable throwable) {
            phoenix.getLogger()
                    .log(
                            Level.WARNING,
                            "Resource pack enforcement is unavailable, the status listener did not register.",
                            throwable);
        }
    }

    public void applyUUID(UUID uUID) {
        this.uUIDsById.remove(uUID);
    }

    private void applyPlayer(Player player, Result result) {
        if (result == Result.DECLINED
                || result == Result.FAILED_DOWNLOAD
                || result == Result.INVALID_URL) {
            if (SettingsConfig.MODERN.RESOURCE_PACKS.LOG_REFUSALS) {
                this.plugin
                        .getLogger()
                        .info(
                                player.getName()
                                        + " refused a resource pack: "
                                        + result.name().toLowerCase());
            }
            if (SettingsConfig.MODERN.RESOURCE_PACKS.KICK_ON_REFUSAL) {
                Tasks.run(
                        () -> {
                            if (player.isOnline()) {
                                player.kickPlayer(
                                        Color.translate(
                                                String.join(
                                                        "\n",
                                                        SettingsConfig.MODERN
                                                                .RESOURCE_PACKS
                                                                .REFUSAL_KICK_MESSAGE)));
                            }
                        });
            }
        }
    }

    public void applyPlayer(Player player) {
        if (SettingsConfig.MODERN.RESOURCE_PACKS.ENABLED) {
            if (this.matchesEnabled(player)) {
                try {
                    for (String text : SettingsConfig.MODERN.RESOURCE_PACKS.PACKS) {
                        this.applyPlayer(player, text);
                    }
                } catch (Throwable throwable) {
                    this.plugin
                            .getLogger()
                            .log(Level.WARNING, "Failed to send resource packs.", throwable);
                }
            }
        }
    }

    public UUID findUUID(Player player, String text, String text2, boolean matches, String text3) {
        if (!this.matchesEnabled(player)) {
            return null;
        }
        UUID uUID = this.findUUID(text);
        if (!ClientVersionUtil.sendPacket(
                player,
                new WrapperPlayServerResourcePackSend(
                        uUID,
                        text,
                        text2,
                        matches,
                        text3 != null && !text3.isEmpty()
                                ? Color.translateComponent(text3)
                                : null))) {
            return null;
        }
        this.uUIDsById
                .computeIfAbsent(player.getUniqueId(), result -> ConcurrentHashMap.newKeySet())
                .add(uUID);
        return uUID;
    }

    public boolean matchesEnabled(Player player, UUID uUID) {
        if (!this.matchesEnabled(player)) {
            return false;
        }
        if (!ClientVersionUtil.sendPacket(player, new WrapperPlayServerResourcePackRemove(uUID))) {
            return false;
        }
        Set<UUID> values;
        if ((values = this.uUIDsById.get(player.getUniqueId())) != null) {
            if (uUID == null) {
                values.clear();
            } else {
                values.remove(uUID);
            }
        }
        return true;
    }

    public UUID findUUID(String text) {
        return UUID.nameUUIDFromBytes(text.trim().getBytes(StandardCharsets.UTF_8));
    }

    public boolean matchesEnabled(Player player) {
        return ClientVersionUtil.serverSupports(serverVersion)
                && ClientVersionUtil.supports(player, clientVersion);
    }

    private void applyPlayer(Player player, String text) {
        if (text != null && !text.trim().isEmpty()) {
            String[] texts;
            if ((texts = text.split("\\|", 4)).length < 2) {
                this.plugin
                        .getLogger()
                        .warning(
                                "Resource pack entry '"
                                        + text
                                        + "' needs at least a url and a hash, skipping it.");
            } else {
                String text2 = texts[0].trim();
                String text3 = texts[1].trim();
                if (!text2.isEmpty() && !text3.isEmpty()) {
                    boolean matches = texts.length > 2 && Boolean.parseBoolean(texts[2].trim());
                    String text4 = texts.length > 3 ? texts[3].trim() : "";
                    this.findUUID(player, text2, text3, matches, text4);
                }
            }
        }
    }

    class ClientLinksListener extends PacketListenerAbstract {

        private ClientLinksListener() {
            super(PacketListenerPriority.NORMAL);
        }

        public void onPacketReceive(PacketReceiveEvent packetReceiveEvent) {
            if (packetReceiveEvent.getPacketType() == Client.RESOURCE_PACK_STATUS) {
                Object result;
                if ((result = packetReceiveEvent.getPlayer()) instanceof Player) {
                    Player player = (Player) result;
                    WrapperPlayClientResourcePackStatus wrapperPlayClientResourcePackStatus =
                            new WrapperPlayClientResourcePackStatus(packetReceiveEvent);
                    Set<UUID> values;
                    if ((values = ResourcePackHandler.this.uUIDsById.get(player.getUniqueId()))
                                    != null
                            && wrapperPlayClientResourcePackStatus.getPackId() != null
                            && values.contains(wrapperPlayClientResourcePackStatus.getPackId())) {
                        packetReceiveEvent.setCancelled(true);
                        ResourcePackHandler.this.applyPlayer(
                                player, wrapperPlayClientResourcePackStatus.getResult());
                    }
                }
            }
        }
    }
}
