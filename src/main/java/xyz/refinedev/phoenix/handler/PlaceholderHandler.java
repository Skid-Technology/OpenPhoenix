package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.placeholder.PlaceholderAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderHandler implements IPlaceholderHandler {

    private final Map<String, PlaceholderAdapter> adapters = new HashMap<>();

    private final Pattern pattern = Pattern.compile("<[a-zA-Z]*_[a-zA-Z_]*>", 2);

    @Override
    public void addAdapter(PlaceholderAdapter placeholderAdapter) {
        this.adapters.put(placeholderAdapter.getIdentifier(), placeholderAdapter);
    }

    @Nullable
    @Override
    public PlaceholderAdapter getAdapter(String text) {
        return this.adapters.get(text);
    }

    @Override
    public String replace(UUID uUID, String text) {
        Matcher matcher2 = this.pattern.matcher(text);
        PlaceholderAdapter placeholderAdapter;
        if ((placeholderAdapter = this.getAdapter("placeholderapi")) != null) {
            text = placeholderAdapter.getPlaceholder(uUID, text);
        }
        while (matcher2.find()) {
            String text2;
            String[] texts;
            if ((text2 = matcher2.group()).contains("_")
                    && (texts = text2.split("_")).length >= 2) {
                String text3 = texts[0].replace("<", "");
                String text4 = text2.substring(text3.length() + 2).replace(">", "");
                PlaceholderAdapter placeholderAdapter2;
                if ((placeholderAdapter2 = this.getAdapter(text3)) != null) {
                    text = text.replace(text2, placeholderAdapter2.getPlaceholder(uUID, text4));
                }
            }
        }
        return text;
    }

    @Override
    public Map<String, PlaceholderAdapter> getAdapters() {
        return this.adapters;
    }

    public Pattern getPattern() {
        return this.pattern;
    }
}
