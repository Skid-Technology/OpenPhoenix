package xyz.refinedev.phoenix.profile.settings;

import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.settings.api.PlayerSettings;
import xyz.refinedev.phoenix.settings.api.builder.BooleanSettingsBuilder;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.item.ItemUtils;

import java.util.HashMap;
import java.util.Map;

public class AdminPlayerSettings extends PlayerSettings {

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> entries;
        (entries = new HashMap())
                .put(
                        0,
                        new BooleanSettingsBuilder()
                                .titleOf(
                                        LocaleUtil.tr(
                                                player, MenuConfig.SETTINGS_MENU.ADMIN_ALERTS.NAME))
                                .materialOf(
                                        ItemUtils.parseFromX(
                                                MenuConfig.SETTINGS_MENU.ADMIN_ALERTS.MATERIAL))
                                .descriptionOf(
                                        LocaleUtil.tr(
                                                player,
                                                MenuConfig.SETTINGS_MENU.ADMIN_ALERTS.DESCRIPTION))
                                .metaDataOf("admin-alerts")
                                .defaultOf(true)
                                .asButton());
        return entries;
    }

    @Override
    public boolean hasPermission(Player player) {
        return player.hasPermission("core.admin");
    }

    @Override
    public String name() {
        return "Admin";
    }

    @Override
    public int getPriority() {
        return 120;
    }
}
