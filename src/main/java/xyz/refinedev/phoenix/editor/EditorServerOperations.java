package xyz.refinedev.phoenix.editor;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.editor.op.EditorOpHandler;
import xyz.refinedev.phoenix.editor.op.EditorOperationException;
import xyz.refinedev.phoenix.editor.op.EditorPayload;
import xyz.refinedev.phoenix.editor.panel.PanelPermissions;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.PunishmentType;
import xyz.refinedev.phoenix.profile.punishment.VoidReason;
import xyz.refinedev.phoenix.profile.punishment.banwave.BanWave;
import xyz.refinedev.phoenix.profile.punishment.banwave.BanWaveEntry;
import xyz.refinedev.phoenix.profile.punishment.banwave.IBanWave;
import xyz.refinedev.phoenix.profile.punishment.banwave.IBanWaveEntry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

public class EditorServerOperations {

    private final Phoenix plugin;

    private static final long time = 15L;

    public EditorServerOperations(Phoenix phoenix) {
        this.plugin = phoenix;
    }

    public EditorOpHandler getEditorOpHandler() {
        return (result, result2) -> {
            boolean matches = EditorPayload.matchesEnabled(result2, "released", false);
            JsonArray jsonArray = new JsonArray();
            if (matches) {
                ArrayList values = new ArrayList();
                Iterator<IBanWave> iterator2 =
                        this.plugin.getBanWaveHandler().getRepository().getCache().iterator();
                while (iterator2.hasNext()) {
                    IBanWave banWave;
                    if ((banWave = (IBanWave) iterator2.next()).isReleased()) {
                        values.add(banWave);
                    }
                }
                values.sort(Comparator.comparingLong(IBanWave::getReleasedAt).reversed());
                for (IBanWave banWave2 : (Iterable<IBanWave>) ((Iterable) values)) {
                    jsonArray.add(this.create(banWave2, true));
                }
            } else {
                for (IBanWave banWave3 : this.plugin.getBanWaveHandler().pending()) {
                    jsonArray.add(this.create(banWave3, true));
                }
            }
            JsonObject jsonObject;
            (jsonObject = new JsonObject()).add("waves", jsonArray);
            return jsonObject;
        };
    }

    public EditorOpHandler getEditorOpHandlerByEditorOpHandler() {
        return (result, result2) -> {
            IBanWave banWave = this.findBanWave(EditorPayload.formatString(result2, "wave"));
            UUID uUID = EditorPayload.findUUID(result2, "uuid");
            boolean matches = EditorPayload.matchesEnabled(result2, "remove", false);
            if (banWave.isReleased()) {
                throw new EditorOperationException(
                        "ALREADY_RELEASED", "that wave has already gone out");
            }
            if (matches) {
                List<IBanWaveEntry> entries = banWave.getEntries();
                entries.removeIf(result3 -> result3.getTarget().equals(uUID));
                this.plugin.getBanWaveHandler().save(banWave);
                return this.create(banWave, true);
            }
            if (banWave.holds(uUID)) {
                throw new EditorOperationException(
                        "ALREADY_STAGED", "that account is already in the wave");
            }
            IProfile profile;
            if ((profile = this.plugin.getProfileHandler().getProfile(uUID)) == null) {
                throw new EditorOperationException("NO_SUCH_PROFILE", "no profile for " + uUID);
            }
            BanWaveEntry banWaveEntry;
            (banWaveEntry =
                            new BanWaveEntry(
                                    uUID,
                                    profile.getName(),
                                    this.findPunishmentType(result2),
                                    EditorPayload.calculateTime(result2, "duration", 0L),
                                    EditorPayload.formatString(result2, "reason", "Ban wave"),
                                    PanelPermissions.findUUID(result2),
                                    System.currentTimeMillis()))
                    .setIp(EditorPayload.matchesEnabled(result2, "ip", false));
            banWave.getEntries().add(banWaveEntry);
            this.plugin.getBanWaveHandler().save(banWave);
            return this.create(banWave, true);
        };
    }

    public EditorOpHandler getEditorOpHandler2() {
        return (result, result2) -> {
            String text;
            if ((text = EditorPayload.formatString(result2, "name").trim()).isEmpty()) {
                throw new EditorOperationException("BAD_REQUEST", "a wave needs a name");
            }
            if (this.plugin.getBanWaveHandler().byName(text) != null) {
                throw new EditorOperationException(
                        "ALREADY_EXISTS", "a wave called " + text + " already exists");
            }
            BanWave banWave =
                    new BanWave(
                            text, PanelPermissions.findUUID(result2), System.currentTimeMillis());
            this.plugin.getBanWaveHandler().save(banWave);
            JsonObject jsonObject;
            (jsonObject = new JsonObject()).addProperty("id", banWave.getId().toString());
            jsonObject.addProperty("name", banWave.getName());
            return jsonObject;
        };
    }

