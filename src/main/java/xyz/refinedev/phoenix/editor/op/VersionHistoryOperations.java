package xyz.refinedev.phoenix.editor.op;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.editor.EditorChangePreview;
import xyz.refinedev.phoenix.editor.panel.PanelBroadcast;
import xyz.refinedev.phoenix.editor.panel.PanelPermissions;
import xyz.refinedev.phoenix.editor.panel.PanelUrl;
import xyz.refinedev.phoenix.editor.protocol.EditorOp;
import xyz.refinedev.phoenix.packet.api.EntityUpdatePacket;
import xyz.refinedev.phoenix.packet.impl.punishments.PunishmentLadderUpdatePacket;
import xyz.refinedev.phoenix.packet.impl.ranks.RankUpdatePacket;
import xyz.refinedev.phoenix.packet.impl.report.ReportCategoryUpdatePacket;
import xyz.refinedev.phoenix.packet.impl.server.FilterUpdatePacket;
import xyz.refinedev.phoenix.packet.impl.tags.CustomTagUpdatePacket;
import xyz.refinedev.phoenix.packet.impl.tags.TagUpdatePacket;
import xyz.refinedev.phoenix.profile.filter.Filter;
import xyz.refinedev.phoenix.profile.filter.IFilter;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadder;
import xyz.refinedev.phoenix.profile.punishment.ladder.PunishmentLadder;
import xyz.refinedev.phoenix.profile.tag.CustomTag;
import xyz.refinedev.phoenix.profile.tag.ICustomTag;
import xyz.refinedev.phoenix.profile.tag.ITag;
import xyz.refinedev.phoenix.profile.tag.Tag;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.rank.Rank;
import xyz.refinedev.phoenix.report.IReportCategory;
import xyz.refinedev.phoenix.report.ReportCategory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class VersionHistoryOperations {

    private final CommonPlatform platform;

    private static final String text = System.lineSeparator();

    public VersionHistoryOperations(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }

    public EditorOpHandler getEditorOpHandler() {
        return (result, result2) -> {
            String text = EditorPayload.formatString(result2, "kind");
            JsonArray jsonArray = new JsonArray();
            switch (text) {
                case "rank":
                    for (IRank rank : this.platform.getRankHandler().getRepository().getCache()) {
                        JsonObject jsonObject;
                        (jsonObject = new JsonObject())
                                .addProperty("id", String.valueOf(rank.getUuid()));
                        jsonObject.addProperty("name", rank.getName());
                        jsonObject.addProperty("displayName", rank.getDisplayName());
                        jsonObject.addProperty("color", rank.getColor());
                        jsonObject.addProperty("prefix", rank.getPrefix());
                        jsonObject.addProperty("priority", rank.getPriority());
                        jsonObject.addProperty("staff", rank.isStaff());
                        jsonObject.addProperty("defaultRank", rank.isDefaultRank());
                        jsonObject.addProperty(
                                "permissions",
                                rank.getPermissions() == null ? 0 : rank.getPermissions().size());
                        jsonObject.addProperty(
                                "inheritance",
                                rank.getInheritance() == null ? 0 : rank.getInheritance().size());
                        jsonArray.add(jsonObject);
                    }
                    break;
                case "tag":
                    for (ITag tag : this.platform.getTagHandler().getTagRepository().getCache()) {
                        JsonObject jsonObject2;
                        (jsonObject2 = new JsonObject()).addProperty("id", tag.getName());
                        jsonObject2.addProperty("name", tag.getName());
                        jsonObject2.addProperty("displayName", tag.getDisplayName());
                        jsonObject2.addProperty("prefix", tag.getPrefix());
                        jsonObject2.addProperty("color", tag.getColor());
                        jsonObject2.addProperty("priority", tag.getPriority());
                        jsonObject2.addProperty("purchasable", tag.isPurchasable());
                        jsonArray.add(jsonObject2);
                    }
                    break;
                case "filter":
                    for (IFilter filter :
                            this.platform.getFilterHandler().getRepository().getCache()) {
                        JsonObject jsonObject3;
                        (jsonObject3 = new JsonObject()).addProperty("id", filter.getName());
                        jsonObject3.addProperty("name", filter.getName());
                        jsonObject3.addProperty("filter", filter.getFilter());
                        jsonObject3.addProperty(
                                "filterType",
                                filter.getFilterType() == null
                                        ? null
                                        : filter.getFilterType().name());
                        jsonObject3.addProperty("hard", filter.isHard());
                        jsonObject3.addProperty("autoPunish", filter.isAutoPunish());
                        jsonObject3.addProperty(
                                "punishmentLadderId", filter.getPunishmentLadderId());
                        jsonArray.add(jsonObject3);
                    }
                    break;
                case "ladder":
                    for (IPunishmentLadder punishmentLadder :
                            this.platform.getPunishmentLadderHandler().getRepository().getCache()) {
                        JsonObject jsonObject4;
                        (jsonObject4 = new JsonObject())
                                .addProperty("id", punishmentLadder.getId());
                        jsonObject4.addProperty("reason", punishmentLadder.getReason());
                        jsonObject4.addProperty("priority", punishmentLadder.getPriority());
                        jsonObject4.addProperty("hidden", punishmentLadder.isHidden());
                        jsonObject4.addProperty(
                                "requireChatSnapshot", punishmentLadder.isRequireChatSnapshot());
                        jsonObject4.addProperty(
                                "steps",
                                punishmentLadder.getLadder() == null
                                        ? 0
                                        : punishmentLadder.getLadder().size());
                        jsonArray.add(jsonObject4);
                    }
                    break;
                case "category":
                    for (IReportCategory reportCategory :
                            this.platform.getReportCategoryHandler().getRepository().getCache()) {
                        JsonObject jsonObject5;
                        (jsonObject5 = new JsonObject()).addProperty("id", reportCategory.getId());
                        jsonObject5.addProperty("name", reportCategory.getId());
                        jsonObject5.addProperty("displayName", reportCategory.getDisplayName());
                        jsonObject5.addProperty(
                                "punishmentLadderId", reportCategory.getPunishmentLadderId());
                        jsonObject5.addProperty(
                                "lines",
                                reportCategory.getDescription() == null
                                        ? 0
                                        : reportCategory.getDescription().size());
                        jsonArray.add(jsonObject5);
                    }
                    break;
                case "customtag":
                    for (ICustomTag customTag :
                            this.platform.getCustomTagHandler().getRepository().getCache()) {
                        JsonObject jsonObject6;
                        (jsonObject6 = new JsonObject()).addProperty("id", customTag.getName());
                        jsonObject6.addProperty("name", customTag.getName());
                        jsonObject6.addProperty("prefix", customTag.getPrefix());
                        jsonObject6.addProperty("color", customTag.getColor());
                        jsonObject6.addProperty(
                                "status",
                                customTag.getStatus() == null
                                        ? null
                                        : customTag.getStatus().name());
                        jsonArray.add(jsonObject6);
                    }
                    break;
                default:
                    throw new EditorOperationException("BAD_REQUEST", "unknown kind " + text);
            }
            JsonObject jsonObject7;
            (jsonObject7 = new JsonObject()).addProperty("kind", text);
            jsonObject7.add("rows", jsonArray);
            return jsonObject7;
        };
    }

    public EditorOpHandler getEditorOpHandlerByEditorOpHandler() {
        return (result, result2) -> {
            String text = EditorPayload.formatString(result2, "kind");
            String text2 = EditorPayload.formatString(result2, "id");
            Object result3;
            if ((result3 = this.findObject(text, text2)) == null) {
                throw new EditorOperationException("NO_SUCH_ENTITY", "no " + text + " " + text2);
            }
            JsonObject jsonObject;
            (jsonObject = new JsonObject()).addProperty("kind", text);
            jsonObject.add("entity", this.platform.getGson().toJsonTree(result3));
            return jsonObject;
        };
    }

    public EditorOpHandler getEditorOpHandler2() {
        return (result, result2) -> this.create(result2, true);
    }

    private JsonObject create(JsonObject jsonObject, boolean matches) {
        String text = EditorPayload.formatString(jsonObject, "kind");
        JsonElement jsonElement;
        if ((jsonElement = jsonObject.get("entity")) != null && jsonElement.isJsonObject()) {
            JsonObject asJsonObject = jsonElement.getAsJsonObject();
            Object result = this.findObject(text, asJsonObject);
            Object result2;
            EntityUpdatePacket entityUpdatePacket;
            switch (text) {
                case "rank":
                    IRank rank = (IRank) this.platform.getGson().fromJson(asJsonObject, Rank.class);
                    this.platform.getRankHandler().getRepository().saveSync(rank);
                    this.platform.getRankHandler().getRepository().addToCache(rank);
                    result2 = rank;
                    entityUpdatePacket = new RankUpdatePacket(rank, false);
                    break;
                case "tag":
                    ITag tag = (ITag) this.platform.getGson().fromJson(asJsonObject, Tag.class);
                    this.platform.getTagHandler().getTagRepository().saveSync(tag);
                    this.platform.getTagHandler().getTagRepository().addToCache(tag);
                    result2 = tag;
                    entityUpdatePacket = new TagUpdatePacket(tag, false);
                    break;
                case "filter":
                    IFilter filter =
                            (IFilter) this.platform.getGson().fromJson(asJsonObject, Filter.class);
                    this.platform.getFilterHandler().getRepository().saveSync(filter);
                    this.platform.getFilterHandler().getRepository().addToCache(filter);
                    result2 = filter;
                    entityUpdatePacket = new FilterUpdatePacket(filter, false);
                    break;
                case "ladder":
                    IPunishmentLadder punishmentLadder =
                            (IPunishmentLadder)
                                    this.platform
                                            .getGson()
                                            .fromJson(asJsonObject, PunishmentLadder.class);
                    this.platform
                            .getPunishmentLadderHandler()
                            .getRepository()
                            .saveSync(punishmentLadder);
                    this.platform
                            .getPunishmentLadderHandler()
                            .getRepository()
                            .addToCache(punishmentLadder);
                    result2 = punishmentLadder;
                    entityUpdatePacket = new PunishmentLadderUpdatePacket(punishmentLadder, false);
                    break;
                case "category":
                    IReportCategory reportCategory =
                            (IReportCategory)
                                    this.platform
                                            .getGson()
                                            .fromJson(asJsonObject, ReportCategory.class);
                    this.platform
                            .getReportCategoryHandler()
                            .getRepository()
                            .saveSync(reportCategory);
                    this.platform
                            .getReportCategoryHandler()
                            .getRepository()
                            .addToCache(reportCategory);
                    result2 = reportCategory;
                    entityUpdatePacket = new ReportCategoryUpdatePacket(reportCategory, false);
                    break;
                case "customtag":
                    ICustomTag customTag =
                            (ICustomTag)
                                    this.platform.getGson().fromJson(asJsonObject, CustomTag.class);
                    this.platform.getCustomTagHandler().getRepository().saveSync(customTag);
                    this.platform.getCustomTagHandler().getRepository().addToCache(customTag);
                    result2 = customTag;
                    entityUpdatePacket = new CustomTagUpdatePacket(customTag, false);
                    break;
                default:
                    throw new EditorOperationException("BAD_REQUEST", "unknown kind " + text);
            }
            this.platform.getPacketHandler().sendSync(entityUpdatePacket);
            String text2 = EditorPayload.formatString(jsonObject, "actorName", "somebody");
            if (matches && result != null) {
                PanelUrl.applyString(
                        text,
                        this.formatString(result2),
                        text2,
                        this.platform.getGson().toJsonTree(result).getAsJsonObject());
            }
            this.applyString(text, text2, result, result2);
            JsonObject jsonObject2;
            (jsonObject2 = new JsonObject()).addProperty("saved", true);
            jsonObject2.add("entity", this.platform.getGson().toJsonTree(result2));
            return jsonObject2;
        } else {
            throw new EditorOperationException("BAD_REQUEST", "missing field entity");
        }
    }

    public EditorOpHandler getEditorOpHandler3() {
        return (result, result2) -> {
            String text = EditorPayload.formatString(result2, "kind");
            String text2 = EditorPayload.formatString(result2, "id");
            Object result3;
            if ((result3 = this.findObject(text, text2)) != null) {
                EntityUpdatePacket entityUpdatePacket;
                switch (text) {
                    case "rank":
                        IRank rank = (IRank) result3;
                        this.platform.getRankHandler().getRepository().deleteSync(rank);
                        this.platform.getRankHandler().getRepository().removeFromCache(rank);
                        entityUpdatePacket = new RankUpdatePacket(rank, true);
                        break;
                    case "tag":
                        ITag tag = (ITag) result3;
                        this.platform.getTagHandler().getTagRepository().deleteSync(tag);
                        this.platform.getTagHandler().getTagRepository().removeFromCache(tag);
                        entityUpdatePacket = new TagUpdatePacket(tag, true);
                        break;
                    case "filter":
                        IFilter filter = (IFilter) result3;
                        this.platform.getFilterHandler().getRepository().deleteSync(filter);
                        this.platform.getFilterHandler().getRepository().removeFromCache(filter);
                        entityUpdatePacket = new FilterUpdatePacket(filter, true);
                        break;
                    case "category":
                        IReportCategory reportCategory = (IReportCategory) result3;
                        this.platform
                                .getReportCategoryHandler()
                                .getRepository()
                                .deleteSync(reportCategory);
                        this.platform
                                .getReportCategoryHandler()
                                .getRepository()
                                .removeFromCache(reportCategory);
                        entityUpdatePacket = new ReportCategoryUpdatePacket(reportCategory, true);
                        break;
                    case "customtag":
                        ICustomTag customTag = (ICustomTag) result3;
                        this.platform.getCustomTagHandler().getRepository().deleteSync(customTag);
                        this.platform
                                .getCustomTagHandler()
                                .getRepository()
                                .removeFromCache(customTag);
                        entityUpdatePacket = new CustomTagUpdatePacket(customTag, true);
                        break;
                    default:
                        IPunishmentLadder punishmentLadder = (IPunishmentLadder) result3;
                        this.platform
                                .getPunishmentLadderHandler()
                                .getRepository()
                                .deleteSync(punishmentLadder);
                        this.platform
                                .getPunishmentLadderHandler()
                                .getRepository()
                                .removeFromCache(punishmentLadder);
                        entityUpdatePacket =
                                new PunishmentLadderUpdatePacket(punishmentLadder, true);
                }
                this.platform.getPacketHandler().sendSync(entityUpdatePacket);
                PanelBroadcast.applyString(
                        "&7[&bPanel&7] &f"
                                + EditorPayload.formatString(result2, "actorName", "somebody")
                                + " &7deleted "
                                + text
                                + " &f"
                                + text2);
            }
            JsonObject jsonObject;
            (jsonObject = new JsonObject()).addProperty("deleted", result3 != null);
            return jsonObject;
        };
    }

    private Object findObject(String text, JsonObject jsonObject) {
        String[] texts;
        int value = (texts = new String[] {"_id", "name"}).length;
        for (int index = 0; index < value; index++) {
            String text2 = texts[index];
            Object result;
            if (jsonObject.has(text2)
                    && jsonObject.get(text2).isJsonPrimitive()
                    && (result = this.findObject(text, jsonObject.get(text2).getAsString()))
                            != null) {
                return this.platform
                        .getGson()
                        .fromJson(this.platform.getGson().toJsonTree(result), result.getClass());
            }
        }
        return null;
    }

    private void applyString(String text2, String text3, Object result, Object result2) {
        String text4 = this.formatString(result2);
        if (result == null) {
            PanelBroadcast.applyString(
                    "&7[&bPanel&7] &f" + text3 + " &7created " + text2 + " &f" + text4);
        } else {
            Gson gson = this.platform.getGson();
            List values = Collections.singletonList(result);
            List values2 = Collections.singletonList(result2);
            List<String> values3;
            if (!(values3 = EditorChangePreview.findList(gson, values, values2, result3 -> text4))
                    .isEmpty()) {
                StringBuilder stringBuilder =
                        new StringBuilder(
                                "&7[&bPanel&7] &f" + text3 + " &7edited " + text2 + " &f" + text4);
                for (String text5 : values3) {
                    stringBuilder.append(text).append(text5);
                }
                PanelBroadcast.applyString(stringBuilder.toString());
            }
        }
    }

    private String formatString(Object result) {
        if (result instanceof IRank) {
            return ((IRank) result).getName();
        } else if (result instanceof ITag) {
            return ((ITag) result).getName();
        } else if (result instanceof IFilter) {
            return ((IFilter) result).getName();
        } else if (result instanceof IPunishmentLadder) {
            return ((IPunishmentLadder) result).getId();
        } else if (result instanceof IReportCategory) {
            return ((IReportCategory) result).getId();
        } else {
            return result instanceof ICustomTag ? ((ICustomTag) result).getName() : "?";
        }
    }

    private Object findObject(String text, String text2) {
        switch (text) {
            case "rank":
                for (IRank rank :
                        (Iterable<IRank>)
                                ((Iterable)
                                        new ArrayList<>(
                                                this.platform
                                                        .getRankHandler()
                                                        .getRepository()
                                                        .getCache()))) {
                    if (text2.equals(String.valueOf(rank.getUuid()))) {
                        return rank;
                    }
                }
                return null;
            case "tag":
                for (ITag tag :
                        (Iterable<ITag>)
                                ((Iterable)
                                        new ArrayList<>(
                                                this.platform
                                                        .getTagHandler()
                                                        .getTagRepository()
                                                        .getCache()))) {
                    if (text2.equals(tag.getName())) {
                        return tag;
                    }
                }
                return null;
            case "filter":
                for (IFilter filter :
                        (Iterable<IFilter>)
                                ((Iterable)
                                        new ArrayList<>(
                                                this.platform
                                                        .getFilterHandler()
                                                        .getRepository()
                                                        .getCache()))) {
                    if (text2.equals(filter.getName())) {
                        return filter;
                    }
                }
                return null;
            case "category":
                for (IReportCategory reportCategory :
                        (Iterable<IReportCategory>)
                                ((Iterable)
                                        new ArrayList<>(
                                                this.platform
                                                        .getReportCategoryHandler()
                                                        .getRepository()
                                                        .getCache()))) {
                    if (text2.equals(reportCategory.getId())) {
                        return reportCategory;
                    }
                }
                return null;
            case "customtag":
                for (ICustomTag customTag :
                        (Iterable<ICustomTag>)
                                ((Iterable)
                                        new ArrayList<>(
                                                this.platform
                                                        .getCustomTagHandler()
                                                        .getRepository()
                                                        .getCache()))) {
                    if (text2.equals(customTag.getName())) {
                        return customTag;
                    }
                }
                return null;
            case "ladder":
                for (IPunishmentLadder punishmentLadder :
                        (Iterable<IPunishmentLadder>)
                                ((Iterable)
                                        new ArrayList<>(
                                                this.platform
                                                        .getPunishmentLadderHandler()
                                                        .getRepository()
                                                        .getCache()))) {
                    if (text2.equals(punishmentLadder.getId())) {
                        return punishmentLadder;
                    }
                }
                return null;
            default:
                throw new EditorOperationException("BAD_REQUEST", "unknown kind " + text);
        }
    }

    public EditorOpHandler getEditorOpHandler4() {
        return (result, result2) -> {
            JsonArray jsonArray = new JsonArray();
            for (PanelUrl.Entry entry : PanelUrl.getList()) {
                JsonObject jsonObject;
                (jsonObject = new JsonObject()).addProperty("id", entry.getText());
                jsonObject.addProperty("kind", entry.getTextString());
                jsonObject.addProperty("entity", entry.getText2());
                jsonObject.addProperty("actor", entry.getText3());
                jsonObject.addProperty("at", entry.getTime());
                jsonArray.add(jsonObject);
            }
            JsonObject jsonObject2;
            (jsonObject2 = new JsonObject()).add("versions", jsonArray);
            return jsonObject2;
        };
    }

    public EditorOpHandler getEditorOpHandler5() {
        return (result, result2) -> {
            String text;
            PanelUrl.Entry entry;
            if ((entry = PanelUrl.findEntry(text = EditorPayload.formatString(result2, "version")))
                    == null) {
                throw new EditorOperationException(
                        "NO_SUCH_VERSION", "that version is no longer kept");
            }
            UUID uUID = PanelPermissions.findUUID(result2);
            if (!PanelPermissions.contains(
                    this.platform, uUID, EditorOp.DEFINITION_RESTORE, entry.getTextString())) {
                throw new EditorOperationException(
                        "FORBIDDEN", "you may not put back a " + entry.getTextString());
            }
            JsonObject jsonObject;
            (jsonObject = new JsonObject()).addProperty("kind", entry.getTextString());
            jsonObject.add("entity", entry.getJsonObject());
            jsonObject.addProperty(
                    "actorName", EditorPayload.formatString(result2, "actorName", "somebody"));
            JsonObject jsonObject2 = this.create(jsonObject, false);
            PanelUrl.applyString(text);
            JsonObject jsonObject3;
            (jsonObject3 = new JsonObject()).addProperty("restored", entry.getText2());
            jsonObject3.addProperty("kind", entry.getTextString());
            jsonObject3.add("entity", jsonObject2.get("entity"));
            return jsonObject3;
        };
    }
}
