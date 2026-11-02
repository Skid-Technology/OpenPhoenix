package xyz.refinedev.phoenix.menu.impl.network;

import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.pagination.PaginatedMenu;
import xyz.refinedev.phoenix.menu.impl.AnalyticsMenu;
import xyz.refinedev.phoenix.server.IServerData;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.HeadLetterTexture;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.StringUtils;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;
import xyz.refinedev.phoenix.utils.reflection.Reflection;

import java.util.HashMap;
import java.util.Map;

public class GroupProxiesMenu extends PaginatedMenu {

    private final String text;

    private static final String textString = "<group>";

    @Override
    public String getTitle(Player player) {
        return LocaleUtil.tr(player, MenuConfig.SERVER_INFO.PROXIES_IN_GROUP_TITLE)
                .replace(textString, this.text);
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        for (IServerData serverData : super.api.getNetworkHandler().getProxiesInGroup(this.text)) {
            entries.put(entries.size(), new GroupProxiesMenu.TrueButton(serverData));
        }
        return entries;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        this.backButton(entries, 4);
        return entries;
    }

    public GroupProxiesMenu(String text) {
        this.setAutoUpdate(true);
        this.text = text;
    }

    class TrueButton extends Button {

        private final IServerData serverData;

        @Override
        public ItemStack getItem(Player player) {
            int size2 =
                    GroupProxiesMenu.super
                            .api
                            .getNetworkHandler()
                            .getOnlyVanishedOnlinePlayers(this.serverData)
                            .size();
            ItemBuilder itemBuilder;
            (itemBuilder =
                            new ItemBuilder(
                                    XMaterial.PLAYER_HEAD.get(),
                                    1,
                                    (short) SkullType.PLAYER.ordinal()))
                    .setName(
                            LocaleUtil.tr(player, MenuConfig.SERVER_INFO.PROXY_SERVER_NAME)
                                    .replace("<name>", this.serverData.getDisplayName()));
            if (this.serverData.isWhitelisted() && this.serverData.isOnline()) {
                for (String text :
                        LocaleUtil.tr(
                                player, MenuConfig.SERVER_INFO.PROXY_SERVER_INFO_WHITELISTED)) {
                    itemBuilder.addLoreLine(
                            text.replace("<region>", this.serverData.getRegion())
                                    .replace(
                                            "<group>",
                                            StringUtils.joinCustomSpacer(
                                                    this.serverData.getServerGroups(), ", "))
                                    .replace("<name>", this.serverData.getServerName())
                                    .replace("<version>", this.serverData.getVersion())
                                    .replace("<port>", "" + this.serverData.getPort())
                                    .replace(
                                            "<countingPlayers>",
                                            this.serverData.isCountPlayers()
                                                    ? Color.translate("&atrue")
                                                    : Color.translate("&cfalse"))
                                    .replace(
                                            "<online>",
                                            String.valueOf(
                                                    this.serverData.getOnlinePlayers().size()))
                                    .replace("<vanished>", String.valueOf(size2))
                                    .replace(
                                            "<uptime>",
                                            DateUtils.formatDateDiff(
                                                    this.serverData.getStartedAt()))
                                    .replace(
                                            "<lastUpdate>",
                                            DateUtils.formatDateDiff(this.serverData.getLastTick()))
                                    .replace(
                                            "<startedAt>",
                                            DateUtils.getDate(this.serverData.getStartedAt()))
                                    .replace("<server>", this.serverData.getDisplayName())
                                    .replace(
                                            "<access>",
                                            StringUtils.join(
                                                    this.serverData
                                                            .getWhitelistInfo()
                                                            .getNiceAccessTypes()))
                                    .replace(
                                            "<rank>",
                                            this.serverData.getWhitelistInfo().getRank() == null
                                                    ? "None"
                                                    : this.serverData
                                                            .getWhitelistInfo()
                                                            .getRank()
                                                            .getDisplayName()));
                }
                return Reflection.itemWithBase64(
                        itemBuilder.toItemStack(),
                        HeadLetterTexture.getWhiteByWord(this.serverData.getServerName())
                                .getTexture());
            } else if (this.serverData.isOnline()) {
                for (String text2 :
                        LocaleUtil.tr(player, MenuConfig.SERVER_INFO.PROXY_SERVER_INFO_ONLINE)) {
                    itemBuilder.addLoreLine(
                            text2.replace("<region>", this.serverData.getRegion())
                                    .replace(
                                            "<group>",
                                            StringUtils.joinCustomSpacer(
                                                    this.serverData.getServerGroups(), ", "))
                                    .replace("<name>", this.serverData.getServerName())
                                    .replace("<version>", this.serverData.getVersion())
                                    .replace("<port>", "" + this.serverData.getPort())
                                    .replace(
                                            "<countingPlayers>",
                                            this.serverData.isCountPlayers()
                                                    ? Color.translate("&atrue")
                                                    : Color.translate("&cfalse"))
                                    .replace(
                                            "<online>",
                                            String.valueOf(
                                                    this.serverData.getOnlinePlayers().size()))
                                    .replace("<vanished>", String.valueOf(size2))
                                    .replace(
                                            "<uptime>",
                                            DateUtils.formatDateDiff(
                                                    this.serverData.getStartedAt()))
                                    .replace(
                                            "<lastUpdate>",
                                            DateUtils.formatDateDiff(this.serverData.getLastTick()))
                                    .replace(
                                            "<startedAt>",
                                            DateUtils.getDate(this.serverData.getStartedAt()))
                                    .replace("<server>", this.serverData.getDisplayName())
                                    .replace(
                                            "<access>",
                                            StringUtils.join(
                                                    this.serverData
                                                            .getWhitelistInfo()
                                                            .getNiceAccessTypes()))
                                    .replace(
                                            "<rank>",
                                            this.serverData.getWhitelistInfo().getRank() == null
                                                    ? "None"
                                                    : this.serverData
                                                            .getWhitelistInfo()
                                                            .getRank()
                                                            .getDisplayName()));
                }
                return Reflection.itemWithBase64(
                        itemBuilder.toItemStack(),
                        HeadLetterTexture.getGreenByWord(this.serverData.getServerName())
                                .getTexture());
            } else {
                for (String text3 :
                        LocaleUtil.tr(player, MenuConfig.SERVER_INFO.PROXY_SERVER_INFO_OFFLINE)) {
                    itemBuilder.addLoreLine(
                            text3.replace("<region>", this.serverData.getRegion())
                                    .replace(
                                            "<group>",
                                            StringUtils.joinCustomSpacer(
                                                    this.serverData.getServerGroups(), ", "))
                                    .replace("<name>", this.serverData.getServerName())
                                    .replace("<version>", this.serverData.getVersion())
                                    .replace("<port>", "" + this.serverData.getPort())
                                    .replace(
                                            "<countingPlayers>",
                                            this.serverData.isCountPlayers()
                                                    ? Color.translate("&atrue")
                                                    : Color.translate("&cfalse"))
                                    .replace(
                                            "<lastUpdate>",
                                            DateUtils.formatDateDiff(this.serverData.getLastTick()))
                                    .replace(
                                            "<stoppedAt>",
                                            DateUtils.getDate(this.serverData.getLastTick()))
                                    .replace("<server>", this.serverData.getDisplayName())
                                    .replace(
                                            "<access>",
                                            StringUtils.join(
                                                    this.serverData
                                                            .getWhitelistInfo()
                                                            .getNiceAccessTypes()))
                                    .replace(
                                            "<rank>",
                                            this.serverData.getWhitelistInfo().getRank() == null
                                                    ? "None"
                                                    : this.serverData
                                                            .getWhitelistInfo()
                                                            .getRank()
                                                            .getDisplayName()));
                }
                return Reflection.itemWithBase64(
                        itemBuilder.toItemStack(),
                        HeadLetterTexture.getRedByWord(this.serverData.getServerName())
                                .getTexture());
            }
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (clickType == ClickType.MIDDLE && !this.serverData.isOnline()) {
                GroupProxiesMenu.super.api.getNetworkHandler().deleteProxy(this.serverData);
            } else if (clickType.isRightClick()) {
                GroupProxiesMenu.super
                        .plugin
                        .getMenuHandler()
                        .openMenu(new AnalyticsMenu(this.serverData), player);
            }
        }

        public TrueButton(IServerData serverData2) {
            this.serverData = serverData2;
        }
    }
}