    public EditorOpHandler getEditorOpHandler3() {
        return (result, result2) -> {
            IBanWave banWave = this.findBanWave(EditorPayload.formatString(result2, "wave"));
            long value = EditorPayload.calculateTime(result2, "releaseAt", 0L);
            if (banWave.isReleased()) {
                throw new EditorOperationException(
                        "ALREADY_RELEASED", "that wave has already gone out");
            }
            if (value > 0L && value < System.currentTimeMillis()) {
                throw new EditorOperationException("BAD_REQUEST", "that time has already passed");
            }
            banWave.setReleaseAt(value);
            this.plugin.getBanWaveHandler().save(banWave);
            JsonObject jsonObject;
            (jsonObject = new JsonObject()).addProperty("wave", banWave.getName());
            jsonObject.addProperty("releaseAt", value);
            return jsonObject;
        };
    }

    public EditorOpHandler getEditorOpHandler4() {
        return (result, result2) -> {
            IBanWave banWave;
            if ((banWave = this.findBanWave(EditorPayload.formatString(result2, "wave")))
                    .isReleased()) {
                throw new EditorOperationException(
                        "ALREADY_RELEASED", "that wave has already gone out");
            }
            IBanWave banWave2 = banWave;
            this.findObject(
                    () -> {
                        this.plugin
                                .getBanWaveHandler()
                                .release(banWave2, Bukkit.getConsoleSender());
                        return null;
                    });
            JsonObject jsonObject;
            (jsonObject = new JsonObject()).addProperty("released", banWave.getName());
            jsonObject.addProperty("entries", banWave.getEntries().size());
            return jsonObject;
        };
    }

    public EditorOpHandler getEditorOpHandler5() {
        return (result, result2) -> {
            IBanWave banWave = this.findBanWave(EditorPayload.formatString(result2, "wave"));
            UUID uUID = PanelPermissions.findUUID(result2);
            String text = EditorPayload.formatString(result2, "note", "Voided from the panel");
            if (!banWave.isReleased()) {
                throw new EditorOperationException(
                        "NOT_RELEASED", "that wave has not gone out yet");
            }
            int value =
                    this.findObject(
                            () -> {
                                int value2 = 0;
                                for (UUID uUID2 : banWave.getIssued()) {
                                    IPunishment punishment;
                                    if ((punishment =
                                                    this.plugin
                                                            .getPunishmentHandler()
                                                            .getActivePunishment(
                                                                    uUID2, PunishmentType.BAN))
                                            != null) {
                                        punishment.voidAs(uUID, VoidReason.MISTAKE, text);
                                        value2++;
                                    }
                                }
                                return value2;
                            });
            JsonObject jsonObject;
            (jsonObject = new JsonObject()).addProperty("wave", banWave.getName());
            jsonObject.addProperty("voided", value);
            return jsonObject;
        };
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
        } catch (ExecutionException executionException) {
            if (executionException.getCause() instanceof RuntimeException) {
                throw (RuntimeException) executionException.getCause();
            } else {
                throw new EditorOperationException("INTERNAL", "the server could not run that");
            }
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            throw new EditorOperationException("INTERNAL", "the server could not run that");
        } catch (TimeoutException timeoutException) {
            throw new EditorOperationException("INTERNAL", "the server did not run that in time");
        }
    }

    private JsonObject create(IBanWave banWave, boolean matches) {
        JsonObject jsonObject;
        (jsonObject = new JsonObject()).addProperty("id", banWave.getId().toString());
        jsonObject.addProperty("name", banWave.getName());
        jsonObject.addProperty("createdAt", banWave.getCreatedAt());
        jsonObject.addProperty("releaseAt", banWave.getReleaseAt());
        jsonObject.addProperty("released", banWave.isReleased());
        jsonObject.addProperty("releasedAt", banWave.getReleasedAt());
        jsonObject.addProperty("size", banWave.getEntries().size());
        if (matches) {
            JsonArray jsonArray = new JsonArray();
            for (IBanWaveEntry banWaveEntry : banWave.getEntries()) {
                JsonObject jsonObject2;
                (jsonObject2 = new JsonObject())
                        .addProperty("uuid", banWaveEntry.getTarget().toString());
                jsonObject2.addProperty("name", banWaveEntry.getTargetName());
                jsonObject2.addProperty(
                        "type",
                        banWaveEntry.getType() == null ? null : banWaveEntry.getType().name());
                jsonObject2.addProperty("reason", banWaveEntry.getReason());
                jsonObject2.addProperty("permanent", banWaveEntry.isPermanent());
                jsonObject2.addProperty("ip", banWaveEntry.isIp());
                jsonObject2.addProperty("stagedAt", banWaveEntry.getStagedAt());
                jsonArray.add(jsonObject2);
            }
            jsonObject.add("entries", jsonArray);
        }
        return jsonObject;
    }

    private IBanWave findBanWave(String text) {
        IBanWave banWave;
        if ((banWave = this.plugin.getBanWaveHandler().byName(text)) == null) {
            throw new EditorOperationException("NO_SUCH_WAVE", "no wave named " + text);
        } else {
            return banWave;
        }
    }

    private PunishmentType findPunishmentType(JsonObject jsonObject) {
        String text = EditorPayload.formatString(jsonObject, "type", "BAN");
        PunishmentType[] punishmentTypes;
        int value = (punishmentTypes = PunishmentType.values()).length;
        for (int index = 0; index < value; index++) {
            PunishmentType punishmentType;
            if ((punishmentType = punishmentTypes[index]).name().equalsIgnoreCase(text)) {
                return punishmentType;
            }
        }
        throw new EditorOperationException("BAD_REQUEST", "unknown punishment type " + text);
    }
}
