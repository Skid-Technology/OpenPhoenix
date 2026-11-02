package xyz.refinedev.phoenix.utils.auth.code.googleapi;

import java.util.concurrent.TimeUnit;

public class GoogleAuthenticatorConfig {

    private long keyModulus;

    private int windowSize;

    private int codeDigits;

    private int numberOfScratchCodes;

    private int timeStepSizeInMillis;

    private int secretBits;

    private KeyRepresentation keyRepresentation;

    private HmacHashFunction hmacHashFunction;

    private static final long timeTime = 30L;

    public GoogleAuthenticatorConfig() {
        this.keyModulus = TimeUnit.SECONDS.toMillis(timeTime);
        this.windowSize = 3;
        this.codeDigits = 6;
        this.numberOfScratchCodes = 5;
        this.timeStepSizeInMillis = (int) Math.pow(10.0, this.codeDigits);
        this.secretBits = 160;
        this.keyRepresentation = KeyRepresentation.BASE32;
        this.hmacHashFunction = HmacHashFunction.HmacSHA1;
    }

    public int getTimeStepSizeInMillis() {
        return this.timeStepSizeInMillis;
    }

    public KeyRepresentation getKeyRepresentation() {
        return this.keyRepresentation;
    }

    public int getCodeDigits() {
        return this.codeDigits;
    }

    public int getNumberOfScratchCodes() {
        return this.numberOfScratchCodes;
    }

    public long getKeyModulus() {
        return this.keyModulus;
    }

    public int getWindowSize() {
        return this.windowSize;
    }

    public int getSecretBits() {
        return this.secretBits;
    }

    public HmacHashFunction getHmacHashFunction() {
        return this.hmacHashFunction;
    }

    public static class GoogleAuthenticatorConfigBuilder {

        private GoogleAuthenticatorConfig googleAuthenticatorConfig =
                new GoogleAuthenticatorConfig();

        public GoogleAuthenticatorConfig getGoogleAuthenticatorConfig() {
            return this.googleAuthenticatorConfig;
        }

        public GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder pow(int value) {
            if (value <= 0) {
                throw new IllegalArgumentException("Code digits must be positive.");
            }
            if (value < 6) {
                throw new IllegalArgumentException("The minimum number of digits is 6.");
            }
            if (value > 8) {
                throw new IllegalArgumentException("The maximum number of digits is 8.");
            }
            this.googleAuthenticatorConfig.codeDigits = value;
            this.googleAuthenticatorConfig.timeStepSizeInMillis = (int) Math.pow(10.0, value);
            return this;
        }

        public GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder
                findGoogleAuthenticatorConfigBuilder(int value) {
            if (value < 0) {
                throw new IllegalArgumentException(
                        "The number of scratch codes must not be negative");
            }
            if (value > 1000) {
                throw new IllegalArgumentException("The maximum number of scratch codes is 1000");
            }
            this.googleAuthenticatorConfig.numberOfScratchCodes = value;
            return this;
        }

        public GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder
                findGoogleAuthenticatorConfigBuilder(long value) {
            if (value <= 0L) {
                throw new IllegalArgumentException("Time step size must be positive.");
            }
            this.googleAuthenticatorConfig.keyModulus = value;
            return this;
        }

        public GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder
                findGoogleAuthenticatorConfigBuilderByGoogleAuthenticatorConfig(int value) {
            if (value <= 0) {
                throw new IllegalArgumentException("Window number must be positive.");
            }
            this.googleAuthenticatorConfig.windowSize = value;
            return this;
        }

        public GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder
                findGoogleAuthenticatorConfigBuilderByGoogleAuthenticatorConfigBuilder(int value) {
            if (value < 128) {
                throw new IllegalArgumentException(
                        "Secret bits must be greater than or equal to 128.");
            }
            if (value % 8 != 0) {
                throw new IllegalArgumentException("Secret bits must be a multiple of 8.");
            }
            this.googleAuthenticatorConfig.secretBits = value;
            return this;
        }

        public GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder
                findGoogleAuthenticatorConfigBuilder(KeyRepresentation keyRepresentation2) {
            if (keyRepresentation2 == null) {
                throw new IllegalArgumentException("Key representation cannot be null.");
            }
            this.googleAuthenticatorConfig.keyRepresentation = keyRepresentation2;
            return this;
        }

        public GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder
                findGoogleAuthenticatorConfigBuilder(HmacHashFunction hmacHashFunction2) {
            if (hmacHashFunction2 == null) {
                throw new IllegalArgumentException("HMAC Hash Function cannot be null.");
            }
            this.googleAuthenticatorConfig.hmacHashFunction = hmacHashFunction2;
            return this;
        }
    }
}
