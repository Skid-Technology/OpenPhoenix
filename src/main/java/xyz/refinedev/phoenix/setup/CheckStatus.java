package xyz.refinedev.phoenix.setup;

public enum CheckStatus {
    OK,
    TODO,
    WARN,
    FAIL;

    public boolean isWorseThan(CheckStatus checkStatus) {
        return this.ordinal() > checkStatus.ordinal();
    }
}
