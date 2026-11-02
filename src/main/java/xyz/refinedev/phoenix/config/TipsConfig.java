package xyz.refinedev.phoenix.config;

import xyz.refinedev.lib.config.ConfigHandler;
import xyz.refinedev.lib.config.StaticConfig;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class TipsConfig extends StaticConfig {

    @Ignore public static TipsConfig INSTANCE;

    @Comment({
        "The tips cycled to players who have tips on, in order.",
        "Each key is one tip and each entry under it is one line, sent as its",
        "own message so {centered} centres that line rather than the whole tip."
    })
    public static Map<String, Tip> TIPS = defaults();

    public TipsConfig(ConfigHandler configHandler) {
        super(new File("plugins/Phoenix/tips.yml"), configHandler);
        INSTANCE = this;
    }

    private static Map<String, Tip> defaults() {
        LinkedHashMap<String, Tip> entries;
        (entries = new LinkedHashMap())
                .put(
                        "discord",
                        Tip.of(
                                "{centered}<main>&lTip",
                                "{centered}<middle>Join our Discord at <main><discord>"));
        entries.put(
                "store",
                Tip.of(
                        "{centered}<main>&lTip",
                        "{centered}<middle>Visit the store at <main><store>"));
        return entries;
    }
}
