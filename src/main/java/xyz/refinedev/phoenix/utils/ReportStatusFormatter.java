package xyz.refinedev.phoenix.utils;

import org.bukkit.command.CommandSender;

import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.report.ReportStatus;

public final class ReportStatusFormatter {

    private static final String text = "This is a utility class and cannot be instantiated";

    public static String ordinal(ReportStatus reportStatus) {
        switch (reportStatus) {
            case IN_PROGRESS:
                return MenuConfig.REPORTS_MENU.REPORT_STATUS.IN_PROGRESS;
            case RESOLVED:
                return MenuConfig.REPORTS_MENU.REPORT_STATUS.RESOLVED;
            case PUNISHED:
                return MenuConfig.REPORTS_MENU.REPORT_STATUS.PUNISHED;
            default:
                return MenuConfig.REPORTS_MENU.REPORT_STATUS.OPEN;
        }
    }

    public static String tr(CommandSender sender, ReportStatus reportStatus) {
        return LocaleUtil.tr(sender, ordinal(reportStatus));
    }

    public static XMaterial ordinalByCounts(ReportStatus reportStatus) {
        switch (reportStatus) {
            case IN_PROGRESS:
                return XMaterial.YELLOW_TERRACOTTA;
            case RESOLVED:
                return XMaterial.BLUE_TERRACOTTA;
            case PUNISHED:
                return XMaterial.RED_TERRACOTTA;
            case OPEN:
                return XMaterial.GREEN_TERRACOTTA;
            default:
                return XMaterial.WHITE_TERRACOTTA;
        }
    }

    private ReportStatusFormatter() {
        throw new UnsupportedOperationException(text);
    }
}
