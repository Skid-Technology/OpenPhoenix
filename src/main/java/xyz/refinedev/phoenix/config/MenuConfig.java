package xyz.refinedev.phoenix.config;

import xyz.refinedev.lib.config.ConfigHandler;
import xyz.refinedev.lib.config.StaticConfig;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MenuConfig extends StaticConfig {

    @Ignore public static MenuConfig INSTANCE;

    public MenuConfig(ConfigHandler configHandler) {
        super(new File("plugins/Phoenix/menus.yml"), configHandler);
        INSTANCE = this;
    }

    public static class ADMIN_PROFILE {

        public static Integer MENU_SIZE = 45;

        public static String TITLE = "<main>&lPROFILE<main> <rail>\u00bb <middle><player>";

        public static String TRUE = "&atrue.";

        public static String FALSE = "<main>false.";

        public static String DISGUISED = " <muted>(Disguised)";

        public static Boolean USE_HEAD_AS_ITEM = true;

        public static String PROFILE_ITEM = "SKULL_ITEM";

        public static Integer PROFILE_ITEM_DAMAGE = 0;

        public static Integer PROFILE_SLOT = 4;

        public static String PROFILE_NAME = "<profile><disguised>";

        public static String PROFILE_NO_DISGUISED_NAME =
                " <rail>\u2503 <muted>Real Name: <secondary><name>";

        public static String PUNISHMENT_TYPE_NONE = "<muted>Not Punished";

        public static List<String> PROFILE_LORE =
                Arrays.asList(
                        "<noDisguiseName>",
                        " <rail>\u2503 <muted>Rank: <secondary><rank> <muted>(<rankDuration>)",
                        " <rail>\u2503 <muted>Punished: <punished> (<punishmentType>)",
                        " <rail>\u2503 <middle>2FA: <2fa>",
                        " <rail>\u2503 <muted>Security: <security>",
                        "",
                        "<main>Login Details",
                        " <rail>\u2503 <muted>First Login: <secondary><firstLogin>",
                        " <rail>\u2503 <muted>Last Login: <secondary><lastLogin>",
                        " <rail>\u2503 <muted>Total Logins: <secondary><logins>");

        public static class ADMIN_SOCIAL_MEDIA_BUTTON {

            public static Boolean ENABLED = true;

            public static Integer SLOT = 15;

            public static String NAME = "<main>&lSocial Media";

            public static List<String> LORE =
                    Arrays.asList(
                            "",
                            "&a\u00bb <middle>Click to view social media of <bright><target><middle>.");

            public static String ITEM = "PRISMARINE_SHARD";

            public static Integer DURABILITY = 0;
        }

        public static class ALTS_BUTTON {

            public static Boolean ENABLED = true;

            public static Integer SLOT = 30;

            public static String NAME = "<main>&lAlts";

            public static List<String> LORE =
                    Arrays.asList(
                            "",
                            " <rail>\u2503 <muted>Linked Accounts: <secondary><alts>",
                            "",
                            "&a\u00bb <middle>Click to view alts of <bright><target><middle>.");

            public static String ITEM = "IRON_AXE";

            public static Integer DURABILITY = 0;
        }

        public static class CHAT_LOGS_BUTTON {

            public static Boolean ENABLED = true;

            public static Integer SLOT = 24;

            public static String NAME = "<main>&lChat Logs";

            public static List<String> LORE =
                    Arrays.asList(
                            "",
                            "&a\u00bb <middle>Click to view the chat logs of <bright><target><middle>.");

            public static String ITEM = "WRITABLE_BOOK";

            public static Integer DURABILITY = 0;
        }

        public static class CHAT_SNAPSHOT_BUTTON {

            public static Boolean ENABLED = true;

            public static Integer SLOT = 25;

            public static String NAME = "<main>&lChat Snapshots";

            public static List<String> LORE =
                    Arrays.asList(
                            "",
                            "&a\u00bb <middle>Click to view chat snapshots of <bright><target><middle>.");

            public static String ITEM = "BOOK";

            public static Integer DURABILITY = 0;
        }

        public static class COMMAND_LOGS_BUTTON {

            public static Boolean ENABLED = true;

            public static Integer SLOT = 23;

            public static String NAME = "<main>&lCommand Logs";

            public static List<String> LORE =
                    Arrays.asList(
                            "",
                            "&a\u00bb <middle>Click to see command logs of <bright><target><middle>.");

            public static String ITEM = "BOOK_AND_QUILL";

            public static Integer DURABILITY = 0;
        }

        public static class GRANTS_BUTTON {

            public static Boolean ENABLED = true;

            public static Integer SLOT = 11;

            public static String NAME = "<main>&lGrants";

            public static List<String> LORE =
                    Arrays.asList(
                            "",
                            " <rail>\u2503 <muted>Active: <secondary><activeGrants>",
                            " <rail>\u2503 <muted>Total: <secondary><grants>",
                            "",
                            "&a\u00bb <middle>Click to view grants of <bright><target><middle>.");

            public static String ITEM = "INK_SACK";

            public static Integer DURABILITY = 10;
        }

        public static class GRANT_BUTTON {

            public static Boolean ENABLED = true;

            public static Integer SLOT = 39;

            public static String NAME = "<main>&lGrant";

            public static List<String> LORE =
                    Arrays.asList("", "&a\u00bb <middle>Click to grant <bright><target><middle>.");

            public static String ITEM = "WOOL";

            public static Integer DURABILITY = 5;
        }

        public static class GRANT_HISTORY_BUTTON {

            public static Boolean ENABLED = true;

            public static Integer SLOT = 12;

            public static String NAME = "<main>&lGrant History";

            public static List<String> LORE =
                    Arrays.asList(
                            "",
                            " <rail>\u2503 <muted>Grants: <secondary><grants>",
                            "",
                            "&a\u00bb <middle>Click to view grant history of <bright><target><middle>.");

            public static String ITEM = "CARPET";

            public static Integer DURABILITY = 5;
        }

        public static class HISTORY_BUTTON {

            public static Boolean ENABLED = true;

            public static Integer SLOT = 19;

            public static String NAME = "<main>&lHistory";

            public static List<String> LORE =
                    Arrays.asList(
                            "",
                            " <rail>\u2503 <muted>Punishments: <secondary><punishments>",
                            " <rail>\u2503 <muted>Still Active: <secondary><activePunishments>",
                            "",
                            "&a\u00bb <middle>Click to view history of <bright><target><middle>.");

            public static String ITEM = "INK_SACK";

            public static Integer DURABILITY = 1;
        }

        public static class LOCATION_LOG_BUTTON {

            public static Boolean ENABLED = true;

            public static Integer SLOT = 32;

            public static String NAME = "<main>&lLocation Log";

            public static List<String> LORE =
                    Arrays.asList(
                            "",
                            "&a\u00bb <middle>Click to see where <bright><target><middle> has connected from.");

            public static String ITEM = "COMPASS";

            public static Integer DURABILITY = 0;
        }

        public static class NOTES_BUTTON {

            public static Boolean ENABLED = true;

            public static Integer SLOT = 16;

            public static String NAME = "<main>&lNotes";

            public static List<String> LORE =
                    Arrays.asList(
                            "",
                            " <rail>\u2503 <muted>Notes: <secondary><notes>",
                            "",
                            "&a\u00bb <middle>Click to view notes of <bright><target><middle>.");

            public static String ITEM = "PAPER";

            public static Integer DURABILITY = 0;
        }

        public static class PERMISSIONS_BUTTON {

            public static Boolean ENABLED = true;

            public static Integer SLOT = 10;

            public static String NAME = "<main>&lPermissions";

            public static List<String> LORE =
                    Arrays.asList(
                            "",
                            " <rail>\u2503 <muted>Permissions: <secondary><permissions>",
                            "",
                            "&a\u00bb <middle>Click to view permissions of <bright><target><middle>.");

            public static String ITEM = "ARROW";

            public static Integer DURABILITY = 0;
        }

        public static class PLAYTIME_BUTTON {

            public static Boolean ENABLED = true;

            public static Integer SLOT = 14;

            public static String NAME = "<main>&lPlaytime";

            public static List<String> LORE =
                    Arrays.asList(
                            "",
                            " <rail>\u2503 <muted>Playtime: <secondary><playtime>",
                            "",
                            "&a\u00bb <middle>Click to view the playtime of <bright><target><middle>.");

            public static String ITEM = "CLOCK";

            public static Integer DURABILITY = 0;
        }

        public static class PUNISH_BUTTON {

            public static Boolean ENABLED = true;

            public static Integer SLOT = 41;

            public static String NAME = "<main>&lPunish";

            public static List<String> LORE =
                    Arrays.asList("", "&a\u00bb <middle>Click to punish <bright><target><middle>.");

            public static String ITEM = "WOOL";

            public static Integer DURABILITY = 14;
        }

        public static class SEARCH_IPS_BUTTON {

            public static Boolean ENABLED = true;

            public static Integer SLOT = 31;

            public static String NAME = "<main>&lAddress Search";

            public static List<String> LORE =
                    Arrays.asList(
                            "",
                            "&a\u00bb <middle>Click to see every address <bright><target><middle> has used.");

            public static String ITEM = "TRIPWIRE_HOOK";

            public static Integer DURABILITY = 0;
        }

        @Comment({
            "The heading on the left of each row. Purely a label -- clicking one does nothing.",
            "Turn them off for the plain grid."
        })
        public static class SECTIONS {

            public static Boolean ENABLED = true;

            public static String ITEM = "STAINED_GLASS_PANE";

            public static Integer ACCOUNT_SLOT = 9;

            public static Integer ACCOUNT_DURABILITY = 5;

            public static String ACCOUNT_NAME = "<main>&lAccount";

            public static Integer RECORD_SLOT = 18;

            public static Integer RECORD_DURABILITY = 4;

            public static String RECORD_NAME = "<main>&lRecord";

            public static Integer CHECKS_SLOT = 27;

            public static Integer CHECKS_DURABILITY = 3;

            public static String CHECKS_NAME = "<main>&lChecks";

            public static Integer ACTIONS_SLOT = 36;

            public static Integer ACTIONS_DURABILITY = 14;

            public static String ACTIONS_NAME = "<main>&lActions";
        }

        public static class STAFF_HISTORY_BUTTON {

            public static Boolean ENABLED = true;

            public static Integer SLOT = 21;

            public static String NAME = "<main>&lStaff History";

            public static List<String> LORE =
                    Arrays.asList(
                            "",
                            "&a\u00bb <middle>Click to view staff history of <bright><target><middle>.");

            public static String ITEM = "CARPET";

            public static Integer DURABILITY = 14;
        }

        public static class WARNINGS_BUTTON {

            public static Boolean ENABLED = true;

            public static Integer SLOT = 20;

            public static String NAME = "<main>&lWarnings";

            public static List<String> LORE =
                    Arrays.asList(
                            "",
                            " <rail>\u2503 <muted>Warnings: <secondary><warnings>",
                            "",
                            "&a\u00bb <middle>Click to view the warnings of <bright><target><middle>.");

            public static String ITEM = "PAPER";

            public static Integer DURABILITY = 0;
        }
    }

    public static class ALTS_MENU {

        public static String TITLE = "<main>&lALTS<main> <rail>\u00bb <middle><player>";

        public static String NAME = "&a<player> <muted>[<status>]";

        public static List<String> LORE =
                Arrays.asList(
                        " <rail>\u2503 <muted>Name: <secondary><name>",
                        " <rail>\u2503 <muted>Last Seen: <secondary><lastSeen>",
                        " <rail>\u2503 <muted>Last Location: <secondary><country> <muted>(<isp>)",
                        "",
                        "<score>",
                        "<matching>",
                        "<punishments>",
                        "<subnetMatch>",
                        "",
                        "<name><middle>'s Current IP Info:",
                        " <rail>\u2503 <muted>Logins: <secondary><logins>",
                        " <rail>\u2503 <muted>First Login: <secondary><firstLogin>",
                        " <rail>\u2503 <muted>Last Login: <secondary><lastLogin>",
                        "<matchingIPInfo>");

        public static List<String> MATCHING_IP_INFO =
                Arrays.asList(
                        "",
                        "<name><middle>'s Matching IP Info <main>(<amount>)<middle>:",
                        " <rail>\u2503 <muted>Logins: <main><logins>",
                        " <rail>\u2503 <muted>First Login: <main><firstLogin>",
                        " <rail>\u2503 <muted>Last Login: <main><lastLogin>");

        public static List<String> SUBNET_ONLY_MATCH =
                Arrays.asList(
                        "",
                        " <rail>\u2503 <main>The IPs of this player do not match,",
                        " <rail>\u2503 <main>however the subnet does match!");

        public static String IP_MATCHING = "&aCurrently matching <bright><player>";

        public static String IP_NOT_MATCHING = "<main>Currently not matching <bright><player>";

        public static String PUNISHMENT_MATCHING = "&a<type> currently matching <bright><player>";

        public static String PUNISHMENT_NOT_MATCHING =
                "<main><type> currently not matching <bright><player>";
    }

    public static class ANALYTICS {

        public static String TITLE = "<main>&lANALYTICS<main> <rail>\u00bb <middle><server>";

        public static class AVERAGE_PLAYTIME {

            public static String NAME = "<main>&lAverage Playtime";

            public static List<String> LORE =
                    Arrays.asList(
                            "",
                            " <rail>\u2503 <muted>Today: <main><playtime>",
                            " <rail>\u2503 <muted>Yesterday: <main><yesterdayPlaytime> <muted>(<yesterdayDiff>)",
                            " <rail>\u2503 <muted>Last Week: <main><weekPlaytime> <muted>(<weekDiff>)");
        }

        public static class PLAYER_PEAK {

            public static String NAME = "<main>&lPlayer Peak";

            public static List<String> LORE =
                    Arrays.asList(
                            "",
                            " <rail>\u2503 <muted>Today: <main><peak>",
                            " <rail>\u2503 <muted>Yesterday: <main><yesterdayPeak> <muted>(<yesterdayDiff>)",
                            " <rail>\u2503 <muted>Last Week: <main><weekPeak> <muted>(<weekDiff>)");
        }
    }

    public static class CHATSNAPSHOT_MENU {

        public static String MENU_TITLE = "<main>&lCHAT SNAPSHOTS";

        public static String OWN_MENU_TITLE = "<main>&lYOUR SNAPSHOTS";

        public static String SNAPSHOT_NAME = "<main>Snapshot <muted>[<time>]";

        public static List<String> OWN_SNAPSHOT_LORE =
                Arrays.asList("", "<main>&lInformation", " <rail>\u2503 <muted>ID: <main><id>");

        public static List<String> SNAPSHOT_LORE =
                Arrays.asList(
                        "",
                        "<main>&lInformation",
                        " <rail>\u2503 <muted>Requested By: <main><requestedBy>",
                        " <rail>\u2503 <muted>ID: <main><id>");
    }

    public static class CUSTOM_TAGS {

        public static String TITLE = "<main>&lCUSTOM TAGS";

        public static String CREATE_BUTTON_NAME = "&a&lCreate Custom Tag";

        public static List<String> CREATE_BUTTON_LORE =
                Arrays.asList(
                        "",
                        " <rail>\u2503 <muted>Tokens: <main><tokens>",
                        "",
                        "&a\u00bb <middle>Click to create.");

        public static String NAME = "<tag><middle><selected>";

        public static List<String> LORE =
                Arrays.asList(
                        "<rail><createdOn>",
                        "",
                        " <rail>\u2503 <muted>Status: <main><status>",
                        "",
                        "<main>&lTag Format:",
                        " <rail>\u2503 <bright><tag>",
                        "",
                        "&a\u00bb <middle>Click to select.",
                        "<main>Drop to delete.");

        @Comment("Name and lore for custom tag button in the delete confirmation menu.")
        public static String DELETE_TITLE = "<main>&lDELETE TAG<main> <rail>\u00bb <middle><tag>";

        public static String DELETE_NAME = "<bright><tag><middle><selected>";

        public static List<String> DELETE_LORE =
                Arrays.asList(
                        "<rail><createdOn>",
                        "",
                        "<main>&lTag Format:",
                        " <rail>\u2503 <bright><tag>");

        public static String RESET_NAME = "<main>&lRemove Tag";

        public static List<String> RESET_LORE =
                Arrays.asList(
                        "",
                        " <rail>\u2503 <muted>Selected: <bright><tag>",
                        "",
                        "&a\u00bb <middle>Click to reset.");

        public static String STATUS_ACCEPTED = "&aAccepted";

        public static String STATUS_DENIED = "<main>Denied";

        public static String STATUS_UNDECIDED = "&eUndecided";
    }

    public static class CUSTOM_TAGS_ADMIN {

        public static String TITLE = "<main>&lCUSTOM TAGS<main> <rail>\u00bb <middle>Management";

        public static String NAME = "<bright><tag>";

        public static List<String> LORE =
                Arrays.asList(
                        "<rail><createdOn>",
                        "",
                        " <rail>\u2503 <muted>Status: &eAwaiting Approval",
                        " <rail>\u2503 <muted>Requested By: <main><creator>",
                        "",
                        "<main>&lTag Format:",
                        " <rail>\u2503 <bright><tag>",
                        "",
                        "&a\u00bb <middle>Left click to accept.",
                        "<main>\u00bb <middle>Right click to deny.");

        @Comment("Name and lore for custom tag button in the accept confirmation menu.")
        public static String CONFIRMATION_TITLE_ACCEPT =
                "<main>&lACCEPT TAG<main> <rail>\u00bb <middle><tag>";

        public static String CONFIRMATION_TITLE_DENY =
                "<main>&lDENY TAG<main> <rail>\u00bb <middle><tag>";

        public static String CONFIRMATION_NAME = "<bright><tag>";

        public static List<String> CONFIRMATION_LORE =
                Arrays.asList(
                        "<rail><createdOn>",
                        "",
                        " <rail>\u2503 <muted>Status: &eAwaiting Approval",
                        " <rail>\u2503 <muted>Requested By: <main><creator>",
                        "",
                        "<main>&lTag Format:",
                        " <rail>\u2503 <bright><tag>");
    }

    public static class DISGUISE_HISTORY_MENU {

        public static String TITLE = "<main>&lDISGUISE HISTORY<main> <rail>\u00bb <middle><player>";

        public static String NAME = "<main><name>";

        public static List<String> LORE =
                Arrays.asList(
                        "<rail><date>",
                        "",
                        " <rail>\u2503 <muted>Rank: <main><rank>",
                        " <rail>\u2503 <muted>Skin: <main><skin>");
    }

    public static class DISGUISE_RANK_MENU {

        public static String TITLE = "<main>&lDISGUISE<main> <rail>\u00bb <middle>Rank";

        public static List<String> LORE =
                Arrays.asList(
                        "",
                        " <rail>\u2503  <player><bright>: Hello Word!",
                        "",
                        "&a\u00bb <middle>Click to disguise as <rank><bright>.");
    }

    public static class DISGUISE_SKIN_MENU {

        public static String TITLE = "<main>&lDISGUISE<main> <rail>\u00bb <middle>Skin";

        public static String SKIN_NAME = "<main><skin>";

        public static String CUSTOM_NAME = "<main>Custom Skin";

        public static String RANDOM_NAME = "<main>Random Skin";

        public static String DISGUISE_AS_NAME = "<main>Disguise as <name>";
    }

    public static class FILTERS {

        public static String NO_FILTERS = "<main>There are no filters available!";

        public static String TITLE = "<main>&lFILTERS<main> <rail>\u00bb <middle><filter>";

        public static class AUTO_PUNISH_TRAIT {

            public static String NAME = "<main>Auto Punish";

            public static String MATERIAL = "REDSTONE";

            public static List<String> DESCRIPTION =
                    Arrays.asList(
                            " <rail>\u2503 <middle>Should the filter automatically",
                            " <rail>\u2503 <middle>punish offenders?");
        }

        public static class HARD_TRAIT {

            public static String NAME = "<main>Hard";

            public static String MATERIAL = "LEVER";

            public static List<String> DESCRIPTION =
                    Collections.singletonList(" <rail>\u2503 <middle>Should the filter be hard?");
        }

        public static class PUNISH_LADDER {

            public static String NAME = "<main>Punishment Ladder";

            public static String MATERIAL = "PAPER";

            public static List<String> DESCRIPTION =
                    Arrays.asList(
                            "",
                            " <rail>\u2503 <muted>Current: <main><current>",
                            "",
                            "&a\u00bb <middle>Click to select punishment ladder.");

            public static String LADDER_FORMAT = "<main><ladder>";
        }

        public static class TYPE {

            public static String NAME = "<main>Filter Type";

            public static String MATERIAL = "HOPPER";

            public static List<String> DESCRIPTION =
                    Arrays.asList(
                            " <rail>\u2503 <middle>Change the filter type.",
                            "",
                            "<types>",
                            "",
                            "&a\u00bb <middle>Click to change.");

            public static String FORMAT = " <rail>\u25a0 <bright><type>";

            public static String FORMAT_SELECTED = " &a\u25a0 <bright><type>";
        }
    }

    @Comment("Held open in front of a frozen player, if the freeze menu is on.")
    public static class FREEZE_MENU {

        public static String NAME = "<main>You've been frozen.";

        public static List<String> LORE =
                Arrays.asList(
                        "<secondary>&m---------------------------------",
                        "<main>You've been frozen by a staff member!",
                        "<main>You will be banned if you disconnect.",
                        "",
                        "<middle>Join our discord: <main>discord.yourname.com",
                        "<middle>and join a waiting room within <main>5 <middle>minutes.",
                        "<secondary>&m---------------------------------");
    }

    public static class GENERAL {

        public static class BACK_SLOT {

            public static String MATERIAL = "BED";

            public static int DURABILITY = 0;

            public static String NAME = "<main>&lBack";

            public static List<String> LORE =
                    Arrays.asList("", "&a\u00bb <middle>Click to go back.");
        }

        public static class FILL_SLOT {

            public static String MATERIAL = "STAINED_GLASS_PANE";

            public static int DURABILITY = 15;
        }

        public static class FILTERS_SLOT {

            public static String NAME = "<main>&lFilters";

            public static String MATERIAL = "HOPPER";

            public static int SLOT = 4;

            public static String SELECTOR = " <main>\u00bb";

            public static String ENABLED_FILTER = " &a\u2713 <middle><filter>";

            public static String DISABLED_FILTER = " <rail>\u2717 <muted><filter>";

            public static List<String> LORE =
                    Arrays.asList(
                            "<filters>",
                            "",
                            "&a\u00bb <middle>Left/Right click to scroll.",
                            "<main>\u00bb <middle>Middle click to select.");
        }

        @Comment({
            "The buttons and lines the native forms share.",
            "Forms have no pages of their own, so these move through the list."
        })
        public static class FORM {

            public static String CLOSE = "<muted>Close";

            public static String BACK = "<muted>\u00ab Back";

            public static String NEXT = "<middle>Next <rail><page>/<pages>";

            public static String PREVIOUS = "<middle>Back <rail><page>/<pages>";

            public static String SHOWING = "<muted><from>-<to> of <total>";

            @Comment({
                "How a setting reads on a form. A button has no lore to carry its state,",
                "so the state is drawn into the label itself."
            })
            public static String TOGGLE_ON = "&a\u25a0 <bright><name>";

            public static String TOGGLE_OFF = "<rail>\u25a0 <bright><name>";

            public static String OPTION = "&e\u25a0 <bright><name><muted>: <bright><option>";
        }

        public static class NEXT_PAGE_SLOT {

            public static String MATERIAL = "SKULL_ITEM";

            public static int DURABILITY = 3;

            public static boolean HEAD = true;

            public static String HEAD_TEXTURE =
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19";

            public static String NAME = "<main>&lNext<main> <rail><page>/<pages>";

            public static List<String> LORE =
                    Arrays.asList("", "<muted>Right click to view all pages.");
        }

        public static class PREVIOUS_PAGE_SLOT {

            public static String MATERIAL = "SKULL_ITEM";

            public static int DURABILITY = 3;

            public static boolean HEAD = true;

            public static String HEAD_TEXTURE =
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==";

            public static String NAME = "<main>&lBack<main> <rail><page>/<pages>";

            public static List<String> LORE =
                    Arrays.asList("", "<muted>Right click to view all pages.");
        }
    }

    public static class GRANTS_HISTORY_MENU {

        public static String NICE_SCOPE = " <secondary>\u25cf <middle><scope>";

        public static String TITLE = "<main>&lGRANT HISTORY<main> <rail>\u00bb <middle><player>";

        public static class ACTIVE_STAFF_GRANT {

            public static String NAME = "&a+<issuedAt> (Active)";

            @Comment("This means that the grant is active but not on that specific scope")
            public static String NOT_ON_SCOPE_NAME = "&2+<issuedAt> (Not Global)";

            public static List<String> PERMANENT =
                    Arrays.asList(
                            "",
                            " <rail>\u2503 <muted>Target: <main><target>",
                            " <rail>\u2503 <muted>Rank: <main><rank>",
                            " <rail>\u2503 <muted>Duration: <main><duration>",
                            "",
                            " <rail>\u2503 <muted>Issued By: <main><issuedBy>",
                            " <rail>\u2503 <muted>Issued On: <main><issuedOn>",
                            " <rail>\u2503 <muted>Issued Reason: <main><reason>",
                            "",
                            "<main>&lGrant Scopes",
                            "<niceScopes>");

            public static List<String> TIMED =
                    Arrays.asList(
                            "&a=<willExpire>",
                            "",
                            " <rail>\u2503 <muted>Target: <main><target>",
                            " <rail>\u2503 <muted>Rank: <main><rank>",
                            " <rail>\u2503 <muted>Duration: <main><duration>",
                            " <rail>\u2503 <muted>Expires In: <main><expire>",
                            " <rail>\u2503 <muted>Will expire on: <main><willExpire>",
                            "",
                            " <rail>\u2503 <muted>Issued By: <main><issuedBy>",
                            " <rail>\u2503 <muted>Issued On: <main><issuedOn>",
                            " <rail>\u2503 <muted>Issued Reason: <main><reason>",
                            "",
                            "<main>&lGrant Scopes",
                            "<niceScopes>");
        }

        public static class INACTIVE_STAFF_GRANT {

            public static String NAME = "<main>+<issuedAt> (Inactive)";

            public static String EXPIRED_NAME = "<muted>+<issuedAt> (Expired)";

            public static List<String> REMOVED =
                    Arrays.asList(
                            "<main>-<removedAt>",
                            "",
                            " <rail>\u2503 <muted>Target: <main><target>",
                            " <rail>\u2503 <muted>Rank: <main><rank>",
                            " <rail>\u2503 <muted>Duration: <main><duration>",
                            "",
                            " <rail>\u2503 <muted>Issued By: <main><issuedBy>",
                            " <rail>\u2503 <muted>Issued On: <main><issuedOn>",
                            " <rail>\u2503 <muted>Issued Reason: <main><reason>",
                            "",
                            " <rail>\u2503 <muted>Removed By: <main><removedBy>",
                            " <rail>\u2503 <muted>Removed On: <main><removedOn>",
                            " <rail>\u2503 <muted>Removed Reason: <main><removedReason>",
                            "",
                            "<main>&lGrant Scopes",
                            "<niceScopes>");

            public static List<String> EXPIRED =
                    Arrays.asList(
                            "<muted>-<removedAt>",
                            "",
                            " <rail>\u2503 <muted>Target: <main><target>",
                            " <rail>\u2503 <muted>Rank: <main><rank>",
                            " <rail>\u2503 <muted>Duration: <main><duration>",
                            "",
                            " <rail>\u2503 <muted>Issued By: <main><issuedBy>",
                            " <rail>\u2503 <muted>Issued On: <main><issuedOn>",
                            " <rail>\u2503 <muted>Issued Reason: <main><reason>",
                            "",
                            "<main>&lGrant Scopes",
                            "<niceScopes>");
        }
    }

    public static class GRANTS_MENU {

        public static String NICE_SCOPE = " <secondary>\u25cf <middle><scope>";

        public static String TITLE = "<main>&lGRANTS<main> <rail>\u00bb <middle><player>";

        public static class ACTIVE_GRANT {

            public static String NAME = "&a+<issuedAt> (Active)";

            @Comment("This means that the grant is active but not on that specific scope")
            public static String NOT_ON_SCOPE_NAME = "&a+<issuedAt> &2(Not Global)";

            public static List<String> PERMANENT =
                    Arrays.asList(
                            "",
                            " <rail>\u2503 <muted>Target: <main><target>",
                            " <rail>\u2503 <muted>Rank: <main><rank>",
                            " <rail>\u2503 <muted>Duration: <main><duration>",
                            "",
                            " <rail>\u2503 <muted>Issued By: <main><issuedBy>",
                            " <rail>\u2503 <muted>Issued On: <main><issuedOn>",
                            " <rail>\u2503 <muted>Issued Reason: <main><reason>",
                            "",
                            "<main>&lGrant Scopes",
                            "<niceScopes>");

            public static List<String> TIMED =
                    Arrays.asList(
                            "&a=<willExpire>",
                            "",
                            " <rail>\u2503 <muted>Target: <main><target>",
                            " <rail>\u2503 <muted>Rank: <main><rank>",
                            " <rail>\u2503 <muted>Duration: <main><duration>",
                            " <rail>\u2503 <muted>Expires In: <main><expire>",
                            " <rail>\u2503 <muted>Will expire on: <main><willExpire>",
                            "",
                            " <rail>\u2503 <muted>Issued By: <main><issuedBy>",
                            " <rail>\u2503 <muted>Issued On: <main><issuedOn>",
                            " <rail>\u2503 <muted>Issued Reason: <main><reason>",
                            "",
                            "<main>&lGrant Scopes",
                            "<niceScopes>");

            public static List<String> CAN_REMOVE =
                    Arrays.asList("", "&a\u00bb <middle>Click to remove.");

            public static List<String> CANT_REMOVE =
                    Arrays.asList("", "<main>You can not remove this grant.");
        }

        public static class INACTIVE_GRANT {

            public static String NAME = "<main>+<issuedAt> (Inactive)";

            public static String EXPIRED_NAME = "<muted>+<issuedAt> (Expired)";

            public static List<String> REMOVED =
                    Arrays.asList(
                            "<main>-<removedAt>",
                            "",
                            " <rail>\u2503 <muted>Target: <main><target>",
                            " <rail>\u2503 <muted>Rank: <main><rank>",
                            " <rail>\u2503 <muted>Duration: <main><duration>",
                            "",
                            " <rail>\u2503 <muted>Issued By: <main><issuedBy>",
                            " <rail>\u2503 <muted>Issued On: <main><issuedOn>",
                            " <rail>\u2503 <muted>Issued Reason: <main><reason>",
                            "",
                            " <rail>\u2503 <muted>Removed By: <main><removedBy>",
                            " <rail>\u2503 <muted>Removed On: <main><removedOn>",
                            " <rail>\u2503 <muted>Removed Reason: <main><removedReason>",
                            "",
                            "<main>&lGrant Scopes",
                            "<niceScopes>");

            public static List<String> EXPIRED =
                    Arrays.asList(
                            "<muted>-<removedAt>",
                            "",
                            " <rail>\u2503 <muted>Target: <main><target>",
                            " <rail>\u2503 <muted>Rank: <main><rank>",
                            " <rail>\u2503 <muted>Duration: <main><duration>",
                            "",
                            " <rail>\u2503 <muted>Issued By: <main><issuedBy>",
                            " <rail>\u2503 <muted>Issued On: <main><issuedOn>",
                            " <rail>\u2503 <muted>Issued Reason: <main><reason>",
                            "",
                            "<main>&lGrant Scopes",
                            "<niceScopes>");
        }
    }

    public static class GRANTS_REMOVE_MENU {

        public static String CONFIRM_NAME = "&a&lConfirm";

        public static List<String> CONFIRM_LORE =
                Arrays.asList(
                        " <rail>\u2503 <muted>Rank: <main><rank>",
                        "",
                        "&a\u00bb <middle>Click to confirm.");

        public static String CANCEL_NAME = "<main>&lCancel";

        public static List<String> CANCEL_LORE =
                Arrays.asList(
                        " <rail>\u2503 <muted>Rank: <main><rank>",
                        "",
                        "<main>\u00bb <middle>Click to cancel.");
    }

    public static class GRANT_MENU {

        public static String RANK_TITLE = "<main>&lGRANT<main> <rail>\u00bb <middle><player>";

        public static String SCOPE_TITLE = "<main>&lGRANT<main> <rail>\u00bb <middle>Scopes";

        public static String CONFIRM_TITLE = "<main>&lGRANT<main> <rail>\u00bb <middle>Confirm";

        public static String REMOVAL_TITLE = "<main>&lGRANT<main> <rail>\u00bb <middle>Removal";

        public static String DURATION_TITLE = "<main>&lGRANT<main> <rail>\u00bb <middle>Duration";

        public static String REASON_TITLE = "<main>&lGRANT<main> <rail>\u00bb <middle>Reason";

        public static String GRANTING_CANCELLED = "<main>Granting cancelled.";

        public static String GRANT_REMOVAL_CANCELLED = "<main>Grant removal cancelled.";

        public static boolean SHOW_UNGRANTABLE_RANKS = false;

        public static class CONFIRM_MENU {

            public static String CONFRIM_NAME = "&a&lConfirm Action";

            public static List<String> CONFIRM_LORE =
                    Arrays.asList(
                            " <rail>\u2503 <muted>Player: <main><player>",
                            " <rail>\u2503 <muted>Duration: <main><duration>",
                            " <rail>\u2503 <muted>Reason: <main><reason>",
                            " <rail>\u2503 <muted>Rank: <main><rank>",
                            " <rail>\u2503 <muted>Scopes: <main><scopes>",
                            "",
                            "&a\u00bb <middle>Click to grant.");

            public static String CANCEL_NAME = "<main>&lCancel Action";

            public static List<String> CANCEL_LORE =
                    Arrays.asList(
                            " <rail>\u2503 <muted>Player: <main><player>",
                            " <rail>\u2503 <muted>Duration: <main><duration>",
                            " <rail>\u2503 <muted>Reason: <main><reason>",
                            " <rail>\u2503 <muted>Rank: <main><rank>",
                            " <rail>\u2503 <muted>Scopes: <main><scopes>",
                            "",
                            "<main>\u00bb <middle>Click to cancel.");
        }

        public static class DURATION_MENU {

            public static String NAME = "<main><time>";

            public static List<String> LORE = Arrays.asList("", "&a\u00bb <middle>Click to set.");
        }

        public static class RANK_MENU {

            public static List<String> ACCESS_LORE =
                    Arrays.asList(
                            " <rail>\u2503 <muted>Priority: <main><priority>",
                            " <rail>\u2503 <muted>Prefix: <main><prefix>",
                            " <rail>\u2503 <muted>Suffix: <main><suffix>",
                            " <rail>\u2503 <muted>Visible: <main><visible>",
                            " <rail>\u2503 <muted>Grantable: <main><grantable>",
                            "",
                            "&a\u00bb <middle>Left click to grant <rank> to <player>.",
                            "<main>\u00bb <middle>Right click to grant with scope selection.");

            public static List<String> NO_ACCESS_LORE =
                    Arrays.asList(
                            " <rail>\u2503 <muted>Priority: <main><priority>",
                            " <rail>\u2503 <muted>Prefix: <main><prefix>",
                            " <rail>\u2503 <muted>Suffix: <main><suffix>",
                            " <rail>\u2503 <muted>Visible: <main><visible>",
                            " <rail>\u2503 <muted>Grantable: <main><grantable>",
                            "",
                            "<main>You do not have access to this rank.");
        }

        public static class REASON_MENU {

            public static String NAME = "<secondary><reason>";

            public static List<String> LORE = Arrays.asList("", "&a\u00bb <middle>Click to set.");

            public static List<String> AVAILABLE_REASONS =
                    Arrays.asList(
                            "Promoted",
                            "Demoted",
                            "Addon Rank",
                            "Famous",
                            "Giveaway",
                            "Event",
                            "Store Issue");
        }

        public static class SCOPE_MENU {

            public static String GLOBAL_NAME = "<main>Global";

            public static List<String> GLOBAL_ADD_LORE =
                    Arrays.asList(
                            " <rail>\u2503 <muted>Scope: <main>Global",
                            "",
                            "&a\u00bb <middle>Click to add.");

            public static List<String> GLOBAL_REMOVE_LORE =
                    Arrays.asList(
                            " <rail>\u2503 <muted>Scope: <main>Global",
                            "",
                            "<main>\u00bb <middle>Click to remove.");

            public static String SCOPE_NAME = "<main><scope>";

            public static List<String> SCOPE_ADD_LORE =
                    Arrays.asList(
                            " <rail>\u2503 <muted>Scope: <main><scope>",
                            "",
                            "&a\u00bb <middle>Click to add.");

            public static List<String> SCOPE_REMOVE_LORE =
                    Arrays.asList(
                            " <rail>\u2503 <muted>Scope: <main><scope>",
                            "",
                            "<main>\u00bb <middle>Click to remove.");

            public static String CONFIRM_NAME = "&a&lConfirm";

            public static List<String> CONFIRM_LORE =
                    Arrays.asList(
                            " <rail>\u2503 <muted>Scopes: <main><scopes>",
                            "",
                            "&a\u00bb <middle>Click to confirm the scope selections.");
        }
    }

    public static class GROUP_INFO {

        public static String GROUP_TITLE = "<main>&lGROUPS";

        public static String GLOBAL_NAME = "<main>&lGlobal Group";

        public static List<String> GLOBAL_LORE =
                Arrays.asList(
                        " <rail>\u2503 <muted>Total Servers: <main><count>",
                        "",
                        "&a\u00bb <middle>Click to view all servers.");

        public static String GROUP_NAME = "<main>&l<group>";

        public static List<String> GROUP_LORE =
                Arrays.asList(
                        " <rail>\u2503 <muted>Total Servers: <main><count>",
                        "",
                        "&a\u00bb <middle>Click to view servers of this group.");
    }

    public static class HISTORY_MENU {

        public static String TITLE = "<main>&lHISTORY<main> <rail>\u00bb <middle><player>";

        public static String TYPE_TITLE =
                "<main>&lHISTORY<main> <rail>\u00bb <middle><type> <player>";

        public static String CATEGORY_NAME = "<main>&l<category>";

        public static List<String> CATEGORY_LORE =
                Arrays.asList(
                        "<muted>View all <bright><category> <muted>that were",
                        "<muted>executed on <main><player><muted>.",
                        "",
                        " <rail>\u2503 <muted>Active: &a<active>",
                        " <rail>\u2503 <muted>Inactive: <main><inactive>",
                        " <rail>\u2503 <muted>Total: <main><total>",
                        "",
                        "&a\u00bb <middle>Click to view this category.");

        public static List<String> CATEGORY_LORE_WITH_COMPETITIVE =
                Arrays.asList(
                        "<muted>View all <bright><category> <muted>that were",
                        "<muted>executed on <main><player><muted>.",
                        "",
                        " <rail>\u2503 <muted>Competitive: &e<competitive>",
                        "",
                        " <rail>\u2503 <muted>Active: &a<active>",
                        " <rail>\u2503 <muted>Inactive: <main><inactive>",
                        " <rail>\u2503 <muted>Total: <main><total>",
                        "",
                        "&a\u00bb <middle>Click to view this category.");

        public static String COMPETITIVE = "<bright> (Competitive)";

        public static String SHADOW = "<rail> (Shadow)";

        public static String IP = "<muted> (IP)";

        public static List<String> PROOF_CLICK =
                Arrays.asList("", "&a\u00bb <middle>Click to view attached proof.");

        public static String VOID_REASON_UNRECORDED = "Unrecorded";

        public static class ACTIVE_PUNISHMENT {

            public static String NAME =
                    "&a+<issuedAt><ip><shadow><competitive> (Active) <bright>[#<punishmentID>]";

            public static List<String> PERMANENT =
                    Arrays.asList(
                            "",
                            " <rail>\u2503 <muted>Target: <main><target>",
                            " <rail>\u2503 <muted>Duration: <main><duration>",
                            "",
                            " <rail>\u2503 <muted>Issued By: <main><issuedBy>",
                            " <rail>\u2503 <muted>Issued On: <main><issuedOn>",
                            " <rail>\u2503 <muted>Issued Reason: <main><reason>");

            public static List<String> TIMED =
                    Arrays.asList(
                            "&a=<willExpire>",
                            "",
                            " <rail>\u2503 <muted>Target: <main><target>",
                            " <rail>\u2503 <muted>Duration: <main><duration>",
                            " <rail>\u2503 <muted>Expires In: <main><expire>",
                            " <rail>\u2503 <muted>Will expire on: <main><willExpire>",
                            "",
                            " <rail>\u2503 <muted>Issued By: <main><issuedBy>",
                            " <rail>\u2503 <muted>Issued On: <main><issuedOn>",
                            " <rail>\u2503 <muted>Issued Reason: <main><reason>");
        }

        public static class INACTIVE_PUNISHMENT {

            public static String NAME =
                    "<main>+<issuedAt><ip><shadow><competitive> (Inactive) <bright>[#<punishmentID>]";

            public static String EXPIRED_NAME =
                    "<muted>+<issuedAt><ip><shadow><competitive> (Expired) <bright>[#<punishmentID>]";

            public static String VOIDED_NAME =
                    "&6+<issuedAt><ip><shadow><competitive> (Voided) <bright>[#<punishmentID>]";

            public static List<String> REMOVED =
                    Arrays.asList(
                            "<main>-<removedAt>",
                            "",
                            " <rail>\u2503 <muted>Target: <main><target>",
                            " <rail>\u2503 <muted>Duration: <main><duration>",
                            "",
                            " <rail>\u2503 <muted>Issued By: <main><issuedBy>",
                            " <rail>\u2503 <muted>Issued On: <main><issuedOn>",
                            " <rail>\u2503 <muted>Issued Reason: <main><reason>",
                            "",
                            " <rail>\u2503 <muted>Removed By: <main><removedBy>",
                            " <rail>\u2503 <muted>Removed On: <main><removedOn>",
                            " <rail>\u2503 <muted>Removed Reason: <main><removedReason>");

            public static List<String> EXPIRED =
                    Arrays.asList(
                            "<muted>-<removedAt>",
                            "",
                            " <rail>\u2503 <muted>Target: <main><target>",
                            " <rail>\u2503 <muted>Duration: <main><duration>",
                            "",
                            " <rail>\u2503 <muted>Issued By: <main><issuedBy>",
                            " <rail>\u2503 <muted>Issued On: <main><issuedOn>",
                            " <rail>\u2503 <muted>Issued Reason: <main><reason>");

            public static List<String> VOIDED =
                    Arrays.asList(
                            "&6-<removedAt>",
                            "",
                            " <rail>\u2503 <muted>Target: <main><target>",
                            " <rail>\u2503 <muted>Duration: <main><duration>",
                            "",
                            " <rail>\u2503 <muted>Issued By: <main><issuedBy>",
                            " <rail>\u2503 <muted>Issued On: <main><issuedOn>",
                            " <rail>\u2503 <muted>Issued Reason: <main><reason>",
                            "",
                            " <rail>\u2503 <muted>Voided By: <main><removedBy>",
                            " <rail>\u2503 <muted>Voided On: <main><removedOn>",
                            " <rail>\u2503 <muted>Voided As: <main><voidReason>",
                            " <rail>\u2503 <muted>Note: <main><voidNote>");
        }
    }

    public static class LOCATION_LOG_MENU {

        public static String TITLE = "<main>&lLOCATION LOG<main> <rail>\u00bb <middle><player>";

        public static String NAME = "&a+<time>";

        public static List<String> LORE =
                Arrays.asList(
                        " <rail>\u2503 <muted>City: <main><city>",
                        " <rail>\u2503 <muted>Region: <main><region>",
                        " <rail>\u2503 <muted>Country: <main><country>",
                        " <rail>\u2503 <muted>ISP: <main><isp>",
                        " <rail>\u2503 <muted>VPN: <main><vpn>");
    }

    public static class NOTIFICATIONS_MENU {

        public static List<String> UNREAD_LORE =
                Arrays.asList("<notification>", "", "&a\u00bb <middle>Click to mark as read.");

        public static List<String> LORE =
                Arrays.asList(
                        "<notification>", "", "<main>You have marked this notification as read.");

        public static List<String> STATS_LORE =
                Arrays.asList(
                        "",
                        " <rail>\u2503 <muted>Viewed By: <main><views>",
                        " <rail>\u2503 <muted>Marked as Read By: <main><reads>");
    }

    @Comment("The staff list, /onlinestaff.")
    public static class ONLINE_STAFF_MENU {

        public static String TITLE = "<main>&lSTAFF ONLINE";

        public static List<String> LORE =
                Arrays.asList(
                        " <rail>\u2503 <muted>Playtime: <main><playtime>",
                        "",
                        " <rail>\u2503 <muted>Rank: <main><rank>",
                        " <rail>\u2503 <muted>Invisible: <main><isVanished>",
                        "",
                        "&a\u00bb <middle>Click to teleport.");
    }

    @Comment("Shown to a panicked player, on clients that can be held on a screen.")
    public static class PANIC_SCREEN {

        public static String NAME = "<main>You are in panic mode.";
    }

    public static class PROFILE {

        public static String SELF_TITLE = "<main>&lPROFILE";

        public static String TITLE = "<main>&lPROFILE<main> <rail>\u00bb <middle><profile>";

        public static Boolean USE_HEAD_AS_ITEM = true;

        public static String PROFILE_ITEM = "SKULL_ITEM";

        public static Integer PROFILE_ITEM_DAMAGE = 0;

        public static String PROFILE_NAME = "<profile>";

        public static String ONLINE = "&aOnline <muted>(<server>)";

        public static String OFFLINE = "<main>Offline";

        public static List<String> PROFILE_LORE =
                Arrays.asList(
                        "",
                        "<rail>\u2503 <muted>Status: <middle><status>",
                        "<rail>\u2503 <muted>Level: <middle><level> <rail>(<experience> XP)",
                        "<rail>\u2503 <muted>Rank: <middle><rank>",
                        "",
                        "<rail>\u2503 <muted>Name: <middle><color><player>",
                        "<rail>\u2503 <muted>Chat: <middle><cc>Hi!",
                        "<rail>\u2503 <muted>Tag: <middle><tag>",
                        "");

        public static class EQUIPPED_COSMETICS_BUTTON {

            public static String NAME = "<main>&lCosmetics";

            public static List<String> LORE =
                    Arrays.asList("", "&a\u00bb <middle>Click to view cosmetics.");

            public static String ITEM = "ENDER_CHEST";

            public static Integer DURABILITY = 0;
        }

        public static class IGNORE_BUTTON {

            public static String IGNORING_NAME = "&aStop Ignoring";

            public static String NOT_IGNORING_NAME = "<main>Start Ignoring";

            public static List<String> NOT_IGNORING_LORE =
                    Arrays.asList(
                            "",
                            "&a\u00bb <middle>Click to add <bright><player> <middle>to your ignore list.");

            public static List<String> IGNORING_LORE =
                    Arrays.asList(
                            "",
                            "<main>\u00bb <middle>Click to remove <bright><player> <middle>from your ignore list.");

            public static String ITEM = "REDSTONE_BLOCK";

            public static Integer DURABILITY = 0;
        }

        public static class REPORT_BUTTON {

            public static String NAME = "<main>&lReport";

            public static List<String> LORE =
                    Arrays.asList("", "&a\u00bb <middle>Click to report <bright><player><middle>.");

            public static String ITEM = "NETHER_STAR";

            public static Integer DURABILITY = 0;
        }

        public static class SETTINGS_BUTTON {

            public static String NAME = "<main>&lSettings";

            public static List<String> LORE =
                    Arrays.asList("", "&a\u00bb <middle>Click to view your settings.");

            public static String ITEM = "ITEM_FRAME";

            public static Integer DURABILITY = 0;
        }

        public static class SOCIAL_MEDIA_BUTTON {

            public static String NAME = "<main>&lSocial Media";

            public static List<String> LORE =
                    Arrays.asList(
                            "",
                            "&a\u00bb <middle>Click to view social media of <bright><player><middle>.");

            public static String ITEM = "BOOK";

            public static Integer DURABILITY = 0;
        }

        public static class VIEW_FRIENDS_BUTTON {

            public static String NAME = "<main>&lFriends";

            public static List<String> LORE =
                    Arrays.asList("", "&a\u00bb <middle>Click to view your friends.");

            public static String ITEM = "SKULL_ITEM";

            public static Integer DURABILITY = 0;
        }
    }

    public static class PROOF_MENU {

        public static String TITLE = "<main>&lPROOF";

        public static String ITEM_NAME = "<main><type>";

        public static List<String> ITEM_LORE =
                Arrays.asList(
                        " <rail>\u2503 <muted>Proof: <secondary><proof>",
                        " <rail>\u2503 <muted>Added By: <secondary><addedBy>",
                        "",
                        "&a\u00bb <middle>Click to view.");
    }

    public static class PUNISH_MENU {

        public static String TITLE = "<main>&lPUNISH<main> <rail>\u00bb <middle><player>";

        public static String ITEM_NAME = "<main>&l<ladder> (#<nextTimes>)";

        public static List<String> SNAPSHOT_ITEM_LORE =
                Arrays.asList(
                        " <rail>\u2503 <middle>This ladder will automatically attach a",
                        " <rail>\u2503 <middle>chat snapshot to the proof of this punishment.",
                        "",
                        "<secondary><player><middle> has been punished <secondary><times><middle> by this ladder.",
                        "<decay>",
                        "",
                        "<main>Next Punishment",
                        " <rail>\u2503 <muted>Type: <secondary><type>",
                        " <rail>\u2503 <muted>Duration: <secondary><duration>",
                        "",
                        "&a\u00bb <middle>Left click to punish <leftAction>.",
                        "&a\u00bb <middle>Right click to punish <rightAction>.");

        public static List<String> ITEM_LORE =
                Arrays.asList(
                        "<secondary><player><middle> has been punished <secondary><times><middle> by this ladder",
                        "<decay>",
                        "",
                        "<main>Next Punishment",
                        " <rail>\u2503 <muted>Type: <secondary><type>",
                        " <rail>\u2503 <muted>Duration: <secondary><duration>",
                        "",
                        "&a\u00bb <middle>Left click to punish <leftAction>.",
                        "&a\u00bb <middle>Right click to punish <rightAction>.");

        public static List<String> DECAY =
                Arrays.asList(
                        "",
                        "<middle>Due to the decay of this ladder, <player> will be punished",
                        "<middle>by the ladder in #<decayed> instead of #<normal>.");
    }

    public static class RANK_EDITOR_MENU {

        public static List<String> RANK_INFO =
                Arrays.asList(
                        " <rail>\u2503 <muted>UUID: <main><uuid>",
                        " <rail>\u2503 <muted>Name: <main><name>",
                        " <rail>\u2503 <muted>Scopes: <main><scopes>",
                        " <rail>\u2503 <muted>Priority: <main><priority>",
                        " <rail>\u2503 <muted>Grantable: <main><grantable>",
                        "",
                        " <rail>\u2503 <muted>Prefix: <main><prefix> <muted>(Modern: <prefixModern>)",
                        " <rail>\u2503 <muted>Playerlist Prefix: <main><playerList> <muted>(Modern: <playerListPrefixModern>)",
                        " <rail>\u2503 <muted>Suffix: <main><suffix> <muted>(Modern: <suffixModern>)",
                        " <rail>\u2503 <muted>Color: <main><color> <muted>(Modern: <colorModern>)",
                        " <rail>\u2503 <muted>Price: <main><price>",
                        "",
                        " <rail>\u2503 <muted>Staff: <main><staff>",
                        " <rail>\u2503 <muted>Subscription: <main><subscription>",
                        " <rail>\u2503 <muted>Visible: <main><visible>",
                        " <rail>\u2503 <muted>Purchasable: <main><purchasable>",
                        " <rail>\u2503 <muted>Default: <main><default>");

        public static String NAME = "<main>&lEdit <type>";

        public static List<String> LORE =
                Arrays.asList(
                        "",
                        " <rail>\u2503 <middle>Change the <main><type><middle> of this rank.",
                        "",
                        "&a\u00bb <middle>Click to change.");

        public static List<String> PERMISSIONS_LORE =
                Arrays.asList(
                        "",
                        " <rail>\u2503 <middle>Change the <main><type><middle> of this rank.",
                        "",
                        "&a\u00bb <middle>Left click to add permissions.",
                        "<main>\u00bb <middle>Right click to remove permissions.",
                        "&e\u00bb <middle>Middle click to view permissions database.");
    }

    @Comment("The reports staff work through, /reports and /reports-all.")
    public static class REPORTS_MENU {

        public static String ALL_TITLE = "<main>&lREPORTS";

        @Comment("Shown in place of the punishment lines when the category has no ladder.")
        public static String NO_LADDER = "<main>No Ladder found.";

        @Comment("The buttons on the report form, which the menu hid behind right click and drop.")
        public static String EVIDENCE_BUTTON = "&aView evidence";

        public static String DELETE_BUTTON = "<main>Delete";

        public static String BACK_BUTTON = "<muted>Back";

        public static String TITLE = "<main>&lREPORTS<main> <rail>\u00bb <middle><target>";

        public static String NAME = "&a<issuedAt>";

        public static List<String> OPEN_LORE =
                Arrays.asList(
                        " <rail>\u2503 <muted>Target: <main><target>",
                        " <rail>\u2503 <muted>Target On: <main><targetOn>",
                        "",
                        " <rail>\u2503 <muted>Issued By: <main><issuedBy>",
                        " <rail>\u2503 <muted>Issued On: <main><issuedOn>",
                        " <rail>\u2503 <muted>Issued Reason: <main><reason>",
                        "",
                        "<main>&lPunishment:",
                        " <rail>\u2503 <muted>Type: <main><type>",
                        " <rail>\u2503 <muted>Time: <main><time>",
                        "",
                        "&a\u00bb <middle>Left Click to manage.",
                        "<main>\u00bb <middle>Right Click to view evidence.");

        public static List<String> IN_PROGRESS_LORE =
                Arrays.asList(
                        " <rail>\u2503 <muted>Target: <main><target>",
                        " <rail>\u2503 <muted>Target On: <main><targetOn>",
                        "",
                        " <rail>\u2503 <muted>Issued By: <main><issuedBy>",
                        " <rail>\u2503 <muted>Issued On: <main><issuedOn>",
                        " <rail>\u2503 <muted>Issued Reason: <main><reason>",
                        "",
                        " <rail>\u2503 <muted>Status: <main><status>",
                        " <rail>\u2503 <muted>In Progress By: <main><progressBy>",
                        "",
                        "&a\u00bb <middle>Left Click to manage.",
                        "<main>\u00bb <middle>Right Click to view evidence.");

        public static List<String> NOT_PUNISHED_LORE =
                Arrays.asList(
                        " <rail>\u2503 <muted>Target: <main><target>",
                        " <rail>\u2503 <muted>Target On: <main><targetOn>",
                        "",
                        " <rail>\u2503 <muted>Issued By: <main><issuedBy>",
                        " <rail>\u2503 <muted>Issued On: <main><issuedOn>",
                        " <rail>\u2503 <muted>Issued Reason: <main><reason>",
                        "",
                        " <rail>\u2503 <muted>Status: <main><status>",
                        " <rail>\u2503 <muted>Punished: <punished>",
                        " <rail>\u2503 <muted>Resolved By: <main><resolvedBy>",
                        "",
                        "&a\u00bb <middle>Right Click to view evidence.",
                        "<main>Drop to delete.");

        public static List<String> PUNISHED_LORE =
                Arrays.asList(
                        " <rail>\u2503 <muted>Target: <main><target>",
                        " <rail>\u2503 <muted>Target On: <main><targetOn>",
                        "",
                        " <rail>\u2503 <muted>Issued By: <main><issuedBy>",
                        " <rail>\u2503 <muted>Issued On: <main><issuedOn>",
                        " <rail>\u2503 <muted>Issued Reason: <main><reason>",
                        "",
                        "<main>&lPunishment:",
                        " <rail>\u2503 <muted>Type: <main><type>",
                        " <rail>\u2503 <muted>Time: <main><time>",
                        "",
                        " <rail>\u2503 <muted>Status: <main><status>",
                        " <rail>\u2503 <muted>Punished By: <main><resolvedBy>",
                        "",
                        "&a\u00bb <middle>Right click to view evidence.",
                        "<main>Drop to delete.");

        public static class REPORT_STATUS {

            public static String OPEN = "&aOpen";

            public static String IN_PROGRESS = "&6In Progress";

            public static String RESOLVED = "&9Resolved";

            public static String PUNISHED = "<main>Punished";
        }

        public static class STATUS_BUTTON {

            public static String NAME = "<status>";

            public static List<String> LORE =
                    Arrays.asList("<bright>Click to update the report status to <status>.");

            public static List<String> PUNISH_LORE =
                    Arrays.asList(
                            "<main>&lPunishment:",
                            " <rail>\u2503 <muted>Type: <main><type>",
                            " <rail>\u2503 <muted>Time: <main><time>",
                            "",
                            "<bright>Click to punish <main><player><bright>.");
        }
    }

    @Comment("The report reason picker, shown by /report.")
    public static class REPORT_MENU {

        public static String TITLE = "<main>&lREPORT<main> <rail>\u00bb <middle>Reason";

        public static String ITEM_NAME = "<main><category>";

        @Comment("The reason a player types themselves, when custom reasons are on.")
        public static String CUSTOM_ITEM_NAME = "&eSomething else";

        public static List<String> CUSTOM_ITEM_LORE =
                Arrays.asList(
                        " <rail>\u2503 <middle>Type the reason yourself.",
                        "",
                        "&a\u00bb <middle>Left click to report <target>.");

        public static String CUSTOM_MATERIAL = "WRITABLE_BOOK";

        @Comment("The line above the reasons on the report form.")
        public static String FORM_SUBTITLE = "<muted>Reporting <bright><target><muted>.";

        public static List<String> ITEM_LORE =
                Arrays.asList(
                        " <rail>\u2503 <middle><description>",
                        "",
                        "&a\u00bb <middle>Left click to report <target>.");

        public static String CLOSE_ITEM_NAME = "<main>Close";

        public static List<String> CLOSE_ITEM_LORE =
                Arrays.asList(" <rail>\u2503 <middle>Close the menu.");

        @Comment(
                "If this is false a default paginated menu will be used, otherwise use the layout below")
        public static boolean CUSTOM_LAYOUT = true;

        @Comment({
            "x is a Report Category, # is a border and c is the close button",
            "You can have a maximum of 54 categories in this menu."
        })
        public static List<String> LAYOUT =
                Arrays.asList("#########", "# xxxxx #", "# xxxxx #", "#   c   #", "#########");
    }

    public static class SERVER_INFO {

        public static String SERVERS_IN_GROUP_TITLE =
                "<main>&lSERVERS<main> <rail>\u00bb <middle><group>";

        public static String SPIGOT_SERVER_NAME = "<main><name>";

        public static List<String> SPIGOT_SERVER_INFO_ONLINE =
                Arrays.asList(
                        " <rail>\u2503 <muted>Name: <main><name>",
                        " <rail>\u2503 <muted>Version: <main><version>",
                        " <rail>\u2503 <muted>Region: <main><region>",
                        " <rail>\u2503 <muted>Group: <main><group>",
                        " <rail>\u2503 <muted>Port: <main><port>",
                        "",
                        "<main>&lStatus <main><muted>(Uptime: <uptime>)",
                        " <rail>\u2503 <muted>Online: <main><online> <muted>(Counting: <countingPlayers>)",
                        " <rail>\u2503 <muted>Vanished: <main><vanished>",
                        " <rail>\u2503 <muted>Started At: <main><startedAt>",
                        " <rail>\u2503 <muted>Last Update: <main><lastUpdate>",
                        "",
                        "<main>Left click to stop <secondary>&n<server><main>.",
                        "<main>Right click to view analytics.");

        public static List<String> SPIGOT_SERVER_INFO_WHITELISTED =
                Arrays.asList(
                        " <rail>\u2503 <muted>Name: <main><name>",
                        " <rail>\u2503 <muted>Version: <main><version>",
                        " <rail>\u2503 <muted>Region: <main><region>",
                        " <rail>\u2503 <muted>Group: <main><group>",
                        " <rail>\u2503 <muted>Port: <main><port>",
                        "",
                        "<main>&lStatus <main><muted>(Uptime: <uptime>)",
                        " <rail>\u2503 <muted>Online: <main><online> <muted>(Counting: <countingPlayers>)",
                        " <rail>\u2503 <muted>Vanished: <main><vanished>",
                        " <rail>\u2503 <muted>Started At: <main><startedAt>",
                        " <rail>\u2503 <muted>Last Update: <main><lastUpdate>",
                        "",
                        "<main>&lWhitelisted",
                        " <rail>\u2503 <muted>Access: <main><access>",
                        " <rail>\u2503 <muted>Rank: <main><rank>",
                        "",
                        "<main>Left click to stop <secondary>&n<server><main>.",
                        "<main>Right click to view analytics.");

        public static List<String> SPIGOT_SERVER_INFO_OFFLINE =
                Arrays.asList(
                        " <rail>\u2503 <muted>Name: <main><name>",
                        " <rail>\u2503 <muted>Version: <main><version>",
                        " <rail>\u2503 <muted>Region: <main><region>",
                        " <rail>\u2503 <muted>Group: <main><group>",
                        " <rail>\u2503 <muted>Port: <main><port>",
                        "",
                        "<main>&lStatus",
                        " <rail>\u2503 <muted>Stopped At: <main><stoppedAt>",
                        " <rail>\u2503 <muted>Last Online: <main><lastUpdate>",
                        "",
                        "<main>Middle click to remove <secondary>&n<server><main> from cache.",
                        "<main>Right click to view analytics.");

        public static String PROXIES_IN_GROUP_TITLE =
                "<main>&lPROXIES<main> <rail>\u00bb <middle><group>";

        public static String PROXY_SERVER_NAME = "<main>&l<name>";

        public static List<String> PROXY_SERVER_INFO_ONLINE =
                Arrays.asList(
                        " <rail>\u2503 <muted>Name: <main><name>",
                        " <rail>\u2503 <muted>Version: <main><version>",
                        " <rail>\u2503 <muted>Region: <main><region>",
                        " <rail>\u2503 <muted>Group: <main><group>",
                        " <rail>\u2503 <muted>Port: <main><port>",
                        "",
                        "<main>&lStatus <main><muted>(Uptime: <uptime>)",
                        " <rail>\u2503 <muted>Online: <main><online> <muted>(Counting: <countingPlayers>)",
                        " <rail>\u2503 <muted>Vanished: <main><vanished>",
                        " <rail>\u2503 <muted>Started At: <main><startedAt>",
                        " <rail>\u2503 <muted>Last Update: <main><lastUpdate>",
                        "",
                        "<main>Right click to view analytics.");

        public static List<String> PROXY_SERVER_INFO_WHITELISTED =
                Arrays.asList(
                        " <rail>\u2503 <muted>Name: <main><name>",
                        " <rail>\u2503 <muted>Version: <main><version>",
                        " <rail>\u2503 <muted>Region: <main><region>",
                        " <rail>\u2503 <muted>Group: <main><group>",
                        " <rail>\u2503 <muted>Port: <main><port>",
                        "",
                        "<main>&lStatus <main><muted>(Uptime: <uptime>)",
                        " <rail>\u2503 <muted>Online: <main><online> <muted>(Counting: <countingPlayers>)",
                        " <rail>\u2503 <muted>Vanished: <main><vanished>",
                        " <rail>\u2503 <muted>Started At: <main><startedAt>",
                        " <rail>\u2503 <muted>Last Update: <main><lastUpdate>",
                        "",
                        "<main>&lWhitelisted",
                        " <rail>\u2503 <muted>Access: <main><access>",
                        " <rail>\u2503 <muted>Rank: <main><rank>",
                        "",
                        "<main>Right click to view analytics.");

        public static List<String> PROXY_SERVER_INFO_OFFLINE =
                Arrays.asList(
                        " <rail>\u2503 <muted>Name: <main><name>",
                        " <rail>\u2503 <muted>Version: <main><version>",
                        " <rail>\u2503 <muted>Region: <main><region>",
                        " <rail>\u2503 <muted>Group: <main><group>",
                        " <rail>\u2503 <muted>Port: <main><port>",
                        "",
                        "<main>Status",
                        " <rail>\u2503 <muted>Stopped At: <main><stoppedAt>",
                        " <rail>\u2503 <muted>Last Online: <main><lastUpdate>",
                        "",
                        "<main>Middle click to remove <secondary>&n<server><main> from cache.",
                        "<main>Right click to view analytics.");
    }

    public static class SETTINGS_MENU {

        public static String TITLE = "<main>&lSETTINGS";

        public static class ADMIN_ALERTS {

            public static String MATERIAL = "TRIPWIRE_HOOK";

            public static String NAME = "Admin Alerts";

            public static List<String> DESCRIPTION =
                    Arrays.asList(
                            " <rail>\u2503 <middle>Would you like to view",
                            " <rail>\u2503 <middle>admin alerts?");
        }

        public static class BOOLEAN_SETTING {

            public static String ENABLED_NAME = "<main>&l<name>";

            public static List<String> ENABLED_LORE =
                    Arrays.asList(
                            "<description>",
                            "",
                            " &a\u25a0 <bright>Enabled",
                            " <rail>\u25a0 <bright>Disabled",
                            "",
                            "&a\u00bb <middle>Click to disable.");

            public static String DISABLED_NAME = "<main>&l<name>";

            public static List<String> DISABLED_LORE =
                    Arrays.asList(
                            "<description>",
                            "",
                            " <rail>\u25a0 <bright>Enabled",
                            " &a\u25a0 <bright>Disabled",
                            "",
                            "&a\u00bb <middle>Click to enable.");

            public static List<String> NO_PERMISSION_ENABLED =
                    Arrays.asList(
                            "<description>",
                            "",
                            " &a\u25a0 <bright>Enabled",
                            " <rail>\u25a0 <bright>Disabled",
                            "",
                            "<main>No permission.");

            public static List<String> NO_PERMISSION_DISABLED =
                    Arrays.asList(
                            "<description>",
                            "",
                            " <rail>\u25a0 <bright>Enabled",
                            " &a\u25a0 <bright>Disabled",
                            "",
                            "<main>No permission.");
        }

        public static class CHAT_CHANNEL {

            public static String MATERIAL = "OAK_SIGN";

            public static String NAME = "Chat Channel";

            public static List<String> DESCRIPTION =
                    Arrays.asList(
                            " <rail>\u2503 <middle>What channel would you like",
                            " <rail>\u2503 <middle>chat in?");
        }

        public static class CHAT_MENTION {

            public static boolean ENABLED = true;

            public static String MATERIAL = "ANVIL";

            public static String NAME = "Chat Mention";

            public static List<String> DESCRIPTION =
                    Arrays.asList(
                            " <rail>\u2503 <middle>Do you want to receive a message very",
                            " <rail>\u2503 <middle>time your name is mentioned in chat?");
        }

        public static class FILTERED_MESSAGES {

            public static String MATERIAL = "REDSTONE";

            public static String NAME = "Filtered Messages";

            public static List<String> DESCRIPTION =
                    Arrays.asList(
                            " <rail>\u2503 <middle>What you like to view",
                            " <rail>\u2503 <middle>filtered messages?");
        }

        public static class GLOBAL_CHAT {

            public static boolean ENABLED = true;

            public static String MATERIAL = "PAPER";

            public static String NAME = "Global Chat";

            public static List<String> DESCRIPTION =
                    Arrays.asList(
                            " <rail>\u2503 <middle>Do you want to be able to chat in global chat?");
        }

        @Comment({
            "Lets a player pick which language Phoenix speaks to them in.",
            "Only shown when a locale overlay is installed under locales/."
        })
        public static class LANGUAGE {

            public static Boolean ENABLED = true;

            public static String MATERIAL = "PAPER";

            public static String NAME = "Language";

            public static List<String> DESCRIPTION =
                    Arrays.asList(
                            " <rail>\u2503 <middle>Which language should we",
                            " <rail>\u2503 <middle>speak to you in?");
        }

        public static class MUTLI_OPTION_SETTING {

            public static String NAME = "<main>&l<name>";

            public static String OPTIONS_STRING = " <rail>\u25a0 <bright><option>";

            public static String OPTIONS_STRING_SELECTED = " &a\u25a0 <bright><option>";

            public static List<String> LORE =
                    Arrays.asList(
                            "<description>",
                            "",
                            "<options>",
                            "",
                            "&a\u00bb <middle>Click to change.");

            public static List<String> NO_PERMISSION_LORE =
                    Arrays.asList("<description>", "", "<options>", "", "<main>No permission.");
        }

        public static class NUMBER_SETTING {

            @Comment("How much should the number change by?")
            public static Integer CHANGE_BY = 10;

            public static String NAME = "<main>&l<name>";

            public static List<String> LORE =
                    Arrays.asList(
                            "<description>",
                            "",
                            " <rail>\u25a0 <bright><number>",
                            "",
                            "&a\u00bb <middle>Click to change.");

            public static List<String> NO_PERMISSION =
                    Arrays.asList(
                            "<description>",
                            "",
                            " <rail>\u25a0 <bright><number>",
                            "",
                            "<main>No permission.");
        }

        public static class PRIVATE_MESSAGES {

            public static boolean ENABLED = true;

            public static String MATERIAL = "WRITABLE_BOOK";

            public static String NAME = "Private Messages";

            public static List<String> DESCRIPTION =
                    Arrays.asList(
                            " <rail>\u2503 <middle>Do you want to receive private messages",
                            " <rail>\u2503 <middle>from other players?");
        }

        public static class PRIVATE_MESSAGE_SOUNDS {

            public static boolean ENABLED = true;

            public static String MATERIAL = "NOTE_BLOCK";

            public static String NAME = "Private Messages Sounds";

            public static List<String> DESCRIPTION =
                    Arrays.asList(
                            " <rail>\u2503 <middle>Do you want to hear a sound every time you",
                            " <rail>\u2503 <middle>receive a private message from other players?");
        }

        public static class REPORTS {

            public static String MATERIAL = "MAP";

            public static String NAME = "Reports";

            public static List<String> DESCRIPTION =
                    Arrays.asList(
                            " <rail>\u2503 <middle>Would you like to view",
                            " <rail>\u2503 <middle>reports?");
        }

        public static class REQUESTS {

            public static String MATERIAL = "ITEM_FRAME";

            public static String NAME = "Requests";

            public static List<String> DESCRIPTION =
                    Arrays.asList(
                            " <rail>\u2503 <middle>Would you like to view",
                            " <rail>\u2503 <middle>requests?");
        }

        public static class SELECT_OPTION_SETTING {

            public static String NAME = "&a<name>";

            public static String OPTIONS_STRING = "<rail>\u25a0 <bright><option>";

            public static String OPTIONS_STRING_SELECTED = " &a\u25a0 <bright><option>";

            public static String OPTION_STRING_SELECTING =
                    " <secondary>\u00bb <rail>\u25a0 <bright><option>";

            public static String OPTION_STRING_SELECTING_SELECTED =
                    " <secondary>\u00bb &a\u25a0 <bright><option>";

            public static List<String> LORE =
                    Arrays.asList(
                            "<description>",
                            "",
                            "<options>",
                            "",
                            "&a\u00bb <middle>Left/Right click to scroll.",
                            "&a\u00bb <middle>Shift click to select/deselect.");

            public static List<String> NO_PERMISSION_LORE =
                    Arrays.asList("<description>", "", "<options>", "", "<main>No permission.");
        }

        public static class STAFF_MESSAGES {

            public static String MATERIAL = "PAPER";

            public static String NAME = "Staff Messages";

            public static List<String> DESCRIPTION =
                    Arrays.asList(
                            " <rail>\u2503 <middle>What you like to view staff",
                            " <rail>\u2503 <middle>messages? (Staff Chat, etc)");
        }

        public static class TIPS {

            public static boolean ENABLED = true;

            public static String MATERIAL = "JUKEBOX";

            public static String NAME = "Tips";

            public static List<String> DESCRIPTION =
                    Arrays.asList(" <rail>\u2503 <middle>Do you want to be able to see tips?");
        }
    }

    @Comment({
        "The setup board -- what /phoenix setup opens for a player.",
        "The area names and the findings themselves are produced by the checks, not read from here."
    })
    public static class SETUP_MENU {

        public static String TITLE = "<main>&lPHOENIX SETUP";

        public static String FEATURE_TITLE = "<main>&lSETUP<main> <rail>\u00bb <middle>Features";

        public static String AREA_TITLE = "<main>&lSETUP<main> <rail>\u00bb <middle><area>";

        @Comment({
            "What each area is for, in an owner's terms rather than the plugin's.",
            "Read before the findings are, so somebody who has never configured Phoenix",
            "knows what the area is about before being told what is wrong with it."
        })
        public static class ABOUT {

            public static List<String> STORAGE =
                    Arrays.asList(
                            "MongoDB or MySQL holds the profiles, punishments and grants.",
                            "Redis carries them between servers. Nothing works until both answer.");

            public static List<String> IDENTITY =
                    Arrays.asList(
                            "What this server calls itself. Scoped grants and per-server",
                            "punishments both target this name, and both fail quietly when it is wrong.");

            public static List<String> RANKS =
                    Arrays.asList(
                            "The ladder you grant from. Prefixes, permissions, staff lists",
                            "and staff nametags all read it.");

            public static List<String> COLOURS =
                    Arrays.asList(
                            "Three codes every message, menu and MOTD is built from.",
                            "Setting them recolours the whole plugin at once.");

            public static List<String> BYPASSES =
                    Arrays.asList(
                            "Who is exempt from the filters, cooldowns and slowmode.",
                            "Skipped now, this returns as staff who cannot talk in their own slowmode.");

            public static List<String> FEATURES =
                    Arrays.asList(
                            "What Phoenix is running. There is no correct answer here:",
                            "a network that uses the reports and nothing else is configured, not broken.");

            public static List<String> CONFIG_MASTER =
                    Arrays.asList(
                            "Which server owns the wording. The others copy messages, menus",
                            "and webhooks from it, so an edit made anywhere else is overwritten.");

            public static List<String> REPORTS =
                    Arrays.asList(
                            "What players can report each other for. Reports ship switched on",
                            "with no categories, which is what makes /report open an empty picker.");

            public static List<String> LADDERS =
                    Arrays.asList(
                            "What a second offence costs, and a third. Without a ladder staff",
                            "guess a duration, and two players doing the same thing get different answers.");

            public static List<String> FILTERS =
                    Arrays.asList(
                            "The phrases chat refuses, and what happens to whoever typed one.",
                            "A filter set to punish with no ladder behind it catches and then does nothing.");

            public static List<String> STAFF_TOOLS =
                    Arrays.asList(
                            "The inventory staff moderate from, and the alarm that watches how",
                            "fast punishments are going out when a staff account is not its owner.");

            public static List<String> TWO_FACTOR =
                    Arrays.asList(
                            "What stands between a stolen staff password and your ban command.",
                            "The one area here whose shipped state is the safe one.");

            public static List<String> ALTS =
                    Arrays.asList(
                            "How second accounts are tied to the first. The weights and the kick",
                            "threshold have to agree, or evaders walk in and innocents are kicked.");

            public static List<String> ANTI_VPN =
                    Arrays.asList(
                            "Whether joins through a VPN are checked. Read here and acted on by",
                            "the proxy, so this server can show the settings and nothing else.");

            public static List<String> TAGS =
                    Arrays.asList(
                            "The cosmetic tags players pick between. Never broken -- but the menu",
                            "is reachable whether or not any exist, and empty reads as a fault.");

            public static List<String> TABLIST =
                    Arrays.asList(
                            "The header, footer and nametags players see. Ships pointing at",
                            "Phoenix's own Discord and website until you replace it.");

            public static List<String> WEBHOOKS =
                    Arrays.asList(
                            "The Discord feeds for punishments, reports, grants and staff.",
                            "An empty url fails silently, because nothing was asked for.");

            public static List<String> LOCALES =
                    Arrays.asList(
                            "Translations, as overlay files holding only the lines you change.",
                            "One language is a complete answer; this area exists so you know the option is there.");

            public static List<String> METRICS =
                    Arrays.asList(
                            "Pushing punishment, report and login counts to InfluxDB for Grafana.",
                            "Off is the shipped state and a perfectly good one.");

            public static List<String> MODERN =
                    Arrays.asList(
                            "Settings that need a new enough server: pause-menu links, tab list",
                            "order, native forms. Each is skipped for clients too old for it.");
        }

        @Comment({
            "The buttons along the bottom of an area. <action> is the name of the change,",
            "and the lore explains what it writes before anything is written."
        })
        public static class ACTION {

            public static String NAME = "&a&l<action>";

            public static List<String> LORE =
                    Arrays.asList(
                            "",
                            "<current>",
                            "<description>",
                            "",
                            "&a\u00bb <middle>Click to change.");

            public static String CURRENT = " <rail>\u2503 <muted>Now: <main><value>";

            public static String DESCRIPTION = " <rail>\u2503 <muted><line>";

            public static String RESTART = " <rail>\u2503 &eOnly in effect after a restart.";
        }

        @Comment({
            "One item per area on the board. <checks> expands to one CHECK line per finding,",
            "each followed by its DETAIL and FIX lines when it has them."
        })
        public static class AREA {

            public static String NAME = "<status>&l<area>";

            public static List<String> LORE =
                    Arrays.asList("", "<about>", "", "<checks>", "&a\u00bb <middle>Click to open.");

            public static String ABOUT = " <rail>\u2503 <bright><line>";

            public static String CHECK = " <status>\u2503 <muted><message>";

            public static String DETAIL = "   <rail><message>";

            public static String FIX = "   <muted>fix: <bright><fix>";

            public static String DOCS =
                    "&a\u00bb <middle>Click for the guide on docs.refinedev.org.";
        }

        public static class BOARD {

            public static String NAME = "<bright>&lBack to the board";

            public static List<String> LORE =
                    Arrays.asList(
                            "",
                            " <rail>\u2503 <middle>Every area at once.",
                            "",
                            "&a\u00bb <middle>Click to go back.");
        }

        @Comment(
                "The rank picker the Bypasses area opens. Ranks that already hold everything are still listed.")
        public static class BYPASS {

            public static String TITLE = "<main>&lSETUP<main> <rail>\u00bb <middle>Bypasses";

            public static String RANK_NAME = "<main>&l<rank>";

            public static List<String> RANK_LORE =
                    Arrays.asList(
                            "",
                            " <rail>\u2503 <muted>Missing: <main><missing> <middle>of <main><total>",
                            "",
                            "<nodes>",
                            "",
                            "&a\u00bb <middle>Click to grant the missing ones.");

            public static String NODE = " <rail>\u2503 <muted><node>";

            public static List<String> RANK_HOLDS_ALL_LORE =
                    Arrays.asList(
                            "",
                            " <rail>\u2503 <middle>This rank already holds all <main><total><middle>.",
                            "",
                            "<muted>Nothing to grant.");
        }

        @Comment("One item per finding, inside an area.")
        public static class CHECK {

            public static String NAME = "<status>&l<label>";

            public static List<String> LORE =
                    Arrays.asList("", "<message>", "<detail>", "", "<muted>fix: <bright><fix>");

            public static String NO_FIX = "<muted>Nothing to do here.";
        }

        @Comment({
            "The item shown beside the yes and no buttons, so nothing is replaced unseen.",
            "<lines> expands to the change itself, which the action works out rather than reading here."
        })
        public static class CONFIRM {

            public static String TITLE =
                    "<main>&lSETUP<main> <rail>\u00bb <middle>Apply this change?";

            public static String NAME = "<bright>&l<action>";

            public static List<String> LORE = Arrays.asList("", "<lines>");

            public static String FROM = " <rail>\u2503 <muted>From: <main><value>";

            public static String TO = " <rail>\u2503 <muted>To: <main><value>";

            public static String NOTE = " <rail>\u2503 <muted><note>";
        }

        @Comment({
            "What each feature does, shown on its switch in the Features area.",
            "An owner deciding whether to run something should not have to find its",
            "config section to learn what it is."
        })
        public static class FEATURES {

            public static List<String> BASICS =
                    Arrays.asList(
                            "Fly, heal, teleports, item tools, gamemodes,",
                            "broadcasts and tips. Off unregisters all of them.");

            public static List<String> VANISH =
                    Arrays.asList(
                            "Hides staff from players and from each other,",
                            "down at the packet level.");

            public static List<String> MODMODE =
                    Arrays.asList(
                            "The moderating inventory staff get on /modmode,",
                            "with the tools laid out in modmode.yml.");

            public static List<String> FREEZE =
                    Arrays.asList(
                            "Holds a suspected cheater in place while staff", "talk to them.");

            public static List<String> REPORTS =
                    Arrays.asList(
                            "Player reports and help requests, filed under",
                            "the categories you create.");

            public static List<String> PANIC =
                    Arrays.asList(
                            "Locks a staff account down until another staff",
                            "member clears it, for a login gone missing.");

            public static List<String> CHAT_CONTROL =
                    Arrays.asList(
                            "/clearchat, /mutechat and /slowchat. Moderating",
                            "the chat itself, not how it is rendered.");

            public static List<String> BAN_WAVES =
                    Arrays.asList(
                            "Punishments staged now and landed together later,",
                            "so a sweep does not warn the next account.");
        }

        @Comment("The walkthrough: from any area, jump to the next one with something left to do.")
        public static class NEXT {

            public static String NAME = "&e&lNext: <main><area>";

            public static List<String> LORE =
                    Arrays.asList(
                            "",
                            " <rail>\u2503 <muted>Status: <status><label>",
                            "",
                            "&a\u00bb <middle>Click to go there.");

            public static String NONE_NAME = "&a&lNothing left here";

            public static List<String> NONE_LORE =
                    Arrays.asList(
                            "",
                            " <rail>\u2503 <middle>Every area on this board is configured.",
                            "",
                            "<muted>Nothing to jump to.");
        }

        @Comment("Shown in the Storage area, which never asks for credentials in game.")
        public static class READ_ONLY {

            public static String NAME = "<muted>&lNothing to change here";

            public static List<String> LORE =
                    Arrays.asList(
                            "",
                            " <rail>\u2503 <middle>Setup never asks for a database or redis",
                            " <rail>\u2503 <middle>password in game, because anything typed here",
                            " <rail>\u2503 <middle>is written to the chat and command logs.",
                            "",
                            "<muted>Edit <main><file> <muted>instead.");
        }

        public static class REFRESH {

            public static String NAME = "&e&lCheck again";

            public static List<String> LORE =
                    Arrays.asList(
                            "",
                            " <rail>\u2503 <middle>Nothing is remembered between opens.",
                            " <rail>\u2503 <middle>Every status is read fresh.",
                            "",
                            "&a\u00bb <middle>Click to re-run the checks.");
        }

        public static class REPORT {

            public static String NAME = "<bright>&lPrint to chat";

            public static List<String> LORE =
                    Arrays.asList(
                            "",
                            " <rail>\u2503 <middle>The same findings as text, with the",
                            " <rail>\u2503 <middle>fix for each one spelled out.",
                            "",
                            "&a\u00bb <middle>Click to print.");
        }

        @Comment(
                "The colour and the word each status is drawn with, on both the board and inside an area.")
        public static class STATUS {

            public static String OK = "&a";

            public static String OK_LABEL = "Done";

            public static String TODO = "&b";

            public static String TODO_LABEL = "Not started";

            public static String WARN = "&e";

            public static String WARN_LABEL = "Worth a look";

            public static String FAIL = "<main>";

            public static String FAIL_LABEL = "Broken";
        }

        @Comment("The book at the top of the board. <next> is the area to open first.")
        public static class SUMMARY {

            public static String NAME = "<main>&lPhoenix Setup";

            public static List<String> LORE =
                    Arrays.asList(
                            "",
                            " <rail>\u2503 <muted>Done: &a<ok>",
                            " <rail>\u2503 <muted>Not started: &b<todo>",
                            " <rail>\u2503 <muted>Worth a look: &e<warn>",
                            " <rail>\u2503 <muted>Broken: <main><fail>",
                            "",
                            " <rail>\u2503 <muted>Next: <main><next>",
                            "",
                            "&a\u00bb <middle>Click to open it.");

            public static String NEXT_NONE = "nothing, the essentials are configured";

            public static List<String> DONE_LORE =
                    Arrays.asList(
                            "",
                            " <rail>\u2503 <muted>Done: &a<ok>",
                            "",
                            " <rail>\u2503 <middle>Every essential area is configured.",
                            " <rail>\u2503 <middle>Open one to change what it holds.");
        }

        @Comment("Moves between the two boards: the seven essentials, and one area per feature.")
        public static class TIER {

            public static String TO_FEATURES_NAME = "&b&lFeature areas";

            public static List<String> TO_FEATURES_LORE =
                    Arrays.asList(
                            "",
                            " <rail>\u2503 <middle>One area per feature, for when you",
                            " <rail>\u2503 <middle>are setting that feature up.",
                            "",
                            "&a\u00bb <middle>Click to open.");

            public static String TO_ESSENTIALS_NAME = "&a&lEssentials";

            public static List<String> TO_ESSENTIALS_LORE =
                    Arrays.asList(
                            "",
                            " <rail>\u2503 <middle>The seven a network cannot open without.",
                            "",
                            "&a\u00bb <middle>Click to open.");
        }
    }

    public static class SOCIAL_MEDIA {

        public static String SELF_TITLE = "<main>&lSOCIAL MEDIA";

        public static String TITLE = "<main>&lSOCIAL MEDIA<main> <rail>\u00bb <middle><profile>";

        public static String NAME = "<color><name>";

        public static String DISCORD_NAME = "Discord";

        public static String TWITTER_NAME = "Twitter";

        public static String INSTAGRAM_NAME = "Instagram";

        public static String YOUTUBE_NAME = "YouTube";

        public static List<String> LORE =
                Arrays.asList(
                        " <rail>\u2503 <middle><current>",
                        "",
                        "&a\u00bb <middle>Click to get link.");

        public static List<String> SELF_LORE =
                Arrays.asList(
                        " <rail>\u2503 <middle><current>",
                        "",
                        " <rail>\u2503 <muted>Privacy: <secondary><privacy>",
                        "",
                        "&a\u00bb <middle>Left click to change link and name.",
                        "&e\u00bb <middle>Middle click to change privacy.",
                        "<main>\u00bb <middle>Right click to delete link and name.");
    }

    public static class STAFF_HISTORY_MENU {

        public static String TITLE = "<main>&lISSUED";

        public static String ISSUED_TYPE_TITLE = "<muted><type>";

        public static String REMOVED_TYPE_TITLE = "<muted>Removed <type>";

        public static String CATEGORY_NAME = "<main><category>";

        public static List<String> CATEGORY_LORE =
                Arrays.asList(
                        "<muted>View all <bright><category> <muted>that were",
                        "<muted>executed by <main><player><muted>.",
                        "",
                        " <rail>\u2503 <muted>Active: &a<active>",
                        " <rail>\u2503 <muted>Inactive: <main><inactive>",
                        " <rail>\u2503 <muted>Total: <main><total>",
                        "",
                        " <rail>\u2503 <muted>Affected Users: <main><affectedUsers>",
                        "",
                        "&a\u00bb <middle>Click to view this category.");

        public static List<String> CATEGORY_LORE_WITH_COMPETITIVE =
                Arrays.asList(
                        "<muted>View all <bright><category> <muted>that were",
                        "<muted>executed by <main><player><muted>.",
                        "",
                        " <rail>\u2503 <muted>Competitive: &e<competitive>",
                        "",
                        " <rail>\u2503 <muted>Active: &a<active>",
                        " <rail>\u2503 <muted>Inactive: <main><inactive>",
                        " <rail>\u2503 <muted>Total: <main><total>",
                        "",
                        " <rail>\u2503 <muted>Affected Users: <main><affectedUsers>",
                        "",
                        "&a\u00bb <middle>Click to view this category.");

        public static String COMPETITIVE = "&e (Competitive)";

        public static String SHADOW = "<rail> (Shadow)";

        public static String IP = "<muted> (IP)";

        public static List<String> PROOF_CLICK =
                Arrays.asList("", "&e\u00bb <middle>Click to view attached proof.");

        public static class ACTIVE_STAFF_PUNISHMENT {

            public static String NAME =
                    "&a+<issuedAt><ip><shadow><competitive> (Active) <bright>[#<punishmentID>]";

            public static List<String> PERMANENT =
                    Arrays.asList(
                            "",
                            " <rail>\u2503 <muted>Target: <main><target>",
                            " <rail>\u2503 <muted>Duration: <main><duration>",
                            "",
                            " <rail>\u2503 <muted>Issued By: <main><issuedBy>",
                            " <rail>\u2503 <muted>Issued On: <main><issuedOn>",
                            " <rail>\u2503 <muted>Issued Reason: <main><reason>");

            public static List<String> TIMED =
                    Arrays.asList(
                            "&a=<willExpire>",
                            "",
                            " <rail>\u2503 <muted>Target: <main><target>",
                            " <rail>\u2503 <muted>Duration: <main><duration>",
                            " <rail>\u2503 <muted>Expires In: <main><expire>",
                            " <rail>\u2503 <muted>Will expire on: <main><willExpire>",
                            "",
                            " <rail>\u2503 <muted>Issued By: <main><issuedBy>",
                            " <rail>\u2503 <muted>Issued On: <main><issuedOn>",
                            " <rail>\u2503 <muted>Issued Reason: <main><reason>");
        }

        public static class INACTIVE_STAFF_PUNISHMENT {

            public static String NAME =
                    "<main>+<issuedAt><ip><shadow><competitive> (Inactive) <bright>[#<punishmentID>]";

            public static String EXPIRED_NAME =
                    "<muted>+<issuedAt><ip><shadow><competitive> (Expired) <bright>[#<punishmentID>]";

            public static String VOIDED_NAME =
                    "&6+<issuedAt><ip><shadow><competitive> (Voided) <bright>[#<punishmentID>]";

            public static List<String> REMOVED =
                    Arrays.asList(
                            "<main>-<removedAt>",
                            "",
                            " <rail>\u2503 <muted>Target: <main><target>",
                            " <rail>\u2503 <muted>Duration: <main><duration>",
                            "",
                            " <rail>\u2503 <muted>Issued By: <main><issuedBy>",
                            " <rail>\u2503 <muted>Issued On: <main><issuedOn>",
                            " <rail>\u2503 <muted>Issued Reason: <main><reason>",
                            "",
                            " <rail>\u2503 <muted>Removed By: <main><removedBy>",
                            " <rail>\u2503 <muted>Removed On: <main><removedOn>",
                            " <rail>\u2503 <muted>Removed Reason: <main><removedReason>");

            public static List<String> EXPIRED =
                    Arrays.asList(
                            "<muted>-<removedAt>",
                            "",
                            " <rail>\u2503 <muted>Target: <main><target>",
                            " <rail>\u2503 <muted>Duration: <main><duration>",
                            "",
                            " <rail>\u2503 <muted>Issued By: <main><issuedBy>",
                            " <rail>\u2503 <muted>Issued On: <main><issuedOn>",
                            " <rail>\u2503 <muted>Issued Reason: <main><reason>");

            public static List<String> VOIDED =
                    Arrays.asList(
                            "&6-<removedAt>",
                            "",
                            " <rail>\u2503 <muted>Target: <main><target>",
                            " <rail>\u2503 <muted>Duration: <main><duration>",
                            "",
                            " <rail>\u2503 <muted>Issued By: <main><issuedBy>",
                            " <rail>\u2503 <muted>Issued On: <main><issuedOn>",
                            " <rail>\u2503 <muted>Issued Reason: <main><reason>",
                            "",
                            " <rail>\u2503 <muted>Voided By: <main><removedBy>",
                            " <rail>\u2503 <muted>Voided On: <main><removedOn>",
                            " <rail>\u2503 <muted>Voided As: <main><voidReason>",
                            " <rail>\u2503 <muted>Note: <main><voidNote>");
        }
    }

    public static class TAGS_MENU {

        public static String CATEGORY_NAME = "<main>&lCategory";

        public static List<String> CATEGORY_LORE =
                Arrays.asList("", "<list>", "", "&a\u00bb <middle>Click to change.");

        public static String CATEGORY_SELECTED = " &a\u25a0 <bright>";

        public static String CATEGORY_UNSELECTED = " <rail>\u25a0 <bright>";

        public static String TITLE = "<main>&lTAGS";

        public static String SELECTED_NAME = " <muted>(Selected)";

        public static String NAME = "<color><name><selected>";

        public static List<String> ACCESS =
                Arrays.asList(
                        "",
                        "<main>&lTag Format:",
                        " <rail>\u2503 <tag>",
                        "",
                        "&a\u00bb <middle>Click to select.");

        public static List<String> NO_ACCESS =
                Arrays.asList(
                        "",
                        "<main>&lTag Format:",
                        " <rail>\u2503 <tag>",
                        "",
                        "<main>No permission.");

        public static String RESET_NAME = "<main>&lRemove Tag";

        public static List<String> RESET_LORE =
                Arrays.asList(
                        "",
                        " <rail>\u2503 <muted>Selected: <main><tag>",
                        "",
                        "&a\u00bb <middle>Click to reset.");
    }

    public static class TEXT_EDITOR_MENU {

        public static class LINE_SLOT {

            public static List<String> LORE =
                    Arrays.asList(
                            "",
                            "&a\u00bb <middle>Left click to edit.",
                            "<main>\u00bb <middle>Right click to delete.");
        }

        public static class SAVE_SLOT {

            public static String CAN_SAVE_NAME = "&a&lSave";

            public static String CANT_SAVE_NAME = "<main>&lNo Changes Made";

            public static List<String> CAN_SAVE =
                    Arrays.asList("", "&a\u00bb <middle>Click to save.");

            public static List<String> CANT_SAVE =
                    Arrays.asList(
                            "",
                            "<muted>&oYou have not made any",
                            "<muted>&ochanges and cannot save.");
        }
    }

    @Comment("Shown to a warned player, on clients that can be held on a screen.")
    public static class WARN_SCREEN {

        public static String NAME = "<main>You have been warned.";

        public static String ACCEPT = "&aI understand";
    }

    @Comment("The players a pile of reports put on the watchlist, /watchlist.")
    public static class WATCHLIST_MENU {

        public static String TITLE = "<main>&lWATCHLIST";

        public static String NAME = "<player>";

        public static List<String> LORE =
                Arrays.asList(
                        " <rail>\u2503 <muted>Status: <main><status>",
                        " <rail>\u2503 <muted>Reports: <main><reports>",
                        "",
                        "&a\u00bb <middle>Click to remove.");
    }
}
