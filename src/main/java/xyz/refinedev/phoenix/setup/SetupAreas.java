package xyz.refinedev.phoenix.setup;

import xyz.refinedev.phoenix.config.MenuConfig;

import java.util.Collections;
import java.util.List;

public final class SetupAreas {

    public static List<String> emptyList(String text) {
        if ("Storage".equals(text)) {
            return MenuConfig.SETUP_MENU.ABOUT.STORAGE;
        } else if ("Identity".equals(text)) {
            return MenuConfig.SETUP_MENU.ABOUT.IDENTITY;
        } else if ("Ranks".equals(text)) {
            return MenuConfig.SETUP_MENU.ABOUT.RANKS;
        } else if ("Colours".equals(text)) {
            return MenuConfig.SETUP_MENU.ABOUT.COLOURS;
        } else if ("Bypasses".equals(text)) {
            return MenuConfig.SETUP_MENU.ABOUT.BYPASSES;
        } else if ("Features".equals(text)) {
            return MenuConfig.SETUP_MENU.ABOUT.FEATURES;
        } else if ("Config master".equals(text)) {
            return MenuConfig.SETUP_MENU.ABOUT.CONFIG_MASTER;
        } else if ("Reports".equals(text)) {
            return MenuConfig.SETUP_MENU.ABOUT.REPORTS;
        } else if ("Punishment ladders".equals(text)) {
            return MenuConfig.SETUP_MENU.ABOUT.LADDERS;
        } else if ("Filters".equals(text)) {
            return MenuConfig.SETUP_MENU.ABOUT.FILTERS;
        } else if ("Staff tools".equals(text)) {
            return MenuConfig.SETUP_MENU.ABOUT.STAFF_TOOLS;
        } else if ("Two-factor".equals(text)) {
            return MenuConfig.SETUP_MENU.ABOUT.TWO_FACTOR;
        } else if ("Alts".equals(text)) {
            return MenuConfig.SETUP_MENU.ABOUT.ALTS;
        } else if ("Anti-VPN".equals(text)) {
            return MenuConfig.SETUP_MENU.ABOUT.ANTI_VPN;
        } else if ("Tags".equals(text)) {
            return MenuConfig.SETUP_MENU.ABOUT.TAGS;
        } else if ("Tab list".equals(text)) {
            return MenuConfig.SETUP_MENU.ABOUT.TABLIST;
        } else if ("Webhooks".equals(text)) {
            return MenuConfig.SETUP_MENU.ABOUT.WEBHOOKS;
        } else if ("Locales".equals(text)) {
            return MenuConfig.SETUP_MENU.ABOUT.LOCALES;
        } else if ("Metrics".equals(text)) {
            return MenuConfig.SETUP_MENU.ABOUT.METRICS;
        } else {
            return "Modern clients".equals(text)
                    ? MenuConfig.SETUP_MENU.ABOUT.MODERN
                    : Collections.emptyList();
        }
    }

    private SetupAreas() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}
