package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.config.StaticConfig;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.config.WebhookConfig;
import xyz.refinedev.phoenix.config.YamlProblem;
import xyz.refinedev.phoenix.utils.ErrorReporter;
import xyz.refinedev.phoenix.utils.version.VersionUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.logging.Level;

public class ConfigSyncHandler {

    private final Phoenix plugin;

    public void loadConfigs() {
        if (!SettingsConfig.SERVER.CONFIG_SYNC) {
            this.loadFromFile();
        } else {
            String text;
            if ((text =
                            this.plugin
                                    .getDatabaseHandler()
                                    .runRedisCommand(
                                            result -> (String) result.get("px-configs-master")))
                    == null) {
                this.loadFromFile();
                this.plugin.error(
                        "There is no config master set. Set the server with your up-to-date configs as master with: /phoenix config set-master!");
            } else {
                if (text.equals(GlobalConfig.SERVER.NAME)) {
                    this.loadFromFile();
                    this.cacheToRedis();
                } else {
                    this.loadFromRedis();
                }
            }
        }
    }

    public void cacheToRedis() {
        this.saveToRedis(MessageConfig.INSTANCE, "messages.yml");
        this.saveToRedis(MenuConfig.INSTANCE, "menus.yml");
        this.saveToRedis(WebhookConfig.INSTANCE, "webhooks.yml");
        this.cacheLocalesToRedis();
    }

    private void cacheLocalesToRedis() {
        File[] files;
        if ((files =
                        new File("plugins/Phoenix/locales")
                                .listFiles((result, result2) -> result2.endsWith(".yml")))
                != null) {
            File[] files2 = files;
            int value = files.length;
            for (int index = 0; index < value; index++) {
                File file = files2[index];
                try {
                    String text =
                            new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                    IDatabaseHandler databaseHandler = this.plugin.getDatabaseHandler();
                    databaseHandler.runRedisCommandAsync(
                            result3 ->
                                    result3.hset("px-configs", "locales/" + file.getName(), text));
                    this.plugin.log("Cached locales/" + file.getName() + " in redis.");
                } catch (Exception exception) {
                    this.plugin.error("Failed to save locales/" + file.getName() + " to redis.");
                    if (this.plugin.isDebugMode()) {
                        this.plugin.getLogger().log(Level.SEVERE, "", exception);
                    }
                }
            }
        }
    }

    private void saveToRedis(StaticConfig staticConfig, String text) {
        try {
            IDatabaseHandler databaseHandler = this.plugin.getDatabaseHandler();
            databaseHandler.runRedisCommandAsync(
                    result -> result.hset("px-configs", text, staticConfig.getDocument().dump()));
            this.plugin.log("Cached " + text + " in redis.");
        } catch (Exception exception) {
            this.plugin.error("Failed to save " + text + " to redis.");
            if (this.plugin.isDebugMode()) {
                this.plugin.getLogger().log(Level.SEVERE, "", exception);
            }
        }
    }

    private void loadFromRedis(StaticConfig staticConfig, String text) {
        IDatabaseHandler databaseHandler = this.plugin.getDatabaseHandler();
        String text2;
        if ((text2 =
                                databaseHandler.runRedisCommand(
                                        result -> (String) result.hget("px-configs", text)))
                        != null
                && !text2.isEmpty()) {
            String text3;
            if ((text3 = YamlProblem.find(text, text2)) != null) {
                this.plugin.error(
                        text
                                + " in redis has a mistake, so this server kept its own copy: "
                                + text3);
                try {
                    staticConfig.load();
                } catch (Exception exception) {
                    this.plugin
                            .getLogger()
                            .log(Level.SEVERE, "Failed to load " + text + ".", exception);
                }
            } else {
                try {
                    ByteArrayInputStream byteArrayInputStream =
                            new ByteArrayInputStream(text2.getBytes());
                    staticConfig.getDocument().reload(byteArrayInputStream);
                    staticConfig.getDocument().save();
                    staticConfig.load();
                    this.plugin.log("Loaded " + text + " from redis.");
                } catch (Exception exception2) {
                    this.plugin
                            .getLogger()
                            .log(
                                    Level.SEVERE,
                                    "Failed to load " + text + " from redis.",
                                    exception2);
                }
            }
        } else {
            this.plugin.error(text + " was not found in redis.");
        }
    }

