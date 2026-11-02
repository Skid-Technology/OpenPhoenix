package xyz.refinedev.phoenix.setup;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.impl.SetupMenu;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.ConfirmationMenus;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.chat.ConversationUtil;
import xyz.refinedev.phoenix.utils.general.Tasks;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;

public final class SetupWriter {

    private SetupWriter() {}

    static void applyPhoenix(
            Phoenix phoenix,
            Player player,
            SetupTier setupTier,
            String text,
            String text2,
            String text3,
            String text4,
            boolean matches,
            List<String> values,
            Function<String, String> function,
            Consumer<String> consumer) {
        player.closeInventory();
        ConversationUtil.ask(
                phoenix,
                player,
                text3,
                result -> {
                    String text5;
                    if ((text5 = function == null ? null : (String) function.apply(result))
                            != null) {
                        player.sendMessage(Color.translate(text5));
                        SetupMenu.applyPhoenix(phoenix, player, setupTier, text);
                    } else if (result.equals(text4)) {
                        player.sendMessage(
                                Color.translate(
                                        LocaleUtil.tr(player, MessageConfig.PHOENIX.SETUP.UNCHANGED)
                                                .replace("<key>", text2)));
                        SetupMenu.applyPhoenix(phoenix, player, setupTier, text);
                    } else {
                        ArrayList<String> values2;
                        (values2 = new ArrayList())
                                .add(
                                        LocaleUtil.tr(player, MenuConfig.SETUP_MENU.CONFIRM.FROM)
                                                .replace(
                                                        "<value>",
                                                        matches
                                                                ? CheckStatusFormatter.formatString(
                                                                        text4)
                                                                : CheckStatusFormatter.isEmpty(
                                                                        text4)));
                        values2.add(
                                LocaleUtil.tr(player, MenuConfig.SETUP_MENU.CONFIRM.TO)
                                        .replace(
                                                "<value>",
                                                matches
                                                        ? CheckStatusFormatter.formatString(result)
                                                        : CheckStatusFormatter.isEmpty(result)));
                        for (String text6 : values) {
                            values2.add(
                                    LocaleUtil.tr(player, MenuConfig.SETUP_MENU.CONFIRM.NOTE)
                                            .replace("<note>", text6));
                        }
                        applyPhoenix(
                                phoenix,
                                player,
                                setupTier,
                                text,
                                text2,
                                values2,
                                () -> {
                                    consumer.accept(result);
                                    player.sendMessage(
                                            Color.translate(
                                                    LocaleUtil.tr(
                                                                    player,
                                                                    MessageConfig.PHOENIX
                                                                            .SETUP
                                                                            .CHANGED)
                                                            .replace("<key>", text2)
                                                            .replace(
                                                                    "<value>",
                                                                    matches
                                                                            ? CheckStatusFormatter
                                                                                    .formatString(
                                                                                            result)
                                                                            : CheckStatusFormatter
                                                                                    .isEmpty(
                                                                                            result))));
                                });
                    }
                });
    }

    public static void applyPhoenix(
            Phoenix phoenix,
            Player player,
            SetupTier setupTier,
            String text,
            final String text2,
            final List<String> values,
            Runnable runnable) {
        Button button =
                new Button() {

                    @Override
                    public ItemStack getItem(Player player) {
                        ItemBuilder itemBuilder;
                        (itemBuilder = new ItemBuilder(XMaterial.PAPER.get()))
                                .setName(
                                        LocaleUtil.tr(player, MenuConfig.SETUP_MENU.CONFIRM.NAME)
                                                .replace("<action>", text2));
                        Iterator<String> iterator2 =
                                LocaleUtil.tr(player, MenuConfig.SETUP_MENU.CONFIRM.LORE)
                                        .iterator();
                        while (iterator2.hasNext()) {
                            String text3;
                            if (!(text3 = (String) iterator2.next()).contains("<lines>")) {
                                itemBuilder.addLoreLine(text3);
                            } else {
                                for (String text4 : values) {
                                    itemBuilder.addLoreLine(text4);
                                }
                            }
                        }
                        return itemBuilder.toItemStack();
                    }
                };
        Tasks.run(
                () -> {
                    String text5 = LocaleUtil.tr(player, MenuConfig.SETUP_MENU.CONFIRM.TITLE);
                    ConfirmationMenus.applyPhoenix(
                            phoenix,
                            player,
                            text5,
                            true,
                            result -> {
                                if (!result) {
                                    player.sendMessage(
                                            Color.translate(
                                                    LocaleUtil.tr(
                                                            player,
                                                            MessageConfig.PHOENIX
                                                                    .SETUP
                                                                    .CANCELLED)));
                                    SetupMenu.applyPhoenix(phoenix, player, setupTier, text);
                                } else {
                                    Tasks.runAsync(
                                            () ->
                                                    sendMessage(
                                                            phoenix, player, setupTier, text,
                                                            runnable));
                                }
                            },
                            button);
                });
    }

    private static void sendMessage(
            Phoenix phoenix, Player player, SetupTier setupTier, String text, Runnable runnable) {
        if (!SetupLock.matchesEnabled(phoenix, text, player.getName())) {
            String text2 = SetupLock.formatString(phoenix, text);
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.PHOENIX.SETUP.LOCKED)
                                    .replace("<holder>", text2 == null ? "Somebody else" : text2)
                                    .replace("<area>", text)));
            SetupMenu.applyPhoenix(phoenix, player, setupTier, text);
        } else {
            try {
                runnable.run();
            } catch (Throwable throwable) {
                player.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player, MessageConfig.PHOENIX.SETUP.FAILED)
                                        .replace(
                                                "<error>",
                                                String.valueOf(throwable.getMessage()))));
                phoenix.getLogger()
                        .log(Level.SEVERE, "Setup failed to write " + text + ".", throwable);
            } finally {
                SetupLock.applyPhoenix(phoenix, text);
            }
            SetupMenu.applyPhoenix(phoenix, player, setupTier, text);
        }
    }
}
