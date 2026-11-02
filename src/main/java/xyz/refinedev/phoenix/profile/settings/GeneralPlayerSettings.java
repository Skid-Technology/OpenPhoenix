package xyz.refinedev.phoenix.profile.settings;

import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.settings.api.PlayerSettings;
import xyz.refinedev.phoenix.settings.api.builder.BooleanSettingsBuilder;
import xyz.refinedev.phoenix.settings.api.builder.MultiOptionsSettingsBuilder;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.item.ItemUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneralPlayerSettings extends PlayerSettings {

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> entries = new HashMap<>();
        if (MenuConfig.SETTINGS_MENU.PRIVATE_MESSAGES.ENABLED) {
            entries.put(
                    0,
                    new BooleanSettingsBuilder()
                            .titleOf(
                                    LocaleUtil.tr(
                                            player, MenuConfig.SETTINGS_MENU.PRIVATE_MESSAGES.NAME))
                            .materialOf(
                                    ItemUtils.parseFromX(
                                            MenuConfig.SETTINGS_MENU.PRIVATE_MESSAGES.MATERIAL))
                            .descriptionOf(
                                    LocaleUtil.tr(
                                            player,
                                            MenuConfig.SETTINGS_MENU.PRIVATE_MESSAGES.DESCRIPTION))
                            .metaDataOf("private-messages")
                            .defaultOf(true)
                            .asButton());
        }
        if (MenuConfig.SETTINGS_MENU.PRIVATE_MESSAGE_SOUNDS.ENABLED) {
            entries.put(
                    entries.size(),
                    new BooleanSettingsBuilder()
                            .titleOf(
                                    LocaleUtil.tr(
                                            player,
                                            MenuConfig.SETTINGS_MENU.PRIVATE_MESSAGE_SOUNDS.NAME))
                            .materialOf(
                                    ItemUtils.parseFromX(
                                            MenuConfig.SETTINGS_MENU
                                                    .PRIVATE_MESSAGE_SOUNDS
                                                    .MATERIAL))
                            .descriptionOf(
                                    LocaleUtil.tr(
                                            player,
                                            MenuConfig.SETTINGS_MENU
                                                    .PRIVATE_MESSAGE_SOUNDS
                                                    .DESCRIPTION))
                            .metaDataOf("private-message-sounds")
                            .defaultOf(true)
                            .asButton());
        }
        if (MenuConfig.SETTINGS_MENU.GLOBAL_CHAT.ENABLED) {
            entries.put(
                    entries.size(),
                    new BooleanSettingsBuilder()
                            .titleOf(
                                    LocaleUtil.tr(
                                            player, MenuConfig.SETTINGS_MENU.GLOBAL_CHAT.NAME))
                            .materialOf(
                                    ItemUtils.parseFromX(
                                            MenuConfig.SETTINGS_MENU.GLOBAL_CHAT.MATERIAL))
                            .descriptionOf(
                                    LocaleUtil.tr(
                                            player,
                                            MenuConfig.SETTINGS_MENU.GLOBAL_CHAT.DESCRIPTION))
                            .metaDataOf("global-chat")
                            .defaultOf(true)
                            .asButton());
        }
        if (MenuConfig.SETTINGS_MENU.CHAT_MENTION.ENABLED) {
            entries.put(
                    entries.size(),
                    new BooleanSettingsBuilder()
                            .titleOf(
                                    LocaleUtil.tr(
                                            player, MenuConfig.SETTINGS_MENU.CHAT_MENTION.NAME))
                            .materialOf(
                                    ItemUtils.parseFromX(
                                            MenuConfig.SETTINGS_MENU.CHAT_MENTION.MATERIAL))
                            .descriptionOf(
                                    LocaleUtil.tr(
                                            player,
                                            MenuConfig.SETTINGS_MENU.CHAT_MENTION.DESCRIPTION))
                            .metaDataOf("chat-mention")
                            .defaultOf(true)
                            .asButton());
        }
        if (MenuConfig.SETTINGS_MENU.TIPS.ENABLED && player.hasPermission("core.settings.tips")) {
            entries.put(
                    entries.size(),
                    new BooleanSettingsBuilder()
                            .titleOf(LocaleUtil.tr(player, MenuConfig.SETTINGS_MENU.TIPS.NAME))
                            .materialOf(
                                    ItemUtils.parseFromX(MenuConfig.SETTINGS_MENU.TIPS.MATERIAL))
                            .descriptionOf(
                                    LocaleUtil.tr(
                                            player, MenuConfig.SETTINGS_MENU.TIPS.DESCRIPTION))
                            .metaDataOf("tips")
                            .defaultOf(true)
                            .permissionOf("core.settings.tips")
                            .asButton());
        }
        List<String> values;
        if (MenuConfig.SETTINGS_MENU.LANGUAGE.ENABLED
                && (values = Phoenix.getInstance().getLocaleHandler().getAvailableLocales()).size()
                        > 1) {
            entries.put(
                    entries.size(),
                    new MultiOptionsSettingsBuilder()
                            .titleOf(LocaleUtil.tr(player, MenuConfig.SETTINGS_MENU.LANGUAGE.NAME))
                            .materialOf(
                                    ItemUtils.parseFromX(
                                            MenuConfig.SETTINGS_MENU.LANGUAGE.MATERIAL))
                            .descriptionOf(
                                    LocaleUtil.tr(
                                            player, MenuConfig.SETTINGS_MENU.LANGUAGE.DESCRIPTION))
                            .valuesOf(values)
                            .metaDataOf("language")
                            .defaultOf("Automatic")
                            .asButton());
        }
        return entries;
    }

    @Override
    public boolean hasPermission(Player player) {
        return true;
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
