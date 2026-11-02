package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.placeholder.PlaceholderAdapter;

import java.util.Map;
import java.util.UUID;

public interface IPlaceholderHandler {

    void addAdapter(PlaceholderAdapter placeholderAdapter);

    @Nullable
    PlaceholderAdapter getAdapter(String text);

    String replace(UUID uUID, String text);

    Map<String, PlaceholderAdapter> getAdapters();
}
