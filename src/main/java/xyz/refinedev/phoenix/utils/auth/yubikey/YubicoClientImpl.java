package xyz.refinedev.phoenix.utils.auth.yubikey;

import xyz.refinedev.phoenix.utils.auth.yubikey.api.HmacUtils;
import xyz.refinedev.phoenix.utils.auth.yubikey.api.QueryString;
import xyz.refinedev.phoenix.utils.auth.yubikey.api.ResponseStatus;
import xyz.refinedev.phoenix.utils.auth.yubikey.api.VerificationResponse;
import xyz.refinedev.phoenix.utils.auth.yubikey.api.VerificationService;
import xyz.refinedev.phoenix.utils.auth.yubikey.api.YubicoClient;
import xyz.refinedev.phoenix.utils.auth.yubikey.exception.YubicoSignatureException;
import xyz.refinedev.phoenix.utils.auth.yubikey.exception.YubicoValidationFailure;
import xyz.refinedev.phoenix.utils.auth.yubikey.exception.YubicoVerificationException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;

public class YubicoClientImpl extends YubicoClient {

    private final VerificationService verificationService;

    YubicoClientImpl(VerificationService verificationService2) {
        this.verificationService = verificationService2;
    }

    public YubicoClientImpl(Integer value) {
        this(new VerificationService());
        super.clientId = value;
    }

    public YubicoClientImpl(Integer value, String text) {
        this(value);
        this.setKey(text);
    }

    public YubicoClientImpl(Integer value, String text, Integer value2) {
        this(value, text);
        this.setSync(value2);
    }

    @Override
    public VerificationResponse verify(String text)
            throws YubicoVerificationException, YubicoValidationFailure {
        if (!isValidOTPFormat(text)) {
            throw new IllegalArgumentException("The OTP is not a valid format");
        }
        TreeMap treeMap = new TreeMap();
        String text2 = UUID.randomUUID().toString().replaceAll("-", "");
        treeMap.put("nonce", text2);
        treeMap.put("id", super.clientId.toString());
        treeMap.put("otp", text);
        treeMap.put("timestamp", "1");
        if (super.sync != null) {
            treeMap.put("sl", super.sync.toString());
        }
        String text3;
        try {
            text3 = QueryString.get(treeMap);
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new YubicoVerificationException(
                    "Failed to encode parameter.", unsupportedEncodingException);
        }
        if (super.key != null) {
            text3 = this.formatString(text3);
        }
        String[] wsapiUrls = this.getWsapiUrls();
        ArrayList<String> values = new ArrayList<>();
        String[] texts = wsapiUrls;
        int value = wsapiUrls.length;
        for (int index = 0; index < value; index++) {
            String text4 = texts[index];
            this.warnIfDeprecatedUrl(text4);
            values.add(text4 + "?" + text3);
        }
        VerificationResponse verificationResponse =
                this.verificationService.findVerificationResponse(
                        values, super.userAgent, super.maxRetries);
        if (super.key != null) {
            this.applyVerificationResponse(verificationResponse);
        }
        if (!verificationResponse.getStatus().isError()) {
            if (verificationResponse.getOtp() == null
                    || !text.equals(verificationResponse.getOtp())) {
                throw new YubicoValidationFailure(
                        "OTP mismatch in response, is there a man-in-the-middle?");
            }
            if (verificationResponse.getNonce() == null
                    || !text2.equals(verificationResponse.getNonce())) {
                throw new YubicoValidationFailure(
                        "Nonce mismatch in response, is there a man-in-the-middle?");
            }
        }
        return verificationResponse;
    }

    private void applyVerificationResponse(VerificationResponse verificationResponse)
            throws YubicoValidationFailure, YubicoVerificationException {
        StringBuilder stringBuilder = new StringBuilder();
        for (Entry<String, String> entry : verificationResponse.getKeyValueMap().entrySet()) {
            if (!"h".equals(entry.getKey())) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append("&");
                }
                stringBuilder
                        .append((String) entry.getKey())
                        .append("=")
                        .append((String) entry.getValue());
            }
        }
        try {
            String text = HmacUtils.formatString(stringBuilder.toString(), super.key).trim();
            if (!verificationResponse.getH().equals(text)
                    && !verificationResponse.getStatus().equals(ResponseStatus.BAD_SIGNATURE)) {
                throw new YubicoValidationFailure("Signatures do not match");
            }
        } catch (YubicoSignatureException yubicoSignatureException) {
            throw new YubicoVerificationException(
                    "Failed to calculate the response signature.", yubicoSignatureException);
        }
    }

    private String formatString(String text) throws YubicoVerificationException {
        try {
            return text
                    + "&h="
                    + URLEncoder.encode(HmacUtils.formatString(text, super.key), "UTF-8");
        } catch (YubicoSignatureException yubicoSignatureException) {
            throw new YubicoVerificationException(
                    "Failed signing of request", yubicoSignatureException);
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new YubicoVerificationException(
                    "Failed to encode signature", unsupportedEncodingException);
        }
    }
}
