package xyz.refinedev.phoenix.menu.impl.tags;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.MenuHandler;
import xyz.refinedev.phoenix.menu.api.menus.ConfirmMenu;
import xyz.refinedev.phoenix.menu.api.pagination.PaginatedMenu;
import xyz.refinedev.phoenix.menu.button.TagButton;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.tag.CustomTagStatus;
import xyz.refinedev.phoenix.profile.tag.ICustomTag;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.HashMap;
import java.util.Map;

public class CustomTagsAdminMenu extends PaginatedMenu {

    @Override
    public String getTitle(Player player) {
        return LocaleUtil.tr(player, MenuConfig.CUSTOM_TAGS_ADMIN.TITLE);
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        for (ICustomTag customTag : super.api.getCustomTagHandler().getUnconfirmedTags()) {
            entries.put(entries.size(), new CustomTagsAdminMenu.EntryButton(customTag));
        }
        return entries;
    }

    class EntryButton extends Button {

        private final ICustomTag customTag;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder = new ItemBuilder(XMaterial.NAME_TAG.get());
            IProfile profile =
                    CustomTagsAdminMenu.super
                            .api
                            .getProfileHandler()
                            .getProfile(this.customTag.getCreator());
            itemBuilder.setName(
                    LocaleUtil.tr(player, MenuConfig.CUSTOM_TAGS_ADMIN.NAME)
                            .replace("<tag>", this.customTag.getDisplayName())
                            .replace(
                                    "<creator>",
                                    profile == null
                                            ? this.customTag.getCreator().toString()
                                            : profile.getNameWithColorNoDisguise())
                            .replace(
                                    "<createdOn>",
                                    DateUtils.getDate(this.customTag.getCreatedOn())));
            for (String text : LocaleUtil.tr(player, MenuConfig.CUSTOM_TAGS_ADMIN.LORE)) {
                itemBuilder.addLoreLine(
                        text.replace("<tag>", this.customTag.getDisplayName())
                                .replace(
                                        "<creator>",
                                        profile == null
                                                ? this.customTag.getCreator().toString()
                                                : profile.getNameWithColorNoDisguise())
                                .replace(
                                        "<createdOn>",
                                        DateUtils.getDate(this.customTag.getCreatedOn())));
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            IProfile profile;
            if ((profile =
                            CustomTagsAdminMenu.super
                                    .api
                                    .getProfileHandler()
                                    .getProfile(this.customTag.getCreator()))
                    != null) {
                int intSetting = profile.getIntSetting("custom-tag-tokens", 0);
                if (clickType.isLeftClick()) {
                    MenuHandler menuHandler = CustomTagsAdminMenu.super.plugin.getMenuHandler();
                    String text =
                            LocaleUtil.tr(
                                            player,
                                            MenuConfig.CUSTOM_TAGS_ADMIN.CONFIRMATION_TITLE_ACCEPT)
                                    .replace("<tag>", this.customTag.getDisplayName());
                    IProfile profile2 = profile;
                    menuHandler.openMenu(
                            new ConfirmMenu(
                                    text,
                                    result -> {
                                        if (result) {
                                            player.closeInventory();
                                            this.customTag.setStatus(CustomTagStatus.ACCEPTED);
                                            CustomTagsAdminMenu.super
                                                    .api
                                                    .getCustomTagHandler()
                                                    .updateTag(this.customTag);
                                            player.sendMessage(
                                                    Color.translate(
                                                            LocaleUtil.tr(
                                                                            player,
                                                                            MessageConfig
                                                                                    .CUSTOM_TAGS_ADMIN
                                                                                    .ACCEPTED)
                                                                    .replace(
                                                                            "<tag>",
                                                                            this.customTag
                                                                                    .getDisplayName())
                                                                    .replace(
                                                                            "<target>",
                                                                            profile2
                                                                                    .getNameWithColorNoDisguise())));
                                        } else {
                                            player.closeInventory();
                                            player.sendMessage(
                                                    Color.translate(
                                                            LocaleUtil.tr(
                                                                            player,
                                                                            MessageConfig
                                                                                    .CUSTOM_TAGS_ADMIN
                                                                                    .ACCEPT_CANCELLED)
                                                                    .replace(
                                                                            "<tag>",
                                                                            this.customTag
                                                                                    .getDisplayName())
                                                                    .replace(
                                                                            "<target>",
                                                                            profile2
                                                                                    .getNameWithColorNoDisguise())));
                                        }
                                    },
                                    true,
                                    new TagButton(this.customTag, CustomTagsAdminMenu.super.api)),
                            player);
                } else if (clickType.isRightClick()) {
                    MenuHandler menuHandler2 = CustomTagsAdminMenu.super.plugin.getMenuHandler();
                    String text2 =
                            LocaleUtil.tr(
                                            player,
                                            MenuConfig.CUSTOM_TAGS_ADMIN.CONFIRMATION_TITLE_DENY)
                                    .replace("<tag>", this.customTag.getDisplayName());
                    IProfile profile3 = profile;
                    menuHandler2.openMenu(
                            new ConfirmMenu(
                                    text2,
                                    result2 -> {
                                        if (result2) {
                                            player.closeInventory();
                                            this.customTag.setStatus(CustomTagStatus.DENIED);
                                            CustomTagsAdminMenu.super
                                                    .api
                                                    .getCustomTagHandler()
                                                    .updateTag(this.customTag);
                                            int value = intSetting + 1;
                                            profile3.updateMetadata(
                                                    "custom-tag-tokens",
                                                    String.valueOf(value),
                                                    true);
                                            player.sendMessage(
                                                    Color.translate(
                                                            LocaleUtil.tr(
                                                                            player,
                                                                            MessageConfig
                                                                                    .CUSTOM_TAGS_ADMIN
                                                                                    .DENIED)
                                                                    .replace(
                                                                            "<tag>",
                                                                            this.customTag
                                                                                    .getDisplayName())
                                                                    .replace(
                                                                            "<target>",
                                                                            profile3
                                                                                    .getNameWithColorNoDisguise())));
                                        } else {
                                            player.closeInventory();
                                            player.sendMessage(
                                                    Color.translate(
                                                            LocaleUtil.tr(
                                                                            player,
                                                                            MessageConfig
                                                                                    .CUSTOM_TAGS_ADMIN
                                                                                    .DENY_CANCELLED)
                                                                    .replace(
                                                                            "<tag>",
                                                                            this.customTag
                                                                                    .getDisplayName())
                                                                    .replace(
                                                                            "<target>",
                                                                            profile3
                                                                                    .getNameWithColorNoDisguise())));
                                        }
                                    },
                                    true,
                                    new TagButton(this.customTag, CustomTagsAdminMenu.super.api)),
                            player);
                }
            }
        }

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }

        public EntryButton(ICustomTag customTag2) {
            this.customTag = customTag2;
        }
    }
}
