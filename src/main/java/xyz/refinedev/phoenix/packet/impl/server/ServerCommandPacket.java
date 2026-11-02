package xyz.refinedev.phoenix.packet.impl.server;

import org.bukkit.Bukkit;

import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.packet.api.Packet;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

public class ServerCommandPacket implements Packet {

    private String server;

    private String command;

    private String sender;

    @Override
    public boolean async() {
        return false;
    }

    @Override
    public void onReceive() {
        if (this.command.startsWith("/")) {
            this.command = this.command.substring(1);
        }
        if (this.server.equalsIgnoreCase("all")) {
            Bukkit.getConsoleSender()
                    .sendMessage(
                            Color.translate(
                                    LocaleUtil.tr(
                                                    Bukkit.getConsoleSender(),
                                                    MessageConfig.EXEC_CMD.PERFORM_CMD_ALL_ALERT)
                                            .replace(
                                                    "<server>",
                                                    Phoenix.getInstance()
                                                            .getNetworkHandler()
                                                            .getDisplayName())
                                            .replace("<sender>", this.sender)
                                            .replace("<command>", this.command)));
            BukkitAPI.broadcastToStaff(
                    result ->
                            Color.translate(
                                    LocaleUtil.tr(
                                                    result,
                                                    MessageConfig.EXEC_CMD.PERFORM_CMD_ALL_ALERT)
                                            .replace(
                                                    "<server>",
                                                    Phoenix.getInstance()
                                                            .getNetworkHandler()
                                                            .getDisplayName())
                                            .replace("<sender>", this.sender)
                                            .replace("<command>", this.command)));
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), this.command);
        } else {
            if (Phoenix.getInstance()
                    .getNetworkHandler()
                    .getServerName()
                    .equalsIgnoreCase(this.server)) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), this.command);
            }
            BukkitAPI.broadcastToStaff(
                    result ->
                            Color.translate(
                                    LocaleUtil.tr(
                                                    result,
                                                    MessageConfig.EXEC_CMD.PERFORM_CMD_SERVER_ALERT)
                                            .replace(
                                                    "<server>",
                                                    Phoenix.getInstance()
                                                            .getNetworkHandler()
                                                            .getDisplayName())
                                            .replace("<sender>", this.sender)
                                            .replace("<command>", this.command)
                                            .replace("<executed>", this.server)));
            Bukkit.getConsoleSender()
                    .sendMessage(
                            Color.translate(
                                    LocaleUtil.tr(
                                                    Bukkit.getConsoleSender(),
                                                    MessageConfig.EXEC_CMD.PERFORM_CMD_SERVER_ALERT)
                                            .replace(
                                                    "<server>",
                                                    Phoenix.getInstance()
                                                            .getNetworkHandler()
                                                            .getDisplayName())
                                            .replace("<sender>", this.sender)
                                            .replace("<command>", this.command)
                                            .replace("<executed>", this.server)));
        }
    }

    public ServerCommandPacket(String server, String command, String sender) {
        this.server = server;
        this.command = command;
        this.sender = sender;
    }

    public ServerCommandPacket() {}
}
