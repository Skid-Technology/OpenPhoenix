package xyz.refinedev.phoenix.editor.op;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.chatsnapshot.IChatEntry;
import xyz.refinedev.phoenix.profile.chatsnapshot.IChatSnapshot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ChatSnapshotOperations {

    private final CommonPlatform platform;

    public ChatSnapshotOperations(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }

    public EditorOpHandler getEditorOpHandler() {
        return (result, result2) -> {
            UUID uUID = EditorPayload.findUUID(result2, "uuid");
            List<IChatSnapshot> values;
            try {
                values =
                        this.platform
                                .getChatSnapshotHandler()
                                .getByPlayer(uUID)
                                .get(10L, TimeUnit.SECONDS);
            } catch (Exception exception) {
                throw new EditorOperationException("INTERNAL", "the snapshots could not be read");
            }
            HashSet<UUID> values2 = new HashSet<>();
            Iterator iterator2 = values.iterator();
            while (iterator2.hasNext()) {
                IChatSnapshot chatSnapshot;
                if ((chatSnapshot = (IChatSnapshot) iterator2.next()) != null
                        && chatSnapshot.getRequestedBy() != null) {
                    values2.add(chatSnapshot.getRequestedBy());
                }
            }
            Map entries = this.findMap(values2);
            JsonArray jsonArray = new JsonArray();
            Iterator iterator3 = values.iterator();
            while (iterator3.hasNext()) {
                IChatSnapshot chatSnapshot2;
                if ((chatSnapshot2 = (IChatSnapshot) iterator3.next()) != null) {
                    JsonObject jsonObject;
                    (jsonObject = new JsonObject()).addProperty("id", chatSnapshot2.getNiceId());
                    jsonObject.addProperty("createdOn", chatSnapshot2.getCreatedOn());
                    jsonObject.addProperty("wholeChat", chatSnapshot2.isWholeChat());
                    jsonObject.addProperty(
                            "lines",
                            chatSnapshot2.getChat() == null ? 0 : chatSnapshot2.getChat().size());
                    jsonObject.addProperty(
                            "requestedBy", (String) entries.get(chatSnapshot2.getRequestedBy()));
                    jsonArray.add(jsonObject);
                }
            }
            JsonObject jsonObject2;
            (jsonObject2 = new JsonObject()).add("snapshots", jsonArray);
            return jsonObject2;
        };
    }

    public EditorOpHandler getEditorOpHandlerByEditorOpHandler() {
        return (result, result2) -> {
            String text = EditorPayload.formatString(result2, "id");
            IChatSnapshot chatSnapshot;
            if ((chatSnapshot = this.platform.getChatSnapshotHandler().getByNiceId(text)) == null) {
                throw new EditorOperationException("NO_SUCH_SNAPSHOT", "no snapshot " + text);
            }
            List<IChatEntry> chat = chatSnapshot.getChat();
            HashSet<UUID> values = new HashSet<>();
            if (chat != null) {
                Iterator iterator2 = chat.iterator();
                while (iterator2.hasNext()) {
                    IChatEntry chatEntry;
                    if ((chatEntry = (IChatEntry) iterator2.next()) != null
                            && chatEntry.getUuid() != null) {
                        values.add(chatEntry.getUuid());
                    }
                }
            }
            if (chatSnapshot.getRequestedBy() != null) {
                values.add(chatSnapshot.getRequestedBy());
            }
            Map entries = this.findMap(values);
            JsonArray jsonArray = new JsonArray();
            if (chat != null) {
                Iterator iterator3 = chat.iterator();
                while (iterator3.hasNext()) {
                    IChatEntry chatEntry2;
                    if ((chatEntry2 = (IChatEntry) iterator3.next()) != null) {
                        if (jsonArray.size() >= 300) {
                            break;
                        }
                        JsonObject jsonObject;
                        (jsonObject = new JsonObject())
                                .addProperty(
                                        "uuid",
                                        chatEntry2.getUuid() == null
                                                ? null
                                                : chatEntry2.getUuid().toString());
                        jsonObject.addProperty("name", (String) entries.get(chatEntry2.getUuid()));
                        jsonObject.addProperty("at", chatEntry2.getTime());
                        jsonObject.addProperty("message", chatEntry2.getMessage());
                        jsonArray.add(jsonObject);
                    }
                }
            }
            JsonObject jsonObject2;
            (jsonObject2 = new JsonObject()).addProperty("id", chatSnapshot.getNiceId());
            jsonObject2.addProperty("createdOn", chatSnapshot.getCreatedOn());
            jsonObject2.addProperty("wholeChat", chatSnapshot.isWholeChat());
            jsonObject2.addProperty(
                    "requestedBy", (String) entries.get(chatSnapshot.getRequestedBy()));
            jsonObject2.addProperty("truncated", chat != null && chat.size() > 300);
            jsonObject2.add("lines", jsonArray);
            return jsonObject2;
        };
    }

    private Map<UUID, String> findMap(Set<UUID> values) {
        HashMap<UUID, String> entries = new HashMap<>();
        if (values.isEmpty()) {
            return entries;
        }
        Iterator<IProfile> iterator2 =
                this.platform.getProfileHandler().getProfiles(values).iterator();
        while (iterator2.hasNext()) {
            IProfile profile;
            if ((profile = (IProfile) iterator2.next()) != null) {
                entries.put(profile.getUniqueId(), profile.getName());
            }
        }
        return entries;
    }
}
