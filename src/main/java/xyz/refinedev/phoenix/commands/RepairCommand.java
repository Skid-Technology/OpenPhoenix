package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.XMaterial;

@Register(name = "repair", aliases = "fix")
@Requires("core.command.repair")
public class RepairCommand {

    @Command(name = "hand")
    @Requires("core.command.repair.hand")
    public void hand(@Sender Player player) {
        ItemStack itemStack;
        if ((itemStack = player.getItemInHand()) == null
                || itemStack.getType() == XMaterial.AIR.parseMaterial()
                || itemStack.getType() == XMaterial.POTION.parseMaterial()
                || itemStack.getType() == XMaterial.GOLDEN_APPLE.parseMaterial()
                || itemStack.getType().isBlock()
                || itemStack.getType().getMaxDurability() < 1) {
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.BASIC.GENERAL.INVALID_ITEM)));
        } else if (itemStack.getDurability() == 0) {
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.REPAIR.ALREADY)));
        } else {
            itemStack.setDurability((short) 0);
            player.updateInventory();
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.REPAIR.SUCCESS_ITEM)));
        }
    }

    @Command(name = "all")
    @Requires("core.command.repair.all")
    public void all(@Sender Player player) {
        ItemStack[] itemStacks;
        int value = (itemStacks = player.getInventory().getContents()).length;
        for (int index = 0; index < value; index++) {
            ItemStack itemStack;
            if ((itemStack = itemStacks[index]) != null
                    && itemStack.getType() != XMaterial.AIR.parseMaterial()
                    && itemStack.getType() != XMaterial.POTION.parseMaterial()
                    && itemStack.getType() != XMaterial.GOLDEN_APPLE.parseMaterial()
                    && !itemStack.getType().isBlock()
                    && itemStack.getType().getMaxDurability() >= 1) {
                itemStack.setDurability((short) 0);
            }
        }
        value = (itemStacks = player.getInventory().getArmorContents()).length;
        for (int index2 = 0; index2 < value; index2++) {
            ItemStack itemStack2;
            if ((itemStack2 = itemStacks[index2]) != null
                    && itemStack2.getType() != XMaterial.AIR.parseMaterial()
                    && itemStack2.getType() != XMaterial.POTION.parseMaterial()
                    && itemStack2.getType() != XMaterial.GOLDEN_APPLE.parseMaterial()
                    && !itemStack2.getType().isBlock()
                    && itemStack2.getType().getMaxDurability() >= 1) {
                itemStack2.setDurability((short) 0);
            }
        }
        player.updateInventory();
        player.sendMessage(
                Color.translate(
                        LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.REPAIR.SUCCESS_ALL)));
    }

    @Command(name = "armor")
    @Requires("core.command.repair.armor")
    public void armor(@Sender Player player) {
        ItemStack[] itemStacks;
        int value = (itemStacks = player.getInventory().getArmorContents()).length;
        for (int index = 0; index < value; index++) {
            ItemStack itemStack;
            if ((itemStack = itemStacks[index]) != null
                    && itemStack.getType() != XMaterial.AIR.parseMaterial()
                    && itemStack.getType() != XMaterial.POTION.parseMaterial()
                    && itemStack.getType() != XMaterial.GOLDEN_APPLE.parseMaterial()
                    && !itemStack.getType().isBlock()
                    && itemStack.getType().getMaxDurability() >= 1) {
                itemStack.setDurability((short) 0);
            }
        }
        player.updateInventory();
        player.sendMessage(
                Color.translate(
                        LocaleUtil.tr(player, MessageConfig.BASIC.ADMIN.REPAIR.SUCCESS_ARMOR)));
    }
}
