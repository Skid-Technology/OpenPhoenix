package xyz.refinedev.phoenix.config;

import xyz.refinedev.lib.config.ConfigHandler;
import xyz.refinedev.lib.config.StaticConfig;
import xyz.refinedev.phoenix.utils.ModModeItem;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class ModModeConfig extends StaticConfig {

    @Ignore public static ModModeConfig INSTANCE;

    @Comment({
        "The items a staff member gets in mod mode. Add your own by adding a key.",
        "Commands leave off the leading slash.",
        "Actions: random-teleport, compass, view-inventory, online-staff, enable-vanish, disable-vanish, freeze"
    })
    public static Map<String, ModModeItem> ITEMS = defaults();

    public ModModeConfig(ConfigHandler configHandler) {
        super(new File("plugins/Phoenix/modmode.yml"), configHandler);
        INSTANCE = this;
    }

    private static Map<String, ModModeItem> defaults() {
        LinkedHashMap<String, ModModeItem> entries;
        (entries = new LinkedHashMap())
                .put(
                        "compass",
                        new ModModeItem(
                                "&bPush Forward",
                                "COMPASS",
                                0,
                                1,
                                ModModeItem.action("compass"),
                                ModModeItem.noCommand(),
                                Arrays.asList("", "&7Right click to push", "&7yourself forward.")));
        entries.put(
                "inspect",
                new ModModeItem(
                        "&bView Player",
                        "BOOK",
                        0,
                        2,
                        ModModeItem.action("view-inventory"),
                        ModModeItem.noCommand(),
                        Arrays.asList(
                                "",
                                "&7Right click a player to",
                                "&7view their inventory and armor.")));
        entries.put(
                "online-staff",
                new ModModeItem(
                        "&bStaff Online",
                        "SKULL_ITEM",
                        3,
                        8,
                        ModModeItem.action("online-staff"),
                        ModModeItem.noCommand(),
                        Arrays.asList("", "&7Right click to see", "&7online staff.")));
        entries.put(
                "vanish-on",
                new ModModeItem(
                        "&aGo Visible",
                        "INK_SACK",
                        8,
                        9,
                        ModModeItem.action("disable-vanish"),
                        ModModeItem.noCommand(),
                        Arrays.asList("", "&7Right click to unvanish")));
        entries.put(
                "vanish-off",
                new ModModeItem(
                        "&aGo Invisible",
                        "INK_SACK",
                        10,
                        9,
                        ModModeItem.action("enable-vanish"),
                        ModModeItem.noCommand(),
                        Arrays.asList("", "&7Right click to vanish")));
        return entries;
    }
}
