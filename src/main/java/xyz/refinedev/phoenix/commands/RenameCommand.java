package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Text;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.XMaterial;

@Register(name = "rename")
@Requires("core.command.rename")
public class RenameCommand {

    @Command(name = "")
    public void execute(@Sender Player player, @Named("name") @Text String text) {
        ItemStack itemStack;
        if ((itemStack = player.getItemInHand()) != null
                && itemStack.getType() != XMaterial.AIR.parseMaterial()) {
            if ((itemStack.getType().equals(XMaterial.NETHER_STAR.parseMaterial())
                            || itemStack.getType().equals(XMaterial.NAME_TAG.parseMaterial())
                            || itemStack.getType().equals(XMaterial.BOOK.parseMaterial())
                            || itemStack.getType().equals(XMaterial.TRIPWIRE_HOOK.parseMaterial()))
                    && !player.hasPermission("core.admin")) {
                throw new ExitMessage(
                        Color.translate(
                                LocaleUtil.tr(
                                        player, MessageConfig.BASIC.ADMIN.ITEM.CAN_NOT_RENAME)));
            }
            ItemMeta itemMeta;
            (itemMeta = itemStack.getItemMeta()).setDisplayName(Color.translate(text));
            itemStack.setItemMeta(itemMeta);
            player.updateInventory();
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.ITEM.RENAME)
                                    .replace("<name>", Color.translate(text))));
        } else {
            throw new ExitMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.BASIC.GENERAL.INVALID_ITEM)));
        }
    }
}
