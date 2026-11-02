package xyz.refinedev.phoenix.profile.settings;

import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.settings.api.PlayerSettings;
import xyz.refinedev.phoenix.settings.api.builder.BooleanSettingsBuilder;
import xyz.refinedev.phoenix.settings.api.builder.MultiOptionsSettingsBuilder;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.item.ItemUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaffPlayerSettings extends PlayerSettings {

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        List<String> accessChannelsSorted =
                Phoenix.getInstance().getChatHandler().getAccessChannelsSorted(player);
        entries.put(
                0,
                new MultiOptionsSettingsBuilder()
                        .titleOf(LocaleUtil.tr(player, MenuConfig.SETTINGS_MENU.CHAT_CHANNEL.NAME))
                        .materialOf(
                                ItemUtils.parseFromX(
                                        MenuConfig.SETTINGS_MENU.CHAT_CHANNEL.MATERIAL))
                        .descriptionOf(
                                LocaleUtil.tr(
                                        player, MenuConfig.SETTINGS_MENU.CHAT_CHANNEL.DESCRIPTION))
                        .valuesOf(accessChannelsSorted)
                        .metaDataOf("channel")
                        .defaultOf("Default")
                        .asButton());
        entries.put(
                entries.size(),
                new BooleanSettingsBuilder()
                        .titleOf(
                                LocaleUtil.tr(player, MenuConfig.SETTINGS_MENU.STAFF_MESSAGES.NAME))
                        .materialOf(
                                ItemUtils.parseFromX(
                                        MenuConfig.SETTINGS_MENU.STAFF_MESSAGES.MATERIAL))
                        .descriptionOf(
                                LocaleUtil.tr(
                                        player,
                                        MenuConfig.SETTINGS_MENU.STAFF_MESSAGES.DESCRIPTION))
                        .metaDataOf("staff-messages")
                        .defaultOf(true)
                        .asButton());
        entries.put(
                entries.size(),
                new BooleanSettingsBuilder()
                        .titleOf(
                                LocaleUtil.tr(
                                        player, MenuConfig.SETTINGS_MENU.FILTERED_MESSAGES.NAME))
                        .materialOf(
                                ItemUtils.parseFromX(
                                        MenuConfig.SETTINGS_MENU.FILTERED_MESSAGES.MATERIAL))
                        .descriptionOf(
                                LocaleUtil.tr(
                                        player,
                                        MenuConfig.SETTINGS_MENU.FILTERED_MESSAGES.DESCRIPTION))
                        .metaDataOf("filtered-messages")
                        .defaultOf(true)
                        .asButton());
        if (SettingsConfig.REPORTS.ENABLED) {
            entries.put(
                    entries.size(),
                    new BooleanSettingsBuilder()
                            .titleOf(LocaleUtil.tr(player, MenuConfig.SETTINGS_MENU.REPORTS.NAME))
                            .materialOf(
                                    ItemUtils.parseFromX(MenuConfig.SETTINGS_MENU.REPORTS.MATERIAL))
                            .descriptionOf(
                                    LocaleUtil.tr(
                                            player, MenuConfig.SETTINGS_MENU.REPORTS.DESCRIPTION))
                            .metaDataOf("reports")
                            .defaultOf(true)
                            .asButton());
            entries.put(
                    entries.size(),
                    new BooleanSettingsBuilder()
                            .titleOf(LocaleUtil.tr(player, MenuConfig.SETTINGS_MENU.REQUESTS.NAME))
                            .materialOf(
                                    ItemUtils.parseFromX(
                                            MenuConfig.SETTINGS_MENU.REQUESTS.MATERIAL))
                            .descriptionOf(
                                    LocaleUtil.tr(
                                            player, MenuConfig.SETTINGS_MENU.REQUESTS.DESCRIPTION))
                            .metaDataOf("requests")
                            .defaultOf(true)
                            .asButton());
        }
        return entries;
    }

    @Override
    public boolean hasPermission(Player player) {
        return player.hasPermission("core.staff");
    }

    @Override
    public String name() {
        return "Staff";
    }

    @Override
    public int getPriority() {
        return 100;
    }
}
