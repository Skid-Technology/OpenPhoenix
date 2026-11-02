package xyz.refinedev.phoenix.handler.database.local;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class LocalStorePersistence {

    public static final Predicate<String> predicate =
            result ->
                    !result.startsWith("px-profiles:")
                            && !result.equals("px-servers")
                            && !result.equals("px-proxies")
                            && !result.startsWith("px-setup-lock:")
                            && !result.startsWith("wave:");

    @SerializedName("strings")
    private Map<String, String> strings = new HashMap<>();

    @SerializedName("hashes")
    private Map<String, Map<String, String>> hashes = new HashMap<>();

    @SerializedName("expiries")
    private Map<String, Long> expiries = new HashMap<>();

    private static final Object lock = new Object();

    public void applyFile(File file, Gson gson) throws IOException {
        synchronized (lock) {
            File file2;
            if ((file2 = file.getAbsoluteFile().getParentFile()) != null) {
                file2.mkdirs();
            }
            File file3 = new File(file.getPath() + ".tmp");
            OutputStreamWriter outputStreamWriter =
                    new OutputStreamWriter(new FileOutputStream(file3), StandardCharsets.UTF_8);
            try {
                gson.toJson(this, outputStreamWriter);
            } catch (Throwable throwable) {
                try {
                    outputStreamWriter.close();
                } catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            outputStreamWriter.close();
            Files.move(file3.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static LocalStorePersistence findLocalStorePersistence(
            File file, Gson gson, Logger logger) {
        if (!file.exists()) {
            return null;
        }
        String text;
        try {
            text = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        } catch (IOException oException) {
            throw new IllegalStateException(
                    "Could not read "
                            + file
                            + "; not starting with an empty store so it is not overwritten",
                    oException);
        }
        LocalStorePersistence localStorePersistence;
        try {
            localStorePersistence =
                    (LocalStorePersistence) gson.fromJson(text, LocalStorePersistence.class);
        } catch (JsonParseException jsonParseException) {
            Path path =
                    file.toPath()
                            .resolveSibling(
                                    file.getName() + ".broken-" + System.currentTimeMillis());
            try {
                Files.move(file.toPath(), path);
            } catch (IOException oException2) {
                throw new IllegalStateException(
                        "Could not move the unreadable "
                                + file
                                + " aside; not starting with an empty store so it is not overwritten",
                        oException2);
            }
            logger.log(
                    Level.WARNING,
                    "Could not parse "
                            + file
                            + ", moved it to "
                            + path
                            + ". Phoenix is starting with an empty local store.",
                    jsonParseException);
            return null;
        }
        if (localStorePersistence == null) {
            return null;
        }
        if (localStorePersistence.strings == null) {
            localStorePersistence.strings = new HashMap<>();
        }
        if (localStorePersistence.hashes == null) {
            localStorePersistence.hashes = new HashMap<>();
        }
        if (localStorePersistence.expiries == null) {
            localStorePersistence.expiries = new HashMap<>();
        }
        return localStorePersistence;
    }

    public Map<String, String> getStrings() {
        return this.strings;
    }

    public Map<String, Map<String, String>> getHashes() {
        return this.hashes;
    }

    public Map<String, Long> getExpiries() {
        return this.expiries;
    }
}
