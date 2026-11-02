package xyz.refinedev.phoenix.menu.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.Menu;
import xyz.refinedev.phoenix.menu.api.button.BackButton;
import xyz.refinedev.phoenix.menu.api.pagination.PaginatedMenu;
import xyz.refinedev.phoenix.menu.impl.history.PunishmentProofMenu;
import xyz.refinedev.phoenix.menu.impl.history.PunishmentTypeHistoryMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.PunishmentType;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaffHistoryMenu extends PaginatedMenu {

    private final IProfile profile;

    private final PunishmentType punishmentType;

    private final List<IPunishment> punishments;

    private final boolean enabled;

    @Override
    public String getTitle(Player player) {
        return this.enabled
                ? Color.translate(
                        LocaleUtil.tr(player, MenuConfig.STAFF_HISTORY_MENU.ISSUED_TYPE_TITLE)
                                .replace("<type>", this.punishmentType.getName())
                                .replace("<player>", this.profile.getNameWithColorNoDisguise()))
                : Color.translate(
                        LocaleUtil.tr(player, MenuConfig.STAFF_HISTORY_MENU.REMOVED_TYPE_TITLE)
                                .replace("<type>", this.punishmentType.getName())
                                .replace("<player>", this.profile.getNameWithColorNoDisguise()));
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        for (IPunishment punishment : this.punishments) {
            entries.put(entries.size(), new StaffHistoryMenu.EntryButton(punishment));
        }
        return entries;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        Menu menu;
        if ((menu = this.getPreviousMenu()) != null && !this.isFirst()) {
            entries.put(4, new BackButton(menu));
        }
        return entries;
    }

    public StaffHistoryMenu(
            IProfile profile2,
            PunishmentType punishmentType2,
            List<IPunishment> punishments,
            boolean enabled) {
        this.profile = profile2;
        this.punishmentType = punishmentType2;
        this.punishments = punishments;
        this.enabled = enabled;
    }

    static class EntryButton extends Button {

        private final IPunishment punishment;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder = new ItemBuilder(XMaterial.WHITE_WOOL.get());
            List<String> values;
            if (this.punishment.isActive()) {
                itemBuilder.setName(
                        LocaleUtil.tr(
                                        player,
                                        MenuConfig.STAFF_HISTORY_MENU.ACTIVE_STAFF_PUNISHMENT.NAME)
                                .replace(
                                        "<issuedAt>",
                                        DateUtils.getDate(this.punishment.getIssuedAt()))
                                .replace("<punishmentID>", this.punishment.getPunishmentID())
                                .replace(
                                        "<ip>",
                                        this.punishment.isIp()
                                                ? LocaleUtil.tr(
                                                        player, MenuConfig.STAFF_HISTORY_MENU.IP)
                                                : "")
                                .replace(
                                        "<shadow>",
                                        this.punishment.isShadow()
                                                ? LocaleUtil.tr(
                                                        player,
                                                        MenuConfig.STAFF_HISTORY_MENU.SHADOW)
                                                : "")
                                .replace(
                                        "<competitive>",
                                        this.punishment
                                                        .getPunishmentType()
                                                        .equals(PunishmentType.COMPETITIVE)
                                                ? LocaleUtil.tr(
                                                        player,
                                                        MenuConfig.STAFF_HISTORY_MENU.COMPETITIVE)
                                                : "")
                                .replace("<target>", this.punishment.getNiceTarget()));
                itemBuilder.setDurability(5);
                if (this.punishment.isPermanent()) {
                    values =
                            LocaleUtil.tr(
                                    player,
                                    MenuConfig.STAFF_HISTORY_MENU
                                            .ACTIVE_STAFF_PUNISHMENT
                                            .PERMANENT);
                } else {
                    values =
                            LocaleUtil.tr(
                                    player,
                                    MenuConfig.STAFF_HISTORY_MENU.ACTIVE_STAFF_PUNISHMENT.TIMED);
                }
            } else if (this.punishment.isVoided()) {
                itemBuilder.setName(
                        LocaleUtil.tr(
                                        player,
                                        MenuConfig.STAFF_HISTORY_MENU
                                                .INACTIVE_STAFF_PUNISHMENT
                                                .VOIDED_NAME)
                                .replace(
                                        "<issuedAt>",
                                        DateUtils.getDate(this.punishment.getIssuedAt()))
                                .replace("<punishmentID>", this.punishment.getPunishmentID())
                                .replace(
                                        "<ip>",
                                        this.punishment.isIp()
                                                ? LocaleUtil.tr(
                                                        player, MenuConfig.STAFF_HISTORY_MENU.IP)
                                                : "")
                                .replace(
                                        "<shadow>",
                                        this.punishment.isShadow()
                                                ? LocaleUtil.tr(
                                                        player,
                                                        MenuConfig.STAFF_HISTORY_MENU.SHADOW)
                                                : "")
                                .replace(
                                        "<competitive>",
                                        this.punishment
                                                        .getPunishmentType()
                                                        .equals(PunishmentType.COMPETITIVE)
                                                ? LocaleUtil.tr(
                                                        player,
                                                        MenuConfig.STAFF_HISTORY_MENU.COMPETITIVE)
                                                : "")
                                .replace("<target>", this.punishment.getNiceTarget()));
                itemBuilder.setDurability(1);
                values =
                        LocaleUtil.tr(
                                player,
                                MenuConfig.STAFF_HISTORY_MENU.INACTIVE_STAFF_PUNISHMENT.VOIDED);
            } else if (this.punishment.getRemovedBy() == null) {
                itemBuilder.setName(
                        LocaleUtil.tr(
                                        player,
                                        MenuConfig.STAFF_HISTORY_MENU
                                                .INACTIVE_STAFF_PUNISHMENT
                                                .EXPIRED_NAME)
                                .replace(
                                        "<issuedAt>",
                                        DateUtils.getDate(this.punishment.getIssuedAt()))
                                .replace("<punishmentID>", this.punishment.getPunishmentID())
                                .replace(
                                        "<ip>",
                                        this.punishment.isIp()
                                                ? LocaleUtil.tr(
                                                        player, MenuConfig.STAFF_HISTORY_MENU.IP)
                                                : "")
                                .replace(
                                        "<shadow>",
                                        this.punishment.isShadow()
                                                ? LocaleUtil.tr(
                                                        player,
                                                        MenuConfig.STAFF_HISTORY_MENU.SHADOW)
                                                : "")
                                .replace(
                                        "<competitive>",
                                        this.punishment
                                                        .getPunishmentType()
                                                        .equals(PunishmentType.COMPETITIVE)
                                                ? LocaleUtil.tr(
                                                        player,
                                                        MenuConfig.STAFF_HISTORY_MENU.COMPETITIVE)
                                                : "")
                                .replace("<target>", this.punishment.getNiceTarget()));
                itemBuilder.setDurability(8);
                values =
                        LocaleUtil.tr(
                                player,
                                MenuConfig.STAFF_HISTORY_MENU.INACTIVE_STAFF_PUNISHMENT.EXPIRED);
            } else {
                itemBuilder.setName(
                        LocaleUtil.tr(
                                        player,
                                        MenuConfig.STAFF_HISTORY_MENU
                                                .INACTIVE_STAFF_PUNISHMENT
                                                .NAME)
                                .replace(
                                        "<issuedAt>",
                                        DateUtils.getDate(this.punishment.getIssuedAt()))
                                .replace("<punishmentID>", this.punishment.getPunishmentID())
                                .replace(
                                        "<ip>",
                                        this.punishment.isIp()
                                                ? LocaleUtil.tr(
                                                        player, MenuConfig.STAFF_HISTORY_MENU.IP)
                                                : "")
                                .replace(
                                        "<shadow>",
                                        this.punishment.isShadow()
                                                ? LocaleUtil.tr(
                                                        player,
                                                        MenuConfig.STAFF_HISTORY_MENU.SHADOW)
                                                : "")
                                .replace(
                                        "<competitive>",
                                        this.punishment
                                                        .getPunishmentType()
                                                        .equals(PunishmentType.COMPETITIVE)
                                                ? LocaleUtil.tr(
                                                        player,
                                                        MenuConfig.STAFF_HISTORY_MENU.COMPETITIVE)
                                                : "")
                                .replace("<target>", this.punishment.getNiceTarget()));
                itemBuilder.setDurability(14);
                values =
                        LocaleUtil.tr(
                                player,
                                MenuConfig.STAFF_HISTORY_MENU.INACTIVE_STAFF_PUNISHMENT.REMOVED);
            }
            values.forEach(
                    result -> {
                        String text =
                                PunishmentTypeHistoryMenu.formatString(
                                        this.punishment, result, player);
                        itemBuilder.addLoreLine(text);
                    });
            if (!this.punishment.getProof().isEmpty()) {
                for (String text2 :
                        LocaleUtil.tr(player, MenuConfig.STAFF_HISTORY_MENU.PROOF_CLICK)) {
                    itemBuilder.addLoreLine(text2);
                }
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Phoenix.getInstance()
                    .getMenuHandler()
                    .openMenu(new PunishmentProofMenu(this.punishment), player);
        }

        public EntryButton(IPunishment punishment2) {
            this.punishment = punishment2;
        }
    }
}
