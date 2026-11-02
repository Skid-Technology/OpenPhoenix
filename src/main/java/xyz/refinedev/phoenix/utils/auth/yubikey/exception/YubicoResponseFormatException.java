package xyz.refinedev.phoenix.utils.auth.yubikey.exception;

public class YubicoResponseFormatException extends Exception {

    public YubicoResponseFormatException(String text) {
        super(text);
    }

    public YubicoResponseFormatException(Throwable throwable) {
        super(throwable);
    }

    public YubicoResponseFormatException(String text, Throwable throwable) {
        super(text, throwable);
    }
}
