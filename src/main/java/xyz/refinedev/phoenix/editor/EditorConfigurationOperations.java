package xyz.refinedev.phoenix.editor;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import xyz.refinedev.lib.config.StaticConfig;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.config.WebhookConfig;
import xyz.refinedev.phoenix.editor.op.EditorOpHandler;
import xyz.refinedev.phoenix.editor.op.EditorOperationException;
import xyz.refinedev.phoenix.editor.op.EditorPayload;
import xyz.refinedev.phoenix.editor.panel.PanelBroadcast;
import xyz.refinedev.phoenix.handler.IDatabaseHandler;
import xyz.refinedev.phoenix.packet.impl.server.ConfigReloadPacket;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class EditorConfigurationOperations {

    private final Phoenix plugin;

    private static final String text = "plugins/Phoenix/locales";

    private static final String textString = "network";

    public EditorConfigurationOperations(Phoenix phoenix) {
        this.plugin = phoenix;
    }

    private Map<String, StaticConfig> getMap() {
        LinkedHashMap<String, StaticConfig> entries;
        (entries = new LinkedHashMap()).put("messages.yml", MessageConfig.INSTANCE);
        entries.put("menus.yml", MenuConfig.INSTANCE);
        entries.put("webhooks.yml", WebhookConfig.INSTANCE);
        return entries;
    }

    private String getString() {
        return !SettingsConfig.SERVER.CONFIG_SYNC
                ? null
                : this.plugin
                        .getDatabaseHandler()
                        .runRedisCommand(result -> (String) result.get("px-configs-master"));
    }

    private String formatString(String text) {
        IDatabaseHandler databaseHandler = this.plugin.getDatabaseHandler();
        return databaseHandler.runRedisCommand(result -> (String) result.hget("px-configs", text));
    }

    private static int charAt(String text) {
        int value = 1;
        for (int index = 0; index < text.length(); index++) {
            if (text.charAt(index) == 10) {
                value++;
            }
        }
        return value;
    }

    private List<String> getList() {
        ArrayList<String> values = new ArrayList<>();
        File[] files;
        if ((files =
                        new File("plugins/Phoenix/locales")
                                .listFiles((result, result2) -> result2.endsWith(".yml")))
                == null) {
            return values;
        }
        File[] files2 = files;
        int value = files.length;
        for (int index = 0; index < value; index++) {
            File file = files2[index];
            values.add("locales/" + file.getName());
        }
        values.sort(String::compareTo);
        return values;
    }

    private File create(String text) {
        String text2;
        if (!(text2 = text.substring("locales/".length())).contains("/")
                && !text2.contains("\\")
                && !text2.contains("..")) {
            File file;
            if (!(file = new File("plugins/Phoenix/locales", text2)).isFile()) {
                throw new EditorOperationException("BAD_REQUEST", "no locale called " + text2);
            } else {
                return file;
            }
        } else {
            throw new EditorOperationException("BAD_REQUEST", "that is not a locale file");
        }
    }

    private String createByString(String text) {
        try {
            return new String(
                    Files.readAllBytes(this.create(text).toPath()), StandardCharsets.UTF_8);
        } catch (IOException oException) {
            throw new EditorOperationException("INTERNAL", "that locale could not be read");
        }
    }

    public EditorOpHandler getEditorOpHandler() {
        return (result, result2) -> {
            String string = this.getString();
            JsonArray jsonArray = new JsonArray();
            for (Entry<String, StaticConfig> entry : this.getMap().entrySet()) {
                JsonObject jsonObject;
                (jsonObject = new JsonObject()).addProperty("name", (String) entry.getKey());
                jsonObject.addProperty(
                        "lines", charAt(this.formatString((String) entry.getKey(), string)));
                jsonArray.add(jsonObject);
            }
            for (String text : this.getList()) {
                JsonObject jsonObject2;
                (jsonObject2 = new JsonObject()).addProperty("name", text);
                jsonObject2.addProperty("lines", charAt(this.formatString(text, string)));
                jsonArray.add(jsonObject2);
            }
            JsonObject jsonObject3;
            (jsonObject3 = new JsonObject()).add("files", jsonArray);
            jsonObject3.addProperty("master", string);
            jsonObject3.addProperty("answeredBy", GlobalConfig.SERVER.NAME);
            jsonObject3.addProperty("syncing", SettingsConfig.SERVER.CONFIG_SYNC);
            jsonObject3.addProperty(
                    "source", string == null ? GlobalConfig.SERVER.NAME : "network");
            return jsonObject3;
        };
    }

    public EditorOpHandler getEditorOpHandlerByEditorOpHandler() {
        return (result, result2) -> {
            String text = EditorPayload.formatString(result2, "name");
            String string = this.getString();
            JsonObject jsonObject;
            (jsonObject = new JsonObject()).addProperty("name", text);
            jsonObject.addProperty("content", this.formatString(text, string));
            jsonObject.addProperty("source", string == null ? GlobalConfig.SERVER.NAME : "network");
            jsonObject.addProperty("master", string);
            return jsonObject;
        };
    }

    private String formatString(String text, String text2) {
        String text3;
        if (text2 != null && (text3 = this.formatString(text)) != null && !text3.isEmpty()) {
            return text3;
        } else {
            return text.startsWith("locales/")
                    ? this.createByString(text)
                    : this.append(text).getDocument().dump();
        }
    }

    private void applyJsonObject(JsonObject jsonObject) {
        String text;
        if ((text = EditorPayload.formatString(jsonObject, "source", null)) != null
                && !"network".equals(text)) {
            if (!text.equals(GlobalConfig.SERVER.NAME)) {
                throw new EditorOperationException(
                        "STALE_SOURCE",
                        "you were shown "
                                + text
                                + "'s copy but this is "
                                + GlobalConfig.SERVER.NAME
                                + ", and they need not match while no config master is set. Set one with /phoenix config set-master, then try again.");
            }
        }
    }

    public EditorOpHandler getEditorOpHandler2() {
        return (result, result2) -> {
            String text = EditorPayload.formatString(result2, "name");
            String text2 = EditorPayload.formatString(result2, "content");
            this.applyJsonObject(result2);
            if (text.startsWith("locales/")) {
                return this.create(text, text2, result2);
            }
            StaticConfig staticConfig = this.append(text);
            try {
                ByteArrayInputStream byteArrayInputStream =
                        new ByteArrayInputStream(text2.getBytes(StandardCharsets.UTF_8));
                staticConfig.getDocument().reload(byteArrayInputStream);
                staticConfig.getDocument().save();
                staticConfig.load();
            } catch (Exception exception) {
                throw new EditorOperationException(
                        "BAD_REQUEST", "that file could not be read: " + exception.getMessage());
            }
            boolean actorName = this.isActorName(text, staticConfig.getDocument().dump(), result2);
            JsonObject jsonObject;
            (jsonObject = new JsonObject()).addProperty("saved", true);
            jsonObject.addProperty("scope", actorName ? "network" : GlobalConfig.SERVER.NAME);
            jsonObject.addProperty("master", actorName ? GlobalConfig.SERVER.NAME : null);
            return jsonObject;
        };
    }

    private JsonObject create(String text, String text2, JsonObject jsonObject) {
        File file = this.create(text);
        try {
            Files.write(file.toPath(), text2.getBytes(StandardCharsets.UTF_8));
        } catch (IOException oException) {
            throw new EditorOperationException("INTERNAL", "that locale could not be written");
        }
        boolean actorName = this.isActorName(text, text2, jsonObject);
        JsonObject jsonObject2;
        (jsonObject2 = new JsonObject()).addProperty("saved", true);
        jsonObject2.addProperty("scope", actorName ? "network" : GlobalConfig.SERVER.NAME);
        jsonObject2.addProperty("master", actorName ? GlobalConfig.SERVER.NAME : null);
        return jsonObject2;
    }

    private boolean isActorName(String text, String text2, JsonObject jsonObject) {
        String text3 = EditorPayload.formatString(jsonObject, "actorName", "somebody");
        if (!SettingsConfig.SERVER.CONFIG_SYNC) {
            PanelBroadcast.applyString(
                    "&7[&bPanel&7] &f"
                            + text3
                            + " &7edited &f"
                            + text
                            + " &7on &f"
                            + GlobalConfig.SERVER.NAME
                            + "&7. Config sync is off, so no other server was changed.");
            return false;
        } else {
            IDatabaseHandler databaseHandler = this.plugin.getDatabaseHandler();
            databaseHandler.runRedisCommand(result -> result.hset("px-configs", text, text2));
            this.plugin
                    .getDatabaseHandler()
                    .runRedisCommand(
                            result2 -> result2.set("px-configs-master", GlobalConfig.SERVER.NAME));
            this.plugin
                    .getPacketHandler()
                    .send(new ConfigReloadPacket(text, GlobalConfig.SERVER.NAME));
            PanelBroadcast.applyString(
                    "&7[&bPanel&7] &f"
                            + text3
                            + " &7edited &f"
                            + text
                            + " &7- every server has been told to re-read it.");
            return true;
        }
    }

    private StaticConfig append(String text) {
        StaticConfig staticConfig;
        if ((staticConfig = this.getMap().get(text)) == null) {
            throw new EditorOperationException("BAD_REQUEST", "no editable file called " + text);
        } else {
            return staticConfig;
        }
    }
}
