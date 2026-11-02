package xyz.refinedev.phoenix.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.sun.net.httpserver.HttpServer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.utils.BedrockUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class RankIconHandler {

    private static final char character = '\ue100';

    private final Phoenix plugin;

    private final Map<String, String> stringsByName = new ConcurrentHashMap<>();

    private volatile byte[] bytes;

    private volatile String text;

    private HttpServer httpServer;

    private static final long time = -1L;

    public RankIconHandler(Phoenix phoenix) {
        this.plugin = phoenix;
        this.initialize();
    }

    public void initialize() {
        this.stringsByName.clear();
        this.bytes = null;
        this.text = null;
        if (!SettingsConfig.MODERN.RANK_ICONS.ENABLED) {
            this.initializeByHttpServer();
        } else {
            try {
                File file;
                if (!(file = new File("plugins/Phoenix/icons")).exists() && !file.mkdirs()) {
                    return;
                }
                File[] files;
                if ((files =
                                        file.listFiles(
                                                (result, result2) ->
                                                        result2.toLowerCase().endsWith(".png")))
                                == null
                        || files.length == 0) {
                    this.plugin.log(
                            "&eRank icons are enabled but plugins/Phoenix/icons holds no pngs.");
                    this.initializeByHttpServer();
                    return;
                }
                Arrays.sort(files, Comparator.comparing(result -> result.getName().toLowerCase()));
                byte[] values = this.calculateByteArray(files);
                this.bytes = values;
                this.text = this.formatString(values);
                this.applyByteArray(values);
                this.initializeByENABLED();
                this.plugin.log(
                        "&aBuilt the rank icon pack with "
                                + this.stringsByName.size()
                                + " icon(s), sha1 "
                                + this.text
                                + ".");
            } catch (Throwable throwable) {
                this.plugin
                        .getLogger()
                        .log(Level.WARNING, "Failed to build the rank icon pack.", throwable);
            }
        }
    }

    public String formatString(IRank rank) {
        return rank != null && rank.getName() != null
                ? this.stringsByName.getOrDefault(rank.getName().toLowerCase(), "")
                : "";
    }

    public void applyPlayer(Player player) {
        if (this.text != null) {
            String text2;
            if (!(text2 = SettingsConfig.MODERN.RANK_ICONS.URL.trim()).isEmpty()) {
                if (!BedrockUtils.getUniqueId(player)) {
                    this.plugin
                            .getResourcePackHandler()
                            .findUUID(
                                    player,
                                    text2,
                                    this.text,
                                    false,
                                    SettingsConfig.MODERN.RANK_ICONS.PROMPT);
                }
            }
        }
    }

    public void initializeByText() {
        if (this.text != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                this.applyPlayer(player);
            }
        }
    }

    private byte[] calculateByteArray(File[] files) throws Exception {
        JsonArray jsonArray = new JsonArray();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
        try {
            char value = '\ue100';
            File[] files2 = files;
            int value2 = files.length;
            for (int index = 0; index < value2; index++) {
                File file;
                String text =
                        (file = files2[index])
                                .getName()
                                .substring(0, file.getName().length() - ".png".length());
                String text2 = this.formatString(text);
                zipOutputStream.putNextEntry(
                        new ZipEntry("assets/phoenix/textures/font/" + text2 + ".png"));
                zipOutputStream.write(Files.readAllBytes(file.toPath()));
                zipOutputStream.closeEntry();
                JsonObject jsonObject;
                (jsonObject = new JsonObject()).addProperty("type", "bitmap");
                jsonObject.addProperty("file", "phoenix:font/" + text2 + ".png");
                jsonObject.addProperty("ascent", SettingsConfig.MODERN.RANK_ICONS.ASCENT);
                jsonObject.addProperty("height", SettingsConfig.MODERN.RANK_ICONS.HEIGHT);
                JsonArray jsonArray2;
                (jsonArray2 = new JsonArray()).add(new JsonPrimitive(String.valueOf(value)));
                jsonObject.add("chars", jsonArray2);
                jsonArray.add(jsonObject);
                this.stringsByName.put(text.toLowerCase(), String.valueOf(value));
                value++;
            }
            JsonObject jsonObject2;
            (jsonObject2 = new JsonObject()).add("providers", jsonArray);
            zipOutputStream.putNextEntry(new ZipEntry("assets/minecraft/font/default.json"));
            zipOutputStream.write(jsonObject2.toString().getBytes(StandardCharsets.UTF_8));
            zipOutputStream.closeEntry();
            JsonObject jsonObject3 = new JsonObject();
            JsonObject jsonObject4;
            (jsonObject4 = new JsonObject())
                    .addProperty("pack_format", SettingsConfig.MODERN.RANK_ICONS.PACK_FORMAT);
            JsonArray jsonArray3;
            (jsonArray3 = new JsonArray())
                    .add(new JsonPrimitive(SettingsConfig.MODERN.RANK_ICONS.PACK_FORMAT));
            jsonArray3.add(new JsonPrimitive(Integer.MAX_VALUE));
            jsonObject4.add("supported_formats", jsonArray3);
            jsonObject4.addProperty("description", "Phoenix rank icons");
            jsonObject3.add("pack", jsonObject4);
            zipOutputStream.putNextEntry(new ZipEntry("pack.mcmeta"));
            zipOutputStream.write(jsonObject3.toString().getBytes(StandardCharsets.UTF_8));
            zipOutputStream.closeEntry();
        } catch (Throwable throwable) {
            try {
                zipOutputStream.close();
            } catch (Throwable throwable2) {
                throwable.addSuppressed(throwable2);
            }
            throw throwable;
        }
        zipOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    private void applyByteArray(byte[] values) {
        try {
            File file;
            if (!(file = new File("plugins/Phoenix/packs")).exists() && !file.mkdirs()) {
                return;
            }
            Files.write(new File(file, "rank-icons.zip").toPath(), values);
        } catch (Throwable throwable) {
            this.plugin
                    .getLogger()
                    .log(Level.WARNING, "Failed to write the rank icon pack to disk.", throwable);
        }
    }

    private void initializeByENABLED() {
        this.initializeByHttpServer();
        if (SettingsConfig.MODERN.RANK_ICONS.HTTP.ENABLED) {
            try {
                this.httpServer =
                        HttpServer.create(
                                new InetSocketAddress(
                                        SettingsConfig.MODERN.RANK_ICONS.HTTP.BIND,
                                        SettingsConfig.MODERN.RANK_ICONS.HTTP.PORT),
                                0);
                this.httpServer.createContext(
                        "/",
                        result -> {
                            byte[] values = this.bytes;
                            if (this.bytes != null
                                    && "GET".equalsIgnoreCase(result.getRequestMethod())) {
                                result.getResponseHeaders().set("Content-Type", "application/zip");
                                result.sendResponseHeaders(200, values.length);
                                OutputStream responseBody = result.getResponseBody();
                                try {
                                    responseBody.write(values);
                                } catch (Throwable throwable) {
                                    if (responseBody != null) {
                                        try {
                                            responseBody.close();
                                        } catch (Throwable throwable2) {
                                            throwable.addSuppressed(throwable2);
                                        }
                                    }
                                    throw throwable;
                                }
                                if (responseBody != null) {
                                    responseBody.close();
                                }
                            } else {
                                result.sendResponseHeaders(404, time);
                                result.close();
                            }
                        });
                this.httpServer.start();
                this.plugin.log(
                        "&aServing the rank icon pack on port "
                                + SettingsConfig.MODERN.RANK_ICONS.HTTP.PORT
                                + ".");
            } catch (Throwable throwable3) {
                this.plugin
                        .getLogger()
                        .log(
                                Level.WARNING,
                                "Failed to start the rank icon pack server.",
                                throwable3);
            }
        }
    }

    public void initializeByHttpServer() {
        if (this.httpServer != null) {
            try {
                this.httpServer.stop(0);
            } catch (Throwable throwable) {
            }
            this.httpServer = null;
        }
    }

    private String formatString(String text) {
        StringBuilder stringBuilder = new StringBuilder();
        char[] values;
        int value = (values = text.toLowerCase().toCharArray()).length;
        for (int index = 0; index < value; index++) {
            char value2;
            boolean matches =
                    (value2 = values[index]) >= 'a' && value2 <= 'z'
                            || value2 >= '0' && value2 <= '9'
                            || value2 == '_'
                            || value2 == '-'
                            || value2 == '.';
            stringBuilder.append(matches ? value2 : '_');
        }
        return stringBuilder.toString();
    }

    private String formatString(byte[] values) throws Exception {
        MessageDigest instance = MessageDigest.getInstance("SHA-1");
        StringBuilder stringBuilder = new StringBuilder();
        byte[] values2;
        int value = (values2 = instance.digest(values)).length;
        for (int index = 0; index < value; index++) {
            byte value2 = values2[index];
            stringBuilder
                    .append(Character.forDigit(value2 >> 4 & 15, 16))
                    .append(Character.forDigit(value2 & 15, 16));
        }
        return stringBuilder.toString();
    }
}
