package xyz.refinedev.phoenix.config;

import xyz.refinedev.lib.config.ConfigHandler;
import xyz.refinedev.lib.config.StaticConfig;
import xyz.refinedev.lib.config.StaticConfig.Priority;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.whitelist.WhitelistAccess;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class GlobalConfig extends StaticConfig {

    @Ignore public static GlobalConfig INSTANCE;

    public GlobalConfig(ConfigHandler configHandler) {
        super(new File("plugins/Phoenix/global.yml"), configHandler);
        INSTANCE = this;
        this.relocate("mongo.uri-mode", "database.mongo.uri-mode");
        this.relocate("mongo.mongo-uri", "database.mongo.mongo-uri");
        this.relocate("mongo.address", "database.mongo.address");
        this.relocate("mongo.port", "database.mongo.port");
        this.relocate("mongo.database", "database.mongo.database");
        this.relocate("mongo.authentication", "database.mongo.authentication");
        this.relocate("mongo.username", "database.mongo.username");
        this.relocate("mongo.password", "database.mongo.password");
    }

    public static void validateAltConfig(Logger logger) {
        if (GlobalConfig.SERVER.ALTS.SCORE.IP_WEIGHT
                < GlobalConfig.SERVER.ALTS.THRESHOLDS.ENFORCE) {
            logger.warning(
                    "ALTS.SCORE.IP_WEIGHT ("
                            + GlobalConfig.SERVER.ALTS.SCORE.IP_WEIGHT
                            + ") is below ALTS.THRESHOLDS.ENFORCE ("
                            + GlobalConfig.SERVER.ALTS.THRESHOLDS.ENFORCE
                            + "). Ban evaders rejoining on the same IP will NOT be kicked automatically.");
        }
        if (GlobalConfig.SERVER.ALTS.SIGNALS.NON_IP_CAP
                >= GlobalConfig.SERVER.ALTS.THRESHOLDS.ENFORCE) {
            logger.warning(
                    "ALTS.SIGNALS.NON_IP_CAP ("
                            + GlobalConfig.SERVER.ALTS.SIGNALS.NON_IP_CAP
                            + ") is at or above ALTS.THRESHOLDS.ENFORCE ("
                            + GlobalConfig.SERVER.ALTS.THRESHOLDS.ENFORCE
                            + "). Accounts can now be kicked for ban evasion without ever sharing an address.");
        }
        if (GlobalConfig.SERVER.ALTS.STRICT_IP.ENABLED
                && GlobalConfig.SERVER.ALTS.IP_SUBNET_CHECK) {
            logger.warning(
                    "ALTS.IP_SUBNET_CHECK is on together with ALTS.STRICT_IP.ENABLED. Strict matching needs an exact address, so subnet-only matches score nothing.");
        }
        if (GlobalConfig.SERVER.ALTS.STRICT_IP.ENABLED && strictIpWindowMillis() < 0L) {
            logger.warning(
                    "ALTS.STRICT_IP.WINDOW ('"
                            + GlobalConfig.SERVER.ALTS.STRICT_IP.WINDOW
                            + "') is not a valid duration. Falling back to 12h.");
        }
    }

    public static long strictIpWindowMillis() {
        String text = GlobalConfig.SERVER.ALTS.STRICT_IP.WINDOW;
        return GlobalConfig.SERVER.ALTS.STRICT_IP.WINDOW != null && !text.isEmpty()
                ? DateUtils.getDuration(text)
                : -1L;
    }

    @Priority(7)
    @Comment("The actual AntiVPN check only happens on pxVelocity/pxBungee")
    public static class ANTIVPN {

        @Comment("Available: IPRisk, ProxyCheck (Needs their API Key)")
        public static String PROVIDER = "IPRisk";

        @Comment("Only required when using ProxyCheck antivpn provider.")
        public static String PROXYCHECK_API_KEY = "";

        @Comment(
                "Should VPN Checks be carried out in the background without kicking someone for using a VPN?")
        public static boolean BACKGROUND_CHECK = true;
    }

    @Priority(0)
    public static class DATABASE {

        @Priority(0)
        @Comment("MySQL or MongoDB")
        public static String DATABASE_TYPE = "MongoDB";

        @Priority(1)
        @Comment("Mongo Database Details")
        public static class MONGO {

            public static boolean URI_MODE = false;

            public static String MONGO_URI = "";

            public static String ADDRESS = "127.0.0.1";

            public static int PORT = 27017;

            public static String DATABASE = "dbname";

            public static boolean AUTHENTICATION = false;

            public static String USERNAME = "username";

            public static String PASSWORD = "password";
        }

        @Priority(2)
        @Comment("MySQL Database Details")
        public static class MYSQL {

            public static String ADDRESS = "127.0.0.1";

            public static int PORT = 3306;

            public static String DATABASE = "dbname";

            public static String USERNAME = "username";

            public static String PASSWORD = "password";
        }
    }

    @Priority(5)
    public static class FILTER {

        public static boolean ENABLED = true;

        @Comment("Should links be filtered at all?")
        public static boolean LINKS_FILTER = true;

        @Comment("Should links be part of the hard filter?")
        public static boolean LINKS_HARD = false;

        @Comment("Send the filtered message to the sender only")
        public static boolean GHOST_MODE = false;

        @Comment("Send the sender a message that their message got filtered")
        public static boolean ALERT_FILTER = true;

        public static List<String> WHITELISTED_LINKS = Arrays.asList("yourserver.com");
    }

    @Priority(8)
    @Comment({
        "Metrics exporter for InfluxDB.",
        "Server statistics (punishments, reports, logins) are pushed to an InfluxDB",
        "instance so they can be visualized in Grafana. Disabled by default."
    })
    public static class METRICS {

        public static boolean ENABLED = false;

        @Comment("Full URL of the InfluxDB instance, eg. http://influx.yourserver.com:8086")
        public static String URL = "http://127.0.0.1:8086";

        @Comment("InfluxDB API token with write access to the configured bucket")
        public static String TOKEN = "";

        public static String ORG = "refinedev";

        public static String BUCKET = "phoenix";
    }

    @Priority(2)
    @Comment("Network settings")
    public static class NETWORK {

        public static String IP = "yourserver.com";

        public static String WEBSITE = "yourserver.com";

        public static String STORE = "store.yourserver.com";

        public static String DISCORD = "discord.yourserver.com";

        public static String TEAMSPEAK = "ts.yourserver.com";

        public static String TWITTER = "twitter.com/yourserver";

        @Comment("Accepts &a, &c, &b&o, and &#RRGGBB on 1.16+.")
        public static class COLORS {

            @Comment("Names and values -- the subject of the sentence.")
            public static String MAIN = "&c";

            @Comment("Bullets and marks.")
            public static String SECONDARY = "&4";

            @Comment("Body prose.")
            public static String MIDDLE = "&f";

            @Comment("The one value on a line to read first.")
            public static String BRIGHT = "&f";

            @Comment("Labels, counts, hints, timestamps.")
            public static String MUTED = "&7";

            @Comment("The lore rail, separators, brackets.")
            public static String RAIL = "&7";
        }
    }

    @Priority(1)
    @Comment("Redis Database Details")
    public static class REDIS {

        @Comment({
            "Turn off to run one server without installing redis. Phoenix then keeps this state in memory",
            "and in plugins/Phoenix/local-store.json. Proxies and networks of more than one server need redis on."
        })
        public static boolean ENABLED = true;

        public static String ADDRESS = "127.0.0.1";

        public static int PORT = 6379;

        public static String USERNAME = "default";

        public static String PASSWORD = "password";

        public static int DATABASE = 0;

        @Comment("Enable SSL/TLS for the Redis connection.")
        public static boolean SSL = false;

        @Comment("Verify the server's TLS certificate against the JVM truststore.")
        public static boolean VERIFY_PEER = true;

        @Comment("Use STARTTLS instead of negotiating TLS at connection time.")
        public static boolean START_TLS = false;

        @Comment({
            "How long (seconds) a cached profile stays in Redis before it is dropped.",
            "The cache is only ever an accelerator - a miss reads the database - so a short",
            "value costs reads and a long one costs memory. 0 disables expiry entirely."
        })
        public static int PROFILE_CACHE_SECONDS = 900;
    }

    @Priority(4)
    public static class SECURITY {

        public static boolean ENABLED = true;

        @Comment(
                "If a user gets a rank in this list and they aren't security verified they'll be punished")
        public static List<String> RANKS_TO_TRIGGER = Arrays.asList("Owner", "Developer");

        @Comment("Should a user who gets opped be punished if they aren't security verified")
        public static boolean OP_TRIGGER = true;

        @Comment("Should security users be automatically opped?")
        public static boolean AUTO_OP = true;

        @Comment(
                "Should the user that opped or gave someone a rank be punished if the person they gave it to isn't security verified")
        public static boolean PUNISH_SENDER = true;

        @Comment("What should the punishment reason be if a security punishment is triggered.")
        public static String PUNISHMENT_REASON = "[Security] Unverified User";
    }

    @Priority(3)
    public static class SERVER {

        @Comment("This should be the same name you put in your bungeecord config.yml")
        public static String NAME = "hub-1";

        public static String DISPLAYNAME = "Hub 1";

        public static Set<String> GROUPS =
                new HashSet<String>() {

                    {
                        this.add("Hubs");
                    }
                };

        public static String REGION = "EU";

        public static String DATE_FORMAT = "dd/MM/yyyy hh:mm:ss a";

        @Comment({
            "If a player with the rank Admin that has the child mod will inherit mod perms. If the mod rank has the child Trial-Mod",
            "and the passthrough is on admin will get both trial and mod perms."
        })
        public static boolean PASSTHROUGH_CHILDREN = false;

        @Comment("Should this servers player count and max player count towards the total?")
        public static boolean COUNT_PLAYERS = true;

        public static String CONSOLE_NAME = "&4&lConsole";

        @Comment("Should the '*' permission be enabled?")
        public static boolean STAR_PERM_ENABLED = true;

        @Comment("Only turn on if you are told by refine staff.")
        public static boolean INCREASE_SERVER_ONLINE_TIMEOUT = false;

        @Comment({
            "TimeZones for all the Dates in Phoenix.",
            "List of TimeZones: https://docs.oracle.com/cd/E84527_01/wcs/tag-ref/MISC/TimeZones.html"
        })
        public static String TIMEZONE = "Europe/Berlin";

        @Comment("Should the plugin use the modern prefix,color,displayname and suffix?")
        public static boolean USE_MODERN_RANK_PREFIX = false;

        @Comment(
                "If you turn this on you'll only be able to see/use/edit ranks that have the right scope.")
        public static boolean ONLY_AVAILABLE_RANKS_ON_SCOPE = false;

        public static class ALTS {

            @Comment("Should the alts system be enabled?")
            public static boolean ENABLED = true;

            @Comment({
                "Turn on for offline-mode (cracked) servers.",
                "Skins are picked from a menu there, so the skin signal is not used."
            })
            public static boolean CRACKED_SERVER = false;

            @Comment({
                "Player A has 2 alts (Player B, Player C).",
                "If this is enabled Player A will get the alts of Player B and Player C too.",
                "Every extra hop is worth less, see SCORE.RECURSIVE_DECAY."
            })
            public static boolean RECURSIVE_ALTS = false;

            @Comment({
                "Should players who share a subnet but never an exact address be scored?",
                "Worth few points on its own, but off is the safer default."
            })
            public static boolean IP_SUBNET_CHECK = false;

            @Comment({
                "Should IP Bans only go into effect for someone's alts.",
                "By default IP Punishments also check your current IP address."
            })
            public static boolean IP_PUNISHMENTS_ONLY_ALTS = false;

            @Comment({
                "Don't re-score a player's links if they were scored less than this ago.",
                "Set to 0 to re-score on every join."
            })
            public static String REFRESH_THROTTLE = "10m";

            @Comment({
                "Delete links below the display threshold that haven't been re-scored in this long.",
                "Runs on startup. Set to 0 to keep everything."
            })
            public static String PRUNE_STALE_LINKS_AFTER = "90d";

            public static class MIGRATION {

                @Comment(
                        "Legacy alts re-scored per profile load, so an old profile can't stall a login.")
                public static int MAX_LEGACY_RESCORE_PER_LOAD = 50;
            }

            @Comment({
                "Scoring weights. Only change these if you understand the maths",
                "described in the alt detection documentation."
            })
            public static class SCORE {

                @Comment("Accounts on one address still treated as a single household.")
                public static int HOUSEHOLD_SIZE = 4;

                @Comment("Points for a clean exact-address match. Must be >= THRESHOLDS.ENFORCE.")
                public static double IP_WEIGHT = 85.0;

                @Comment("Points per additional independent subnet two accounts share.")
                public static double MULTI_ADDRESS_WEIGHT = 25.0;

                @Comment({
                    "Multiplier per extra shared address in the same subnet.",
                    "Provider pools move players across many addresses, so they shouldn't stack."
                })
                public static double SUBNET_STACK_DECAY = 0.5;

                public static double SUBNET_WEIGHT = 15.0;

                public static double VPN_TRUST_MULTIPLIER = 0.05;

                @Comment("Ignored when no AntiVPN provider is configured.")
                public static double MOBILE_ASN_MULTIPLIER = 0.5;

                public static double FORCED_SHARED_TRUST = 0.02;

                public static String CLOSE_WINDOW = "7d";

                public static String REASSIGN_WINDOW = "90d";

                public static double MIN_OVERLAP_FACTOR = 0.15;

                public static int FULL_SESSION_COUNT = 5;

                @Comment("Score multiplier applied per transitive hop when RECURSIVE_ALTS is on.")
                public static double RECURSIVE_DECAY = 0.6;
            }

            public static class SHARED_ADDRESS {

                @Comment({
                    "Accounts on one address before it's labelled SHARED to staff.",
                    "Just a label, scoring already accounts for how busy an address is."
                })
                public static int AUTO_CLASSIFY_AT = 25;

                public static boolean BACKFILL_ON_STARTUP = true;

                public static int BACKFILL_CHUNK_SIZE = 5000;

                @Comment({
                    "Skip addresses with at least this many accounts when looking for candidates.",
                    "They score near zero anyway, and skipping them keeps joins fast behind",
                    "large gateways. 0 disables."
                })
                public static int DISCOVERY_DENSITY_CUTOFF = 500;

                @Comment("Hard cap on candidates evaluated per link refresh.")
                public static int MAX_CANDIDATES_PER_REFRESH = 500;
            }

            @Comment({
                "Weights for evidence that has nothing to do with IP addresses.",
                "Capped by NON_IP_CAP, so these can never auto-kick on their own."
            })
            public static class SIGNALS {

                @Comment("Combined ceiling for all non-IP signals. Keep below THRESHOLDS.ENFORCE.")
                public static double NON_IP_CAP = 60.0;

                @Comment(
                        "Points when two accounts repeatedly log in right after each other logs out.")
                public static double ALTERNATION_WEIGHT = 35.0;

                public static int FULL_HANDOFFS = 5;

                @Comment(
                        "Share of the shallower account's sessions that must be handoffs for full points.")
                public static double FULL_HANDOFF_RATIO = 0.25;

                @Comment(
                        "Points for a brand-new account appearing right after the other account was banned.")
                public static double BAN_APPEAR_WEIGHT = 20.0;

                public static String BAN_APPEAR_WINDOW = "7d";

                public static String HANDOFF_WINDOW = "5m";

                @Comment({
                    "Points removed when both accounts are online at once, which usually means two people.",
                    "Not applied while STRICT_IP is on and the two share an address."
                })
                public static double CONCURRENCY_PENALTY = 20.0;

                public static int FULL_CONCURRENT = 5;

                @Comment(
                        "Points for an identical client fingerprint (settings, locale, view distance, brand).")
                public static double FINGERPRINT_WEIGHT = 25.0;

                @Comment("Accounts sharing a fingerprint before it stops being evidence.")
                public static int FINGERPRINT_RARE_AT = 4;

                public static double NAME_WEIGHT = 20.0;

                @Comment("Name similarity below this contributes nothing.")
                public static double NAME_FLOOR = 0.6;

                @Comment("Everyone in a region plays at similar times, so this is worth little.")
                public static double RHYTHM_WEIGHT = 10.0;

                public static double RHYTHM_FLOOR = 0.85;

                public static double SKIN_WEIGHT = 20.0;

                public static int SKIN_RARE_AT = 3;

                public static double LATENCY_WEIGHT = 8.0;

                public static long LATENCY_TOLERANCE_MS = 15L;

                @Comment(
                        "Points for logging out in the same spot. Busy spots like spawn count for less.")
                public static double LOCATION_WEIGHT = 25.0;

                public static int LOCATION_RARE_AT = 3;

                public static int FULL_SHARED_LOCATIONS = 2;
            }

            @Comment({
                "For servers where most of the player base shares one IP.",
                "Normally a shared address always counts for something, no matter how long",
                "ago the two accounts used it. This drops it entirely outside the window."
            })
            public static class STRICT_IP {

                @Comment(
                        "Off scores addresses as usual, by how busy they are and how far apart they were used.")
                public static boolean ENABLED = false;

                @Comment({
                    "How far apart the two accounts may have used an address for it to still count.",
                    "Anything wider is ignored. Use 0s to require them to be online at the same time."
                })
                public static String WINDOW = "12h";

                @Comment({
                    "Also require the address to be the current one for both accounts.",
                    "An account with no known address gets no address evidence at all."
                })
                public static boolean REQUIRE_CURRENT_ADDRESS = false;
            }

            public static class THRESHOLDS {

                @Comment("Minimum score to appear in /alts.")
                public static int DISPLAY = 25;

                @Comment("Minimum score to be written into the profile's alt list.")
                public static int LINK = 50;

                @Comment("Minimum score to notify staff of possible ban evasion.")
                public static int ALERT = 50;

                @Comment("Minimum score to automatically kick for ban evasion.")
                public static int ENFORCE = 75;
            }
        }

        public static class CHATLOGS {

            @Comment("Number of days to keep chat logs in the database")
            public static int DAYS_KEEP = 30;

            @Comment("Should we delete older chatlogs than the number of days above on startup?")
            public static boolean DELETE_ON_STARTUP = true;
        }
    }

    @Priority(6)
    @Comment("Whitelist Settings")
    public static class WHITELIST {

        @Comment({
            "If enabled the whitelist will be using names instead of uuid's.",
            "Please clear your whitelist list before changing this option!",
            "This will be same as UUID but instead of UUID it will be getting lowerCaseName"
        })
        public static boolean NAME_BASED = false;

        public static boolean ENABLED = false;

        public static String ACCESS_RANK = "Default";

        @Comment("DO NOT EDIT!")
        public static Set<WhitelistAccess> ACCESS_TYPES =
                new HashSet<>(Arrays.asList(WhitelistAccess.UUID, WhitelistAccess.RANK));

        public static List<String> PLAYERS = Collections.emptyList();

        public static List<String> TOKEN_PLAYERS = Collections.emptyList();
    }
}
