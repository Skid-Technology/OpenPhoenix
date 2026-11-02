package xyz.refinedev.phoenix.utils.auth.yubikey.exception;

public class YubicoVerificationException extends Exception {

    public YubicoVerificationException(String text, Throwable throwable) {
        super(text, throwable);
    }

    public YubicoVerificationException(String text) {
        super(text);
    }
}
