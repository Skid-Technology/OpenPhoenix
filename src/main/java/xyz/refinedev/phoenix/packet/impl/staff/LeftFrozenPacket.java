package xyz.refinedev.phoenix.packet.impl.staff;

import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.packet.api.Packet;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.Utilities;
import xyz.refinedev.phoenix.utils.chat.Clickable;

import java.util.stream.Stream;

public class LeftFrozenPacket implements Packet {

    private String name;

    private String displayName;

    private String server;

    @Override
    public void onReceive() {
        Stream<Player> stream2 =
                Utilities.getOnlinePlayers().stream()
                        .filter(result -> result.hasPermission("core.staff"));
        stream2.forEach(result2 -> this.render(result2).sendToPlayer(result2));
    }

    private Clickable render(Player player) {
        Clickable clickable;
        (clickable = new Clickable())
                .add(
                        Color.translate(
                                        LocaleUtil.tr(
                                                player,
                                                MessageConfig.STAFF.LEFT_WHILE_FROZEN_ALERT))
                                .replace("<name>", this.displayName)
                                .replace("<server>", this.server));
        if (SettingsConfig.FREEZE.LEFT_FROZEN_CLICK) {
            clickable.add(" ");
            clickable.add(
                    Color.translate(LocaleUtil.tr(player, MessageConfig.FREEZE.BAN_BUTTON.TEXT)),
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.FREEZE.BAN_BUTTON.HOVER)
                                    .replace("<name>", this.name)),
                    MessageConfig.FREEZE.BAN_BUTTON.COMMAND.replace("<name>", this.name));
        }
        return clickable;
    }

    public LeftFrozenPacket(String name, String displayName, String server) {
        this.name = name;
        this.displayName = displayName;
        this.server = server;
    }

    public LeftFrozenPacket() {}
}
