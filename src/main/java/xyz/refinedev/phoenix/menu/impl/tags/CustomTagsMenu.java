package xyz.refinedev.phoenix.menu.impl.tags;

import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.lib.xseries.XEnchantment;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.MenuHandler;
import xyz.refinedev.phoenix.menu.api.menus.ConfirmMenu;
import xyz.refinedev.phoenix.menu.api.pagination.PaginatedMenu;
import xyz.refinedev.phoenix.menu.button.SelectableTagButton;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.tag.CustomTag;
import xyz.refinedev.phoenix.profile.tag.CustomTagStatus;
import xyz.refinedev.phoenix.profile.tag.ICustomTag;
import xyz.refinedev.phoenix.profile.tag.ITag;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.Utilities;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.chat.ConversationUtil;
import xyz.refinedev.phoenix.utils.general.Tasks;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;
import xyz.refinedev.phoenix.utils.reflection.Reflection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class CustomTagsMenu extends PaginatedMenu {

    private final List<ICustomTag> customTags;

    @Override
    public String getTitle(Player player) {
        return LocaleUtil.tr(player, MenuConfig.CUSTOM_TAGS.TITLE);
    }

    @Override
    public void onClose(Player player) {
        super.api.getProfileHandler().getCachedProfile(player.getUniqueId()).saveProfile();
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        for (ICustomTag customTag : this.customTags) {
            entries.put(entries.size(), new CustomTagsMenu.CustomTagButton(customTag));
        }
        return entries;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        IProfile profile;
        if ((profile = super.api.getProfileHandler().getCachedProfile(player.getUniqueId()))
                                .getTag()
                        != null
                && profile.getCustomTag() != null) {
            entries.put(3, new CustomTagsMenu.CreateCustomTagButton());
            entries.put(5, new CustomTagsMenu.EntryButton());
        } else {
            entries.put(4, new CustomTagsMenu.CreateCustomTagButton());
        }
        return entries;
    }

    public CustomTagsMenu(List<ICustomTag> customTags) {
        this.customTags = customTags;
    }

    class CreateCustomTagButton extends Button {

        private CreateCustomTagButton() {}

        @Override
        public ItemStack getItem(Player player) {
            int intSetting =
                    CustomTagsMenu.super
                            .api
                            .getProfileHandler()
                            .getCachedProfile(player.getUniqueId())
                            .getIntSetting("custom-tag-tokens", 0);
            ItemBuilder itemBuilder;
            (itemBuilder =
                            new ItemBuilder(
                                    XMaterial.PLAYER_HEAD.get(),
                                    1,
                                    (short) SkullType.PLAYER.ordinal()))
                    .setName(LocaleUtil.tr(player, MenuConfig.CUSTOM_TAGS.CREATE_BUTTON_NAME));
            for (String text : LocaleUtil.tr(player, MenuConfig.CUSTOM_TAGS.CREATE_BUTTON_LORE)) {
                itemBuilder.addLoreLine(text.replace("<tokens>", String.valueOf(intSetting)));
            }
            return Reflection.itemWithBase64(
                    itemBuilder.toItemStack(),
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA1NmJjMTI0NGZjZmY5OTM0NGYxMmFiYTQyYWMyM2ZlZTZlZjZlMzM1MWQyN2QyNzNjMTU3MjUzMWYifX19");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            IProfile profile;
            int value;
            if ((value =
                            (profile =
                                            CustomTagsMenu.super
                                                    .api
                                                    .getProfileHandler()
                                                    .getCachedProfile(player.getUniqueId()))
                                    .getIntSetting("custom-tag-tokens", 0))
                    < 1) {
                player.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(
                                        player, MessageConfig.CUSTOM_TAGS.NOT_ENOUGH_TOKENS)));
            } else {
                player.closeInventory();
                Phoenix phoenix = CustomTagsMenu.super.plugin;
                String text = LocaleUtil.tr(player, MessageConfig.CUSTOM_TAGS.PROMPT_CREATE_TAG);
                IProfile profile2 = profile;
                int value2 = value;
                Consumer<String> consumer =
                        result -> {
                            if (result.length() > SettingsConfig.SERVER.MAX_CUSTOM_TAG_LENGTH) {
                                player.sendMessage(
                                        Color.translate(
                                                LocaleUtil.tr(
                                                        player,
                                                        MessageConfig.CUSTOM_TAGS
                                                                .PROMPT_FAILED_LONGER)));
                            } else if (CustomTagsMenu.super
                                    .api
                                    .getFilterHandler()
                                    .isFiltered(result)) {
                                player.sendMessage(
                                        Color.translate(
                                                LocaleUtil.tr(
                                                                player,
                                                                MessageConfig.CUSTOM_TAGS
                                                                        .PROMPT_FILTERED_TAG)
                                                        .replace("<tag>", result)));
                            } else {
                                Stream<ICustomTag> stream2 =
                                        CustomTagsMenu.super
                                                .api
                                                .getCustomTagHandler()
                                                .getTags(player.getUniqueId())
                                                .stream();
                                if (stream2.anyMatch(
                                        result2 ->
                                                result2.getPrefix()
                                                        .equals(Color.translate(result)))) {
                                    player.sendMessage(
                                            Color.translate(
                                                    LocaleUtil.tr(
                                                                    player,
                                                                    MessageConfig.CUSTOM_TAGS
                                                                            .PROMPT_ALREADY_EXISTS)
                                                            .replace("<tag>", result)));
                                } else {
                                    Tasks.run(
                                            () -> {
                                                CustomTag customTag;
                                                (customTag =
                                                                new CustomTag(
                                                                        "CustomTag-"
                                                                                + Utilities
                                                                                        .getRandomSaltedString(
                                                                                                5),
                                                                        player.getUniqueId()))
                                                        .setPrefix(Color.translate(result));
                                                customTag.setColor("&f");
                                                CustomTagsMenu.super
                                                        .api
                                                        .getCustomTagHandler()
                                                        .updateTag(customTag);
                                                int value3 = value2 - 1;
                                                profile2.updateMetadata(
                                                        "custom-tag-tokens",
                                                        String.valueOf(value3),
                                                        true);
                                                player.sendMessage(
                                                        Color.translate(
                                                                LocaleUtil.tr(
                                                                                player,
                                                                                MessageConfig
                                                                                        .CUSTOM_TAGS
                                                                                        .CREATED)
                                                                        .replace(
                                                                                "<tag>",
                                                                                customTag
                                                                                        .getDisplayName())));
                                            });
                                }
                            }
                        };
                ConversationUtil.ask(
                        phoenix,
                        player,
                        text,
                        consumer,
                        () ->
                                player.sendMessage(
                                        Color.translate(
                                                LocaleUtil.tr(
                                                        player,
                                                        MessageConfig.CUSTOM_TAGS
                                                                .PROMPT_CANCELLED))));
            }
        }
    }

    class CustomTagButton extends Button {

        private final ICustomTag customTag;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder =
                    new ItemBuilder(
                            this.customTag.isAccepted()
                                    ? XMaterial.NAME_TAG.get()
                                    : (this.customTag.isDenied()
                                            ? XMaterial.REDSTONE.get()
                                            : XMaterial.PAPER.get()));
            IProfile cachedProfile =
                    CustomTagsMenu.super
                            .api
                            .getProfileHandler()
                            .getCachedProfile(player.getUniqueId());
            String text =
                    this.customTag.getStatus() == CustomTagStatus.ACCEPTED
                            ? LocaleUtil.tr(player, MenuConfig.CUSTOM_TAGS.STATUS_ACCEPTED)
                            : (this.customTag.getStatus() == CustomTagStatus.DENIED
                                    ? LocaleUtil.tr(player, MenuConfig.CUSTOM_TAGS.STATUS_DENIED)
                                    : LocaleUtil.tr(
                                            player, MenuConfig.CUSTOM_TAGS.STATUS_UNDECIDED));
            itemBuilder.setName(
                    LocaleUtil.tr(player, MenuConfig.CUSTOM_TAGS.NAME)
                            .replace(
                                    "<createdOn>", DateUtils.getDate(this.customTag.getCreatedOn()))
                            .replace("<tag>", this.customTag.getDisplayName())
                            .replace("<status>", text)
                            .replace(
                                    "<selected>",
                                    cachedProfile.getTag() == this.customTag
                                            ? LocaleUtil.tr(
                                                    player, MenuConfig.TAGS_MENU.SELECTED_NAME)
                                            : ""));
            for (String text2 : LocaleUtil.tr(player, MenuConfig.CUSTOM_TAGS.LORE)) {
                itemBuilder.addLoreLine(
                        text2.replace(
                                        "<createdOn>",
                                        DateUtils.getDate(this.customTag.getCreatedOn()))
                                .replace("<tag>", this.customTag.getDisplayName())
                                .replace("<status>", text));
            }
            if (cachedProfile.getTag() == this.customTag) {
                itemBuilder.addUnsafeEnchantment(XEnchantment.UNBREAKING.getEnchant(), 10);
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            IProfile profile;
            int intSetting =
                    (profile =
                                    CustomTagsMenu.super
                                            .api
                                            .getProfileHandler()
                                            .getCachedProfile(player.getUniqueId()))
                            .getIntSetting("custom-tag-tokens", 0);
            if (clickType == ClickType.DROP) {
                MenuHandler menuHandler = CustomTagsMenu.super.plugin.getMenuHandler();
                String text =
                        LocaleUtil.tr(player, MenuConfig.CUSTOM_TAGS.DELETE_TITLE)
                                .replace("<tag>", this.customTag.getDisplayName());
                IProfile profile2 = profile;
                menuHandler.openMenu(
                        new ConfirmMenu(
                                text,
                                result -> {
                                    player.closeInventory();
                                    if (result) {
                                        player.sendMessage(
                                                Color.translate(
                                                        LocaleUtil.tr(
                                                                        player,
                                                                        MessageConfig.CUSTOM_TAGS
                                                                                .DELETED)
                                                                .replace(
                                                                        "<tag>",
                                                                        this.customTag
                                                                                .getDisplayName())));
                                        if (this.customTag.getStatus()
                                                == CustomTagStatus.UNDECIDED) {
                                            int value = intSetting + 1;
                                            profile2.updateMetadata(
                                                    "custom-tag-tokens",
                                                    String.valueOf(value),
                                                    true);
                                            player.sendMessage(
                                                    Color.translate(
                                                            LocaleUtil.tr(
                                                                            player,
                                                                            MessageConfig
                                                                                    .CUSTOM_TAGS
                                                                                    .TOKEN_RETURNED)
                                                                    .replace(
                                                                            "<amount>",
                                                                            value + "")));
                                        }
                                        if (profile2.getTag() == this.customTag) {
                                            profile2.setTag(null);
                                        }
                                        CustomTagsMenu.super
                                                .api
                                                .getCustomTagHandler()
                                                .deleteTag(this.customTag);
                                    } else {
                                        player.sendMessage(
                                                Color.translate(
                                                        LocaleUtil.tr(
                                                                        player,
                                                                        MessageConfig.CUSTOM_TAGS
                                                                                .DELETION_CANCELLED)
                                                                .replace(
                                                                        "<tag>",
                                                                        this.customTag
                                                                                .getDisplayName())));
                                    }
                                },
                                true,
                                new SelectableTagButton(this.customTag, CustomTagsMenu.super.api)),
                        player);
            } else if (profile.getTag() == this.customTag) {
                player.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player, MessageConfig.TAGS.ALREADY_SELECTED)
                                        .replace("<tag>", this.customTag.getDisplayName())));
            } else if (this.customTag.getStatus() == CustomTagStatus.DENIED) {
                player.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player, MessageConfig.CUSTOM_TAGS.NOT_ACCEPTED)
                                        .replace("<tag>", this.customTag.getDisplayName())));
            } else if (this.customTag.getStatus() == CustomTagStatus.UNDECIDED) {
                player.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player, MessageConfig.CUSTOM_TAGS.NOT_APPROVED)
                                        .replace("<tag>", this.customTag.getDisplayName())));
            } else {
                player.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(player, MessageConfig.TAGS.TAG_SELECTED)
                                        .replace("<tag>", this.customTag.getDisplayName())));
                profile.setTag(this.customTag);
            }
        }

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }

        public CustomTagButton(ICustomTag customTag2) {
            this.customTag = customTag2;
        }
    }

    class EntryButton extends Button {

        private EntryButton() {}

        @Override
        public ItemStack getItem(Player player) {
            IProfile profile;
            ITag tag =
                    (profile =
                                    CustomTagsMenu.super
                                            .api
                                            .getProfileHandler()
                                            .getCachedProfile(player.getUniqueId()))
                            .getTag();
            ItemBuilder itemBuilder;
            (itemBuilder =
                            new ItemBuilder(
                                    XMaterial.PLAYER_HEAD.get(),
                                    1,
                                    (short) SkullType.PLAYER.ordinal()))
                    .setSkullOwner(profile.getName());
            itemBuilder.setName(
                    LocaleUtil.tr(player, MenuConfig.CUSTOM_TAGS.RESET_NAME)
                            .replace("<tag>", tag.getDisplayName()));
            for (String text : LocaleUtil.tr(player, MenuConfig.CUSTOM_TAGS.RESET_LORE)) {
                itemBuilder.addLoreLine(text.replace("<tag>", tag.getDisplayName()));
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            IProfile profile;
            if ((profile =
                                    CustomTagsMenu.super
                                            .api
                                            .getProfileHandler()
                                            .getCachedProfile(player.getUniqueId()))
                            .getTag()
                    == null) {
                player.sendMessage(
                        Color.translate(LocaleUtil.tr(player, MessageConfig.TAGS.NO_TAG_SELECTED)));
            } else {
                profile.setTag(null);
                player.sendMessage(
                        Color.translate(LocaleUtil.tr(player, MessageConfig.TAGS.TAG_RESET)));
            }
        }

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }
    }
}
