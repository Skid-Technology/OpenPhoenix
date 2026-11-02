package xyz.refinedev.phoenix.menu.impl;

import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.dialog.api.PhoenixDialog;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.Menu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.HashMap;
import java.util.Map;

public class ProfileMenu extends Menu {

    private final IProfile profile;

    private final Boolean enabled;

    @Override
    public String getTitle(Player player) {
        return this.enabled
                ? LocaleUtil.tr(player, MenuConfig.PROFILE.SELF_TITLE)
                        .replace("<profile>", this.profile.getNameWithColor())
                : LocaleUtil.tr(player, MenuConfig.PROFILE.TITLE)
                        .replace("<profile>", this.profile.getNameWithColor());
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        if (this.enabled) {
            entries.put(4, new ProfileMenu.ProfileButton());
            entries.put(19, new ProfileMenu.SocialMediaButton());
            entries.put(21, new ProfileMenu.EquippedCosmeticsButton());
            entries.put(23, new ProfileMenu.EntryButton());
            entries.put(25, new ProfileMenu.ViewFriendsButton());
            for (int index = 0; index < 45; index++) {
                entries.computeIfAbsent(index, result -> Button.placeholder());
            }
        } else {
            entries.put(4, new ProfileMenu.ProfileButton());
            entries.put(19, new ProfileMenu.SocialMediaButton());
            entries.put(21, new ProfileMenu.IgnoreButton());
            entries.put(23, new ProfileMenu.ReportButton());
            entries.put(25, new ProfileMenu.SettingsButton());
            for (int index2 = 0; index2 < 45; index2++) {
                entries.computeIfAbsent(index2, result -> Button.placeholder());
            }
        }
        return entries;
    }

    public ProfileMenu(IProfile profile2, Boolean enabled) {
        this.profile = profile2;
        this.enabled = enabled;
    }

    class EntryButton extends Button {

        private static final String text = "/settings";

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(MenuConfig.PROFILE.SETTINGS_BUTTON.ITEM))
                    .setDurability(MenuConfig.PROFILE.SETTINGS_BUTTON.DURABILITY);
            itemBuilder.setName(LocaleUtil.tr(player, MenuConfig.PROFILE.SETTINGS_BUTTON.NAME));
            for (String text : LocaleUtil.tr(player, MenuConfig.PROFILE.SETTINGS_BUTTON.LORE)) {
                itemBuilder.addLoreLine(text);
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            player.chat(text);
        }

