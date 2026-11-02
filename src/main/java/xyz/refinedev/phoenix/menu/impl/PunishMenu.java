package xyz.refinedev.phoenix.menu.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.pagination.PaginatedMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.chatsnapshot.IChatSnapshot;
import xyz.refinedev.phoenix.profile.punishment.Proof;
import xyz.refinedev.phoenix.profile.punishment.ProofType;
import xyz.refinedev.phoenix.profile.punishment.Punishment;
import xyz.refinedev.phoenix.profile.punishment.PunishmentType;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadder;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadderType;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.ConfirmationMenus;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.PunishmentUtil;
import xyz.refinedev.phoenix.utils.TimeConstants;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.general.Tasks;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PunishMenu extends PaginatedMenu {

    private final IProfile profile;

    @Override
    public String getTitle(Player player) {
        return LocaleUtil.tr(player, MenuConfig.PUNISH_MENU.TITLE)
                .replace("<player>", this.profile.getNameWithColorNoDisguise());
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        Iterator<IPunishmentLadder> iterator2 =
                super.api.getPunishmentLadderHandler().getSortedLadders().iterator();
        while (iterator2.hasNext()) {
            IPunishmentLadder punishmentLadder;
            if (!(punishmentLadder = (IPunishmentLadder) iterator2.next()).isHidden()
                    && player.hasPermission("core.punish." + punishmentLadder.getId())) {
                entries.put(entries.size(), new PunishMenu.SilentlyButton(punishmentLadder));
            }
        }
        return entries;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        this.backButton(entries, 4);
        return entries;
    }

    public PunishMenu(IProfile profile2) {
        this.profile = profile2;
    }

    class SilentlyButton extends Button {

        private final IPunishmentLadder punishmentLadder;

        @Override
        public ItemStack getItem(Player player) {
            List<xyz.refinedev.phoenix.profile.punishment.IPunishment> punishments =
                    PunishMenu.this.profile.getPunishments();
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.PAPER.get()))
                    .setName(
                            LocaleUtil.tr(player, MenuConfig.PUNISH_MENU.ITEM_NAME)
                                    .replace("<ladder>", this.punishmentLadder.getReason())
                                    .replace(
                                            "<nextTimes>",
                                            String.valueOf(
                                                    this.punishmentLadder.getPunishedTimes(
                                                                    punishments)
                                                            + 1)));
            IPunishmentLadderType punishmentLadderType;
            if ((punishmentLadderType = this.punishmentLadder.getNextLadder(punishments)) == null) {
                itemBuilder.addLoreLine("&cNo Ladder found.");
                return itemBuilder.toItemStack();
            }
            boolean matches = SettingsConfig.SERVER.PUNISHMENTS.AUTO_SILENT_PUNISHMENT_LADDERS;
            String text =
                    SettingsConfig.SERVER.PUNISHMENTS.AUTO_SILENT_PUNISHMENT_LADDERS
                            ? "silently"
                            : "publicly";
            String text2 = matches ? "publicly" : "silently";
            if (this.punishmentLadder.isRequireChatSnapshot()) {
                Iterator<String> iterator2 =
                        LocaleUtil.tr(player, MenuConfig.PUNISH_MENU.SNAPSHOT_ITEM_LORE).iterator();
                while (iterator2.hasNext()) {
                    String text3;
                    if ((text3 = (String) iterator2.next()).contains("<decay>")) {
                        for (String text4 : LocaleUtil.tr(player, MenuConfig.PUNISH_MENU.DECAY)) {
                            itemBuilder.addLoreLine(
                                    text4.replace(
                                                    "<player>",
                                                    PunishMenu.this.profile
                                                            .getNameWithColorNoDisguise())
                                            .replace(
                                                    "<normal>",
                                                    String.valueOf(
                                                            this.punishmentLadder.getPunishedTimes(
                                                                            punishments)
                                                                    + 1))
                                            .replace(
                                                    "<decayed>",
                                                    String.valueOf(
                                                            punishmentLadderType.getOrder())));
                        }
                    } else {
                        itemBuilder.addLoreLine(
                                text3.replace(
                                                "<player>",
                                                PunishMenu.this.profile
                                                        .getNameWithColorNoDisguise())
                                        .replace(
                                                "<times>",
                                                String.valueOf(
                                                        this.punishmentLadder.getPunishedTimes(
                                                                punishments)))
                                        .replace(
                                                "<type>",
                                                (punishmentLadderType.isIp() ? "IP " : "")
                                                        + (punishmentLadderType.isShadow()
                                                                ? "Shadow "
                                                                : "")
                                                        + punishmentLadderType
                                                                .getType()
                                                                .getNiceName())
                                        .replace(
                                                "<duration>",
                                                punishmentLadderType.getDuration() == -5L
                                                        ? TimeConstants.PERMANENT
                                                        : DateUtils.formatTimeMillis(
                                                                punishmentLadderType.getDuration()))
                                        .replace("<leftAction>", text)
                                        .replace("<rightAction>", text2));
                    }
                }
            } else {
                Iterator<String> iterator3 =
                        LocaleUtil.tr(player, MenuConfig.PUNISH_MENU.ITEM_LORE).iterator();
                while (iterator3.hasNext()) {
                    String text5;
                    if ((text5 = (String) iterator3.next()).contains("<decay>")) {
                        for (String text6 : LocaleUtil.tr(player, MenuConfig.PUNISH_MENU.DECAY)) {
                            itemBuilder.addLoreLine(
                                    text6.replace(
                                                    "<player>",
                                                    PunishMenu.this.profile
                                                            .getNameWithColorNoDisguise())
                                            .replace(
                                                    "<normal>",
                                                    String.valueOf(
                                                            this.punishmentLadder.getPunishedTimes(
                                                                            punishments)
                                                                    + 1))
                                            .replace(
                                                    "<decayed>",
                                                    String.valueOf(
                                                            punishmentLadderType.getOrder())));
                        }
                    } else {
                        itemBuilder.addLoreLine(
                                text5.replace(
                                                "<player>",
                                                PunishMenu.this.profile
                                                        .getNameWithColorNoDisguise())
                                        .replace(
                                                "<times>",
                                                String.valueOf(
                                                        this.punishmentLadder.getPunishedTimes(
                                                                punishments)))
                                        .replace(
                                                "<type>",
                                                (punishmentLadderType.isIp() ? "IP " : "")
                                                        + (punishmentLadderType.isShadow()
                                                                ? "Shadow "
                                                                : "")
                                                        + punishmentLadderType
                                                                .getType()
                                                                .getNiceName())
                                        .replace(
                                                "<duration>",
                                                punishmentLadderType.getDuration() == -5L
                                                        ? TimeConstants.PERMANENT
                                                        : DateUtils.formatTimeMillis(
                                                                punishmentLadderType.getDuration()))
                                        .replace("<leftAction>", text)
                                        .replace("<rightAction>", text2));
                    }
                }
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            PunishMenu.super
                    .api
                    .getModUtilsHook()
                    .setFrozen(PunishMenu.this.profile.getUniqueId(), false);
            List<xyz.refinedev.phoenix.profile.punishment.IPunishment> punishments =
                    PunishMenu.this.profile.getPunishments();
            IPunishmentLadderType punishmentLadderType;
            if ((punishmentLadderType = this.punishmentLadder.getNextLadder(punishments)) != null) {
                if (PunishMenu.this.profile.hasActivePunishment(punishmentLadderType.getType())
                        && punishmentLadderType.getType() != PunishmentType.KICK
                        && punishmentLadderType.getType() != PunishmentType.WARN) {
                    player.sendMessage(
                            Color.translate(
                                    LocaleUtil.tr(player, MessageConfig.PUNISHMENT.ALREADY_PUNISHED)
                                            .replace(
                                                    "<player>",
                                                    PunishMenu.this.profile
                                                            .getNameWithColorNoDisguise())
                                            .replace(
                                                    "<type>",
                                                    punishmentLadderType.getType().getPunished())));
                } else {
                    IPunishmentLadderType punishmentLadderType2 = punishmentLadderType;
                    ConfirmationMenus.applyPhoenix(
                            PunishMenu.super.plugin,
                            player,
                            "Are you sure?",
                            true,
                            result -> {
                                if (result) {
                                    Punishment punishment;
                                    (punishment = new Punishment())
                                            .setPunishmentType(punishmentLadderType2.getType());
                                    punishment.setTarget(PunishMenu.this.profile.getUniqueId());
                                    punishment.setAddress(
                                            PunishMenu.this.profile.getCurrentAddress());
                                    punishment.setSilent(
                                            SettingsConfig.SERVER
                                                            .PUNISHMENTS
                                                            .AUTO_SILENT_PUNISHMENT_LADDERS
                                                    != clickType.isRightClick());
                                    punishment.setPermanent(
                                            punishmentLadderType2.getDuration() == -5L);
                                    punishment.setDuration(punishmentLadderType2.getDuration());
                                    punishment.setIp(
                                            punishmentLadderType2.isIp()
                                                    || punishmentLadderType2.getType()
                                                            == PunishmentType.BLACKLIST);
                                    punishment.setShadow(punishmentLadderType2.isShadow());
                                    punishment.setIssuedOn(GlobalConfig.SERVER.NAME);
                                    punishment.setIssuedAt(System.currentTimeMillis());
                                    punishment.setReason(this.punishmentLadder.getReason());
                                    punishment.setIssuedBy(player.getUniqueId());
                                    IChatSnapshot chatSnapshot;
                                    if (this.punishmentLadder.isRequireChatSnapshot()
                                            && (chatSnapshot =
                                                            PunishMenu.super
                                                                    .api
                                                                    .getChatSnapshotHandler()
                                                                    .createSnapshot(
                                                                            player.getUniqueId(),
                                                                            PunishMenu.this.profile
                                                                                    .getUniqueId()))
                                                    != null) {
                                        punishment
                                                .getProof()
                                                .add(
                                                        new Proof(
                                                                ProofType.CHAT_SNAPSHOT,
                                                                chatSnapshot.getNiceId(),
                                                                player.getUniqueId()));
                                    }
                                    Phoenix instance = Phoenix.getInstance();
                                    Punishment punishment2 = punishment;
                                    Tasks.runAsync(
                                            instance,
                                            () -> PunishmentUtil.issue(player, punishment2));
                                }
                            });
                }
            }
        }

        public SilentlyButton(IPunishmentLadder punishmentLadder2) {
            this.punishmentLadder = punishmentLadder2;
        }
    }
}
