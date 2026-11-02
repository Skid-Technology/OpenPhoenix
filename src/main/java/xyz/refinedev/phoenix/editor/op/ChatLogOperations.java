package xyz.refinedev.phoenix.editor.op;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.profile.chatsnapshot.IChatLog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ChatLogOperations {

    private final CommonPlatform platform;

    public ChatLogOperations(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }

    public EditorOpHandler getEditorOpHandler() {
        return (result, result2) -> {
            UUID uUID = EditorPayload.findUUID(result2, "uuid");
            String text = EditorPayload.formatString(result2, "search", null);
            List<IChatLog> values;
            try {
                values =
                        (text != null && !text.trim().isEmpty()
                                        ? this.platform
                                                .getChatLogHandler()
                                                .getByPlayer(uUID, text.trim())
                                        : this.platform.getChatLogHandler().getByPlayer(uUID))
                                .get(10L, TimeUnit.SECONDS);
            } catch (Exception exception) {
                throw new EditorOperationException("INTERNAL", "the chat logs could not be read");
            }
            List values2 = new ArrayList();
            Iterator iterator2 = values.iterator();
            while (iterator2.hasNext()) {
                IChatLog chatLog;
                if ((chatLog = (IChatLog) iterator2.next()) != null) {
                    values2.add(chatLog);
                }
            }
            values2.sort(Comparator.comparingLong(IChatLog::getTimestamp).reversed());
            boolean matches;
            if (matches = values2.size() > 200) {
                values2 = values2.subList(0, 200);
            }
            JsonArray jsonArray = new JsonArray();
            for (IChatLog chatLog2 : (Iterable<IChatLog>) ((Iterable) values2)) {
                JsonObject jsonObject;
                (jsonObject = new JsonObject()).addProperty("id", chatLog2.getId());
                jsonObject.addProperty("at", chatLog2.getTimestamp());
                jsonObject.addProperty("message", chatLog2.getMessage());
                jsonArray.add(jsonObject);
            }
            JsonObject jsonObject2;
            (jsonObject2 = new JsonObject()).add("messages", jsonArray);
            jsonObject2.addProperty("truncated", matches);
            return jsonObject2;
        };
    }
}
