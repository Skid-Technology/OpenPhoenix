package xyz.refinedev.phoenix.dialog.impl;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.CommonDialogData;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.Dialog;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.DialogAction;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.MultiActionDialog;
import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.dialog.api.DialogResponse;
import xyz.refinedev.phoenix.dialog.api.PhoenixDialog;
import xyz.refinedev.phoenix.menu.impl.SettingsMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.settings.AdminPlayerSettings;
import xyz.refinedev.phoenix.profile.settings.GeneralPlayerSettings;
import xyz.refinedev.phoenix.profile.settings.StaffPlayerSettings;
import xyz.refinedev.phoenix.settings.api.PlayerSettings;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.events.misc.ProfileSettingUpdateEvent;
import xyz.refinedev.phoenix.utils.general.Tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SettingsDialog extends PhoenixDialog {

    private static final String SETTING_TEXT = "setting";

    private static final String MENU_TEXT = "menu";

    private static final String CHANNEL_TEXT = "channel";

    private final Phoenix plugin;

    public SettingsDialog(Phoenix phoenix) {
        super("settings");
        this.plugin = phoenix;
    }

    @Override
    public Dialog build(Player player) {
        IProfile profile;
        if ((profile = this.plugin.getProfileHandler().getCachedProfile(player.getUniqueId()))
                == null) {
            return null;
        }
        List<xyz.refinedev.phoenix.dialog.impl.SettingsDialog.Entry> values;
        if ((values = this.findList(player)).isEmpty()) {
            return null;
        }
        ArrayList<
                        xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.dialog.button
                                .ActionButton>
                values2 = new ArrayList<>();
        for (SettingsDialog.Entry entry : (Iterable<SettingsDialog.Entry>) ((Iterable) values)) {
            values2.add(
                    this.button(
                            this.formatString(player, profile, entry),
                            this.formatStringByENABLED_LORE(player, profile, entry),
                            "setting",
                            SettingsDialog.Entry.getText(entry)));
        }
        if (this.isEnabled()) {
            values2.add(this.button("&7All Settings...", null, "menu", "true"));
        }
        CommonDialogData commonDialogData =
                new CommonDialogData(
                        Color.translateComponent("&eSettings"),
                        null,
                        true,
                        false,
                        DialogAction.NONE,
                        this.body(Collections.singletonList("&7Press a setting to change it.")),
                        Collections.emptyList());
        return new MultiActionDialog(commonDialogData, values2, this.close(player), 2);
    }

    @Override
    public void handle(Player player, DialogResponse dialogResponse) {
        if (!this.closed(player, dialogResponse)) {
            if (dialogResponse.matchesEnabled("menu", false)) {
                Tasks.run(() -> this.plugin.getMenuHandler().openMenu(new SettingsMenu(), player));
            } else {
                IProfile cachedProfile =
                        this.plugin.getProfileHandler().getCachedProfile(player.getUniqueId());
                String text = dialogResponse.formatString("setting", null, 64);
                if (cachedProfile != null && text != null) {
                    Iterator<xyz.refinedev.phoenix.dialog.impl.SettingsDialog.Entry> iterator2 =
                            this.findList(player).iterator();
                    while (iterator2.hasNext()) {
                        SettingsDialog.Entry entry;
                        if (SettingsDialog.Entry.getText(
                                        entry = (SettingsDialog.Entry) iterator2.next())
                                .equals(text)) {
                            this.applyProfile(cachedProfile, entry);
                            BukkitAPI.callEvent(
                                    new ProfileSettingUpdateEvent(
                                            player.getUniqueId(),
                                            SettingsDialog.Entry.getText(entry)));
                            this.plugin.getDialogHandler().matchesEnabled(player, this, null);
                            return;
                        }
                    }
                    this.dismiss(player);
                } else {
                    this.dismiss(player);
                }
            }
        }
    }

    private void applyProfile(IProfile profile, SettingsDialog.Entry entry) {
        if (SettingsDialog.Entry.getStringsList(entry) == null) {
            profile.updateMetadata(
                    SettingsDialog.Entry.getText(entry),
                    !this.matchesEnabled(profile, entry),
                    false);
        } else {
            int value =
                    SettingsDialog.Entry.getStringsList(entry)
                                    .indexOf(
                                            profile.getSetting(
                                                    SettingsDialog.Entry.getText(entry),
                                                    SettingsDialog.Entry.getText2(entry)))
                            + 1;
            profile.updateMetadata(
                    SettingsDialog.Entry.getText(entry),
                    value >= SettingsDialog.Entry.getStringsList(entry).size()
                            ? SettingsDialog.Entry.getText2(entry)
                            : SettingsDialog.Entry.getStringsList(entry).get(value),
                    false);
        }
    }

    private String formatString(Player player, IProfile profile, SettingsDialog.Entry entry) {
        return SettingsDialog.Entry.getStringsList(entry) == null
                ? LocaleUtil.tr(
                                player,
                                this.matchesEnabled(profile, entry)
                                        ? MenuConfig.GENERAL.FORM.TOGGLE_ON
                                        : MenuConfig.GENERAL.FORM.TOGGLE_OFF)
                        .replace("<name>", SettingsDialog.Entry.getTextString(entry))
                : LocaleUtil.tr(player, MenuConfig.GENERAL.FORM.OPTION)
                        .replace("<name>", SettingsDialog.Entry.getTextString(entry))
                        .replace(
                                "<option>",
                                profile.getSetting(
                                        SettingsDialog.Entry.getText(entry),
                                        SettingsDialog.Entry.getText2(entry)));
    }

    private String formatStringByENABLED_LORE(
            Player player, IProfile profile, SettingsDialog.Entry entry) {
        boolean matches;
        boolean matches2 =
                (matches = SettingsDialog.Entry.getStringsList(entry) == null)
                        && this.matchesEnabled(profile, entry);
        String text =
                matches
                        ? null
                        : profile.getSetting(
                                SettingsDialog.Entry.getText(entry),
                                SettingsDialog.Entry.getText2(entry));
        List<String> values =
                LocaleUtil.tr(
                        player,
                        matches
                                ? (matches2
                                        ? MenuConfig.SETTINGS_MENU.BOOLEAN_SETTING.ENABLED_LORE
                                        : MenuConfig.SETTINGS_MENU.BOOLEAN_SETTING.DISABLED_LORE)
                                : MenuConfig.SETTINGS_MENU.MUTLI_OPTION_SETTING.LORE);
        StringBuilder stringBuilder = new StringBuilder();
        Iterator iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            String text2;
            if ((text2 = (String) iterator2.next()).contains("<description>")) {
                for (String text3 :
                        (Iterable<String>)
                                ((Iterable)
                                        LocaleUtil.tr(
                                                player, SettingsDialog.Entry.getStrings(entry)))) {
                    this.applyStringBuilder(stringBuilder, "&7" + text3);
                }
            } else if (text2.contains("<options>")) {
                for (String text4 :
                        (Iterable<String>)
                                ((Iterable) SettingsDialog.Entry.getStringsList(entry))) {
                    this.applyStringBuilder(
                            stringBuilder,
                            LocaleUtil.tr(
                                            player,
                                            text4.equals(text)
                                                    ? MenuConfig.SETTINGS_MENU
                                                            .MUTLI_OPTION_SETTING
                                                            .OPTIONS_STRING_SELECTED
                                                    : MenuConfig.SETTINGS_MENU
                                                            .MUTLI_OPTION_SETTING
                                                            .OPTIONS_STRING)
                                    .replace("<name>", SettingsDialog.Entry.getTextString(entry))
                                    .replace("<option>", text4));
                }
            } else {
                this.applyStringBuilder(
                        stringBuilder,
                        text2.replace("<name>", SettingsDialog.Entry.getTextString(entry)));
            }
        }
        return stringBuilder.length() == 0 ? null : stringBuilder.toString();
    }

    private void applyStringBuilder(StringBuilder stringBuilder, String text) {
        if (stringBuilder.length() > 0) {
            stringBuilder.append('\n');
        }
        stringBuilder.append(text);
    }

    private boolean matchesEnabled(IProfile profile, SettingsDialog.Entry entry) {
        return profile.isMetadata(
                SettingsDialog.Entry.getText(entry),
                Boolean.parseBoolean(SettingsDialog.Entry.getText2(entry)));
    }

    private List<SettingsDialog.Entry> findList(Player player) {
        ArrayList<SettingsDialog.Entry> values = new ArrayList<>();
        if (MenuConfig.SETTINGS_MENU.PRIVATE_MESSAGES.ENABLED) {
            values.add(
                    this.create(
                            player,
                            "private-messages",
                            MenuConfig.SETTINGS_MENU.PRIVATE_MESSAGES.NAME,
                            MenuConfig.SETTINGS_MENU.PRIVATE_MESSAGES.DESCRIPTION));
        }
        if (MenuConfig.SETTINGS_MENU.PRIVATE_MESSAGE_SOUNDS.ENABLED) {
            values.add(
                    this.create(
                            player,
                            "private-message-sounds",
                            MenuConfig.SETTINGS_MENU.PRIVATE_MESSAGE_SOUNDS.NAME,
                            MenuConfig.SETTINGS_MENU.PRIVATE_MESSAGE_SOUNDS.DESCRIPTION));
        }
        if (MenuConfig.SETTINGS_MENU.GLOBAL_CHAT.ENABLED) {
            values.add(
                    this.create(
                            player,
                            "global-chat",
                            MenuConfig.SETTINGS_MENU.GLOBAL_CHAT.NAME,
                            MenuConfig.SETTINGS_MENU.GLOBAL_CHAT.DESCRIPTION));
        }
        if (MenuConfig.SETTINGS_MENU.CHAT_MENTION.ENABLED) {
            values.add(
                    this.create(
                            player,
                            "chat-mention",
                            MenuConfig.SETTINGS_MENU.CHAT_MENTION.NAME,
                            MenuConfig.SETTINGS_MENU.CHAT_MENTION.DESCRIPTION));
        }
        if (MenuConfig.SETTINGS_MENU.TIPS.ENABLED && player.hasPermission("core.settings.tips")) {
            values.add(
                    this.create(
                            player,
                            "tips",
                            MenuConfig.SETTINGS_MENU.TIPS.NAME,
                            MenuConfig.SETTINGS_MENU.TIPS.DESCRIPTION));
        }
        List values2;
        if (MenuConfig.SETTINGS_MENU.LANGUAGE.ENABLED
                && (values2 = this.plugin.getLocaleHandler().getAvailableLocales()).size() > 1) {
            values.add(
                    this.create(
                            player,
                            "language",
                            MenuConfig.SETTINGS_MENU.LANGUAGE.NAME,
                            MenuConfig.SETTINGS_MENU.LANGUAGE.DESCRIPTION,
                            values2,
                            "Automatic"));
        }
        if (player.hasPermission("core.staff")) {
            if (!(values2 = this.plugin.getChatHandler().getAccessChannelsSorted(player))
                    .isEmpty()) {
                values.add(
                        this.create(
                                player,
                                "channel",
                                MenuConfig.SETTINGS_MENU.CHAT_CHANNEL.NAME,
                                MenuConfig.SETTINGS_MENU.CHAT_CHANNEL.DESCRIPTION,
                                values2,
                                "Default"));
            }
            values.add(
                    this.create(
                            player,
                            "staff-messages",
                            MenuConfig.SETTINGS_MENU.STAFF_MESSAGES.NAME,
                            MenuConfig.SETTINGS_MENU.STAFF_MESSAGES.DESCRIPTION));
            values.add(
                    this.create(
                            player,
                            "filtered-messages",
                            MenuConfig.SETTINGS_MENU.FILTERED_MESSAGES.NAME,
                            MenuConfig.SETTINGS_MENU.FILTERED_MESSAGES.DESCRIPTION));
            if (SettingsConfig.REPORTS.ENABLED) {
                values.add(
                        this.create(
                                player,
                                "reports",
                                MenuConfig.SETTINGS_MENU.REPORTS.NAME,
                                MenuConfig.SETTINGS_MENU.REPORTS.DESCRIPTION));
                values.add(
                        this.create(
                                player,
                                "requests",
                                MenuConfig.SETTINGS_MENU.REQUESTS.NAME,
                                MenuConfig.SETTINGS_MENU.REQUESTS.DESCRIPTION));
            }
        }
        if (player.hasPermission("core.admin")) {
            values.add(
                    this.create(
                            player,
                            "admin-alerts",
                            MenuConfig.SETTINGS_MENU.ADMIN_ALERTS.NAME,
                            MenuConfig.SETTINGS_MENU.ADMIN_ALERTS.DESCRIPTION));
        }
        return values;
    }

    private SettingsDialog.Entry create(
            Player player, String text, String text2, List<String> values) {
        return new SettingsDialog.Entry(text, LocaleUtil.tr(player, text2), values, null, "true");
    }

    private SettingsDialog.Entry create(
            Player player,
            String text,
            String text2,
            List<String> values,
            List<String> values2,
            String text3) {
        return new SettingsDialog.Entry(text, LocaleUtil.tr(player, text2), values, values2, text3);
    }

    private boolean isEnabled() {
        Iterator<PlayerSettings> iterator2 = this.plugin.getPlayerSettings().iterator();
        while (iterator2.hasNext()) {
            PlayerSettings playerSettings;
            if (!((playerSettings = (PlayerSettings) iterator2.next())
                            instanceof GeneralPlayerSettings)
                    && !(playerSettings instanceof StaffPlayerSettings)
                    && !(playerSettings instanceof AdminPlayerSettings)) {
                return true;
            }
        }
        return false;
    }

    static class Entry {

        private final String text;

        private final String textString;

        private final List<String> strings;

        private final List<String> stringsList;

        private final String text2;

        public Entry(
                String text,
                String textString,
                List<String> strings,
                List<String> stringsList,
                String text2) {
            this.text = text;
            this.textString = textString;
            this.strings = strings;
            this.stringsList = stringsList;
            this.text2 = text2;
        }

        static String getText(SettingsDialog.Entry entry) {
            return entry.text;
        }

        static List<String> getStringsList(SettingsDialog.Entry entry) {
            return entry.stringsList;
        }

        static String getText2(SettingsDialog.Entry entry) {
            return entry.text2;
        }

        static String getTextString(SettingsDialog.Entry entry) {
            return entry.textString;
        }

        static List<String> getStrings(SettingsDialog.Entry entry) {
            return entry.strings;
        }
    }
}
