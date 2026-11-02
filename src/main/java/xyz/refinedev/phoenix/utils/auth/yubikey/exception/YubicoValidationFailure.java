package xyz.refinedev.phoenix.utils.auth.yubikey.exception;

public class YubicoValidationFailure extends Exception {

    public YubicoValidationFailure(String text) {
        super(text);
    }

    public YubicoValidationFailure(Throwable throwable) {
        super(throwable);
    }

    public YubicoValidationFailure(String text, Throwable throwable) {
        super(text, throwable);
    }
}
