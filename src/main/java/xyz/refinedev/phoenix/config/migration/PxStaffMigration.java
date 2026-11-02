package xyz.refinedev.phoenix.config.migration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class PxStaffMigration {

    private static final File FOLDER = new File("plugins/pxStaff");

    private static final File LEGACY = new File(FOLDER, "config.yml");

    private static final File IMPORTED = new File(FOLDER, "config.yml.imported");

    private static final File TARGET_FOLDER = new File("plugins/Phoenix");

    private static final Map<String, String> SETTINGS = new LinkedHashMap<>();

    private static final Map<String, String> MESSAGES = new LinkedHashMap<>();

    private static final Map<String, String> MENUS = new LinkedHashMap<>();

    private static final Map<String, String> WEBHOOKS = new LinkedHashMap<>();

    public static void run(Logger logger) {
        if (LEGACY.exists() && !IMPORTED.exists()) {
            try {
                LegacyImport legacyImport;
                apply(
                        legacyImport = new LegacyImport(TARGET_FOLDER, LEGACY),
                        SETTINGS,
                        "settings.yml");
                apply(legacyImport, MESSAGES, "messages.yml");
                apply(legacyImport, MENUS, "menus.yml");
                apply(legacyImport, WEBHOOKS, "webhooks.yml");
                webhookUrl(legacyImport);
                legacyImport.save();
                copyModModeItems(logger);
                if (!LEGACY.renameTo(IMPORTED)) {
                    logger.warning(
                            "Imported the pxStaff configuration but could not rename "
                                    + LEGACY.getPath()
                                    + "; delete it so it is not imported again.");
                }
                logger.info(
                        "Imported "
                                + legacyImport.getCopied()
                                + " settings from the old pxStaff configuration into settings.yml, messages.yml, menus.yml and webhooks.yml.");
                logger.info(
                        "Those last three are synced from the config master, so run this import on the master server for the wording to reach the network.");
            } catch (Exception exception) {
                logger.log(
                        Level.SEVERE,
                        "Could not import the pxStaff configuration, the staff toolkit starts on its defaults: ",
                        exception);
            }
        }
    }

    private static void apply(LegacyImport legacyImport, Map<String, String> entries, String text) {
        for (Entry<String, String> entry : entries.entrySet()) {
            legacyImport.copy((String) entry.getKey(), text, (String) entry.getValue());
        }
    }

    private static void webhookUrl(LegacyImport legacyImport) {
        if (legacyImport.current("webhooks.yml", "reports.webhook-url", "").isEmpty()) {
            String[] texts;
            int value =
                    (texts =
                                    new String[] {
                                        "report-embed", "report-resolved-embed", "request-embed"
                                    })
                            .length;
            for (int index = 0; index < value; index++) {
                String text = texts[index];
                String text2;
                if (!(text2 = legacyImport.string("webhooks." + text + ".webhook-url", ""))
                        .isEmpty()) {
                    legacyImport.set("webhooks.yml", "reports.webhook-url", text2);
                    return;
                }
            }
        }
    }

    private static void copyModModeItems(Logger logger) {
        File file = new File(FOLDER, "modmode.yml");
        File file2 = new File(TARGET_FOLDER, "modmode.yml");
        if (file.exists() && !file2.exists()) {
            try {
                Files.copy(file.toPath(), file2.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException oException) {
                logger.log(
                        Level.WARNING, "Could not copy the pxStaff mod mode items: ", oException);
            }
        }
    }

    private PxStaffMigration() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }

    static {
        SETTINGS.put("settings.modmode-on-join", "modmode.on-join");
        SETTINGS.put("settings.mod-commands", "modmode.enabled");
        SETTINGS.put("settings.disable-spam-kick", "server.disable-spam-kick");
        SETTINGS.put("settings.report-staff", "reports.report-staff");
        SETTINGS.put("settings.report-days-keep", "reports.days-keep");
        SETTINGS.put("settings.delete-old-reports-startup", "reports.delete-old-on-startup");
        SETTINGS.put("settings.report-reward-commands", "reports.reward-commands");
        SETTINGS.put("settings.watchlist", "reports.watchlist");
        SETTINGS.put("settings.pending-review-mute", "reports.pending-review-mute");
        SETTINGS.put("settings.freeze-gui", "freeze.menu");
        SETTINGS.put("settings.freeze-message-repeat", "freeze.message-repeat");
        SETTINGS.put("settings.freeze-teleport", "freeze.teleport");
        SETTINGS.put("settings.panic-message-repeat", "panic.message-repeat");
        SETTINGS.put("settings.chat-muted", "chat.muted");
        SETTINGS.put("settings.slowed-delay", "chat.slow-delay");
        SETTINGS.put("settings.request-cooldown", "cooldowns.request");
        SETTINGS.put("settings.report-cooldown", "cooldowns.report");
        SETTINGS.put("settings.vanish.update-player-visibility", "vanish.update-player-visibility");
        SETTINGS.put("settings.vanish.packet-listener", "vanish.packet-listener");
        SETTINGS.put("settings.vanish.see-all", "vanish.see-all");
        SETTINGS.put("settings.vanish.vanish-on-join", "vanish.on-join");
        SETTINGS.put("settings.vanish.vanish-on-modmode", "vanish.on-modmode");
        SETTINGS.put("settings.vanish.force-modmode", "vanish.force-modmode");
        SETTINGS.put("settings.vanish.commands", "vanish.commands");
        SETTINGS.put("settings.update-player-visibility", "vanish.update-player-visibility");
        SETTINGS.put("settings.vanish-packet-listener", "vanish.packet-listener");
        SETTINGS.put("settings.all-staff-see-all-staff", "vanish.see-all");
        SETTINGS.put("settings.vanish-on-join", "vanish.on-join");
        SETTINGS.put("settings.vanish-on-modmode", "vanish.on-modmode");
        SETTINGS.put("panic.enabled", "panic.enabled");
        SETTINGS.put("panic.allowed-commands", "panic.allowed-commands");
        SETTINGS.put("freeze.allowed-commands", "freeze.allowed-commands");
        SETTINGS.put("freeze.left-frozen-click", "freeze.left-frozen-click");
        MESSAGES.put("player-messages.panic", "panic.panicked");
        MESSAGES.put("player-messages.unpanic", "panic.unpanicked");
        MESSAGES.put("player-messages.already-panic", "panic.already-panic");
        MESSAGES.put("player-messages.not-in-panic", "panic.not-in-panic");
        MESSAGES.put("player-messages.cant-hit-entities-panic", "panic.cant-hit-entities");
        MESSAGES.put("player-messages.freeze", "freeze.frozen");
        MESSAGES.put("player-messages.unfreeze", "freeze.unfrozen");
        MESSAGES.put("player-messages.freeze-higher-rank", "freeze.higher-rank");
        MESSAGES.put("player-messages.cant-hit-entities-frozen", "freeze.cant-hit-entities");
        MESSAGES.put("player-messages.report-sent", "report.sent");
        MESSAGES.put("player-messages.cant-report-self", "report.cant-report-self");
        MESSAGES.put("player-messages.cant-report-staff", "report.cant-report-staff");
        MESSAGES.put("player-messages.currently-report-muted", "report.muted");
        MESSAGES.put("player-messages.report-reward", "report.reward");
        MESSAGES.put("player-messages.pending-review-mute", "report.pending-review-mute");
        MESSAGES.put("player-messages.request-sent", "request.sent");
        MESSAGES.put("player-messages.currently-request-muted", "request.muted");
        MESSAGES.put("player-messages.request-mute", "request.mute");
        MESSAGES.put("player-messages.request-unmute", "request.unmute");
        MESSAGES.put("player-messages.clear-chat", "chat.cleared");
        MESSAGES.put("player-messages.mute-chat", "chat.muted");
        MESSAGES.put("player-messages.unmute-chat", "chat.unmuted");
        MESSAGES.put("player-messages.slow-chat", "chat.slowed");
        MESSAGES.put("player-messages.chat-muted", "chat.currently-muted");
        MESSAGES.put("player-messages.chat-slowed", "chat.currently-slowed");
        MESSAGES.put("player-messages.report-cooldown", "cooldowns.report-cooldown");
        MESSAGES.put("player-messages.request-cooldown", "cooldowns.request-cooldown");
        MESSAGES.put("staff-messages.vanish", "vanish.enabled");
        MESSAGES.put("staff-messages.un-vanish", "vanish.disabled");
        MESSAGES.put("staff-messages.vanish-sender", "vanish.enabled-other");
        MESSAGES.put("staff-messages.un-vanish-sender", "vanish.disabled-other");
        MESSAGES.put("staff-messages.auto-vanish", "vanish.automatic");
        MESSAGES.put("staff-messages.must-unvanish", "vanish.must-unvanish");
        MESSAGES.put("staff-messages.hidestaff", "vanish.hide-staff");
        MESSAGES.put("staff-messages.showstaff", "vanish.show-staff");
        MESSAGES.put("staff-messages.silent-chest", "vanish.silent-chest");
        MESSAGES.put("staff-messages.silent-container", "vanish.silent-container");
        MESSAGES.put("staff-messages.silent-ender-chest", "vanish.silent-ender-chest");
        MESSAGES.put("staff-messages.modmode", "modmode.enabled");
        MESSAGES.put("staff-messages.unmodmode", "modmode.disabled");
        MESSAGES.put("staff-messages.modmode-sender", "modmode.enabled-other");
        MESSAGES.put("staff-messages.unmodmode-sender", "modmode.disabled-other");
        MESSAGES.put("staff-messages.cant-hit-entities", "modmode.cant-hit-entities");
        MESSAGES.put("staff-messages.freeze", "freeze.froze");
        MESSAGES.put("staff-messages.unfreeze", "freeze.unfroze");
        MESSAGES.put("staff-messages.unpanic", "panic.unpanic-other");
        MESSAGES.put("staff-messages.request-mute", "request.mute-sender");
        MESSAGES.put("staff-messages.request-unmute", "request.unmute-sender");
        MESSAGES.put("staff-messages.clear-chat", "staff.clear-chat-alert");
        MESSAGES.put("staff-messages.mute-chat", "staff.mute-chat-alert");
        MESSAGES.put("staff-messages.unmute-chat", "staff.unmute-chat-alert");
        MESSAGES.put("staff-messages.slow-chat", "staff.slow-chat-alert");
        MESSAGES.put("staff-messages.freeze-staff", "staff.freeze-alert");
        MESSAGES.put("staff-messages.unfreeze-staff", "staff.unfreeze-alert");
        MESSAGES.put("staff-messages.left-while-frozen", "staff.left-while-frozen-alert");
        MESSAGES.put("staff-messages.panic-staff", "staff.panic-alert");
        MESSAGES.put("staff-messages.unpanic-staff", "staff.unpanic-alert");
        MESSAGES.put("staff-messages.report", "staff.report-alert");
        MESSAGES.put("staff-messages.request", "staff.request-alert");
        MESSAGES.put("staff-messages.disguised", "staff.disguised-suffix");
        MESSAGES.put("staff-messages.admin-alerts", "staff.admin-alerts");
        MESSAGES.put("panic.message", "panic.message");
        MESSAGES.put("freeze.message", "freeze.message");
        MESSAGES.put("freeze.prefix", "freeze.prefix");
        MESSAGES.put("freeze.text", "freeze.ban-button.text");
        MESSAGES.put("freeze.hover", "freeze.ban-button.hover");
        MESSAGES.put("freeze.command", "freeze.ban-button.command");
        MESSAGES.put("report-category.category-doesnt-exist", "report.category.doesnt-exist");
        MESSAGES.put("report-category.category-already-exists", "report.category.already-exists");
        MESSAGES.put("report-category.created", "report.category.created");
        MESSAGES.put("report-category.deleted", "report.category.deleted");
        MESSAGES.put("report-category.updated", "report.category.updated");
        MENUS.put("online-staff-menu", "online-staff-menu");
        MENUS.put("freeze-menu", "freeze-menu");
        MENUS.put("reports-menu", "reports-menu");
        MENUS.put("watchlist-menu", "watchlist-menu");
        MENUS.put("report", "report-menu");
        MENUS.put("settings.reports", "settings-menu.reports");
        MENUS.put("settings.requests", "settings-menu.requests");
        WEBHOOKS.put("webhooks.report-embed.webhook-url", "reports.webhook-url");
        WEBHOOKS.put("webhooks.report-embed.color", "reports.report.color");
        WEBHOOKS.put("webhooks.report-embed.title", "reports.report.title");
        WEBHOOKS.put("webhooks.report-resolved-embed.color", "reports.report-resolved.color");
        WEBHOOKS.put("webhooks.report-resolved-embed.title", "reports.report-resolved.title");
        WEBHOOKS.put("webhooks.request-embed.color", "reports.request.color");
        WEBHOOKS.put("webhooks.request-embed.title", "reports.request.title");
    }
}
