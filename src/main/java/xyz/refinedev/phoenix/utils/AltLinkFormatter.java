package xyz.refinedev.phoenix.utils;

import org.bukkit.command.CommandSender;

import xyz.refinedev.phoenix.alt.AltLinkStatus;
import xyz.refinedev.phoenix.alt.IAltLink;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.IProfile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

public final class AltLinkFormatter {

    public static Set<UUID> findSet(IProfile profile, Map<UUID, IAltLink> entries) {
        LinkedHashSet<UUID> linkedHashSet = new LinkedHashSet<>();
        Iterator<Entry<UUID, IAltLink>> iterator2 = entries.entrySet().iterator();
        while (iterator2.hasNext()) {
            Entry entry;
            IAltLink altLink;
            if ((altLink = (IAltLink) (entry = (Entry) iterator2.next()).getValue()).getStatus()
                            != AltLinkStatus.SUPPRESSED
                    && (altLink.getStatus() == AltLinkStatus.TRUSTED
                            || altLink.getScore() >= GlobalConfig.SERVER.ALTS.THRESHOLDS.DISPLAY)) {
                linkedHashSet.add((UUID) entry.getKey());
            }
        }
        for (UUID uUID : profile.getAlts()) {
            IAltLink altLink2;
            if ((altLink2 = (IAltLink) entries.get(uUID)) == null
                    || altLink2.getStatus() != AltLinkStatus.SUPPRESSED) {
                linkedHashSet.add(uUID);
            }
        }
        return linkedHashSet;
    }

    public static boolean getStatus(IAltLink altLink) {
        return altLink != null && altLink.getStatus() == AltLinkStatus.TRUSTED;
    }

    public static String formatString(IAltLink altLink) {
        return formatString(null, altLink);
    }

    public static String formatString(CommandSender sender, IAltLink altLink) {
        if (altLink == null) {
            return LocaleUtil.tr(sender, MessageConfig.ALTS.TIER.UNSCORED);
        } else {
            return altLink.getStatus() == AltLinkStatus.TRUSTED
                    ? LocaleUtil.tr(sender, MessageConfig.ALTS.TIER.TRUSTED_GROUP)
                    : tr(sender, altLink.getScore());
        }
    }

    public static String formatString(int value) {
        return tr(null, value);
    }

    public static String tr(CommandSender sender, int value) {
        if (value >= 90) {
            return LocaleUtil.tr(sender, MessageConfig.ALTS.TIER.CONFIRMED);
        } else if (value >= 75) {
            return LocaleUtil.tr(sender, MessageConfig.ALTS.TIER.LIKELY);
        } else {
            return value >= 50
                    ? LocaleUtil.tr(sender, MessageConfig.ALTS.TIER.POSSIBLE)
                    : LocaleUtil.tr(sender, MessageConfig.ALTS.TIER.WEAK);
        }
    }

    public static List<String> findList(IAltLink altLink) {
        return findList(null, altLink);
    }

    public static List<String> findList(CommandSender sender, IAltLink altLink) {
        ArrayList<String> values = new ArrayList<>();
        if (altLink == null) {
            values.add(LocaleUtil.tr(sender, MessageConfig.ALTS.NO_SCORE));
            return values;
        }
        Iterator<String> iterator2 =
                LocaleUtil.tr(sender, MessageConfig.ALTS.SCORE_FORMAT).iterator();
        while (iterator2.hasNext()) {
            String text;
            if (!(text = (String) iterator2.next()).contains("<breakdown>")
                    && (!text.contains("<lastSharedLogin>") || altLink.getLastSharedLogin() > 0L)) {
                values.add(
                        text.replace("<score>", String.valueOf(altLink.getScore()))
                                .replace("<tier>", tr(sender, altLink.getScore()))
                                .replace(
                                        "<sharedAddresses>",
                                        String.valueOf(altLink.getSharedAddresses()))
                                .replace(
                                        "<independentSubnets>",
                                        String.valueOf(altLink.getIndependentSubnets()))
                                .replace(
                                        "<lastSharedLogin>",
                                        DateUtils.getDate(altLink.getLastSharedLogin()))
                                .replace(
                                        "<firstLinked>",
                                        DateUtils.getDate(altLink.getFirstLinked()))
                                .replace(
                                        "<lastEvaluated>",
                                        DateUtils.getDate(altLink.getLastEvaluated())));
            }
        }
        if (altLink.getStatus() == AltLinkStatus.TRUSTED) {
            values.add(LocaleUtil.tr(sender, MessageConfig.ALTS.TRUSTED_LINE));
        }
        return values;
    }

    public static String formatString(double value) {
        String text = String.format(Locale.ROOT, "%.1f", Math.abs(value));
        return (value >= 0.0 ? "+" : "-") + text;
    }

    private AltLinkFormatter() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}
