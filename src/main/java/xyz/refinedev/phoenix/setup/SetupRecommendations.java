package xyz.refinedev.phoenix.setup;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.ModModeConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.config.WebhookConfig;
import xyz.refinedev.phoenix.profile.filter.IFilter;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadder;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadderType;
import xyz.refinedev.phoenix.report.IReportCategory;
import xyz.refinedev.phoenix.setup.check.AltsCheck;
import xyz.refinedev.phoenix.setup.check.AntiVpnCheck;
import xyz.refinedev.phoenix.setup.check.FiltersCheck;
import xyz.refinedev.phoenix.setup.check.LocalesCheck;
import xyz.refinedev.phoenix.setup.check.MetricsCheck;
import xyz.refinedev.phoenix.setup.check.ModernClientsCheck;
import xyz.refinedev.phoenix.setup.check.PunishmentLaddersCheck;
import xyz.refinedev.phoenix.setup.check.ReportsCheck;
import xyz.refinedev.phoenix.setup.check.StaffToolsCheck;
import xyz.refinedev.phoenix.setup.check.TabListCheck;
import xyz.refinedev.phoenix.setup.check.TagsCheck;
import xyz.refinedev.phoenix.setup.check.TwoFactorCheck;
import xyz.refinedev.phoenix.setup.check.WebhooksCheck;
import xyz.refinedev.phoenix.utils.version.ServerVersion;
import xyz.refinedev.phoenix.utils.version.VersionUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Supplier;

public final class SetupRecommendations {

    public static List<SetupCheck> addAll(Phoenix phoenix) {
        ArrayList<SetupCheck> values;
        ArrayList values2 = values = new ArrayList();
        values2.addAll(findList("Reports", () -> findList(phoenix)));
        values.addAll(findList("Punishment ladders", () -> findListByList(phoenix)));
        values.addAll(findList("Filters", () -> findListByENABLED(phoenix)));
        values.addAll(findList("Staff tools", SetupRecommendations::getList));
        values.addAll(findList("Two-factor", SetupRecommendations::getListByENABLED));
        values.addAll(findList("Alts", SetupRecommendations::strictIpWindowMillis));
        values.addAll(findList("Anti-VPN", () -> findListByPROXYCHECK_API_KEY(phoenix)));
        values.addAll(findList("Tags", () -> findListByPhoenix(phoenix)));
        values.addAll(findList("Tab list", SetupRecommendations::getListByHEADER));
        values.addAll(findList("Webhooks", SetupRecommendations::getListByWEBHOOK_URL));
        values.addAll(findList("Locales", () -> findListByNAME(phoenix)));
        values.addAll(findList("Metrics", SetupRecommendations::getListByTOKEN));
        values.addAll(findList("Modern clients", SetupRecommendations::getListByV_1_21));
        return values;
    }

    private static List<SetupCheck> findList(String text, Supplier<List<SetupCheck>> supplier) {
        try {
            return (List<SetupCheck>) supplier.get();
        } catch (Throwable throwable) {
            return Collections.singletonList(
                    SetupCheck.ofByFAIL(
                            text, "Could not read this area: " + throwable.getMessage(), null));
        }
    }

    private static List<SetupCheck> findList(Phoenix phoenix) {
        List<IReportCategory> cache = phoenix.getReportCategoryHandler().getRepository().getCache();
        ArrayList<String> values = new ArrayList<>();
        int value = 0;
        boolean matches = false;
        Iterator iterator2 = cache.iterator();
        while (iterator2.hasNext()) {
            IReportCategory reportCategory;
            IPunishmentLadder punishmentLadder;
            if ((punishmentLadder =
                            (reportCategory = (IReportCategory) iterator2.next())
                                    .getPunishmentLadder())
                    == null) {
                values.add(
                        reportCategory.getId() + " -> " + reportCategory.getPunishmentLadderId());
            } else if (punishmentLadder.isRequireChatSnapshot()) {
                value++;
            }
            if (reportCategory
                    .getId()
                    .equalsIgnoreCase(SettingsConfig.REPORTS.CUSTOM_REASON.CATEGORY)) {
                matches = true;
            }
        }
        return ReportsCheck.evaluate(
                SettingsConfig.REPORTS.ENABLED,
                cache.size(),
                values,
                SettingsConfig.REPORTS.CUSTOM_REASON.ENABLED,
                SettingsConfig.REPORTS.CUSTOM_REASON.CATEGORY,
                matches,
                SettingsConfig.REPORTS.PENDING_REVIEW_MUTE.ENABLED,
                value);
    }

