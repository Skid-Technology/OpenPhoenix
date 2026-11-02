package xyz.refinedev.phoenix.editor.op;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.editor.panel.PanelPermissions;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.rank.IRank;

import java.util.Iterator;
import java.util.UUID;

public class RankOperations {

    private final CommonPlatform platform;

    public RankOperations(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }

    public EditorOpHandler getEditorOpHandler() {
        return (result, result2) -> {
            UUID uUID = PanelPermissions.findUUID(result2);
            IProfile profile;
            if ((profile = this.platform.getProfileHandler().getProfile(uUID)) != null) {
                profile.updatePermissions();
            }
            JsonArray jsonArray = new JsonArray();
            Iterator<IRank> iterator2 =
                    this.platform.getRankHandler().getSortedRanksReversed().iterator();
            while (iterator2.hasNext()) {
                IRank rank;
                if ((rank = (IRank) iterator2.next()) != null) {
                    JsonObject jsonObject;
                    (jsonObject = new JsonObject()).addProperty("uuid", rank.getUuid().toString());
                    jsonObject.addProperty("name", rank.getName());
                    jsonObject.addProperty("displayName", rank.getDisplayName());
                    jsonObject.addProperty("color", rank.getColor());
                    jsonObject.addProperty("priority", rank.getPriority());
                    jsonObject.addProperty("grantable", rank.isGrantable());
                    jsonObject.addProperty(
                            "canGrant",
                            profile != null
                                    && this.platform.getRankHandler().canGrant(profile, rank));
                    jsonArray.add(jsonObject);
                }
            }
            JsonObject jsonObject2;
            (jsonObject2 = new JsonObject()).add("ranks", jsonArray);
            return jsonObject2;
        };
    }
}
