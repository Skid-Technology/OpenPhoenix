package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.profile.filter.IFilter;
import xyz.refinedev.phoenix.repository.IFilterRepository;

public interface IFilterHandler {

    @Nullable
    IFilter getFilterFromMessage(String text);

    void updateFilter(IFilter filter);

    void removeFilter(IFilter filter);

    @Nullable
    IFilter getFilterByName(String text);

    boolean isFiltered(String text);

    @NotNull
    String sanitize(String text);

    IFilterRepository getRepository();
}
