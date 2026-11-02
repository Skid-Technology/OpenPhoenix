package xyz.refinedev.phoenix.utils.auth.code.googleapi;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class ReseedingSecureRandom {

    private final String text;

    private final String textString;

    private final AtomicInteger counter = new AtomicInteger(0);

    private volatile SecureRandom random;

    ReseedingSecureRandom() {
        this.textString = null;
        this.text = null;
        this.initialize();
    }

    ReseedingSecureRandom(String textString) {
        if (textString == null) {
            throw new IllegalArgumentException("Algorithm cannot be null.");
        }
        this.textString = textString;
        this.text = null;
        this.initialize();
    }

    ReseedingSecureRandom(String textString, String text) {
        if (textString == null) {
            throw new IllegalArgumentException("Algorithm cannot be null.");
        }
        if (text == null) {
            throw new IllegalArgumentException("Provider cannot be null.");
        }
        this.textString = textString;
        this.text = text;
        this.initialize();
    }

    private void initialize() {
        try {
            if (this.textString == null && this.text == null) {
                this.random = new SecureRandom();
            } else if (this.text == null) {
                this.random = SecureRandom.getInstance(this.textString);
            } else {
                this.random = SecureRandom.getInstance(this.textString, this.text);
            }
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            throw new GoogleAuthenticatorException(
                    String.format(
                            "Could not initialise SecureRandom with the specified algorithm: %s. Another provider can be chosen setting the %s system property.",
                            this.textString,
                            "xyz.refinedev.phoenix.utils.auth.code.googleapi.rng.algorithm"),
                    noSuchAlgorithmException);
        } catch (NoSuchProviderException noSuchProviderException) {
            throw new GoogleAuthenticatorException(
                    String.format(
                            "Could not initialise SecureRandom with the specified provider: %s. Another provider can be chosen setting the %s system property.",
                            this.text,
                            "xyz.refinedev.phoenix.utils.auth.code.googleapi.rng.algorithmProvider"),
                    noSuchProviderException);
        }
    }

    void applyByteArray(byte[] values) {
        if (this.counter.incrementAndGet() > 1000000) {
            synchronized (this) {
                if (this.counter.get() > 1000000) {
                    this.initialize();
                    this.counter.set(0);
                }
            }
        }
        this.random.nextBytes(values);
    }
}
