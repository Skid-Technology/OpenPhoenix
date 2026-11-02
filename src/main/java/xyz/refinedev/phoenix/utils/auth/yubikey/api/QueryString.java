package xyz.refinedev.phoenix.utils.auth.yubikey.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

public class QueryString {

    private static final String UTF8 = "UTF-8";

    public static String get(Map<String, String> entries) throws UnsupportedEncodingException {
        StringBuilder stringBuilder = new StringBuilder();
        for (Entry<String, String> entry : entries.entrySet()) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append("&");
            }
            stringBuilder
                    .append((String) entry.getKey())
                    .append("=")
                    .append(URLEncoder.encode((String) entry.getValue(), UTF8));
        }
        return stringBuilder.toString();
    }
}
