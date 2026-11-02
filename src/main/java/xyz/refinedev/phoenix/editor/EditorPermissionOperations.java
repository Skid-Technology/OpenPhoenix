package xyz.refinedev.phoenix.editor;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.editor.op.EditorOpHandler;
import xyz.refinedev.phoenix.editor.op.EditorOperationException;
import xyz.refinedev.phoenix.editor.op.EditorPayload;
import xyz.refinedev.phoenix.editor.panel.PanelBroadcast;
import xyz.refinedev.phoenix.packet.impl.ranks.RankUpdatePacket;
import xyz.refinedev.phoenix.permissible.database.PermissionInfo;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.rank.permission.IPermission;
import xyz.refinedev.phoenix.rank.permission.Permission;
import xyz.refinedev.phoenix.scope.IScope;
import xyz.refinedev.phoenix.scope.Scope;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class EditorPermissionOperations {

    private final Phoenix plugin;

    public EditorPermissionOperations(Phoenix phoenix) {
        this.plugin = phoenix;
    }

    public EditorOpHandler getEditorOpHandler() {
        return (result, result2) -> {
            String text = EditorPayload.formatString(result2, "search", "").trim().toLowerCase();
            String text2 = EditorPayload.formatString(result2, "plugin", "").trim();
            LinkedHashMap<String, List<String>> entries = new LinkedHashMap();
            Iterator<IRank> iterator2 =
                    this.plugin.getRankHandler().getRepository().getCache().iterator();
            while (iterator2.hasNext()) {
                IRank rank;
                if ((rank = (IRank) iterator2.next()).getPermissions() != null) {
                    Iterator<IPermission> iterator3 = rank.getPermissions().iterator();
                    while (iterator3.hasNext()) {
                        IPermission permission;
                        if ((permission = (IPermission) iterator3.next()) != null
                                && permission.getPermission() != null) {
                            entries.computeIfAbsent(
                                            permission.getPermission().toLowerCase(),
                                            result3 -> new ArrayList<>())
                                    .add(rank.getName());
                        }
                    }
                }
            }
            JsonArray jsonArray = new JsonArray();
            int value = 0;
            Iterator<PermissionInfo> iterator4 =
                    this.plugin.getPermissionDatabase().getSet().iterator();
            while (iterator4.hasNext()) {
                String text3;
                PermissionInfo permissionInfo;
                if ((text3 = (permissionInfo = (PermissionInfo) iterator4.next()).getPermission())
                                != null
                        && (text.isEmpty() || text3.toLowerCase().contains(text))
                        && (text2.isEmpty()
                                || text2.equalsIgnoreCase(permissionInfo.getPlugin()))) {
                    value++;
                    JsonObject jsonObject;
                    (jsonObject = new JsonObject()).addProperty("permission", text3);
                    jsonObject.addProperty("plugin", permissionInfo.getPlugin());
                    jsonObject.addProperty("description", permissionInfo.getDescription());
                    jsonObject.addProperty("source", permissionInfo.getSource());
                    JsonArray jsonArray2 = new JsonArray();
                    for (String text4 :
                            (Iterable<String>)
                                    ((Iterable)
                                            entries.getOrDefault(
                                                    text3.toLowerCase(), new ArrayList()))) {
                        jsonArray2.add(new JsonPrimitive(text4));
                    }
                    jsonObject.add("ranks", jsonArray2);
                    jsonArray.add(jsonObject);
                }
            }
            JsonObject jsonObject2;
            (jsonObject2 = new JsonObject()).add("permissions", jsonArray);
            jsonObject2.addProperty("total", value);
            return jsonObject2;
        };
    }

    public EditorOpHandler getEditorOpHandlerByEditorOpHandler() {
        return (result, result2) -> {
            String text;
            if ((text = EditorPayload.formatString(result2, "permission").trim()).isEmpty()) {
                throw new EditorOperationException("BAD_REQUEST", "no permission given");
            }
            boolean matches = EditorPayload.matchesEnabled(result2, "remove", false);
            List<IScope> values = this.findList(result2);
            JsonArray jsonArray;
            if ((jsonArray =
                                    result2.has("ranks") && result2.get("ranks").isJsonArray()
                                            ? result2.getAsJsonArray("ranks")
                                            : new JsonArray())
                            .size()
                    == 0) {
                throw new EditorOperationException("BAD_REQUEST", "no ranks given");
            }
            ArrayList<String> values2 = new ArrayList<>();
            for (int index = 0; index < jsonArray.size(); index++) {
                IRank rank;
                if ((rank =
                                this.plugin
                                        .getRankHandler()
                                        .getRank(
                                                UUID.fromString(
                                                        jsonArray.get(index).getAsString())))
                        != null) {
                    ArrayList<IPermission> values3 = new ArrayList<>(rank.getPermissions());
                    boolean matches2;
                    if (matches) {
                        String text2 = text;
                        matches2 =
                                values3.removeIf(
                                        result3 ->
                                                result3 != null
                                                        && text2.equalsIgnoreCase(
                                                                result3.getPermission()));
                    } else {
                        Stream<IPermission> stream2 = values3.stream();
                        String text3 = text;
                        matches2 =
                                stream2.noneMatch(
                                                result3 ->
                                                        result3 != null
                                                                && text3.equalsIgnoreCase(
                                                                        result3.getPermission()))
                                        && values3.add(new Permission(text, values));
                    }
                    if (matches2) {
                        rank.setPermissions(values3);
                        this.plugin.getRankHandler().getRepository().saveSync(rank);
                        this.plugin.getPacketHandler().sendSync(new RankUpdatePacket(rank, false));
                        values2.add(rank.getName());
                    }
                }
            }
            if (!values2.isEmpty()) {
                PanelBroadcast.applyString(
                        "&7[&bPanel&7] &f"
                                + EditorPayload.formatString(result2, "actorName", "somebody")
                                + (matches ? " &7removed &f" : " &7added &f")
                                + text
                                + " &7on &f"
                                + String.join(", ", values2));
            }
            JsonObject jsonObject;
            (jsonObject = new JsonObject()).addProperty("changed", values2.size());
            return jsonObject;
        };
    }

    private List<IScope> findList(JsonObject jsonObject) {
        ArrayList<IScope> values = new ArrayList<>();
        if (jsonObject.has("scopes") && jsonObject.get("scopes").isJsonArray()) {
            JsonArray asJsonArray = jsonObject.getAsJsonArray("scopes");
            for (int index = 0; index < asJsonArray.size(); index++) {
                values.add(new Scope(asJsonArray.get(index).getAsString()));
            }
        }
        if (values.isEmpty()) {
            values.add(Scope.scopeScope);
        }
        return values;
    }
}
