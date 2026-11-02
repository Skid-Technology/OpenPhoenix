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
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.events.misc.ProfileSettingUpdateEvent;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class MultiOptionsSettingsBuilder {

    private String title;

    private Material material;

    private List<String> description;

    private List<String> values;

    private String metaData;

    private String defaultValue;

    private String permission;

    private String overrideValue;

    private Runnable afterAction;

    private boolean useOverrideLore;

    private List<String> overrideLore;

    private List<String> overrideNoPermissionLore;

    private static final String N_A = "N/A";

    public MultiOptionsSettingsBuilder() {
        this.title = N_A;
        this.material = Material.AIR;
        this.description = Collections.emptyList();
        this.values = Collections.emptyList();
        this.metaData = "";
        this.defaultValue = "";
        this.permission = "";
        this.afterAction = null;
        this.useOverrideLore = false;
        this.overrideLore = new ArrayList<>();
        this.overrideNoPermissionLore = new ArrayList<>();
    }

    public MultiOptionsSettingsBuilder titleOf(String title) {
        this.title = title;
        return this;
    }

    public MultiOptionsSettingsBuilder materialOf(Material material2) {
        this.material = material2;
        return this;
    }

    public MultiOptionsSettingsBuilder materialOf(XMaterial xMaterial) {
        this.material = xMaterial.get();
        return this;
    }

    public MultiOptionsSettingsBuilder descriptionOf(String... text) {
        this.description = Arrays.asList(text);
        return this;
    }

    public MultiOptionsSettingsBuilder descriptionOf(List<String> description) {
        this.description = description;
        return this;
    }

    public MultiOptionsSettingsBuilder valuesOf(List<String> values) {
        this.values = values;
        return this;
    }

    public MultiOptionsSettingsBuilder metaDataOf(String metaData) {
        this.metaData = metaData;
        return this;
    }

    public MultiOptionsSettingsBuilder defaultOf(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public MultiOptionsSettingsBuilder permissionOf(String permission) {
        this.permission = permission;
        return this;
    }

    public MultiOptionsSettingsBuilder afterActionOf(Runnable runnable) {
        this.afterAction = runnable;
        return this;
    }

    public MultiOptionsSettingsBuilder overrideValueOf(String overrideValue) {
        this.overrideValue = overrideValue;
        return this;
    }

    public MultiOptionsSettingsBuilder overrideLoreOf(List<String> overrideLore) {
        this.overrideLore = overrideLore;
        return this;
    }

    public MultiOptionsSettingsBuilder overrideNoPermissionLoreOf(
            List<String> overrideNoPermissionLore) {
        this.overrideNoPermissionLore = overrideNoPermissionLore;
        return this;
    }

    public MultiOptionsSettingsBuilder useOverrideLoreOf(boolean useOverrideLore) {
        this.useOverrideLore = useOverrideLore;
        return this;
    }

    public Button asButton() {
        return new Button() {

            @Override
            public ItemStack getItem(Player player) {
                IProfile profile =
                        Phoenix.getInstance().getProfileHandler().getProfile(player.getUniqueId());
                String result =
                        MultiOptionsSettingsBuilder.this.overrideValue != null
                                ? MultiOptionsSettingsBuilder.this.overrideValue
                                : profile.getSetting(
                                        MultiOptionsSettingsBuilder.this.metaData,
                                        MultiOptionsSettingsBuilder.this.defaultValue);
                ItemBuilder setting;
                (setting = new ItemBuilder(MultiOptionsSettingsBuilder.this.material))
                        .setName(
                                LocaleUtil.tr(
                                                player,
                                                MenuConfig.SETTINGS_MENU.MUTLI_OPTION_SETTING.NAME)
                                        .replace("<name>", MultiOptionsSettingsBuilder.this.title));
                Iterator<String> result2 =
                        (MultiOptionsSettingsBuilder.this.useOverrideLore
                                        ? (!MultiOptionsSettingsBuilder.this.permission.isEmpty()
                                                        && !player.hasPermission(
                                                                MultiOptionsSettingsBuilder.this
                                                                        .permission)
                                                ? MultiOptionsSettingsBuilder.this
                                                        .overrideNoPermissionLore
                                                : MultiOptionsSettingsBuilder.this.overrideLore)
                                        : (!MultiOptionsSettingsBuilder.this.permission.isEmpty()
                                                        && !player.hasPermission(
                                                                MultiOptionsSettingsBuilder.this
                                                                        .permission)
                                                ? LocaleUtil.tr(
                                                        player,
                                                        MenuConfig.SETTINGS_MENU
                                                                .MUTLI_OPTION_SETTING
                                                                .NO_PERMISSION_LORE)
                                                : LocaleUtil.tr(
                                                        player,
                                                        MenuConfig.SETTINGS_MENU
                                                                .MUTLI_OPTION_SETTING
                                                                .LORE)))
                                .iterator();
                while (result2.hasNext()) {
                    String text;
                    if ((text = (String) result2.next()).contains("<description>")) {
                        for (String text2 : MultiOptionsSettingsBuilder.this.description) {
                            setting.addLoreLine("&7" + text2);
                        }
                    } else if (text.contains("<options>")) {
                        for (String text3 : MultiOptionsSettingsBuilder.this.values) {
                            setting.addLoreLine(
                                    Objects.equals(result, text3)
                                            ? LocaleUtil.tr(
                                                            player,
                                                            MenuConfig.SETTINGS_MENU
                                                                    .MUTLI_OPTION_SETTING
                                                                    .OPTIONS_STRING_SELECTED)
                                                    .replace(
                                                            "<name>",
                                                            MultiOptionsSettingsBuilder.this.title)
                                                    .replace("<option>", text3)
                                            : LocaleUtil.tr(
                                                            player,
                                                            MenuConfig.SETTINGS_MENU
                                                                    .MUTLI_OPTION_SETTING
                                                                    .OPTIONS_STRING)
                                                    .replace(
                                                            "<name>",
                                                            MultiOptionsSettingsBuilder.this.title)
                                                    .replace("<option>", text3));
                        }
                    } else {
                        setting.addLoreLine(text);
                    }
                }
                return setting.toItemStack();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                if (MultiOptionsSettingsBuilder.this.permission.isEmpty()
                        || player.hasPermission(MultiOptionsSettingsBuilder.this.permission)) {
                    IProfile result;
                    String setting =
                            (result =
                                            Phoenix.getInstance()
                                                    .getProfileHandler()
                                                    .getProfile(player.getUniqueId()))
                                    .getSetting(
                                            MultiOptionsSettingsBuilder.this.metaData,
                                            MultiOptionsSettingsBuilder.this.defaultValue);
                    int value = clickType.isRightClick() ? -1 : 1;
                    int result2;
                    String text =
                            (result2 =
                                                    MultiOptionsSettingsBuilder.this.values.indexOf(
                                                                    setting)
                                                            + value)
                                            >= MultiOptionsSettingsBuilder.this.values.size()
                                    ? MultiOptionsSettingsBuilder.this.defaultValue
                                    : (result2 < 0
                                            ? MultiOptionsSettingsBuilder.this.values.get(
                                                    MultiOptionsSettingsBuilder.this.values.size()
                                                            - 1)
                                            : MultiOptionsSettingsBuilder.this.values.get(result2));
                    result.updateMetadata(MultiOptionsSettingsBuilder.this.metaData, text, false);
                    if (MultiOptionsSettingsBuilder.this.afterAction != null) {
                        MultiOptionsSettingsBuilder.this.afterAction.run();
                    }
                    Phoenix.getInstance()
                            .getMenuHandler()
                            .openMenu(
                                    Phoenix.getInstance().getMenuHandler().getByPlayer(player),
                                    player);
                    BukkitAPI.callEvent(
                            new ProfileSettingUpdateEvent(
                                    player.getUniqueId(),
                                    MultiOptionsSettingsBuilder.this.metaData));
                }
            }
        };
    }
}
