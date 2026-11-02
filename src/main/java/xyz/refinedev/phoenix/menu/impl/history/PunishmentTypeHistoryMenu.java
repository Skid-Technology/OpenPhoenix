package xyz.refinedev.phoenix.menu.impl.history;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.pagination.PaginatedMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.PunishmentType;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.TimeConstants;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PunishmentTypeHistoryMenu extends PaginatedMenu {

    private final IProfile profile;

    private final PunishmentType punishmentType;

    private final List<IPunishment> punishments;

    public static String formatString(IPunishment punishment, String text, Player player) {
        text =
                text.replace(
                                "<willExpire>",
                                DateUtils.getDate(
                                        punishment.getIssuedAt() + punishment.getDuration()))
                        .replace("<target>", punishment.getNiceTarget())
                        .replace("<punishmentID>", punishment.getPunishmentID())
                        .replace("<expire>", punishment.getNiceExpire())
                        .replace(
                                "<duration>",
                                punishment.getPunishmentType() != PunishmentType.KICK
                                                && punishment.getPunishmentType()
                                                        != PunishmentType.WARN
                                        ? punishment.getNiceDuration()
                                        : TimeConstants.PERMANENT)
                        .replace("<issuedAt>", DateUtils.getDate(punishment.getIssuedAt()))
                        .replace(
                                "<issuedBy>",
                                player.hasPermission("core.history.viewaddedby")
                                        ? punishment.getNiceIssuedBy()
                                        : "&c&kgihnfjdglknb")
                        .replace("<issuedOn>", punishment.getIssuedOn())
                        .replace("<reason>", punishment.getReason())
                        .replace(
                                "<voidReason>",
                                punishment.getVoidReason() == null
                                        ? MenuConfig.HISTORY_MENU.VOID_REASON_UNRECORDED
                                        : punishment.getVoidReason().getNiceName())
                        .replace("<voidNote>", punishment.getVoidNote());
        if (punishment.getRemovedBy() != null) {
            text =
                    text.replace("<removedAt>", DateUtils.getDate(punishment.getRemovedAt()))
                            .replace(
                                    "<removedBy>",
                                    player.hasPermission("core.history.viewaddedby")
                                            ? punishment.getNiceRemovedBy()
                                            : "&c&kgihnfjdglknb")
                            .replace("<removedOn>", punishment.getRemovedOn())
                            .replace("<removedReason>", punishment.getRemovedReason());
        } else {
            text =
                    text.replace("<removedBy>", GlobalConfig.SERVER.CONSOLE_NAME)
                            .replace("<removedOn>", "Internal")
                            .replace("<removedReason>", TimeConstants.EXPIRED)
                            .replace(
                                    "<removedAt>",
                                    DateUtils.getDate(
                                            punishment.getIssuedAt() + punishment.getDuration()));
        }
        return text;
    }

    @Override
    public String getTitle(Player player) {
        return Color.translate(
                LocaleUtil.tr(player, MenuConfig.HISTORY_MENU.TYPE_TITLE)
                        .replace("<type>", this.punishmentType.getName())
                        .replace("<player>", this.profile.getNameWithColorNoDisguise()));
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        this.punishments.sort(IPunishment.COMPARATOR);
        for (IPunishment punishment : this.punishments) {
            entries.put(entries.size(), new PunishmentTypeHistoryMenu.EntryButton(punishment));
        }
        return entries;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        this.backButton(entries, 4);
        return entries;
    }

    public PunishmentTypeHistoryMenu(
            IProfile profile2, PunishmentType punishmentType2, List<IPunishment> punishments) {
        this.profile = profile2;
        this.punishmentType = punishmentType2;
        this.punishments = punishments;
    }

    static class EntryButton extends Button {

        private final IPunishment punishment;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder = new ItemBuilder(XMaterial.WHITE_WOOL.get());
            List<String> values;
            if (this.punishment.isActive()) {
                itemBuilder.setName(
                        LocaleUtil.tr(player, MenuConfig.HISTORY_MENU.ACTIVE_PUNISHMENT.NAME)
                                .replace(
                                        "<issuedAt>",
                                        DateUtils.getDate(this.punishment.getIssuedAt()))
                                .replace("<punishmentID>", this.punishment.getPunishmentID())
                                .replace(
                                        "<ip>",
                                        this.punishment.isIp()
                                                ? LocaleUtil.tr(player, MenuConfig.HISTORY_MENU.IP)
                                                : "")
                                .replace(
                                        "<shadow>",
                                        this.punishment.isShadow()
                                                ? LocaleUtil.tr(
                                                        player, MenuConfig.HISTORY_MENU.SHADOW)
                                                : "")
                                .replace(
                                        "<competitive>",
                                        this.punishment
                                                        .getPunishmentType()
                                                        .equals(PunishmentType.COMPETITIVE)
                                                ? LocaleUtil.tr(
                                                        player, MenuConfig.HISTORY_MENU.COMPETITIVE)
                                                : "")
                                .replace("<target>", this.punishment.getNiceTarget()));
                itemBuilder.setDurability(5);
                if (this.punishment.isPermanent()) {
                    values =
                            LocaleUtil.tr(
                                    player, MenuConfig.HISTORY_MENU.ACTIVE_PUNISHMENT.PERMANENT);
                } else {
                    values = LocaleUtil.tr(player, MenuConfig.HISTORY_MENU.ACTIVE_PUNISHMENT.TIMED);
                }
            } else if (this.punishment.isVoided()) {
                itemBuilder.setName(
                        LocaleUtil.tr(
                                        player,
                                        MenuConfig.HISTORY_MENU.INACTIVE_PUNISHMENT.VOIDED_NAME)
                                .replace(
                                        "<issuedAt>",
                                        DateUtils.getDate(this.punishment.getIssuedAt()))
                                .replace("<punishmentID>", this.punishment.getPunishmentID())
                                .replace(
                                        "<ip>",
                                        this.punishment.isIp()
                                                ? LocaleUtil.tr(player, MenuConfig.HISTORY_MENU.IP)
                                                : "")
                                .replace(
                                        "<shadow>",
                                        this.punishment.isShadow()
                                                ? LocaleUtil.tr(
                                                        player, MenuConfig.HISTORY_MENU.SHADOW)
                                                : "")
                                .replace(
                                        "<competitive>",
                                        this.punishment
                                                        .getPunishmentType()
                                                        .equals(PunishmentType.COMPETITIVE)
                                                ? LocaleUtil.tr(
                                                        player, MenuConfig.HISTORY_MENU.COMPETITIVE)
                                                : "")
                                .replace("<target>", this.punishment.getNiceTarget()));
                itemBuilder.setDurability(1);
                values = LocaleUtil.tr(player, MenuConfig.HISTORY_MENU.INACTIVE_PUNISHMENT.VOIDED);
            } else if (this.punishment.getRemovedBy() == null) {
                itemBuilder.setName(
                        LocaleUtil.tr(
                                        player,
                                        MenuConfig.HISTORY_MENU.INACTIVE_PUNISHMENT.EXPIRED_NAME)
                                .replace(
                                        "<issuedAt>",
                                        DateUtils.getDate(this.punishment.getIssuedAt()))
                                .replace("<punishmentID>", this.punishment.getPunishmentID())
                                .replace(
                                        "<ip>",
                                        this.punishment.isIp()
                                                ? LocaleUtil.tr(player, MenuConfig.HISTORY_MENU.IP)
                                                : "")
                                .replace(
                                        "<shadow>",
                                        this.punishment.isShadow()
                                                ? LocaleUtil.tr(
                                                        player, MenuConfig.HISTORY_MENU.SHADOW)
                                                : "")
                                .replace(
                                        "<competitive>",
                                        this.punishment
                                                        .getPunishmentType()
                                                        .equals(PunishmentType.COMPETITIVE)
                                                ? LocaleUtil.tr(
                                                        player, MenuConfig.HISTORY_MENU.COMPETITIVE)
                                                : "")
                                .replace("<target>", this.punishment.getNiceTarget()));
                itemBuilder.setDurability(8);
                values = LocaleUtil.tr(player, MenuConfig.HISTORY_MENU.INACTIVE_PUNISHMENT.EXPIRED);
            } else {
                itemBuilder.setName(
                        LocaleUtil.tr(player, MenuConfig.HISTORY_MENU.INACTIVE_PUNISHMENT.NAME)
                                .replace(
                                        "<issuedAt>",
                                        DateUtils.getDate(this.punishment.getIssuedAt()))
                                .replace("<punishmentID>", this.punishment.getPunishmentID())
                                .replace(
                                        "<ip>",
                                        this.punishment.isIp()
                                                ? LocaleUtil.tr(player, MenuConfig.HISTORY_MENU.IP)
                                                : "")
                                .replace(
                                        "<shadow>",
                                        this.punishment.isShadow()
                                                ? LocaleUtil.tr(
                                                        player, MenuConfig.HISTORY_MENU.SHADOW)
                                                : "")
                                .replace(
                                        "<competitive>",
                                        this.punishment
                                                        .getPunishmentType()
                                                        .equals(PunishmentType.COMPETITIVE)
                                                ? LocaleUtil.tr(
                                                        player, MenuConfig.HISTORY_MENU.COMPETITIVE)
                                                : "")
                                .replace("<target>", this.punishment.getNiceTarget()));
                itemBuilder.setDurability(14);
                values = LocaleUtil.tr(player, MenuConfig.HISTORY_MENU.INACTIVE_PUNISHMENT.REMOVED);
            }
            values.forEach(
                    result -> {
                        String text =
                                PunishmentTypeHistoryMenu.formatString(
                                        this.punishment, result, player);
                        itemBuilder.addLoreLine(text);
                    });
            if (!this.punishment.getProof().isEmpty()) {
                for (String text2 : LocaleUtil.tr(player, MenuConfig.HISTORY_MENU.PROOF_CLICK)) {
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
