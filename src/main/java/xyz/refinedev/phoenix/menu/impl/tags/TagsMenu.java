package xyz.refinedev.phoenix.menu.impl.tags;

import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.lib.xseries.XEnchantment;
import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.pagination.PaginatedMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.tag.ITag;
import xyz.refinedev.phoenix.profile.tag.ITagCategory;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TagsMenu extends PaginatedMenu {

    private ITagCategory selectedCategory = this.api.getTagHandler().getDefaultCategory();

    @Override
    public String getTitle(Player player) {
        return Color.translate(LocaleUtil.tr(player, MenuConfig.TAGS_MENU.TITLE));
    }

    @Override
    public void onClose(Player player) {
        super.api.getProfileHandler().getCachedProfile(player.getUniqueId()).saveProfile();
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        if (this.selectedCategory == null) {
            this.selectedCategory = super.api.getTagHandler().getDefaultCategory();
        }
        List<ITag> sortedTagsReversed = super.api.getTagHandler().getSortedTagsReversed();
        if (!this.selectedCategory.isDef()) {
            for (ITag tag : sortedTagsReversed) {
                if (this.selectedCategory.getTags().contains(tag.getName())) {
                    entries.put(entries.size(), new TagsMenu.TagButton(tag));
                }
            }
            return entries;
        } else {
            for (ITag tag2 : sortedTagsReversed) {
                entries.put(entries.size(), new TagsMenu.TagButton(tag2));
            }
            return entries;
        }
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        IProfile profile;
        if ((profile = super.api.getProfileHandler().getCachedProfile(player.getUniqueId()))
                                .getTag()
                        != null
                && profile.getCustomTag() == null) {
            entries.put(40, new TagsMenu.EntryButton());
        }
        if (SettingsConfig.SERVER.TAG_CATEGORIES) {
            entries.put(4, new TagsMenu.SelectedTagsButton());
        }
        return entries;
    }

    class EntryButton extends Button {

        private EntryButton() {}

        @Override
        public ItemStack getItem(Player player) {
            IProfile profile;
            ITag tag =
                    (profile =
                                    TagsMenu.super
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
                    LocaleUtil.tr(player, MenuConfig.TAGS_MENU.RESET_NAME)
                            .replace("<tag>", tag.getPrefix())
                            .replace("<color>", tag.getColor())
                            .replace("<name>", tag.getName()));
            for (String text : LocaleUtil.tr(player, MenuConfig.TAGS_MENU.RESET_LORE)) {
                itemBuilder.addLoreLine(
                        text.replace("<tag>", tag.getPrefix())
                                .replace("<color>", tag.getColor())
                                .replace("<name>", tag.getName()));
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            IProfile profile;
            if ((profile =
                                    TagsMenu.super
                                            .api
                                            .getProfileHandler()
                                            .getCachedProfile(player.getUniqueId()))
                            .getTag()
                    == null) {
                player.sendMessage(
                        Color.translate(
                                TagsMenu.super
                                        .plugin
                                        .getLocaleHandler()
                                        .get(
                                                player,
                                                "tags.no-tag-selected",
                                                MessageConfig.TAGS.NO_TAG_SELECTED)));
            } else {
                profile.setTag(null);
                player.sendMessage(
                        Color.translate(
                                TagsMenu.super
                                        .plugin
                                        .getLocaleHandler()
                                        .get(
                                                player,
                                                "tags.tag-reset",
                                                MessageConfig.TAGS.TAG_RESET)));
            }
        }

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }
    }

    class SelectedTagsButton extends Button {

        private static final String text = "<list>";

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.OAK_SIGN.get()))
                    .setName(LocaleUtil.tr(player, MenuConfig.TAGS_MENU.CATEGORY_NAME));
            Iterator<String> iterator2 =
                    LocaleUtil.tr(player, MenuConfig.TAGS_MENU.CATEGORY_LORE).iterator();
            while (iterator2.hasNext()) {
                String text2;
                if ((text2 = (String) iterator2.next()).contains(text)) {
                    for (ITagCategory tagCategory :
                            TagsMenu.super.api.getTagHandler().getSortedCategories()) {
                        itemBuilder.addLoreLine(
                                (tagCategory == TagsMenu.this.selectedCategory
                                                ? LocaleUtil.tr(
                                                        player,
                                                        MenuConfig.TAGS_MENU.CATEGORY_SELECTED)
                                                : LocaleUtil.tr(
                                                        player,
                                                        MenuConfig.TAGS_MENU.CATEGORY_UNSELECTED))
                                        + tagCategory.getName());
                    }
                } else {
                    itemBuilder.addLoreLine(text2);
                }
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            List<ITagCategory> values;
            int value =
                    (values = TagsMenu.super.api.getTagHandler().getSortedCategories())
                            .indexOf(TagsMenu.this.selectedCategory);
            TagsMenu.this.selectedCategory =
                    (ITagCategory) values.get(value + 1 >= values.size() ? 0 : value + 1);
        }

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }

        public SelectedTagsButton() {}
    }

    class TagButton extends Button {

        private final ITag tag;

        @Override
        public ItemStack getItem(Player player) {
            boolean matches = BukkitAPI.hasTag(player, this.tag);
            ItemBuilder itemBuilder = new ItemBuilder(XMaterial.NAME_TAG.get());
            IProfile cachedProfile =
                    TagsMenu.super.api.getProfileHandler().getCachedProfile(player.getUniqueId());
            itemBuilder.setName(
                    LocaleUtil.tr(player, MenuConfig.TAGS_MENU.NAME)
                            .replace("<color>", this.tag.getColor())
                            .replace("<name>", this.tag.getName())
                            .replace("<tag>", this.tag.getPrefix())
                            .replace(
                                    "<selected>",
                                    cachedProfile.getTag() == this.tag
                                            ? LocaleUtil.tr(
                                                    player, MenuConfig.TAGS_MENU.SELECTED_NAME)
                                            : ""));
            for (String text :
                    matches
                            ? LocaleUtil.tr(player, MenuConfig.TAGS_MENU.ACCESS)
                            : LocaleUtil.tr(player, MenuConfig.TAGS_MENU.NO_ACCESS)) {
                itemBuilder.addLoreLine(
                        text.replace("<tag>", this.tag.getPrefix())
                                .replace("<color>", this.tag.getColor())
                                .replace("<name>", this.tag.getName()));
            }
            if (cachedProfile.getTag() == this.tag) {
                itemBuilder.addUnsafeEnchantment(XEnchantment.UNBREAKING.getEnchant(), 10);
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            IProfile cachedProfile =
                    TagsMenu.super.api.getProfileHandler().getCachedProfile(player.getUniqueId());
            if (!BukkitAPI.hasTag(player, this.tag)) {
                player.sendMessage(
                        Color.translate(
                                TagsMenu.super
                                        .plugin
                                        .getLocaleHandler()
                                        .get(
                                                player,
                                                "tags.no-permission",
                                                MessageConfig.TAGS.NO_PERMISSION)
                                        .replace(
                                                "<name>",
                                                this.tag.getColor() + this.tag.getName())));
            } else if (!this.tag.isScheduleActive()) {
                player.sendMessage(
                        Color.translate(
                                TagsMenu.super
                                        .plugin
                                        .getLocaleHandler()
                                        .get(
                                                player,
                                                "tags.not-active",
                                                MessageConfig.TAGS.NOT_ACTIVE)
                                        .replace(
                                                "<tag>",
                                                this.tag.getColor() + this.tag.getName())));
            } else if (cachedProfile.getTag() == this.tag) {
                player.sendMessage(
                        Color.translate(
                                TagsMenu.super
                                        .plugin
                                        .getLocaleHandler()
                                        .get(
                                                player,
                                                "tags.already-selected",
                                                MessageConfig.TAGS.ALREADY_SELECTED)
                                        .replace(
                                                "<tag>",
                                                this.tag.getColor() + this.tag.getName())));
            } else {
                player.sendMessage(
                        Color.translate(
                                TagsMenu.super
                                        .plugin
                                        .getLocaleHandler()
                                        .get(
                                                player,
                                                "tags.tag-selected",
                                                MessageConfig.TAGS.TAG_SELECTED)
                                        .replace(
                                                "<tag>",
                                                this.tag.getColor() + this.tag.getName())));
                cachedProfile.setTag(this.tag);
            }
        }

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }

        public TagButton(ITag tag2) {
            this.tag = tag2;
        }
    }
}
