package xyz.refinedev.phoenix.menu.impl;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.pagination.PaginatedMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.antivpn.IAntiVPNData;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;
import xyz.refinedev.phoenix.utils.reflection.InventoryTitleUpdate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationLogMenu extends PaginatedMenu {

    private final IProfile profile;

    private final List<IAntiVPNData> antiVPNDatas;

    @Override
    public String getTitle(Player player) {
        return Color.translate(
                LocaleUtil.tr(player, MenuConfig.LOCATION_LOG_MENU.TITLE)
                        .replace("<player>", this.profile.getName()));
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        this.backButton(entries, 4);
        return entries;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        for (IAntiVPNData antiVPNData : this.antiVPNDatas) {
            entries.put(entries.size(), new LocationLogMenu.TrueButton(antiVPNData));
        }
        return entries;
    }

    @Override
    public void onOpen(Player player) {
        InventoryTitleUpdate.sendInventoryTitle(
                player,
                LocaleUtil.tr(player, MenuConfig.LOCATION_LOG_MENU.TITLE)
                        .replace("<player>", this.profile.getNameWithColorNoDisguise()));
    }

    public LocationLogMenu(IProfile profile2, List<IAntiVPNData> antiVPNDatas) {
        this.profile = profile2;
        this.antiVPNDatas = antiVPNDatas;
    }

    class TrueButton extends Button {

        private final IAntiVPNData antiVPNData;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.PAPER.get()))
                    .setName(
                            LocaleUtil.tr(player, MenuConfig.LOCATION_LOG_MENU.NAME)
                                    .replace(
                                            "<time>",
                                            DateUtils.getDate(this.antiVPNData.getTime())));
            for (String text : LocaleUtil.tr(player, MenuConfig.LOCATION_LOG_MENU.LORE)) {
                itemBuilder.addLoreLine(
                        text.replace("<city>", this.antiVPNData.getCity())
                                .replace("<region>", this.antiVPNData.getRegion())
                                .replace("<country>", this.antiVPNData.getCountry())
                                .replace("<isp>", this.antiVPNData.getIsp())
                                .replace("<vpn>", this.antiVPNData.isVpn() ? "&atrue" : "&cfalse")
                                .replace("<time>", DateUtils.getDate(this.antiVPNData.getTime())));
            }
            return itemBuilder.toItemStack();
        }

        public TrueButton(IAntiVPNData antiVPNData2) {
            this.antiVPNData = antiVPNData2;
        }
    }
}
