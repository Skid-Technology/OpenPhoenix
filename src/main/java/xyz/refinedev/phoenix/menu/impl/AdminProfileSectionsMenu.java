package xyz.refinedev.phoenix.menu.impl;

import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.Menu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.PunishmentType;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.TimeConstants;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AdminProfileSectionsMenu extends Menu {

    private final IProfile profile;

    @Override
    public String getTitle(Player player) {
        return LocaleUtil.tr(player, MenuConfig.ADMIN_PROFILE.TITLE)
                .replace("<player>", this.profile.getName());
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> entries;
        (entries = new HashMap())
                .put(
                        MenuConfig.ADMIN_PROFILE.PROFILE_SLOT,
                        new AdminProfileSectionsMenu.ProfileButton());
        this.applyMap(
                entries,
                MenuConfig.ADMIN_PROFILE.GRANT_HISTORY_BUTTON.ENABLED,
                MenuConfig.ADMIN_PROFILE.GRANT_HISTORY_BUTTON.SLOT,
                MenuConfig.ADMIN_PROFILE.GRANT_HISTORY_BUTTON.ITEM,
                MenuConfig.ADMIN_PROFILE.GRANT_HISTORY_BUTTON.DURABILITY,
                MenuConfig.ADMIN_PROFILE.GRANT_HISTORY_BUTTON.NAME,
                MenuConfig.ADMIN_PROFILE.GRANT_HISTORY_BUTTON.LORE,
                "granthistory");
        this.applyMap(
                entries,
                MenuConfig.ADMIN_PROFILE.STAFF_HISTORY_BUTTON.ENABLED,
                MenuConfig.ADMIN_PROFILE.STAFF_HISTORY_BUTTON.SLOT,
                MenuConfig.ADMIN_PROFILE.STAFF_HISTORY_BUTTON.ITEM,
                MenuConfig.ADMIN_PROFILE.STAFF_HISTORY_BUTTON.DURABILITY,
                MenuConfig.ADMIN_PROFILE.STAFF_HISTORY_BUTTON.NAME,
                MenuConfig.ADMIN_PROFILE.STAFF_HISTORY_BUTTON.LORE,
                "staffhist");
        this.applyMap(
                entries,
                MenuConfig.ADMIN_PROFILE.GRANTS_BUTTON.ENABLED,
                MenuConfig.ADMIN_PROFILE.GRANTS_BUTTON.SLOT,
                MenuConfig.ADMIN_PROFILE.GRANTS_BUTTON.ITEM,
                MenuConfig.ADMIN_PROFILE.GRANTS_BUTTON.DURABILITY,
                MenuConfig.ADMIN_PROFILE.GRANTS_BUTTON.NAME,
                MenuConfig.ADMIN_PROFILE.GRANTS_BUTTON.LORE,
                "grants");
        this.applyMap(
                entries,
                MenuConfig.ADMIN_PROFILE.HISTORY_BUTTON.ENABLED,
                MenuConfig.ADMIN_PROFILE.HISTORY_BUTTON.SLOT,
                MenuConfig.ADMIN_PROFILE.HISTORY_BUTTON.ITEM,
                MenuConfig.ADMIN_PROFILE.HISTORY_BUTTON.DURABILITY,
                MenuConfig.ADMIN_PROFILE.HISTORY_BUTTON.NAME,
                MenuConfig.ADMIN_PROFILE.HISTORY_BUTTON.LORE,
                "hist");
        this.applyMap(
                entries,
                MenuConfig.ADMIN_PROFILE.GRANT_BUTTON.ENABLED,
                MenuConfig.ADMIN_PROFILE.GRANT_BUTTON.SLOT,
                MenuConfig.ADMIN_PROFILE.GRANT_BUTTON.ITEM,
                MenuConfig.ADMIN_PROFILE.GRANT_BUTTON.DURABILITY,
                MenuConfig.ADMIN_PROFILE.GRANT_BUTTON.NAME,
                MenuConfig.ADMIN_PROFILE.GRANT_BUTTON.LORE,
                "grant");
        this.applyMap(
                entries,
                MenuConfig.ADMIN_PROFILE.PUNISH_BUTTON.ENABLED,
                MenuConfig.ADMIN_PROFILE.PUNISH_BUTTON.SLOT,
                MenuConfig.ADMIN_PROFILE.PUNISH_BUTTON.ITEM,
                MenuConfig.ADMIN_PROFILE.PUNISH_BUTTON.DURABILITY,
                MenuConfig.ADMIN_PROFILE.PUNISH_BUTTON.NAME,
                MenuConfig.ADMIN_PROFILE.PUNISH_BUTTON.LORE,
                "punish");
        this.applyMap(
                entries,
                MenuConfig.ADMIN_PROFILE.COMMAND_LOGS_BUTTON.ENABLED,
                MenuConfig.ADMIN_PROFILE.COMMAND_LOGS_BUTTON.SLOT,
                MenuConfig.ADMIN_PROFILE.COMMAND_LOGS_BUTTON.ITEM,
                MenuConfig.ADMIN_PROFILE.COMMAND_LOGS_BUTTON.DURABILITY,
                MenuConfig.ADMIN_PROFILE.COMMAND_LOGS_BUTTON.NAME,
                MenuConfig.ADMIN_PROFILE.COMMAND_LOGS_BUTTON.LORE,
                "commandlogs player");
        this.applyMap(
                entries,
                MenuConfig.ADMIN_PROFILE.CHAT_SNAPSHOT_BUTTON.ENABLED,
                MenuConfig.ADMIN_PROFILE.CHAT_SNAPSHOT_BUTTON.SLOT,
                MenuConfig.ADMIN_PROFILE.CHAT_SNAPSHOT_BUTTON.ITEM,
                MenuConfig.ADMIN_PROFILE.CHAT_SNAPSHOT_BUTTON.DURABILITY,
                MenuConfig.ADMIN_PROFILE.CHAT_SNAPSHOT_BUTTON.NAME,
                MenuConfig.ADMIN_PROFILE.CHAT_SNAPSHOT_BUTTON.LORE,
                "chatsnapshot player");
        this.applyMap(
                entries,
                MenuConfig.ADMIN_PROFILE.ADMIN_SOCIAL_MEDIA_BUTTON.ENABLED,
                MenuConfig.ADMIN_PROFILE.ADMIN_SOCIAL_MEDIA_BUTTON.SLOT,
                MenuConfig.ADMIN_PROFILE.ADMIN_SOCIAL_MEDIA_BUTTON.ITEM,
                MenuConfig.ADMIN_PROFILE.ADMIN_SOCIAL_MEDIA_BUTTON.DURABILITY,
                MenuConfig.ADMIN_PROFILE.ADMIN_SOCIAL_MEDIA_BUTTON.NAME,
                MenuConfig.ADMIN_PROFILE.ADMIN_SOCIAL_MEDIA_BUTTON.LORE,
                "socials");
        this.applyMap(
                entries,
                MenuConfig.ADMIN_PROFILE.NOTES_BUTTON.ENABLED,
                MenuConfig.ADMIN_PROFILE.NOTES_BUTTON.SLOT,
                MenuConfig.ADMIN_PROFILE.NOTES_BUTTON.ITEM,
                MenuConfig.ADMIN_PROFILE.NOTES_BUTTON.DURABILITY,
                MenuConfig.ADMIN_PROFILE.NOTES_BUTTON.NAME,
                MenuConfig.ADMIN_PROFILE.NOTES_BUTTON.LORE,
                "notes");
        this.applyMap(
                entries,
                MenuConfig.ADMIN_PROFILE.PERMISSIONS_BUTTON.ENABLED,
                MenuConfig.ADMIN_PROFILE.PERMISSIONS_BUTTON.SLOT,
                MenuConfig.ADMIN_PROFILE.PERMISSIONS_BUTTON.ITEM,
                MenuConfig.ADMIN_PROFILE.PERMISSIONS_BUTTON.DURABILITY,
                MenuConfig.ADMIN_PROFILE.PERMISSIONS_BUTTON.NAME,
                MenuConfig.ADMIN_PROFILE.PERMISSIONS_BUTTON.LORE,
                "user perm list");
        this.applyMap(
                entries,
                MenuConfig.ADMIN_PROFILE.WARNINGS_BUTTON.ENABLED,
                MenuConfig.ADMIN_PROFILE.WARNINGS_BUTTON.SLOT,
                MenuConfig.ADMIN_PROFILE.WARNINGS_BUTTON.ITEM,
                MenuConfig.ADMIN_PROFILE.WARNINGS_BUTTON.DURABILITY,
                MenuConfig.ADMIN_PROFILE.WARNINGS_BUTTON.NAME,
                MenuConfig.ADMIN_PROFILE.WARNINGS_BUTTON.LORE,
                "warnings");
        this.applyMap(
                entries,
                MenuConfig.ADMIN_PROFILE.PLAYTIME_BUTTON.ENABLED,
                MenuConfig.ADMIN_PROFILE.PLAYTIME_BUTTON.SLOT,
                MenuConfig.ADMIN_PROFILE.PLAYTIME_BUTTON.ITEM,
                MenuConfig.ADMIN_PROFILE.PLAYTIME_BUTTON.DURABILITY,
                MenuConfig.ADMIN_PROFILE.PLAYTIME_BUTTON.NAME,
                MenuConfig.ADMIN_PROFILE.PLAYTIME_BUTTON.LORE,
                "playtime");
        this.applyMap(
                entries,
                MenuConfig.ADMIN_PROFILE.CHAT_LOGS_BUTTON.ENABLED,
                MenuConfig.ADMIN_PROFILE.CHAT_LOGS_BUTTON.SLOT,
                MenuConfig.ADMIN_PROFILE.CHAT_LOGS_BUTTON.ITEM,
                MenuConfig.ADMIN_PROFILE.CHAT_LOGS_BUTTON.DURABILITY,
                MenuConfig.ADMIN_PROFILE.CHAT_LOGS_BUTTON.NAME,
                MenuConfig.ADMIN_PROFILE.CHAT_LOGS_BUTTON.LORE,
                "chatlogs view");
        this.applyMap(
                entries,
                MenuConfig.ADMIN_PROFILE.LOCATION_LOG_BUTTON.ENABLED,
                MenuConfig.ADMIN_PROFILE.LOCATION_LOG_BUTTON.SLOT,
                MenuConfig.ADMIN_PROFILE.LOCATION_LOG_BUTTON.ITEM,
                MenuConfig.ADMIN_PROFILE.LOCATION_LOG_BUTTON.DURABILITY,
                MenuConfig.ADMIN_PROFILE.LOCATION_LOG_BUTTON.NAME,
                MenuConfig.ADMIN_PROFILE.LOCATION_LOG_BUTTON.LORE,
                "alts locations");
        this.applyMap(
                entries,
                MenuConfig.ADMIN_PROFILE.SEARCH_IPS_BUTTON.ENABLED,
                MenuConfig.ADMIN_PROFILE.SEARCH_IPS_BUTTON.SLOT,
                MenuConfig.ADMIN_PROFILE.SEARCH_IPS_BUTTON.ITEM,
                MenuConfig.ADMIN_PROFILE.SEARCH_IPS_BUTTON.DURABILITY,
                MenuConfig.ADMIN_PROFILE.SEARCH_IPS_BUTTON.NAME,
                MenuConfig.ADMIN_PROFILE.SEARCH_IPS_BUTTON.LORE,
                "alts ips");
        this.applyMap(
                entries,
                MenuConfig.ADMIN_PROFILE.ALTS_BUTTON.ENABLED,
                MenuConfig.ADMIN_PROFILE.ALTS_BUTTON.SLOT,
                MenuConfig.ADMIN_PROFILE.ALTS_BUTTON.ITEM,
                MenuConfig.ADMIN_PROFILE.ALTS_BUTTON.DURABILITY,
                MenuConfig.ADMIN_PROFILE.ALTS_BUTTON.NAME,
                MenuConfig.ADMIN_PROFILE.ALTS_BUTTON.LORE,
                "alts");
        if (MenuConfig.ADMIN_PROFILE.SECTIONS.ENABLED) {
            entries.put(
                    MenuConfig.ADMIN_PROFILE.SECTIONS.ACCOUNT_SLOT,
                    new AdminProfileSectionsMenu.DisguiseNameButton(
                            MenuConfig.ADMIN_PROFILE.SECTIONS.ACCOUNT_NAME,
                            MenuConfig.ADMIN_PROFILE.SECTIONS.ACCOUNT_DURABILITY));
            entries.put(
                    MenuConfig.ADMIN_PROFILE.SECTIONS.RECORD_SLOT,
                    new AdminProfileSectionsMenu.DisguiseNameButton(
                            MenuConfig.ADMIN_PROFILE.SECTIONS.RECORD_NAME,
                            MenuConfig.ADMIN_PROFILE.SECTIONS.RECORD_DURABILITY));
            entries.put(
                    MenuConfig.ADMIN_PROFILE.SECTIONS.CHECKS_SLOT,
                    new AdminProfileSectionsMenu.DisguiseNameButton(
                            MenuConfig.ADMIN_PROFILE.SECTIONS.CHECKS_NAME,
                            MenuConfig.ADMIN_PROFILE.SECTIONS.CHECKS_DURABILITY));
            entries.put(
                    MenuConfig.ADMIN_PROFILE.SECTIONS.ACTIONS_SLOT,
                    new AdminProfileSectionsMenu.DisguiseNameButton(
                            MenuConfig.ADMIN_PROFILE.SECTIONS.ACTIONS_NAME,
                            MenuConfig.ADMIN_PROFILE.SECTIONS.ACTIONS_DURABILITY));
        }
        for (int index = 0; index < MenuConfig.ADMIN_PROFILE.MENU_SIZE; index++) {
            entries.computeIfAbsent(index, result -> Button.placeholder());
        }
        return entries;
    }

    private void applyMap(
            Map<Integer, Button> entries,
            Boolean matches,
            Integer value,
            String text,
            Integer value2,
            String text2,
            List<String> values,
            String text3) {
        if (matches != null && matches) {
            entries.put(
                    value,
                    new AdminProfileSectionsMenu.EntryButton(text, value2, text2, values, text3));
        }
    }

    private String formatString(String text, Player player) {
        if (text.indexOf(60) < 0) {
            return text;
        }
        String text2;
        if ((text2 = text.replace("<target>", this.profile.getNameWithColorNoDisguise()))
                        .contains("<punishments>")
                || text2.contains("<activePunishments>")
                || text2.contains("<warnings>")) {
            int value = 0;
            int value2 = 0;
            int value3 = 0;
            Iterator<IPunishment> iterator2 = this.profile.getPunishments().iterator();
            while (iterator2.hasNext()) {
                IPunishment punishment;
                if (!(punishment = (IPunishment) iterator2.next()).isVoided()) {
                    value++;
                    if (punishment.isActive()) {
                        value2++;
                    }
                    if (punishment.getPunishmentType() == PunishmentType.WARN) {
                        value3++;
                    }
                }
            }
            text2 =
                    text2.replace("<punishments>", String.valueOf(value))
                            .replace("<activePunishments>", String.valueOf(value2))
                            .replace("<warnings>", String.valueOf(value3));
        }
        if (text2.contains("<grants>")) {
            text2 = text2.replace("<grants>", String.valueOf(this.profile.getGrants().size()));
        }
        if (text2.contains("<activeGrants>")) {
            text2 =
                    text2.replace(
                            "<activeGrants>",
                            String.valueOf(this.profile.getActiveGrants().size()));
        }
        if (text2.contains("<alts>")) {
            text2 = text2.replace("<alts>", String.valueOf(this.profile.getAlts().size()));
        }
        if (text2.contains("<notes>")) {
            text2 = text2.replace("<notes>", String.valueOf(this.profile.getNotes().size()));
        }
        if (text2.contains("<permissions>")) {
            text2 =
                    text2.replace(
                            "<permissions>", String.valueOf(this.profile.getPermissions().size()));
        }
        if (text2.contains("<playtime>")) {
            text2 =
                    text2.replace(
                            "<playtime>",
                            DateUtils.formatTimeMillis(
                                    this.profile.getPlayTime(
                                            super.api
                                                    .getLoginHandler()
                                                    .getLogins(this.profile.getUniqueId()))));
        }
        return text2;
    }

    public AdminProfileSectionsMenu(IProfile profile2) {
        this.profile = profile2;
    }

    static class DisguiseNameButton extends Button {

        private final String text;

        private final int count;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(MenuConfig.ADMIN_PROFILE.SECTIONS.ITEM))
                    .setDurability(this.count);
            itemBuilder.setName(LocaleUtil.tr(player, this.text));
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {}

        public DisguiseNameButton(String text, int count) {
            this.text = text;
            this.count = count;
        }
    }

    class EntryButton extends Button {

        private final String text;

        private final int count;

        private final String textString;

        private final List<String> strings;

        private final String text2;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(this.text)).setDurability(this.count);
            itemBuilder.setName(LocaleUtil.tr(player, this.textString));
            for (String text2 : LocaleUtil.tr(player, this.strings)) {
                itemBuilder.addLoreLine(AdminProfileSectionsMenu.this.formatString(text2, player));
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            player.performCommand(
                    this.text2 + " " + AdminProfileSectionsMenu.this.profile.getName());
        }

        public EntryButton(
                String text, int count, String textString, List<String> strings, String text2) {
            this.text = text;
            this.count = count;
            this.textString = textString;
            this.strings = strings;
            this.text2 = text2;
        }
    }

    class ProfileButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            List<xyz.refinedev.phoenix.profile.login.ILogin> logins =
                    AdminProfileSectionsMenu.super
                            .api
                            .getLoginHandler()
                            .getLogins(AdminProfileSectionsMenu.this.profile.getUniqueId());
            ItemBuilder itemBuilder;
            if (MenuConfig.ADMIN_PROFILE.USE_HEAD_AS_ITEM) {
                (itemBuilder =
                                new ItemBuilder(
                                        XMaterial.PLAYER_HEAD.get(),
                                        1,
                                        (short) SkullType.PLAYER.ordinal()))
                        .setSkullOwner(AdminProfileSectionsMenu.this.profile.getName());
                itemBuilder.setName(
                        LocaleUtil.tr(player, MenuConfig.ADMIN_PROFILE.PROFILE_NAME)
                                .replace(
                                        "<profile>",
                                        AdminProfileSectionsMenu.this.profile.getNameWithColor())
                                .replace(
                                        "<disguised>",
                                        AdminProfileSectionsMenu.this
                                                        .profile
                                                        .getDisguiseData()
                                                        .isDisguised()
                                                ? LocaleUtil.tr(
                                                        player, MenuConfig.ADMIN_PROFILE.DISGUISED)
                                                : ""));
            } else {
                (itemBuilder =
                                new ItemBuilder(
                                        LocaleUtil.tr(
                                                player, MenuConfig.ADMIN_PROFILE.PROFILE_ITEM)))
                        .setDurability(MenuConfig.ADMIN_PROFILE.PROFILE_ITEM_DAMAGE);
                itemBuilder.setName(
                        LocaleUtil.tr(player, MenuConfig.ADMIN_PROFILE.PROFILE_NAME)
                                .replace(
                                        "<profile>",
                                        AdminProfileSectionsMenu.this.profile.getNameWithColor())
                                .replace(
                                        "<disguised>",
                                        AdminProfileSectionsMenu.this
                                                        .profile
                                                        .getDisguiseData()
                                                        .isDisguised()
                                                ? LocaleUtil.tr(
                                                        player, MenuConfig.ADMIN_PROFILE.DISGUISED)
                                                : ""));
            }
            for (String text : LocaleUtil.tr(player, MenuConfig.ADMIN_PROFILE.PROFILE_LORE)) {
                String text2 =
                        !AdminProfileSectionsMenu.this.profile.isBanned()
                                        && !AdminProfileSectionsMenu.this.profile.isBlacklisted()
                                        && !AdminProfileSectionsMenu.this.profile.isMuted()
                                        && !AdminProfileSectionsMenu.this.profile
                                                .isCompetitiveBanned()
                                ? LocaleUtil.tr(player, MenuConfig.ADMIN_PROFILE.FALSE)
                                : LocaleUtil.tr(player, MenuConfig.ADMIN_PROFILE.TRUE);
                IPunishment punishment = null;
                if (AdminProfileSectionsMenu.this.profile.getActiveBan() != null) {
                    punishment = AdminProfileSectionsMenu.this.profile.getActiveBan();
                }
                if (AdminProfileSectionsMenu.this.profile.getActiveBlacklist() != null) {
                    punishment = AdminProfileSectionsMenu.this.profile.getActiveBlacklist();
                }
                if (AdminProfileSectionsMenu.this.profile.getActiveMute() != null) {
                    punishment = AdminProfileSectionsMenu.this.profile.getActiveMute();
                }
                if (AdminProfileSectionsMenu.this.profile.getActiveCompetitiveBan() != null) {
                    punishment = AdminProfileSectionsMenu.this.profile.getActiveCompetitiveBan();
                }
                if (text.contains("<noDisguiseName>")) {
                    if (AdminProfileSectionsMenu.this.profile.getDisguiseData().isDisguised()) {
                        itemBuilder.addLoreLine(
                                LocaleUtil.tr(
                                                player,
                                                MenuConfig.ADMIN_PROFILE.PROFILE_NO_DISGUISED_NAME)
                                        .replace(
                                                "<name>",
                                                AdminProfileSectionsMenu.this.profile
                                                        .getNameWithColorNoDisguise()));
                    }
                } else {
                    itemBuilder.addLoreLine(
                            text.replace(
                                            "<rank>",
                                            AdminProfileSectionsMenu.this
                                                    .profile
                                                    .getHighestRank()
                                                    .getDisplayName())
                                    .replace(
                                            "<rankDuration>",
                                            AdminProfileSectionsMenu.this.profile.getBestGrant()
                                                            != null
                                                    ? AdminProfileSectionsMenu.this
                                                            .profile
                                                            .getBestGrant()
                                                            .getNiceDuration()
                                                    : TimeConstants.PERMANENT)
                                    .replace("<punished>", text2)
                                    .replace(
                                            "<punishmentType>",
                                            punishment != null
                                                    ? punishment.getPunishmentType().getNiceName()
                                                    : LocaleUtil.tr(
                                                            player,
                                                            MenuConfig.ADMIN_PROFILE
                                                                    .PUNISHMENT_TYPE_NONE))
                                    .replace(
                                            "<firstLogin>",
                                            AdminProfileSectionsMenu.this.profile.getFirstLogin(
                                                    logins))
                                    .replace(
                                            "<lastLogin>",
                                            AdminProfileSectionsMenu.this.profile.getLastLogin(
                                                    logins))
                                    .replace("<logins>", String.valueOf(logins.size()))
                                    .replace(
                                            "<2fa>",
                                            !AdminProfileSectionsMenu.this
                                                            .profile
                                                            .getSecretKey()
                                                            .equalsIgnoreCase("")
                                                    ? LocaleUtil.tr(
                                                            player, MenuConfig.ADMIN_PROFILE.TRUE)
                                                    : LocaleUtil.tr(
                                                            player, MenuConfig.ADMIN_PROFILE.FALSE))
                                    .replace(
                                            "<security>",
                                            AdminProfileSectionsMenu.super
                                                            .api
                                                            .getSecurityHandler()
                                                            .hasSecurity(
                                                                    AdminProfileSectionsMenu.this
                                                                            .profile.getUniqueId())
                                                    ? LocaleUtil.tr(
                                                            player, MenuConfig.ADMIN_PROFILE.TRUE)
                                                    : LocaleUtil.tr(
                                                            player,
                                                            MenuConfig.ADMIN_PROFILE.FALSE)));
                }
            }
            return itemBuilder.toItemStack();
        }

        public ProfileButton() {}
    }
}
