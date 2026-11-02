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

public final class IntegerSettingsBuilder {

    private String title;

    private Material material;

    private List<String> description;

    private String metaData;

    private String permission;

    private int limit;

    private int defaultValue;

    private String overrideValue;

    private Runnable afterAction;

    private boolean useOverrideLore;

    private List<String> overrideLore;

    private List<String> overrideNoPermissionLore;

    private static final String N_A = "N/A";

    public IntegerSettingsBuilder() {
        this.title = N_A;
        this.material = Material.AIR;
        this.description = Collections.emptyList();
        this.metaData = "";
        this.permission = "";
        this.limit = 50;
        this.defaultValue = 0;
        this.afterAction = null;
        this.useOverrideLore = false;
        this.overrideLore = new ArrayList<>();
        this.overrideNoPermissionLore = new ArrayList<>();
    }

    public IntegerSettingsBuilder titleOf(String title) {
        this.title = title;
        return this;
    }

    public IntegerSettingsBuilder materialOf(Material material2) {
        this.material = material2;
        return this;
    }

    public IntegerSettingsBuilder materialOf(XMaterial xMaterial) {
        this.material = xMaterial.get();
        return this;
    }

    public IntegerSettingsBuilder descriptionOf(String... text) {
        this.description = Arrays.asList(text);
        return this;
    }

    public IntegerSettingsBuilder descriptionOf(List<String> description) {
        this.description = description;
        return this;
    }

    public IntegerSettingsBuilder metaDataOf(String metaData) {
        this.metaData = metaData;
        return this;
    }

    public IntegerSettingsBuilder permissionOf(String permission) {
        this.permission = permission;
        return this;
    }

    public IntegerSettingsBuilder defaultOf(int defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public IntegerSettingsBuilder limitOf(int limit) {
        this.limit = limit;
        return this;
    }

    public IntegerSettingsBuilder afterActionOf(Runnable runnable) {
        this.afterAction = runnable;
        return this;
    }

    public IntegerSettingsBuilder overrideValueOf(String overrideValue) {
        this.overrideValue = overrideValue;
        return this;
    }

    public IntegerSettingsBuilder overrideLoreOf(List<String> overrideLore) {
        this.overrideLore = overrideLore;
        return this;
    }

    public IntegerSettingsBuilder overrideNoPermissionLoreOf(
            List<String> overrideNoPermissionLore) {
        this.overrideNoPermissionLore = overrideNoPermissionLore;
        return this;
    }

    public IntegerSettingsBuilder useOverrideLoreOf(boolean useOverrideLore) {
        this.useOverrideLore = useOverrideLore;
        return this;
    }

    public Button asButton() {
        return new Button() {

            @Override
            public ItemStack getItem(Player player) {
                IProfile profile =
                        Phoenix.getInstance().getProfileHandler().getProfile(player.getUniqueId());
                int result =
                        IntegerSettingsBuilder.this.overrideValue != null
                                ? Integer.parseInt(IntegerSettingsBuilder.this.overrideValue)
                                : profile.getIntSetting(
                                        IntegerSettingsBuilder.this.metaData,
                                        IntegerSettingsBuilder.this.defaultValue);
                ItemBuilder intSetting;
                (intSetting = new ItemBuilder(IntegerSettingsBuilder.this.material))
                        .setName(
                                LocaleUtil.tr(player, MenuConfig.SETTINGS_MENU.NUMBER_SETTING.NAME)
                                        .replace("<name>", IntegerSettingsBuilder.this.title));
                List<String> result2 =
                        IntegerSettingsBuilder.this.useOverrideLore
                                ? (!IntegerSettingsBuilder.this.permission.isEmpty()
                                                && !player.hasPermission(
                                                        IntegerSettingsBuilder.this.permission)
                                        ? IntegerSettingsBuilder.this.overrideNoPermissionLore
                                        : IntegerSettingsBuilder.this.overrideLore)
                                : (!IntegerSettingsBuilder.this.permission.isEmpty()
                                                && !player.hasPermission(
                                                        IntegerSettingsBuilder.this.permission)
                                        ? LocaleUtil.tr(
                                                player,
                                                MenuConfig.SETTINGS_MENU
                                                        .NUMBER_SETTING
                                                        .NO_PERMISSION)
                                        : LocaleUtil.tr(
                                                player,
                                                MenuConfig.SETTINGS_MENU.NUMBER_SETTING.LORE));
                String result3 = result == 0 ? "Unrestricted" : String.valueOf(result);
                Iterator iterator2 = result2.iterator();
                while (iterator2.hasNext()) {
                    String text;
                    if ((text = (String) iterator2.next()).contains("<description>")) {
                        for (String text2 : IntegerSettingsBuilder.this.description) {
                            intSetting.addLoreLine("&7" + text2);
                        }
                    } else {
                        intSetting.addLoreLine(
                                text.replace("<name>", IntegerSettingsBuilder.this.title)
                                        .replace("<number>", result3));
                    }
                }
                return intSetting.toItemStack();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                if (IntegerSettingsBuilder.this.permission.isEmpty()
                        || player.hasPermission(IntegerSettingsBuilder.this.permission)) {
                    IProfile result;
                    int intSetting =
                            (result =
                                            Phoenix.getInstance()
                                                    .getProfileHandler()
                                                    .getProfile(player.getUniqueId()))
                                    .getIntSetting(
                                            IntegerSettingsBuilder.this.metaData,
                                            IntegerSettingsBuilder.this.defaultValue);
                    byte result2;
                    int result3 =
                            (result2 =
                                                    (byte)
                                                            (clickType.isRightClick()
                                                                    ? intSetting
                                                                            - MenuConfig
                                                                                    .SETTINGS_MENU
                                                                                    .NUMBER_SETTING
                                                                                    .CHANGE_BY
                                                                    : intSetting
                                                                            + MenuConfig
                                                                                    .SETTINGS_MENU
                                                                                    .NUMBER_SETTING
                                                                                    .CHANGE_BY))
                                            >= IntegerSettingsBuilder.this.limit
                                    ? IntegerSettingsBuilder.this.defaultValue
                                    : (result2 < 0 ? IntegerSettingsBuilder.this.limit : result2);
                    result.updateMetadata(IntegerSettingsBuilder.this.metaData, result3, false);
                    if (IntegerSettingsBuilder.this.afterAction != null) {
                        IntegerSettingsBuilder.this.afterAction.run();
                    }
                    Phoenix.getInstance()
                            .getMenuHandler()
                            .openMenu(
                                    Phoenix.getInstance().getMenuHandler().getByPlayer(player),
                                    player);
                    BukkitAPI.callEvent(
                            new ProfileSettingUpdateEvent(
                                    player.getUniqueId(), IntegerSettingsBuilder.this.metaData));
                }
            }
        };
    }

    public String getTitle() {
        return this.title;
    }

    public Material getMaterial() {
        return this.material;
    }

    public List<String> getDescription() {
        return this.description;
    }

    public String getMetaData() {
        return this.metaData;
    }

    public String getPermission() {
        return this.permission;
    }

    public int getLimit() {
        return this.limit;
    }

    public int getDefaultValue() {
        return this.defaultValue;
    }

    public String getOverrideValue() {
        return this.overrideValue;
    }

    public Runnable getAfterAction() {
        return this.afterAction;
    }

    public boolean isUseOverrideLore() {
        return this.useOverrideLore;
    }

    public List<String> getOverrideLore() {
        return this.overrideLore;
    }

    public List<String> getOverrideNoPermissionLore() {
        return this.overrideNoPermissionLore;
    }
}
