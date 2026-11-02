package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.commands.api.annotations.PlayerNames;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

@Register(
        name = "skull",
        aliases = {"head", "getskull", "gethead"})
@Requires("core.command.skull")
public class SkullCommand {

    private static final String text = "<name>";

    @Command(name = "")
    public void execute(@Sender Player player, @PlayerNames @Named("skull") String text2) {
        if (player.getInventory().firstEmpty() == -1) {
            throw new ExitMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.BASIC.GENERAL.FULL_INVENTORY)));
        }
        player.getInventory()
                .addItem(
                        new ItemStack[] {
                            new ItemBuilder(XMaterial.PLAYER_HEAD.parseMaterial())
                                    .setDurability(3)
                                    .setSkullOwner(text2)
                                    .toItemStack()
                        });
        player.sendMessage(
                Color.translate(
                        LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.SKULL.SUCCESS)
                                .replace(text, text2)));
    }
}
