package xyz.refinedev.phoenix.setup;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.menu.api.Button;
import xyz.refinedev.phoenix.menu.impl.SetupBypassMenu;
import xyz.refinedev.phoenix.menu.impl.rank.RankEditorMenu;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.XMaterial;
import xyz.refinedev.phoenix.utils.general.Tasks;
import xyz.refinedev.phoenix.utils.item.ItemBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class SetupMenuFactory {

    private SetupMenuFactory() {}

    public static List<Button> findList(
            Phoenix phoenix, SetupTier setupTier, SetupSummary setupSummary) {
        String text = setupSummary.getText();
        if ("Storage".equals(text)) {
            return singletonList("global.yml");
        } else if ("Identity".equals(text)) {
            return asListByNAME_TAG(phoenix, setupTier, text);
        } else if ("Ranks".equals(text)) {
            return findListByBOOK(phoenix, setupTier, text);
        } else if ("Colours".equals(text)) {
            return findListByPhoenix(phoenix, setupTier, text);
        } else if ("Bypasses".equals(text)) {
            return findList(setupSummary);
        } else if ("Features".equals(text)) {
            return findListByFeatureToggles(phoenix, setupTier, text);
        } else if ("Config master".equals(text)) {
            return asListByBEACON(phoenix, setupTier, text);
        } else if ("Reports".equals(text)) {
            return asList(phoenix, setupTier, text);
        } else if ("Punishment ladders".equals(text)) {
            return singletonList(
                    "Punishment ladders",
                    XMaterial.IRON_SWORD,
                    Arrays.asList("Build a ladder per offence, then a", "rung per repeat."),
                    "punishmentladder list");
        } else if ("Filters".equals(text)) {
            return singletonList(
                    "Filters",
                    XMaterial.HOPPER,
                    Arrays.asList(
                            "The phrases chat refuses, and what", "happens to whoever typed one."),
                    "filter list");
        } else if ("Tags".equals(text)) {
            return singletonList(
                    "Tags",
                    XMaterial.NAME_TAG,
                    Arrays.asList("The cosmetic tags players pick", "between."),
                    "tag list");
        } else if ("Staff tools".equals(text)) {
            return findList(phoenix, setupTier, text);
        } else if ("Two-factor".equals(text)) {
            return findListByList(phoenix, setupTier, text);
        } else if ("Tab list".equals(text)) {
            return findListByPAPER(phoenix, setupTier, text);
        } else if ("Modern clients".equals(text)) {
            return asListByList(phoenix, setupTier, text);
        } else if ("Alts".equals(text)) {
            return singletonList("global.yml");
        } else if ("Anti-VPN".equals(text)) {
            return singletonList("global.yml");
        } else if ("Metrics".equals(text)) {
            return singletonList("global.yml");
        } else if ("Webhooks".equals(text)) {
            return singletonList("webhooks.yml");
        } else {
            return "Locales".equals(text)
                    ? singletonList("locales/messages_<code>.yml")
                    : Collections.emptyList();
        }
    }

    private static List<Button> singletonList(
            String text, XMaterial xMaterial, List<String> values, String text2) {
        return Collections.singletonList(
                new SetupMenuFactory.EntryButton(
                        text,
                        xMaterial,
                        values,
                        null,
                        result -> {
                            Button.playNeutral(result);
                            result.closeInventory();
                            result.performCommand(text2);
                        }));
    }

    private static List<Button> singletonList(final String text) {
        return Collections.singletonList(
                new Button() {

                    private static final String textString = "<file>";

                    @Override
                    public ItemStack getItem(Player player) {
                        ItemBuilder itemBuilder;
                        (itemBuilder = new ItemBuilder(XMaterial.CHEST.get()))
                                .setName(
                                        LocaleUtil.tr(
                                                player, MenuConfig.SETUP_MENU.READ_ONLY.NAME));
                        for (String text2 :
                                LocaleUtil.tr(player, MenuConfig.SETUP_MENU.READ_ONLY.LORE)) {
                            itemBuilder.addLoreLine(text2.replace(textString, text));
                        }
                        return itemBuilder.toItemStack();
                    }

                    @Override
                    public void clicked(Player player, ClickType clickType) {
                        Button.playFail(player);
                    }
                });
    }

    private static List<Button> asList(Phoenix phoenix, SetupTier setupTier, String text) {
        ArrayList<Button> values;
        (values = new ArrayList())
                .add(
                        new SetupMenuFactory.EntryButton(
                                "Report categories",
                                XMaterial.WRITABLE_BOOK,
                                Arrays.asList(
                                        "One category per reason players",
                                        "should be able to pick."),
                                null,
                                result -> {
                                    Button.playNeutral(result);
                                    result.closeInventory();
                                    result.performCommand("reports categories");
                                }));
        values.add(
                findButton(
                        phoenix,
                        setupTier,
                        text,
                        "Typed reasons",
                        "reports.custom-reason.enabled",
                        Arrays.asList(
                                "Let players write a reason as well as",
                                "picking one. Still filed under a category."),
                        false,
                        () -> SettingsConfig.REPORTS.CUSTOM_REASON.ENABLED,
                        result -> SettingsConfig.REPORTS.CUSTOM_REASON.ENABLED = result));
        values.add(
                findButton(
                        phoenix,
                        setupTier,
                        text,
                        "Pending-review mute",
                        "reports.pending-review-mute.enabled",
                        Arrays.asList(
                                "Mute a player reported for chat often",
                                "enough, until staff have looked."),
                        false,
                        () -> SettingsConfig.REPORTS.PENDING_REVIEW_MUTE.ENABLED,
                        result -> SettingsConfig.REPORTS.PENDING_REVIEW_MUTE.ENABLED = result));
        return values;
    }

    private static List<Button> findList(Phoenix phoenix, SetupTier setupTier, String text) {
        return Collections.singletonList(
                findButton(
                        phoenix,
                        setupTier,
                        text,
                        "Punishment alarm",
                        "staff-protection.enabled",
                        Arrays.asList(
                                "Watches how fast punishments go out,",
                                "for a staff account that is not its owner."),
                        false,
                        () -> SettingsConfig.STAFF_PROTECTION.ENABLED,
                        result -> SettingsConfig.STAFF_PROTECTION.ENABLED = result));
    }

    private static List<Button> findListByList(Phoenix phoenix, SetupTier setupTier, String text) {
        ArrayList<Button> values;
        (values = new ArrayList())
                .add(
                        findButton(
                                phoenix,
                                setupTier,
                                text,
                                "Two-factor",
                                "auth.enabled",
                                Arrays.asList(
                                        "Require a second factor from staff",
                                        "before they can act."),
                                true,
                                () -> SettingsConfig.AUTH.ENABLED,
                                result -> SettingsConfig.AUTH.ENABLED = result));
        values.add(
                findButton(
                        phoenix,
                        setupTier,
                        text,
                        "Authenticator app",
                        "auth.qrcode-enabled",
                        Collections.singletonList("Enrol by scanning a QR code."),
                        true,
                        () -> SettingsConfig.AUTH.QRCODE_ENABLED,
                        result -> SettingsConfig.AUTH.QRCODE_ENABLED = result));
        values.add(
                findButton(
                        phoenix,
                        setupTier,
                        text,
                        "Hardware key",
                        "auth.yubikey-enabled",
                        Collections.singletonList("Enrol with a YubiKey."),
                        true,
                        () -> SettingsConfig.AUTH.YUBIKEY_ENABLED,
                        result -> SettingsConfig.AUTH.YUBIKEY_ENABLED = result));
        return values;
    }

    private static List<Button> findListByPAPER(Phoenix phoenix, SetupTier setupTier, String text) {
        ArrayList<Button> values;
        ArrayList<Button> values2 = values = new ArrayList();
        XMaterial xMaterial = XMaterial.PAPER;
        List<String> values3 =
                Arrays.asList("The lines above the player list.", "Ships naming Phoenix.");
        Supplier supplier =
                () -> CheckStatusFormatter.isEmpty(SettingsConfig.SERVER.TABLIST.HEADER);
        values2.add(
                new SetupMenuFactory.EntryButton(
                        "Tab list header",
                        xMaterial,
                        values3,
                        supplier,
                        result ->
                                SetupWriter.applyPhoenix(
                                        phoenix,
                                        result,
                                        setupTier,
                                        text,
                                        "tablist.header",
                                        "&aType the tab list header. Use {0} for a line break.",
                                        SettingsConfig.SERVER.TABLIST.HEADER,
                                        false,
                                        Collections.singletonList(
                                                "Writes settings.yml on this server only."),
                                        null,
                                        result2 -> {
                                            SettingsConfig.SERVER.TABLIST.HEADER = result2;
                                            SettingsConfig.INSTANCE.save();
                                        })));
        xMaterial = XMaterial.PAPER;
        values3 =
                Arrays.asList(
                        "The lines below the player list.", "Ships naming Phoenix's Discord.");
        supplier = () -> CheckStatusFormatter.isEmpty(SettingsConfig.SERVER.TABLIST.FOOTER);
        values.add(
                new SetupMenuFactory.EntryButton(
                        "Tab list footer",
                        xMaterial,
                        values3,
                        supplier,
                        result ->
                                SetupWriter.applyPhoenix(
                                        phoenix,
                                        result,
                                        setupTier,
                                        text,
                                        "tablist.footer",
                                        "&aType the tab list footer. Use {0} for a line break.",
                                        SettingsConfig.SERVER.TABLIST.FOOTER,
                                        false,
                                        Collections.singletonList(
                                                "Writes settings.yml on this server only."),
                                        null,
                                        result2 -> {
                                            SettingsConfig.SERVER.TABLIST.FOOTER = result2;
                                            SettingsConfig.INSTANCE.save();
                                        })));
        values.add(
                findButton(
                        phoenix,
                        setupTier,
                        text,
                        "Tab list",
                        "tablist.enabled",
                        Collections.singletonList("Whether Phoenix writes it at all."),
                        true,
                        () -> SettingsConfig.SERVER.TABLIST.ENABLED,
                        result3 -> SettingsConfig.SERVER.TABLIST.ENABLED = result3));
        values.add(
                findButton(
                        phoenix,
                        setupTier,
                        text,
                        "Rank nametags",
                        "server.nametags",
                        Collections.singletonList("Ranks above players' heads."),
                        true,
                        () -> SettingsConfig.SERVER.NAMETAGS,
                        result3 -> SettingsConfig.SERVER.NAMETAGS = result3));
        return values;
    }

    private static List<Button> asListByList(Phoenix phoenix, SetupTier setupTier, String text) {
        ArrayList<Button> values;
        (values = new ArrayList())
                .add(
                        findButton(
                                phoenix,
                                setupTier,
                                text,
                                "Pause-menu links",
                                "modern.server-links.enabled",
                                Arrays.asList(
                                        "Your website and Discord in the",
                                        "vanilla pause menu. Needs a 1.21+ server."),
                                true,
                                () -> SettingsConfig.MODERN.SERVER_LINKS.ENABLED,
                                result -> SettingsConfig.MODERN.SERVER_LINKS.ENABLED = result));
        values.add(
                findButton(
                        phoenix,
                        setupTier,
                        text,
                        "Tab list order",
                        "modern.tablist-order.enabled",
                        Arrays.asList(
                                "Sort the tab list by rank. Leave off if",
                                "TAB is sorting it. Needs 1.21.2+."),
                        true,
                        () -> SettingsConfig.MODERN.TABLIST_ORDER.ENABLED,
                        result -> SettingsConfig.MODERN.TABLIST_ORDER.ENABLED = result));
        values.add(
                findButton(
                        phoenix,
                        setupTier,
                        text,
                        "Native forms",
                        "modern.dialogs.enabled",
                        Arrays.asList(
                                "Real forms instead of chat prompts and",
                                "menus, on 1.21.6+ clients."),
                        true,
                        () -> SettingsConfig.MODERN.DIALOGS.ENABLED,
                        result -> SettingsConfig.MODERN.DIALOGS.ENABLED = result));
        return values;
    }

    private static Button findButton(
            Phoenix phoenix,
            SetupTier setupTier,
            String text,
            String text2,
            String text3,
            List<String> values,
            boolean matches,
            Supplier<Boolean> supplier,
            Consumer<Boolean> consumer) {
        XMaterial xMaterial = XMaterial.LEVER;
        Supplier supplier2 = () -> values;
        Supplier supplier3 = () -> Boolean.TRUE.equals(supplier.get()) ? "&aon" : "&coff";
        return new SetupMenuFactory.EntryButton(
                text2,
                xMaterial,
                supplier2,
                matches,
                supplier3,
                result -> {
                    boolean matches2 = Boolean.TRUE.equals(supplier.get());
                    ArrayList<String> values2 =
                            new ArrayList<>(
                                    Arrays.asList(
                                            " &7From: " + (matches2 ? "&aon" : "&coff"),
                                            " &7To: " + (matches2 ? "&coff" : "&aon")));
                    if (matches) {
                        values2.add(" &eTakes effect when this server restarts.");
                    }
                    SetupWriter.applyPhoenix(
                            phoenix,
                            result,
                            setupTier,
                            text,
                            text3,
                            values2,
                            () -> {
                                consumer.accept(!matches2);
                                SettingsConfig.INSTANCE.save();
                            });
                });
    }

    private static List<Button> asListByNAME_TAG(
            Phoenix phoenix, SetupTier setupTier, String text) {
        List<String> values =
                Arrays.asList(
                        "Scoped grants and per-server punishments key off this name.",
                        "The ones already stored keep the old one.",
                        "Restart this server once it is set.");
        ArrayList<Button> values2;
        ArrayList<Button> values3 = values2 = new ArrayList();
        XMaterial xMaterial = XMaterial.NAME_TAG;
        List<String> values4 =
                Arrays.asList(
                        "The name your proxy config uses for",
                        "this server. Other servers target it.");
        Supplier supplier = () -> CheckStatusFormatter.isEmpty(GlobalConfig.SERVER.NAME);
        values3.add(
                new SetupMenuFactory.EntryButton(
                        "Server name",
                        xMaterial,
                        values4,
                        supplier,
                        result ->
                                SetupWriter.applyPhoenix(
                                        phoenix,
                                        result,
                                        setupTier,
                                        text,
                                        "server.name",
                                        "&aType the name this server has in your proxy config.",
                                        GlobalConfig.SERVER.NAME,
                                        false,
                                        values,
                                        null,
                                        result2 -> {
                                            GlobalConfig.SERVER.NAME = result2;
                                            GlobalConfig.INSTANCE.save();
                                        })));
        xMaterial = XMaterial.PAINTING;
        values4 = Arrays.asList("The name players read. Colour codes", "are allowed here.");
        supplier = () -> CheckStatusFormatter.isEmpty(GlobalConfig.SERVER.DISPLAYNAME);
        values2.add(
                new SetupMenuFactory.EntryButton(
                        "Display name",
                        xMaterial,
                        values4,
                        supplier,
                        result3 ->
                                SetupWriter.applyPhoenix(
                                        phoenix,
                                        result3,
                                        setupTier,
                                        text,
                                        "server.displayname",
                                        "&aType the name players should read.",
                                        GlobalConfig.SERVER.DISPLAYNAME,
                                        false,
                                        Collections.singletonList(
                                                "Writes global.yml on this server only."),
                                        null,
                                        result2 -> {
                                            GlobalConfig.SERVER.DISPLAYNAME = result2;
                                            GlobalConfig.INSTANCE.save();
                                        })));
        return values2;
    }

    private static List<Button> findListByBOOK(Phoenix phoenix, SetupTier setupTier, String text) {
        ArrayList<Button> values;
        ArrayList<Button> values2 = values = new ArrayList();
        XMaterial xMaterial = XMaterial.BOOK;
        List<String> values3 =
                Arrays.asList("Rename, recolour and reorder the ranks", "you already have.");
        values2.add(
                new SetupMenuFactory.EntryButton(
                        "Rank editor",
                        xMaterial,
                        values3,
                        null,
                        result -> {
                            Button.playNeutral(result);
                            phoenix.getMenuHandler().openMenu(new RankEditorMenu(), result);
                        }));
        if (phoenix.getRankHandler().getRepository().getCache().size() > 1) {
            return values;
        }
        xMaterial = XMaterial.WRITABLE_BOOK;
        values3 =
                Arrays.asList(
                        "A ready-made ladder to rename rather",
                        "than building one from a blank page.");
        values.add(
                new SetupMenuFactory.EntryButton(
                        "Load the starter ranks",
                        xMaterial,
                        values3,
                        null,
                        result2 ->
                                SetupWriter.applyPhoenix(
                                        phoenix,
                                        result2,
                                        setupTier,
                                        text,
                                        "Load the starter ranks",
                                        Arrays.asList(
                                                " &7This replaces every rank that exists.",
                                                " &7Everybody online is kicked while it runs.",
                                                " &c"
                                                        + phoenix.getRankHandler()
                                                                .getRepository()
                                                                .getCache()
                                                                .size()
                                                        + " rank(s) exist now."),
                                        () ->
                                                Tasks.run(
                                                        () ->
                                                                Bukkit.dispatchCommand(
                                                                        Bukkit.getConsoleSender(),
                                                                        "rank tools import-preset")))));
        return values;
    }

    private static List<Button> findListByPhoenix(
            Phoenix phoenix, SetupTier setupTier, String text) {
        ArrayList<Button> values;
        (values = new ArrayList())
                .add(
                        asList(
                                phoenix,
                                setupTier,
                                text,
                                "Main colour",
                                "network.colors.main",
                                () -> GlobalConfig.NETWORK.COLORS.MAIN,
                                result -> GlobalConfig.NETWORK.COLORS.MAIN = result));
        values.add(
                asList(
                        phoenix,
                        setupTier,
                        text,
                        "Secondary colour",
                        "network.colors.secondary",
                        () -> GlobalConfig.NETWORK.COLORS.SECONDARY,
                        result -> GlobalConfig.NETWORK.COLORS.SECONDARY = result));
        values.add(
                asList(
                        phoenix,
                        setupTier,
                        text,
                        "Body colour",
                        "network.colors.middle",
                        () -> GlobalConfig.NETWORK.COLORS.MIDDLE,
                        result -> GlobalConfig.NETWORK.COLORS.MIDDLE = result));
        return values;
    }

    private static Button asList(
            Phoenix phoenix,
            SetupTier setupTier,
            String text,
            String text2,
            String text3,
            Supplier<String> supplier,
            Consumer<String> consumer) {
        XMaterial xMaterial = XMaterial.LIGHT_BLUE_DYE;
        List<String> values =
                Arrays.asList("Recolours every message, menu and", "MOTD that uses it.");
        Supplier supplier2 = () -> CheckStatusFormatter.formatString((String) supplier.get());
        return new SetupMenuFactory.EntryButton(
                text2,
                xMaterial,
                values,
                supplier2,
                result -> {
                    String text4 = (String) supplier.get();
                    List<String> values2 =
                            Collections.singletonList("Writes global.yml on this server only.");
                    Function<String, String> function =
                            result2 ->
                                    CheckStatusFormatter.matchesEnabled(result2)
                                            ? null
                                            : "&c'"
                                                    + result2
                                                    + "' is not a colour code. Use &f&c&c&c, &f&#ff5555&c or a tag.";
                    SetupWriter.applyPhoenix(
                            phoenix,
                            result,
                            setupTier,
                            text,
                            text3,
                            "&aType a colour code, like &c&c &aor &c&#ff5555&a.",
                            text4,
                            true,
                            values2,
                            function,
                            result3 -> {
                                consumer.accept(result3);
                                GlobalConfig.INSTANCE.save();
                                Phoenix.MAIN_COLOR = GlobalConfig.NETWORK.COLORS.MAIN;
                                Phoenix.MIDDLE_COLOR = GlobalConfig.NETWORK.COLORS.MIDDLE;
                                Phoenix.SECONDARY_COLOR = GlobalConfig.NETWORK.COLORS.SECONDARY;
                            });
                });
    }

    private static List<Button> findList(SetupSummary setupSummary) {
        XMaterial xMaterial = XMaterial.IRON_CHESTPLATE;
        List<String> values =
                Arrays.asList(
                        "Pick a rank to hold the bypass nodes",
                        "nobody holds yet. Nothing is removed.");
        return Collections.singletonList(
                new SetupMenuFactory.EntryButton(
                        "Grant the bypasses",
                        xMaterial,
                        values,
                        null,
                        result -> {
                            Button.playNeutral(result);
                            Phoenix.getInstance()
                                    .getMenuHandler()
                                    .openMenu(new SetupBypassMenu(setupSummary), result);
                        }));
    }

    private static List<Button> findListByFeatureToggles(
            Phoenix phoenix, SetupTier setupTier, String text) {
        ArrayList<Button> values = new ArrayList<>();
        for (FeatureToggle featureToggle : FeatureToggles.featureToggles) {
            String text2 = featureToggle.getText();
            XMaterial xMaterial = XMaterial.LEVER;
            Supplier supplier = featureToggle::getList;
            boolean enabled = featureToggle.isEnabled();
            Supplier supplier2 =
                    () -> featureToggle.isEnabledBySupplierSupplier() ? "&aon" : "&coff";
            values.add(
                    new SetupMenuFactory.EntryButton(
                            text2,
                            xMaterial,
                            supplier,
                            enabled,
                            supplier2,
                            result -> {
                                ArrayList<String> values2 =
                                        new ArrayList<>(
                                                Arrays.asList(
                                                        " &7From: "
                                                                + (featureToggle
                                                                                .isEnabledBySupplierSupplier()
                                                                        ? "&aon"
                                                                        : "&coff"),
                                                        " &7To: "
                                                                + (featureToggle
                                                                                .isEnabledBySupplierSupplier()
                                                                        ? "&coff"
                                                                        : "&aon")));
                                if (featureToggle.isEnabled()) {
                                    values2.add(" &eTakes effect when this server restarts.");
                                }
                                String textString = featureToggle.getTextString();
                                SetupWriter.applyPhoenix(
                                        phoenix,
                                        result,
                                        setupTier,
                                        text,
                                        textString,
                                        values2,
                                        () ->
                                                featureToggle.applyEnabled(
                                                        !featureToggle
                                                                .isEnabledBySupplierSupplier()));
                            }));
        }
        return values;
    }

    private static List<Button> asListByBEACON(Phoenix phoenix, SetupTier setupTier, String text) {
        ArrayList<Button> values;
        ArrayList<Button> values2 = values = new ArrayList();
        XMaterial xMaterial = XMaterial.BEACON;
        List<String> values3 =
                Arrays.asList(
                        "This server's wording becomes the",
                        "network's. Edit messages here after.");
        values2.add(
                new SetupMenuFactory.EntryButton(
                        "Make this the master",
                        xMaterial,
                        values3,
                        null,
                        result -> {
                            List<String> values4 =
                                    Arrays.asList(
                                            " &7Master becomes: &f" + GlobalConfig.SERVER.NAME,
                                            " &7Pushes this server's messages, menus and",
                                            " &7webhooks over the copies in redis.",
                                            " &cAny wording only the old master had is lost.");
                            SetupWriter.applyPhoenix(
                                    phoenix,
                                    result,
                                    setupTier,
                                    text,
                                    "Make this the master",
                                    values4,
                                    () -> {
                                        phoenix.getDatabaseHandler()
                                                .runRedisCommand(
                                                        result2 ->
                                                                result2.set(
                                                                        "px-configs-master",
                                                                        GlobalConfig.SERVER.NAME));
                                        phoenix.getConfigSyncHandler().cacheToRedis();
                                    });
                        }));
        xMaterial = XMaterial.COMPASS;
        values3 = Arrays.asList("Replaces this server's wording with", "whatever the master has.");
        values.add(
                new SetupMenuFactory.EntryButton(
                        "Fetch from the master",
                        xMaterial,
                        values3,
                        null,
                        result -> {
                            List<String> values4 =
                                    Arrays.asList(
                                            " &7Replaces messages, menus and webhooks",
                                            " &7on this server with the master's copies.",
                                            " &cLocal edits to those files are lost.");
                            SetupWriter.applyPhoenix(
                                    phoenix,
                                    result,
                                    setupTier,
                                    text,
                                    "Fetch from the master",
                                    values4,
                                    () -> phoenix.getConfigSyncHandler().loadFromRedis());
                        }));
        return values;
    }

    static class EntryButton extends Button {

        private final String text;

        private final XMaterial xMaterial;

        private final Supplier<List<String>> supplier;

        private final boolean enabled;

        private final Supplier<String> supplierSupplier;

        private final Consumer<Player> consumer;

        EntryButton(
                String text,
                XMaterial xMaterial,
                List<String> values,
                Supplier<String> supplier,
                Consumer<Player> consumer) {
            this(text, xMaterial, () -> values, false, supplier, consumer);
        }

        EntryButton(
                String text,
                XMaterial xMaterial2,
                Supplier<List<String>> supplier2,
                boolean enabled,
                Supplier<String> supplier3,
                Consumer<Player> consumer2) {
            this.text = text;
            this.xMaterial = xMaterial2;
            this.supplier = supplier2;
            this.enabled = enabled;
            this.supplierSupplier = supplier3;
            this.consumer = consumer2;
        }

        @Override
        public ItemStack getItem(Player player) {
            ItemStack itemStack = this.xMaterial.parseItem();
            ItemBuilder itemBuilder;
            (itemBuilder =
                            new ItemBuilder(
                                    itemStack == null
                                            ? new ItemStack(XMaterial.PAPER.get())
                                            : itemStack))
                    .setName(
                            LocaleUtil.tr(player, MenuConfig.SETUP_MENU.ACTION.NAME)
                                    .replace("<action>", this.text));
            Iterator<String> iterator2 =
                    LocaleUtil.tr(player, MenuConfig.SETUP_MENU.ACTION.LORE).iterator();
            while (iterator2.hasNext()) {
                String text2;
                if ((text2 = (String) iterator2.next()).contains("<current>")) {
                    if (this.supplierSupplier != null) {
                        itemBuilder.addLoreLine(
                                LocaleUtil.tr(player, MenuConfig.SETUP_MENU.ACTION.CURRENT)
                                        .replace("<value>", this.supplierSupplier.get()));
                    }
                } else if (!text2.contains("<description>")) {
                    itemBuilder.addLoreLine(text2);
                } else {
                    for (String text3 : LocaleUtil.tr(player, this.supplier.get())) {
                        itemBuilder.addLoreLine(
                                LocaleUtil.tr(player, MenuConfig.SETUP_MENU.ACTION.DESCRIPTION)
                                        .replace("<line>", text3));
                    }
                    if (this.enabled) {
                        itemBuilder.addLoreLine(
                                LocaleUtil.tr(player, MenuConfig.SETUP_MENU.ACTION.RESTART));
                    }
                }
            }
            return itemBuilder.toItemStack();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            this.consumer.accept(player);
        }
    }
}
