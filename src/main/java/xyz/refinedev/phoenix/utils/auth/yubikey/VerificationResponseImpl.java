package xyz.refinedev.phoenix.utils.auth.yubikey;

import xyz.refinedev.phoenix.utils.auth.yubikey.api.ResponseStatus;
import xyz.refinedev.phoenix.utils.auth.yubikey.api.VerificationResponse;
import xyz.refinedev.phoenix.utils.auth.yubikey.api.YubicoClient;
import xyz.refinedev.phoenix.utils.auth.yubikey.exception.YubicoResponseFormatException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;

public class VerificationResponseImpl implements VerificationResponse {

    private String h;

    private String t;

    private ResponseStatus status;

    private String timestamp;

    private String sessioncounter;

    private String sessionuse;

    private String sl;

    private String publicId;

    private String nonce;

    private final Map<String, String> keyValueMap = new TreeMap<>();

    public VerificationResponseImpl(InputStream inputStream)
            throws IOException, YubicoResponseFormatException {
        if (inputStream == null) {
            throw new IOException("InputStream argument was null");
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String text;
        while ((text = bufferedReader.readLine()) != null) {
            int value;
            if ((value = text.indexOf("=")) != -1) {
                String text2 = text.substring(0, value);
                String text3 = text.substring(value + 1);
                if ("h".equals(text2)) {
                    this.h = text3;
                } else if ("t".equals(text2)) {
                    this.t = text3;
                } else if ("otp".equals(text2)) {
                    this.publicId = text3;
                } else if ("status".equals(text2)) {
                    this.status = ResponseStatus.valueOf(text3);
                } else if ("timestamp".equals(text2)) {
                    this.timestamp = text3;
                } else if ("sessioncounter".equals(text2)) {
                    this.sessioncounter = text3;
                } else if ("sessionuse".equals(text2)) {
                    this.sessionuse = text3;
                } else if ("sl".equals(text2)) {
                    this.sl = text3;
                } else if ("nonce".equals(text2)) {
                    this.nonce = text3;
                }
                this.keyValueMap.put(text2, text3);
            }
        }
        bufferedReader.close();
        if (this.status == null) {
            throw new YubicoResponseFormatException("Invalid response, contains no status.");
        }
    }

    @Override
    public String toString() {
        return this.publicId + ":" + this.status;
    }

    @Override
    public boolean isOk() {
        return this.getStatus() == ResponseStatus.OK;
    }

    @Override
    public String getPublicId() {
        return YubicoClient.getPublicId(this.publicId);
    }

    @Override
    public String getH() {
        return this.h;
    }

    @Override
    public String getT() {
        return this.t;
    }

    @Override
    public ResponseStatus getStatus() {
        return this.status;
    }

    @Override
    public String getTimestamp() {
        return this.timestamp;
    }

    @Override
    public String getSessioncounter() {
        return this.sessioncounter;
    }

    @Override
    public String getSessionuse() {
        return this.sessionuse;
    }

    @Override
    public String getSl() {
        return this.sl;
    }

    @Override
    public String getOtp() {
        return this.publicId;
    }

    @Override
    public String getNonce() {
        return this.nonce;
    }

    @Override
    public Map<String, String> getKeyValueMap() {
        return this.keyValueMap;
    }
}
