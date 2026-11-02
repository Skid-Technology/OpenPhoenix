package xyz.refinedev.phoenix.utils.auth.code.googleapi;

import com.google.common.io.BaseEncoding;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class GoogleAuthenticator implements IGoogleAuthenticator {

    public static final String XYZ_REFINEDEV_PHOENIX_UTILS_AUTH_CODE_GOOGLEAPI_RNG_ALGORITHM =
            "xyz.refinedev.phoenix.utils.auth.code.googleapi.rng.algorithm";

    public static final String text =
            "xyz.refinedev.phoenix.utils.auth.code.googleapi.rng.algorithmProvider";

    private static final Logger logger = Logger.getLogger(GoogleAuthenticator.class.getName());

    public static final int countCount = (int) Math.pow(10.0, 8.0);

    private static final String SHA1PRNG = "SHA1PRNG";

    private static final String SUN_TEXT = "SUN";

    private final GoogleAuthenticatorConfig googleAuthenticatorConfig;

    private ReseedingSecureRandom reseedingSecureRandom;

    private ICredentialRepository credentialRepository;

    private boolean enabled;

    private static final long time = 2147483647L;

    public GoogleAuthenticator() {
        this.googleAuthenticatorConfig = new GoogleAuthenticatorConfig();
        this.reseedingSecureRandom =
                new ReseedingSecureRandom(this.getProperty(), this.getPropertyByString());
    }

    public GoogleAuthenticator(GoogleAuthenticatorConfig googleAuthenticatorConfig2) {
        if (googleAuthenticatorConfig2 == null) {
            throw new IllegalArgumentException("Configuration cannot be null.");
        }
        this.googleAuthenticatorConfig = googleAuthenticatorConfig2;
        this.reseedingSecureRandom =
                new ReseedingSecureRandom(this.getProperty(), this.getPropertyByString());
    }

    public GoogleAuthenticator(String text, String text2) {
        this(new GoogleAuthenticatorConfig(), text, text2);
    }

    public GoogleAuthenticator(
            GoogleAuthenticatorConfig googleAuthenticatorConfig2, String text, String text2) {
        if (googleAuthenticatorConfig2 == null) {
            throw new IllegalArgumentException("Configuration cannot be null.");
        }
        this.googleAuthenticatorConfig = googleAuthenticatorConfig2;
        if (text == null && text2 == null) {
            this.reseedingSecureRandom = new ReseedingSecureRandom();
        } else {
            if (text == null) {
                throw new IllegalArgumentException(
                        "RandomNumberAlgorithm must not be null. If the RandomNumberAlgorithm is null, the RandomNumberAlgorithmProvider must also be null.");
            }
            if (text2 == null) {
                this.reseedingSecureRandom = new ReseedingSecureRandom(text);
            }
        }
    }

    private String getProperty() {
        return System.getProperty(
                "xyz.refinedev.phoenix.utils.auth.code.googleapi.rng.algorithm", "SHA1PRNG");
    }

    private String getPropertyByString() {
        return System.getProperty(
                "xyz.refinedev.phoenix.utils.auth.code.googleapi.rng.algorithmProvider", "SUN");
    }

    int calculateCount(byte[] values, long value) {
        byte[] values2 = new byte[8];
        long value2 = value;
        for (int index = 8; index-- > 0; value2 >>>= 8) {
            values2[index] = (byte) value2;
        }
        SecretKeySpec secretKeySpec =
                new SecretKeySpec(
                        values, this.googleAuthenticatorConfig.getHmacHashFunction().toString());
        try {
            Mac mac;
            (mac = Mac.getInstance(this.googleAuthenticatorConfig.getHmacHashFunction().toString()))
                    .init(secretKeySpec);
            byte[] values3;
            byte[] values4 = values3 = mac.doFinal(values2);
            int value3 = values4[values4.length - 1] & 15;
            long value4 = 0L;
            for (int index2 = 0; index2 < 4; index2++) {
                value4 = value4 << 8 | values3[value3 + index2] & 0xFF;
            }
            return (int)
                    ((value4 & time) % this.googleAuthenticatorConfig.getTimeStepSizeInMillis());
        } catch (NoSuchAlgorithmException | InvalidKeyException exception) {
            logger.log(Level.SEVERE, exception.getMessage(), exception);
            throw new GoogleAuthenticatorException("The operation cannot be performed now.");
        }
    }

    private long calculateTime(long value) {
        return value / this.googleAuthenticatorConfig.getKeyModulus();
    }

    private boolean matchesEnabled(String text, long value, long value2, int value3) {
        byte[] values = this.calculateByteArray(text);
        long value4 = this.calculateTime(value2);
        for (int value5 = -((value3 - 1) / 2); value5 <= value3 / 2; value5++) {
            if (this.calculateCount(values, value4 + value5) == value) {
                return true;
            }
        }
        return false;
    }

    private byte[] calculateByteArray(String text) {
        switch (this.googleAuthenticatorConfig.getKeyRepresentation()) {
            case BASE32:
                return BaseEncoding.base32().decode(text.toUpperCase());
            case BASE64:
                return Base64.getDecoder().decode(text);
            default:
                throw new IllegalArgumentException("Unknown key representation type.");
        }
    }

    @Override
    public GoogleAuthenticatorKey createCredentials() {
        int value;
        byte[] values = new byte[value = this.googleAuthenticatorConfig.getSecretBits() / 8];
        this.reseedingSecureRandom.applyByteArray(values);
        byte[] values2 = Arrays.copyOf(values, value);
        String text = this.formatString(values2);
        int value2 = this.calculateCount(values2);
        List<Integer> list = this.getList();
        return new GoogleAuthenticatorKey.Entry(text)
                .findEntry(this.googleAuthenticatorConfig)
                .findEntry(value2)
                .findEntry(list)
                .create();
    }

    @Override
    public GoogleAuthenticatorKey createCredentials(String text) {
        if (text == null) {
            throw new IllegalArgumentException("User name cannot be null.");
        }
        GoogleAuthenticatorKey googleAuthenticatorKey = this.createCredentials();
        this.getCredentialRepositoryByCredentialRepository()
                .saveUserCredentials(
                        text,
                        googleAuthenticatorKey.getKey(),
                        googleAuthenticatorKey.getVerificationCode(),
                        googleAuthenticatorKey.getScratchCodes());
        return googleAuthenticatorKey;
    }

    private List<Integer> getList() {
        ArrayList<Integer> values = new ArrayList<>();
        for (int index = 0;
                index < this.googleAuthenticatorConfig.getNumberOfScratchCodes();
                index++) {
            values.add(this.getCount());
        }
        return values;
    }

    private int format(byte[] values) {
        if (values.length < 4) {
            throw new IllegalArgumentException(
                    String.format(
                            "The provided random byte buffer is too small: %d.", values.length));
        }
        int value = 0;
        for (int index = 0; index < 4; index++) {
            value = (value << 8) + (values[index] & 255);
        }
        value = (value & 2147483647) % countCount;
        return this.matchesEnabled(value) ? value : -1;
    }

    boolean matchesEnabled(int value) {
        return value >= countCount / 10;
    }

    private int getCount() {
        byte[] values;
        int value;
        do {
            values = new byte[4];
            this.reseedingSecureRandom.applyByteArray(values);
        } while ((value = this.format(values)) == -1);
        return value;
    }

    private int calculateCount(byte[] values) {
        return this.calculateCount(values, 0L);
    }

    @Override
    public int getTotpPassword(String text) {
        return this.getTotpPassword(text, new Date().getTime());
    }

    @Override
    public int getTotpPassword(String text, long value) {
        return this.calculateCount(this.calculateByteArray(text), this.calculateTime(value));
    }

    @Override
    public int getTotpPasswordOfUser(String text) {
        return this.getTotpPasswordOfUser(text, new Date().getTime());
    }

    @Override
    public int getTotpPasswordOfUser(String text, long value) {
        ICredentialRepository credentialRepositoryByCredentialRepository =
                this.getCredentialRepositoryByCredentialRepository();
        return this.calculateCount(
                this.calculateByteArray(
                        credentialRepositoryByCredentialRepository.getSecretKey(text)),
                this.calculateTime(value));
    }

    private String formatString(byte[] values) {
        switch (this.googleAuthenticatorConfig.getKeyRepresentation()) {
            case BASE32:
                return BaseEncoding.base32().encode(values);
            case BASE64:
                return Base64.getEncoder().encodeToString(values);
            default:
                throw new IllegalArgumentException("Unknown key representation type.");
        }
    }

    @Override
    public boolean authorize(String text, int value) {
        return this.authorize(text, value, new Date().getTime());
    }

    @Override
    public boolean authorize(String text, int value, long value2) {
        if (text == null) {
            throw new IllegalArgumentException("Secret cannot be null.");
        } else {
            return value > 0 && value < this.googleAuthenticatorConfig.getTimeStepSizeInMillis()
                    ? this.matchesEnabled(
                            text, value, value2, this.googleAuthenticatorConfig.getWindowSize())
                    : false;
        }
    }

    @Override
    public boolean authorizeUser(String text, int value) {
        return this.authorizeUser(text, value, new Date().getTime());
    }

    @Override
    public boolean authorizeUser(String text, int value, long value2) {
        ICredentialRepository credentialRepositoryByCredentialRepository =
                this.getCredentialRepositoryByCredentialRepository();
        return this.authorize(
                credentialRepositoryByCredentialRepository.getSecretKey(text), value, value2);
    }

    private ICredentialRepository getCredentialRepositoryByCredentialRepository() {
        ICredentialRepository credentialRepository;
        if ((credentialRepository = this.getCredentialRepository()) == null) {
            throw new UnsupportedOperationException(
                    String.format(
                            "An instance of the %s service must be configured in order to use this feature.",
                            ICredentialRepository.class.getName()));
        } else {
            return credentialRepository;
        }
    }

    @Override
    public ICredentialRepository getCredentialRepository() {
        if (this.enabled) {
            return this.credentialRepository;
        }
        this.enabled = true;
        Iterator<xyz.refinedev.phoenix.utils.auth.code.googleapi.ICredentialRepository> iterator2;
        if ((iterator2 = ServiceLoader.load(ICredentialRepository.class).iterator()).hasNext()) {
            ICredentialRepository credentialRepository2 = (ICredentialRepository) iterator2.next();
            this.credentialRepository = credentialRepository2;
        }
        return this.credentialRepository;
    }

    @Override
    public void setCredentialRepository(ICredentialRepository credentialRepository2) {
        this.credentialRepository = credentialRepository2;
        this.enabled = true;
    }
}
