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

import java.util.ArrayList;
import java.util.List;

@Register(name = "addlore")
@Requires("core.command.addlore")
public class AddloreCommand {

    private static final String text = "<lore>";

    @Command(name = "")
    public void execute(@Sender Player player, @Named("lore") @Text String text2) {
        ItemStack itemStack;
        if ((itemStack = player.getItemInHand()) != null
                && itemStack.getType() != XMaterial.AIR.parseMaterial()) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            List<String> values = new ArrayList<>();
            if (itemMeta.getLore() != null) {
                values = itemMeta.getLore();
            }
            values.add(text2);
            itemMeta.setLore(Color.translate(values));
            itemStack.setItemMeta(itemMeta);
            player.updateInventory();
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.ITEM.LORE_ADDED)
                                    .replace(text, Color.translate(text2))));
        } else {
            throw new ExitMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.BASIC.GENERAL.INVALID_ITEM)));
        }
    }
}
