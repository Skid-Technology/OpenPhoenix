package xyz.refinedev.phoenix.editor.op;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.editor.EditorSession;
import xyz.refinedev.phoenix.editor.dto.OperationResult;
import xyz.refinedev.phoenix.editor.panel.PanelBroadcast;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.grant.IGrant;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.PunishmentType;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.scope.IScope;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class GrantOperations {

    private final CommonPlatform platform;

    public GrantOperations(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }

    public EditorOpHandler getEditorOpHandler() {
        return (result, result2) -> {
            IProfile profile = this.findProfile(result2);
            String text = EditorPayload.formatString(result2, "rank");
            IRank rank;
            if ((rank = this.platform.getRankHandler().getRank(text)) == null) {
                throw new EditorOperationException("NO_SUCH_RANK", "no rank named " + text);
            }
            boolean matches = EditorPayload.matchesEnabled(result2, "permanent", true);
            long value = EditorPayload.calculateTime(result2, "duration", 0L);
            String text2 = EditorPayload.formatString(result2, "reason", "Editor");
            UUID uUID = this.findUUID(result, result2);
            this.applyUUID(uUID, rank);
            this.platform
                    .getGrantHandler()
                    .grant(uUID, profile, value, matches, text2, rank, this.findList(result2));
            return this.findJsonElement(
                    OperationResult.of("granted " + rank.getName() + " to " + profile.getName()));
        };
    }

    public EditorOpHandler getEditorOpHandlerByEditorOpHandler() {
        return (result, result2) -> {
            IProfile profile = this.findProfile(result2);
            UUID uUID = EditorPayload.findUUID(result2, "grantId");
            String text = EditorPayload.formatString(result2, "reason", "Editor");
            IGrant grant = null;
            Iterator<IGrant> iterator2 = profile.getGrants().iterator();
            while (iterator2.hasNext()) {
                IGrant grant2;
                if ((grant2 = (IGrant) iterator2.next()).getUuid().equals(uUID)) {
                    grant = grant2;
                    break;
                }
            }
            if (grant == null) {
                throw new EditorOperationException(
                        "NO_SUCH_GRANT", "no grant " + uUID + " on " + profile.getName());
            }
            UUID uUID2 = this.findUUID(result, result2);
            if (grant.getRank() != null) {
                this.applyUUID(uUID2, grant.getRank());
            }
            this.platform.getGrantHandler().ungrant(uUID2, profile, grant, text);
            return this.findJsonElement(
                    OperationResult.of("removed a grant from " + profile.getName()));
        };
    }

    public EditorOpHandler getEditorOpHandler2() {
        return (result, result2) -> {
            IProfile profile = this.findProfile(result2);
            PunishmentType punishmentType = this.findPunishmentType(result2);
            String text = EditorPayload.formatString(result2, "reason", "Editor");
            IPunishment punishment;
            (punishment = this.platform.getPunishmentHandler().createEmptyPunishment())
                    .setPunishmentType(punishmentType);
            punishment.setTarget(profile.getUniqueId());
            punishment.setIssuedBy(this.findUUID(result, result2));
            punishment.setIssuedOn(GlobalConfig.SERVER.NAME);
            punishment.setIssuedAt(System.currentTimeMillis());
            punishment.setReason(text);
            punishment.setPermanent(EditorPayload.matchesEnabled(result2, "permanent", true));
            punishment.setDuration(EditorPayload.calculateTime(result2, "duration", 0L));
            punishment.setSilent(EditorPayload.matchesEnabled(result2, "silent", false));
            punishment.setIp(EditorPayload.matchesEnabled(result2, "ip", false));
            this.platform
                    .getPunishmentHandler()
                    .getRepository()
                    .saveSync(punishment.getUuid(), punishment);
            this.platform.getPunishmentHandler().executePunishment(punishment);
            return this.findJsonElement(
                    OperationResult.of(punishmentType.getPunished() + " " + profile.getName()));
        };
    }

    public EditorOpHandler getEditorOpHandler3() {
        return (result, result2) -> {
            IProfile profile = this.findProfile(result2);
            PunishmentType punishmentType;
            if (!(punishmentType = this.findPunishmentType(result2)).isRevokable()) {
                throw new EditorOperationException(
                        "NOT_REVOKABLE",
                        "a " + punishmentType.getNiceName().toLowerCase() + " cannot be lifted");
            }
            String text = EditorPayload.formatString(result2, "reason", "Editor");
            IPunishment punishment;
            if ((punishment = profile.getActivePunishment(punishmentType)) == null) {
                throw new EditorOperationException(
                        "NOT_PUNISHED",
                        profile.getName() + " is not " + punishmentType.getPunished());
            }
            punishment.setRemovedSilent(EditorPayload.matchesEnabled(result2, "silent", false));
            punishment.remove(this.findUUID(result, result2), text);
            this.platform
                    .getPunishmentHandler()
                    .getRepository()
                    .saveSync(punishment.getUuid(), punishment);
            this.platform.getPunishmentHandler().executeRevokePunishment(punishment);
            return this.findJsonElement(
                    OperationResult.of(punishmentType.getRemoved() + " " + profile.getName()));
        };
    }

    private void applyUUID(UUID uUID, IRank rank) {
        IProfile profile;
        if ((profile = this.platform.getProfileHandler().getProfile(uUID)) == null) {
            throw new EditorOperationException("FORBIDDEN", "no profile for whoever asked");
        }
        profile.updatePermissions();
        if (!this.platform.getRankHandler().canGrant(profile, rank)) {
            throw new EditorOperationException(
                    "FORBIDDEN",
                    "you may not grant "
                            + rank.getName()
                            + (rank.isGrantable() ? "" : " - it is marked ungrantable"));
        }
    }

    private UUID findUUID(EditorSession editorSession, JsonObject jsonObject) {
        String text;
        if ((text = EditorPayload.formatString(jsonObject, "actorUuid", null)) == null) {
            return editorSession.getId();
        }
        try {
            return UUID.fromString(text);
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new EditorOperationException("BAD_REQUEST", "actorUuid was not a uuid: " + text);
        }
    }

    private IProfile findProfile(JsonObject jsonObject) {
        UUID uUID = EditorPayload.findUUID(jsonObject, "uuid");
        IProfile profile;
        if ((profile = this.platform.getProfileHandler().getProfile(uUID)) == null) {
            throw new EditorOperationException("NO_SUCH_PROFILE", "no profile for " + uUID);
        } else {
            return profile;
        }
    }

    private PunishmentType findPunishmentType(JsonObject jsonObject) {
        String text = EditorPayload.formatString(jsonObject, "type");
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

    private List<IScope> findList(JsonObject jsonObject) {
        ArrayList<IScope> values = new ArrayList<>();
        JsonElement jsonElement;
        if ((jsonElement = jsonObject.get("scopes")) != null
                && !jsonElement.isJsonNull()
                && jsonElement.isJsonArray()
                && jsonElement.getAsJsonArray().size() != 0) {
            for (JsonElement jsonElement2 :
                    (Iterable<JsonElement>) ((Iterable) jsonElement.getAsJsonArray())) {
                try {
                    values.add(
                            this.platform.getRankHandler().createScope(jsonElement2.getAsString()));
                } catch (IllegalArgumentException illegalArgumentException) {
                    throw new EditorOperationException(
                            "BAD_REQUEST", "invalid scope " + jsonElement2.getAsString());
                }
            }
            return values;
        } else {
            IScope scope;
            if ((scope = this.platform.getRankHandler().getGlobalScope()) != null) {
                values.add(scope);
            }
            return values;
        }
    }

    private JsonElement findJsonElement(OperationResult operationResult) {
        return this.platform.getGson().toJsonTree(operationResult);
    }

    public EditorOpHandler getEditorOpHandler4() {
        return (result, result2) -> {
            UUID uUID = EditorPayload.findUUID(result2, "id");
            IPunishment punishment;
            if ((punishment =
                            this.platform
                                    .getPunishmentHandler()
                                    .getRepository()
                                    .fetchSync(uUID)
                                    .orElse(null))
                    == null) {
                throw new EditorOperationException("NO_SUCH_PUNISHMENT", "no punishment " + uUID);
            }
            if (punishment.isVoided()) {
                throw new EditorOperationException(
                        "BAD_REQUEST", "a voided punishment is not edited");
            }
            ArrayList<String> values = new ArrayList<>();
            String text;
            if ((text = EditorPayload.formatString(result2, "reason", null)) != null
                    && !text.equals(punishment.getReason())) {
                values.add("reason to " + text);
                punishment.setReason(text);
            }
            long value;
            if ((value = EditorPayload.calculateTime(result2, "issuedAt", 0L)) > 0L
                    && value != punishment.getIssuedAt()) {
                if (value > System.currentTimeMillis()) {
                    throw new EditorOperationException(
                            "BAD_REQUEST", "a punishment cannot start in the future");
                }
                values.add("start date");
                punishment.setIssuedAt(value);
            }
            boolean matches;
            if (result2.has("permanent")
                    && (matches = EditorPayload.matchesEnabled(result2, "permanent", false))
                            != punishment.isPermanent()) {
                values.add(matches ? "to permanent" : "to temporary");
                punishment.setPermanent(matches);
            }
            long value2;
            if ((value2 = EditorPayload.calculateTime(result2, "duration", 0L)) > 0L
                    && value2 != punishment.getDuration()) {
                values.add("length");
                punishment.setDuration(value2);
                punishment.setPermanent(false);
            }
            if (values.isEmpty()) {
                throw new EditorOperationException("BAD_REQUEST", "nothing was changed");
            }
            this.platform.getPunishmentHandler().getRepository().saveSync(uUID, punishment);
            PanelBroadcast.applyString(
                    "&7[&bPanel&7] &f"
                            + EditorPayload.formatString(result2, "actorName", "somebody")
                            + " &7edited a "
                            + punishment.getPunishmentType()
                            + " &7on &f"
                            + punishment.getTarget()
                            + "&7: "
                            + String.join(", ", values));
            JsonObject jsonObject;
            (jsonObject = new JsonObject()).addProperty("edited", true);
            return jsonObject;
        };
    }
}
