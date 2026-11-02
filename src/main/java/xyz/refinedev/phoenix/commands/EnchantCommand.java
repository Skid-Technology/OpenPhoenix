package xyz.refinedev.phoenix.commands;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.lib.xseries.XEnchantment;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.XMaterial;

@Register(name = "enchant")
@Requires("core.command.enchant")
public class EnchantCommand {

    private static final String text = "<enchant>";

    @Command(name = "")
    public void execute(
            @Sender Player player, XEnchantment xEnchantment, @Named("level") int value) {
        ItemStack itemStack;
        if ((itemStack = player.getItemInHand()) == null
                || itemStack.getType() == XMaterial.AIR.parseMaterial()) {
            throw new ExitMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.BASIC.GENERAL.INVALID_ITEM)));
        }
        if (xEnchantment != null && xEnchantment.isSupported()) {
            Enchantment enchant = xEnchantment.getEnchant();
            itemStack.addUnsafeEnchantment(enchant, value);
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.ITEM.ENCHANTED)
                                    .replace(text, enchant.getName())));
        } else {
            throw new ExitMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.BASIC.GENERAL.INVALID_ENCHANT)));
        }
    }
}
