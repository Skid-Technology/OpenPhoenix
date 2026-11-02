package xyz.refinedev.phoenix.config;

import xyz.refinedev.lib.config.ConfigHandler;
import xyz.refinedev.lib.config.StaticConfig;
import xyz.refinedev.lib.config.StaticConfig.Priority;
import xyz.refinedev.phoenix.utils.version.VersionUtil;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SettingsConfig extends StaticConfig {

    @Ignore public static SettingsConfig INSTANCE;

    public SettingsConfig(ConfigHandler configHandler) {
        super(new File("plugins/Phoenix/settings.yml"), configHandler);
        INSTANCE = this;
    }

    @Priority(5)
    @Comment("Two Factor Authentication Settings")
    public static class AUTH {

        @Comment("Should 2fa be enabled?")
        public static boolean ENABLED = true;

        @Comment("Default 2FA method.")
        public static String DEFAULT_METHOD = "QRCode";

        @Comment("Should people be able to use Authy, Google Auth etc for 2fa?")
        public static boolean QRCODE_ENABLED = true;

        @Comment("Should people be able to use security keys for 2fa?")
        public static boolean YUBIKEY_ENABLED = false;

        @Comment("Only for Yubi key auth")
        public static int YUBIKEY_ID = 0;

        public static String YUBIKEY_SECRET = "";

        @Comment("Only for QR Code auth")
        public static String NAME = "Server";

        @Comment("How many failed auth attempts should a user have.")
        public static int ATTEMPTS = 3;

        public static String FAILED_COMMAND = "blacklist <user> [Security] Compromised Account -s";

        public static List<String> ALLOWED_COMMANDS =
                Arrays.asList("setup2fa", "2fasetup", "2fa", "link", "auth");

        public static List<String> ALLOWED_PERMISSIONS = Arrays.asList("core.2fa");
    }

    @Priority(13)
    @Comment({
        "Punishments staged now and applied together later, so a sweep does not",
        "trickle out and give the next account time to be warned."
    })
    public static class BAN_WAVES {

        public static boolean ENABLED = true;

        @Comment({
            "How many accounts are punished per tick when a wave breaks.",
            "A large wave landing in one tick is a visible stall."
        })
        public static int BATCH_SIZE = 10;

        @Comment("How often a scheduled wave is checked for, in seconds.")
        public static int CHECK_EVERY = 30;

        @Comment({
            "Keep a wave open at all times: when one goes out, the next is opened",
            "and scheduled straight away, so there is always somewhere to stage an",
            "account without anyone remembering to make one."
        })
        public static class RECURRING {

            public static boolean ENABLED = false;

            @Comment({
                "When the next wave goes out. Either a day and time that repeats,",
                "written as FRIDAY 18:00, or a plain duration like 7d."
            })
            public static String EVERY = "FRIDAY 18:00";

            @Comment("What the opened waves are called. <date> is the day it goes out.")
            public static String NAME = "wave-<date>";
        }
    }

    @Priority(9)
    @Comment({
        "The essentials that used to live in pxBasic: fly, heal, teleports,",
        "item tools, gamemodes, broadcasts, tips and the rest. Turning this",
        "off unregisters every one of those commands on the next restart."
    })
    public static class BASICS {

        public static boolean ENABLED = true;

        @Comment("Allow /build toggling build mode metadata.")
        public static boolean BUILDMODE = true;

        @Comment("Show the welcome message from messages.yml on join.")
        public static boolean WELCOME_MESSAGE = true;

        @Comment("Whether /heal also clears potion effects.")
        public static boolean HEAL_RESET_POTION_EFFECTS = true;

        @Comment("Periodic gameplay tips from tips.yml, for players with tips enabled.")
        public static class TIPS {

            public static boolean ENABLED = true;

            @Comment("Seconds between tips.")
            public static int SEND_EVERY = 500;
        }
    }

    @Priority(15)
    @Comment({
        "Chat control, toggled at runtime by /mutechat and /slowchat.",
        "This is moderating the chat itself; how chat is rendered lives under modern.chat."
    })
    public static class CHAT_CONTROL {

        @Comment({"Should /clearchat, /mutechat and /slowchat be enabled?", "Requires a restart!"})
        public static boolean ENABLED = true;

        public static boolean MUTED = false;

        @Comment("Seconds a player must wait between messages, zero is off.")
        public static int SLOW_DELAY = 0;
    }

    @Priority(4)
    public static class COOLDOWNS {

        public static String DISGUISE = "2h";

        public static String REDISGUISE = "1h";

        public static String CHAT_SNAPSHOT = "2h";

        public static String COMMAND = "3s";

        public static String REPORT = "1m";

        public static String REQUEST = "1m";
    }

    @Priority(7)
    public static class DISGUISE {

        public static boolean DISGUISE_ON_JOIN = true;

        @Comment({
            "Send a fake uuid to other players, so the tab list cannot be used to unmask a disguise.",
            "Requires a restart!"
        })
        public static boolean HIDE_UUID = true;

        @Comment({
            "Log every tab entry a disguise rewrites, with the uuid before and after.",
            "Console spam; for diagnosing a name that will not leave the tab list."
        })
        public static boolean DEBUG = false;

        @Comment(
                "Allow players to disguise as players who have logged in before. Admins bypass this.")
        public static boolean DISGUISE_AS_REAL_PLAYER = true;

        @Comment({
            "Players will not be able to disguise as these names.",
            "All names must be LOWERCASE."
        })
        public static List<String> BLACKLISTED_NAMES = Arrays.asList("creaxx");

        @Comment("One of these skins will be randomly picked when disguising")
        public static List<String> SKINS =
                Arrays.asList(
                        "_rsu",
                        "E_Girl",
                        "burningbridge",
                        "BackupDancer",
                        "Arro",
                        "CocoDeMedellin",
                        "bennyknight",
                        "SheAGoldDigger",
                        "Marcel",
                        "Zwergoor",
                        "HikakinGames",
                        "Creaxx",
                        "SolluxCaptor",
                        "Dream",
                        "Dan86",
                        "Beanie215",
                        "GermanBanana",
                        "BlueHaro",
                        "i7M_",
                        "Av3ryx",
                        "iibakr_999z",
                        "4cop",
                        "Hacker",
                        "Alenh",
                        "2807",
                        "1Nex_",
                        "akuyano",
                        "vxo",
                        "8468",
                        "Active02",
                        "4ln",
                        "Agn1kk",
                        "0Fall",
                        "AztrixLeChiffre",
                        "Atletigio",
                        "AppealAccepted",
                        "bjarnator15",
                        "AndresRocks",
                        "baykurabiye",
                        "byLxrry",
                        "ALS_FireStrike",
                        "blodziak",
                        "asuoo",
                        "CryMyselfToSleep",
                        "Dejeh",
                        "Castellano",
                        "cyqo",
                        "cichy_ziom",
                        "Cali_Aigame06",
                        "Chill1ng",
                        "cosmcs",
                        "D3moteado_",
                        "Dark_Kun");

        @Comment("One of these names will be randomly picked when disguising")
        public static List<String> NAMES =
                Arrays.asList(
                        "01Dolars",
                        "03a0_",
                        "07_Games",
                        "0hAqua",
                        "0hPetitChat",
                        "0h_gal",
                        "1337Firanki",
                        "187koeftespiess",
                        "1B6ran_",
                        "1DeCapro",
                        "1eh_",
                        "3eii",
                        "3laws",
                        "4peewee",
                        "5VeX",
                        "62gg",
                        "6EVQ",
                        "9julian9",
                        "9Reflex",
                        "9teq",
                        "Agresivoqh",
                        "Ahmadmhameed11",
                        "AhmetStrafee",
                        "akemi___________",
                        "Alanance",
                        "Alargado",
                        "aleesad",
                        "altdeSWNA",
                        "anarqvissta_",
                        "angeclickt",
                        "Ankatuuwu",
                        "Ardaeren2009",
                        "AreuCrazyLol",
                        "areyouthicclol",
                        "ArhyYT",
                        "AroufGangsta_a",
                        "ASAP_JoacoB",
                        "at1976",
                        "AtomicRiceCooker",
                        "autoclickerzZz",
                        "AweWelico",
                        "AxneAPELAUM_",
                        "AxtrixG0D",
                        "Ayuko_0",
                        "AztecZombie",
                        "B0LDZZ_",
                        "babalillelam",
                        "BabuschkaPvP",
                        "BabyHwamz",
                        "BabyMustafis",
                        "BatmanPlayGames",
                        "berrychiii",
                        "BiggyChungu",
                        "biipmk",
                        "BinImCasino",
                        "BlackYear43",
                        "blffx",
                        "BongCai",
                        "BonnieFan5",
                        "boulderbaby76",
                        "Bruh_Itz_NoScope",
                        "BufuQQ",
                        "Builderkroete",
                        "BusteddPentaHit",
                        "BxA_LP",
                        "byronwastaken",
                        "CaitlinsHoe",
                        "CakeGamer_SK",
                        "CamperOfTheWeek",
                        "CARROUF__YP",
                        "cazestsu",
                        "Chainkilla",
                        "Chinakast",
                        "ChooseProfile",
                        "ChrelleFNBR",
                        "chxat",
                        "ClappedByChris",
                        "ClappedG",
                        "cniejivchvgnoip",
                        "Colookid",
                        "Computter",
                        "Coolboyandstrong",
                        "coos_exe",
                        "CopyPage",
                        "CoronaLouis",
                        "Corzi_",
                        "CPotwor",
                        "Crims0n_Eclipse",
                        "CrK_Rag",
                        "Crowseuuh",
                        "CuzImVollbuff",
                        "czar7eee",
                        "DaddyIogurte_",
                        "DaMellon_PvP",
                        "DarwinnFromArab",
                        "Das_butt3rBrot",
                        "davidsimps",
                        "davidwillraus",
                        "DeepPsycho",
                        "def4ult011",
                        "Desubicad0",
                        "Devil_Killzer",
                        "diablo3k16",
                        "Dipropor23",
                        "Disell_",
                        "Dist0rted_456te2",
                        "Dodvid",
                        "doktoramcoolsten",
                        "Donkxy",
                        "DontWasteMyIdeas",
                        "Dooooow_",
                        "dqtyyy",
                        "Dr1ke_",
                        "Dragi_x",
                        "DrinkThatPepsi",
                        "duc7492003",
                        "DustySway",
                        "DxrkMomo42150",
                        "Dylan_120",
                        "EgemenLxrd",
                        "EgirlAstro",
                        "EinenBoule",
                        "ElecTr1c77",
                        "ElFamosoJus2Kiwi",
                        "Elias_XZW",
                        "EllieAElijah",
                        "Elysiumdragon",
                        "El_Pinguinoo",
                        "em0t10nal",
                        "EmreKandemirHD",
                        "Enganchadoh",
                        "Eqster",
                        "eschuyaaa",
                        "Evanilli",
                        "fafixdwtf",
                        "FastedLyqer",
                        "FaZe_Bruh285",
                        "Filipek1337_",
                        "FilQGejmer2009",
                        "FinnoRus",
                        "fl0pi1",
                        "FlawCraBot00",
                        "Flettyexe",
                        "FooxzzSx",
                        "Fr1d3k",
                        "Frosty_90",
                        "Frqkandel",
                        "Frxeado",
                        "fsk99",
                        "Fujoshi__Spencer",
                        "FunnyVibes",
                        "GarotoLegit",
                        "gebr0chener",
                        "gGrimm_",
                        "GGyuMoo___",
                        "God0fHonnor",
                        "Goosser",
                        "Graf_Jubool",
                        "Graf_Loris",
                        "grayurr",
                        "GucciTas",
                        "Guioosrap",
                        "GullibleMrBob",
                        "Hacker_to_ja",
                        "HappyCat46",
                        "hat4ke",
                        "Hawaiijona",
                        "HerresMetMees",
                        "Hgfjfjk94",
                        "hogaspl",
                        "HolographicCloud",
                        "HonigOP",
                        "HoonMorningStar",
                        "Hussein112",
                        "HypixelCombo",
                        "HzSweax",
                        "iAmarusss_",
                        "IchKakeDichAn",
                        "iCufaah",
                        "idkPiif",
                        "iFinishLog",
                        "iFM_Karq",
                        "Iforgot123",
                        "IHealU_Unity",
                        "IIKqrstenII",
                        "iJuliann",
                        "IJUSTWANNAWIN",
                        "iLoveQp",
                        "iLuveAzdhar_",
                        "ilycaposta",
                        "ILYLuvxy",
                        "ILyWoofles",
                        "ImASleepyOne",
                        "iMattqx",
                        "ImDreamer_UwU",
                        "IMohProMC",
                        "ImTntDropper",
                        "ImVentr0x",
                        "Im_AczinoMC",
                        "InCrackSaneDonut",
                        "iNotStrafe",
                        "INSPECTEUR_Flash",
                        "iOnurKiller",
                        "iPerfectTrash_",
                        "iq_poleb",
                        "IrbxIsHigh",
                        "Irdius",
                        "iRivhn_",
                        "iStalker_",
                        "ISw3arG0d",
                        "ItsMessi_",
                        "ItsSxbaz",
                        "ItzDiabolico",
                        "ItzJustSeanLmao",
                        "ItzKvbaa",
                        "itzMxrb1e_",
                        "Itzz_Mimondo",
                        "iTz_D41N45",
                        "ixaan_22_uwu",
                        "iXlqw",
                        "Jalannelex",
                        "JoseTheMonkey",
                        "JottapeSz",
                        "Jqcco_",
                        "JuannLP",
                        "Jungk00ky",
                        "justdontsaythat",
                        "JustPandaXXL",
                        "JustPeque___",
                        "Jvhstin",
                        "Jxiro_o",
                        "k4jfa1",
                        "KaanxSad",
                        "KacerTatsu",
                        "KaiYuqii",
                        "Kapi99PL",
                        "Karmagott",
                        "KAYZEEN_N",
                        "KEKW_PLATYNASILV",
                        "Keldeoooo",
                        "kikooGLAD",
                        "Kiraiwa",
                        "KiritoSz",
                        "KleinesFishy",
                        "KnockByAweiik",
                        "Koksipold",
                        "Korpih",
                        "kpwtw",
                        "KrazyKonfuzion",
                        "Kuba_x09",
                        "KurokuShimasu",
                        "kvbakvn_",
                        "Kxyuna",
                        "Kyouko_Ko",
                        "L1L2R1R2",
                        "LACHAISES",
                        "Laored",
                        "LapinLyifte",
                        "LatencySkills_",
                        "latzhose187",
                        "lauchasaad",
                        "LDC_Frig0",
                        "Leagueman8",
                        "lecisznabombe",
                        "LeCoinsException",
                        "LeoPaulineU3U",
                        "LeqitHxfi",
                        "LilEsc0bar",
                        "LilNazke_",
                        "lilsheetyvert",
                        "littlegamerman8",
                        "lizukox_",
                        "loadingfailure",
                        "locito19",
                        "LookAtLeo_",
                        "Lotopethia",
                        "LucasMcc144",
                        "LuisTho",
                        "lxnne",
                        "M1N1K",
                        "M47m0ud",
                        "MadManChris20",
                        "Major_Miner0445",
                        "MANZORDZZ",
                        "marcelcio_priv",
                        "marcelot",
                        "marcin_646",
                        "MareCold",
                        "Maria250810",
                        "Mastertunder",
                        "Matjuu_",
                        "Matr0DaVinci_",
                        "Maxionf",
                        "mcaatu",
                        "MC_Igest",
                        "memedbyanttt",
                        "MemedByPepe",
                        "Miki_Milk",
                        "mio1325",
                        "ModePolizist",
                        "Mqster__",
                        "MrBanni",
                        "MrLembas_",
                        "Mr_Twixp",
                        "mttnz",
                        "Muhammed_Bro",
                        "Muhqmmedd",
                        "MustafaGamer07",
                        "MyHardCombo",
                        "MyNameIsBenMC",
                        "MystiCx_0",
                        "NaganoSenpai",
                        "nessiekittycat",
                        "NeverMoreAndres",
                        "nevetz36",
                        "Neyok",
                        "NicoChriss",
                        "niedostepny__",
                        "nieznajomy177",
                        "Nigcheek",
                        "NineQ",
                        "NkcpTheKait",
                        "NoHacksJustSofa",
                        "NohaxJustFruste",
                        "Noob0409",
                        "NotLegitaay_",
                        "NOTniikkk",
                        "NotSqnix",
                        "NotSwedish",
                        "NxtTommy",
                        "Ocean_Play",
                        "oEaster",
                        "OhAnqs_",
                        "oHIsseI",
                        "OkDag",
                        "okdenis",
                        "oluio",
                        "OmeR_AYT",
                        "onlyniki",
                        "OoBleed",
                        "OPMuzg",
                        "oStonkeeyz07",
                        "OverSaltedTea",
                        "OwOredvx",
                        "O_OxeD",
                        "p1aul",
                        "panmda",
                        "Panzeroffensive",
                        "PanZiutek",
                        "PapiSquishy",
                        "PartyMeForWin",
                        "PauloLondra_",
                        "PenguinDoesStuff",
                        "periatluvxo",
                        "Pinto80km",
                        "PocketRamen",
                        "pontoKuurt",
                        "Prqyed",
                        "Pupetix1_",
                        "qLugia",
                        "QnlyMehmetAli",
                        "qp_KiWi",
                        "RA1DER5_",
                        "railedKnxck",
                        "Raxzly",
                        "ReAging",
                        "reatention",
                        "RedIsGreek",
                        "Reisverschluss",
                        "RektOnline",
                        "Replyed_",
                        "ResatColak",
                        "restartMaster",
                        "Reunu",
                        "RHOADESS",
                        "RinitoPaLasNenas",
                        "Rinnolovet",
                        "RmXorfiXRm",
                        "RolaPayPal",
                        "RoyalLaurance",
                        "ruitneves",
                        "RxchieTrashMouth",
                        "RY0ZA",
                        "ryansCuddles",
                        "RyZeeN_",
                        "sadlalo",
                        "sad_mirai",
                        "SaltyLycanYT101",
                        "SantulaUla",
                        "satqrnn",
                        "sauciflard_d",
                        "Schlaffi_is_shy",
                        "selbstGeliebt",
                        "SenseiClemens",
                        "SenseiMarvin",
                        "SenseiVolkan",
                        "SergioDelBarrio_",
                        "ShadowliebtElla",
                        "Shiidoro",
                        "Shraddie69",
                        "ShukziO",
                        "silleque",
                        "Sinthony",
                        "Skynez_Owo",
                        "SkywarsGHG",
                        "SlayerGTII",
                        "Slurpthatderp",
                        "sL_uRp_DeRp",
                        "SmetexLVM",
                        "SneaX_Twitch",
                        "Sociaa",
                        "Social_Demokrat",
                        "SoLeichtNicht",
                        "SolxrCloud",
                        "Spocony_majlo",
                        "SqdCqtMeme",
                        "Sqnsitive",
                        "stickyykeys",
                        "stufepBEBO",
                        "StylexFN",
                        "Suchiin",
                        "Sudanyn",
                        "summ3riscoming",
                        "SuP3R37",
                        "surroundcsr",
                        "susipaistia22",
                        "SweazyIsBad",
                        "SweetJonny",
                        "Szalonykolesi",
                        "szejker1nho",
                        "taktikselpatates",
                        "TakumiBr",
                        "TamTaiik",
                        "Tatg",
                        "Tatsuyo_kagamer",
                        "TeekyWeeky",
                        "tekku_ad",
                        "TeSuTra_",
                        "ThaaFabianxD",
                        "tHeFlOoRiSsLiPpY",
                        "TheGangster08",
                        "Theo_OwO",
                        "The_King1_1",
                        "ThijmenLTH",
                        "Tiopo",
                        "TitusTheCreator",
                        "TizTie",
                        "TLBcooler",
                        "TonieGoblin",
                        "Top1_Kotlecik",
                        "Top1_wixaa",
                        "TopMinecraftSkin",
                        "ToufikDu36",
                        "ToxicAndLegit_",
                        "ToxicMqsoc",
                        "TqkeMyCombo",
                        "Tripable",
                        "Trollbabo",
                        "TryHardMyThix",
                        "tubbytoes",
                        "ugalfs",
                        "UkendtTK",
                        "UlasDoganayy",
                        "unbanwizing",
                        "unverletzter",
                        "uwuiida",
                        "uwuZachy",
                        "VersprochxnerFx",
                        "Verwunschener_",
                        "vibinqxtayk",
                        "VixhitoInfectado",
                        "Vliegstoel",
                        "Voiculet",
                        "WaknezZ",
                        "WeakFantasy",
                        "Weyzed",
                        "wheezemaster09",
                        "WichtigesGeld",
                        "wiliamlasaucisse",
                        "Witnness",
                        "WitzigerKobold",
                        "Wxo45",
                        "x4_Skill",
                        "XAYOO_2_zakola",
                        "xB2l0o",
                        "xBloque",
                        "xCrimen_",
                        "xDoNuTx97",
                        "xdreddcube",
                        "xFqlling",
                        "xGonFrqs",
                        "xiiAboDy_",
                        "xImNotAltz_",
                        "xJymbu",
                        "xKayZiii",
                        "xKoiis",
                        "xLegu",
                        "xoliq",
                        "Xqmqq_jxgz__",
                        "xrshia",
                        "xSnownAli",
                        "xUnTorky_EG",
                        "xZakyy_",
                        "x_shocz3k",
                        "X_Weeb_X",
                        "Yashiro_x",
                        "yeaimamint",
                        "yHoneyTheCrack",
                        "Yigit_Kun",
                        "YodiGD",
                        "yourstrulyalec",
                        "youssefsayenbleu",
                        "YouTubeOyuncusu",
                        "YouTube_Xadow",
                        "Yovssef",
                        "yScoopey_",
                        "ysdfasd",
                        "yShadoxxx",
                        "yVitooor_bRz",
                        "zDeprex",
                        "zDontHack_TP",
                        "zDrakVP",
                        "ZELKYOOOOOONNNN",
                        "zHitboxer",
                        "zHmO_",
                        "ZjaranyItz",
                        "zKingHunter",
                        "zKumpa_",
                        "zNeycoow_",
                        "zNLion_",
                        "Zockergirl63",
                        "zoki_hi",
                        "zPercy_",
                        "zPrxsih_",
                        "zSebas_XD",
                        "zSxltyy",
                        "zubidex01",
                        "zzZyLqm",
                        "_Amphyxel_",
                        "_B3K0N",
                        "_BielS_",
                        "_Cetinaa_",
                        "_DarkFilusek",
                        "_Dxqth",
                        "_Egee_YTT",
                        "_ExtrupedGoSHA",
                        "_G4nG4_",
                        "_Grodor_",
                        "_IchBinVess",
                        "_ImFlat_",
                        "_JustRektByMe_",
                        "_Kamatian_",
                        "_kochany",
                        "_LaraBR_",
                        "_Maelwen",
                        "_MonsterPL",
                        "_N0tice_",
                        "_Nethz_",
                        "_OhFlow_",
                        "_OrNah_",
                        "_Qyne",
                        "_RxzorX",
                        "_SearchMyName_",
                        "_Squeedge_",
                        "_Susl1kAdv_",
                        "_Trakfo",
                        "_Verzidee_",
                        "_Yebiemieto",
                        "__iiDark",
                        "__KacpereK",
                        "__SrLuiz__");
    }

    @Priority(3)
    @Comment("Configure the different formats")
    public static class FORMATS {

        @Comment("Defines if chat-format is used")
        public static boolean CHAT_ENABLED = true;

        public static String CHAT_FORMAT =
                "<prefix><color><player><suffix><tag>&7: &f<chatcolor><message>";

        @Comment("Turning this on might break mutes and stuff.")
        public static boolean CHAT_LISTENER_PRIORITY_MONITOR = false;

        public static boolean HOVER_ENABLED = true;

        public static List<String> HOVER_FORMAT =
                Arrays.asList("<middle>Click to message <color><player><middle>.");

        public static String CHAT_COMMAND = "/message <player> ";

        @Comment({
            "Hover shown when a player hovers over a rank's prefix in chat (Hypixel-style).",
            "Configured per-rank via /rank hover set <rank> <text> and /rank hover url <rank> <url>."
        })
        public static boolean RANK_HOVER_ENABLED = true;

        @Comment("Should PlaceholderAPI relation placeholders be parsed?")
        public static boolean PARSE_RELATIONAL_PLACEHOLDERS = false;

        public static String TAGS_FORMAT = " <tag>";

        public static String BUILDER_CHAT_FORMAT = "&2[BC] &7[<server>] &a<player>&7: &f<message>";

        public static String STAFF_CHAT_FORMAT = "&b[SC] &7[<server>] &b<player>&7: &f<message>";

        public static String ADMIN_CHAT_FORMAT = "&c[AC] &7[<server>] &c<player>&7: &f<message>";

        public static String MANAGEMENT_CHAT_FORMAT =
                "&4[MC] &7[<server>] &4<player>&7: &f<message>";

        public static class STAFF_NAMETAGS {

            public static String VANISHED_PREFIX = "<prefix>";

            public static String MODMODE_PREFIX = "<prefix>";

            public static String VANISHED_SUFFIX = " &7[V]";

            public static String MODMODE_SUFFIX = " &7[M]";
        }
    }

    @Priority(12)
    public static class FREEZE {

        @Comment({"Should freezing be enabled?", "Requires a restart!"})
        public static boolean ENABLED = true;

        @Comment("Should /freeze open a menu?")
        public static boolean MENU = true;

        @Comment("Every how many seconds should the freeze message send")
        public static int MESSAGE_REPEAT = 1;

        @Comment("The only commands a frozen player may run.")
        public static List<String> ALLOWED_COMMANDS =
                Arrays.asList("/msg", "/message", "/r", "/reply", "/freeze", "/ss");

        @Comment("Should staff get a click to ban button when a frozen player logs out?")
        public static boolean LEFT_FROZEN_CLICK = true;

        @Comment({
            "Take a chat snapshot when a freeze starts, so what was said survives",
            "the suspect logging out mid screenshare."
        })
        public static boolean CHAT_SNAPSHOT = true;

        public static class TELEPORT {

            @Comment("Should people be teleported to this location when frozen?")
            public static boolean ENABLED = false;

            @Comment("Set with /setfreezeloc.")
            public static ConfigLocation LOCATION = new ConfigLocation("world", 0.0, 100.0, 0.0);
        }
    }

    @Priority(14)
    @Comment("Timed ranks, and telling players before one runs out.")
    public static class GRANTS {

        @Comment({
            "A rank that lapses without warning is a renewal nobody was offered.",
            "Both messages go to the player in game, nowhere else."
        })
        public static class EXPIRY_NOTICE {

            public static boolean ENABLED = true;

            @Comment("How long before it runs out to tell them.")
            public static String BEFORE = "3d";
        }
    }

    @Priority(8)
    @Comment({
        "Features that only exist on newer Minecraft versions.",
        "Each one is skipped for players whose client is too old, so these are safe to leave on."
    })
    public static class MODERN {

        @Comment("Chat rendering features that only newer clients can draw.")
        public static class CHAT {

            @Comment({
                "Draw the sender's head next to their name in chat, on 1.21.5+ clients.",
                "Requires a 1.21.5+ server. Off by default, since it changes how every",
                "chat line looks and that is a taste decision."
            })
            public static boolean HEAD_SPRITES = false;

            @Comment({
                "Honour a rank's 'shadow-color' metadata, drawing its prefix with a",
                "coloured text shadow on 1.21.4+ clients. Requires a 1.21.4+ server.",
                "Set it with /rank shadow <rank> <hex>.",
                "",
                "#RRGGBB is opaque. #RRGGBBAA sets the alpha last, so #20202080 is",
                "half transparent and #20202000 is invisible."
            })
            public static boolean SHADOW_COLORS = true;
        }

        @Comment({
            "Adds entries to the client's chat autocomplete on 1.19+ clients.",
            "Vanished players are never included."
        })
        public static class CHAT_COMPLETIONS {

            public static boolean ENABLED = true;

            @Comment("Suggest the names of players the viewer can actually see.")
            public static boolean PLAYERS = true;

            @Comment({
                "Also suggest the names of players online on the network's other",
                "servers, refreshed every half minute, so /msg completes across the",
                "whole network. Vanished players are never included."
            })
            public static boolean NETWORK_PLAYERS = false;

            @Comment("Suggest the commands the viewer has permission to run.")
            public static boolean COMMANDS = true;

            @Comment("Suggest rank names, for staff typing grant commands.")
            public static boolean RANKS = true;

            @Comment("Permission required to receive rank name suggestions.")
            public static String RANKS_PERMISSION = "core.grant";

            @Comment("Any extra entries you want suggested to everyone.")
            public static List<String> CUSTOM = Collections.emptyList();
        }

        @Comment({
            "Native dialogs on 1.21.6+ clients: real forms with text fields,",
            "dropdowns and toggles, instead of chest menus. Requires a 1.21.6+ server.",
            "Players on older clients silently keep the existing menus."
        })
        public static class DIALOGS {

            public static boolean ENABLED = true;

            @Comment("Use a dialog for /punish instead of the punishment ladder menu.")
            public static boolean PUNISH = true;

            @Comment({
                "Ask for text with a dialog instead of making players type in chat.",
                "Covers every menu that asks a question, such as grant reasons and rank editing."
            })
            public static boolean INPUT = true;

            @Comment({
                "Prompt for two factor codes with a dialog instead of chat.",
                "The dialog cannot be closed, and submitting runs the normal /auth command."
            })
            public static boolean AUTH = true;

            @Comment({
                "Walk /grant through forms instead of the five-menu procedure.",
                "One form per step, the same rank, scope, duration, reason and confirm."
            })
            public static boolean GRANT = true;

            @Comment("Ask yes or no questions with a dialog instead of the confirm menu.")
            public static boolean CONFIRM = true;

            @Comment({
                "Use a settings form for /settings instead of the settings menu.",
                "Settings pages registered by addons stay in the menu, which the form links to."
            })
            public static boolean SETTINGS = true;

            @Comment("Use a form for editing your own socials instead of the social media menu.")
            public static boolean SOCIALS = true;

            @Comment("Pick a report reason with a form instead of the report menu.")
            public static boolean REPORT = true;

            @Comment({
                "Work through reports with forms instead of the report menus.",
                "Deleting and viewing evidence become buttons, which Bedrock can reach."
            })
            public static boolean REPORTS = true;

            @Comment({
                "Hold frozen players on a screen they cannot close, instead of",
                "reopening the freeze menu twice a second."
            })
            public static boolean FREEZE = true;

            @Comment(
                    "Hold panicked players on a screen they cannot close, instead of repeating the message in chat.")
            public static boolean PANIC = true;

            @Comment({
                "Put a warning on a screen the player has to accept, rather than a",
                "line of chat that scrolls past. One handed out while they were",
                "offline waits for their next join."
            })
            public static boolean WARN = true;

            @Comment("Show the staff list as a form instead of a menu of heads.")
            public static boolean ONLINE_STAFF = true;

            @Comment("Show the watchlist as a form instead of a menu of heads.")
            public static boolean WATCHLIST = true;

            @Comment({
                "A rules screen shown on join, remembering who accepted.",
                "Only 1.21.6+ clients can see it; older clients are never blocked by it."
            })
            public static class RULES {

                public static boolean ENABLED = false;

                public static String TITLE = "&eServer Rules";

                public static List<String> BODY =
                        Arrays.asList(
                                "&71. Be respectful.", "&72. No cheating.", "&73. No advertising.");

                public static String ACCEPT_BUTTON = "&aAccept";

                public static String DECLINE_BUTTON = "&cDecline";

                @Comment({
                    "Console commands run when a player declines, <player> is their name.",
                    "Leave empty to just close the dialog until next join."
                })
                public static List<String> DECLINE_COMMANDS =
                        Arrays.asList("kick <player> You must accept the rules to play here.");

                @Comment("Only show the dialog until the player has accepted once.")
                public static boolean SHOW_ONCE = true;
            }
        }

        @Comment({"Filters the locator bar on 1.21.6+ clients.", "Requires a 1.21.6+ server."})
        public static class LOCATOR_BAR {

            public static boolean ENABLED = true;

            @Comment({
                "Hide vanished players from the locator bar of everyone who cannot",
                "see them. Without this the bar points every 1.21.6+ client straight",
                "at your vanished staff."
            })
            public static boolean HIDE_VANISHED = true;

            @Comment({
                "Remove the locator bar entirely instead, for everyone on every",
                "world, without touching the locatorBar game rule."
            })
            public static boolean DISABLE = false;
        }

        @Comment({
            "Rank icons rendered in chat through an auto-generated resource pack.",
            "Drop a <rankname>.png into plugins/Phoenix/icons and put <rankIcon> in",
            "your chat format; Phoenix builds the pack, assigns the glyphs and",
            "pushes it on join. Requires a 1.20.3+ server for the pack push."
        })
        public static class RANK_ICONS {

            public static boolean ENABLED = false;

            @Comment({
                "The url players download the pack from. With the built-in server",
                "below, point this at your server's public address and port, like",
                "http://1.2.3.4:8017/rank-icons.zip - otherwise upload the generated",
                "plugins/Phoenix/packs/rank-icons.zip anywhere and link it here."
            })
            public static String URL = "";

            @Comment({
                "The pack format stamped into pack.mcmeta. The pack also declares",
                "a supported range reaching every newer format, so this only needs",
                "raising if very old clients complain."
            })
            public static int PACK_FORMAT = 34;

            @Comment("How the glyphs sit in the chat line; the defaults suit 8x8 icons.")
            public static int HEIGHT = 8;

            public static int ASCENT = 7;

            public static String PROMPT = "&eRank icons";

            @Comment("Serve the generated pack from the server itself.")
            public static class HTTP {

                public static boolean ENABLED = false;

                public static String BIND = "0.0.0.0";

                public static int PORT = 8017;
            }
        }

        @Comment({
            "Extra lines written into the crash and disconnect reports 1.21+",
            "clients generate. Requires a 1.21+ server."
        })
        public static class REPORT_DETAILS {

            public static boolean ENABLED = true;

            @Comment({
                "KEY|VALUE - shown as plain text, colors are stripped. Values support",
                "<server>, <website>, <store>, <discord>, <teamspeak> and <twitter>,",
                "which resolve from your network settings. At most 32 entries."
            })
            public static List<String> DETAILS =
                    Arrays.asList("Server|<server>", "Website|<website>", "Discord|<discord>");
        }

        @Comment({"Stacked resource packs on 1.20.3+ clients.", "Requires a 1.20.3+ server."})
        public static class RESOURCE_PACKS {

            public static boolean ENABLED = false;

            @Comment({
                "URL|HASH|REQUIRED|PROMPT - hash is the pack's sha1, required and",
                "prompt are optional. Sent to everyone on join, in order.",
                "",
                "Push and pull extra packs while players are online with /pack push",
                "and /pack remove - an event pack can sit on top of these and come",
                "off again without anyone reconnecting."
            })
            public static List<String> PACKS = Collections.emptyList();

            @Comment("Log to console when a player declines a pack or fails to download one.")
            public static boolean LOG_REFUSALS = true;

            @Comment({
                "Kick players who decline or fail to download a pack.",
                "The client enforces 'required' on its own; this is the server side",
                "half, and it applies to every pack, required or not."
            })
            public static boolean KICK_ON_REFUSAL = false;

            public static List<String> REFUSAL_KICK_MESSAGE =
                    Arrays.asList(
                            "&cResource pack required",
                            "",
                            "&7You must accept the resource pack to play here.");
        }

        @Comment({
            "Links shown in the vanilla pause menu on 1.21+ clients.",
            "Requires a 1.21+ server."
        })
        public static class SERVER_LINKS {

            public static boolean ENABLED = true;

            @Comment({
                "Known links are written as TYPE|URL, custom links as CUSTOM|LABEL|URL.",
                "Known types are labelled by the client itself, so they take no label.",
                "BUG_REPORT is the only type that also shows on the disconnect screen.",
                "Valid types: BUG_REPORT, COMMUNITY_GUIDELINES, SUPPORT, STATUS, FEEDBACK,",
                "             COMMUNITY, WEBSITE, FORUMS, NEWS, ANNOUNCEMENTS, CUSTOM",
                "Labels support colors and placeholders. Urls support <website>, <store>,",
                "<discord>, <teamspeak> and <twitter>, which resolve from your network settings.",
                "Ranks can add their own links via the 'server-links' rank metadata key,",
                "holding the same format with entries separated by ';'."
            })
            public static List<String> LINKS =
                    Arrays.asList(
                            "WEBSITE|<website>",
                            "CUSTOM|&6&lStore|<store>",
                            "COMMUNITY|&9Discord|<discord>");
        }

        @Comment({
            "Sorts the tab list on 1.21.2+ clients using rank priority.",
            "Requires a 1.21.2+ server."
        })
        public static class TABLIST_ORDER {

            @Comment({
                "Disabled by default because TAB also manages tab list sorting.",
                "If TAB is installed and sorting, leave this off - otherwise the two",
                "fight over the same field and the order flickers."
            })
            public static boolean ENABLED = false;

            @Comment("Flip the sort direction if higher ranks end up at the bottom.")
            public static boolean INVERT = false;
        }

        @Comment({
            "Move players to another server with no proxy in the path.",
            "Requires a 1.20.5+ server."
        })
        public static class TRANSFER {

            public static boolean ENABLED = false;

            @Comment({
                "Hosts /transfer is allowed to send players to. Leave empty to allow",
                "any host, which is not recommended - a transfer hands the client a",
                "new address to connect to."
            })
            public static List<String> ALLOWED_HOSTS = Collections.emptyList();

            @Comment({
                "Send banned players to an appeal server instead of showing them a",
                "kick screen, so they can actually read why and appeal it.",
                "Applies to bans and blacklists, never to kicks - there is nothing",
                "to appeal about a kick. Players on older clients are kicked as usual."
            })
            public static boolean REDIRECT_BANNED = false;

            public static String APPEAL_HOST = "";

            public static int APPEAL_PORT = 25565;
        }
    }

    @Priority(11)
    @Comment("The staff mod mode inventory, kept in modmode.yml.")
    public static class MODMODE {

        @Comment({
            "Should mod mode be enabled? Vanish handles the hiding it does,",
            "so leaving vanish off only unregisters /vanish itself.",
            "Requires a restart!"
        })
        public static boolean ENABLED = true;

        @Comment("Should staff be put into mod mode when they join?")
        public static boolean ON_JOIN = false;
    }

    @Priority(2)
    public static class ON_JOIN {

        public static boolean CLEAR_CHAT = false;

        public static boolean PLAY_SOUND = true;

        public static boolean TELEPORT = true;

        public static String TELEPORT_LOCATION = "";
    }

    @Priority(1)
    @Comment("Web panel settings")
    public static class PANEL {

        @Comment({
            "Should this server hold an always-on link to the web panel?",
            "When off, the server doesn't.",
            "/phoenix editor keeps working exactly as it used to."
        })
        public static boolean ENABLED = true;

        @Comment({
            "Should /panel code send a link that fills in the sign-in form instead of",
            "printing the code? The code rides in the link only, never in chat."
        })
        public static boolean CODE_IN_LINK = true;
    }

    @Priority(13)
    @Comment("Panic locks a staff member's account down until another staff member clears it.")
    public static class PANIC {

        @Comment({"Should the panic system be enabled?", "Requires Restart!"})
        public static boolean ENABLED = true;

        @Comment("Every how many seconds should the panic message send")
        public static int MESSAGE_REPEAT = 2;

        @Comment("The only commands a panicked player may run.")
        public static List<String> ALLOWED_COMMANDS =
                Arrays.asList("/msg", "/message", "/r", "/reply", "/panic", "/unpanic");
    }

    @Priority(6)
    public static class QUICK_ACCESS_PREFIXES {

        public static String BUILD_CHAT = "% ";

        public static String STAFF_CHAT = "# ";

        public static String ADMIN_CHAT = "@ ";

        public static String MANAGEMENT_CHAT = "! ";
    }

    @Priority(14)
    @Comment("Player reports and staff help requests.")
    public static class REPORTS {

        @Comment({"Should reports and requests be enabled?", "Requires a restart!"})
        public static boolean ENABLED = true;

        @Comment("Should players be able to report staff members?")
        public static boolean REPORT_STAFF = false;

        @Comment("How many days of reports /staffactivity counts when judging a staff member.")
        public static int ACTIVITY_WINDOW = 30;

        @Comment("Number of days after the report that reports show up in /reports-all")
        public static int DAYS_KEEP = 7;

        @Comment("Should we delete older reports than the number of days above on startup?")
        public static boolean DELETE_OLD_ON_STARTUP = true;

        @Comment(
                "Commands that will be given to players when they get a report reward. You can use <player> as a placeholder for the player's name. These commands will be executed by the console.")
        public static List<String> REWARD_COMMANDS = Arrays.asList("ilove <player>");

        @Comment({
            "Let players report for a reason they type themselves, as well as",
            "picking one of the categories. The report is still filed under a",
            "category, so staff keep the ladder they punish from."
        })
        public static class CUSTOM_REASON {

            public static boolean ENABLED = false;

            @Comment("The category a typed reason is filed under. It has to exist.")
            public static String CATEGORY = "other";

            @Comment("The longest reason to accept, beyond which it is trimmed.")
            public static int MAX_LENGTH = 80;
        }

        public static class PENDING_REVIEW_MUTE {

            @Comment(
                    "Should players who get reported many times for chat get automatically muted pending a staff review of the report?")
            public static boolean ENABLED = true;

            @Comment(
                    "How many reports (on a category requiring a chat snapshot) needed to trigger this automatic mute?")
            public static int REPORT_COUNT = 8;

            @Comment("How long should the automatic pending-review mute last?")
            public static String DURATION = "1d";

            @Comment("The reason attached to the automatic pending-review mute.")
            public static String REASON = "Chat Reports - Pending Review";
        }

        public static class WATCHLIST {

            @Comment("How many reports needed to show up on the watchlist?")
            public static int REPORT_COUNT = 5;

            @Comment("After how long should the watchlist expire?")
            public static String EXPIRE = "10m";
        }
    }

    @Priority(1)
    @Comment("Local server settings")
    public static class SERVER {

        @Comment("Use the cores built in nametags")
        public static boolean NAMETAGS = true;

        @Comment("Should tag categories be enabled in /tags?")
        public static boolean TAG_CATEGORIES = true;

        @Comment(
                "Should we create a profile when punishing/granting someone who's never logged in? WILL NOT WORK ON CRACKED SERVERS!!!")
        public static boolean CREATE_PROFILE = false;

        @Comment("Should we register the default visibility adapter?")
        public static boolean REGISTER_DEFAULT_VISIBILITY_ADAPTER = true;

        @Comment("Is the server a auth server for a cracked network?")
        public static boolean AUTH_SERVER = false;

        @Comment("Should the Bukkit OP System be enabled?")
        public static boolean OP_ENABLED = true;

        @Comment("Should group.groupName permissions count as groups for Vault?")
        public static boolean LUCKPERMS_GROUP_PERMISSION_EMULATOR = false;

        @Comment(
                "Should we sync messages.yml, menus.yml and webhooks.yml across all servers via redis?")
        public static boolean CONFIG_SYNC = true;

        @Comment("Should chat mentions be enabled?")
        public static boolean CHAT_MENTION = true;

        @Comment("Should actionbars be enabled?")
        public static boolean ACTIONBARS = true;

        @Comment("Should the preset ranks be created by default if none are present.")
        public static boolean LOAD_RANK_PRESET = true;

        @Comment(
                "Tell the console on startup, and admins when they join, about unfinished /phoenix setup items.")
        public static boolean SETUP_REMINDERS = true;

        @Comment("Check for new Phoenix releases and tell admins when one is out.")
        public static boolean UPDATE_CHECKS = true;

        @Comment(
                "While nobody owns this network, print a one-time /pxclaim code to the console on startup.")
        public static boolean OWNER_CLAIM = true;

        @Comment("Should modern rank color,prefix etc. be used if 1.16+ is detected?")
        public static boolean AUTO_MODERN_RANKS = true;

        @Comment("Disable people getting kicked for disconnect.spam")
        public static boolean DISABLE_SPAM_KICK = true;

        @Comment({
            "Should text be parsed as MiniMessage? Parsed components are cached,",
            "so repeated formats cost one parse rather than one per message.",
            "Defaults on where hex renders, because the modern messages.yml and",
            "menus.yml written there use gradients, which need it."
        })
        public static boolean MINI_MESSAGE = VersionUtil.canHex();

        @Hidden public static boolean TRUE_BACKEND_IP = false;

        public static int MAX_CUSTOM_TAG_LENGTH = 16;

        public static class ANTISPAM {

            @Comment({
                "Basic anti spam, preventing players from sending similar messages twice.",
                "Bypass permission: core.antispam.bypass"
            })
            public static boolean ENABLED = true;

            @Comment("How similar do messages have to be, to be flagged.")
            public static double SIMILARITY = 0.85;
        }

        public static class COMMANDS {

            @Comment(
                    "Should Phoenix only allows Players to tab complete commands they have permission for?")
            public static boolean TAB_COMPLETE_LISTENER = false;

            @Comment(
                    "Should an admin alert get sent when a player tries to execute a blocked command?")
            public static boolean BLOCKED_COMMAND_ALERT = true;

            @Comment("Only disables Phoenix commands")
            public static List<String> DISABLED_COMMANDS = Arrays.asList("yourcmdhere");

            @Comment("Commands that players without core.admin shouldn't be able to execute.")
            public static List<String> BLOCKED_COMMANDS =
                    Arrays.asList(
                            "/about",
                            "/bukkit:about",
                            "/bukkit:help",
                            "/bukkit:?",
                            "/bukkit:me",
                            "/bukkit:pl",
                            "/bukkit:plugins",
                            "/bukkit:ver",
                            "/bukkit:version",
                            "/bukkit:ehelp",
                            "/help",
                            "/icanhasbukkit",
                            "/icanhasspigot",
                            "/info",
                            "/?",
                            "/pl",
                            "/plugins",
                            "/ver",
                            "/version",
                            "/mc:help",
                            "/minecraft:help");

            @Comment("Commands that are allowed if a user is banned or blacklisted")
            public static List<String> BANNED_ALLOWED_COMMANDS =
                    Arrays.asList("discord", "website", "site", "disc");

            @Comment("What commands should be excluded from Command Logs?")
            public static List<String> EXCLUDED_FROM_LOGS =
                    Arrays.asList("login", "register", "2fa", "auth", "2faregister");
        }

        public static class LUNAR {

            public static boolean ENABLED = true;

            @Comment("Should players be receive a popup before disconnecting?")
            public static boolean COMPETITIVE_GAME = true;

            @Comment(
                    "Should staff modules be allowed for staff? (Permission: core.lunar.staffmods)")
            public static boolean STAFF_MODULES = true;

            public static class NAMETAG {

                public static String MODMODE = "&c[Staff Mode]";

                public static String VANISHED = "&7[V] ";

                @Comment(
                        "Available Placeholders: <modMode>, <vanished>, <prefix>, <playerListPrefix>, <suffix>, <color>, <name>")
                public static List<String> LINES =
                        Arrays.asList("<modMode>", "<vanished><prefix><color><name>");
            }
        }

        public static class NAMEMC {

            public static boolean ENABLED = true;

            public static String SERVER_IP = "server.ip";

            public static boolean SEND_MESSAGE = true;

            public static List<String> MESSAGE =
                    Arrays.asList(
                            "",
                            "<middle>Like us on <main>NameMC<middle> and get a free rank!",
                            "<secondary>https://namemc.com/server/yourserver.com",
                            "");

            public static boolean REWARD = true;

            @Comment("Only give the reward the first time they like.")
            public static boolean REWARD_ONE_TIME = true;

            public static String REWARD_MESSAGE =
                    "&aCongratulations! &fYou received Voter rank for voting!";

            public static String REWARD_REMOVE_MESSAGE =
                    "&aYou've unliked us on NameMC and your Voter rank has been removed!";

            public static List<String> REWARD_COMMANDS =
                    Arrays.asList("ogrant <user> Voter perm global Voted on NameMC");

            public static List<String> REWARD_REMOVE_COMMANDS =
                    Arrays.asList("rgrant <user> Voter Unvoted on NameMC");
        }

        public static class PUNISHMENTS {

            @Comment(
                    "What should the max duration be for temporary punishments, set to 'perm' to disable. (Bypass permission: core.punish.duration.bypass)")
            public static String MAX_TEMP_DURATION = "60d";

            @Comment("Force Silent Punishments (Can be bypass with -p)")
            public static boolean FORCE_SILENT_PUNISHMENTS = false;

            @Comment("Force Silent Punishments While Vanished")
            public static boolean FORCE_SILENT_PUNISHMENTS_WHILE_VANISHED = false;

            @Comment("Should punishments issued through a punishment ladder be silent by default?")
            public static boolean AUTO_SILENT_PUNISHMENT_LADDERS = false;

            @Comment(
                    "Should competitive bans be enabled? This is for hooking into the practice plugin")
            public static boolean COMPETITIVE_BANS = false;

            @Comment("After how much time should warns expire? 'permanent' to disable.")
            public static String WARN_EXPIRE = "7d";

            public static boolean BANNED_JOIN = false;

            public static boolean BLACKLISTED_JOIN = false;

            public static boolean PUNISHMENT_EVADE_ALERT = true;

            @Comment(
                    "Should people with core.immune be immune to punishments? (Bypass core.immune.bypass - You can punish people who are immune)")
            public static boolean IMMUNE_SYSTEM = true;

            @Comment({
                "Require evidence on the punishment types listed below. Issuing one",
                "with nothing attached asks the staff member for it first, and backing",
                "out of that question calls the punishment off.",
                "Punishments raised from a report already carry the report's evidence."
            })
            public static class REQUIRE_PROOF {

                public static boolean ENABLED = false;

                public static List<String> TYPES = Arrays.asList("BAN", "BLACKLIST");
            }
        }

        public static class SOUNDS {

            public static String ALERT = "ORB_PICKUP";

            public static String PRIVATE_MESSAGE = "ORB_PICKUP";

            public static String ON_JOIN = "LEVEL_UP";
        }

        public static class STAFF_ACTIVITY {

            public static boolean ENABLED = false;

            @Comment(
                    "How long a staff member must be inactive before being marked and tracked as AFK.")
            public static String AFK_THRESHOLD = "5m";
        }

        public static class TABLIST {

            public static boolean ENABLED = true;

            @Comment("Should the tablist header and footer update every second?")
            public static boolean UPDATING = false;

            public static String HEADER =
                    "&r {0}&r &r &r &r &r &r &r <main>&lPhoenix Core &7\u2758 <middle>Refine &r &r &r &r &r &r &r{0}&r";

            public static String FOOTER =
                    "&r {0}<middle>Online: <main>%phoenix_server_online% &7(%phoenix_server_global_online% global){0}&r {0}<middle>Discord: <main>dsc.gg/refine{0}<middle>Website: <main>refinedev.org{0}&r ";
        }
    }

    @Priority(9)
    @Comment({
        "Protection against compromised staff accounts: a staff member issuing",
        "punishments far faster than a human moderates trips an alert, and staff",
        "can raise the alarm themselves with /panic."
    })
    public static class STAFF_PROTECTION {

        public static boolean ENABLED = true;

        @Comment("How many punishments within the window trip the alarm.")
        public static int PUNISHMENT_THRESHOLD = 12;

        public static int WINDOW_SECONDS = 60;

        @Comment("Who gets the alert messages.")
        public static String ALERT_PERMISSION = "core.admin";

        @Comment({
            "Console commands run when the alarm trips, <player> is the staff",
            "member. Empty means alert only. Something like",
            "'reset2fa <player>' or a kick makes the alarm bite."
        })
        public static List<String> TRIGGER_COMMANDS = Collections.emptyList();

        @Comment({
            "Console commands run when a staff member uses /panic on themselves,",
            "on top of the freeze the staff toolkit already applies. Add",
            "something like force2fa or a rank change if your setup supports it."
        })
        public static List<String> PANIC_COMMANDS = Collections.emptyList();
    }

    @Priority(10)
    @Comment("Staff vanish, hiding staff from players and from each other.")
    public static class VANISH {

        @Comment({"Should /vanish and staff hiding be enabled?", "Requires a restart!"})
        public static boolean ENABLED = true;

        @Comment(
                "Should vanish hide or show the player? Have it false on a practice server for example")
        public static boolean UPDATE_PLAYER_VISIBILITY = true;

        @Comment({
            "Should the vanish packet listener be enabled?",
            "This is used to hide players from other players on a packet level.",
            "Requires a restart!"
        })
        public static boolean PACKET_LISTENER = true;

        @Comment(
                "Make so all staff can see other vanished staff. If this is off rank priority is used")
        public static boolean SEE_ALL = false;

        public static boolean ON_JOIN = false;

        public static boolean ON_MODMODE = true;

        @Comment({
            "When going into vanish you will automatically go into modmode.",
            "It will not be possible for you to exit modmode."
        })
        public static boolean FORCE_MODMODE = true;

        public static class COMMANDS {

            @Comment("Should only the commands below be allowed in vanish?")
            public static boolean ENABLED = false;

            public static List<String> ALLOWED_COMMANDS =
                    Arrays.asList("/vanish", "/v", "/ban", "/mute");
        }
    }
}
