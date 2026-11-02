package xyz.refinedev.phoenix.menu.impl.staff;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.lib.xseries.XMaterial;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.pagination.PaginatedMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.login.ILogin;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.Utilities;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StaffOnlineMenu extends PaginatedMenu {

    private static final String title = "Staff Online";

    public StaffOnlineMenu() {
        this.setAutoUpdate(true);
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        for (Player player2 : Utilities.getOnlinePlayers()) {
            IProfile profile;
            if ((profile = super.api.getProfileHandler().getProfile(player2.getUniqueId())) != null
                    && profile.isStaff()) {
                entries.put(
                        entries.size(),
                        new StaffOnlineMenu.YesButton(
                                player2,
                                super.api.getLoginHandler().getLogins(player2.getUniqueId())));
            }
        }
        return entries;
    }

    @Override
    public String getTitle(Player player) {
        return Color.translate(title);
    }

    class YesButton extends Button {

        private final Player player;

        private final List<ILogin> logins;

        @Override
        public ItemStack getItem(Player player2) {
            IProfile profile =
                    StaffOnlineMenu.super
                            .api
                            .getProfileHandler()
                            .getProfile(this.player.getUniqueId());
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.PLAYER_HEAD.parseMaterial()))
                    .setName(profile.getNameWithColorNoDisguise());
            itemBuilder.setDurability(3);
            itemBuilder.setSkullOwner(this.player.getName());
            Iterator<String> iterator2 =
                    LocaleUtil.tr(player2, MenuConfig.ONLINE_STAFF_MENU.LORE).iterator();
            while (iterator2.hasNext()) {
                String text =
                        ((String) iterator2.next())
                                .replace("<isVanished>", profile.isVanished() ? "&aYes" : "&cNo")
                                .replace("<rank>", profile.getGeneralRank().getDisplayName())
                                .replace("<playtime>", profile.getLastSeenAgo(this.logins))
                                .replace(
                                        "<target>",
                                        StaffOnlineMenu.super
                                                .api
                                                .getProfileHandler()
                                                .getProfile(this.player.getUniqueId())
                                                .getNameWithColor());
                itemBuilder.addLoreLine(text);
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player2, ClickType clickType) {
            player2.closeInventory();
            Player player3;
            if ((player3 = Bukkit.getPlayer(this.player.getUniqueId())) != null) {
                Phoenix.getInstance()
                        .getNmsHandler()
                        .getTeleportAPI()
                        .teleport(player2, player3.getLocation());
            }
        }

        public YesButton(Player player2, List<ILogin> logins) {
            this.player = player2;
            this.logins = logins;
        }
    }
}
