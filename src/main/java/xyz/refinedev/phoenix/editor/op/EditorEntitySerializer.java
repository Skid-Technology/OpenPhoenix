package xyz.refinedev.phoenix.editor.op;

import com.google.gson.JsonArray;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.editor.dto.GrantSummary;
import xyz.refinedev.phoenix.editor.dto.PunishmentSummary;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.grant.IGrant;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.scope.IScope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class EditorEntitySerializer {

    private EditorEntitySerializer() {}

    public static JsonArray create(CommonPlatform commonPlatform, List<IPunishment> values) {
        HashSet values2 = new HashSet();
        Iterator<IPunishment> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            IPunishment punishment;
            if ((punishment = (IPunishment) iterator2.next()) != null) {
                applySet(
                        values2,
                        punishment.getTarget(),
                        punishment.getIssuedBy(),
                        punishment.getRemovedBy());
            }
        }
        Map entries = findMap(commonPlatform, values2);
        JsonArray jsonArray = new JsonArray();
        Iterator<IPunishment> iterator3 = values.iterator();
        while (iterator3.hasNext()) {
            IPunishment punishment2;
            if ((punishment2 = (IPunishment) iterator3.next()) != null) {
                jsonArray.add(
                        commonPlatform
                                .getGson()
                                .toJsonTree(
                                        new PunishmentSummary(
                                                punishment2.getUuid(),
                                                punishment2.getPunishmentID(),
                                                punishment2.getPunishmentType() == null
                                                        ? null
                                                        : punishment2.getPunishmentType().name(),
                                                punishment2.getTarget(),
                                                (String) entries.get(punishment2.getTarget()),
                                                punishment2.getIssuedBy(),
                                                (String) entries.get(punishment2.getIssuedBy()),
                                                punishment2.getReason(),
                                                punishment2.getIssuedAt(),
                                                punishment2.getIssuedOn(),
                                                punishment2.isPermanent(),
                                                punishment2.getDuration(),
                                                punishment2.isActive(),
                                                punishment2.isVoided(),
                                                punishment2.isSilent(),
                                                punishment2.isIp(),
                                                punishment2.getRemovedBy(),
                                                (String) entries.get(punishment2.getRemovedBy()),
                                                punishment2.getRemovedAt(),
                                                punishment2.getRemovedReason())));
            }
        }
        return jsonArray;
    }

    public static JsonArray createByJsonArray(CommonPlatform commonPlatform, List<IGrant> values) {
        HashSet values2 = new HashSet();
        Iterator<IGrant> iterator2 = values.iterator();
        while (iterator2.hasNext()) {
            IGrant grant;
            if ((grant = (IGrant) iterator2.next()) != null) {
                applySet(values2, grant.getIssuedBy(), grant.getRemovedBy());
            }
        }
        Map entries = findMap(commonPlatform, values2);
        JsonArray jsonArray = new JsonArray();
        Iterator<IGrant> iterator3 = values.iterator();
        while (iterator3.hasNext()) {
            IGrant grant2;
            if ((grant2 = (IGrant) iterator3.next()) != null) {
                IRank rank =
                        grant2.getRankId() == null
                                ? null
                                : commonPlatform.getRankHandler().getRank(grant2.getRankId());
                ArrayList<String> values3 = new ArrayList<>();
                if (grant2.getScopes() != null) {
                    Iterator<IScope> iterator4 = grant2.getScopes().iterator();
                    while (iterator4.hasNext()) {
                        IScope scope;
                        if ((scope = (IScope) iterator4.next()) != null) {
                            values3.add(scope.isGlobal() ? "Global" : scope.getDisplayName());
                        }
                    }
                }
                jsonArray.add(
                        commonPlatform
                                .getGson()
                                .toJsonTree(
                                        new GrantSummary(
                                                grant2.getUuid(),
                                                grant2.getRankId(),
                                                rank == null ? "(deleted rank)" : rank.getName(),
                                                rank == null ? null : rank.getColor(),
                                                values3,
                                                grant2.getIssuedBy(),
                                                (String) entries.get(grant2.getIssuedBy()),
                                                grant2.getIssuedAt(),
                                                grant2.getReason(),
                                                grant2.isActive(),
                                                grant2.isPermanent(),
                                                grant2.getDuration(),
                                                grant2.getRemovedBy(),
                                                (String) entries.get(grant2.getRemovedBy()),
                                                grant2.getRemovedAt(),
                                                grant2.getRemovedReason())));
            }
        }
        return jsonArray;
    }

    private static void applySet(Set<UUID> values, UUID... uUID) {
        UUID[] uUIDs = uUID;
        int value = uUID.length;
        for (int index = 0; index < value; index++) {
            UUID uUID2;
            if ((uUID2 = uUIDs[index]) != null) {
                values.add(uUID2);
            }
        }
    }

    private static Map<UUID, String> findMap(CommonPlatform commonPlatform, Set<UUID> values) {
        HashMap<UUID, String> entries = new HashMap<>();
        if (values.isEmpty()) {
            return entries;
        }
        Iterator<IProfile> iterator2 =
                commonPlatform.getProfileHandler().getProfiles(values).iterator();
        while (iterator2.hasNext()) {
            IProfile profile;
            if ((profile = (IProfile) iterator2.next()) != null) {
                entries.put(profile.getUniqueId(), profile.getName());
            }
        }
        return entries;
    }
}