        public EntryButton() {}
    }

    class EquippedCosmeticsButton extends Button {

        private static final String text = "/cosmetics";

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(MenuConfig.PROFILE.EQUIPPED_COSMETICS_BUTTON.ITEM))
                    .setDurability(MenuConfig.PROFILE.EQUIPPED_COSMETICS_BUTTON.DURABILITY);
            itemBuilder.setName(
                    LocaleUtil.tr(player, MenuConfig.PROFILE.EQUIPPED_COSMETICS_BUTTON.NAME));
            for (String text :
                    LocaleUtil.tr(player, MenuConfig.PROFILE.EQUIPPED_COSMETICS_BUTTON.LORE)) {
                itemBuilder.addLoreLine(text);
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.chat(text);
        }

        public EquippedCosmeticsButton() {}
    }

    class IgnoreButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            IProfile cachedProfile =
                    ProfileMenu.super
                            .api
                            .getProfileHandler()
                            .getCachedProfile(player.getUniqueId());
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(MenuConfig.PROFILE.IGNORE_BUTTON.ITEM))
                    .setDurability(MenuConfig.PROFILE.IGNORE_BUTTON.DURABILITY);
            if (cachedProfile.isIgnoring(ProfileMenu.this.profile.getUniqueId())) {
                itemBuilder.setName(
                        LocaleUtil.tr(player, MenuConfig.PROFILE.IGNORE_BUTTON.IGNORING_NAME));
                for (String text :
                        LocaleUtil.tr(player, MenuConfig.PROFILE.IGNORE_BUTTON.IGNORING_LORE)) {
                    itemBuilder.addLoreLine(
                            text.replace("<player>", ProfileMenu.this.profile.getNameWithColor()));
                }
            } else {
                itemBuilder.setName(
                        LocaleUtil.tr(player, MenuConfig.PROFILE.IGNORE_BUTTON.NOT_IGNORING_NAME));
                for (String text2 :
                        LocaleUtil.tr(player, MenuConfig.PROFILE.IGNORE_BUTTON.NOT_IGNORING_LORE)) {
                    itemBuilder.addLoreLine(
                            text2.replace("<player>", ProfileMenu.this.profile.getNameWithColor()));
                }
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            IProfile cachedProfile =
                    ProfileMenu.super
                            .api
                            .getProfileHandler()
                            .getCachedProfile(player.getUniqueId());
            player.closeInventory();
            if (cachedProfile.isIgnoring(ProfileMenu.this.profile.getUniqueId())) {
                player.chat("/ignore remove " + ProfileMenu.this.profile.getName());
            } else {
                player.chat("/ignore add " + ProfileMenu.this.profile.getName());
            }
        }

        public IgnoreButton() {}
    }

    class ProfileButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            if (MenuConfig.PROFILE.USE_HEAD_AS_ITEM) {
                (itemBuilder =
                                new ItemBuilder(
                                        XMaterial.PLAYER_HEAD.get(),
                                        1,
                                        (short) SkullType.PLAYER.ordinal()))
                        .setSkullOwner(ProfileMenu.this.profile.getName());
                itemBuilder.setName(
                        LocaleUtil.tr(player, MenuConfig.PROFILE.PROFILE_NAME)
                                .replace("<profile>", ProfileMenu.this.profile.getNameWithColor()));
            } else {
                (itemBuilder =
                                new ItemBuilder(
                                        LocaleUtil.tr(player, MenuConfig.PROFILE.PROFILE_ITEM)))
                        .setDurability(MenuConfig.PROFILE.PROFILE_ITEM_DAMAGE);
                itemBuilder.setName(
                        LocaleUtil.tr(player, MenuConfig.PROFILE.PROFILE_NAME)
                                .replace("<profile>", ProfileMenu.this.profile.getNameWithColor()));
            }
            for (String text : LocaleUtil.tr(player, MenuConfig.PROFILE.PROFILE_LORE)) {
                String nicePresence =
                        ProfileMenu.super
                                .api
                                .getNetworkHandler()
                                .getNicePresence(ProfileMenu.this.profile.getUniqueId());
                itemBuilder.addLoreLine(
                        text.replace(
                                        "<rank>",
                                        ProfileMenu.this.profile.getHighestRank().getDisplayName())
                                .replace(
                                        "<experience>",
                                        String.valueOf(
                                                ProfileMenu.super
                                                        .api
                                                        .getLevelHook()
                                                        .getExperience(ProfileMenu.this.profile)))
                                .replace(
                                        "<level>",
                                        ProfileMenu.super
                                                .api
                                                .getLevelHook()
                                                .getLevelNameWithColor(ProfileMenu.this.profile))
                                .replace(
                                        "<color>",
                                        ProfileMenu.super
                                                .api
                                                .getCosmeticsHook()
                                                .getColor(ProfileMenu.this.profile.getUniqueId()))
                                .replace("<player>", ProfileMenu.this.profile.getName())
                                .replace(
                                        "<cc>",
                                        ProfileMenu.super
                                                .api
                                                .getCosmeticsHook()
                                                .getChatColor(
                                                        ProfileMenu.this.profile.getUniqueId()))
                                .replace("<tag>", ProfileMenu.this.profile.getTag().getPrefix())
                                .replace(
                                        "<status>",
                                        nicePresence.equalsIgnoreCase("N/A")
                                                ? Color.translate(
                                                        LocaleUtil.tr(
                                                                player, MenuConfig.PROFILE.OFFLINE))
                                                : Color.translate(
                                                        LocaleUtil.tr(
                                                                        player,
                                                                        MenuConfig.PROFILE.ONLINE)
                                                                .replace(
                                                                        "<server>",
                                                                        ProfileMenu.super
                                                                                .plugin
                                                                                .getNetworkHandler()
                                                                                .getServerByName(
                                                                                        nicePresence)
                                                                                .getDisplayName()))));
            }
            return itemBuilder.toItemStack();
        }

        public ProfileButton() {}
    }

    class ReportButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(MenuConfig.PROFILE.REPORT_BUTTON.ITEM))
                    .setDurability(MenuConfig.PROFILE.REPORT_BUTTON.DURABILITY);
            itemBuilder.setName(LocaleUtil.tr(player, MenuConfig.PROFILE.REPORT_BUTTON.NAME));
            for (String text : LocaleUtil.tr(player, MenuConfig.PROFILE.REPORT_BUTTON.LORE)) {
                itemBuilder.addLoreLine(
                        text.replace("<player>", ProfileMenu.this.profile.getNameWithColor()));
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            player.chat("/report " + ProfileMenu.this.profile.getName());
        }

        public ReportButton() {}
    }

    class SettingsButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(MenuConfig.PROFILE.SETTINGS_BUTTON.ITEM))
                    .setDurability(MenuConfig.PROFILE.SETTINGS_BUTTON.DURABILITY);
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
        }

        public SettingsButton() {}
    }

    class SocialMediaButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(MenuConfig.PROFILE.SOCIAL_MEDIA_BUTTON.ITEM))
                    .setDurability(MenuConfig.PROFILE.SOCIAL_MEDIA_BUTTON.DURABILITY);
            itemBuilder.setName(LocaleUtil.tr(player, MenuConfig.PROFILE.SOCIAL_MEDIA_BUTTON.NAME));
            for (String text : LocaleUtil.tr(player, MenuConfig.PROFILE.SOCIAL_MEDIA_BUTTON.LORE)) {
                itemBuilder.addLoreLine(
                        text.replace("<player>", ProfileMenu.this.profile.getNameWithColor()));
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (ProfileMenu.this.enabled) {
                PhoenixDialog phoenixDialog;
                if (SettingsConfig.MODERN.DIALOGS.SOCIALS
                        && (phoenixDialog =
                                        ProfileMenu.super
                                                .plugin
                                                .getDialogHandler()
                                                .findPhoenixDialog("socials"))
                                != null
                        && ProfileMenu.super
                                .plugin
                                .getDialogHandler()
                                .matchesEnabled(player, phoenixDialog, null)) {
                    return;
                }
                ProfileMenu.super
                        .plugin
                        .getMenuHandler()
                        .openMenu(new SocialMediaMenu(ProfileMenu.this.profile, true), player);
            } else {
                ProfileMenu.super
                        .plugin
                        .getMenuHandler()
                        .openMenu(new SocialMediaMenu(ProfileMenu.this.profile, false), player);
            }
        }

        public SocialMediaButton() {}
    }

    class ViewFriendsButton extends Button {

        private static final String text = "/f menu";

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(MenuConfig.PROFILE.VIEW_FRIENDS_BUTTON.ITEM))
                    .setDurability(MenuConfig.PROFILE.VIEW_FRIENDS_BUTTON.DURABILITY);
            itemBuilder.setName(LocaleUtil.tr(player, MenuConfig.PROFILE.VIEW_FRIENDS_BUTTON.NAME));
            for (String text : LocaleUtil.tr(player, MenuConfig.PROFILE.VIEW_FRIENDS_BUTTON.LORE)) {
                itemBuilder.addLoreLine(text);
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.chat(text);
        }

        public ViewFriendsButton() {}
    }
}
