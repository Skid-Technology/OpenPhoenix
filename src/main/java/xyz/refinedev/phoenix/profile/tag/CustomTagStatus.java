package xyz.refinedev.phoenix.profile.tag;

public enum CustomTagStatus {
    ACCEPTED("Accepted"),
    DENIED("Denied"),
    UNDECIDED("Undecided");

    private final String displayName;

    public String getDisplayName() {
        return this.displayName;
    }

    CustomTagStatus(String displayName) {
        this.displayName = displayName;
    }
}
