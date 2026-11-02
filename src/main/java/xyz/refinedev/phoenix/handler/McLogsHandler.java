package xyz.refinedev.phoenix.handler;

import org.bukkit.command.CommandSender;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.EncryptedPasteBuilder;
import xyz.refinedev.phoenix.utils.GsonExclude;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.general.Tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class McLogsHandler {

    private final Phoenix plugin;

    private final Pattern pattern = Pattern.compile(".*\\.(log|txt)(\\.0)?(\\.gz)?");

    private final Pattern patternPattern =
            Pattern.compile(
                    "(?<!([0-9]|-|\\w))(?:[1-2]?[0-9]{1,2}\\.){3}[1-2]?[0-9]{1,2}(?!([0-9]|-|\\w))");

    private final Pattern[] patterns =
            new Pattern[] {
                Pattern.compile("127\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}"),
                Pattern.compile("0\\.0\\.0\\.0"),
                Pattern.compile("1\\.[01]\\.[01]\\.1"),
                Pattern.compile("8\\.8\\.[84]\\.[84]")
            };

    @GsonExclude
    public static String text =
            "[54, 70, 102, 66, 55, 118, 89, 55, 53, 56, 113, 89, 118, 73, 95, 99, 57, 112, 121, 119, 107, 85, 102, 80, 84, 115, 52, 109, 110, 57, 67, 105, 75, 113, 106, 122, 87, 70, 48, 116, 104, 120, 52, 61]";

    public void sendCrashReports(
            CommandSender sender, String text, String text2, boolean matches2, boolean matches3)
            throws java.io.IOException {
        if (!text.endsWith(".")
                && !text.contains("..")
                && !text.contains("/")
                && !text.contains("\\")) {
            File file;
            if (matches2) {
                file = new File("crash-reports", text);
            } else {
                file = new File("logs", text);
            }
            if (!file.exists()) {
                sender.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(sender, MessageConfig.MC_LOGS.FILE_DOES_NOT_EXIST)
                                        .replace("<file>", file.getPath())));
            } else if (file.isDirectory()) {
                sender.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(sender, MessageConfig.MC_LOGS.FILE_IS_DIR)
                                        .replace("<file>", file.getPath())));
            } else if (!text.matches(this.pattern.pattern())) {
                sender.sendMessage(
                        Color.translate(
                                LocaleUtil.tr(sender, MessageConfig.MC_LOGS.MUST_BE_A_LOG_FILE)
                                        .replace("<file>", file.getPath())));
            } else {
                InputStream inputStream = Files.newInputStream(file.toPath());
                if (text.endsWith(".gz")) {
                    inputStream = new GZIPInputStream(inputStream);
                }
                String text3 = this.formatString(inputStream);
                Matcher matcher2 = this.patternPattern.matcher(text3);
                while (matcher2.find()) {
                    String text4 = matcher2.group();
                    if (!this.matchesEnabled(text4)) {
                        text3 = text3.replace(text4, "**.**.**.**");
                    }
                }
                String text5 = text3;
                Tasks.runAsync(
                        () -> {
                            EncryptedPasteBuilder encryptedPasteBuilder;
                            (encryptedPasteBuilder = new EncryptedPasteBuilder())
                                    .findEncryptedPasteBuilderByTextString(text5);
                            if (text2 != null && !text2.isEmpty()) {
                                encryptedPasteBuilder.findEncryptedPasteBuilder(text2);
                            }
                            encryptedPasteBuilder.findEncryptedPasteBuilder(matches3);
                            encryptedPasteBuilder.getEncryptedPasteBuilder();
                            try {
                                sender.sendMessage(
                                        Color.translate(
                                                Phoenix.SECONDARY_COLOR
                                                        + "Link: "
                                                        + encryptedPasteBuilder.getString()));
                            } catch (Exception exception) {
                                sender.sendMessage(
                                        Color.translate(
                                                LocaleUtil.tr(
                                                        sender, MessageConfig.ERROR.PASTE_FAILED)));
                                Phoenix.getInstance()
                                        .getLogger()
                                        .log(
                                                Level.SEVERE,
                                                "An error occurred while creating a mclogs paste:",
                                                exception);
                            }
                        });
            }
        } else {
            sender.sendMessage(
                    Color.translate(LocaleUtil.tr(sender, MessageConfig.ERROR.INVALID_FILE_NAME)));
        }
    }

    public String formatString(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .collect(Collectors.joining("\n"));
    }

    private boolean matchesEnabled(String text) {
        Pattern[] patterns2 = this.patterns;
        int value = this.patterns.length;
        for (int index = 0; index < value; index++) {
            Pattern pattern2 = patterns2[index];
            if (text.matches(pattern2.pattern())) {
                return true;
            }
        }
        return false;
    }

    public McLogsHandler(Phoenix phoenix) {
        this.plugin = phoenix;
    }

    public Pattern getPattern() {
        return this.pattern;
    }
}
