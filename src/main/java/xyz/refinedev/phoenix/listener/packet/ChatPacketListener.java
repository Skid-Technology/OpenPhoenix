package xyz.refinedev.phoenix.listener.packet;

import xyz.refinedev.lib.com.github.retrooper.packetevents.event.PacketListenerAbstract;
import xyz.refinedev.lib.com.github.retrooper.packetevents.event.PacketListenerPriority;
import xyz.refinedev.lib.com.github.retrooper.packetevents.event.PacketSendEvent;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.chat.ChatTypes;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Server;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSystemChatMessage;
import xyz.refinedev.lib.kyori.adventure.text.Component;
import xyz.refinedev.lib.kyori.adventure.text.TextComponent;
import xyz.refinedev.lib.kyori.adventure.text.event.ClickEvent;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.utils.LimitedSizeList;
import xyz.refinedev.phoenix.utils.Pair;
import xyz.refinedev.phoenix.utils.version.ServerVersion;
import xyz.refinedev.phoenix.utils.version.VersionUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class ChatPacketListener extends PacketListenerAbstract {

    private static final String text = "/deletemessage ";

    private final Phoenix plugin;

    public ChatPacketListener(Phoenix phoenix) {
        super(PacketListenerPriority.HIGHEST);
        this.plugin = phoenix;
    }

    public static Component findComponent(UUID uUID) {
        return Component.text("").clickEvent(ClickEvent.runCommand("/deletemessage " + uUID));
    }

    private static boolean matchesEnabled(Component component) {
        return component instanceof TextComponent
                && ((TextComponent) component).content().isEmpty()
                && component.children().isEmpty();
    }

    public void onPacketSend(PacketSendEvent packetSendEvent) {
        if (packetSendEvent.getPacketType() == Server.CHAT_MESSAGE
                || packetSendEvent.getPacketType() == Server.SYSTEM_CHAT_MESSAGE) {
            if (!VersionUtil.getVersion().isOlderThan(ServerVersion.V_1_19)
                    || packetSendEvent.getPacketType() != Server.SYSTEM_CHAT_MESSAGE) {
                UUID uUID = packetSendEvent.getUser().getUUID();
                try {
                    WrapperPlayServerChatMessage wrapperPlayServerChatMessage = null;
                    WrapperPlayServerSystemChatMessage wrapperPlayServerSystemChatMessage = null;
                    Component component;
                    if (packetSendEvent.getPacketType() == Server.CHAT_MESSAGE) {
                        component =
                                (wrapperPlayServerChatMessage =
                                                new WrapperPlayServerChatMessage(packetSendEvent))
                                        .getMessage()
                                        .getChatContent();
                        if (wrapperPlayServerChatMessage.getMessage().getType()
                                == ChatTypes.GAME_INFO) {
                            return;
                        }
                    } else {
                        component =
                                (wrapperPlayServerSystemChatMessage =
                                                new WrapperPlayServerSystemChatMessage(
                                                        packetSendEvent))
                                        .getMessage();
                        if (wrapperPlayServerSystemChatMessage.isOverlay()) {
                            return;
                        }
                    }
                    UUID uUID2 = null;
                    Component component2 = null;
                    Iterator<Component> iterator2 = component.children().iterator();
                    while (iterator2.hasNext()) {
                        Component component3;
                        String text;
                        if ((component3 = (Component) iterator2.next()).clickEvent() != null
                                && (text = component3.clickEvent().value())
                                        .contains("/deletemessage ")) {
                            uUID2 = UUID.fromString(text.replace("/deletemessage ", ""));
                            if (matchesEnabled(component3)) {
                                component2 = component3;
                            }
                            break;
                        }
                    }
                    if (component2 != null) {
                        ArrayList values;
                        (values = new ArrayList(component.children())).remove(component2);
                        Component component4 = component.children(values);
                        if (wrapperPlayServerChatMessage != null) {
                            wrapperPlayServerChatMessage.getMessage().setChatContent(component4);
                        } else {
                            wrapperPlayServerSystemChatMessage.setMessage(component4);
                        }
                        packetSendEvent.markForReEncode(true);
                    }
                    Map<UUID, LimitedSizeList<Pair<UUID, Component>>> entries;
                    if (!(entries = this.plugin.getChatHandler().getLastChatMessages())
                            .containsKey(uUID)) {
                        entries.put(uUID, new LimitedSizeList(100));
                    }
                    ((LimitedSizeList) entries.get(uUID)).add(Pair.of(uUID2, component));
                } catch (Throwable throwable) {
                    if (this.plugin.isDebugMode()) {
                        this.plugin
                                .getLogger()
                                .log(
                                        Level.SEVERE,
                                        "Error whilst intercepting packets for chat packet listener: ",
                                        throwable);
                    }
                }
            }
        }
    }
}
