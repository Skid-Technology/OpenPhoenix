package xyz.refinedev.phoenix.editor.op;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.alt.IAltLink;
import xyz.refinedev.phoenix.alt.IAltSignal;
import xyz.refinedev.phoenix.editor.dto.AltDetails;
import xyz.refinedev.phoenix.editor.dto.AltSummary;
import xyz.refinedev.phoenix.editor.dto.ProfileDetails;
import xyz.refinedev.phoenix.editor.dto.ProfileSummary;
import xyz.refinedev.phoenix.handler.IPunishmentHandler;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.punishment.PunishmentType;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.server.IServerData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class AltOperations {

    private final CommonPlatform platform;

    public AltOperations(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }

    public EditorOpHandler getEditorOpHandler() {
        return (result, result2) -> {
            String text = EditorPayload.formatString(result2, "query").trim();
            JsonArray jsonArray = new JsonArray();
            for (IProfile profile : this.findCollection(text)) {
                jsonArray.add(this.platform.getGson().toJsonTree(this.create(profile)));
            }
            JsonObject jsonObject;
            (jsonObject = new JsonObject()).add("results", jsonArray);
            return jsonObject;
        };
    }

    private Collection<IProfile> findCollection(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        UUID uUID;
        if ((uUID = this.fromString(text)) != null) {
            IProfile profile;
            return (profile = this.platform.getProfileHandler().getProfile(uUID)) == null
                    ? Collections.emptyList()
                    : Collections.singletonList(profile);
        }
        String text2 = text.toLowerCase();
        LinkedHashMap entries = new LinkedHashMap();
        IProfile profile2;
        if ((profile2 = this.platform.getProfileHandler().getProfileFromName(text2)) != null) {
            entries.put(profile2.getUniqueId(), profile2);
        }
        List<IProfile> values2;
        if ((values2 = this.platform.getProfileHandler().getProfilesFromName(text2)) != null) {
            Iterator iterator2 = values2.iterator();
            while (iterator2.hasNext()) {
                IProfile profile3;
                if ((profile3 = (IProfile) iterator2.next()) != null) {
                    entries.put(profile3.getUniqueId(), profile3);
                }
            }
        }
        return entries.values();
    }

    private UUID fromString(String text) {
        try {
            return UUID.fromString(text);
        } catch (IllegalArgumentException illegalArgumentException) {
            return null;
        }
    }

    public EditorOpHandler getEditorOpHandlerByEditorOpHandler() {
        return (result, result2) -> {
            UUID uUID = EditorPayload.findUUID(result2, "uuid");
            JsonArray jsonArray = new JsonArray();
            List<IAltLink> values;
            for (IAltLink altLink : values = this.findList(uUID)) {
                if (jsonArray.size() >= 100) {
                    break;
                }
                IProfile profile;
                if ((profile = this.platform.getProfileHandler().getProfile(altLink.other(uUID)))
                        != null) {
                    jsonArray.add(
                            this.platform
                                    .getGson()
                                    .toJsonTree(
                                            new AltDetails(
                                                    this.create(profile, altLink),
                                                    this.findList(altLink),
                                                    altLink.getLastEvaluated())));
                }
            }
            JsonObject jsonObject;
            (jsonObject = new JsonObject()).add("links", jsonArray);
            jsonObject.addProperty("truncated", values.size() > 100);
            return jsonObject;
        };
    }

    private List<AltDetails.Entry> findList(IAltLink altLink) {
        ArrayList<AltDetails.Entry> values = new ArrayList<>();
        for (IAltSignal altSignal : altLink.getSignals()) {
            values.add(
                    new AltDetails.Entry(
                            altSignal.getName(), altSignal.getPoints(), altSignal.getDetail()));
        }
        return values;
    }

    public EditorOpHandler getEditorOpHandler2() {
        return (result, result2) -> {
            UUID uUID = EditorPayload.findUUID(result2, "uuid");
            IProfile profile;
            if ((profile = this.platform.getProfileHandler().getProfile(uUID)) == null) {
                throw new EditorOperationException("NO_SUCH_PROFILE", "no profile for " + uUID);
            }
            List<xyz.refinedev.phoenix.profile.login.ILogin> logins =
                    this.platform.getLoginHandler().getLogins(uUID);
            List<IAltLink> values = this.findList(uUID);
            ProfileDetails profileDetails =
                    new ProfileDetails(
                            this.create(profile),
                            EditorEntitySerializer.createByJsonArray(
                                    this.platform, profile.getSortedGrantsReversed()),
                            EditorEntitySerializer.create(this.platform, profile.getPunishments()),
                            profile.getNotes(),
                            this.findList(uUID, values),
                            values.size() > 100,
                            logins,
                            profile.getFirstLogin(logins),
                            profile.getLastLogin(logins));
            return this.platform.getGson().toJsonTree(profileDetails);
        };
    }

    private List<IAltLink> findList(UUID uUID) {
        ArrayList<IAltLink> values;
        (values = new ArrayList<>(this.platform.getAltHandler().getLinks(uUID)))
                .sort(Comparator.comparingInt(IAltLink::getScore).reversed());
        return values;
    }

    private List<AltSummary> findList(UUID uUID, List<IAltLink> values) {
        ArrayList<AltSummary> values2 = new ArrayList<>();
        for (IAltLink altLink : values) {
            if (values2.size() >= 100) {
                break;
            }
            IProfile profile;
            if ((profile = this.platform.getProfileHandler().getProfile(altLink.other(uUID)))
                    != null) {
                values2.add(this.create(profile, altLink));
            }
        }
        return values2;
    }

    private AltSummary create(IProfile profile, IAltLink altLink) {
        ProfileSummary profileSummary = this.create(profile);
        IPunishmentHandler punishmentHandler = this.platform.getPunishmentHandler();
        return new AltSummary(
                profileSummary.getUuid(),
                profileSummary.getName(),
                profileSummary.getRankName(),
                profileSummary.getRankColor(),
                profileSummary.isOnline(),
                profileSummary.getServer(),
                altLink.getStatus(),
                altLink.getScore(),
                altLink.getSharedAddresses(),
                altLink.getIndependentSubnets(),
                altLink.getFirstLinked(),
                altLink.getLastSharedLogin(),
                punishmentHandler.getActivePunishment(profile.getUniqueId(), PunishmentType.BAN)
                        != null,
                punishmentHandler.getActivePunishment(profile.getUniqueId(), PunishmentType.MUTE)
                        != null);
    }

    private ProfileSummary create(IProfile profile) {
        IRank highestRank = profile.getHighestRank();
        IServerData presence = this.platform.getNetworkHandler().getPresence(profile.getUniqueId());
        return new ProfileSummary(
                profile.getUniqueId(),
                profile.getName(),
                highestRank == null ? "" : highestRank.getName(),
                highestRank == null ? "" : highestRank.getColor(),
                presence != null,
                presence == null ? "" : presence.getServerName());
    }
}
