package xyz.refinedev.phoenix.config.migration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class PxBasicMigration {

    private static final File FOLDER = new File("plugins/pxBasic");

    private static final File LEGACY = new File(FOLDER, "messages.yml");

    private static final File IMPORTED = new File(FOLDER, "messages.yml.imported");

    private static final File TIPS = new File(FOLDER, "tips.yml");

    private static final File TARGET_FOLDER = new File("plugins/Phoenix");

    private static final Set<String> MOVED_FROM_GENERAL =
            new HashSet<>(Collections.singletonList("eneder-chest-other"));

    private static final Set<String> MOVED_FROM_ADMIN =
            new HashSet<>(Collections.singletonList("heal.reset-potion-effects"));

    public static void run(Logger logger) {
        if (LEGACY.exists() && !IMPORTED.exists()) {
            try {
                LegacyImport legacyImport;
                (legacyImport = new LegacyImport(TARGET_FOLDER, LEGACY))
                        .copy("settings.buildmode", "settings.yml", "basics.buildmode");
                legacyImport.copy(
                        "settings.welcome-message", "settings.yml", "basics.welcome-message");
                legacyImport.copy("settings.tips", "settings.yml", "basics.tips");
                legacyImport.copy(
                        "admin.heal.reset-potion-effects",
                        "settings.yml",
                        "basics.heal-reset-potion-effects");
                legacyImport.copy("social", "messages.yml", "basic.social");
                legacyImport.copy("general", "messages.yml", "basic.general", MOVED_FROM_GENERAL);
                legacyImport.copy("admin", "messages.yml", "basic.admin", MOVED_FROM_ADMIN);
                legacyImport.copy(
                        "general.eneder-chest-other",
                        "messages.yml",
                        "basic.general.ender-chest-other");
                tips(legacyImport, logger);
                legacyImport.save();
                if (!LEGACY.renameTo(IMPORTED)) {
                    logger.warning(
                            "Imported the pxBasic configuration but could not rename "
                                    + LEGACY.getPath()
                                    + "; delete it so it is not imported again.");
                }
                logger.info(
                        "Imported "
                                + legacyImport.getCopied()
                                + " settings from the old pxBasic configuration into settings.yml and messages.yml.");
                logger.info(
                        "The wording is synced from the config master, so run this import on the master server for it to reach the network.");
            } catch (Exception exception) {
                logger.log(
                        Level.SEVERE,
                        "Could not import the pxBasic configuration, the essentials start on their defaults: ",
                        exception);
            }
        }
    }

    private static void tips(LegacyImport legacyImport, Logger logger) {
        if (TIPS.exists()) {
            File file;
            if (!(file = new File(TARGET_FOLDER, "tips.yml")).exists()) {
                try {
                    Files.copy(TIPS.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException oException) {
                    logger.log(Level.WARNING, "Could not copy the pxBasic tips: ", oException);
                }
            }
        }
    }

    private PxBasicMigration() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}
