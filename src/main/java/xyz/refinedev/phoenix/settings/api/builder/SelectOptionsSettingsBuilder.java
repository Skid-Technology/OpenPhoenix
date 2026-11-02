package xyz.refinedev.phoenix.settings.api.builder;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.StringUtils;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.events.misc.ProfileSettingUpdateEvent;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class SelectOptionsSettingsBuilder {

    private String title;

    private Material material;

    private List<String> description;

    private List<String> values;

    private String defaultValue;

    private String metaData;

    private int currentIndex;

    private String permission;

    private List<String> overrideValue;

    private Runnable afterAction;

    private static final String N_A = "N/A";

    public SelectOptionsSettingsBuilder() {
        this.title = N_A;
        this.material = Material.AIR;
        this.description = Collections.emptyList();
        this.values = Collections.emptyList();
        this.defaultValue = "";
        this.metaData = "";
        this.currentIndex = 0;
        this.permission = "";
        this.afterAction = null;
    }

    public SelectOptionsSettingsBuilder titleOf(String title) {
        this.title = title;
        return this;
    }

    public SelectOptionsSettingsBuilder materialOf(Material material2) {
        this.material = material2;
        return this;
    }

    public SelectOptionsSettingsBuilder materialOf(XMaterial xMaterial) {
        this.material = xMaterial.get();
        return this;
    }

    public SelectOptionsSettingsBuilder descriptionOf(String... text) {
        this.description = Arrays.asList(text);
        return this;
    }

    public SelectOptionsSettingsBuilder descriptionOf(List<String> description) {
        this.description = description;
        return this;
    }

    public SelectOptionsSettingsBuilder defaultOf(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public SelectOptionsSettingsBuilder valuesOf(List<String> values) {
        this.values = values;
        return this;
    }

    public SelectOptionsSettingsBuilder metaDataOf(String metaData) {
        this.metaData = metaData;
        return this;
    }

    public SelectOptionsSettingsBuilder permissionOf(String permission) {
        this.permission = permission;
        return this;
    }

    public SelectOptionsSettingsBuilder afterActionOf(Runnable runnable) {
        this.afterAction = runnable;
        return this;
    }

    public SelectOptionsSettingsBuilder overrideValueOf(List<String> overrideValue) {
        this.overrideValue = overrideValue;
        return this;
    }

    public Button asButton() {
        return new Button() {

            @Override
            public ItemStack getItem(Player player) {
                IProfile profile =
                        Phoenix.getInstance().getProfileHandler().getProfile(player.getUniqueId());
                SelectOptionsSettingsBuilder.this.currentIndex =
                        profile.getIntSetting(
                                SelectOptionsSettingsBuilder.this.metaData + "-option",
                                SelectOptionsSettingsBuilder.this.currentIndex);
                List<String> values2 =
                        SelectOptionsSettingsBuilder.this.overrideValue != null
                                ? SelectOptionsSettingsBuilder.this.overrideValue
                                : StringUtils.getListFromString(
                                        profile.getSetting(
                                                SelectOptionsSettingsBuilder.this.metaData,
                                                SelectOptionsSettingsBuilder.this.defaultValue));
                ItemBuilder result;
                (result = new ItemBuilder(SelectOptionsSettingsBuilder.this.material))
                        .setName(
                                LocaleUtil.tr(
                                                player,
                                                MenuConfig.SETTINGS_MENU.SELECT_OPTION_SETTING.NAME)
                                        .replace(
                                                "<name>", SelectOptionsSettingsBuilder.this.title));
                Iterator<String> result2 =
                        (!SelectOptionsSettingsBuilder.this.permission.isEmpty()
                                                && !player.hasPermission(
                                                        SelectOptionsSettingsBuilder.this
                                                                .permission)
                                        ? LocaleUtil.tr(
                                                player,
                                                MenuConfig.SETTINGS_MENU
                                                        .SELECT_OPTION_SETTING
                                                        .NO_PERMISSION_LORE)
                                        : LocaleUtil.tr(
                                                player,
                                                MenuConfig.SETTINGS_MENU
                                                        .SELECT_OPTION_SETTING
                                                        .LORE))
                                .iterator();
                while (result2.hasNext()) {
                    String text;
                    if ((text = (String) result2.next()).contains("<description>")) {
                        for (String text2 : SelectOptionsSettingsBuilder.this.description) {
                            result.addLoreLine("&7" + text2);
                        }
                    } else if (text.contains("<options>")) {
                        for (String text3 : SelectOptionsSettingsBuilder.this.values) {
                            if (Objects.equals(
                                    SelectOptionsSettingsBuilder.this.values.get(
                                            SelectOptionsSettingsBuilder.this.currentIndex),
                                    text3)) {
                                result.addLoreLine(
                                        values2.contains(text3)
                                                ? LocaleUtil.tr(
                                                                player,
                                                                MenuConfig.SETTINGS_MENU
                                                                        .SELECT_OPTION_SETTING
                                                                        .OPTION_STRING_SELECTING_SELECTED)
                                                        .replace("<option>", text3)
                                                        .replace(
                                                                "<name>",
                                                                SelectOptionsSettingsBuilder.this
                                                                        .title)
                                                : LocaleUtil.tr(
                                                                player,
                                                                MenuConfig.SETTINGS_MENU
                                                                        .SELECT_OPTION_SETTING
                                                                        .OPTION_STRING_SELECTING)
                                                        .replace("<option>", text3)
                                                        .replace(
                                                                "<name>",
                                                                SelectOptionsSettingsBuilder.this
                                                                        .title));
                            } else {
                                result.addLoreLine(
                                        values2.contains(text3)
                                                ? LocaleUtil.tr(
                                                                player,
                                                                MenuConfig.SETTINGS_MENU
                                                                        .SELECT_OPTION_SETTING
                                                                        .OPTIONS_STRING_SELECTED)
                                                        .replace("<option>", text3)
                                                        .replace(
                                                                "<name>",
                                                                SelectOptionsSettingsBuilder.this
                                                                        .title)
                                                : LocaleUtil.tr(
                                                                player,
                                                                MenuConfig.SETTINGS_MENU
                                                                        .SELECT_OPTION_SETTING
                                                                        .OPTIONS_STRING)
                                                        .replace("<option>", text3)
                                                        .replace(
                                                                "<name>",
                                                                SelectOptionsSettingsBuilder.this
                                                                        .title));
                            }
                        }
                    } else {
                        result.addLoreLine(text);
                    }
                }
                return result.toItemStack();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                if (SelectOptionsSettingsBuilder.this.permission.isEmpty()
                        || player.hasPermission(SelectOptionsSettingsBuilder.this.permission)) {
                    IProfile profile =
                            Phoenix.getInstance()
                                    .getProfileHandler()
                                    .getProfile(player.getUniqueId());
                    if (clickType.isShiftClick()) {
                        List<String> values2 =
                                SelectOptionsSettingsBuilder.this.overrideValue != null
                                        ? SelectOptionsSettingsBuilder.this.overrideValue
                                        : StringUtils.getListFromString(
                                                profile.getSetting(
                                                        SelectOptionsSettingsBuilder.this.metaData,
                                                        ""));
                        String result =
                                SelectOptionsSettingsBuilder.this.values.get(
                                        SelectOptionsSettingsBuilder.this.currentIndex);
                        if (values2.contains(result)) {
                            values2.remove(result);
                        } else {
                            values2.add(result);
                        }
                        profile.updateMetadata(
                                SelectOptionsSettingsBuilder.this.metaData,
                                StringUtils.getStringFromList(values2),
                                false);
                        if (SelectOptionsSettingsBuilder.this.afterAction != null) {
                            SelectOptionsSettingsBuilder.this.afterAction.run();
                        }
                        BukkitAPI.callEvent(
                                new ProfileSettingUpdateEvent(
                                        player.getUniqueId(),
                                        SelectOptionsSettingsBuilder.this.metaData));
                        Phoenix.getInstance()
                                .getMenuHandler()
                                .openMenu(
                                        Phoenix.getInstance().getMenuHandler().getByPlayer(player),
                                        player);
                    } else {
                        int value = clickType.isRightClick() ? -1 : 1;
                        int result2 = SelectOptionsSettingsBuilder.this.currentIndex + value;
                        SelectOptionsSettingsBuilder.this.currentIndex =
                                result2 >= SelectOptionsSettingsBuilder.this.values.size()
                                        ? 0
                                        : (result2 < 0
                                                ? SelectOptionsSettingsBuilder.this.values.size()
                                                        - 1
                                                : result2);
                        profile.updateMetadata(
                                SelectOptionsSettingsBuilder.this.metaData + "-option",
                                SelectOptionsSettingsBuilder.this.currentIndex,
                                false);
                        if (SelectOptionsSettingsBuilder.this.afterAction != null) {
                            SelectOptionsSettingsBuilder.this.afterAction.run();
                        }
                        BukkitAPI.callEvent(
                                new ProfileSettingUpdateEvent(
                                        player.getUniqueId(),
                                        SelectOptionsSettingsBuilder.this.metaData));
                        Phoenix.getInstance()
                                .getMenuHandler()
                                .openMenu(
                                        Phoenix.getInstance().getMenuHandler().getByPlayer(player),
                                        player);
                    }
                }
            }
        };
    }
}
