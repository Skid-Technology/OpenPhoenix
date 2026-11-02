package xyz.refinedev.phoenix.placeholder;

import java.util.UUID;

public interface PlaceholderAdapter {

    String getIdentifier();

    String getPlaceholder(UUID uUID, String text);
}
