package xyz.refinedev.phoenix.menu.api.menus;

import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.pagination.PaginatedMenu;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.chat.ConversationUtil;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;
import xyz.refinedev.phoenix.utils.reflection.Reflection;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class TextEditorMenu extends PaginatedMenu {

    private final LinkedList<String> lines;

    private boolean changesMade = false;

    private static final String title = "Edit Text";

    @Override
    public String getTitle(Player player) {
        return title;
    }

    @Override
    public void onClose(Player player) {}

    public abstract void onSave(Player player, List<String> values);

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        for (int index = 0; index < this.lines.size(); index++) {
            entries.put(entries.size(), new TextEditorMenu.LineButton(this, index));
        }
        return entries;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        HashMap<Integer, Button> entries;
        (entries = new HashMap()).put(2, new TextEditorMenu.AddLineButton(this));
        entries.put(6, new TextEditorMenu.SaveButton());
        return entries;
    }

    public TextEditorMenu(LinkedList<String> lines) {
        this.lines = lines;
    }

    class AddLineButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder =
                            new ItemBuilder(
                                    XMaterial.PLAYER_HEAD.get(),
                                    1,
                                    (short) SkullType.PLAYER.ordinal()))
                    .setName("&a&lAdd Line");
            return Reflection.itemWithBase64(
                    itemBuilder.toItemStack(),
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA1NmJjMTI0NGZjZmY5OTM0NGYxMmFiYTQyYWMyM2ZlZTZlZjZlMzM1MWQyN2QyNzNjMTU3MjUzMWYifX19");
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            ConversationUtil.ask(
                    TextEditorMenu.super.plugin,
                    player,
                    "&aPlease input a new line.",
                    result -> {
                        TextEditorMenu.this.lines.add(result);
                        TextEditorMenu.this.changesMade = true;
                        TextEditorMenu.super
                                .plugin
                                .getMenuHandler()
                                .openMenu(TextEditorMenu.this, player);
                    });
        }

        public AddLineButton(TextEditorMenu textEditorMenu) {}
    }

    class LineButton extends Button {

        private final int index;

        @Override
        public ItemStack getItem(Player player) {
            String text =
                    TextEditorMenu.this.lines.size() < this.index
                            ? "&cEmpty"
                            : TextEditorMenu.this.lines.get(this.index);
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(Material.PAPER)).setName(text);
            itemBuilder.setLore(LocaleUtil.tr(player, MenuConfig.TEXT_EDITOR_MENU.LINE_SLOT.LORE));
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (clickType.isLeftClick()) {
                player.closeInventory();
                ConversationUtil.ask(
                        TextEditorMenu.super.plugin,
                        player,
                        "&aPlease input the new line text.",
                        result -> {
                            TextEditorMenu.this.lines.remove(this.index);
                            TextEditorMenu.this.lines.add(this.index, result);
                            TextEditorMenu.this.changesMade = true;
                            TextEditorMenu.super
                                    .plugin
                                    .getMenuHandler()
                                    .openMenu(TextEditorMenu.this, player);
                        });
            } else if (clickType.isRightClick()) {
                TextEditorMenu.this.lines.remove(this.index);
                TextEditorMenu.this.changesMade = true;
            }
        }

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return clickType.isRightClick();
        }

        public LineButton(TextEditorMenu textEditorMenu, int index) {
            this.index = index;
        }
    }

    class SaveButton extends Button {

        private SaveButton() {}

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.WHITE_WOOL.get()))
                    .setDurability(TextEditorMenu.this.changesMade ? 13 : 14);
            if (TextEditorMenu.this.changesMade) {
                itemBuilder.setName(
                        LocaleUtil.tr(player, MenuConfig.TEXT_EDITOR_MENU.SAVE_SLOT.CAN_SAVE_NAME));
                itemBuilder.setLore(
                        LocaleUtil.tr(player, MenuConfig.TEXT_EDITOR_MENU.SAVE_SLOT.CAN_SAVE));
            } else {
                itemBuilder.setName(
                        LocaleUtil.tr(
                                player, MenuConfig.TEXT_EDITOR_MENU.SAVE_SLOT.CANT_SAVE_NAME));
                itemBuilder.setLore(
                        LocaleUtil.tr(player, MenuConfig.TEXT_EDITOR_MENU.SAVE_SLOT.CANT_SAVE));
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (TextEditorMenu.this.changesMade) {
                TextEditorMenu.this.onSave(player, TextEditorMenu.this.lines);
                TextEditorMenu.this.changesMade = false;
            }
        }
    }
}
