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

public final class BooleanSettingsBuilder {

    private String title;

    private Material material;

    private List<String> description;

    private String metaData;

    private String permission;

    private boolean defaultValue;

    private String overrideValue;

    private Runnable afterAction;

    private boolean useOverrideLore;

    private List<String> overrideEnabledLore;

    private List<String> overrideDisabledLore;

    private List<String> overrideNoPermissionLore;

    private static final String N_A = "N/A";

    public BooleanSettingsBuilder() {
        this.title = N_A;
        this.material = Material.AIR;
        this.description = Collections.emptyList();
        this.metaData = "";
        this.permission = "";
        this.defaultValue = true;
        this.afterAction = null;
        this.useOverrideLore = false;
        this.overrideEnabledLore = new ArrayList<>();
        this.overrideDisabledLore = new ArrayList<>();
        this.overrideNoPermissionLore = new ArrayList<>();
    }

    public BooleanSettingsBuilder titleOf(String title) {
        this.title = title;
        return this;
    }

    public BooleanSettingsBuilder materialOf(Material material2) {
        this.material = material2;
        return this;
    }

    public BooleanSettingsBuilder materialOf(XMaterial xMaterial) {
        this.material = xMaterial.get();
        return this;
    }

    public BooleanSettingsBuilder descriptionOf(String... text) {
        this.description = Arrays.asList(text);
        return this;
    }

    public BooleanSettingsBuilder descriptionOf(List<String> description) {
        this.description = description;
        return this;
    }

    public BooleanSettingsBuilder metaDataOf(String metaData) {
        this.metaData = metaData;
        return this;
    }

    public BooleanSettingsBuilder permissionOf(String permission) {
        this.permission = permission;
        return this;
    }

    public BooleanSettingsBuilder defaultOf(boolean defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public BooleanSettingsBuilder afterActionOf(Runnable runnable) {
        this.afterAction = runnable;
        return this;
    }

    public BooleanSettingsBuilder overrideValueOf(String overrideValue) {
        this.overrideValue = overrideValue;
        return this;
    }

    public BooleanSettingsBuilder overrideEnabledLoreOf(List<String> overrideEnabledLore) {
        this.overrideEnabledLore = overrideEnabledLore;
        return this;
    }

    public BooleanSettingsBuilder overrideDisabledLoreOf(List<String> overrideDisabledLore) {
        this.overrideDisabledLore = overrideDisabledLore;
        return this;
    }

    public BooleanSettingsBuilder overrideNoPermissionLoreOf(
            List<String> overrideNoPermissionLore) {
        this.overrideNoPermissionLore = overrideNoPermissionLore;
        return this;
    }

    public BooleanSettingsBuilder useOverrideLoreOf(boolean useOverrideLore) {
        this.useOverrideLore = useOverrideLore;
        return this;
    }

    public Button asButton() {
        return new Button() {

            @Override
            public ItemStack getItem(Player player) {
                IProfile profile =
                        Phoenix.getInstance().getProfileHandler().getProfile(player.getUniqueId());
                boolean result =
                        BooleanSettingsBuilder.this.overrideValue != null
                                ? Boolean.parseBoolean(BooleanSettingsBuilder.this.overrideValue)
                                : profile.isMetadata(
                                        BooleanSettingsBuilder.this.metaData,
                                        BooleanSettingsBuilder.this.defaultValue);
                ItemBuilder metadata = new ItemBuilder(BooleanSettingsBuilder.this.material);
                String text =
                        result
                                ? LocaleUtil.tr(
                                        player,
                                        MenuConfig.SETTINGS_MENU.BOOLEAN_SETTING.ENABLED_NAME)
                                : LocaleUtil.tr(
                                        player,
                                        MenuConfig.SETTINGS_MENU.BOOLEAN_SETTING.DISABLED_NAME);
                List<String> values =
                        BooleanSettingsBuilder.this.useOverrideLore
                                ? (!BooleanSettingsBuilder.this.permission.isEmpty()
                                                && !player.hasPermission(
                                                        BooleanSettingsBuilder.this.permission)
                                        ? BooleanSettingsBuilder.this.overrideNoPermissionLore
                                        : (result
                                                ? BooleanSettingsBuilder.this.overrideEnabledLore
                                                : BooleanSettingsBuilder.this.overrideDisabledLore))
                                : (!BooleanSettingsBuilder.this.permission.isEmpty()
                                                && !player.hasPermission(
                                                        BooleanSettingsBuilder.this.permission)
                                        ? (result
                                                ? LocaleUtil.tr(
                                                        player,
                                                        MenuConfig.SETTINGS_MENU
                                                                .BOOLEAN_SETTING
                                                                .NO_PERMISSION_ENABLED)
                                                : LocaleUtil.tr(
                                                        player,
                                                        MenuConfig.SETTINGS_MENU
                                                                .BOOLEAN_SETTING
                                                                .NO_PERMISSION_DISABLED))
                                        : (result
                                                ? LocaleUtil.tr(
                                                        player,
                                                        MenuConfig.SETTINGS_MENU
                                                                .BOOLEAN_SETTING
                                                                .ENABLED_LORE)
                                                : LocaleUtil.tr(
                                                        player,
                                                        MenuConfig.SETTINGS_MENU
                                                                .BOOLEAN_SETTING
                                                                .DISABLED_LORE)));
                metadata.setName(text.replace("<name>", BooleanSettingsBuilder.this.title));
                Iterator iterator2 = values.iterator();
                while (iterator2.hasNext()) {
                    String text2;
                    if ((text2 = (String) iterator2.next()).contains("<description>")) {
                        for (String text3 : BooleanSettingsBuilder.this.description) {
                            metadata.addLoreLine("&7" + text3);
                        }
                    } else {
                        metadata.addLoreLine(
                                text2.replace("<name>", BooleanSettingsBuilder.this.title));
                    }
                }
                return metadata.toItemStack();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                if (BooleanSettingsBuilder.this.permission.isEmpty()
                        || player.hasPermission(BooleanSettingsBuilder.this.permission)) {
                    IProfile result;
                    boolean metadata =
                            (result =
                                            Phoenix.getInstance()
                                                    .getProfileHandler()
                                                    .getProfile(player.getUniqueId()))
                                    .isMetadata(
                                            BooleanSettingsBuilder.this.metaData,
                                            BooleanSettingsBuilder.this.defaultValue);
                    result.updateMetadata(BooleanSettingsBuilder.this.metaData, !metadata, false);
                    if (BooleanSettingsBuilder.this.afterAction != null) {
                        BooleanSettingsBuilder.this.afterAction.run();
                    }
                    Phoenix.getInstance()
                            .getMenuHandler()
                            .openMenu(
                                    Phoenix.getInstance().getMenuHandler().getByPlayer(player),
                                    player);
                    BukkitAPI.callEvent(
                            new ProfileSettingUpdateEvent(
                                    player.getUniqueId(), BooleanSettingsBuilder.this.metaData));
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

    public boolean isDefaultValue() {
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

    public List<String> getOverrideEnabledLore() {
        return this.overrideEnabledLore;
    }

    public List<String> getOverrideDisabledLore() {
        return this.overrideDisabledLore;
    }

    public List<String> getOverrideNoPermissionLore() {
        return this.overrideNoPermissionLore;
    }
}
