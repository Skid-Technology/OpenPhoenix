package xyz.refinedev.phoenix.utils.auth.yubikey.api;

import xyz.refinedev.lib.sl4j.Logger;
import xyz.refinedev.lib.sl4j.LoggerFactory;
import xyz.refinedev.phoenix.utils.auth.yubikey.YubicoClientImpl;
import xyz.refinedev.phoenix.utils.auth.yubikey.exception.YubicoValidationFailure;
import xyz.refinedev.phoenix.utils.auth.yubikey.exception.YubicoVerificationException;

import java.util.Base64;

public abstract class YubicoClient {

    private static final Logger log = LoggerFactory.getLogger(YubicoClient.class);

    public Integer clientId;

    public byte[] key;

    public Integer sync;

    public int maxRetries = 5;

    protected String[] wsapi_urls = new String[] {"https://api.yubico.com/wsapi/2.0/verify"};

    public String userAgent =
            "yubico-java-client/3.0.5 ("
                    + System.getProperty("java.vendor")
                    + " "
                    + System.getProperty("java.version")
                    + ")";

    private static final Integer OTP_MIN_LEN = 32;

    private static final Integer OTP_MAX_LEN = 48;

    public abstract VerificationResponse verify(String text)
            throws YubicoVerificationException,
                    YubicoValidationFailure,
                    YubicoVerificationException,
                    YubicoValidationFailure;

    public void setKey(String text) {
        this.key = Base64.getDecoder().decode(text.getBytes());
    }

    public String getKey() {
        return new String(Base64.getEncoder().encode(this.key));
    }

    public void setMaxRetries(int maxRetries) {
        if (maxRetries < 0) {
            throw new IllegalArgumentException("negative maxRetries is not valid.");
        }
        this.maxRetries = maxRetries;
    }

    public String[] getWsapiUrls() {
        return this.wsapi_urls;
    }

    public void setWsapiUrls(String[] texts) {
        String[] texts2 = texts;
        int value = texts.length;
        for (int index = 0; index < value; index++) {
            String text = texts2[index];
            this.warnIfDeprecatedUrl(text);
        }
        this.wsapi_urls = texts;
    }

    protected void warnIfDeprecatedUrl(String text) {
        if (text != null && text.startsWith("http:")) {
            log.warn(
                    "Deprecated YubiCloud URL: {} - naked HTTP requests are deprecated and will not be supported from 2019-02-04. See: https://status.yubico.com/2018/11/26/deprecating-yubicloud-v1-protocol-plain-text-requests-and-old-tls-versions/",
                    text);
        }
        if (text != null
                && (text.startsWith("https://api2.yubico.com/")
                        || text.startsWith("https://api3.yubico.com/")
                        || text.startsWith("https://api4.yubico.com/")
                        || text.startsWith("https://api5.yubico.com/"))) {
            log.warn(
                    "Deprecated YubiCloud URL: {} - api2, api3, api4 and api5 are deprecated will not be supported from 2020-07-01. See: https://status.yubico.com/",
                    text);
        }
    }

    public static YubicoClient getClient(Integer value, String text) {
        return new YubicoClientImpl(value, text);
    }

    public static String getPublicId(String text) {
        if (text != null && text.length() >= OTP_MIN_LEN) {
            Integer size = text.length();
            return text.substring(0, size - 32).toLowerCase();
        } else {
            throw new IllegalArgumentException("The OTP is too short to be valid");
        }
    }

    public static boolean isValidOTPFormat(String text) {
        if (text == null) {
            return false;
        }
        int size = text.length();
        char[] values;
        int value = (values = text.toCharArray()).length;
        for (int index = 0; index < value; index++) {
            char value2;
            if ((value2 = values[index]) < ' ' || value2 > '~') {
                return false;
            }
        }
        return OTP_MIN_LEN <= size && size <= OTP_MAX_LEN;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Integer getClientId() {
        return this.clientId;
    }

    public void setSync(Integer sync) {
        this.sync = sync;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
