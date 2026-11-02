package xyz.refinedev.phoenix.menu.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.Menu;
import xyz.refinedev.phoenix.menu.impl.history.PunishmentTypeHistoryMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.PunishmentType;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.PunishmentUtils;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryMenu extends Menu {

    private final IProfile profile;

    private final List<IPunishment> punishments;

    @Override
    public String getTitle(Player player) {
        return LocaleUtil.tr(player, MenuConfig.HISTORY_MENU.TITLE)
                .replace("<player>", this.profile.getNameWithColorNoDisguise());
    }

    @Override
    public void onOpen(Player player) {
        if (!this.isOpenedByMenu()) {
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.GENERAL.GENERIC_VIEW_MESSAGE)
                                    .replace("<type>", "punishment history")
                                    .replace(
                                            "<player>",
                                            this.profile.getNameWithColorNoDisguise())));
        }
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        this.punishments.sort(IPunishment.COMPARATOR);
        this.backButton(entries, 4);
        if (player.hasPermission("core.history.viewblacklists")) {
            entries.put(
                    10,
                    new HistoryMenu.EntryButton(
                            PunishmentType.KICK,
                            PunishmentUtils.findList(this.punishments, PunishmentType.KICK)));
            entries.put(
                    12,
                    new HistoryMenu.EntryButton(
                            PunishmentType.MUTE,
                            PunishmentUtils.findList(this.punishments, PunishmentType.MUTE)));
            entries.put(
                    14,
                    new HistoryMenu.EntryButton(
                            PunishmentType.BAN,
                            PunishmentUtils.findList(this.punishments, PunishmentType.BAN)));
            entries.put(
                    16,
                    new HistoryMenu.EntryButton(
                            PunishmentType.BLACKLIST,
                            PunishmentUtils.findList(this.punishments, PunishmentType.BLACKLIST)));
        } else {
            entries.put(
                    11,
                    new HistoryMenu.EntryButton(
                            PunishmentType.KICK,
                            PunishmentUtils.findList(this.punishments, PunishmentType.KICK)));
            entries.put(
                    13,
                    new HistoryMenu.EntryButton(
                            PunishmentType.MUTE,
                            PunishmentUtils.findList(this.punishments, PunishmentType.MUTE)));
            entries.put(
                    15,
                    new HistoryMenu.EntryButton(
                            PunishmentType.BAN,
                            PunishmentUtils.findList(this.punishments, PunishmentType.BAN)));
        }
        for (int index = 0; index < 27; index++) {
            entries.computeIfAbsent(index, result -> Button.placeholder());
        }
        return entries;
    }

    public HistoryMenu(IProfile profile2, List<IPunishment> punishments) {
        this.profile = profile2;
        this.punishments = punishments;
    }

    class EntryButton extends Button {

        private final PunishmentType punishmentType;

        private final List<IPunishment> punishments;

        @Override
        public ItemStack getItem(Player player) {
            int value = PunishmentUtils.calculateCount(this.punishments);
            ItemBuilder itemBuilder;
            (itemBuilder =
                            new ItemBuilder(
                                    XMaterial.WHITE_WOOL.get(),
                                    Math.min(Math.max(this.punishments.size(), 1), 64)))
                    .setDurability(value > 0 ? 5 : 14);
            itemBuilder.setName(
                    LocaleUtil.tr(player, MenuConfig.HISTORY_MENU.CATEGORY_NAME)
                            .replace("<category>", this.punishmentType.getName()));
            if (this.punishmentType == PunishmentType.BAN) {
                int value2 = PunishmentUtils.calculateCountByCOMPETITIVE(this.punishments);
                List<String> values =
                        LocaleUtil.tr(
                                player, MenuConfig.HISTORY_MENU.CATEGORY_LORE_WITH_COMPETITIVE);
                ItemBuilder itemBuilder2 = itemBuilder;
                values.forEach(
                        result -> {
                            result =
                                    result.replace(
                                                    "<total>",
                                                    String.valueOf(this.punishments.size()))
                                            .replace("<active>", String.valueOf(value))
                                            .replace(
                                                    "<inactive>",
                                                    String.valueOf(this.punishments.size() - value))
                                            .replace("<competitive>", String.valueOf(value2))
                                            .replace(
                                                    "<player>",
                                                    HistoryMenu.this.profile
                                                            .getNameWithColorNoDisguise())
                                            .replace("<category>", this.punishmentType.getName());
                            itemBuilder2.addLoreLine(result);
                        });
            } else {
                List<String> values2 = LocaleUtil.tr(player, MenuConfig.HISTORY_MENU.CATEGORY_LORE);
                ItemBuilder itemBuilder3 = itemBuilder;
                values2.forEach(
                        result2 -> {
                            result2 =
                                    result2.replace(
                                                    "<total>",
                                                    String.valueOf(this.punishments.size()))
                                            .replace("<active>", String.valueOf(value))
                                            .replace(
                                                    "<inactive>",
                                                    String.valueOf(this.punishments.size() - value))
                                            .replace(
                                                    "<player>",
                                                    HistoryMenu.this.profile
                                                            .getNameWithColorNoDisguise())
                                            .replace("<category>", this.punishmentType.getName());
                            itemBuilder3.addLoreLine(result2);
                        });
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (!this.punishments.isEmpty()) {
                HistoryMenu.super
                        .plugin
                        .getMenuHandler()
                        .openMenuAsync(
                                new PunishmentTypeHistoryMenu(
                                        HistoryMenu.this.profile,
                                        this.punishmentType,
                                        this.punishments),
                                player);
            }
        }

        public EntryButton(PunishmentType punishmentType2, List<IPunishment> punishments) {
            this.punishmentType = punishmentType2;
            this.punishments = punishments;
        }
    }
}
