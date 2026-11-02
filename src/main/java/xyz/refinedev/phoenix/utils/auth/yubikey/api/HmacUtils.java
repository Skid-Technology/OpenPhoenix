package xyz.refinedev.phoenix.utils.auth.yubikey.api;

import xyz.refinedev.phoenix.utils.auth.yubikey.exception.YubicoSignatureException;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HmacUtils {

    private static final String text = "HmacSHA1";

    public static String formatString(String text, byte[] values) throws YubicoSignatureException {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(values, "HmacSHA1");
            Mac mac;
            (mac = Mac.getInstance("HmacSHA1")).init(secretKeySpec);
            byte[] values2 = mac.doFinal(text.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(values2);
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            throw new YubicoSignatureException(
                    "No such algorithm (HMAC_SHA1?)", noSuchAlgorithmException);
        } catch (InvalidKeyException invalidKeyException) {
            throw new YubicoSignatureException("Invalid key in signature.", invalidKeyException);
        } catch (IllegalStateException illegalStateException) {
            throw new YubicoSignatureException("Illegal state in signature", illegalStateException);
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new YubicoSignatureException(
                    "Unsupported encoding (utf8?)", unsupportedEncodingException);
        }
    }
}
