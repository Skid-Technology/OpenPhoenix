package xyz.refinedev.phoenix.editor;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.editor.op.EditorOpHandler;
import xyz.refinedev.phoenix.setup.SetupCheck;
import xyz.refinedev.phoenix.setup.SetupSnapshot;
import xyz.refinedev.phoenix.setup.SetupTier;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class EditorSetupOperations {

    private final Phoenix plugin;

    public EditorSetupOperations(Phoenix phoenix) {
        this.plugin = phoenix;
    }

    public EditorOpHandler getEditorOpHandler() {
        return (result, result2) -> {
            ArrayList<SetupCheck> values;
            (values = new ArrayList<>(SetupSnapshot.findList(this.plugin, SetupTier.ESSENTIALS)))
                    .addAll(SetupSnapshot.findList(this.plugin, SetupTier.FEATURES));
            Map<String, List<SetupCheck>> entries = new LinkedHashMap();
            for (SetupCheck setupCheck : values) {
                entries.computeIfAbsent(setupCheck.getText(), result3 -> new ArrayList<>())
                        .add(setupCheck);
            }
            JsonArray jsonArray = new JsonArray();
            int value = 0;
            for (Entry entry : entries.entrySet()) {
                JsonObject jsonObject;
                (jsonObject = new JsonObject()).addProperty("name", (String) entry.getKey());
                jsonObject.addProperty(
                        "status",
                        SetupCheck.findCheckStatus((List<SetupCheck>) entry.getValue()).name());
                JsonArray jsonArray2 = new JsonArray();
                for (SetupCheck setupCheck2 :
                        (Iterable<SetupCheck>) ((Iterable) (List) entry.getValue())) {
                    JsonObject jsonObject2;
                    (jsonObject2 = new JsonObject())
                            .addProperty("status", setupCheck2.getCheckStatus().name());
                    jsonObject2.addProperty("title", setupCheck2.getTextString());
                    jsonObject2.addProperty("fix", setupCheck2.getText2());
                    JsonArray jsonArray3 = new JsonArray();
                    for (String text : setupCheck2.getStrings()) {
                        jsonArray3.add(new JsonPrimitive(text));
                    }
                    jsonObject2.add("detail", jsonArray3);
                    jsonArray2.add(jsonObject2);
                    if (setupCheck2.getCheckStatus().name().equals("FAIL")) {
                        value++;
                    }
                }
                jsonObject.add("checks", jsonArray2);
                jsonArray.add(jsonObject);
            }
            JsonObject jsonObject3;
            (jsonObject3 = new JsonObject()).addProperty("answeredBy", GlobalConfig.SERVER.NAME);
            jsonObject3.add("areas", jsonArray);
            jsonObject3.addProperty("checks", values.size());
            jsonObject3.addProperty("failing", value);
            jsonObject3.addProperty("status", SetupCheck.findCheckStatus(values).name());
            return jsonObject3;
        };
    }
}
