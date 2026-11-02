package xyz.refinedev.phoenix.profile.filter;

public enum FilterType {
    WORD("Word"),
    REGEX("Regex"),
    CONTAINS("Contains");

    private final String displayName;

    public String getDisplayName() {
        return this.displayName;
    }

    FilterType(String displayName) {
        this.displayName = displayName;
    }
}
