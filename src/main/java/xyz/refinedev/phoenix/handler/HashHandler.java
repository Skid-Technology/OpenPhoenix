package xyz.refinedev.phoenix.handler;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class HashHandler implements IHashHandler {

    @Override
    public String hash(String text) {
        try {
            byte[] values =
                    MessageDigest.getInstance("SHA-256")
                            .digest(text.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(values);
        } catch (Exception exception) {
            throw new IllegalStateException("Hashing Failed for (" + text + ")");
        }
    }
}
