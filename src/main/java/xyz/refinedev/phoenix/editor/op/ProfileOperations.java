package xyz.refinedev.phoenix.editor.op;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.disguise.IDisguiseData;
import xyz.refinedev.phoenix.profile.grant.IGrant;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.server.IServerData;
import xyz.refinedev.phoenix.utils.repository.filters.api.Filters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProfileOperations {

    private final CommonPlatform platform;

    public ProfileOperations(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }

    public EditorOpHandler getEditorOpHandler() {
        return (result, result2) -> {
            ArrayList<IRank> values2 = new ArrayList();
            Iterator<IRank> iterator2 =
                    this.platform.getRankHandler().getRepository().getCache().iterator();
            while (iterator2.hasNext()) {
                IRank rank;
                if ((rank = (IRank) iterator2.next()) != null && rank.isStaff()) {
                    values2.add(rank);
                }
            }
            values2.sort(
                    (result3, result4) ->
                            Integer.compare(result4.getPriority(), result3.getPriority()));
            Map<java.util.UUID, JsonObject> entries = new LinkedHashMap();
            for (IRank rank2 : (Iterable<IRank>) ((Iterable) values2)) {
                List<IGrant> values3;
                try {
                    values3 =
                            this.platform
                                    .getGrantHandler()
                                    .getRepository()
                                    .fetchAllSync(Filters.eq("rankId", rank2.getUuid().toString()));
                } catch (Exception exception) {
                    continue;
                }
                Iterator iterator3 = values3.iterator();
                while (iterator3.hasNext()) {
                    IGrant grant;
                    if ((grant = (IGrant) iterator3.next()) != null
                            && grant.getTarget() != null
                            && grant.isActive()
                            && !entries.containsKey(grant.getTarget())) {
                        JsonObject jsonObject;
                        (jsonObject = new JsonObject())
                                .addProperty("uuid", grant.getTarget().toString());
                        jsonObject.addProperty("rank", rank2.getName());
                        jsonObject.addProperty("rankColor", rank2.getColor());
                        jsonObject.addProperty("since", grant.getIssuedAt());
                        entries.put(grant.getTarget(), jsonObject);
                    }
                }
            }
            Iterator<IProfile> iterator4 =
                    this.platform.getProfileHandler().getProfiles(entries.keySet()).iterator();
            while (iterator4.hasNext()) {
                IProfile profile;
                JsonObject jsonObject2;
                if ((profile = (IProfile) iterator4.next()) != null
                        && (jsonObject2 = (JsonObject) entries.get(profile.getUniqueId()))
                                != null) {
                    IServerData presence =
                            this.platform.getNetworkHandler().getPresence(profile.getUniqueId());
                    jsonObject2.addProperty("name", profile.getName());
                    jsonObject2.addProperty("online", presence != null);
                    jsonObject2.addProperty(
                            "server", presence == null ? "" : presence.getServerName());
                    jsonObject2.addProperty(
                            "twoFactor",
                            this.platform.getSecurityHandler().hasSecurity(profile.getUniqueId()));
                    IDisguiseData disguiseData;
                    boolean matches =
                            (disguiseData = profile.getDisguiseData()) != null
                                    && disguiseData.isDisguised();
                    jsonObject2.addProperty("disguised", matches);
                    jsonObject2.addProperty(
                            "disguisedAs", matches ? disguiseData.getDisguiseName() : null);
                    jsonObject2.addProperty(
                            "punishments",
                            profile.getPunishments() == null ? 0 : profile.getPunishments().size());
                }
            }
            JsonArray jsonArray = new JsonArray();
            for (JsonObject jsonObject3 : entries.values()) {
                jsonArray.add(jsonObject3);
            }
            JsonObject jsonObject4;
            (jsonObject4 = new JsonObject()).add("staff", jsonArray);
            return jsonObject4;
        };
    }
}
