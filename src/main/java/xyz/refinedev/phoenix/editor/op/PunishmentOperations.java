package xyz.refinedev.phoenix.editor.op;

import com.google.gson.JsonObject;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.profile.punishment.IPunishment;
import xyz.refinedev.phoenix.profile.punishment.PunishmentType;
import xyz.refinedev.phoenix.utils.repository.filters.api.FilterOptions;
import xyz.refinedev.phoenix.utils.repository.filters.api.Filters;
import xyz.refinedev.phoenix.utils.repository.sorting.api.Sorts;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PunishmentOperations {

    private static final String text = ":";

    private final CommonPlatform platform;

    public PunishmentOperations(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }

    public EditorOpHandler getEditorOpHandler() {
        return (result, result2) -> {
            int value = this.min(result2);
            List<IPunishment> values =
                    this.platform
                            .getPunishmentHandler()
                            .getRepository()
                            .fetchPageSync(
                                    this.findFilterOptions(result2),
                                    Sorts.descending("issuedAt", "_id"),
                                    value);
            JsonObject jsonObject;
            (jsonObject = new JsonObject())
                    .add("punishments", EditorEntitySerializer.create(this.platform, values));
            jsonObject.addProperty(
                    "nextCursor",
                    values.size() < value
                            ? null
                            : this.formatString((IPunishment) values.get(values.size() - 1)));
            return jsonObject;
        };
    }

    private FilterOptions findFilterOptions(JsonObject jsonObject) {
        ArrayList<FilterOptions> values = new ArrayList<>();
        String text;
        if ((text = EditorPayload.formatString(jsonObject, "type", null)) != null) {
            values.add(Filters.eq("punishmentType", this.findPunishmentType(text).name()));
        }
        String text2;
        if ((text2 = EditorPayload.formatString(jsonObject, "target", null)) != null) {
            values.add(Filters.eq("target", this.findUUID(text2, "target").toString()));
        }
        String text3;
        if ((text3 = EditorPayload.formatString(jsonObject, "issuedBy", null)) != null) {
            values.add(Filters.eq("issuedBy", this.findUUID(text3, "issuedBy").toString()));
        }
        String text4;
        if ((text4 = EditorPayload.formatString(jsonObject, "server", null)) != null) {
            values.add(Filters.eq("issuedOn", text4));
        }
        values.addAll(this.findList(EditorPayload.formatString(jsonObject, "state", "ALL")));
        long value;
        if ((value = EditorPayload.calculateTime(jsonObject, "from", 0L)) > 0L) {
            values.add(Filters.gte("issuedAt", Filters.storedLong(value)));
        }
        long value2;
        if ((value2 = EditorPayload.calculateTime(jsonObject, "to", 0L)) > 0L) {
            values.add(Filters.lt("issuedAt", Filters.storedLong(value2)));
        }
        String text5;
        if ((text5 = EditorPayload.formatString(jsonObject, "cursor", null)) != null) {
            values.add(this.findFilterOptions(text5));
        }
        if (values.isEmpty()) {
            return Filters.exists("_id");
        } else {
            return values.size() == 1
                    ? (FilterOptions) values.get(0)
                    : Filters.and(values.toArray(new FilterOptions[0]));
        }
    }

    private List<FilterOptions> findList(String text) {
        ArrayList<FilterOptions> values = new ArrayList<>();
        switch (text.toUpperCase()) {
            case "ALL":
                return values;
            case "ACTIVE":
                values.add(Filters.eq("active", true));
                values.add(Filters.neq("voided", true));
                return values;
            case "INACTIVE":
                values.add(Filters.neq("active", true));
                return values;
            case "VOIDED":
                values.add(Filters.eq("voided", true));
                return values;
            default:
                throw new EditorOperationException("BAD_REQUEST", "unknown state " + text);
        }
    }

    private FilterOptions findFilterOptions(String text) {
        int value;
        if ((value = text.indexOf(":")) <= 0) {
            throw new EditorOperationException("BAD_REQUEST", "malformed cursor");
        }
        String text2;
        try {
            text2 = Filters.storedLong(Long.parseLong(text.substring(0, value)));
        } catch (NumberFormatException numberFormatException) {
            throw new EditorOperationException("BAD_REQUEST", "malformed cursor");
        }
        String text3 = this.findUUID(text.substring(value + 1), "cursor").toString();
        return Filters.or(
                Filters.lt("issuedAt", text2),
                Filters.and(Filters.eq("issuedAt", text2), Filters.lt("_id", text3)));
    }

    private String formatString(IPunishment punishment) {
        return punishment.getIssuedAt() + ":" + punishment.getUuid();
    }

    private int min(JsonObject jsonObject) {
        long value;
        return (value = EditorPayload.calculateTime(jsonObject, "limit", 50L)) <= 0L
                ? 50
                : (int) Math.min(value, 100L);
    }

    private PunishmentType findPunishmentType(String text) {
        PunishmentType[] punishmentTypes;
        int value = (punishmentTypes = PunishmentType.values()).length;
        for (int index = 0; index < value; index++) {
            PunishmentType punishmentType;
            if ((punishmentType = punishmentTypes[index]).name().equalsIgnoreCase(text)) {
                return punishmentType;
            }
        }
        throw new EditorOperationException("BAD_REQUEST", "unknown punishment type " + text);
    }

    private UUID findUUID(String text, String text2) {
        try {
            return UUID.fromString(text);
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new EditorOperationException("BAD_REQUEST", text2 + " was not a uuid: " + text);
        }
    }
}
