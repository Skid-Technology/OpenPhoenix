package xyz.refinedev.phoenix.utils.auth.yubikey.api;

import java.util.Map;

public interface VerificationResponse {

    boolean isOk();

    String getH();

    String getT();

    ResponseStatus getStatus();

    String getTimestamp();

    String getSessioncounter();

    String getSessionuse();

    String getSl();

    String getOtp();

    String getNonce();

    Map<String, String> getKeyValueMap();

    String getPublicId();
}
