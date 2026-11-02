package xyz.refinedev.phoenix.repository.redis;

import com.google.gson.Gson;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class RedisRepositorySerializer {

    private RedisRepositorySerializer() {}

    static boolean matchesEnabled(Map<String, String> entries, Set<String> values) {
        return entries != null && !entries.isEmpty() && entries.keySet().containsAll(values);
    }

    static String formatString(Map<String, String> entries, Gson gson) {
        StringBuilder stringBuilder = new StringBuilder("{");
        boolean matches = true;
        for (Entry<String, String> entry : entries.entrySet()) {
            if (!matches) {
                stringBuilder.append(',');
            }
            stringBuilder
                    .append(gson.toJson(entry.getKey()))
                    .append(':')
                    .append((String) entry.getValue());
            matches = false;
        }
        return stringBuilder.append('}').toString();
    }
}
