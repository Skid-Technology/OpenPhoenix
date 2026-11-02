package xyz.refinedev.phoenix.packet.impl.profile;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.chat.api.ChatChannelType;
import xyz.refinedev.phoenix.packet.api.Packet;
import xyz.refinedev.phoenix.utils.Color;

import java.util.UUID;

public class ChatChannelMessagePacket implements Packet {

    private UUID sender;

    private String type;

    private String name;

    private String server;

    private String message;

    @Override
    public void onReceive() {
        ChatChannelType chatChannelType;
        if ((chatChannelType =
                        Phoenix.getInstance().getChatHandler().getNullableChannelByName(this.type))
                != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (chatChannelType.canReceiveMessage(this.sender, player)) {
                    player.sendMessage(
                            Color.translate(
                                    chatChannelType
                                            .getFormat()
                                            .replace("<player>", this.name)
                                            .replace("<server>", this.server)
                                            .replace("<message>", this.message)));
                }
            }
        }
    }

    public ChatChannelMessagePacket(
            UUID sender, String type, String name, String server, String message) {
        this.sender = sender;
        this.type = type;
        this.name = name;
        this.server = server;
        this.message = message;
    }

    public ChatChannelMessagePacket() {}
}
