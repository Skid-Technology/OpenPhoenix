package xyz.refinedev.phoenix.utils.auth.yubikey.api;

public enum ResponseStatus {
    OK,
    BAD_OTP,
    REPLAYED_OTP,
    BAD_SIGNATURE,
    MISSING_PARAMETER,
    NO_SUCH_CLIENT,
    OPERATION_NOT_ALLOWED,
    BACKEND_ERROR,
    NOT_ENOUGH_ANSWERS,
    REPLAYED_REQUEST;

    public boolean isError() {
        return this == BACKEND_ERROR
                || this == BAD_OTP
                || this == BAD_SIGNATURE
                || this == NO_SUCH_CLIENT
                || this == MISSING_PARAMETER;
    }
}