    private static List<SetupCheck> findListByList(Phoenix phoenix) {
        List<IPunishmentLadder> cache =
                phoenix.getPunishmentLadderHandler().getRepository().getCache();
        ArrayList<String> values = new ArrayList<>();
        int value = 0;
        int value2 = 0;
        Iterator iterator2 = cache.iterator();
        while (iterator2.hasNext()) {
            IPunishmentLadder punishmentLadder;
            List<IPunishmentLadderType> values2;
            if ((values2 = (punishmentLadder = (IPunishmentLadder) iterator2.next()).getLadder())
                            != null
                    && !values2.isEmpty()) {
                for (IPunishmentLadderType punishmentLadderType : values2) {
                    value++;
                    if (punishmentLadderType.getDecay() <= 0L) {
                        value2++;
                    }
                }
            } else {
                values.add(punishmentLadder.getId());
            }
        }
        return PunishmentLaddersCheck.evaluate(cache.size(), values, value, value2);
    }

    private static List<SetupCheck> findListByENABLED(Phoenix phoenix) {
        List<IFilter> cache = phoenix.getFilterHandler().getRepository().getCache();
        ArrayList<String> values = new ArrayList<>();
        Iterator iterator2 = cache.iterator();
        while (iterator2.hasNext()) {
            IFilter filter;
            if ((filter = (IFilter) iterator2.next()).isAutoPunish()
                    && (!filter.hasPunishmentLadder() || filter.getPunishmentLadder() == null)) {
                values.add(filter.getName() + " -> " + filter.getPunishmentLadderId());
            }
        }
        return FiltersCheck.evaluate(GlobalConfig.FILTER.ENABLED, cache.size(), values);
    }

    private static List<SetupCheck> getList() {
        int value = ModModeConfig.ITEMS == null ? 0 : ModModeConfig.ITEMS.size();
        return StaffToolsCheck.evaluate(
                SettingsConfig.MODMODE.ENABLED,
                value,
                SettingsConfig.STAFF_PROTECTION.ENABLED,
                SettingsConfig.STAFF_PROTECTION.PUNISHMENT_THRESHOLD,
                SettingsConfig.STAFF_PROTECTION.WINDOW_SECONDS);
    }

    private static List<SetupCheck> getListByENABLED() {
        int value =
                GlobalConfig.SECURITY.ENABLED && GlobalConfig.SECURITY.RANKS_TO_TRIGGER != null
                        ? GlobalConfig.SECURITY.RANKS_TO_TRIGGER.size()
                        : 0;
        return TwoFactorCheck.evaluate(
                SettingsConfig.AUTH.ENABLED,
                SettingsConfig.AUTH.QRCODE_ENABLED,
                SettingsConfig.AUTH.YUBIKEY_ENABLED,
                value);
    }

    private static List<SetupCheck> strictIpWindowMillis() {
        return AltsCheck.evaluate(
                GlobalConfig.SERVER.ALTS.ENABLED,
                GlobalConfig.SERVER.ALTS.SCORE.IP_WEIGHT,
                GlobalConfig.SERVER.ALTS.THRESHOLDS.ENFORCE,
                GlobalConfig.SERVER.ALTS.SIGNALS.NON_IP_CAP,
                GlobalConfig.SERVER.ALTS.STRICT_IP.ENABLED,
                GlobalConfig.SERVER.ALTS.IP_SUBNET_CHECK,
                GlobalConfig.strictIpWindowMillis() >= 0L);
    }

