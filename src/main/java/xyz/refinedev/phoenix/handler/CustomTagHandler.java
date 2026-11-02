package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.profile.tag.ICustomTag;
import xyz.refinedev.phoenix.utils.repository.IListRepository;
import xyz.refinedev.phoenix.utils.repository.RepositoryFactory;
import xyz.refinedev.phoenix.utils.repository.filters.api.Filters;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class CustomTagHandler implements ICustomTagHandler {

    private final IListRepository<String, ICustomTag> repository;

    public CustomTagHandler(CommonPlatform commonPlatform) {
        this.repository = new RepositoryFactory(commonPlatform).customTags();
    }

    @Override
    public List<ICustomTag> getUnconfirmedTags() {
        return this.repository.fetchAllSync(Filters.eq("status", "UNDECIDED"));
    }

    @Override
    public List<ICustomTag> getTags(UUID uUID) {
        return this.repository.fetchAllSync(Filters.eq("creator", uUID.toString()));
    }

    @NotNull
    @Override
    public List<ICustomTag> getSortedTags(UUID uUID) {
        List<ICustomTag> values;
        (values = this.repository.fetchAllSync(Filters.eq("creator", uUID.toString())))
                .sort(Comparator.comparingLong(ICustomTag::getCreatedOn));
        return values;
    }

    @NotNull
    @Override
    public List<ICustomTag> getSortedTagsReversed(UUID uUID) {
        List<ICustomTag> values;
        (values = this.repository.fetchAllSync(Filters.eq("creator", uUID.toString())))
                .sort(Comparator.comparingLong(ICustomTag::getCreatedOn).reversed());
        return values;
    }

    @Override
    public void updateTag(ICustomTag customTag) {
        this.repository.save(customTag);
    }

    @Override
    public void deleteTag(ICustomTag customTag) {
        this.repository.delete(customTag);
    }

    @Override
    public IListRepository<String, ICustomTag> getRepository() {
        return this.repository;
    }
}
