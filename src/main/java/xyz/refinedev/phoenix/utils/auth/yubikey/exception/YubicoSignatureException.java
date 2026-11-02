package xyz.refinedev.phoenix.utils.auth.yubikey.exception;

public class YubicoSignatureException extends Exception {

    public YubicoSignatureException(String text, Throwable throwable) {
        super(text, throwable);
    }
}