    private static List<SetupCheck> findListByPROXYCHECK_API_KEY(Phoenix phoenix) {
        boolean matches =
                GlobalConfig.ANTIVPN.PROXYCHECK_API_KEY != null
                        && !GlobalConfig.ANTIVPN.PROXYCHECK_API_KEY.trim().isEmpty();
        return AntiVpnCheck.evaluate(
                GlobalConfig.ANTIVPN.PROVIDER,
                matches,
                GlobalConfig.ANTIVPN.BACKGROUND_CHECK,
                phoenix.getNetworkHandler().getProxies().size());
    }

    private static List<SetupCheck> findListByPhoenix(Phoenix phoenix) {
        return TagsCheck.evaluate(
                phoenix.getTagHandler().getSortedTags().size(),
                phoenix.getTagHandler().getSortedCategories().size(),
                phoenix.getTagHandler().getDefaultCategory() != null);
    }

    private static List<SetupCheck> getListByHEADER() {
        return TabListCheck.evaluate(
                SettingsConfig.SERVER.TABLIST.ENABLED,
                SettingsConfig.SERVER.TABLIST.HEADER,
                SettingsConfig.SERVER.TABLIST.FOOTER,
                SettingsConfig.SERVER.NAMETAGS);
    }

    private static List<SetupCheck> getListByWEBHOOK_URL() {
        LinkedHashMap entries;
        (entries = new LinkedHashMap())
                .put("staff", matchesEnabled(WebhookConfig.STAFF.WEBHOOK_URL));
        entries.put("server", matchesEnabled(WebhookConfig.SERVER.WEBHOOK_URL));
        entries.put("grant", matchesEnabled(WebhookConfig.GRANT.WEBHOOK_URL));
        entries.put("reports", matchesEnabled(WebhookConfig.REPORTS.WEBHOOK_URL));
        entries.put("punishment", matchesEnabled(WebhookConfig.PUNISHMENT.WEBHOOK_URL));
        return WebhooksCheck.evaluate(entries);
    }

    private static List<SetupCheck> findListByNAME(Phoenix phoenix) {
        String text;
        boolean matches =
                (text =
                                        phoenix.getDatabaseHandler()
                                                .runRedisCommand(
                                                        result ->
                                                                (String)
                                                                        result.get(
                                                                                "px-configs-master")))
                                == null
                        || text.trim().isEmpty()
                        || text.equals(GlobalConfig.SERVER.NAME);
        return LocalesCheck.evaluate(
                phoenix.getLocaleHandler().getOverlayCount(),
                phoenix.getLocaleHandler().getAvailableLocales(),
                matches);
    }

    private static List<SetupCheck> getListByTOKEN() {
        boolean matches =
                GlobalConfig.METRICS.TOKEN != null && !GlobalConfig.METRICS.TOKEN.trim().isEmpty();
        return MetricsCheck.evaluate(
                GlobalConfig.METRICS.ENABLED,
                GlobalConfig.METRICS.URL,
                matches,
                GlobalConfig.METRICS.BUCKET);
    }

    private static List<SetupCheck> getListByV_1_21() {
        ServerVersion serverVersion;
        return ModernClientsCheck.evaluate(
                (serverVersion = VersionUtil.getVersion()).getReleaseName(),
                SettingsConfig.MODERN.SERVER_LINKS.ENABLED,
                serverVersion.isNewerThanOrEquals(ServerVersion.V_1_21),
                SettingsConfig.MODERN.TABLIST_ORDER.ENABLED,
                serverVersion.isNewerThanOrEquals(ServerVersion.V_1_21_2),
                SettingsConfig.MODERN.DIALOGS.ENABLED,
                serverVersion.isNewerThanOrEquals(ServerVersion.V_1_21_6));
    }

    private static boolean matchesEnabled(String text) {
        return text != null && !text.trim().isEmpty();
    }

    private SetupRecommendations() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}
