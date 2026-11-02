package xyz.refinedev.phoenix.menu.impl.staff;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.lib.io.lettuce.core.api.sync.RedisCommands;
import xyz.refinedev.lib.xseries.XMaterial;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.handler.IDatabaseHandler;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.pagination.PaginatedMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.HashMap;
import java.util.Map;

public class WatchlistMenu extends PaginatedMenu {

    private final Map<IProfile, Integer> integersByProfile;

    private static final String title = "Watchlist";

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        for (IProfile profile : this.integersByProfile.keySet()) {
            int value = this.integersByProfile.get(profile);
            entries.put(entries.size(), new WatchlistMenu.OnlineButton(profile, value));
        }
        return entries;
    }

    @Override
    public String getTitle(Player player) {
        return Color.translate(title);
    }

    public WatchlistMenu(Map<IProfile, Integer> entries) {
        this.integersByProfile = entries;
    }

    class OnlineButton extends Button {

        private final IProfile profile;

        private final int count;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.PLAYER_HEAD.parseMaterial()))
                    .setName(
                            LocaleUtil.tr(player, MenuConfig.WATCHLIST_MENU.NAME)
                                    .replace(
                                            "<player>", this.profile.getNameWithColorNoDisguise()));
            itemBuilder.setDurability(3);
            itemBuilder.setSkullOwner(this.profile.getName());
            for (String text : LocaleUtil.tr(player, MenuConfig.WATCHLIST_MENU.LORE)) {
                itemBuilder.addLoreLine(
                        text.replace("<reports>", String.valueOf(this.count))
                                .replace(
                                        "<status>",
                                        WatchlistMenu.super
                                                        .api
                                                        .getNetworkHandler()
                                                        .isOnline(this.profile.getUuid())
                                                ? "Online"
                                                : "Offline"));
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            IDatabaseHandler databaseHandler = WatchlistMenu.super.api.getDatabaseHandler();
            WatchlistMenu.OnlineButton onlineButton = this;
            databaseHandler.runRedisCommand(onlineButton::findLong);
        }

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }

        public OnlineButton(IProfile profile2, int count) {
            this.profile = profile2;
            this.count = count;
        }

        private Long findLong(RedisCommands<String, String> redisCommands) {
            return redisCommands.del(
                    new String[] {"watchlist:" + this.profile.getUniqueId().toString()});
        }
    }
}
