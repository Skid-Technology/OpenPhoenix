package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.phoenix.profile.tag.ICustomTag;
import xyz.refinedev.phoenix.utils.repository.IListRepository;

import java.util.List;
import java.util.UUID;

public interface ICustomTagHandler {

    List<ICustomTag> getUnconfirmedTags();

    List<ICustomTag> getTags(UUID uUID);

    @NotNull
    List<ICustomTag> getSortedTags(UUID uUID);

    @NotNull
    List<ICustomTag> getSortedTagsReversed(UUID uUID);

    void updateTag(ICustomTag customTag);

    void deleteTag(ICustomTag customTag);

    IListRepository<String, ICustomTag> getRepository();
}