    public void loadFromRedis() {
        Map<String, String> entries;
        if ((entries =
                                this.plugin
                                        .getDatabaseHandler()
                                        .runRedisCommand(result -> result.hgetall("px-configs")))
                        != null
                && !entries.isEmpty()) {
            this.loadFromRedis(new MessageConfig(this.plugin.getConfigHandler()), "messages.yml");
            this.loadFromRedis(new MenuConfig(this.plugin.getConfigHandler()), "menus.yml");
            this.loadFromRedis(new WebhookConfig(this.plugin.getConfigHandler()), "webhooks.yml");
            this.loadLocalesFromRedis(entries);
        } else {
            this.plugin.error("Failed to load configs from redis.");
        }
        this.loadFromFile();
    }

    private void loadLocalesFromRedis(Map<String, String> entries) {
        File file;
        if ((file = new File("plugins/Phoenix/locales")).exists() || file.mkdirs()) {
            boolean matches = false;
            Iterator<Entry<String, String>> iterator2 = entries.entrySet().iterator();
            while (iterator2.hasNext()) {
                Entry entry;
                String text;
                if (((String) (entry = (Entry) iterator2.next()).getKey()).startsWith("locales/")
                        && !(text = ((String) entry.getKey()).substring("locales/".length()))
                                .contains("/")
                        && !text.contains("\\")
                        && !text.contains("..")
                        && text.endsWith(".yml")) {
                    try {
                        Files.write(
                                new File(file, text).toPath(),
                                ((String) entry.getValue()).getBytes(StandardCharsets.UTF_8));
                        this.plugin.log("Loaded locales/" + text + " from redis.");
                        matches = true;
                    } catch (Exception exception) {
                        this.plugin
                                .getLogger()
                                .log(
                                        Level.SEVERE,
                                        "Failed to load locales/" + text + " from redis.",
                                        exception);
                    }
                }
            }
            if (matches && this.plugin.getLocaleHandler() != null) {
                this.plugin.getLocaleHandler().load();
            }
        }
    }

    private void seedModernDefaults() {
        if (VersionUtil.canHex()) {
            String[] texts;
            int value = (texts = new String[] {"messages", "menus"}).length;
            for (int index = 0; index < value; index++) {
                String text = texts[index];
                File file;
                if (!(file = new File("plugins/Phoenix/" + text + ".yml")).exists()) {
                    try {
                        InputStream resource = this.plugin.getResource(text + "-modern.yml");
                        label56:
                        {
                            try {
                                if (resource == null) {
                                    break label56;
                                }
                                if (file.getParentFile() != null) {
                                    file.getParentFile().mkdirs();
                                }
                                Files.copy(
                                        resource,
                                        file.toPath(),
                                        StandardCopyOption.REPLACE_EXISTING);
                                this.plugin.log(
                                        "Wrote the modern " + text + ".yml for this server.");
                            } catch (Throwable throwable) {
                                if (resource != null) {
                                    try {
                                        resource.close();
                                    } catch (Throwable throwable2) {
                                        throwable.addSuppressed(throwable2);
                                    }
                                }
                                throw throwable;
                            }
                            if (resource != null) {
                                resource.close();
                            }
                            continue;
                        }
                        if (resource != null) {
                            resource.close();
                        }
                    } catch (Exception exception) {
                        this.plugin
                                .getLogger()
                                .log(
                                        Level.WARNING,
                                        "Could not write the modern "
                                                + text
                                                + ".yml, using the shipped defaults.",
                                        exception);
                    }
                }
            }
        }
    }

    public void loadFromFile() {
        this.seedModernDefaults();
        this.loadFile("messages.yml", () -> new MessageConfig(this.plugin.getConfigHandler()));
        this.loadFile("menus.yml", () -> new MenuConfig(this.plugin.getConfigHandler()));
        this.loadFile("webhooks.yml", () -> new WebhookConfig(this.plugin.getConfigHandler()));
    }

    private void loadFile(String text, Supplier<StaticConfig> supplier) {
        String text2;
        if ((text2 = YamlProblem.find(new File("plugins/Phoenix", text))) != null) {
            this.plugin
                    .getLogger()
                    .severe(
                            text
                                    + " has a mistake, so Phoenix is using its built-in defaults for it. Your file was not changed.");
            this.plugin.getLogger().severe(text2);
        } else {
            try {
                ((StaticConfig) supplier.get()).load();
            } catch (Exception exception) {
                this.plugin
                        .getLogger()
                        .severe(
                                "There was an issue while loading "
                                        + text
                                        + ": "
                                        + ErrorReporter.formatString(exception));
            }
        }
    }

    public ConfigSyncHandler(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
