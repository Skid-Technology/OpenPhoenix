package xyz.refinedev.phoenix.profile.punishment;

public enum ProofType {
    LINK("Link"),
    CHAT_SNAPSHOT("Chat Snapshot");

    private final String niceName;

    public static ProofType getByName(String text) {
        return !text.equalsIgnoreCase("snapshot")
                        && !text.equalsIgnoreCase("chatsnapshot")
                        && !text.equalsIgnoreCase("chat")
                ? LINK
                : CHAT_SNAPSHOT;
    }

    public String getNiceName() {
        return this.niceName;
    }

    ProofType(String niceName) {
        this.niceName = niceName;
    }
}
