package xyz.refinedev.phoenix.editor.dto;

import com.google.gson.annotations.SerializedName;

public class OperationResult {

    @SerializedName("success")
    private final boolean success;

    @SerializedName("code")
    private final String code;

    @SerializedName("message")
    private final String message;

    private OperationResult(boolean success, String code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    public static OperationResult of(String text) {
        return new OperationResult(true, null, text);
    }

    public static OperationResult of(String text, String text2) {
        return new OperationResult(false, text, text2);
    }

    public boolean isSuccess() {
        return this.success;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
