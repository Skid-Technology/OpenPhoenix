package xyz.refinedev.phoenix.editor;

import com.google.gson.JsonObject;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.editor.op.EditorOpHandler;
import xyz.refinedev.phoenix.editor.op.EditorOperationException;
import xyz.refinedev.phoenix.editor.op.EditorPayload;
import xyz.refinedev.phoenix.editor.panel.PanelBroadcast;
import xyz.refinedev.phoenix.editor.panel.PanelPermissions;
import xyz.refinedev.phoenix.packet.impl.profile.BukkitGrantsUpdatePacket;
import xyz.refinedev.phoenix.packet.impl.profile.ProfileDeletePacket;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.grant.IGrant;
import xyz.refinedev.phoenix.utils.repository.filters.api.Filters;

import java.util.Optional;
import java.util.UUID;

public class EditorProfileErasureOperations {

    private final Phoenix plugin;

    public EditorProfileErasureOperations(Phoenix phoenix) {
        this.plugin = phoenix;
    }

    public EditorOpHandler getEditorOpHandler() {
        return (result, result2) -> {
            IProfile profile;
            String text =
                    (profile = this.findProfile(EditorPayload.findUUID(result2, "uuid")))
                            .getUniqueId()
                            .toString();
            JsonObject jsonObject;
            (jsonObject = new JsonObject())
                    .add("profile", this.plugin.getGson().toJsonTree(profile));
            jsonObject.add(
                    "grants",
                    this.plugin
                            .getGson()
                            .toJsonTree(
                                    this.plugin.getGrantHandler().getGrants(profile.getUuid())));
            jsonObject.add(
                    "punishments",
                    this.plugin
                            .getGson()
                            .toJsonTree(
                                    this.plugin
                                            .getPunishmentHandler()
                                            .getPunishments(profile.getUuid())));
            jsonObject.add(
                    "customTags",
                    this.plugin
                            .getGson()
                            .toJsonTree(
                                    this.plugin
                                            .getCustomTagHandler()
                                            .getRepository()
                                            .fetchAllSync(Filters.eq("creator", text))));
            jsonObject.add(
                    "disguiseHistory",
                    this.plugin
                            .getGson()
                            .toJsonTree(
                                    this.plugin
                                            .getDisguiseHandler()
                                            .getHistoryRepository()
                                            .fetchAllSync(Filters.eq("player", text))));
            jsonObject.add(
                    "chatSnapshots",
                    this.plugin
                            .getGson()
                            .toJsonTree(
                                    this.plugin
                                            .getChatSnapshotHandler()
                                            .getRepository()
                                            .fetchAllSync(Filters.eq("requestedBy", text))));
            jsonObject.add(
                    "chatLogs",
                    this.plugin
                            .getGson()
                            .toJsonTree(
                                    this.plugin
                                            .getChatLogHandler()
                                            .getRepository()
                                            .fetchAllSync(Filters.eq("sender", text))));
            jsonObject.add(
                    "logins",
                    this.plugin
                            .getGson()
                            .toJsonTree(
                                    this.plugin
                                            .getLoginHandler()
                                            .getRepository()
                                            .fetchAllSync(Filters.eq("target", text))));
            if (this.plugin.getSecurityHandler().hasSecurity(profile.getUuid())) {
                Optional<xyz.refinedev.phoenix.profile.security.ISecurityUser> optional =
                        this.plugin
                                .getSecurityHandler()
                                .getRepository()
                                .fetchSync(profile.getUuid());
                JsonObject jsonObject2 = jsonObject;
                optional.ifPresent(
                        result3 ->
                                jsonObject2.add(
                                        "security", this.plugin.getGson().toJsonTree(result3)));
            }
            PanelBroadcast.applyString(
                    "&7[&bPanel&7] &f"
                            + EditorPayload.formatString(result2, "actorName", "somebody")
                            + " &7exported the data held on &f"
                            + profile.getName()
                            + "&7.");
            JsonObject jsonObject3;
            (jsonObject3 = new JsonObject()).addProperty("uuid", text);
            jsonObject3.addProperty("name", profile.getName());
            jsonObject3.add("data", jsonObject);
            return jsonObject3;
        };
    }

    public EditorOpHandler getEditorOpHandlerByEditorOpHandler() {
        return (result, result2) -> {
            UUID uUID = PanelPermissions.findUUID(result2);
            if (!this.plugin.getSecurityHandler().hasSecurity(uUID)) {
                throw new EditorOperationException(
                        "FORBIDDEN", "only a security user may erase somebody");
            }
            EditorAuthentication.applyPhoenix(this.plugin, uUID, result2);
            IProfile profile = this.findProfile(EditorPayload.findUUID(result2, "uuid"));
            if (!EditorPayload.formatString(result2, "confirm")
                    .trim()
                    .equalsIgnoreCase(profile.getName())) {
                throw new EditorOperationException(
                        "BAD_REQUEST", "type " + profile.getName() + " exactly to confirm this");
            }
            String text = profile.getUniqueId().toString();
            JsonObject jsonObject = (JsonObject) this.getEditorOpHandler().handle(result, result2);
            for (IGrant grant : this.plugin.getGrantHandler().getGrants(profile.getUuid())) {
                this.plugin.getGrantHandler().getRepository().deleteSync(grant.getUuid());
            }
            this.plugin.getProfileHandler().getCacheRepository().delete(profile.getUniqueId());
            this.plugin.getProfileHandler().getRepository().deleteSync(profile.getUniqueId());
            this.plugin.getSecurityHandler().removeSecurity(profile.getUuid());
            this.plugin
                    .getCustomTagHandler()
                    .getRepository()
                    .deleteSync(Filters.eq("creator", text));
            this.plugin
                    .getDisguiseHandler()
                    .getHistoryRepository()
                    .deleteSync(Filters.eq("player", text));
            this.plugin
                    .getChatSnapshotHandler()
                    .getRepository()
                    .deleteSync(Filters.eq("requestedBy", text));
            this.plugin.getLoginHandler().getRepository().deleteSync(Filters.eq("target", text));
            this.plugin.getChatLogHandler().clearLogs(profile.getUuid());
            this.plugin.getPacketHandler().send(new BukkitGrantsUpdatePacket(profile.getUuid()));
            this.plugin.getPacketHandler().send(new ProfileDeletePacket(profile));
            PanelBroadcast.applyString(
                    "&7[&bPanel&7] &c"
                            + EditorPayload.formatString(result2, "actorName", "somebody")
                            + " &7erased all data held on &f"
                            + profile.getName()
                            + "&7. Punishments were kept.");
            JsonObject jsonObject2;
            (jsonObject2 = new JsonObject()).addProperty("erased", true);
            jsonObject2.addProperty("name", profile.getName());
            jsonObject2.add("export", jsonObject.getAsJsonObject("data"));
            return jsonObject2;
        };
    }

    private IProfile findProfile(UUID uUID) {
        IProfile profile;
        if ((profile = this.plugin.getProfileHandler().getProfile(uUID)) == null) {
            throw new EditorOperationException("NO_SUCH_PROFILE", "no profile for " + uUID);
        } else {
            return profile;
        }
    }
}
