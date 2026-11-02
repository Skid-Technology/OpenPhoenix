package xyz.refinedev.phoenix.menu.impl;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.api.Menu;
import xyz.refinedev.phoenix.server.IServerData;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.HashMap;
import java.util.Map;

public class AnalyticsMenu extends Menu {

    private final IServerData serverData;

    private static final String text = "<server>";

    @Override
    public String getTitle(Player player) {
        return LocaleUtil.tr(player, MenuConfig.ANALYTICS.TITLE)
                .replace(text, this.serverData.getDisplayName());
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        this.backButton(entries, 4);
        for (int index = 0; index < 9; index++) {
            if (!entries.containsKey(index)) {
                entries.put(index, Button.placeholder());
            }
        }
        entries.put(9, new AnalyticsMenu.EntryButton());
        entries.put(10, new AnalyticsMenu.PlaytimeButton());
        return entries;
    }

    public AnalyticsMenu(IServerData serverData2) {
        this.setAutoUpdate(true);
        this.serverData = serverData2;
    }

    class EntryButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.PAPER.get()))
                    .setName(
                            Color.translate(
                                    LocaleUtil.tr(player, MenuConfig.ANALYTICS.PLAYER_PEAK.NAME)));
            int peakPlayers =
                    AnalyticsMenu.super
                            .api
                            .getAnalyticsHandler()
                            .getPeakPlayers(AnalyticsMenu.this.serverData);
            int peakPlayers2 =
                    AnalyticsMenu.super
                            .api
                            .getAnalyticsHandler()
                            .getPeakPlayers(AnalyticsMenu.this.serverData, 1);
            int peakPlayers3 =
                    AnalyticsMenu.super
                            .api
                            .getAnalyticsHandler()
                            .getPeakPlayers(AnalyticsMenu.this.serverData, 7);
            for (String text : LocaleUtil.tr(player, MenuConfig.ANALYTICS.PLAYER_PEAK.LORE)) {
                itemBuilder.addLoreLine(
                        text.replace("<peak>", peakPlayers + "")
                                .replace("<yesterdayPeak>", peakPlayers2 + "")
                                .replace(
                                        "<yesterdayDiff>",
                                        peakPlayers >= peakPlayers2
                                                ? "&a+" + (peakPlayers - peakPlayers2)
                                                : "&c" + (peakPlayers - peakPlayers2))
                                .replace("<weekPeak>", peakPlayers3 + "")
                                .replace(
                                        "<weekDiff>",
                                        peakPlayers >= peakPlayers3
                                                ? "&a+" + (peakPlayers - peakPlayers3)
                                                : "&c" + (peakPlayers - peakPlayers3)));
            }
            return itemBuilder.toItemStack();
        }

        public EntryButton() {}
    }

    class PlaytimeButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder itemBuilder;
            (itemBuilder = new ItemBuilder(XMaterial.PAPER.get()))
                    .setName(
                            Color.translate(
                                    LocaleUtil.tr(
                                            player, MenuConfig.ANALYTICS.AVERAGE_PLAYTIME.NAME)));
            String averagePlayTime =
                    AnalyticsMenu.super
                            .api
                            .getAnalyticsHandler()
                            .getAveragePlayTime(AnalyticsMenu.this.serverData);
            String averagePlayTime2 =
                    AnalyticsMenu.super
                            .api
                            .getAnalyticsHandler()
                            .getAveragePlayTime(AnalyticsMenu.this.serverData, 1);
            String averagePlayTime3 =
                    AnalyticsMenu.super
                            .api
                            .getAnalyticsHandler()
                            .getAveragePlayTime(AnalyticsMenu.this.serverData, 7);
            for (String text : LocaleUtil.tr(player, MenuConfig.ANALYTICS.AVERAGE_PLAYTIME.LORE)) {
                itemBuilder.addLoreLine(
                        text.replace("<playtime>", averagePlayTime)
                                .replace("<yesterdayPlaytime>", averagePlayTime2)
                                .replace(
                                        "<yesterdayDiff>",
                                        AnalyticsMenu.super
                                                .api
                                                .getAnalyticsHandler()
                                                .getAveragePlayTimeDifference(
                                                        AnalyticsMenu.this.serverData, 1))
                                .replace("<weekPlaytime>", averagePlayTime3)
                                .replace(
                                        "<weekDiff>",
                                        AnalyticsMenu.super
                                                .api
                                                .getAnalyticsHandler()
                                                .getAveragePlayTimeDifference(
                                                        AnalyticsMenu.this.serverData, 7)));
            }
            return itemBuilder.toItemStack();
        }

        public PlaytimeButton() {}
    }
}
