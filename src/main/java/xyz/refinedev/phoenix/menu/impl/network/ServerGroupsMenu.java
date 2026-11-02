package xyz.refinedev.phoenix.menu.impl.network;

import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.pagination.PaginatedMenu;
import xyz.refinedev.phoenix.utils.HeadLetterTexture;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;
import xyz.refinedev.phoenix.utils.reflection.Reflection;

import java.util.HashMap;
import java.util.Map;

public class ServerGroupsMenu extends PaginatedMenu {

    @Override
    public String getTitle(Player player) {
        return LocaleUtil.tr(player, MenuConfig.GROUP_INFO.GROUP_TITLE);
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        for (String text : super.api.getNetworkHandler().getProxyGroups()) {
            entries.put(entries.size(), new ServerGroupsMenu.EntryButton(text));
        }
        return entries;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        HashMap<Integer, Button> entries;
        (entries = new HashMap()).put(4, new ServerGroupsMenu.GlobalGroupButton());
        return entries;
    }

    class EntryButton extends Button {

        private final String text;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder =
                            new ItemBuilder(
                                    XMaterial.PLAYER_HEAD.get(),
                                    1,
                                    (short) SkullType.PLAYER.ordinal()))
                    .setName(
                            LocaleUtil.tr(player, MenuConfig.GROUP_INFO.GROUP_NAME)
                                    .replace("<group>", this.text));
            for (String text2 : LocaleUtil.tr(player, MenuConfig.GROUP_INFO.GROUP_LORE)) {
                itemBuilder.addLoreLine(
                        text2.replace(
                                "<count>",
                                ""
                                        + ServerGroupsMenu.super
                                                .api
                                                .getNetworkHandler()
                                                .getProxiesInGroup(this.text)
                                                .size()));
            }
            return Reflection.itemWithBase64(
                    itemBuilder.toItemStack(),
                    HeadLetterTexture.getWoodByWord(this.text).getTexture());
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            ServerGroupsMenu.super
                    .plugin
                    .getMenuHandler()
                    .openMenu(new GroupProxiesMenu(this.text), player);
        }

        public EntryButton(String text) {
            this.text = text;
        }
    }

    class GlobalGroupButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.BOOK.get(), 1))
                    .setName(LocaleUtil.tr(player, MenuConfig.GROUP_INFO.GLOBAL_NAME));
            for (String text : LocaleUtil.tr(player, MenuConfig.GROUP_INFO.GLOBAL_LORE)) {
                itemBuilder.addLoreLine(
                        text.replace(
                                "<count>",
                                ""
                                        + ServerGroupsMenu.super
                                                .api
                                                .getNetworkHandler()
                                                .getProxies()
                                                .size()));
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            ServerGroupsMenu.super
                    .plugin
                    .getMenuHandler()
                    .openMenu(new GroupProxiesMenu("global"), player);
        }

        public GlobalGroupButton() {}
    }
}
