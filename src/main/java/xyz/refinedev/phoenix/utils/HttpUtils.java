package xyz.refinedev.phoenix.utils;

import xyz.refinedev.phoenix.PlatformGetter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public final class HttpUtils {

    public static String get(String text) {
        try {
            URLConnection uRLConnection;
            (uRLConnection = new URL(text).openConnection())
                    .setRequestProperty("User-Agent", "Phoenix Java API");
            uRLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            uRLConnection.connect();
            BufferedReader bufferedReader =
                    new BufferedReader(
                            new InputStreamReader(
                                    uRLConnection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder stringBuilder = new StringBuilder();
            String text2;
            while ((text2 = bufferedReader.readLine()) != null) {
                stringBuilder.append(text2);
            }
            return stringBuilder.toString();
        } catch (Exception exception) {
            PlatformGetter.getInstance().getLogger().severe("Failed to fetch from: " + text);
            return null;
        }
    }

    private HttpUtils() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}
