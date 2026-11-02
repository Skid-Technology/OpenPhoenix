package xyz.refinedev.phoenix.utils.auth.code.googleapi;

import java.util.ArrayList;
import java.util.List;

public final class GoogleAuthenticatorKey {

    private final GoogleAuthenticatorConfig config;

    private final String key;

    private final int verificationCode;

    private final List<Integer> scratchCodes;

    private GoogleAuthenticatorKey(
            GoogleAuthenticatorConfig googleAuthenticatorConfig,
            String key,
            int verificationCode,
            List<Integer> values) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        if (googleAuthenticatorConfig == null) {
            throw new IllegalArgumentException("Configuration cannot be null");
        }
        if (values == null) {
            throw new IllegalArgumentException("Scratch codes cannot be null");
        }
        this.config = googleAuthenticatorConfig;
        this.key = key;
        this.verificationCode = verificationCode;
        this.scratchCodes = new ArrayList<>(values);
    }

    public List<Integer> getScratchCodes() {
        return this.scratchCodes;
    }

    public GoogleAuthenticatorConfig getConfig() {
        return this.config;
    }

    public String getKey() {
        return this.key;
    }

    public int getVerificationCode() {
        return this.verificationCode;
    }

    public static class Entry {

        private GoogleAuthenticatorConfig googleAuthenticatorConfig =
                new GoogleAuthenticatorConfig();

        private String text;

        private int count;

        private List<Integer> integers = new ArrayList<>();

        public Entry(String text) {
            this.text = text;
        }

        public GoogleAuthenticatorKey create() {
            return new GoogleAuthenticatorKey(
                    this.googleAuthenticatorConfig, this.text, this.count, this.integers);
        }

        public GoogleAuthenticatorKey.Entry findEntry(
                GoogleAuthenticatorConfig googleAuthenticatorConfig2) {
            this.googleAuthenticatorConfig = googleAuthenticatorConfig2;
            return this;
        }

        public GoogleAuthenticatorKey.Entry findEntry(String text) {
            this.text = text;
            return this;
        }

        public GoogleAuthenticatorKey.Entry findEntry(int count) {
            this.count = count;
            return this;
        }

        public GoogleAuthenticatorKey.Entry findEntry(List<Integer> integers) {
            this.integers = integers;
            return this;
        }
    }
}
