package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.XMaterial;

@Register(name = "more")
@Requires("core.command.more")
public class MoreCommand {

    @Command(name = "")
    public void execute(@Sender Player player) {
        ItemStack itemStack;
        if ((itemStack = player.getItemInHand()) != null
                && itemStack.getType() != XMaterial.AIR.parseMaterial()) {
            itemStack.setAmount(64);
            player.updateInventory();
            player.sendMessage(
                    Color.translate(LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.MORE.SUCCESS)));
        } else {
            throw new ExitMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.BASIC.GENERAL.INVALID_ITEM)));
        }
    }
}
