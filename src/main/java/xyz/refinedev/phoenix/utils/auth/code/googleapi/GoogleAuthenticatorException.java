package xyz.refinedev.phoenix.utils.auth.code.googleapi;

public class GoogleAuthenticatorException extends RuntimeException {

    public GoogleAuthenticatorException(String text) {
        super(text);
    }

    public GoogleAuthenticatorException(String text, Throwable throwable) {
        super(text, throwable);
    }
}
