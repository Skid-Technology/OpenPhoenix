package xyz.refinedev.phoenix.setup;

import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class FeatureToggles {

    public static final List<FeatureToggle> featureToggles;

    public static Map<String, Boolean> getMap() {
        LinkedHashMap<String, Boolean> entries = new LinkedHashMap<>();
        for (FeatureToggle featureToggle : featureToggles) {
            entries.put(featureToggle.getText(), featureToggle.isEnabledBySupplierSupplier());
        }
        return entries;
    }

    private FeatureToggles() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }

    static {
        FeatureToggle[] featureToggles2 = new FeatureToggle[8];
        featureToggles2[0] =
                new FeatureToggle(
                        "basics",
                        "basics.enabled",
                        true,
                        () -> MenuConfig.SETUP_MENU.FEATURES.BASICS,
                        () -> SettingsConfig.BASICS.ENABLED,
                        result -> SettingsConfig.BASICS.ENABLED = result);
        featureToggles2[1] =
                new FeatureToggle(
                        "vanish",
                        "vanish.enabled",
                        true,
                        () -> MenuConfig.SETUP_MENU.FEATURES.VANISH,
                        () -> SettingsConfig.VANISH.ENABLED,
                        result -> SettingsConfig.VANISH.ENABLED = result);
        featureToggles2[2] =
                new FeatureToggle(
                        "modmode",
                        "modmode.enabled",
                        true,
                        () -> MenuConfig.SETUP_MENU.FEATURES.MODMODE,
                        () -> SettingsConfig.MODMODE.ENABLED,
                        result -> SettingsConfig.MODMODE.ENABLED = result);
        featureToggles2[3] =
                new FeatureToggle(
                        "freeze",
                        "freeze.enabled",
                        true,
                        () -> MenuConfig.SETUP_MENU.FEATURES.FREEZE,
                        () -> SettingsConfig.FREEZE.ENABLED,
                        result -> SettingsConfig.FREEZE.ENABLED = result);
        featureToggles2[4] =
                new FeatureToggle(
                        "reports",
                        "reports.enabled",
                        true,
                        () -> MenuConfig.SETUP_MENU.FEATURES.REPORTS,
                        () -> SettingsConfig.REPORTS.ENABLED,
                        result -> SettingsConfig.REPORTS.ENABLED = result);
        featureToggles2[5] =
                new FeatureToggle(
                        "panic",
                        "panic.enabled",
                        true,
                        () -> MenuConfig.SETUP_MENU.FEATURES.PANIC,
                        () -> SettingsConfig.PANIC.ENABLED,
                        result -> SettingsConfig.PANIC.ENABLED = result);
        featureToggles2[6] =
                new FeatureToggle(
                        "chat-control",
                        "chat-control.enabled",
                        true,
                        () -> MenuConfig.SETUP_MENU.FEATURES.CHAT_CONTROL,
                        () -> SettingsConfig.CHAT_CONTROL.ENABLED,
                        result -> SettingsConfig.CHAT_CONTROL.ENABLED = result);
        featureToggles2[7] =
                new FeatureToggle(
                        "ban-waves",
                        "ban-waves.enabled",
                        false,
                        () -> MenuConfig.SETUP_MENU.FEATURES.BAN_WAVES,
                        () -> SettingsConfig.BAN_WAVES.ENABLED,
                        result -> SettingsConfig.BAN_WAVES.ENABLED = result);
        featureToggles = Collections.unmodifiableList(Arrays.asList(featureToggles2));
    }
}
