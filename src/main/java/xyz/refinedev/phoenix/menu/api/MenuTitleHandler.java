package xyz.refinedev.phoenix.menu.api;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.PacketEventsAPI;
import xyz.refinedev.lib.com.github.retrooper.packetevents.event.PacketListenerAbstract;
import xyz.refinedev.lib.com.github.retrooper.packetevents.event.PacketListenerPriority;
import xyz.refinedev.lib.com.github.retrooper.packetevents.event.PacketSendEvent;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Server;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
import xyz.refinedev.lib.kyori.adventure.text.Component;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.utils.Color;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class MenuTitleHandler {

    private final Phoenix plugin;

    private final Map<UUID, Component> pending = new ConcurrentHashMap<>();

    private static final String text =
            "Menu titles will lose their fonts, the window listener did not register.";

    public MenuTitleHandler(Phoenix phoenix) {
        this.plugin = phoenix;
        try {
            PacketEventsAPI packetEventsAPI;
            if ((packetEventsAPI = phoenix.getPacketEvents()) != null) {
                packetEventsAPI
                        .getEventManager()
                        .registerListener(new MenuTitleHandler.WindowListener());
            }
        } catch (Throwable throwable) {
            phoenix.getLogger().log(Level.WARNING, text, throwable);
        }
    }

    public void expect(Player player, String text) {
        if (player != null && text != null && !text.isEmpty()) {
            try {
                this.pending.put(player.getUniqueId(), Color.translateComponent(text));
            } catch (Throwable throwable) {
                this.pending.remove(player.getUniqueId());
            }
        }
    }

    public void forget(UUID uUID) {
        this.pending.remove(uUID);
    }

    class WindowListener extends PacketListenerAbstract {

        private static final String text = "Failed to apply a menu title.";

        private WindowListener() {
            super(PacketListenerPriority.LOW);
        }

        public void onPacketSend(PacketSendEvent packetSendEvent) {
            if (packetSendEvent.getPacketType() == Server.OPEN_WINDOW) {
                Object result;
                if ((result = packetSendEvent.getPlayer()) instanceof Player) {
                    Component component;
                    if ((component =
                                    MenuTitleHandler.this.pending.remove(
                                            ((Player) result).getUniqueId()))
                            != null) {
                        try {
                            new WrapperPlayServerOpenWindow(packetSendEvent).setTitle(component);
                        } catch (Throwable throwable) {
                            if (MenuTitleHandler.this.plugin.isDebugMode()) {
                                MenuTitleHandler.this
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
}
