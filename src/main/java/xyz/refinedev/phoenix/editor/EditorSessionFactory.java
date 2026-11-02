package xyz.refinedev.phoenix.editor;

import com.google.gson.annotations.SerializedName;

import xyz.refinedev.phoenix.profile.filter.IFilter;
import xyz.refinedev.phoenix.profile.punishment.ladder.IPunishmentLadder;
import xyz.refinedev.phoenix.profile.tag.ITag;
import xyz.refinedev.phoenix.rank.IRank;

import java.util.List;

public class EditorSessionFactory {

    @SerializedName("ranks")
    private final List<IRank> ranks;

    @SerializedName("tags")
    private final List<ITag> tags;

    @SerializedName("filters")
    private final List<IFilter> filters;

    @SerializedName("punishmentLadders")
    private final List<IPunishmentLadder> punishmentLadders;

    public List<IRank> getRanks() {
        return this.ranks;
    }

    public List<ITag> getTags() {
        return this.tags;
    }

    public List<IFilter> getFilters() {
        return this.filters;
    }

    public List<IPunishmentLadder> getPunishmentLadders() {
        return this.punishmentLadders;
    }

    public EditorSessionFactory(
            List<IRank> ranks,
            List<ITag> tags,
            List<IFilter> filters,
            List<IPunishmentLadder> punishmentLadders) {
        this.ranks = ranks;
        this.tags = tags;
        this.filters = filters;
        this.punishmentLadders = punishmentLadders;
    }
}
