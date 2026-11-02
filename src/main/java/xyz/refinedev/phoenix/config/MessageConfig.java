package xyz.refinedev.phoenix.config;

import xyz.refinedev.lib.config.ConfigHandler;
import xyz.refinedev.lib.config.StaticConfig;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MessageConfig extends StaticConfig {

    @Ignore public static MessageConfig INSTANCE;

    public MessageConfig(ConfigHandler configHandler) {
        super(new File("plugins/Phoenix/messages.yml"), configHandler);
        INSTANCE = this;
    }

    public static class ACTIONBARS {

        public static String CURRENTLY = "&a\u2713 <middle>You are currently <types>";

        public static String TYPE_COLOR = "&e";

        public static String SEPARATOR_COLOR = "&a";

        public static class TYPES {

            public static String VANISHED = "Vanished";

            public static String MODMODE = "In ModMode";

            public static String DISGUISED = "Disguised";
        }
    }

    public static class ALTS {

        public static String NONE = "<main>None<muted>.";

        public static String TRUNCATED =
                "<main>\u2717 <middle>Only showing you the first <amount> alts. For all of them use /altsmenu <player>.";

        public static List<String> FORMAT =
                Arrays.asList(
                        "<muted>[&aOnline<muted>, Offline, <bright>&oMuted<bright><muted> (<bright>&oOnline<bright><muted>/&oOffline<muted>), <main>Banned<muted>, <secondary>Blacklisted<muted>]",
                        "<name><middle>'s Alts <muted>(<amount>):",
                        "<alts>");

        public static List<String> HOVER_FORMAT =
                Arrays.asList(
                        "<middle>Name: <main><name>",
                        "<middle>Last Seen: <main><lastSeen>",
                        "<middle>Last Location: <main><country> <muted>(<isp>)",
                        "",
                        "<score>",
                        "<matching>",
                        "<punishments>",
                        "<subnetMatch>",
                        "",
                        "<name><middle>'s Current IP Info:",
                        " <secondary>\u25cf <middle>Logins: <main><logins>",
                        " <secondary>\u25cf <middle>First Login: <main><firstLogin>",
                        " <secondary>\u25cf <middle>Last Login: <main><lastLogin>",
                        "<matchingIPInfo>");

        public static List<String> MATCHING_IP_INFO =
                Arrays.asList(
                        "",
                        "<name><middle>'s Matching IP Info <main>(<amount>)<middle>:",
                        " <secondary>\u25cf <middle>Logins: <main><logins>",
                        " <secondary>\u25cf <middle>First Login: <main><firstLogin>",
                        " <secondary>\u25cf <middle>Last Login: <main><lastLogin>");

        public static List<String> SUBNET_ONLY_MATCH =
                Arrays.asList(
                        "",
                        "<main>The IPs of this player do not match,",
                        "<main>however the subnet does match!");

        public static String IP_MATCHING = "&a\u2713 <middle>Currently matching <main><player>";

        public static String IP_NOT_MATCHING =
                "<main>\u2717 <middle>Currently not matching <main><player>";

        public static String PUNISHMENT_MATCHING =
                "&a\u2713 <middle><type> currently matching <main><player>";

        public static String PUNISHMENT_NOT_MATCHING =
                "<main>\u2717 <middle><type> currently not matching <main><player>";

        @Comment("Header above each confidence group in /alts. <tier> and <amount> placeholders.")
        public static String GROUP_HEADER = "<tier> <muted>(<amount>):";

        @Comment({
            "Rendered where <score> appears. Placeholders: <score>, <tier>,",
            "<sharedAddresses>, <independentSubnets>, <lastSharedLogin>, <firstLinked>, <lastEvaluated>.",
            "Lines with <lastSharedLogin> are dropped when the pair never shared an address."
        })
        public static List<String> SCORE_FORMAT =
                Arrays.asList(
                        "<middle>Confidence: <main><score>% <muted>(<tier>)",
                        "<middle>Shared: <main><sharedAddresses> <muted>address(es) across <main><independentSubnets> <muted>subnet(s)",
                        "<middle>Last shared login: <main><lastSharedLogin>",
                        "<middle>First linked: <main><firstLinked> <muted>- last checked <main><lastEvaluated>");

        public static String NO_SCORE = "<middle>Confidence: <muted>not scored yet";

        @Comment("Appended to the name where <sibling> appears when the link is trusted.")
        public static String TRUSTED_TAG = " <muted>(Trusted)";

        @Comment("Extra line under the score when the link is trusted.")
        public static String TRUSTED_LINE = "<muted>Pinned as trusted - never enforces.";

        public static String DISABLED = "<main>\u2717 <middle>Alts are disabled on this server.";

        @Comment({
            "Output of /alts address. <player>, <accounts>, <classification>, <firstSeen>,",
            "<lastSeen>, <isp> and <sharedAt> placeholders where they make sense."
        })
        public static class ADDRESS {

            public static String NO_ADDRESS = "<main>\u2717 <middle><player> has no known address.";

            public static String HEADER =
                    "<muted>&m--------------------------------------------------";

            public static String TITLE = "<bright>Address stats for <player>'s current address:";

            public static String ACCOUNTS = " &6\u25cf <bright>Accounts seen: &a<accounts>";

            public static String CLASSIFICATION =
                    " &6\u25cf <bright>Classification: &a<classification>";

            public static String FIRST_SEEN = " &6\u25cf <bright>First seen: &a<firstSeen>";

            public static String LAST_SEEN = " &6\u25cf <bright>Last seen: &a<lastSeen>";

            public static String VPN_FLAG = " &6\u25cf <main>Known VPN exit";

            public static String MOBILE_FLAG = " &6\u25cf Mobile carrier network";

            public static String ISP = " &6\u25cf <bright>ISP: &a<isp>";

            public static String FOOTNOTE =
                    "<muted>The classification is worked out from how many accounts share this address.";

            public static String FORCED_SHARED =
                    "&a\u2713 <middle>Pinned as shared - accounts on it will barely count as alt evidence.";

            public static String FORCED_TRUSTED =
                    "&a\u2713 <middle>Pinned as trusted - full evidence weight regardless of density.";

            public static String CLEARED =
                    "&a\u2713 <middle>Override cleared - the classification recomputes from density again.";
        }

        @Comment("Shown to the punisher after banning someone with likely alt accounts.")
        public static class BAN_NOTICE {

            public static String HEADER =
                    "&6<player> has <amount> likely alt(s) - consider checking them:";

            public static String ENTRY =
                    " &6\u25cf <player> <muted>- <bright><score>% <muted>(<tier>)";
        }

        @Comment({
            "Output of /alts debug. <first>, <second>, <score>, <tier>, <display>, <link>,",
            "<alert> and <enforce> placeholders where they make sense."
        })
        public static class DEBUG {

            public static String HEADER =
                    "<muted>&m--------------------------------------------------";

            public static String TITLE = "<bright>Alt score: <first> <muted>vs <second>";

            public static String TOTAL = "<bright>Total: &a<score>% <muted>(<tier>)";

            public static String SAME_ACCOUNT =
                    "<main>\u2717 <middle>That is the same account twice.";

            public static String NO_EVIDENCE =
                    "<muted>No evidence of any kind connects these accounts.";

            @Comment("One line per signal. <signal>, <points> and <detail> placeholders.")
            public static String SIGNAL =
                    " &6\u25cf <bright><signal>: &a<points> <muted>(<detail>)";

            public static String THRESHOLDS =
                    "<bright>Thresholds: <muted>display <bright><display> <muted>| link <bright><link> <muted>| alert <bright><alert> <muted>| enforce <bright><enforce>";

            public static String VERDICT_ENFORCE =
                    "<main>\u2717 <middle>Above enforce - ban evasion through this pair is punished automatically.";

            public static String VERDICT_ALERT =
                    "&6Alerts staff when one side evades a punishment, but never kicks automatically.";

            public static String VERDICT_DISPLAY = "<muted>Shown in /alts, nothing more.";

            public static String VERDICT_NONE =
                    "<muted>Below every threshold - these accounts are not linked.";
        }

        public static class STATUS {

            public static class BANNED {

                public static String NAME = "<main>Banned";

                public static String COLOR = "<main>";
            }

            public static class BLACKLIST {

                public static String NAME = "<secondary>Blacklist";

                public static String COLOR = "<secondary>";
            }

            public static class MUTED_OFFLINE {

                public static String NAME = "<muted>&oMuted";

                public static String COLOR = "<muted>&o";
            }

            public static class MUTED_ONLINE {

                public static String NAME = "<bright>&oMuted";

                public static String COLOR = "<bright>&o";
            }

            public static class OFFLINE {

                public static String NAME = "<muted>Offline";

                public static String COLOR = "<muted>";
            }

            public static class ONLINE {

                public static String NAME = "&aOnline";

                public static String COLOR = "&a";
            }
        }

        @Comment({
            "Output of /alts suppressed. <player> on the empty line,",
            "<name>, <score> and <tier> on each entry."
        })
        public static class SUPPRESSED_LIST {

            public static String NONE = "<middle>Nothing is suppressed on <main><player><middle>.";

            public static String ENTRY =
                    " &6\u25cf <name> <muted>- <bright><score>% <muted>(<tier><muted>)";
        }

        @Comment({
            "Confidence tiers shown next to the score.",
            "CONFIRMED is 90+, LIKELY 75-89, POSSIBLE 50-74, WEAK below 50."
        })
        public static class TIER {

            public static String CONFIRMED = "<main>\u2717 <middle>Confirmed";

            public static String LIKELY = "<main>Likely";

            public static String POSSIBLE = "&e\u2691 <middle>Possible";

            public static String WEAK = "<muted>Weak";

            public static String TRUSTED_GROUP = "&aTrusted";

            public static String UNSCORED = "<muted>Unscored";
        }

        @Comment("Output of /alts trust, untrust and suppress. <first> and <second> placeholders.")
        public static class TRUST {

            public static String TRUSTED =
                    "&a\u2713 <middle>The link between <first> and <second> is now trusted - it will never enforce, whatever it scores.";

            public static String UNTRUSTED =
                    "&a\u2713 <middle>The link between <first> and <second> is active again.";

            public static String SUPPRESSED =
                    "&a\u2713 <middle>The link between <first> and <second> is now suppressed - it is hidden and will never enforce. Use untrust to bring it back.";
        }
    }

    public static class ANALYTICS {

        public static String LOADING = "<muted>&oLoading Analytics, please wait...";

        public static List<String> MESSAGE =
                Arrays.asList(
                        "<main>&lServer Analytics <main><muted>(Global)",
                        " <secondary>\u25cf <middle>Online: <main><online>",
                        "",
                        "<main>&lTotal Player Peak",
                        " <secondary>\u25cf <middle>Today: <main><peak>",
                        " <secondary>\u25cf <middle>Yesterday: <main><peakYesterday> <muted>(<peakYesterdayDiff>)",
                        " <secondary>\u25cf <middle>Last Week: <main><peakWeek> <muted>(<peakWeekDiff>)",
                        "",
                        "<main>&lTotal Average Playtime",
                        " <secondary>\u25cf <middle>Today: <main><playtime>",
                        " <secondary>\u25cf <middle>Yesterday: <main><playtimeYesterday> <muted>(<playtimeYesterdayDiff>)",
                        " <secondary>\u25cf <middle>Last Week: <main><playtimeWeek> <muted>(<playtimeWeekDiff>)",
                        "",
                        "<main>&lTotal Logins",
                        " <secondary>\u25cf <middle>Today: <main><logins>",
                        " <secondary>\u25cf <middle>Yesterday: <main><loginsYesterday> <muted>(<loginsYesterdayDiff>)",
                        " <secondary>\u25cf <middle>Last Week: <main><loginsWeek>",
                        " <secondary>\u25cf <middle>Last Month: <main><loginsMonth>",
                        "",
                        "<main>&lTotal Server Switches",
                        " <secondary>\u25cf <middle>Today: <main><switches>",
                        " <secondary>\u25cf <middle>Yesterday: <main><switchesYesterday>",
                        "",
                        "<main>&lRetention <main><muted>(new = first seen in 30 days)",
                        " <secondary>\u25cf <middle>Unique Today: <main><uniquePlayers> <muted>(<newPlayers> new, <returningPlayers> returning)",
                        " <secondary>\u25cf <middle>Unique Yesterday: <main><uniquePlayersYesterday> <muted>(<newPlayersYesterday> new)",
                        " <secondary>\u25cf <middle>D1 Retention: <main><d1Retention>",
                        " <secondary>\u25cf <middle>D7 Retention: <main><d7Retention>");
    }

    public static class ANTISPAM {

        public static String YOU_CANT_SEND_TWICE =
                "<main>\u2717 <middle>You can't send the same message twice.";
    }

    public static class AUTH {

        public static String SETUP_FAILED =
                "<main>\u2717 <middle>There was an error while trying to setup your 2FA.";

        public static String NEED_TO_SETUP_AUTH_ALERT = "<main>You need to setup 2FA.";

        public static String NEED_TO_AUTH_ALERT = "<main>You need to authenticate yourself.";

        public static String AUTH_METHOD_NOT_ENABLED =
                "<main>\u2717 <middle>This authentication method is not enabled.";

        public static String NO_NEED_TO = "<main>\u2717 <middle>You don't have to auth right now.";

        public static String ALREADY_ENABLED = "<main>\u2717 <middle>You have already setup 2FA";

        public static String NOT_ENABLED = "<main>\u2717 <middle>You haven't setup 2FA.";

        public static String INVALID_CODE =
                "<main>\u2717 <middle>That code is invalid. You have <bright><attempts><middle> attempts remaining.";

        public static String SUCCESSFUL = "&a\u2713 <middle>You have been authenticated.";

        public static String YUBIKEY_SPECIFIC =
                "<middle>Please do <main>/auth <middle>and the yubikey code.";

        public static String RESET = "<middle>You have reset <main><player><middle>'s 2FA.";

        public static String FORCE = "<middle>You have forced <main><player> <middle>to setup 2FA.";

        public static String QRCODE_URL = "&a\u2713 <middle>In case the map is empty click here.";

        public static String ALREADY_AUTHED =
                "&a\u2713 <middle>You've automatically been authenticated since you logged in with the same address.";

        public static String CODE_REQUIRED =
                "<main>\u2717 <middle>A code is required with the <type> authentication type.";

        public static String CANNOT_CHAT =
                "<main>\u2717 <middle>You can't chat before completing your 2FA.";

        public static String CANNOT_COMMAND =
                "<main>\u2717 <middle>You can't execute this command before completing your 2FA.";
    }

    @Comment("Ban waves: punishments staged now and released together later.")
    public static class BAN_WAVE {

        public static String DISABLED = "<main>\u2717 <middle>Ban waves are turned off.";

        public static String DOESNT_EXIST =
                "<main>\u2717 <middle>There is no wave called <bright><wave><middle>.";

        public static String ALREADY_EXISTS =
                "<main>\u2717 <middle>A wave called <bright><wave><middle> already exists.";

        public static String CREATED = "&a\u2713 <middle>Started the wave <bright><wave><middle>.";

        public static String STAGED =
                "&a\u2713 <middle>Staged <player> in <bright><wave><middle>, now <bright><amount><middle> account(s).";

        public static String UNSTAGED =
                "&a\u2713 <middle>Took <player> back out of <bright><wave><middle>.";

        public static String ALREADY_STAGED =
                "<main>\u2717 <middle><player> is already staged in this wave.";

        public static String NOT_STAGED =
                "<main>\u2717 <middle><player> is not staged in this wave.";

        public static String NO_PERMISSION =
                "<main>\u2717 <middle>You can not issue a <type>, so you can not stage one.";

        public static String EMPTY =
                "<main>\u2717 <middle>The wave <bright><wave><middle> has nothing staged in it.";

        public static String NONE = "<main>\u2717 <middle>There are no waves waiting.";

        public static String CANCELLED =
                "<main>\u2717 <middle>Threw away the wave <bright><wave><middle>.";

        public static String MANUAL = "<muted>on release";

        public static String PERMANENT = "Permanent";

        @Comment("Marks an address punishment in the staged list.")
        public static String IP_PREFIX = "IP ";

        @Comment("Shown when a wave that never went out is asked to be taken back.")
        public static String NOT_RELEASED =
                "<main>\u2717 <middle><wave> has not gone out yet. Cancel it instead.";

        public static String NOTHING_TO_VOID =
                "<main>\u2717 <middle><wave> did not issue any punishments.";

        public static String CONFIRM_VOID =
                "&e\u2691 <middle>Void all <main><amount><middle> punishments from <main><wave><middle>?";

        public static String VOIDED =
                "&a\u2713 <middle>Voided &e<amount><middle> punishments from &e<wave><middle> as &e<void_reason><middle>.";

        public static String NO_ADDRESS =
                "<main>\u2717 <middle><player> has no known address to ban.";

        @Comment("Scheduling. <when> is the date and time, <remaining> how long until then.")
        public static String SCHEDULED =
                "&a\u2713 <middle>The wave <bright><wave><middle> goes out at <bright><when> <muted>(<remaining>)<middle>.";

        public static String UNSCHEDULED =
                "&a\u2713 <middle>The wave <bright><wave><middle> will now wait to be released by hand.";

        public static String BAD_TIME =
                "<main>\u2717 <middle>Use a date and time like <bright>2026-08-30 18:00<middle>, or a duration like <bright>12h<middle>.";

        public static String TIME_PASSED = "<main>\u2717 <middle>That moment has already passed.";

        public static String CONFIRM =
                "<main>\u2717 <middle>Release <wave> now? <amount> account(s) will be punished.";

        public static List<String> LIST_HEADER =
                Arrays.asList(
                        "<muted>&m----------------------------------------",
                        "<main>&lBan waves <main><muted>- waiting");

        public static String LIST_ENTRY =
                " <secondary>\u25cf <main><wave> <muted>- <middle><amount> account(s), <main><when>";

        public static String VIEW_HEADER =
                "<main>&l<wave> <main><muted>- <middle><amount> account(s), <main><when>";

        public static String VIEW_ENTRY =
                " <secondary>\u25cf <main><player> <muted>- <middle><type> <main><time> <7>- <middle><reason>";

        @Comment("Said once on every server when a wave goes out.")
        public static String BROADCAST =
                "<main>&lBAN WAVE<main> <rail>\u00bb <middle><amount> <muted>accounts have been removed.";
    }

    public static class BASIC {

        public static class ADMIN {

            public static String BROADCAST =
                    "<main>&lBROADCAST<main> <rail>\u00bb <middle><message>";

            public static String STAFF_ALERT =
                    "<main>&lSTAFF ALERT<main> <rail>\u00bb <middle><message>";

            public static String STAFF_BROADCAST = "&b&lSTAFF&b <rail>\u00bb <middle><message>";

            public static class ADMIN_ALERTS {

                public static String TELEPORTED_TO = "<middle>teleported to <target>";

                public static String TELEPORT_HERE = "<middle>teleported <target> to himself";

                public static String TELEPORTED_EVERYONE_TO_HIMSELF =
                        "<middle>teleported everyone to himself";

                public static String HEALED_SOMEONE = "<middle>healed <target>";

                public static String SELF_HEAL = "<middle>healed himself";

                public static String FED_SOMEONE = "<middle>fed <target>";

                public static String SELF_FED = "<middle>fed himself";

                public static String FLIGHT_OTHER_ON = "<middle>enabled flight mode for <target>";

                public static String SELF_FLIGHT_ON = "<middle>enabled flight mode";

                public static String FLIGHT_OTHER_OFF = "<middle>disabled flight mode for <target>";

                public static String SELF_FLIGHT_OFF = "<middle>disabled flight mode";

                public static String GOD_MODE_ON = "<middle>enabled god";

                public static String GOD_MODE_OFF = "<middle>disabled god";

                public static String KILL_OTHER = "<bright>killed <target>.";
            }

            public static class BUILD {

                public static String DISABLED_IN_CONFIG =
                        "<main>\u2717 <middle>Build mode is disabled in the config.";

                public static String ENABLED = "&a\u2713 <middle>You have enabled build mode.";

                public static String DISABLED =
                        "<main>\u2717 <middle>You have disabled build mode.";

                public static String ENABLED_OTHER =
                        "&a\u2713 <middle>You have enabled build mode for <player>.";

                public static String DISABLED_OTHER =
                        "<main>\u2717 <middle>You have disabled build mode for <player>.";
            }

            public static class CLEAR {

                public static String SELF =
                        "<middle>You have cleared your inventory. <muted>(<total> items in total)";

                public static String OTHER =
                        "<middle>You have cleared <main><player><middle>'s inventory. <muted>(<total> items in total)";

                public static String R_OTHER =
                        "<middle>Your inventory has been cleared by <main><player><middle>. <muted>(<total> items in total)";
            }

            public static class FEED {

                public static String SELF = "<middle>You have been fed.";

                public static String OTHER = "<middle>You have fed <main><player><middle>.";

                public static String R_OTHER =
                        "<middle>You have been fed by <main><player><middle>.";
            }

            public static class FLY {

                public static String ENABLED = "&a\u2713 <middle>You have enabled fly mode.";

                public static String DISABLED = "<main>\u2717 <middle>You have disabled fly mode.";

                public static String OTHER_ON =
                        "&a\u2713 <middle>You have enabled fly mode for <bright><player><middle>.";

                public static String OTHER_OFF =
                        "<main>\u2717 <middle>You have disabled fly mode for <bright><player><middle>.";

                public static String R_OTHER_ON =
                        "&a\u2713 <middle>Your fly mode has been enabled by <bright><player><middle>.";

                public static String R_OTHER_OFF =
                        "<main>\u2717 <middle>Your fly mode has been disabled by <bright><player><middle>.";
            }

            public static class GAMEMODE {

                public static String NO_PERMISSION =
                        "<main>\u2717 <middle>You are not allowed to use this gamemode.";

                public static String INVALID = "<main>\u2717 <middle>That game mode is invalid.";

                public static String UPDATED =
                        "<middle>You have updated your game mode to <main><gamemode><middle>.";

                public static String OTHER =
                        "<middle>You have updated <main><player><middle>'s game mode to <main><gamemode><middle>.";

                public static String R_OTHER =
                        "<middle>Your game mode has been updated to <main><gamemode> <middle>by <main><player><middle>.";
            }

            public static class GOD {

                public static String ENABLED = "&a\u2713 <middle>You have enabled god mode.";

                public static String DISABLED = "<main>\u2717 <middle>You have disabled god mode.";
            }

            public static class HEAL {

                public static String SELF = "&a\u2713 <middle>You have been healed!";

                public static String OTHER = "<middle>You have healed <main><player><middle>.";

                public static String R_OTHER =
                        "&a\u2713 <middle>You have been healed by <bright><player><middle>.";
            }

            public static class ITEM {

                public static String LORE_ADDED =
                        "<middle>You have added lore <muted>(<lore>) <middle>to the item you're holding.";

                public static String LORE_REMOVE =
                        "<middle>You have removed lore <muted>(<id>) <middle>from the item you're holding.";

                public static String LORE_REMOVE_FAILED =
                        "<main>\u2717 <middle>Failed to remove this lore.";

                public static String CAN_NOT_RENAME =
                        "<main>\u2717 <middle>You can't rename this item.";

                public static String RENAME =
                        "<middle>You have renamed the item you're holding to <main><name><middle>.";

                public static String ENCHANTED =
                        "<middle>Successfully enchanted your item with <main><enchant><middle>.";

                public static String GAVE =
                        "<middle>Successfully gave you &ex<amount> <material><middle>.";

                public static String GAVE_OTHER =
                        "<middle>Successfully gave <main><player> &ex<amount> <material><middle>.";
            }

            public static class KILL {

                public static String PLAYER_KILL =
                        "<middle>You have killed <main><player><middle>.";

                public static String KILL_RESPONSE =
                        "<middle>You have been killed by <main><player><middle>.";
            }

            public static class MORE {

                public static String SUCCESS =
                        "&a\u2713 <middle>The item you are holding has been multiplied by 64 times.";
            }

            public static class REPAIR {

                public static String ALREADY =
                        "<main>\u2717 <middle>This item is already repaired.";

                public static String SUCCESS_ITEM =
                        "<middle>You have repaired the item you are holding.";

                public static String SUCCESS_ARMOR = "<middle>You have repaired your armor.";

                public static String SUCCESS_ALL = "<middle>You have repaired armor & items.";
            }

            public static class SKULL {

                public static String SUCCESS =
                        "<middle>You have received <main><name><middle>'s skull.";
            }

            public static class SPEED {

                public static String WALK_OR_FLY =
                        "<main>\u2717 <middle>Please use <secondary>&lWALK <middle>or <secondary>&lFLY<middle>.";

                public static String LIMIT =
                        "<main>\u2717 <middle>There is a limit between <secondary>&l<min> <middle>- <secondary>&l<max><middle>.";

                public static String FLY =
                        "<middle>Fly speed has been updated to <main><value><middle>.";

                public static String FLY_OTHER =
                        "<middle>Updated <main><player><middle>'s fly speed to <main><value><middle>.";

                public static String WALK =
                        "<middle>Walk speed has been updated to <main><value><middle>.";

                public static String WALK_OTHER =
                        "<middle>Updated <main><player><middle>'s walk speed to <main><value><middle>.";
            }

            public static class SUDO {

                public static String HIGHER_PRIORITY =
                        "<main>\u2717 <middle>You can not sudo <bright><player><middle> because their priority is higher.";

                public static String SUDO =
                        "<middle>You have made <main><player> <middle>type <main><chat><middle>.";

                public static String ALL =
                        "<middle>You have made all players type <main><chat><middle>.";

                public static String NO_COMMANDS =
                        "<main>\u2717 <middle>You can not force commands through sudo.";
            }

            public static class TELEPORT {

                public static String TP = "<middle>You have teleported to <main><player><middle>.";

                public static String PITCH_REQUIRED =
                        "<main>\u2717 <middle>Pitch must be specified.";

                public static String HERE =
                        "<middle>You have teleported <main><target> <middle>to you.";

                public static String R_HERE =
                        "<middle>You have been teleported to <main><player><middle>'s position.";

                public static String ALL =
                        "<middle>You have teleported all players to your location.";

                public static String POSITION =
                        "<middle>You have teleported to <main><x><middle>, <main><y><middle>, <main><z> <middle>location.";

                public static String POSITION_WITH_YAW_PITCH =
                        "<middle>You have teleported to <main><x><middle>, <main><y><middle>, <main><z><middle>, <main><yaw><middle>, <main><pitch> <middle>location.";

                public static String ALREADY_AT_TOP =
                        "<main>\u2717 <middle>You're already at the top.";

                public static String ALREADY_IN_WORLD =
                        "<main>\u2717 <middle>You're already in this world.";

                public static String TOP = "<middle>You have been teleported at top.";

                public static String WORLD =
                        "<middle>You have been teleported to <main><world> <middle>world.";
            }
        }

        public static class GENERAL {

            public static String INVALID_ITEM = "<main>This item is invalid.";

            public static String INVALID_ENCHANT = "<main>\u2717 <middle>This enchant is invalid.";

            public static String FULL_INVENTORY = "<main>\u2717 <middle>Your inventory is full.";

            public static String PING_SELF = "&e\u2691 <middle>Your Ping: <main><ping>ms";

            public static String PING_OTHER = "<main><player>&e's Ping: <main><ping>ms";

            public static String ENDER_CHEST_OTHER =
                    "<middle>You have opened <main><player><middle>'s ender chest.";

            public static List<String> WELCOME_MESSAGE =
                    Arrays.asList(
                            "<muted>&m-----------------------------------",
                            "",
                            "<middle>Welcome to <main>&lRefine Development",
                            "",
                            " <secondary>\u25cf <middle>Discord: <main><discord>",
                            " <secondary>\u25cf <middle>Website: <main><website>",
                            " <secondary>\u25cf <middle>Store: <main><store>",
                            "",
                            "<muted>&m-----------------------------------");

            public static List<String> HELP =
                    Arrays.asList(
                            "<muted>&m-----------------------------------",
                            "<main>&lHelp <main><muted>- <middle>Useful information",
                            "<muted>&m-----------------------------------",
                            " <secondary>\u25cf <main>/report (player) <muted>\u00bb <middle>Report a player.",
                            " <secondary>\u25cf <main>/request (message) <muted>\u00bb <middle>Request help.",
                            "",
                            "<main>&lSocial Media",
                            " <secondary>\u25cf <main>Discord <muted>\u00bb <middle><discord>",
                            " <secondary>\u25cf <main>Website <muted>\u00bb <middle><website>",
                            " <secondary>\u25cf <main>Store <muted>\u00bb <middle><store>",
                            "<muted>&m-----------------------------------");

            public static List<String> STAFF_HELP =
                    Arrays.asList(
                            "<muted>&m-----------------------------------",
                            "<main>&lStaff Help <main><muted>- <middle>Useful commands.",
                            "<muted>&m-----------------------------------",
                            " <secondary>\u25cf <main>/c (player) <muted>\u00bb <middle>View player's history.",
                            " <secondary>\u25cf <main>/b (player) (reason) [-s] <muted>\u00bb <middle>Ban a player.",
                            " <secondary>\u25cf <main>/tb (player) (reason) [-s] <muted>\u00bb <middle>Temp-ban a player.",
                            " <secondary>\u25cf <main>/ub (player) (reason) [-s] <muted>\u00bb <middle>Unban a player.",
                            " <secondary>\u25cf <main>/mute (player) (reason) [-s] <muted>\u00bb <middle>Mute a player.",
                            " <secondary>\u25cf <main>/tm (player) (reason) [-s] <muted>\u00bb <middle>Temp-mute a player.",
                            " <secondary>\u25cf <main>/um (player) (reason) [-s] <muted>\u00bb <middle>Unmute a player.",
                            " <secondary>\u25cf <main>/shadowmute (player) (reason) [-s] <muted>\u00bb <middle>Shadow-mute a player.",
                            " <secondary>\u25cf <main>/stm (player) (reason) [-s] <muted>\u00bb <middle>Shadow-temp-mute a player.",
                            "<muted>&m-----------------------------------");

            public static List<String> ADMIN_HELP =
                    Arrays.asList(
                            "<muted>&m-----------------------------------",
                            "<main>&lAdmin Help <main><muted>- <middle>Useful commands.",
                            "<muted>&m-----------------------------------",
                            " <secondary>\u25cf <main>/grant (player) <muted>\u00bb <middle>Grant a player.",
                            " <secondary>\u25cf <main>/grants (player) <muted>\u00bb <middle>View player's grants.",
                            " <secondary>\u25cf <main>/user info (player) <muted>\u00bb <middle>View player's info.",
                            " <secondary>\u25cf <main>/staffhistory (player) <muted>\u00bb <middle>View player's staff history.",
                            " <secondary>\u25cf <main>/granthistory (player) <muted>\u00bb <middle>View player's grant history.",
                            "<muted>&m-----------------------------------");
        }

        public static class SOCIAL {

            public static String STORE = "<main>&lStore <main><muted>\u00bb <middle><store>";

            public static String TEAMSPEAK =
                    "<main>&lTeamSpeak <main><muted>\u00bb <middle><teamspeak>";

            public static String DISCORD = "<main>&lDiscord <main><muted>\u00bb <middle><discord>";

            public static String TWITTER = "<main>&lTwitter <main><muted>\u00bb <middle><twitter>";

            public static String WEBSITE = "<main>&lWebsite <main><muted>\u00bb <middle><website>";
        }
    }

    @Comment("Chat control, /clearchat, /mutechat and /slowchat.")
    public static class CHAT {

        public static String CLEARED = "&a\u2713 <middle>The chat was cleared by <player>.";

        public static String MUTED = "<main>\u2717 <middle>The chat has been muted by <player>.";

        public static String UNMUTED = "&a\u2713 <middle>The chat has been unmuted by <player>.";

        public static String SLOWED = "&a\u2713 <middle>The chat has been slowed by <player>.";

        public static String CURRENTLY_MUTED = "<main>\u2717 <middle>The chat is currently muted.";

        public static String CURRENTLY_SLOWED =
                "<main>\u2717 <middle>The chat is currently slowed. Wait another <seconds> seconds.";
    }

    public static class CHAT_LOGS {

        public static String CLEAR = "<middle>You have cleared <target>'s chat logs.";

        public static String FORMAT =
                " <secondary>\u25cf <rail>[#<id>] <muted>&o(<date>) <muted><middle><prefix><sender>: <message>";
    }

    public static class CHAT_SNAPSHOT {

        public static String CREATED =
                "<middle>You have created a chat snapshot with the id <main><id><middle>.";

        public static String NO_CHAT_MESSAGES =
                "<main>\u2717 <middle>No chat messages have been sent in the chat yet.";

        public static String DELETED =
                "<middle>You have deleted the chat snapshot with the id <main><id><middle>.";
    }

    public static class COOLDOWNS {

        public static String COMMAND_COOLDOWN =
                "<main>\u2717 <middle>You are on a command cooldown for <bright><remaining><middle>.";

        public static String DISGUISE_COOLDOWN =
                "<main>\u2717 <middle>You are on a disguise cooldown for <bright><remaining><middle>.";

        public static String REDISGUISE_COOLDOWN =
                "<main>\u2717 <middle>You are on a disguise cooldown for <bright><remaining><middle>.";

        public static String SNAPSHOT_COOLDOWN =
                "<main>\u2717 <middle>You are on a chat snapshot cooldown for <bright><remaining><middle>.";

        public static String SPAWN_COOLDOWN =
                "<main>\u2717 <middle>You are on a spawn cooldown for <bright><remaining><middle>.";

        public static String REPORT_COOLDOWN =
                "<main>\u2717 <middle>You're on a report cooldown for another <remaining>.";

        public static String REQUEST_COOLDOWN =
                "<main>\u2717 <middle>You're on a request cooldown for another <remaining>.";
    }

    public static class CUSTOM_TAGS {

        public static String NOT_ENOUGH_TOKENS =
                "<main>\u2717 <middle>You do not have enough tokens to create a custom tag!";

        public static String PROMPT_CREATE_TAG =
                "&a\u2713 <middle>Please type your tag design, if you wish to cancel type &ecancel<middle>.";

        public static String PROMPT_CANCELLED =
                "<main>\u2717 <middle>You have cancelled the custom tag creation!";

        public static String PROMPT_FAILED_LONGER =
                "<main>\u2717 <middle>Custom tag can not be longer than 16 characters.";

        public static String PROMPT_FILTERED_TAG =
                "<main>\u2717 <middle>We are sorry but &e<tag> <middle>tag does not pass our filter.";

        public static String PROMPT_ALREADY_EXISTS =
                "<main>\u2717 <middle>We are sorry but &e<tag> <middle>tag already exists.";

        public static String CREATED =
                "<middle>You have successfully requested <main><tag><middle>.";

        public static String DELETED =
                "<middle>You have deleted your custom tag <main><tag><middle>.";

        public static String DELETION_CANCELLED =
                "<main>\u2717 <middle>You have cancelled the deletion process of &e<tag> <middle>custom tag!";

        public static String NOT_APPROVED =
                "<main>\u2717 <middle>Your custom tag <muted>(<tag>) <middle>was not approved yet!";

        public static String NOT_ACCEPTED =
                "<main>\u2717 <middle>Your custom tag <muted>(<tag>) <middle>was not accepted!";

        public static String TOKEN_RETURNED =
                "&a\u2713 <middle>A custom tag token has been returned to you. Tokens: &e<amount><middle>!";
    }

    public static class CUSTOM_TAGS_ADMIN {

        public static String ACCEPTED =
                "<middle>You have accepted <main><target><middle>'s custom tag <muted>(<tag>)<middle>.";

        public static String DENIED =
                "<middle>You have denied <main><target><middle>'s custom tag <muted>(<tag>)<middle>.";

        public static String ACCEPT_CANCELLED =
                "<main>\u2717 <middle>You have cancelled the acceptance process of &e<tag> <middle>custom tag that was requested by <target>!";

        public static String DENY_CANCELLED =
                "<main>\u2717 <middle>You have cancelled the denial process of &e<tag> <middle>custom tag that was requested by <target>!";

        public static String ADDED_TOKENS =
                "<middle>You have added <main><amount> <middle>custom tag tokens to <main><target> <middle>and they now have <main><newTokens> <middle>custom tag tokens.";

        public static String REMOVED_TOKENS =
                "<middle>You have removed <main><amount> <middle>custom tag tokens from <main><target> <middle>and they have been left with <main><leftTokens> <middle>custom tag tokens.";

        public static String NOT_ENOUGH_TOKENS = "<target> <main>does not have enough tokens.";

        public static String CHECK =
                "<target> <middle>has <main><amount> <middle>custom tag tokens.";

        public static String FORMAT =
                " <secondary>\u25cf <middle>Status: <status>, Tag: <main><tag> <muted>[ID: <tagId>]";
    }

    public static class DISGUISE {

        public static String DISGUISE_MESSAGE = "<middle>You have disguised as <color><name>.";

        public static String REDISGUISE_MESSAGE =
                "&aSince you were disguised on a different server your disguise has been reapplied.";

        public static String UNDISGUISE_MESSAGE = "&aYou have been undisguised.";

        public static String FAILED =
                "<main>\u2717 <middle>There was an issue while trying to disguise. Contact an administrator.";

        public static String ALREADY_DISGUISED =
                "<main>\u2717 <middle>You are already disguised as <color><name>.";

        public static String ALREADY_DISGUISED_OTHER =
                "<main>\u2717 <middle>That player is already disguised as <color><name>.";

        public static String NO_PREV_DISGUISE =
                "<main>\u2717 <middle>You have not previously disguised.";

        public static String NOT_DISGUISED = "<main>\u2717 <middle>You are not disguised.";

        public static String DISGUISED =
                "<main>\u2717 <middle><player> is disguised as <main><disguised><middle>.";

        public static String FORCE_UNDISGUISED =
                "<middle>You have undisguised <main><player><middle>.";

        public static String VALID = "<main>\u2717 <middle>You need to disguise as a valid name.";

        public static String DISGUISING_IN_PROGRESS =
                "&a\u2713 <middle>Fetching requested skin, please wait.";

        public static String SKIN_FALLBACK =
                "<main>\u2717 <middle>The skin <bright><skin><middle> doesn't exist, disguising you with the default skin instead.";

        public static String NOT_ALLOWED =
                "<main>\u2717 <middle>You cannot disguise with that name.";

        public static String ONLINE_PLAYER_WITH_THAT_NAME =
                "<main>A player with that name is already online.";

        public static String NO_DISGUISED_PLAYERS =
                "<main>\u2717 <middle>There are no disguised players.";

        public static String NO_AVAILABLE_NAMES =
                "<main>\u2717 <middle>There are no available disguise names.";

        public static List<String> LIST =
                Arrays.asList("<muted>", "<main>&lDisguised Players:", "<format>", "<muted>");

        public static String LIST_FORMAT = " <secondary>\u25cf <player> <middle>-> <disguise>";

        public static String CHOOSE_SKIN =
                "&a\u2713 <middle>Please type the name of the Skin you would like.";
    }

    public static class ERROR {

        public static String PLAYER_NOT_ONLINE =
                "<main>\u2717 <bright><player><middle> is currently not online.";

        public static String PROFILE_DOESNT_EXIST =
                "<main>\u2717 <middle>There was no user entry found for <bright><player><middle>.";

        public static String RANK_DOESNT_EXIST =
                "<main>\u2717 <middle>The rank <bright><rank><middle> doesn't exist.";

        public static String CHAT_SNAPSHOT_DOESNT_EXIST =
                "<main>\u2717 <middle>The chat snapshot <bright><chatSnapshot><middle> doesn't exist.";

        public static String NOTIFICATION_DOESNT_EXIST =
                "<main>\u2717 <middle>The notification <bright><notification><middle> doesn't exist.";

        public static String LADDER_DOESNT_EXIST =
                "<main>\u2717 <middle>The punishment ladder <bright><ladder><middle> doesn't exist.";

        public static String TAG_DOESNT_EXIST =
                "<main>\u2717 <middle>The tag <bright><tag><middle> doesn't exist.";

        public static String TAG_CATEGORY_DOESNT_EXIST =
                "<main>\u2717 <middle>The category &n<category><middle> doesn't exist.";

        public static String SERVER_DOESNT_EXIST =
                "<main>\u2717 <middle>The server <bright><server><middle> doesn't exist.";

        public static String SERVER_NOT_ONLINE =
                "<main>\u2717 <middle>The server <bright><server><middle> is not online.";

        public static String PROXY_DOESNT_EXIST =
                "<main>\u2717 <middle>The proxy <bright><proxy><middle> doesn't exist.";

        public static String SKIN_DOESNT_EXIST =
                "<main>\u2717 <middle>The skin <bright><skin><middle> doesn't exist.";

        public static String FILTER_DOESNT_EXIST =
                "<main>\u2717 <middle>The filter <bright><filter><middle> doesn't exist.";

        public static String VOIDREASON_DOESNT_EXIST =
                "<main>\u2717 <middle>The void reason <bright><reason><middle> doesn't exist.";

        public static String GLOBAL_CHAT_DISABLED =
                "<main>\u2717 <middle>You have global chat disabled.";

        public static String SERVER_LOADING =
                "<main>\u2717 <middle>The server is currently loading... Please wait!";

        public static String CANT_CHAT_WHILE_BANNED =
                "<main>\u2717 <middle>You can not chat while being banned/blacklisted.";

        public static String INVALID_SCOPE =
                "<main>\u2717 <bright><input><middle> is an invalid scope. <muted>(Examples: s:Hub-01, g:Hubs, global)<middle>.";

        public static String INVALID_SCOPES =
                "<main>\u2717 <bright><input><middle> are invalid scopes. <muted>(Examples: s:Hub-01;g:Hubs;global)<middle>.";

        public static String OPERATOR_DISABLED =
                "<main>\u2717 <middle>Operator has been disabled via Phoenix.";

        public static String MULTIPLE_PROFILES_WITH_SAME_NAME =
                "<main>There was a total of &e<entries><main> user entries found for &e<player><main>:";

        public static String MULTIPLE_PROFILES_NAME =
                "<muted><number>. &e<name> <muted>(<uuid>) &a&l[Click to execute]";

        public static String MULTIPLE_PLAYERS_MATCH =
                "<main>\u2717 <middle>Multiple players matched <bright><search> <middle>(<all>), please be more specific.";

        public static String INVALID_USERNAME =
                "<main>\u2717 <middle><name> is not a valid username.";

        public static String INVALID_ENCHANTMENT =
                "<main>\u2717 <middle><name> is not a valid enchantment.";

        public static String MESSAGE_ALREADY_DELETED =
                "<main>\u2717 <middle>You have already deleted this message.";

        public static String MOVED_TO_DEFAULT =
                "<main>\u2717 <middle>You're no longer able to use this chat channel and have been moved to &eDefault<middle>.";

        public static String PROFILE_NOT_LOADED = "<main>\u2717 <middle>Profile is not loaded.";

        public static String PROFILE_NOT_CACHED =
                "<main>\u2717 <middle>You tried chatting before your profile has been cached.";

        public static String MOVED_TO_DEFAULT_CHANNEL =
                "<main>\u2717 <middle>You've been moved to the default channel.";

        public static String PASTE_FAILED =
                "<main>\u2717 <middle>An error occurred while creating the paste. Check console.";

        public static String SPLIT_PERMISSIONS = "<main>\u2717 <middle>Split permissions with ;.";

        public static String POSITIVE_NUMBER = "<main>\u2717 <middle>Please use a positive number.";

        public static String INVALID_FILE_NAME = "<main>That is not a file name.";
    }

    public static class EXEC_CMD {

        public static String PERFORM_COMMAND =
                "<middle>You have performed <main><cmd> <middle>on <main><server><middle>.";

        public static String GROUP_DOESNT_EXIST =
                "<main>\u2717 <middle>Group <bright><group> <middle>doesn't exist.";

        public static String PERFORM_GROUP_CMD =
                "<middle>You have performed <main><cmd> <middle>on every server in <main><group> <middle>group.";

        public static String PERFORM_GLOBAL_CMD =
                "<middle>You have performed <main><cmd> <middle>on every server.";

        public static String PERFORM_CMD_ALL_ALERT =
                "&b[S] <muted>[<server>] <main><sender> <middle>executed: <main><command> <middle>on all servers.";

        public static String PERFORM_CMD_SERVER_ALERT =
                "&b[S] <muted>[<server>] <main><sender> <middle>executed: <main><command> <middle>on <main><executed><middle>.";
    }

    public static class FILTER {

        public static String ALREADY_EXISTS =
                "<main>\u2717 <middle>A filter with that name already exists.";

        public static String WOULDVE_BEEN_FILTERED =
                "<main>\u2717 <middle>This would have been filtered.";

        public static String CONTAINS_BLOCKED_WORDS =
                "<main>\u2717 <middle>Your message contains blocked word(s) and can not be sent!";

        public static String STAFF_ALERT =
                "&b[FILTER] <muted>[<server>] <main><player><muted>: <bright><message>";

        public static String STAFF_ALERT_SEVERE =
                "&b[FILTER] &e\u2691 <muted>[<server>] <main><player><muted>: <main><message>";

        public static String STAFF_ALERT_PRIVATE =
                "&b[FILTER] <muted>[<server>] <main><sender> <muted>-> <main><target><muted>: <bright><message>";

        public static String STAFF_ALERT_PRIVATE_SEVERE =
                "&b[FILTER] &e\u2691 <muted>[<server>] <main><sender> <muted>-> <main><target><muted>: <main><message>";

        public static String CREATE =
                "<middle>A <main><type> <middle>filter with the pattern <main><filter> <middle>has been created.";

        public static String DELETED =
                "<middle>The <main><filter> <middle>filter has been deleted.";

        public static String HARD_ON =
                "<middle>The filter <main><filter> <middle>has been set to hard.";

        public static String HARD_OFF =
                "<middle>The filter <main><filter> <middle>has been set to soft.";

        public static String IS_NOT_FILTERED = "<main>\u2717 <middle>This word isn't filtered.";

        public static List<String> FILTER_TEST =
                Arrays.asList(
                        "",
                        "<main>Filter Detected",
                        " <secondary>\u25cf <middle>Name: <main><name>",
                        " <secondary>\u25cf <middle>Filter: <main><filter>",
                        " <secondary>\u25cf <middle>Type: <main><type>",
                        "");

        public static String FORMAT =
                " <secondary>\u25cf <middle><name> <muted>(Type: <type>, Filter: <filter>)";
    }

    public static class FREEZE {

        public static String LOCATION_SET =
                "&a\u2713 <middle>Successfully set the freeze location to your current location.";

        @Comment("Tells the staff member which snapshot was taken when they froze someone.")
        public static String SNAPSHOT_TAKEN =
                "<muted>Chat snapshot <main><id> <muted>taken of <target>.";

        public static String FROZEN = "&a\u2713 <middle>You have been frozen.";

        public static String UNFROZEN = "&a\u2713 <middle>You have been unfrozen.";

        public static String FROZE = "&a\u2713 <middle>You have frozen <target>.";

        public static String UNFROZE = "&a\u2713 <middle>You have unfrozen <target>.";

        public static String HIGHER_RANK =
                "<main>\u2717 <middle>You can not freeze this user since they have a higher rank than you.";

        public static String CANT_HIT_ENTITIES =
                "<main>\u2717 <middle>You cannot hit this player as they're currently frozen.";

        @Comment("Prefixed to anything a frozen player says.")
        public static String PREFIX = "<secondary>[Frozen] ";

        @Comment("Repeated to the frozen player, the interval lives in settings.yml.")
        public static List<String> MESSAGE =
                Arrays.asList(
                        "<main>You have been frozen by a staff member.",
                        "<middle>Please join our Discord: <main>discord.yourserver.com",
                        "<middle>You have <main>5 <middle>minutes. Logging out will result in a ban!");

        @Comment("Shown to staff when a frozen player logs out, if left-frozen-click is on.")
        public static class BAN_BUTTON {

            public static String TEXT = "<main>(Ban)";

            public static String HOVER =
                    "<main>\u00bb <middle>Click to ban <name> for Logged out whilst frozen";

            public static String COMMAND = "/ban <name> Logged out whilst frozen -s";
        }
    }

    public static class GENERAL {

        public static String SPAWN_UPDATED =
                "&a\u2713 <middle>You have updated the spawn location.";

        public static String SPAWN_TELEPORTED =
                "&a\u2713 <middle>You have been teleported to the spawn.";

        public static String SEND_SERVER = "<middle>You are being sent to <main><server><middle>.";

        public static String CHAT_MENTION =
                "<middle>You have been mentioned by <main><player><middle>.";

        public static String CHANNEL_SWITCHED =
                "<middle>You have changed to the <main><channel> <middle>channel.";

        public static String TOGGLE_SETTING_ON =
                "&a\u2713 <middle>You have enabled your <setting>.";

        public static String TOGGLE_SETTING_OFF =
                "<main>\u2717 <middle>You have disabled your <setting>.";

        public static String GENERIC_VIEW_MESSAGE =
                "<middle>Viewing <main><type> <middle>of <player>...";

        public static String GENERIC_CLEAR_MESSAGE =
                "<middle>You have removed <main><size> <middle><type> from <player>.";

        public static String GENERIC_LIST_HEADER_FORMAT =
                "<main>&lSHOWING<main> <rail>\u00bb <middle><type>";

        public static String GENERIC_LIST_HEADER_ADVANCED_FORMAT =
                "<main>&lSHOWING<main> <rail>\u00bb <middle><type> <rail>of <middle><otherType>";

        public static String GENERIC_LIST_FORMAT = " <secondary>\u25cf <middle>";

        public static String GENERIC_LIST_FOOTER_FORMAT =
                "<muted>Page <middle><page><muted>/<middle><pages> <rail>(<results> results)";

        public static String GENERIC_LIST_COLOR_HAS_OTHER_PAGE = "&a";

        public static String GENERIC_LIST_COLOR_NO_OTHER_PAGE = "<main>";

        public static String GENERIC_LIST_NEXT_PAGE_FORMAT = " \u00bb\u00bb";

        public static String GENERIC_LIST_PREVIOUS_PAGE_FORMAT = "\u00ab\u00ab ";

        public static String GENERIC_LIST_AND = "and";

        public static String RANK_LIST_FORMAT = "<displayName> <rail>\u2503 <middle><name>";

        public static String ALREADY_HAVE_PERMISSION =
                "<main>\u2717 <middle>Player <bright><player> <middle>already has <bright><permission> <middle>permission.";

        public static String DONT_HAVE_PERMISSION =
                "<main>\u2717 <middle>Player <bright><player> <middle>doesn't have <bright><permission> <middle>permission.";

        public static String PERMISSION_ADDED =
                "<middle>Added <main><permission> <muted>(<duration>) <middle>permission to <main><player><middle>.";

        public static String PERMISSION_REMOVED =
                "<middle>Removed <main><permission> <middle>permission from <main><player><middle>.";

        public static String PERMISSION_CLEARED =
                "<middle>Cleared <main><player><middle>'s permissions.";

        public static String KICK_SERVER_FULL =
                "<main>\u2717 <middle>The server is currently full.";

        public static class HELP {

            public static String USAGE = "&e\u2691 <muted>Usage <middle><usage>";

            public static String NO_COMMANDS_MATCHED_SEARCH =
                    "<main>\u2717 <middle>No command matched <bright><search><middle>.";

            public static String NO_RESULTS = "<main>\u2717 <middle>No more results.";

            public static String HEADER = "<main>&lHELP<main> <rail>\u00bb <middle><command>";

            public static String SEARCH_HEADER =
                    "<main>&lHELP<main> <rail>\u00bb <middle><command> <muted><search>";

            public static String FORMAT = "<rail>\u25cf <main><command> <middle><parameters>";

            public static String FORMAT_WITH_DESCRIPTION =
                    "<rail>\u25cf <main><command> <middle><parameters> <muted><description>";

            public static String FOOTER =
                    "<previousPage> <muted>Page <middle><page><muted>/<middle><totalPages> <rail>(<results> results) <nextPage>";

            public static String NEXT_PAGE = "&a\u00bb";

            public static String NEXT_PAGE_HOVER = "<muted>Click to go to the next page.";

            public static String PREVIOUS_PAGE = "<main>\u00ab ";

            public static String PREVIOUS_PAGE_HOVER = "<muted>Click to go to the previous page.";
        }

        public static class IGNORE {

            public static String CANT_IGNORE_SELF =
                    "<main>\u2717 <middle>You can not ignore yourself.";

            public static String ALREADY_IGNORING =
                    "<main>\u2717 <middle>You are already ignoring <bright><player><middle>.";

            public static String NOT_IGNORING =
                    "<main>\u2717 <middle>You are not currently ignoring <bright><player><middle>.";

            public static String BEING_IGNORRED = "<bright><player><main> is ignoring you.";

            public static String NAMES = "&a<names>";

            public static String CURRENTLY_IGNORING =
                    "<main>\u2717 <middle>You are currently ignoring <bright><player><middle>.";

            public static String NO_IGNORES =
                    "<main>\u2717 <middle>You are not currently ignoring anybody.";

            public static String IGNORE_ADDED =
                    "<middle>You are now ignoring <main><player><middle>.";

            public static String IGNORE_REMOVED =
                    "<middle>You are no longer ignoring <main><player><middle>.";

            public static List<String> FORMAT =
                    Arrays.asList("<main>&lIgnore List", " <secondary>\u25cf <middle><names>");
        }

        public static class LIST {

            public static List<String> MESSAGE =
                    Arrays.asList(
                            "<middle>",
                            "<ranks><muted>.",
                            "<middle>",
                            "<muted>(<main><online><muted>/<main><max><muted>): <players>.");

            public static String SEPERATOR = "<muted>, ";

            public static String SHOWING_MAX_PLAYERS =
                    "<main>\u2717 <middle>(Only showing the first 350 players)";
        }

        public static class MEDIA_LIST {

            public static List<String> MESSAGE =
                    Arrays.asList("", "<main>&lMedia List", "<servers>", "");

            public static String FORMAT = " <secondary>\u25cf <middle><server>: <players><muted>.";

            public static String DISGUISED = " <muted>(Disguised)";

            public static String SEPERATOR = "<middle>, ";
        }

        public static class MESSAGE {

            public static String PRIVATE_MESSAGE_SENDER =
                    "<muted>(To <main><target><muted>) <bright><message>";

            public static String PRIVATE_MESSAGE_TARGET =
                    "<muted>(From <main><sender><muted>) <bright><message>";

            public static String NO_ONE_TO_REPLY =
                    "<main>\u2717 <middle>You do not have anyone to reply to.";

            public static String MESSAGES_TOGGLED_OFF =
                    "<main>\u2717 <middle>You have your messages toggled off.";

            public static String MESSAGES_TOGGLED_OFF_TARGET =
                    "<bright><player><main> has their messages toggled off.";

            public static String CANT_MESSAGE_SELF =
                    "<main>\u2717 <middle>You can not message yourself.";
        }

        @Comment("Some miscellaneous strings used in Punishments/Grants")
        public static class MISC {

            public static String EXPIRED = "Expired";

            public static String PERMANENT = "Permanent";

            public static String NEVER = "Never";
        }

        public static class SERVER_STATUS {

            public static String ONLINE = "&aOnline";

            public static String WHITELISTED = "&e\u2691 <middle>Whitelisted";

            public static String OFFLINE = "<main>Offline";
        }

        public static class STAFF_LIST {

            public static List<String> MESSAGE =
                    Arrays.asList("", "<main>&lStaff List", "<servers>", "");

            public static String FORMAT = " <secondary>\u25cf <middle><server>: <players><muted>.";

            public static String SEPERATOR = "<middle>, ";
        }
    }

    public static class GRANT {

        @Comment("Sent to the player whose timed rank is nearly up, and once it is.")
        public static String EXPIRING =
                "<middle>Your <main><rank> <middle>rank runs out in <main><remaining><middle>.";

        public static String EXPIRED = "<middle>Your <main><rank> <middle>rank has run out.";

        public static String ALREADY_HAS_RANK =
                "<main>\u2717 <middle><player> already has that rank.";

        public static String DOESNT_HAVE_RANK =
                "<main>\u2717 <middle><player> doesn't have that rank.";

        public static String CANT_GRANT_RANK = "<main>\u2717 <middle>You can not grant this rank.";

        public static String GRANT_REMOVE_PROCEDURE_REASON_ASK =
                "<main>\u2717 <middle>Please type your custom removal reason in chat.";

        public static String GRANT_REMOVE_PROCEDURE_REASON_SET =
                "<middle>You have set the reason to <main><reason><middle>.";

        public static String GRANT_PROCEDURE_RANK_SET =
                "<middle>You have set the grant rank to <main><rank><middle>.";

        public static String GRANT_PROCEDURE_DURATION_ASK =
                "<main>\u2717 <middle>Please type a custom duration for this grant in chat. Type <muted>perm <middle>for permanent duration.";

        public static String GRANT_PROCEDURE_DURATION_SET =
                "<middle>You have set the duration to <main><duration><middle>.";

        public static String GRANT_PROCEDURE_SCOPES_SET =
                "<middle>You have set the grant scopes to <main><scopes><middle>.";

        public static String GRANT_PROCEDURE_REASON_ASK =
                "<middle>Please type a custom reason for this grant in chat.";

        public static String GRANT_PROCEDURE_REASON_SET =
                "<middle>You have set the reason to <main><reason><middle>.";

        public static String HIGHER_PRIORITY =
                "<main>\u2717 <middle>You cannot grant <bright><player><middle> because their priority is higher.";

        public static List<String> GRANTED_PERM_SELF =
                Arrays.asList(
                        "<middle>You have granted <main><user> <middle>the <main><rank> <middle>rank for <main><reason><middle>.",
                        "",
                        "<main>&lGrant Scopes",
                        " <secondary>\u25cf <middle><scopes>",
                        "",
                        "<middle>This grant will <main>never <middle>expire.");

        public static List<String> GRANTED_TEMP_SELF =
                Arrays.asList(
                        "<middle>You have granted <main><user> <middle>the <main><rank> <middle>rank for <main><reason><middle>.",
                        "",
                        "<main>&lGrant Scopes",
                        " <secondary>\u25cf <middle><scopes>",
                        "",
                        "<middle>This grant will expire in <main><expire><middle>.");

        public static List<String> GRANTED_PERM_TARGET =
                Arrays.asList("", "<middle>You have been granted the rank <rank>.", "");

        public static List<String> GRANTED_TEMP_TARGET =
                Arrays.asList(
                        "",
                        "<middle>You have been granted the rank <rank> for <main><time><middle>.",
                        "");

        public static List<String> GRANT_REMOVED_SELF =
                Arrays.asList(
                        "",
                        "<middle>You have removed the <main><rank> <middle>grant from <main><player><middle>.",
                        "");

        public static List<String> GRANT_REMOVED_TARGET =
                Arrays.asList("", "<middle>Your <main><rank> <middle>grant has been removed.", "");
    }

    public static class GROUP_INFO {

        public static List<String> LIST =
                Arrays.asList(
                        "",
                        "<main>&lAvailable Groups:",
                        "  <secondary>\u25cf <middle><groups>",
                        "");

        public static List<String> SERVERS =
                Arrays.asList(
                        "",
                        "<main>&lAvailable Servers:",
                        "  <secondary>\u25cf <middle><servers>",
                        "");
    }

    public static class MC_LOGS {

        public static String FILE_DOES_NOT_EXIST =
                "<main>\u2717 <middle>The specified file &e(<file>)<middle> does not exist.";

        public static String FILE_IS_DIR =
                "<main>\u2717 <middle>The specified file &e(<file>)<middle> is a directory.";

        public static String MUST_BE_A_LOG_FILE =
                "&<main>The specified file &e(<file>)<main> is not a log file. Valid suffixes: .txt .log .gz";
    }

    public static class MODMODE {

        public static String CANT_BREAK_BLOCKS =
                "<main>\u2717 <middle>You can't break blocks while in modmode.";

        public static String CANT_PLACE_BLOCKS =
                "<main>\u2717 <middle>You can't place blocks while in modmode.";

        public static String ENABLED = "&a\u2713 <middle>You've enabled modmode.";

        public static String DISABLED = "<main>\u2717 <middle>You've disabled modmode.";

        public static String ENABLED_OTHER =
                "&a\u2713 <middle>You've enabled modmode for <player>.";

        public static String DISABLED_OTHER =
                "<main>\u2717 <middle>You've disabled modmode for <player>.";

        public static String CANT_HIT_ENTITIES =
                "<main>\u2717 <middle>You can't hit entities while in modmode.";
    }

    public static class NOTES {

        public static List<String> ADDED =
                Arrays.asList(
                        "",
                        "<main>&lNote Added",
                        " <secondary>\u25cf <middle>Player: <main><player>",
                        " <secondary>\u25cf <middle>ID: <main><note>",
                        "");

        public static List<String> REMOVED =
                Arrays.asList(
                        "",
                        "<main>&lNote Removed",
                        " <secondary>\u25cf <middle>Player: <main><player>",
                        " <secondary>\u25cf <middle>Note: <main><note>",
                        "");

        public static String DOES_NOT_EXIST =
                "<main>\u2717 <middle>Note with this ID does not exist.";

        public static String FORMAT =
                " <secondary>\u25cf <middle><issuedBy> <muted>&o(<date>)<muted><middle>: <reason>";

        public static String REMOVE_X = "<middle> <main>&l[\u2718]";

        public static String HOVER_REM0VE = "<main>\u00bb <middle>Click to remove this note.";

        public static class ON_LOGIN {

            @Comment("How many of the latest notes to show?")
            public static int AMOUNT = 3;

            @Comment("Required Permission: core.notes.view")
            public static List<String> MESSAGE =
                    Arrays.asList(
                            "",
                            "<middle>Showing you the last <main><amount> <middle>notes of <player>.",
                            "<notes>",
                            "");
        }
    }

    public static class NOTIFICATIONS {

        public static String UNREAD_NOTIFICATIONS =
                "<middle>You have <main><size> <middle>unread notifications.";

        public static String ALREADY_EXISTS =
                "<main>\u2717 <middle>A notification called <bright><notification><middle> already exists.";

        public static String CREATED =
                "<middle>You have created the <main><notification> <middle>notification.";

        public static String DELETED =
                "<middle>You have deleted the <main><notification> <middle>notification.";

        public static String TITLE_UPDATED =
                "<middle>You have updated the title of <main><notification> <middle>to <main><title><middle>.";
    }

    public static class PANIC {

        public static String PANICKED = "&a\u2713 <middle>You have been put into panic mode.";

        public static String UNPANICKED = "&a\u2713 <middle>You are no longer in panic mode.";

        public static String UNPANIC_OTHER = "&a\u2713 <middle>You have unpanicked <target>.";

        public static String ALREADY_PANIC = "<main>\u2717 <middle>You are already in panic mode!";

        public static String NOT_IN_PANIC = "<main>\u2717 <middle><target> is not in panic!";

        public static String CANT_HIT_ENTITIES =
                "<main>\u2717 <middle>You cannot hit this player as they're currently in panic.";

        @Comment("Repeated to the panicked player, the interval lives in settings.yml.")
        public static List<String> MESSAGE =
                Arrays.asList(
                        "<main>You are in panic mode.",
                        "<middle>Please join our Discord: <main>discord.yourserver.com",
                        "<middle>Message a staff member so they can unpanic you!");
    }

    public static class PHOENIX {

        public static List<String> DEBUG =
                Arrays.asList(
                        "<main>&lPHOENIX DEBUG",
                        "<rail>\u2503 <muted>Profiles: <middle><profiles>",
                        "<rail>\u2503 <muted>Ranks: <middle><ranks>",
                        "<rail>\u2503 <muted>Servers: <middle><servers>",
                        "<rail>\u2503 <muted>Proxies: <middle><proxies>",
                        "<rail>\u2503 <muted>Filters: <middle><filters>",
                        "<rail>\u2503 <muted>Grants: <middle><grants>",
                        "<rail>\u2503 <muted>Punishments: <middle><punishments>",
                        "<rail>\u2503 <muted>Logins: <middle><logins>",
                        "<rail>\u2503 <muted>Chat Logs: <middle><chatLogs>",
                        "<rail>\u2503 <muted>Tags: <middle><tags>",
                        "<rail>\u2503 <muted>Tag Categories: <middle><categories>",
                        "<rail>\u2503 <muted>Disguised Players: <middle><disguisedPlayers>");

        public static String GIT_ID = "<bright>Git ID: <muted><id>";

        public static String SPAWN_NOT_SET =
                "<main>\u2717 <middle>Please set the spawn or turn off teleportation.";

        public static class CLAIM {

            public static String CLAIMED =
                    "&a\u2713 <middle>You own this server now, with the <main><rank> <middle>rank.";

            public static String SECURITY =
                    "<middle>Set up two-factor auth next. It keeps the owner account safe if your password leaks.";

            public static String WRONG =
                    "<main>\u2717 <middle>That code is wrong. <bright><left> <middle>tries left.";

            public static String LOCKED =
                    "<main>\u2717 <middle>Too many wrong codes. Restart the server for a new one.";

            public static String NOTHING =
                    "<main>\u2717 <middle>There is nothing to claim on this server.";

            public static String NO_RANKS =
                    "<main>\u2717 <middle>There are no ranks yet. Run <bright>rank tools import-preset <middle>from the console first.";
        }

        public static class CONFIG {

            public static String SET_MASTER =
                    "<middle>Set master config server to <main><server><middle>.";

            public static String UPDATED =
                    "&a\u2713 <middle>Loaded and updated config files from redis.";

            public static String PUSHED = "&a\u2713 <middle>Pushed local config files to redis.";

            public static String FORCE_FETCHED =
                    "&a\u2713 <middle>Updated all server's config files from redis.";
        }

        @Comment("The lines /phoenix doctor reports with.")
        public static class DOCTOR {

            public static String DONE =
                    "<muted>Done. Lines marked &e[WARN]<muted> or <main>[FAIL]<muted> deserve a look.";

            public static String OK = "&a\u2713 <middle><message>";

            public static String WARN = "&e\u2691 <middle><message>";

            public static String FAIL = "<main>\u2717 <middle><message>";

            public static String TODO = "&b\u25cf <middle><message>";

            public static String DETAIL = "     <rail><message>";
        }

        @Comment("What /phoenix import says while it works.")
        public static class IMPORT {

            public static String ALREADY_STARTED =
                    "<main>\u2717 <middle>Import has already been started.";

            public static String FINISHED = "&a\u2713 <middle>Import finished!";

            public static String AQUACORE_FINISHED = "&a\u2713 <middle>AquaCore import finished!";

            public static String AQUACORE_NEEDS_MONGO =
                    "<main>\u2717 <middle>The AquaCore import reads Mongo through Phoenix's own connection, so Phoenix has to be on Mongo to run it.";
        }

        public static class PERMISSIONS {

            public static String WRITTEN =
                    "&a\u2713 <middle>Wrote <main><count> <middle>permissions to <bright><file><middle>.";

            public static String FAILED =
                    "<main>\u2717 <middle>Could not write the file: <bright><error>";
        }

        public static class PREVIEW {

            public static String HEADER =
                    "<muted>Preview of <bright><key><muted>, as saved in messages.yml:";

            public static String UNKNOWN =
                    "<main>\u2717 <middle>There is no message called <bright><key><middle>. Close matches: <bright><suggestions>";
        }

        public static class RELOAD {

            public static String FAILED =
                    "<main>\u2717 <middle>Nothing was reloaded, because a file has a mistake:";

            public static String PROBLEM = "   <bright><problem>";
        }

        @Comment("The lines /phoenix setup reports with.")
        public static class SETUP {

            public static String HEADER = "<main>&lPHOENIX SETUP";

            public static String DONE =
                    "<muted><todo> unstarted, <warn> worth a look, <fail> broken. Fixes are shown under each line.";

            public static String FIX = "        <muted>fix: <bright><fix>";

            public static String FIX_HOVER =
                    "&a\u00bb <middle>Click to put <bright><command> <middle>in your chat box.";

            public static String DOCS = "        <muted>guide: <bright><url>";

            public static String DOCS_HOVER =
                    "&a\u00bb <middle>Click to open the guide for this in your browser.";

            public static String CHANGED =
                    "<middle>Set <main><key> <middle>to <main><value><middle>.";

            public static String UNCHANGED = "<muted><key> was already that. Nothing was written.";

            public static String CANCELLED = "<muted>Nothing was changed.";

            public static String LOCKED =
                    "<main>\u2717 <middle><holder> is changing <area> right now. Try again in a moment.";

            public static String FAILED = "<main>\u2717 <middle>Could not write that: <error>";

            public static String BYPASSES_GRANTED =
                    "<middle>Granted <main><count> <middle>bypass permissions to <main><rank><middle>.";

            public static String NO_AREA =
                    "<main>\u2717 <middle>There is no setup area called <bright><area><middle>. Run <bright>/phoenix setup <middle>to see them.";

            public static String STARTUP_HEADER =
                    "<main>&lPHOENIX <middle>has <main><count> <middle>setup items to look at:";

            public static String STARTUP_FOOTER =
                    "<muted>Run <bright>/phoenix setup <muted>in game to see how to fix them.";

            public static String JOIN_REMINDER =
                    "<main>\u00bb <middle>Phoenix has <main><count> <middle>setup items to look at. <bright>[Open setup]";

            public static String JOIN_REMINDER_HOVER =
                    "&a\u00bb <middle>Click to run <bright>/phoenix setup";

            public static class DATABASE {

                public static String HEADER =
                        "<main>&lDATABASE TEST <muted>(global.yml as saved on disk)";

                public static String OK =
                        "&a\u2713 <middle><backend> answered in <bright><millis>ms<middle>.";

                public static String FAIL = "<main>\u2717 <middle><backend>: <bright><error>";

                public static String SKIPPED = "<muted>\u25cf <middle><backend>: <bright><hint>";

                public static String HINT = "     <rail><hint>";

                public static String NOTE =
                        "<muted>Restart the server to start using these settings.";

                public static String BROKEN_FILE =
                        "<main>\u2717 <middle>global.yml can't be read: <bright><problem>";
            }
        }

        public static class UPDATE {

            public static String AVAILABLE =
                    "<main>\u00bb <middle>Phoenix <main><latest> <middle>is out; this server runs <bright><current><middle>. Restart to load it.";
        }

        public static class VERSION {

            public static List<String> LINES =
                    Arrays.asList(
                            "<main>&lPHOENIX VERSION",
                            "<rail>\u2503 <muted>Version: <middle><version>",
                            "<rail>\u2503 <muted>Commit: <middle><commit>",
                            "<rail>\u2503 <muted>Git Branch: <middle><branch>",
                            "<rail>\u2503 <muted>Loader Branch: <middle><loader-branch>",
                            "<rail>\u2503 <muted>Server: <middle><server>");

            public static String UNKNOWN = "unknown";
        }
    }

    public static class PLAYTIME {

        public static List<String> PLAYTIME_SELF =
                Arrays.asList(
                        "",
                        "<main>Your Playtime Statistics",
                        " <secondary>\u25cf <middle>Total Playtime: <main><playtime>",
                        " <secondary>\u25cf <middle>Logins: <main><logins>",
                        "");

        public static List<String> PLAYTIME_OTHER =
                Arrays.asList(
                        "",
                        "<player><main>'s Playtime Statistics",
                        " <secondary>\u25cf <middle>Total Playtime: <main><playtime>",
                        " <secondary>\u25cf <middle>Logins: <main><logins>",
                        "");
    }

    public static class PROOF {

        public static String CANCELLED = "<main>\u2717 <middle>Cancelled";

        public static String TYPE =
                "&a\u2713 <middle>Is this a &eLink<middle> or a &eChat Snapshot<middle> proof? Type &eLink<middle> or &eSnapshot<middle>.";

        public static String PROOF = "&a\u2713 <middle>Please input the proof.";

        public static String PROOF_ADDED = "&a\u2713 <middle>Proof added.";
    }

    public static class PROXY_INFO {

        public static List<String> INFO =
                Arrays.asList(
                        "",
                        "<main>&l<proxy> Information",
                        " <secondary>\u25cf <middle>Display Name: <main><display>",
                        " <secondary>\u25cf <middle>Group: <main><group>",
                        " <secondary>\u25cf <middle>Region: <main><region>",
                        " <secondary>\u25cf <middle>Players: <main><count> / <max>",
                        " <secondary>\u25cf <middle>Whitelisted: <main><whitelisted>",
                        "");

        public static List<String> PROXIES =
                Arrays.asList(
                        "",
                        "<main>&lAvailable Proxies:",
                        "  <secondary>\u25cf <middle><proxies>",
                        "");

        public static String DELETE = "<middle>You have deleted the <main><proxy> <middle>proxy.";
    }

    public static class PUNISHMENT {

        public static String IMMUNE = "<bright><player><main> is immune from punishments.";

        public static String ALREADY_PUNISHED = "<bright><player><main> is already <type>.";

        public static String NOT_PUNISHED = "<bright><player><main> is not <type>.";

        public static String CANT_TALK_WHILE_BANNED_OR_BLACKLISTED =
                "<main>\u2717 <middle>You cannot chat while being banned/blacklisted.";

        public static String VOIDED_ALL =
                "&a\u2713 <middle>Done... Voided all currently active <type>.";

        @Comment({
            "Shown after a network-wide wipe. Worth keeping the wording explicit:",
            "these delete the punishment type for every player, not for one of them."
        })
        public static String KICKS_WIPED =
                "&a\u2713 <middle>Deleted <bright><amount><middle> kicks <muted>(every player on the network).";

        public static String WARNS_WIPED =
                "&a\u2713 <middle>Deleted <bright><amount><middle> warns <muted>(every player on the network).";

        @Comment({
            "Shown when a wipe is run without -confirm. These are the most destructive",
            "commands in the plugin, so the prompt names the scope rather than implying it."
        })
        public static List<String> WIPE_CONFIRM =
                Arrays.asList(
                        "",
                        "<main>&lHOLD ON",
                        "<middle>This deletes every <type> on record, for &nevery player on the network<middle> -- not for one of them.",
                        "<middle>It cannot be undone.",
                        "",
                        "<muted>Run <bright><command> -confirm <muted>if that is what you meant.",
                        "");

        public static String HIGHER_PRIORITY =
                "<main>\u2717 <middle>You cannot punish <bright><player><middle> because their priority is higher.";

        public static String LOOKUP_INVALID_ID =
                "<main>\u2717 <middle>ID <bright>#<id> <middle>is invalid.";

        public static String VOIDED =
                "<middle>You have voided punishment <secondary>#<id><middle> as <secondary><void_reason><middle>.";

        public static String PUNISHMENT_DURATION_TOO_LARGE =
                "<main>\u2717 <middle>You don't have permission to use such a long duration.";

        public static String INVALID_DURATION =
                "<main>\u2717 <middle>You've provided an invalid duration.";

        public static String PUNISHMENT_EXECUTED =
                "<silent>&a\u2713 <middle>You have <type> <player> for <main><reason><middle>.";

        public static String SILENT = "<rail>(Silent) ";

        @Comment("Asked of the staff member when a punishment needs evidence and has none.")
        public static String PROOF_PROMPT =
                "&e\u2691 <middle>Link the evidence for this <type>, or type 'cancel' to stop.";

        public static String PROOF_CANCELLED =
                "<main>\u2717 <middle>No evidence given, so nothing was issued.";

        public static class BAN {

            public static String DEFAULT_REASON = "Cheating";

            public static String BROADCAST =
                    "<silent><main>\u2717 <sender><middle> permanently banned <target>.";

            public static List<String> HOVER =
                    Arrays.asList("<middle>Reason: <main><reason> <muted>(<server>)");

            public static List<String> KICK_MESSAGE =
                    Arrays.asList(
                            "",
                            "<main>&lBANNED",
                            "",
                            "<middle>This account is banned from the network.",
                            "",
                            "<muted>Reason: <bright><reason>",
                            "<muted>Punishment ID: <bright>#<punishmentID>",
                            "",
                            "<muted>Appeal at <main><discord>",
                            "");
        }

        public static class BLACKLIST {

            public static String DEFAULT_REASON = "Misconduct";

            public static String BROADCAST =
                    "<silent><main>\u2717 <sender><middle> blacklisted <target>.";

            public static List<String> HOVER =
                    Arrays.asList("<middle>Reason: <main><reason> <muted>(<server>)");

            public static List<String> KICK_MESSAGE =
                    Arrays.asList(
                            "",
                            "<main>&lBLACKLISTED",
                            "",
                            "<middle>This account is permanently removed from the network.",
                            "",
                            "<muted>Reason: <bright><reason>",
                            "<muted>Punishment ID: <bright>#<punishmentID>",
                            "",
                            "<rail>This punishment cannot be appealed.",
                            "");

            public static List<String> ALT_KICK_MESSAGE =
                    Arrays.asList(
                            "",
                            "<main>&lBLACKLISTED",
                            "",
                            "<middle>This account is permanently removed from the network.",
                            "",
                            "<muted>Reason: <bright><reason>",
                            "<muted>Punishment ID: <bright>#<punishmentID>",
                            "<muted>Alt: <bright><alt>",
                            "",
                            "<rail>This punishment cannot be appealed.",
                            "");
        }

        public static class COMPETITIVE_BAN {

            public static String DEFAULT_REASON = "Boosting";

            public static String BROADCAST =
                    "<silent><main>\u2717 <sender><middle> competitively banned <target>.";

            public static List<String> HOVER =
                    Arrays.asList("<middle>Reason: <main><reason> <muted>(<server>)");
        }

        public static class COMPETITIVE_TEMPBAN {

            public static String DEFAULT_REASON = "Boosting";

            public static String BROADCAST =
                    "<silent><main>\u2717 <sender><middle> competitively banned <target> for <secondary><duration><middle>.";

            public static List<String> HOVER =
                    Arrays.asList(
                            "<middle>Reason: <main><reason> <muted>(<server>)",
                            "<middle>Duration: <main><duration>");
        }

        public static class COMPETITIVE_UNBAN {

            public static String DEFAULT_REASON = "Pardoned";

            public static String BROADCAST =
                    "<silent>&a\u2713 <sender><middle> competitively unbanned <target>.";

            public static List<String> HOVER =
                    Arrays.asList("<middle>Reason: <main><reason> <muted>(<server>)");
        }

        public static class IPBAN {

            public static String DEFAULT_REASON = "Cheating";

            public static String BROADCAST =
                    "<silent><main>\u2717 <sender><middle> permanently ip-banned <target>.";

            public static List<String> HOVER =
                    Arrays.asList("<middle>Reason: <main><reason> <muted>(<server>)");

            public static List<String> KICK_MESSAGE =
                    Arrays.asList(
                            "",
                            "<main>&lIP BANNED",
                            "",
                            "<middle>This address is banned from the network.",
                            "",
                            "<muted>Reason: <bright><reason>",
                            "<muted>Punishment ID: <bright>#<punishmentID>",
                            "",
                            "<muted>Appeal at <main><discord>",
                            "");

            public static List<String> ALT_KICK_MESSAGE =
                    Arrays.asList(
                            "",
                            "<main>&lIP BANNED",
                            "",
                            "<middle>This address is banned from the network.",
                            "",
                            "<muted>Reason: <bright><reason>",
                            "<muted>Punishment ID: <bright>#<punishmentID>",
                            "<muted>Alt: <bright><alt>",
                            "",
                            "<muted>Appeal at <main><discord>",
                            "");
        }

        public static class IPMUTE {

            public static String DEFAULT_REASON = "Spamming";

            public static String BROADCAST =
                    "<silent><main>\u2717 <sender><middle> permanently ip-muted <target>.";

            public static List<String> HOVER =
                    Arrays.asList("<middle>Reason: <main><reason> <muted>(<server>)");

            public static List<String> MESSAGE =
                    Arrays.asList(
                            "",
                            "<main>&lYOU HAVE BEEN IP MUTED",
                            "<rail>\u2503 <muted>Alt: <middle><alt>",
                            "<rail>\u2503 <muted>Duration: <middle>Permanent",
                            "<rail>\u2503 <muted>Reason: <middle><reason>",
                            "<rail>\u2503 <muted>Punishment ID: <middle>#<punishmentID>",
                            "");

            public static List<String> CANT_TALK_MESSAGE =
                    Arrays.asList(
                            "",
                            "<main>&lYOU ARE IP MUTED",
                            "<rail>\u2503 <muted>Alt: <middle><alt>",
                            "<rail>\u2503 <muted>Duration: <middle>Permanent",
                            "<rail>\u2503 <muted>Reason: <middle><reason>",
                            "<rail>\u2503 <muted>Punishment ID: <middle>#<punishmentID>",
                            "");
        }

        public static class KICK {

            public static String DEFAULT_REASON = "Misconduct";

            public static String BROADCAST =
                    "<silent><main>\u2717 <sender><middle> kicked <target>.";

            public static List<String> HOVER =
                    Arrays.asList("<middle>Reason: <main><reason> <muted>(<server>)");

            public static List<String> KICK_MESSAGE =
                    Arrays.asList("", "<main>&lKICKED", "", "<middle><reason>", "");
        }

        public static class MUTE {

            public static String DEFAULT_REASON = "Spamming";

            public static String BROADCAST =
                    "<silent><main>\u2717 <sender><middle> permanently muted <target>.";

            public static List<String> HOVER =
                    Arrays.asList("<middle>Reason: <main><reason> <muted>(<server>)");

            public static List<String> MESSAGE =
                    Arrays.asList(
                            "",
                            "<main>&lYOU HAVE BEEN MUTED",
                            "<rail>\u2503 <muted>Duration: <middle>Permanent",
                            "<rail>\u2503 <muted>Reason: <middle><reason>",
                            "<rail>\u2503 <muted>Punishment ID: <middle>#<punishmentID>",
                            "");

            public static List<String> CANT_TALK_MESSAGE =
                    Arrays.asList(
                            "",
                            "<main>&lYOU ARE MUTED",
                            "<rail>\u2503 <muted>Duration: <middle>Permanent",
                            "<rail>\u2503 <muted>Reason: <middle><reason>",
                            "<rail>\u2503 <muted>Punishment ID: <middle>#<punishmentID>",
                            "");
        }

        public static class TEMPBAN {

            public static String DEFAULT_REASON = "Cheating";

            public static String BROADCAST =
                    "<silent><main>\u2717 <sender><middle> temporarily banned <target> for <secondary><duration><middle>.";

            public static List<String> HOVER =
                    Arrays.asList(
                            "<middle>Reason: <main><reason> <muted>(<server>)",
                            "<middle>Duration: <main><duration>");

            public static List<String> KICK_MESSAGE =
                    Arrays.asList(
                            "",
                            "<main>&lTEMPORARILY BANNED",
                            "",
                            "<middle>This account is banned from the network for now.",
                            "",
                            "<muted>Reason: <bright><reason>",
                            "<muted>Punishment ID: <bright>#<punishmentID>",
                            "<muted>Expires in: <bright><expire>",
                            "",
                            "<muted>Appeal at <main><discord>",
                            "");
        }

        public static class TEMPIPBAN {

            public static String DEFAULT_REASON = "Cheating";

            public static String BROADCAST =
                    "<silent><main>\u2717 <sender><middle> temporarily ip-banned <target> for <secondary><duration><middle>.";

            public static List<String> HOVER =
                    Arrays.asList(
                            "<middle>Reason: <main><reason> <muted>(<server>)",
                            "<middle>Duration: <main><duration>");

            public static List<String> KICK_MESSAGE =
                    Arrays.asList(
                            "",
                            "<main>&lTEMPORARILY IP BANNED",
                            "",
                            "<middle>This address is banned from the network for now.",
                            "",
                            "<muted>Reason: <bright><reason>",
                            "<muted>Punishment ID: <bright>#<punishmentID>",
                            "<muted>Expires in: <bright><expire>",
                            "",
                            "<muted>Appeal at <main><discord>",
                            "");

            public static List<String> ALT_KICK_MESSAGE =
                    Arrays.asList(
                            "",
                            "<main>&lTEMPORARILY IP BANNED",
                            "",
                            "<middle>This address is banned from the network for now.",
                            "",
                            "<muted>Reason: <bright><reason>",
                            "<muted>Punishment ID: <bright>#<punishmentID>",
                            "<muted>Expires in: <bright><expire>",
                            "<muted>Alt: <bright><alt>",
                            "",
                            "<muted>Appeal at <main><discord>",
                            "");
        }

        public static class TEMPIPMUTE {

            public static String DEFAULT_REASON = "Spamming";

            public static String BROADCAST =
                    "<silent><main>\u2717 <sender><middle> temporarily ip-muted <target> for <secondary><duration><middle>.";

            public static List<String> HOVER =
                    Arrays.asList(
                            " <secondary>\u25cf <middle>Reason: <main><reason> <muted>(<server>)",
                            " <secondary>\u25cf <middle>Duration: <main><duration>");

            public static List<String> MESSAGE =
                    Arrays.asList(
                            "",
                            "<main>&lYOU HAVE BEEN IP MUTED",
                            "<rail>\u2503 <muted>Alt: <middle><alt>",
                            "<rail>\u2503 <muted>Duration: <middle><duration>",
                            "<rail>\u2503 <muted>Reason: <middle><reason>",
                            "<rail>\u2503 <muted>Punishment ID: <middle>#<punishmentID>",
                            "");

            public static List<String> CANT_TALK_MESSAGE =
                    Arrays.asList(
                            "",
                            "<main>&lYOU ARE IP MUTED",
                            "<rail>\u2503 <muted>Alt: <middle><alt>",
                            "<rail>\u2503 <muted>Duration: <middle><duration>",
                            "<rail>\u2503 <muted>Reason: <middle><reason>",
                            "<rail>\u2503 <muted>Punishment ID: <middle>#<punishmentID>",
                            "");
        }

        public static class TEMPMUTE {

            public static String DEFAULT_REASON = "Spamming";

            public static String BROADCAST =
                    "<silent><main>\u2717 <sender><middle> temporarily muted <target> for <secondary><duration><middle>.";

            public static List<String> HOVER =
                    Arrays.asList(
                            "<middle>Reason: <main><reason> <muted>(<server>)",
                            "<middle>Duration: <main><duration>");

            public static List<String> MESSAGE =
                    Arrays.asList(
                            "",
                            "<main>&lYOU HAVE BEEN MUTED",
                            "<rail>\u2503 <muted>Duration: <middle><duration>",
                            "<rail>\u2503 <muted>Reason: <middle><reason>",
                            "<rail>\u2503 <muted>Punishment ID: <middle>#<punishmentID>",
                            "");

            public static List<String> CANT_TALK_MESSAGE =
                    Arrays.asList(
                            "",
                            "<main>&lYOU ARE MUTED",
                            "<rail>\u2503 <muted>Duration: <middle><duration>",
                            "<rail>\u2503 <muted>Reason: <middle><reason>",
                            "<rail>\u2503 <muted>Punishment ID: <middle>#<punishmentID>",
                            "");
        }

        public static class UNBAN {

            public static String DEFAULT_REASON = "Pardoned";

            public static String BROADCAST = "<silent>&a\u2713 <sender><middle> unbanned <target>.";

            public static List<String> HOVER =
                    Arrays.asList("<middle>Reason: <main><reason> <muted>(<server>)");
        }

        public static class UNBLACKLIST {

            public static String DEFAULT_REASON = "Pardoned";

            public static String BROADCAST =
                    "<silent>&a\u2713 <sender><middle> unblacklisted <target>.";

            public static List<String> HOVER =
                    Arrays.asList("<middle>Reason: <main><reason> <muted>(<server>)");
        }

        public static class UNMUTE {

            public static String DEFAULT_REASON = "Pardoned";

            public static String BROADCAST = "<silent>&a\u2713 <sender><middle> unmuted <target>.";

            public static List<String> HOVER =
                    Arrays.asList("<middle>Reason: <main><reason> <muted>(<server>)");
        }

        public static class WARN {

            public static String DEFAULT_REASON = "Misconduct";

            public static String BROADCAST =
                    "<silent><main>\u2717 <sender><middle> warned <target>.";

            public static List<String> HOVER =
                    Arrays.asList("<middle>Reason: <main><reason> <muted>(<server>)");

            public static List<String> MESSAGE =
                    Arrays.asList(
                            "",
                            "<main>&lYOU HAVE BEEN WARNED",
                            "<rail>\u2503 <muted>Reason: <middle><reason>",
                            "<rail>\u2503 <muted>Punishment ID: <middle>#<punishmentID>",
                            "");
        }
    }

    public static class PUNISHMENT_LADDER {

        public static String ID_DOESNT_EXIST =
                "<main>\u2717 <middle>A punishment with that doesn't exist.";

        public static String CREATED =
                "<middle>You have created a new <main><ladder> <middle>punishment ladder.";

        public static String ALREADY_EXISTS =
                "<main>\u2717 <middle>Ladder &l&n<ladder><middle>&l<middle> already exists.";

        public static String DELETED =
                "<middle>You have deleted <main><ladder> <middle>punishment ladder.";

        public static String HIDDEN_ENABLED =
                "<middle>You have made <main><ladder> <middle>punishment ladder invisible.";

        public static String HIDDEN_DISABLED =
                "<middle>You have made <main><ladder> <middle>punishment ladder visible.";

        public static String PRIORITY_CHANGED =
                "<middle>You have set <main><ladder> <middle>punishment ladder priority to <main><priority><middle>.";

        public static List<String> PUNISHMENT_ADDED =
                Arrays.asList(
                        "<main>&lPunishment Added <main><muted>(<ladder>)",
                        " <secondary>\u25cf <middle>Reason: <main><reason>",
                        " <secondary>\u25cf <middle>Type: <main><type>",
                        " <secondary>\u25cf <middle>Duration: <main><duration>",
                        " <secondary>\u25cf <middle>Decay: <main><decay>",
                        " <secondary>\u25cf <middle>IP Relative: <main><ip>",
                        " <secondary>\u25cf <middle>Shadow: <main><shadow>");

        public static List<String> PUNISHMENT_EDITED =
                Arrays.asList(
                        "<main>&lPunishment Edited <main><muted>(<ladder>)",
                        " <secondary>\u25cf <middle>Reason: <main><reason>",
                        " <secondary>\u25cf <middle>Type: <main><type>",
                        " <secondary>\u25cf <middle>Duration: <main><duration>",
                        " <secondary>\u25cf <middle>Decay: <main><decay>",
                        " <secondary>\u25cf <middle>IP Relative: <main><ip>",
                        " <secondary>\u25cf <middle>Shadow: <main><shadow>");

        public static String PUNISHMENT_REMOVED =
                "<main><type> <middle>has been removed from <main><ladder> <middle>punishment ladder.";

        public static List<String> LIST =
                Arrays.asList("", "<middle>&lPunishments List", "<list>", "");

        public static String FORMAT =
                " <secondary>\u25cf <middle>#<order> <main><type> <muted>(Reason: <reason> - Duration: <duration> - IP: <ip> - Shadow: <shadow> - Decay: <decay>)";
    }

    public static class RANK {

        public static String ALREADY_EXISTS =
                "<main>\u2717 <middle>The rank <bright><rank> <middle>already exists.";

        public static String CREATED = "<middle>You have created rank <main><rank><middle>.";

        public static String CLONED =
                "<middle>You have cloned rank <main><rank> <middle>-> <main><newRank><middle>.";

        public static String DELETED = "<middle>You have deleted rank <main><rank><middle>.";

        public static String PRICE_UPDATED =
                "<middle>You have updated price of <main><rank> <middle>to <main><price><middle>.";

        public static String PRIORITY_UPDATED =
                "<middle>You have updated priority of <main><rank> <middle>to <main><priority><middle>.";

        public static String PLAYERLIST_PREFIX =
                "<middle>You have updated the player list prefix of <main><rank> <middle>to <main><prefix><middle>.";

        public static String COLOR_UPDATED =
                "<middle>You have updated the color of <main><rank> <middle>rank to <main><color>Color<middle>.";

        public static String CHATCOLOR_UPDATED =
                "<middle>You have updated the chat color of <main><rank> <middle>rank to <main><color>Color<middle>.";

        public static String PREFIX_UPDATED =
                "<middle>You have updated the prefix of <main><rank> <middle>rank to <main><prefix><middle>.";

        public static String SUFFIX_UPDATED =
                "<middle>You have updated the suffix of <main><rank> <middle>rank to <main><suffix><middle>.";

        public static String DISPLAY_UPDATED =
                "<middle>You have updated the display of <main><rank> <middle>rank to <main><displayName><middle>.";

        public static String NAME_UPDATED =
                "<middle>You have updated the name of <main><rank> <middle>rank to <main><name><middle>.";

        public static List<String> RANK_INFO =
                Arrays.asList(
                        "",
                        "<main><rank><middle>'s Information",
                        " <secondary>\u25cf <middle>UUID: <main><uuid>",
                        " <secondary>\u25cf <middle>Name: <main><name>",
                        " <secondary>\u25cf <middle>Scopes: <main><scopes>",
                        " <secondary>\u25cf <middle>Priority: <main><priority>",
                        " <secondary>\u25cf <middle>Grantable: <main><grantable>",
                        "",
                        " <secondary>\u25cf <middle>Prefix: <main><prefix> <muted>(Modern: <prefixModern>)",
                        " <secondary>\u25cf <middle>Playerlist Prefix: <main><playerList> <muted>(Modern: <playerListPrefixModern>)",
                        " <secondary>\u25cf <middle>Suffix: <main><suffix> <muted>(Modern: <suffixModern>)",
                        " <secondary>\u25cf <middle>Color: <main><color> <muted>(Modern: <colorModern>)",
                        " <secondary>\u25cf <middle>Price: <main><price>",
                        "",
                        " <secondary>\u25cf <middle>Staff: <main><staff>",
                        " <secondary>\u25cf <middle>Subscription: <main><subscription>",
                        " <secondary>\u25cf <middle>Visible: <main><visible>",
                        " <secondary>\u25cf <middle>Purchasable: <main><purchasable>",
                        " <secondary>\u25cf <middle>Default: <main><default>");

        public static class RANK_HOVER {

            public static String SET =
                    "<middle>You have updated the chat hover of <main><rank> <middle>rank.";

            public static String CLEARED =
                    "<middle>You have cleared the chat hover of <main><rank> <middle>rank.";

            public static String URL_SET =
                    "<middle>You have set the chat hover URL of <main><rank> <middle>rank to <main><url><middle>.";

            public static String URL_CLEARED =
                    "<middle>You have cleared the chat hover URL of <main><rank> <middle>rank.";

            public static String VIEW_HEADER =
                    "<middle>Hover preview for <main><rank> <middle>(hover the prefix below):";
        }

        public static class RANK_INHERITANCE {

            public static String ALREADY_HAS =
                    "<main>\u2717 <middle>The rank <bright><rank> <middle>already inherits from <bright><inheritance><middle>.";

            public static List<String> LOCKED =
                    Arrays.asList(
                            "<main><childRank> can not inherit from <rank> because of a dependency lock caused by <causedRank>.",
                            "<middle>",
                            "<main>A dependency lock means that rank A can not inherit rank B because rank A indirectly",
                            "<main>inherits rank B or vice-versa, which would recursively try to collect inheritance",
                            "<main>resulting in a lock, or a never-ending execution stack.");

            public static String DOESNT_HAVE =
                    "<main>\u2717 <middle>The rank <bright><rank> <middle>does not inherit from <bright><inheritance><middle>.";

            public static String ADDED =
                    "<middle>Rank <main><rank> <middle>now inherits from <main><inheritance><middle>.";

            public static String REMOVED =
                    "<middle>Rank <main><rank> <middle>no longer inherits from <main><inheritance><middle>.";

            public static String CLEARED = "&a\u2713 <middle>You have cleared all inheritances.";

            public static String ADDED_TO_ALL =
                    "<middle>You have made all ranks inherit from <main><inheritance><middle>.";

            public static String FROM_START =
                    "<middle>Made <main>all ranks <middle>inherit from the one before them.";
        }

        public static class RANK_PERMS {

            public static String COMMAND_DOESNT_EXIST =
                    "<main>\u2717 <middle>That command does not exist.";

            public static String COMMAND_DOESNT_HAVE_PERMISSION =
                    "<main>\u2717 <middle>That command does not have a permission or it hasn't been detected correctly.";

            public static String ALREADY_HAS_PERMISSION =
                    "<main>\u2717 <middle>The rank <bright><rank> <middle>already has <bright><perm> <middle>permission.";

            public static String DOESNT_HAVE_PERMISSION =
                    "<main>\u2717 <middle>The rank <bright><rank> <middle>does not have <bright><perm> <middle>permission.";

            public static String ADDED =
                    "<middle>You have added <main><perm> <middle>permission to <main><rank> <middle>rank.";

            public static String SET_SCOPES =
                    "<middle>You have set the scopes of <main><perm> <middle>permission to <main><scopes><middle>.";

            public static String REMOVED =
                    "<middle>You have remove <main><perm> <middle>permission from <main><rank> <middle>rank.";

            public static String CLEARED =
                    "<middle>You have cleared the permissions of <main><rank> <middle>rank.";
        }

        public static class RANK_SCOPES {

            public static String ALREADY_HAS =
                    "<main>\u2717 <middle>The rank <bright><rank> <middle>already has <bright><scope> <middle>scope.";

            public static String DOESNT_HAVE =
                    "<main>\u2717 <middle>The rank <bright><rank> <middle>does not have <bright><scope> <middle>scope.";

            public static String ADDED =
                    "<middle>You have added <main><scope> <middle>scope to <main><rank> <middle>rank.";

            public static String REMOVED =
                    "<middle>You have removed <main><scope> <middle>scope from <main><rank> <middle>rank.";
        }

        public static class RANK_STATE {

            public static String DEFAULT_ON =
                    "<middle>Rank <main><rank> <middle>is now a default rank.";

            public static String DEFAULT_OFF =
                    "<middle>Rank <main><rank> <middle>is no longer a default rank.";

            public static String STAFF_ON =
                    "<middle>Rank <main><rank> <middle>is now a staff rank.";

            public static String STAFF_OFF =
                    "<middle>Rank <main><rank> <middle>is no longer a staff rank.";

            public static String SUB_ON =
                    "<middle>Rank <main><rank> <middle>is now a subscription rank.";

            public static String SUB_OFF =
                    "<middle>Rank <main><rank> <middle>is no longer a subscription rank.";

            public static String PURCHASABLE_ON =
                    "<middle>Rank <main><rank> <middle>is now a purchasable rank.";

            public static String PURCHASABLE_OFF =
                    "<middle>Rank <main><rank> <middle>is no longer a purchasable rank.";

            public static String GRANTABLE_ON =
                    "<middle>Rank <main><rank> <middle>is now grantable.";

            public static String GRANTABLE_OFF =
                    "<middle>Rank <main><rank> <middle>is no longer grantable.";

            public static String VISIBLE_ON = "<middle>Rank <main><rank> <middle>is now visible.";

            public static String VISIBLE_OFF =
                    "<middle>Rank <main><rank> <middle>is no longer visible.";
        }

        @Comment(
                "The rank tooling that has no menu behind it: colours, links, importing and searching.")
        public static class TOOLS {

            public static String SHADOW_SET =
                    "&a\u2713 <middle>Set the shadow colour on <bright><rank><middle> to <bright><hex><middle>.";

            public static String SHADOW_CLEARED =
                    "&a\u2713 <middle>Cleared the shadow colour on <bright><rank><middle>.";

            public static String SHADOW_INVALID =
                    "<main>\u2717 <middle>'<hex>' is not a hex colour. Use <bright>#RRGGBB<middle>, or <bright>#RRGGBBAA<middle> to set the alpha.";

            public static String LINK_ADDED =
                    "&a\u2713 <middle>Added a server link to <bright><rank><middle>, now <bright><amount><middle>.";

            public static String LINK_REMOVED =
                    "&a\u2713 <middle>Removed <bright><link><middle> from <bright><rank><middle>.";

            public static String LINKS_CLEARED =
                    "&a\u2713 <middle>Cleared the server links on <bright><rank><middle>.";

            public static String LINK_INVALID_TYPE =
                    "<main>\u2717 <middle>'<type>' is not a link type. Use <bright>CUSTOM<middle> or one of: <bright><types>";

            public static String LINK_NEEDS_LABEL =
                    "<main>\u2717 <middle>A <bright>CUSTOM<middle> link needs a label: <bright>/rank links add <rank> CUSTOM <url> <label>";

            public static String LINK_NOT_STORED =
                    "<main>\u2717 <middle>That link could not be stored. Check the url.";

            public static String LINK_NOT_FOUND =
                    "<main>\u2717 <middle>There is no link <bright><index><middle>. Use <bright>/rank links list<middle> to see them.";

            public static String NOTHING_SET = "<bright><rank><main> has no <type> set.";

            public static String SEARCH_IN_PROGRESS =
                    "<main>\u2717 <middle>A search is already being performed. Please wait...";

            public static String SEARCH_FETCHING = "<muted>Fetching players...";

            public static String SEARCH_TOOK = "<muted>Fetching took <time>ms.";

            public static String EXPORTED =
                    "&a\u2713 <middle>All ranks have been exported to &eranks.json<middle>.";

            public static String IMPORTED =
                    "&a\u2713 <middle>All ranks have been successfully imported.";

            public static String FILE_MISSING =
                    "<main>\u2717 <middle>A &eranks.json<middle> file is not present.";

            public static String PRESET_FILE_MISSING =
                    "<main>\u2717 <middle>A &eranks-preset.json<middle> file is not present.";
        }
    }

    public static class REBOOT {

        public static String ALREADY_REBOOTING =
                "<main>\u2717 <middle>The server is already rebooting.";

        public static String NOT_REBOOTING = "<main>\u2717 <middle>The server is not rebooting.";

        public static String REBOOTING_IN =
                "<middle>The server will be rebooting in <main><time><middle>.";

        public static String CANCELLED =
                "<main>\u2717 <middle>The server reboot has been cancelled.";

        public static String SCHEDULED = "<middle>The server will reboot in <main><time><middle>.";

        public static String CANCELLED_STAFF =
                "&b[S] <muted>[<server>] <player> <middle>cancelled the reboot.";

        public static String SCHEDULED_STAFF =
                "&b[S] <muted>[<server>] <player> <middle>scheduled a reboot <muted>(<time>)<middle>.";

        public static String REBOOT_SCHEDULED_IN =
                "<middle>This server is scheduled to reboot in <main><time><middle>.";

        public static Boolean TITLE_ENABLED = true;

        public static String TITLE = "<main>&lServer Reboot";

        public static String SUBTITLE = "<middle>Rebooting in <main><time>";

        public static List<String> KICK = Arrays.asList("<main>The server is rebooting.");
    }

    public static class REPORT {

        public static String SENT = "&a\u2713 <middle>Your report has been sent to all staff.";

        @Comment("What a player is asked when they report for a reason of their own.")
        public static String CUSTOM_PROMPT =
                "&a\u2713 <middle>Type what you are reporting <target> for, or 'cancel' to stop.";

        public static String CUSTOM_DISABLED =
                "<main>\u2717 <middle>Reports need one of the reasons on the list.";

        public static String CUSTOM_MISSING_CATEGORY =
                "<main>\u2717 <middle>Custom reasons are misconfigured, tell an administrator.";

        public static String CANT_REPORT_SELF =
                "<main>\u2717 <middle>You're are not allowed to report yourself.";

        public static String CANT_REPORT_STAFF =
                "<main>\u2717 <middle>You're are not allowed to report staff members.";

        public static String MUTED =
                "<main>\u2717 <middle>You are currently report muted! Create a ticket on discord.server.rip for more information.";

        public static String REWARD =
                "&a\u2713 <middle>You recent report has been reviewed and handled. Thank you! :)";

        @Comment("Sent to whoever filed the report once staff are done with it, however it ended.")
        public static String REVIEWED =
                "&a\u2713 <middle>Your report on <target> has been reviewed. Thank you!";

        @Comment("Shown when the report category's ladder has no punishment left to give.")
        public static String NO_LADDER =
                "<main>\u2717 <middle>There are no available ladders for this punishment ladder.";

        @Comment({
            "How the report queue has been going, shown by /reports stats.",
            "Placeholders: <days>, <total>, <open>, <inProgress>, <resolved>, <punished>,",
            "<outstanding>, <response>, <resolution>, <handled>"
        })
        public static List<String> STATISTICS =
                Arrays.asList(
                        "<muted>&m----------------------------------------",
                        "<main>&lReports <main><muted>- <middle>the last <main><days> <middle>days",
                        "",
                        " <secondary>\u25cf <middle>Filed: <main><total> <muted>(<outstanding> still waiting)",
                        " <secondary>\u25cf <middle>Open: <main><open> <middle>| In progress: <main><inProgress>",
                        " <secondary>\u25cf <middle>Resolved: <main><resolved> <middle>| Punished: <main><punished>",
                        "",
                        " <secondary>\u25cf <middle>Picked up after: <main><response>",
                        " <secondary>\u25cf <middle>Closed after: <main><resolution>",
                        "",
                        "<main>&lHandled by",
                        "<handled>",
                        "<muted>&m----------------------------------------");

        @Comment("One line of the leaderboard, repeated under <handled>.")
        public static String STATISTICS_HANDLED =
                " <secondary>\u25cf <main><player><muted>: <middle><amount>";

        @Comment("Shown in place of a time nobody has produced yet.")
        public static String STATISTICS_NEVER = "<muted>n/a";

        @Comment("Shown in place of the leaderboard when nobody has closed a report yet.")
        public static String STATISTICS_NOBODY = " <secondary>\u25cf <middle>Nobody yet.";

        @Comment("Kick screen of the automatic mute a pile of chat reports triggers.")
        public static List<String> PENDING_REVIEW_MUTE =
                Arrays.asList(
                        "<secondary>&m--------------------------------------------------",
                        "<main>A report against you is currently under review.",
                        "<muted>Your mute will expire in <bright><duration><muted>.",
                        "",
                        "<muted>Find out more here: &b&nwww.yourserver.net/mutes",
                        "<muted>Mute ID: <bright><punishmentID>",
                        "<secondary>&m--------------------------------------------------");

        public static class CATEGORY {

            public static String DOESNT_EXIST =
                    "<main>\u2717 <middle>The report category &e<category><middle> doesn't exist.";

            public static String ALREADY_EXISTS =
                    "<main>\u2717 <middle>A report category with that id already exists.";

            public static String CREATED =
                    "&a\u2713 <middle>You have created the <bright><category><middle> report category.";

            public static String DELETED =
                    "&a\u2713 <middle>You have deleted the <bright><category><middle> report category.";

            public static String UPDATED =
                    "&a\u2713 <middle>You have updated the <bright><type><middle> of the report category <bright><category><middle>.";

            @Comment("Shown by /reports categories.")
            public static List<String> LIST =
                    Arrays.asList(
                            "",
                            "<main>Report Categories <secondary>(<amount>)",
                            "<categories>",
                            "<middle>Create and edit them with <main>/adminreport<middle>.",
                            "");

            public static String LIST_ENTRY =
                    " <rail>\u2503 <main><category> <middle>- <secondary><display> <middle>(ladder: <secondary><ladder><middle>)";

            public static String LIST_EMPTY =
                    "<middle>No report categories exist yet. Create one with <main>/adminreport create<middle>.";
        }
    }

    public static class REQUEST {

        public static String SENT = "&a\u2713 <middle>Your request has been sent to all staff.";

        public static String MUTED =
                "<main>\u2717 <middle>You are currently request muted! Create a ticket on discord.server.rip for more information.";

        public static String MUTE =
                "&e\u2691 <middle>You have been request muted. You can no longer use /report or /request.";

        public static String UNMUTE =
                "&e\u2691 <middle>You have been request unmuted. You can now use /report and /request again.";

        public static String MUTE_SENDER = "&a\u2713 <middle>You have request muted &6<player>&e.";

        public static String UNMUTE_SENDER =
                "&a\u2713 <middle>You have request unmuted &6<player>&e.";
    }

    @Comment("Pushing resource packs with /resourcepack.")
    public static class RESOURCE_PACK {

        public static String PUSHED =
                "&a\u2713 <middle>Pushed the pack to <bright><player><middle>.";

        public static String PUSHED_ALL =
                "&a\u2713 <middle>Pushed the pack to <bright><amount><middle> player(s). Remove it with <bright>/pack remove <url><middle>.";

        public static String PUSH_FAILED =
                "<main>\u2717 <middle>Could not push the pack to <bright><player><middle>. Their client may be older than 1.20.3.";

        public static String REMOVED_ALL =
                "&a\u2713 <middle>Removed the pack from <bright><amount><middle> player(s).";

        public static String CLEARED_ALL =
                "&a\u2713 <middle>Cleared every pack for <bright><amount><middle> player(s).";
    }

    @Comment("Output of /alts ips. <player>, <ip>, <name>, <lastSeen> placeholders")
    public static class SEARCH_IPS {

        public static String NO_IPS = "<main>\u2717 <middle><player> has no known IPs.";

        public static String HEADER = "<muted>&m--------------------------------------------------";

        public static String TITLE = "&aIP history for <bright><player>&a:";

        public static String IP_HEADER = "&e[IP: <bright><ip>&e]";

        public static String NO_LOGINS = "  <muted>No login records.";

        public static String ENTRY =
                "  <color><name> <muted>(last seen: <bright><lastSeen><muted>)";

        @Comment("Color prefix applied to <color> when the entry is the search target")
        public static String ENTRY_COLOR_TARGET = "&a";

        @Comment("Color prefix applied to <color> when the entry is anyone else")
        public static String ENTRY_COLOR_OTHER = "<bright>";

        public static String FOOTER = "<muted>&m--------------------------------------------------";

        @Comment("DateFormat pattern for the <lastSeen> placeholder")
        public static String DATE_FORMAT = "yyyy-MM-dd";
    }

    public static class SECURITY {

        public static String NOT_SECURITY_USER =
                "<main>\u2717 <middle>You need to be a security user to use this.";

        public static String NOT_ADMIN =
                "<main>\u2717 <middle>You need to be an admin security user to use this.";

        public static String ADDED =
                "<middle>You have added <main><player> <middle>as a security user.";

        public static String ALREADY_ADDED =
                "<main>\u2717 <middle><player> is already a security user.";

        public static String REMOVED =
                "<middle>You have removed <main><player><middle>'s security user.";

        public static String NOT_IN_LIST = "<main>\u2717 <middle><player> is not a security user.";

        public static String LIST_FORMAT =
                "<main><player> <muted>(Added By: <issuedBy>, Admin: <middle><admin><muted>)";

        @Comment({
            "Sent to admins on every server when the security check punishes somebody.",
            "Available Placeholders: <server> - Gets the server display name, <target> - Who was punished,",
            "<actor> - Who acted on them, <rank> - The rank involved"
        })
        public static String ALERT_GRANT =
                "&b[S] <muted>[<server>] <main><actor> <middle>tried to grant <main><rank> <middle>to <main><target> <middle>without verification.";

        public static String ALERT_GRANT_JOIN =
                "&b[S] <muted>[<server>] <main><target> <middle>joined holding <main><rank><middle>, granted by an unverified account.";

        public static String ALERT_OP_JOIN =
                "&b[S] <muted>[<server>] <main><target> <middle>joined opped without verification.";

        public static String ALERT_OP =
                "&b[S] <muted>[<server>] <main><actor> <middle>tried to op <main><target> <middle>without verification.";

        public static String ALERT_OP_UNKNOWN =
                "&b[S] <muted>[<server>] <main><actor> <middle>tried to op <main><target><middle>, who has no profile.";
    }

    public static class SERVER_INFO {

        public static List<String> INFO =
                Arrays.asList(
                        "",
                        "<main>&l<server> Information",
                        " <secondary>\u25cf <middle>Display Name: <main><display>",
                        " <secondary>\u25cf <middle>Group: <main><group>",
                        " <secondary>\u25cf <middle>Region: <main><region>",
                        " <secondary>\u25cf <middle>TPS: <main><tps>",
                        " <secondary>\u25cf <middle>Players: <main><count> / <max>",
                        " <secondary>\u25cf <middle>Whitelisted: <main><whitelisted>",
                        "");

        public static List<String> SERVERS =
                Arrays.asList(
                        "",
                        "<main>&lAvailable Servers:",
                        "  <secondary>\u25cf <middle><servers>",
                        "");

        public static String DELETE = "<middle>You have deleted the <main><server> <middle>server.";
    }

    public static class SOCIALS {

        public static String SYNC_DISCORD =
                "<main>\u2717 <middle>Please use /sync to update your discord.";

        public static String ENTER_URL =
                "<middle>Type your <main><social> <middle>social media url.";

        public static String UPDATED =
                "<middle>You have updated your <main><social> <middle>to <main><url><middle>.";

        public static String INVALID_URL = "<main>\u2717 <middle>You have entered an invalid url.";
    }

    public static class STAFF {

        @Comment({
            "Available Placeholders: <server> - Gets the servername, <serverDisplay> - Gets the server displayname",
            "<proxy> - Gets the proxy name, <proxyDisplay> - Gets the proxy display name"
        })
        public static String STAFF_CONNECT =
                "&b[S] <player> <middle>connected to <main><server> <middle>through <main><proxy><middle>.";

        @Comment({
            "Available Placeholders: <server> - Gets the servername, <serverDisplay> - Gets the server displayname",
            "<from> - Gets the server name the player joined from, <fromDisplay> - Gets the server display name the player joined from"
        })
        public static String STAFF_SWITCH =
                "&b[S] <player> <middle>joined <main><server> <middle>from <main><from><middle>.";

        @Comment(
                "Available Placeholders: <server> - Gets the servername, <serverDisplay> - Gets the server displayname")
        public static String STAFF_DISCONNECT =
                "&b[S] <player> <middle>disconnected from <main><server><middle>.";

        public static String PUNISHMENT_EVADING_ALERT =
                "&b[S] <player> <main>is punishment evading! <muted>(&bAlts<muted>: &b<alts><muted>)";

        public static String RANK_DISGUISE_ALERT =
                "&b[S] <muted>[<server>] <player> <middle>rank disguised <target> as <rank>.";

        public static String RANK_UNDISGUISE_ALERT =
                "&b[S] <muted>[<server>] <player> <middle>rank undisguised <target>.";

        public static String DISGUISE_ALERT =
                "&b[S] <muted>[<server>] <realName> <middle>disguised as <disguiseName>.";

        public static String REDISGUISE_ALERT =
                "&b[S] <muted>[<server>] <realName> <middle>redisguised as <disguiseName>.";

        public static String UNDISGUISE_ALERT =
                "&b[S] <muted>[<server>] <realName> <middle>undisguised from <disguiseName>.";

        public static String CLEAR_CHAT_ALERT =
                "&b[S] <muted>[<server>] <player> <middle>has cleared the chat.";

        public static String MUTE_CHAT_ALERT =
                "&b[S] <muted>[<server>] <player> <middle>has muted the chat.";

        public static String UNMUTE_CHAT_ALERT =
                "&b[S] <muted>[<server>] <player> <middle>has unmuted the chat.";

        public static String SLOW_CHAT_ALERT =
                "&b[S] <muted>[<server>] <player> <middle>has slowed the chat to <main><seconds>s<middle>.";

        public static String FREEZE_ALERT =
                "&b[S] <muted>[<server>] <player> <middle>froze <target>.";

        public static String UNFREEZE_ALERT =
                "&b[S] <muted>[<server>] <player> <middle>unfroze <target>.";

        public static String LEFT_WHILE_FROZEN_ALERT =
                "&b[S] <muted>[<server>] <main><name> <middle>left whilst frozen.";

        public static String WATCHLIST_ALERT =
                "&b[S] <main><player> <middle>has reached <main><reports> <middle>reports and is on the watchlist.";

        public static String PANIC_ALERT = "&b[S] <muted>[<server>] <player> <middle>has panicked!";

        public static String UNPANIC_ALERT =
                "&b[S] <muted>[<server>] <player> <middle>has been unpanicked by <sender>.";

        public static String REPORT_ALERT =
                "&b[Report] &3[<player_server>] <main><player> <middle>reported <main><target><disguised><muted> [<main><reportCount><muted>] <middle>for <reason>.";

        public static String REQUEST_ALERT =
                "&b[Request] &3[<player_server>] <player><disguised> <middle>requested staff help for <main><reason><middle>.";

        @Comment("Appended to the report and request alerts when the player is disguised.")
        public static String DISGUISED_SUFFIX = " <muted>(Disguised)";

        public static String SERVER_STARTED =
                "&b[A] <main><server> <muted>(<region>) <middle>has started in <main><time><middle>.";

        public static String SERVER_STOPPED =
                "&b[A] <main><server> <muted>(<region>) <middle>has stopped.";

        public static String AM_I_VANISHED_TRUE = "&a\u2713 <middle>You are currently vanished.";

        public static String AM_I_VANISHED_FALSE =
                "<main>\u2717 <middle>You are not currently vanished.";

        @Comment("Using &h in the message removes the prefix and just sends the message.")
        public static String ALERT = "<main>&lAlert <secondary>\u25cf <bright><message>";

        public static String SOCIAL_SPY_ALERT =
                "&b[A] <muted>[<server>] <sender> -> <middle><target><muted>: <bright><message>";

        public static String ADMIN_ALERT = "&b[A] <main><player><muted>: <action>";

        public static String ADMIN_PREFIX = "<main>&l\u2691 ";

        public static String ADMIN_HOVER =
                "&a\u00bb <middle>Click to open this users admin profile.";

        public static String ADMIN_COMMAND = "/adminprofile <player>";

        public static boolean DELETE_MESSAGE_ENABLED = true;

        public static String DELETE_MESSAGE_PREFIX = "&b[X] ";

        public static String DELETE_MESSAGE_HOVER =
                "<main>\u00bb <middle>Click to delete this message from the chat.";

        public static List<String> USER_INFO =
                Arrays.asList(
                        "<main>&lProfile Information",
                        " <secondary>\u25cf <middle>Target: <main><player>",
                        " <secondary>\u25cf <middle>UUID: <main><uuid>",
                        " <secondary>\u25cf <middle>Previous Names: <main><nameHistory>",
                        " <secondary>\u25cf <middle>Permissions: <main><permissions>",
                        "",
                        " <secondary>\u25cf <middle>Current Rank: <main><rank>",
                        " <secondary>\u25cf <middle>Active Grants: <main><activeGrants>",
                        " <secondary>\u25cf <middle>Punishments: <main><activePunishments> <muted>/ <main><totalPunishments>",
                        "",
                        " <secondary>\u25cf <middle>Logins: <main><logins>",
                        " <secondary>\u25cf <middle>First Login: <main><firstLogin>",
                        " <secondary>\u25cf <middle>Last Login: <main><lastLogin> <muted>[<lastSeen>]",
                        " <secondary>\u25cf <middle>Connected To: <main><server>");

        public static List<String> USER_STAFF_INFO =
                Arrays.asList(
                        "<player><main>&l's Profile Information",
                        " <secondary>\u25cf <middle>UUID: <main><uuid>",
                        " <secondary>\u25cf <middle>Rank: <main><rank>",
                        "",
                        "<main>&lLogins",
                        " <secondary>\u25cf <middle>Logins: <main><logins>",
                        " <secondary>\u25cf <middle>First Login: <main><firstLogin>",
                        " <secondary>\u25cf <middle>Last Login: <main><lastLogin> <muted>[<lastSeen>]",
                        " <secondary>\u25cf <middle>Daily Logins: <main><dailyAverage>",
                        " <secondary>\u25cf <middle>Average Time Logged In: <main><timeAverage>",
                        "",
                        "<main>&lPunishments <main><muted>[Executed/Removed]",
                        " <secondary>\u25cf <middle>Total: <main><executed>/<removed>",
                        " <secondary>\u25cf <middle>Mutes: <main><mutes>/<mutesRemoved>",
                        " <secondary>\u25cf <middle>Bans: <main><bans>/<bansRemoved>",
                        " <secondary>\u25cf <middle>Blacklists: <main><blacklists>/<blacklistsRemoved>");

        public static String DELETED = "<secondary>You have deleted <target>'s profile.";

        public static String DELETE_CANCELLED =
                "<main>\u2717 <middle>Cancelled deletion of <player>.";

        @Comment("What /user gdpr export says once it has the data together.")
        public static String EXPORT_PASTED = "<secondary>Link to data export: <link>";

        public static String EXPORT_PASSWORD = "<secondary>Password: <password>";

        public static String EXPORT_WRITTEN =
                "<secondary>Wrote the data export to <main><path><secondary>.";

        public static String EXPORT_FAILED =
                "<main>\u2717 <middle>Could not write the data export. Check console.";

        public static class ADMIN_ALERTS {

            public static String BLOCKED_COMMAND =
                    "tried to execute a blocked command: <main><command>";

            public static String SELF_VANISH_ON = "<middle>enabled vanish";

            public static String SELF_VANISH_OFF = "<middle>disabled vanish";

            public static String VANISH_OTHER_ON = "<middle>enabled vanish for <target>";

            public static String VANISH_OTHER_OFF = "<middle>disabled vanish for <target>";

            public static String SELF_MOD_ON = "<middle>enabled mod mode";

            public static String SELF_MOD_OFF = "<middle>disabled mod mode";

            public static String MOD_OTHER_ON = "<middle>enabled mod mode for <target>";

            public static String MOD_OTHER_OFF = "<middle>disabled mod mode for <target>";
        }

        public static class ROLLBACK {

            public static String WIPING = "<middle>Wiping all <main><type><middle>, please wait...";

            public static String WIPED =
                    "<middle>Successfully wiped <main><amount> <type> <middle>by <main><name><bright>. <muted>(&a<active> Active<muted>, <main><inactive> Inactive<muted>)";

            public static String NO_HISTORY =
                    "<main>\u2717 <middle>That player doesn't have <secondary><type> <middle>performed.";
        }

        public static class WARNS {

            public static String FORMAT =
                    "<active> <middle>#<id>: <main><reason> <muted>[<date> - Expire: <expire>]";

            public static String ACTIVE = "&a\u2713 <middle>(Active)";

            public static String INACTIVE = "<main>\u2717 <middle>(Inactive)";

            public static String REMOVE_TEXT = " <main>&l[\u2718]";

            public static String REMOVE_HOVER =
                    "<main>\u00bb <middle>Click to remove the <bright>#<id> <middle>warning.";
        }
    }

    public static class STAFF_ACTIVITY {

        public static List<String> FORMAT =
                Arrays.asList(
                        "",
                        "<player><main>'s Staff Activity",
                        " <secondary>\u25cf <middle>Playtime: <main><playtime>",
                        " <secondary>\u25cf <middle>AFK Time: <main><afk>",
                        " <secondary>\u25cf <middle>Punishments Issued: <main><punishments>",
                        " <secondary>\u25cf <middle>Voided In Error: <main><voided> <secondary>(<voidrate>)",
                        " <secondary>\u25cf <middle>Bans: <main><bans> <middle>Mutes: <main><mutes> <middle>Kicks: <main><kicks> <middle>Warns: <main><warns>",
                        " <secondary>\u25cf <middle>Punishments Removed: <main><removed>",
                        " <secondary>\u25cf <middle>Reports Handled: <main><reportshandled> <secondary>(avg <reportspeed>)",
                        " <secondary>\u25cf <middle>Server Switches: <main><switches>",
                        " <secondary>\u25cf <middle>Most Active Server: <main><activeserver>",
                        " <secondary>\u25cf <middle>Peak Online Hour: <main><peakonline>",
                        " <secondary>\u25cf <middle>Total Logins: <main><logins>",
                        " <secondary>\u25cf <middle>Last Login: <main><lastlogin>",
                        " <secondary>\u25cf <middle>Last Leave: <main><lastleave>",
                        "");
    }

    public static class STAFF_PROTECTION {

        public static String ALERT =
                "&b&l[Security] &b<bright><staff> <muted>issued <bright><count> <muted>punishments within <bright><window>s<muted>.";

        public static String PANIC_ALERT =
                "&b&l[Security] &b<bright><staff> <muted>pulled the panic alarm, their account may be compromised.";
    }

    public static class TAGS {

        public static String EXPORTED =
                "&a\u2713 <middle>All tags have been exported to &etags.json<middle>.";

        public static String IMPORTED =
                "&a\u2713 <middle>All tags have been successfully imported.";

        public static String FILE_MISSING =
                "<main>\u2717 <middle>A &etags.json<middle> file is not present.";

        public static String DELETED = "<middle>You have deleted <main><tag> <middle>tag.";

        public static String CREATED = "<middle>You have created <main><tag> <middle>tag.";

        public static String ALREADY_EXISTS =
                "<main>\u2717 <middle>Tag <bright><tag> <middle>already exists.";

        public static String PRIORITY_UPDATED =
                "<middle>You have updated price of <main><tag> <middle>tag to <main><priority><middle>.";

        public static String PRICE_UPDATED =
                "<middle>You have updated price of <main><tag> <middle>tag to <main><price><middle>.";

        public static String PREFIX_UPDATED =
                "<middle>You have updated prefix of <main><tag> <middle>tag to <main><prefix><middle>.";

        public static String COLOR_UPDATED =
                "<middle>You have updated color of <main><tag> <middle>tag to <main><color>Color<middle>.";

        public static String PURCHASABLE_UPDATED =
                "<middle>You have updated purchasable status of <main><tag> <middle>tag to <main><status><middle>.";

        public static String TAG_RESET = "&a\u2713 <middle>You have reset your tag.";

        public static String TAG_SELECTED =
                "<middle>You have updated your tag to <main><tag><middle>.";

        public static String NO_TAG_SELECTED =
                "<main>\u2717 <middle>You don't have a tag selected.";

        public static String ALREADY_SELECTED =
                "<main>\u2717 <middle>You already have a tag selected.";

        public static String NO_PERMISSION = "<main>\u2717 <middle>You don't own this tag.";

        public static String NOT_ACTIVE =
                "<main>\u2717 <middle>The <tag> tag is not in season right now.";

        public static String SCHEDULED =
                "<middle>The <main><tag> <middle>tag is now active from <main><from> <middle>until <main><until><middle>.";

        public static String SCHEDULE_CLEARED =
                "<middle>The <main><tag> <middle>tag is no longer scheduled.";

        public static String INVALID_SCHEDULE =
                "<main>\u2717 <middle>Schedule bounds are MM-dd for a yearly window or yyyy-MM-dd for a one-off.";

        public static class CATEGORY {

            public static String ALREADY_EXISTS =
                    "<main>\u2717 <middle>Category <bright><tag> <middle>already exists.";

            public static String CANT_DELETE_DEFAULT =
                    "<main>\u2717 <middle>You can not delete default category.";

            public static String CREATED = "<middle>You have created <main><tag> <middle>category.";

            public static String DELETED = "<middle>You have deleted <main><tag> <middle>category.";

            public static String DEFAULT_ON =
                    "<middle>Category <main><category> <middle>is now default.";

            public static String DEFAULT_OFF =
                    "<middle>Category <main><category> <middle>is no longer default.";

            public static String VISIBLE =
                    "<middle>Category <main><category> <middle>is now visible.";

            public static String INVISBLE =
                    "<middle>Category <main><category> <middle>is no longer visible.";

            public static String PRIORITY =
                    "<middle>Updated priority of <main><tag> <middle>category to <main><priority><middle>.";

            public static String TAG_ADDED =
                    "<middle>Added <main><tag> <middle>to <main><category> <middle>category.";

            public static String TAG_REMOVED =
                    "<middle>Removed <main><tag> <middle>from <main><category> <middle>category.";

            public static String ALREADY_HAS_TAG =
                    "<main>\u2717 <middle>Tag <bright><tag> <middle>is already part of <bright><category> <middle>category.";

            public static String DOESNT_HAVE_TAG =
                    "<main>\u2717 <middle>Tag <bright><tag> <middle>is not a part of <bright><category> <middle>category.";
        }
    }

    public static class TIMELINE {

        public static String TIMELINE = "<main>Timeline:";

        public static String PARTED_WAYS =
                "<player> <middle>parted ways with the staff team on <main><date><middle>.";

        public static String HELPED_FOR =
                "<player> <middle>helped the staff team for <main><time><middle>.";

        public static String HELPING_FOR =
                "<player> <middle>has been helping the staff team for <main><time><middle>.";

        public static String JOINED =
                "<player> <middle>joined the staff team on <main><date> <middle>and was given <rank> by <granter>.";

        public static String PROMOTED =
                "<player> <middle>was &apromoted to <rank> <middle>on <main><date> <middle>by <granter>.";

        public static String DEMOTED =
                "<player> <middle>was <main>demoted from <rank> <middle>on <main><date> <middle>by <granter>.";

        public static String NEVER_BEEN_STAFF =
                "<main>\u2717 <middle>That player has never been apart of the staff team.";
    }

    @Comment("Moving players to another server with /transfer.")
    public static class TRANSFER {

        public static String DISABLED = "<main>\u2717 <middle>Transfers are disabled.";

        public static String SERVER_TOO_OLD =
                "<main>\u2717 <middle>Transfers need Minecraft 1.20.5 or newer on this server.";

        public static String CLIENT_TOO_OLD =
                "<main>\u2717 <middle><player> is on a client too old to be transferred.";

        public static String INVALID_PORT = "<main>\u2717 <middle>That port is not valid.";

        public static String NOT_ALLOWED =
                "<main>\u2717 <middle><host> is not in the transfer allowlist.";

        public static String SENDING =
                "&a\u2713 <middle>Transferring <bright><player><middle> to <bright><host><middle>.";

        public static String FAILED = "<main>\u2717 <middle>The transfer could not be sent.";
    }

    public static class VANISH {

        public static String ENABLED = "&a\u2713 <middle>You've enabled vanish.";

        public static String DISABLED = "<main>\u2717 <middle>You've disabled vanish.";

        public static String ENABLED_OTHER = "&a\u2713 <middle>You've enabled vanish for <player>.";

        public static String DISABLED_OTHER =
                "<main>\u2717 <middle>You've disabled vanish for <player>.";

        public static String AUTOMATIC =
                "&e\u2691 <middle>&lYou've automatically been put into vanish.";

        public static String MUST_UNVANISH =
                "<main>\u2717 <middle>You can't go out of modmode while being vanished.";

        public static String HIDE_STAFF = "&a\u2713 <middle>You've hidden staff.";

        public static String SHOW_STAFF = "<main>\u2717 <middle>You've shown staff.";

        public static String SILENT_CHEST = "<muted>Opening a chest silently...";

        public static String SILENT_CONTAINER = "<muted>Opening container silently...";

        public static String SILENT_ENDER_CHEST =
                "<main>\u2717 <middle>You're vanished and cannot open this container.";
    }

    public static class WHITELIST {

        public static String NEED_ACCESS_TYPE =
                "<main>\u2717 <middle>You must have at least one access type enabled.";

        public static List<String> RANK_KICK_MESSAGE =
                Arrays.asList(
                        "",
                        "<main>The network is currently whitelisted",
                        "<main>Additional info may be found on our discord.",
                        "",
                        "<main>The server is for <rank> rank and higher only.");

        public static List<String> UUID_KICK_MESSAGE =
                Arrays.asList(
                        "",
                        "<main>The network is currently whitelisted",
                        "<main>Additional info may be found on our discord.");

        public static String ENABLED = "&a\u2713 <middle>You have enabled the whitelist.";

        public static String DISABLED = "<main>\u2717 <middle>You have disabled the whitelist.";

        public static String ACCESS_RANK =
                "<middle>Updated whitelist access rank to <main><rank><middle>.";

        public static String ACCESS_TYPE_ON = "<middle>Enabled <main><type> <middle>access type.";

        public static String ACCESS_TYPE_OFF = "<middle>Disabled <main><type> <middle>access type.";

        public static String ALREADY_ADDED =
                "<main>\u2717 <middle>Player <main><player> <middle>is already added to the whitelist.";

        public static String NOT_ADDED =
                "<main>\u2717 <middle>Player <main><player> <middle>is not added to the whitelist.";

        public static String DOESNT_EXIST =
                "<main>\u2717 <middle>Player <main><player> <middle>does not exist.";

        public static String ADDED_PERM = "<middle>Added <main><player> <middle>to the whitelist.";

        public static String ADDED_TEMP =
                "<middle>Temporarily added <main><player> <middle>to the whitelist.";

        public static String REMOVED = "<middle>Removed <main><player> <middle>from the whitelist.";

        public static List<String> STATUS =
                Arrays.asList(
                        "<main>&lWhitelist Status",
                        " <secondary>\u25cf <middle>Enabled: <enabled>",
                        " <secondary>\u25cf <middle>Access Types: <main><accessTypes>",
                        " <secondary>\u25cf <middle>Access Rank: <main><rank>",
                        " <secondary>\u25cf <middle>Whitelisted Users: <main><players>");

        public static String PLAYER_FORMAT = "<main><player>";

        public static String PLAYER_TEMPORARY_FORMAT = "<main><player> <secondary>(Temp)";
    }
}
