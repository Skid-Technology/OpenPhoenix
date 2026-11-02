package xyz.refinedev.phoenix.editor.op;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.commandlog.ICommandLog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PunishmentStatisticsOperations {

    private final CommonPlatform platform;

    public PunishmentStatisticsOperations(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }

    public EditorOpHandler getEditorOpHandler() {
        return (result, result2) -> {
            int value = (int) Math.min(EditorPayload.calculateTime(result2, "days", 7L), 90L);
            long value2 = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(value);
            String text = EditorPayload.formatString(result2, "issuedBy", null);
            String text2 = EditorPayload.formatString(result2, "server", null);
            List values;
            try {
                if (text != null) {
                    values =
                            this.platform
                                    .getCommandLogHandler()
                                    .findByIssuedBy(this.findUUID(text), value2, false)
                                    .get(10L, TimeUnit.SECONDS);
                } else {
                    if (text2 == null) {
                        throw new EditorOperationException(
                                "BAD_REQUEST", "name a staff member or a server");
                    }
                    values =
                            this.platform
                                    .getCommandLogHandler()
                                    .findByServer(text2, value2, false)
                                    .get(10L, TimeUnit.SECONDS);
                }
            } catch (Exception exception) {
                throw new EditorOperationException(
                        "INTERNAL", "the command logs could not be read");
            }
            List values2 = new ArrayList();
            Iterator iterator2 = values.iterator();
            while (iterator2.hasNext()) {
                ICommandLog commandLog;
                if ((commandLog = (ICommandLog) iterator2.next()) != null) {
                    values2.add(commandLog);
                }
            }
            values2.sort(Comparator.comparingLong(ICommandLog::getIssuedOn).reversed());
            boolean matches;
            if (matches = values2.size() > 250) {
                values2 = values2.subList(0, 250);
            }
            HashSet<UUID> values3 = new HashSet<>();
            Iterator iterator3 = values2.iterator();
            while (iterator3.hasNext()) {
                ICommandLog commandLog2;
                if ((commandLog2 = (ICommandLog) iterator3.next()).getIssuedBy() != null) {
                    values3.add(commandLog2.getIssuedBy());
                }
            }
            HashMap entries = new HashMap();
            if (!values3.isEmpty()) {
                Iterator iterator4 =
                        this.platform.getProfileHandler().getProfiles(values3).iterator();
                while (iterator4.hasNext()) {
                    IProfile profile;
                    if ((profile = (IProfile) iterator4.next()) != null) {
                        entries.put(profile.getUniqueId(), profile.getName());
                    }
                }
            }
            JsonArray jsonArray = new JsonArray();
            for (ICommandLog commandLog3 : (Iterable<ICommandLog>) ((Iterable) values2)) {
                JsonObject jsonObject;
                (jsonObject = new JsonObject())
                        .addProperty(
                                "id",
                                commandLog3.getId() == null
                                        ? null
                                        : commandLog3.getId().toString());
                jsonObject.addProperty(
                        "issuedBy",
                        commandLog3.getIssuedBy() == null
                                ? null
                                : commandLog3.getIssuedBy().toString());
                jsonObject.addProperty(
                        "issuedByName", (String) entries.get(commandLog3.getIssuedBy()));
                jsonObject.addProperty("command", commandLog3.getCommand());
                jsonObject.addProperty("server", commandLog3.getServer());
                jsonObject.addProperty("at", commandLog3.getIssuedOn());
                jsonArray.add(jsonObject);
            }
            JsonObject jsonObject2;
            (jsonObject2 = new JsonObject()).add("commands", jsonArray);
            jsonObject2.addProperty("truncated", matches);
            jsonObject2.addProperty("days", value);
            return jsonObject2;
        };
    }

    private UUID findUUID(String text) {
        try {
            return UUID.fromString(text);
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new EditorOperationException("BAD_REQUEST", "issuedBy was not a uuid: " + text);
        }
    }
}
