package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.profile.tag.ITag;
import xyz.refinedev.phoenix.profile.tag.ITagCategory;
import xyz.refinedev.phoenix.repository.ITagCategoryRepository;
import xyz.refinedev.phoenix.repository.ITagRepository;

import java.util.List;

public interface ITagHandler {

    @NotNull
    List<ITagCategory> getSortedCategories();

    @NotNull
    List<ITagCategory> getSortedCategoriesReversed();

    @NotNull
    List<ITag> getSortedTags();

    @NotNull
    List<ITag> getSortedTagsReversed();

    @Nullable
    ITag getTag(String text);

    @Nullable
    ITagCategory getTagCategory(String text);

    void updateTag(ITag tag);

    void updateCategory(ITagCategory tagCategory);

    void deleteTag(ITag tag);

    void deleteCategory(ITagCategory tagCategory);

    @Nullable
    ITagCategory getDefaultCategory();

    boolean isDefaultCategory();

    ITagCategoryRepository getCategoryRepository();

    ITagRepository getTagRepository();
}
