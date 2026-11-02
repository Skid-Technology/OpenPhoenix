package xyz.refinedev.phoenix.editor;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.editor.op.EditorOpHandler;
import xyz.refinedev.phoenix.editor.op.EditorOperationException;
import xyz.refinedev.phoenix.editor.op.EditorPayload;
import xyz.refinedev.phoenix.editor.panel.PanelBroadcast;
import xyz.refinedev.phoenix.packet.impl.profile.AlertPacket;
import xyz.refinedev.phoenix.packet.impl.profile.BungeePlayerSendAllPacket;
import xyz.refinedev.phoenix.packet.impl.profile.BungeePlayerSendPacket;
import xyz.refinedev.phoenix.packet.impl.server.ServerCommandPacket;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.whitelist.WhitelistAccess;

import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class EditorWhitelistOperations {

    private final Phoenix plugin;

    private static final long time = 15L;

    public EditorWhitelistOperations(Phoenix phoenix) {
        this.plugin = phoenix;
    }

    public EditorOpHandler getEditorOpHandler() {
        return (result, result2) -> {
            String text = EditorPayload.formatString(result2, "server");
            String text2;
            if ((text2 = EditorPayload.formatString(result2, "command").trim()).isEmpty()) {
                throw new EditorOperationException("BAD_REQUEST", "no command was given");
            }
            this.plugin
                    .getPacketHandler()
                    .sendSync(new ServerCommandPacket(text, text2, this.formatString(result2)));
            JsonObject jsonObject;
            (jsonObject = new JsonObject()).addProperty("server", text);
            jsonObject.addProperty("command", text2);
            return jsonObject;
        };
    }

    public EditorOpHandler getEditorOpHandlerByEditorOpHandler() {
        return (result, result2) -> {
            String text;
            if ((text = EditorPayload.formatString(result2, "message").trim()).isEmpty()) {
                throw new EditorOperationException("BAD_REQUEST", "no message was given");
            }
            this.plugin.getPacketHandler().sendSync(new AlertPacket(text));
            JsonObject jsonObject;
            (jsonObject = new JsonObject()).addProperty("sent", true);
            return jsonObject;
        };
    }

    private String formatString(JsonObject jsonObject) {
        String text;
        if ((text = EditorPayload.formatString(jsonObject, "actorName", null)) != null
                && !text.trim().isEmpty()) {
            return text;
        }
        String text2;
        if ((text2 = EditorPayload.formatString(jsonObject, "actorUuid", null)) == null) {
            return "the panel";
        }
        try {
            IProfile profile;
            return (profile = this.plugin.getProfileHandler().getProfile(UUID.fromString(text2)))
                            == null
                    ? "the panel"
                    : profile.getName();
        } catch (IllegalArgumentException illegalArgumentException) {
            return "the panel";
        }
    }

    public EditorOpHandler getEditorOpHandler2() {
        return (result, result2) -> {
            String text = EditorPayload.formatString(result2, "actorName", "somebody");
            String text2;
            if ((text2 = EditorPayload.formatString(result2, "player", null)) != null) {
                boolean matches;
                if (matches = EditorPayload.matchesEnabled(result2, "add", true)) {
                    this.plugin.getWhitelistHandler().whitelistPlayer(text2, true);
                } else {
                    this.plugin.getWhitelistHandler().unwhitelistPlayer(text2);
                }
                this.applyString(
                        text
                                + (matches ? " whitelisted " : " unwhitelisted ")
                                + text2
                                + " on "
                                + GlobalConfig.SERVER.NAME);
            }
            if (result2.has("enabled")) {
                boolean matches2 = EditorPayload.matchesEnabled(result2, "enabled", false);
                this.plugin.getWhitelistHandler().setEnabled(matches2);
                this.applyString(
                        text
                                + (matches2
                                        ? " turned the whitelist on"
                                        : " turned the whitelist off")
                                + " on "
                                + GlobalConfig.SERVER.NAME);
            }
            if (result2.has("rank")) {
                IRank rank = this.findRank(EditorPayload.formatString(result2, "rank").trim());
                this.plugin.getWhitelistHandler().updateRank(rank);
                this.applyString(
                        text
                                + " set the whitelist rank to "
                                + rank.getName()
                                + " on "
                                + GlobalConfig.SERVER.NAME);
            }
            if (result2.has("accessTypes")) {
                this.applyJsonObject(result2, text);
            }
            return this.create();
        };
    }

    private IRank findRank(String text) {
        IRank rank = null;
        try {
            rank = this.plugin.getRankHandler().getRank(UUID.fromString(text));
        } catch (IllegalArgumentException illegalArgumentException) {
        }
        if (rank == null) {
            rank = this.plugin.getRankHandler().getRank(text);
        }
        if (rank == null) {
            throw new EditorOperationException("BAD_REQUEST", "no rank called " + text);
        } else {
            return rank;
        }
    }

    private void applyJsonObject(JsonObject jsonObject, String text) {
        JsonArray jsonArray =
                jsonObject.get("accessTypes").isJsonArray()
                        ? jsonObject.getAsJsonArray("accessTypes")
                        : new JsonArray();
        HashSet<WhitelistAccess> values2 = new HashSet<>();
        for (int index = 0; index < jsonArray.size(); index++) {
            WhitelistAccess whitelistAccess;
            if ((whitelistAccess =
                            WhitelistAccess.getWhitelistAccessByName(
                                    jsonArray.get(index).getAsString()))
                    == null) {
                throw new EditorOperationException(
                        "BAD_REQUEST",
                        "no access type called " + jsonArray.get(index).getAsString());
            }
            values2.add(whitelistAccess);
        }
        if (values2.isEmpty()) {
            throw new EditorOperationException(
                    "BAD_REQUEST", "leave at least one way in, or turn the whitelist off instead");
        }
        WhitelistAccess[] whitelistAccesss;
        int value = (whitelistAccesss = WhitelistAccess.values()).length;
        for (int index2 = 0; index2 < value; index2++) {
            WhitelistAccess whitelistAccess2 = whitelistAccesss[index2];
            boolean matches = this.plugin.getWhitelistHandler().hasAccessType(whitelistAccess2);
            if (values2.contains(whitelistAccess2) && !matches) {
                this.plugin.getWhitelistHandler().addAccessType(whitelistAccess2);
            } else if (!values2.contains(whitelistAccess2) && matches) {
                this.plugin.getWhitelistHandler().removeAccessType(whitelistAccess2);
            }
        }
        this.applyString(
                text
                        + " set who the whitelist lets through on "
                        + GlobalConfig.SERVER.NAME
                        + " to "
                        + String.join(
                                ", ", this.plugin.getWhitelistHandler().getAccessTypesAsStrings()));
    }

    private JsonObject create() {
        IRank whitelistRank = this.plugin.getWhitelistHandler().getWhitelistRank();
        JsonObject jsonObject;
        (jsonObject = new JsonObject()).addProperty("server", GlobalConfig.SERVER.NAME);
        jsonObject.addProperty("enabled", this.plugin.getWhitelistHandler().isWhitelisted());
        jsonObject.addProperty("rank", whitelistRank == null ? null : whitelistRank.getName());
        jsonObject.addProperty(
                "rankId", whitelistRank == null ? null : whitelistRank.getUuid().toString());
        jsonObject.addProperty("nameBased", this.plugin.getWhitelistHandler().isNameBased());
        JsonArray jsonArray = new JsonArray();
        WhitelistAccess[] whitelistAccesss;
        int value = (whitelistAccesss = WhitelistAccess.values()).length;
        for (int index = 0; index < value; index++) {
            WhitelistAccess whitelistAccess = whitelistAccesss[index];
            JsonObject jsonObject2;
            (jsonObject2 = new JsonObject()).addProperty("name", whitelistAccess.name());
            jsonObject2.addProperty("label", whitelistAccess.getDisplayName());
            jsonObject2.addProperty(
                    "on", this.plugin.getWhitelistHandler().hasAccessType(whitelistAccess));
            jsonArray.add(jsonObject2);
        }
        jsonObject.add("accessTypes", jsonArray);
        JsonArray jsonArray2 = new JsonArray();
        for (String text : this.plugin.getWhitelistHandler().getWhitelistedUsers()) {
            jsonArray2.add(new JsonPrimitive(text));
        }
        jsonObject.add("players", jsonArray2);
        return jsonObject;
    }

    public EditorOpHandler getEditorOpHandler3() {
        return (result, result2) -> {
            String text = EditorPayload.formatString(result2, "actorName", "somebody");
            if (EditorPayload.matchesEnabled(result2, "cancel", false)) {
                if (!this.plugin.getRebootHandler().isRebootScheduled()) {
                    throw new EditorOperationException("BAD_REQUEST", "no reboot is scheduled");
                }
                this.findObject(
                        () -> {
                            this.plugin.getRebootHandler().cancel(Bukkit.getConsoleSender());
                            return null;
                        });
                this.applyString(text + " called off the reboot on " + GlobalConfig.SERVER.NAME);
            } else {
                long value;
                if ((value = EditorPayload.calculateTime(result2, "millis", 0L)) <= 0L) {
                    throw new EditorOperationException(
                            "BAD_REQUEST", "say how long until it reboots");
                }
                long value2 = value;
                this.findObject(
                        () -> {
                            this.plugin
                                    .getRebootHandler()
                                    .reboot(Bukkit.getConsoleSender(), value2);
                            return null;
                        });
                this.applyString(text + " scheduled a reboot on " + GlobalConfig.SERVER.NAME);
            }
            JsonObject jsonObject;
            (jsonObject = new JsonObject()).addProperty("server", GlobalConfig.SERVER.NAME);
            jsonObject.addProperty("scheduled", this.plugin.getRebootHandler().isRebootScheduled());
            jsonObject.addProperty("remaining", this.plugin.getRebootHandler().getTimeRemaining());
            return jsonObject;
        };
    }

    public EditorOpHandler getEditorOpHandler4() {
        return (result, result2) -> {
            String text = EditorPayload.formatString(result2, "server");
            String text2 = EditorPayload.formatString(result2, "actorName", "somebody");
            if (EditorPayload.matchesEnabled(result2, "everyone", false)) {
                this.plugin.getPacketHandler().send(new BungeePlayerSendAllPacket(text));
                this.applyString(
                        text2 + " moved everybody on " + GlobalConfig.SERVER.NAME + " to " + text);
            } else {
                UUID uUID = EditorPayload.findUUID(result2, "uuid");
                this.plugin.getPacketHandler().send(new BungeePlayerSendPacket(uUID, text));
                this.applyString(text2 + " moved a player to " + text);
            }
            JsonObject jsonObject;
            (jsonObject = new JsonObject()).addProperty("server", text);
            return jsonObject;
        };
    }

    private void applyString(String text) {
        PanelBroadcast.applyString("&7[&bPanel&7] &f" + text + "&7.");
    }

    private <T> T findObject(Supplier<T> supplier) {
        if (Bukkit.isPrimaryThread()) {
            return (T) supplier.get();
        }
        CompletableFuture completableFuture = new CompletableFuture();
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTask(
                this.plugin,
                () -> {
                    try {
                        completableFuture.complete(supplier.get());
                    } catch (Throwable throwable) {
                        completableFuture.completeExceptionally(throwable);
                    }
                });
        try {
            return (T) completableFuture.get(time, TimeUnit.SECONDS);
        } catch (Exception exception) {
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new EditorOperationException("INTERNAL", "the server could not run that");
        }
    }
}
