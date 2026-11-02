package xyz.refinedev.phoenix.utils.auth.code.googleapi;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public final class GoogleAuthenticatorQRGenerator {

    private static final String text =
            "https://api.qrserver.com/v1/create-qr-code/?data=%s&size=200x200&ecc=M&margin=10";

    private static final long time = 1000L;

    private static String encode(String text) {
        try {
            return URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new IllegalArgumentException(
                    "UTF-8 encoding is not supported by URLEncoder.", unsupportedEncodingException);
        }
    }

    private static String formatString(String text, String text2) {
        if (text2 != null && text2.trim().length() != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            if (text != null) {
                if (text.contains(":")) {
                    throw new IllegalArgumentException("Issuer cannot contain the ':' character.");
                }
                stringBuilder.append(text);
                stringBuilder.append(":");
            }
            stringBuilder.append(text2);
            return stringBuilder.toString();
        } else {
            throw new IllegalArgumentException("Account name must not be empty.");
        }
    }

    public static String format(
            String text, String text2, GoogleAuthenticatorKey googleAuthenticatorKey) {
        return String.format(
                "https://api.qrserver.com/v1/create-qr-code/?data=%s&size=200x200&ecc=M&margin=10",
                encode(formatString(text, text2, googleAuthenticatorKey)));
    }

    public static String formatString(
            String text, String text2, GoogleAuthenticatorKey googleAuthenticatorKey) {
        String text3 =
                "otpauth://totp/"
                        + formatString(text, text2)
                        + "?secret="
                        + googleAuthenticatorKey.getKey();
        if (text != null) {
            if (text.contains(":")) {
                throw new IllegalArgumentException("Issuer cannot contain the ':' character.");
            }
            text3 = text3 + "&issuer=" + text;
        }
        GoogleAuthenticatorConfig config = googleAuthenticatorKey.getConfig();
        text3 = text3 + "&algorithm=" + formatString(config.getHmacHashFunction());
        text3 = text3 + "&digits=" + config.getCodeDigits();
        return text3 + "&period=" + config.getKeyModulus() / time;
    }

    private static String formatString(HmacHashFunction hmacHashFunction) {
        switch (hmacHashFunction) {
            case HmacSHA1:
                return "SHA1";
            case HmacSHA256:
                return "SHA256";
            case HmacSHA512:
                return "SHA512";
            default:
                throw new IllegalArgumentException(
                        String.format("Unknown algorithm %s", hmacHashFunction));
        }
    }
}
