package xyz.refinedev.phoenix.menu.impl.history;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.Menu;
import xyz.refinedev.phoenix.menu.impl.StaffHistoryMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.PunishmentType;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.PunishmentUtils;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaffHistoryOverviewMenu extends Menu {

    private final IProfile profile;

    private final List<IPunishment> punishments;

    private final List<IPunishment> punishmentsList;

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        this.backButton(entries, 4);
        entries.put(
                10,
                new StaffHistoryOverviewMenu.RemovedButton(
                        true, PunishmentType.KICK, this.findList(PunishmentType.KICK, true)));
        entries.put(
                12,
                new StaffHistoryOverviewMenu.RemovedButton(
                        true, PunishmentType.MUTE, this.findList(PunishmentType.MUTE, true)));
        entries.put(
                14,
                new StaffHistoryOverviewMenu.RemovedButton(
                        true, PunishmentType.BAN, this.findList(PunishmentType.BAN, true)));
        entries.put(
                16,
                new StaffHistoryOverviewMenu.RemovedButton(
                        true,
                        PunishmentType.BLACKLIST,
                        this.findList(PunishmentType.BLACKLIST, true)));
        if (player.hasPermission("core.history.viewblacklists")) {
            entries.put(
                    20,
                    new StaffHistoryOverviewMenu.RemovedButton(
                            false, PunishmentType.MUTE, this.findList(PunishmentType.MUTE, false)));
            entries.put(
                    22,
                    new StaffHistoryOverviewMenu.RemovedButton(
                            false, PunishmentType.BAN, this.findList(PunishmentType.BAN, false)));
            entries.put(
                    24,
                    new StaffHistoryOverviewMenu.RemovedButton(
                            false,
                            PunishmentType.BLACKLIST,
                            this.findList(PunishmentType.BLACKLIST, false)));
        } else {
            entries.put(
                    21,
                    new StaffHistoryOverviewMenu.RemovedButton(
                            false, PunishmentType.MUTE, this.findList(PunishmentType.MUTE, false)));
            entries.put(
                    23,
                    new StaffHistoryOverviewMenu.RemovedButton(
                            false, PunishmentType.BAN, this.findList(PunishmentType.BAN, false)));
        }
        for (int index = 0; index < 36; index++) {
            entries.computeIfAbsent(index, result -> Button.placeholder());
        }
        return entries;
    }

    @Override
    public String getTitle(Player player) {
        return LocaleUtil.tr(player, MenuConfig.STAFF_HISTORY_MENU.TITLE)
                .replace("<player>", this.profile.getNameWithColorNoDisguise());
    }

    public List<IPunishment> findList(PunishmentType punishmentType, boolean matches) {
        return PunishmentUtils.findList(
                matches ? this.punishments : this.punishmentsList, punishmentType);
    }

    public StaffHistoryOverviewMenu(
            IProfile profile2, List<IPunishment> punishments, List<IPunishment> punishmentsList) {
        this.profile = profile2;
        this.punishments = punishments;
        this.punishmentsList = punishmentsList;
    }

    class RemovedButton extends Button {

        private final boolean enabled;

        private final PunishmentType punishmentType;

        private final List<IPunishment> punishments;

        @Override
        public ItemStack getItem(Player player) {
            int value = PunishmentUtils.calculateCount(this.punishments);
            ItemBuilder itemBuilder;
            (itemBuilder =
                            new ItemBuilder(
                                    XMaterial.PAPER.get(),
                                    Math.min(Math.max(this.punishments.size(), 1), 64)))
                    .setName(
                            LocaleUtil.tr(player, MenuConfig.STAFF_HISTORY_MENU.CATEGORY_NAME)
                                    .replace(
                                            "<category>",
                                            (this.enabled ? "" : "Removed ")
                                                    + this.punishmentType.getName()));
            if (this.punishmentType == PunishmentType.BAN) {
                List<String> values =
                        LocaleUtil.tr(
                                player,
                                MenuConfig.STAFF_HISTORY_MENU.CATEGORY_LORE_WITH_COMPETITIVE);
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
                                            .replace(
                                                    "<player>",
                                                    StaffHistoryOverviewMenu.this.profile
                                                            .getNameWithColorNoDisguise())
                                            .replace(
                                                    "<affectedUsers>",
                                                    String.valueOf(
                                                            PunishmentUtils.calculateCount(
                                                                    this.punishments,
                                                                    this.punishmentType)))
                                            .replace(
                                                    "<competitive>",
                                                    String.valueOf(
                                                            PunishmentUtils
                                                                    .calculateCountByCOMPETITIVE(
                                                                            this.punishments)))
                                            .replace(
                                                    "<category>",
                                                    (this.enabled ? "" : "Removed ")
                                                            + this.punishmentType.getName());
                            itemBuilder2.addLoreLine(result);
                        });
            } else {
                List<String> values2 =
                        LocaleUtil.tr(player, MenuConfig.STAFF_HISTORY_MENU.CATEGORY_LORE);
                ItemBuilder itemBuilder3 = itemBuilder;
                values2.forEach(
                        result -> {
                            result =
                                    result.replace(
                                                    "<total>",
                                                    String.valueOf(this.punishments.size()))
                                            .replace("<active>", String.valueOf(value))
                                            .replace(
                                                    "<inactive>",
                                                    String.valueOf(this.punishments.size() - value))
                                            .replace(
                                                    "<player>",
                                                    StaffHistoryOverviewMenu.this.profile
                                                            .getNameWithColorNoDisguise())
                                            .replace(
                                                    "<affectedUsers>",
                                                    String.valueOf(
                                                            PunishmentUtils.calculateCount(
                                                                    this.punishments,
                                                                    this.punishmentType)))
                                            .replace(
                                                    "<category>",
                                                    (this.enabled ? "" : "Removed ")
                                                            + this.punishmentType.getName());
                            itemBuilder3.addLoreLine(result);
                        });
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (!this.punishments.isEmpty()) {
                StaffHistoryOverviewMenu.super
                        .plugin
                        .getMenuHandler()
                        .openMenuAsync(
                                new StaffHistoryMenu(
                                        StaffHistoryOverviewMenu.this.profile,
                                        this.punishmentType,
                                        this.punishments,
                                        this.enabled),
                                player);
            }
        }

        public RemovedButton(
                boolean enabled, PunishmentType punishmentType2, List<IPunishment> punishments) {
            this.enabled = enabled;
            this.punishmentType = punishmentType2;
            this.punishments = punishments;
        }
    }
}
