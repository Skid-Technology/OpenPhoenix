package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.packet.impl.tags.TagCategoryUpdatePacket;
import xyz.refinedev.phoenix.packet.impl.tags.TagUpdatePacket;
import xyz.refinedev.phoenix.profile.tag.ITag;
import xyz.refinedev.phoenix.profile.tag.ITagCategory;
import xyz.refinedev.phoenix.profile.tag.TagCategory;
import xyz.refinedev.phoenix.repository.ITagCategoryRepository;
import xyz.refinedev.phoenix.repository.ITagRepository;
import xyz.refinedev.phoenix.utils.repository.RepositoryFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class TagHandler implements ITagHandler {

    private final CommonPlatform platform;

    private final ITagRepository tagRepository;

    private final ITagCategoryRepository categoryRepository;

    public TagHandler(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
        RepositoryFactory repositoryFactory = new RepositoryFactory(commonPlatform);
        this.tagRepository = repositoryFactory.tags();
        this.tagRepository
                .fetchAll()
                .whenCompleteAsync(
                        (result, result2) ->
                                this.tagRepository
                                        .getCache()
                                        .addAll((Collection<? extends ITag>) result));
        this.categoryRepository = repositoryFactory.tagCategories();
        this.categoryRepository
                .fetchAll()
                .whenCompleteAsync(
                        (result, result2) -> {
                            this.categoryRepository
                                    .getCache()
                                    .addAll((Collection<? extends ITagCategory>) result);
                            if (!this.isDefaultCategory() && this.getTagCategory("All") == null) {
                                TagCategory tagCategory;
                                (tagCategory = new TagCategory("All")).setDef(true);
                                this.categoryRepository.addToCache(tagCategory);
                            }
                        });
    }

    @NotNull
    @Override
    public List<ITagCategory> getSortedCategories() {
        ArrayList<ITagCategory> values;
        (values = new ArrayList<>(this.categoryRepository.getCache()))
                .sort(Comparator.comparingInt(ITagCategory::getPriority));
        return values;
    }

    @NotNull
    @Override
    public List<ITagCategory> getSortedCategoriesReversed() {
        ArrayList<ITagCategory> values;
        (values = new ArrayList<>(this.categoryRepository.getCache()))
                .sort(Comparator.comparingInt(ITagCategory::getPriority).reversed());
        return values;
    }

    @NotNull
    @Override
    public List<ITag> getSortedTags() {
        ArrayList<ITag> values;
        (values = new ArrayList<>(this.tagRepository.getCache()))
                .sort(Comparator.comparingInt(ITag::getPriority));
        return values;
    }

    @NotNull
    @Override
    public List<ITag> getSortedTagsReversed() {
        ArrayList<ITag> values;
        (values = new ArrayList<>(this.tagRepository.getCache()))
                .sort(Comparator.comparingInt(ITag::getPriority).reversed());
        return values;
    }

    @Nullable
    @Override
    public ITag getTag(String text) {
        return this.tagRepository.getFromCache(text).orElse(null);
    }

    @Nullable
    @Override
    public ITagCategory getTagCategory(String text) {
        return this.categoryRepository.getFromCache(text).orElse(null);
    }

    @Override
    public void updateTag(ITag tag) {
        this.tagRepository.save(tag);
        this.platform.getPacketHandler().send(new TagUpdatePacket(tag, false));
    }

    @Override
    public void updateCategory(ITagCategory tagCategory) {
        this.categoryRepository.save(tagCategory);
        this.platform.getPacketHandler().send(new TagCategoryUpdatePacket(tagCategory, false));
    }

    @Override
    public void deleteTag(ITag tag) {
        this.tagRepository.removeFromCache(tag);
        this.tagRepository.delete(tag);
        this.platform.getPacketHandler().send(new TagUpdatePacket(tag, true));
    }

    @Override
    public void deleteCategory(ITagCategory tagCategory) {
        this.categoryRepository.removeFromCache(tagCategory);
        this.categoryRepository.delete(tagCategory);
        this.platform.getPacketHandler().send(new TagCategoryUpdatePacket(tagCategory, true));
    }

    @Nullable
    @Override
    public ITagCategory getDefaultCategory() {
        Iterator<ITagCategory> iterator2 = this.categoryRepository.getCache().iterator();
        while (iterator2.hasNext()) {
            ITagCategory tagCategory;
            if ((tagCategory = (ITagCategory) iterator2.next()).isDef()) {
                return tagCategory;
            }
        }
        return null;
    }

    @Override
    public boolean isDefaultCategory() {
        Iterator<ITagCategory> iterator2 = this.categoryRepository.getCache().iterator();
        while (iterator2.hasNext()) {
            if (((ITagCategory) iterator2.next()).isDef()) {
                return true;
            }
        }
        return false;
    }

    public CommonPlatform getPlatform() {
        return this.platform;
    }

    @Override
    public ITagRepository getTagRepository() {
        return this.tagRepository;
    }

    @Override
    public ITagCategoryRepository getCategoryRepository() {
        return this.categoryRepository;
    }
}
