package xyz.refinedev.phoenix.dialog.bedrock;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.Dialog;

public interface IBedrockDialogRenderer {

    boolean isBedrock(Player player);

    boolean show(Player player, Dialog dialog);
}
