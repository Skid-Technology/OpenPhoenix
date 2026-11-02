package xyz.refinedev.phoenix.config;

import xyz.refinedev.lib.config.ConfigHandler;
import xyz.refinedev.lib.config.StaticConfig;
import xyz.refinedev.lib.config.StaticConfig.Priority;
import xyz.refinedev.phoenix.internal.CachedSettings;
import xyz.refinedev.phoenix.nametag.line.ConditionalLine;
import xyz.refinedev.phoenix.nametag.line.LineCondition;
import xyz.refinedev.phoenix.nametag.line.LineRenderer;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class NameTagsConfig extends StaticConfig {

    @Ignore public static NameTagsConfig INSTANCE;

    @Ignore private static volatile CachedSettings cachedSettings;

    @Ignore private static volatile LineRenderer[] cachedLines;

    @Ignore private static volatile LineRenderer[] cachedTab;

    public NameTagsConfig(ConfigHandler configHandler) {
        super(new File("plugins/Phoenix/nametags.yml"), configHandler);
        INSTANCE = this;
    }

    public static CachedSettings displaySettings() {
        CachedSettings cachedSettings2 = cachedSettings;
        if (cachedSettings == null) {
            cachedSettings =
                    cachedSettings2 =
                            new CachedSettings(
                                    NameTagsConfig.DISPLAY.SEE_THROUGH,
                                    NameTagsConfig.DISPLAY.SHADOW,
                                    NameTagsConfig.DISPLAY.BACKGROUND,
                                    NameTagsConfig.DISPLAY.TEXT_OPACITY,
                                    (float) NameTagsConfig.DISPLAY.VIEW_DISTANCE,
                                    NameTagsConfig.DISPLAY.BILLBOARD,
                                    (float) NameTagsConfig.DISPLAY.LINE_SPACING,
                                    (float) NameTagsConfig.DISPLAY.BASE_OFFSET,
                                    NameTagsConfig.DISPLAY.FADE,
                                    NameTagsConfig.DISPLAY.FADE_IN_TICKS);
        }
        return cachedSettings2;
    }

    public static LineRenderer[] lines() {
        LineRenderer[] lineRenderers = cachedLines;
        if (cachedLines == null) {
            cachedLines =
                    lineRenderers =
                            NameTagsConfig.DISPLAY.LINES.values().toArray(new LineRenderer[0]);
        }
        return lineRenderers;
    }

    public static LineRenderer[] tabFormats() {
        LineRenderer[] lineRenderers = cachedTab;
        if (cachedTab == null) {
            cachedTab =
                    lineRenderers =
                            NameTagsConfig.DISPLAY.TAB.values().toArray(new LineRenderer[0]);
        }
        return lineRenderers;
    }

    public static void invalidate() {
        cachedSettings = null;
        cachedLines = null;
        cachedTab = null;
    }

    public void load() {
        super.load();
        invalidate();
    }

    @Priority(1)
    public static class DISPLAY {

        @Comment({
            "Hide your own tag from yourself. Only skips the floating lines; your",
            "vanilla name is still hidden and your tab entry still set.",
            "Turn on if you use LabyMod's 'show own nametag', which draws client",
            "side and would otherwise show alongside this tag."
        })
        public static boolean HIDE_SELF = false;

        @Comment({
            "The tab list entry. Takes the same condition and visible-to terms as",
            "'lines' below, but only one is used: the first that passes, in order.",
            "Keep the last unconditional. Scale and animation are not available."
        })
        public static Map<String, LineRenderer> TAB = tabDefaults();

        @Comment("Visible through blocks?")
        public static boolean SEE_THROUGH = false;

        public static boolean SHADOW = false;

        @Comment("'default', 'none', #RRGGBB or #AARRGGBB.")
        public static String BACKGROUND = "#40000000";

        @Comment("0 to 255.")
        public static int TEXT_OPACITY = 255;

        @Comment("Blocks.")
        public static double VIEW_DISTANCE = 32.0;

        @Comment("FIXED, VERTICAL, HORIZONTAL or CENTER.")
        public static String BILLBOARD = "CENTER";

        public static double LINE_SPACING = 0.25;

        @Comment("Height of the bottom line above the head.")
        public static double BASE_OFFSET = 0.3;

        @Comment("Fade a tag in when it appears.")
        public static boolean FADE = true;

        public static int FADE_IN_TICKS = 4;

        @Comment("How often the animation ticker runs, in ticks.")
        public static int ANIMATION_TICK = 2;

        @Comment({
            "How often every tag is re-evaluated, in ticks. 0 disables it.",
            "Events cover rank, vanish, death and world changes already; turn",
            "this on for placeholders that change on their own, like health."
        })
        public static int REFRESH_INTERVAL = 0;

        @Comment({
            "Log what the display backend sends, and every team packet from any",
            "plugin that would have shown a nametag. Console spam; for diagnosing",
            "a name that will not hide."
        })
        public static boolean DEBUG = false;

        @Comment({
            "Lines of the tag, top first. A line whose condition fails is skipped",
            "and the ones below it close the gap.",
            "",
            "  text        colour codes, MiniMessage, PlaceholderAPI, and",
            "              <prefix>, <color>, <name>, <suffix>.",
            "  scale       1.0 is normal.",
            "  condition   when the line shows. Checked against the player the tag",
            "              belongs to.",
            "  visible-to  who can see the line. Checked against the player looking.",
            "  animation   interval in ticks plus frames, replacing 'text'.",
            "",
            "Both condition and visible-to take the same terms, as does 'tab'",
            "above. Omit either to always pass.",
            "",
            "  permission:<node>   has the permission node",
            "  rank:<name>         highest rank is exactly <name>",
            "  rank>=:<name>       highest rank is <name> or more senior. An",
            "                      unknown rank name never passes",
            "  has-prefix          highest rank has a tab list prefix set",
            "  vanished            is vanished",
            "  modmode             is in mod mode",
            "  self                the viewer is the player the tag belongs to",
            "  same-team           viewer and player share a scoreboard team",
            "  papi:<expression>   see below",
            "",
            "papi: resolves placeholders on both sides, then compares with",
            "==, !=, <, >, <= or >=. Two numbers compare numerically, anything",
            "else as text where only == and != apply. With no operator it is a",
            "yes/no check: false, 0 and empty are no, everything else is yes.",
            "",
            "  papi:%player_health% < 5",
            "  papi:%player_world% == world_nether",
            "  papi:%essentials_afk%",
            "",
            "Join terms with && and ||, evaluated strictly left to right with no",
            "precedence, so a || b && c means (a || b) && c. Put ! in front of a",
            "term to negate it. Parentheses are not supported: split the line in",
            "two instead.",
            "",
            "  condition: 'vanished && !modmode'",
            "  visible-to: 'permission:core.staff || self'"
        })
        public static Map<String, LineRenderer> LINES = defaults();

        private static Map<String, LineRenderer> tabDefaults() {
            LinkedHashMap<String, LineRenderer> entries;
            (entries = new LinkedHashMap())
                    .put(
                            "vanished",
                            new LineRenderer(
                                    "vanished",
                                    "&7[V] <prefix><color><name><suffix>",
                                    1.0F,
                                    LineCondition.of("vanished"),
                                    LineCondition.of("permission:core.staff"),
                                    ConditionalLine.conditionalLine));
            entries.put(
                    "default",
                    new LineRenderer(
                            "default",
                            "<prefix><color><name><suffix>",
                            1.0F,
                            LineCondition.lineCondition,
                            LineCondition.lineCondition,
                            ConditionalLine.conditionalLine));
            return entries;
        }

        private static Map<String, LineRenderer> defaults() {
            LinkedHashMap<String, LineRenderer> entries;
            (entries = new LinkedHashMap())
                    .put(
                            "vanish",
                            new LineRenderer(
                                    "vanish",
                                    "&7&lVANISHED",
                                    0.8F,
                                    LineCondition.of("vanished"),
                                    LineCondition.of("permission:core.staff"),
                                    ConditionalLine.conditionalLine));
            entries.put(
                    "name",
                    new LineRenderer(
                            "name",
                            "<prefix><color><name><suffix>",
                            1.0F,
                            LineCondition.lineCondition,
                            LineCondition.lineCondition,
                            ConditionalLine.conditionalLine));
            return entries;
        }
    }
}
