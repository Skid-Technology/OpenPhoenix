package xyz.refinedev.phoenix.utils;

import xyz.refinedev.lib.snakeyml.Yaml;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.Map.Entry;

public final class RetentionEnvironment {

    private static final String text = "default";

    private RetentionEnvironment() {}

    public static String formatString(File file, String text) {
        if (!file.isFile()) {
            return null;
        }
        try {
            InputStreamReader inputStreamReader =
                    new InputStreamReader(
                            Files.newInputStream(file.toPath()), StandardCharsets.UTF_8);
            String text2;
            label57:
            {
                label56:
                {
                    String[] texts;
                    try {
                        Object result = new Yaml().load(inputStreamReader);
                        int value = (texts = new String[] {"PRODUCTS", "PLUGINS"}).length;
                        for (int index = 0; index < value; index++) {
                            String text3 = texts[index];
                            Object result2;
                            if ((result2 = findObject(findObject(result, text3), text))
                                    instanceof Map) {
                                Object result3;
                                if ((result3 = findObject(result2, "BRANCH")) != null
                                        && !String.valueOf(result3).trim().isEmpty()) {
                                    text2 = String.valueOf(result3).trim();
                                    break label56;
                                }
                                text2 = "default";
                                break label57;
                            }
                        }
                        texts = null;
                    } catch (Throwable throwable) {
                        try {
                            inputStreamReader.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                        throw throwable;
                    }
                    inputStreamReader.close();
                    return null;
                }
                inputStreamReader.close();
                return text2;
            }
            inputStreamReader.close();
            return text2;
        } catch (Exception exception) {
            return null;
        }
    }

    private static Object findObject(Object result, String text) {
        if (!(result instanceof Map)) {
            return null;
        }
        for (Entry<Object, Object> entry :
                (Iterable<Entry<Object, Object>>) ((Iterable) ((Map) result).entrySet())) {
            if (text.equalsIgnoreCase(String.valueOf(entry.getKey()))) {
                return entry.getValue();
            }
        }
        return null;
    }
}
