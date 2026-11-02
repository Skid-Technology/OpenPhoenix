package xyz.refinedev.phoenix.handler;

import xyz.refinedev.lib.boostedyaml.YamlDocument;
import xyz.refinedev.lib.boostedyaml.settings.Settings;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.profile.IProfile;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class LocaleHandler {

    private static final Object MISSING = new Object();

    public static final String AUTOMATIC = "Automatic";

    public static final String LANGUAGE_SETTING = "language";

    private final CommonPlatform api;

    private final Map<UUID, String> locales = new ConcurrentHashMap<>();

    private final List<LocaleHandler.Entry> registered = new ArrayList<>();

    private final Map<String, Object> cache = new ConcurrentHashMap<>();

    private volatile Map<Object, String> valuePaths = Collections.emptyMap();

    private volatile int overlayCount;

    public LocaleHandler(CommonPlatform commonPlatform) {
        this.api = commonPlatform;
    }

    public void register(String text, String text2, Class<?> classValue) {
        this.registered.add(new LocaleHandler.Entry(text, text2, classValue));
        this.registered.sort(
                (result, result2) ->
                        LocaleHandler.Entry.getTextString(result2).length()
                                - LocaleHandler.Entry.getTextString(result).length());
    }

    public void load() {
        Iterator<xyz.refinedev.phoenix.handler.LocaleHandler.Entry> iterator2 =
                this.registered.iterator();
        while (iterator2.hasNext()) {
            LocaleHandler.Entry.getYamlDocumentsByName((LocaleHandler.Entry) iterator2.next())
                    .clear();
        }
        this.cache.clear();
        this.overlayCount = 0;
        File file;
        if ((file = new File("plugins/Phoenix/locales")).exists() || file.mkdirs()) {
            File[] files;
            if ((files = file.listFiles()) != null) {
                int value = 0;
                File[] files2 = files;
                int value2 = files.length;
                for (int index = 0; index < value2; index++) {
                    File file2;
                    String text;
                    if ((text = (file2 = files2[index]).getName()).endsWith(".yml")) {
                        for (LocaleHandler.Entry entry : this.registered) {
                            String text2 = LocaleHandler.Entry.getText(entry) + "_";
                            if (text.startsWith(text2)) {
                                String text3;
                                if (!(text3 =
                                                text.substring(
                                                                text2.length(),
                                                                text.length() - ".yml".length())
                                                        .toLowerCase())
                                        .isEmpty()) {
                                    try {
                                        LocaleHandler.Entry.getYamlDocumentsByName(entry)
                                                .put(
                                                        text3,
                                                        YamlDocument.create(
                                                                file2, new Settings[0]));
                                        value++;
                                    } catch (Throwable throwable) {
                                        this.api
                                                .getLogger()
                                                .log(
                                                        Level.WARNING,
                                                        "Failed to load the '"
                                                                + text3
                                                                + "' locale overlay.",
                                                        throwable);
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
                this.overlayCount = value;
                if (value > 0) {
                    this.api.getLogger().info("Loaded " + value + " locale overlay(s).");
                }
                this.mapConfigValues();
            }
        }
    }

    private void mapConfigValues() {
        IdentityHashMap identityHashMap = new IdentityHashMap();
        for (LocaleHandler.Entry entry : this.registered) {
            try {
                this.walkConfig(
                        LocaleHandler.Entry.getType(entry),
                        LocaleHandler.Entry.getTextString(entry),
                        identityHashMap);
            } catch (Throwable throwable) {
                this.api
                        .getLogger()
                        .log(
                                Level.WARNING,
                                "Failed to map "
                                        + LocaleHandler.Entry.getType(entry).getSimpleName()
                                        + " for locales.",
                                throwable);
            }
        }
        this.valuePaths = identityHashMap;
    }

    public String pathOf(Object result) {
        return result == null ? null : this.valuePaths.get(result);
    }

    public String getTranslation(IProfile profile, String text) {
        if (text == null) {
            return null;
        }
        Object result;
        return (result = this.resolve(this.getLocale(profile), text, false)) instanceof String
                ? (String) result
                : null;
    }

    private void walkConfig(Class<?> classValue, String text, Map<Object, String> entries)
            throws IllegalAccessException {
        Field[] fields;
        int value = (fields = classValue.getDeclaredFields()).length;
        for (int index = 0; index < value; index++) {
            Field field;
            Object result;
            if (Modifier.isStatic((field = fields[index]).getModifiers())
                    && (field.getType() == String.class
                            || List.class.isAssignableFrom(field.getType()))
                    && (result = field.get(null)) != null) {
                entries.put(result, text + this.formatKey(field.getName()));
            }
        }
        Class[] classs;
        value = (classs = classValue.getDeclaredClasses()).length;
        for (int index2 = 0; index2 < value; index2++) {
            Class classValue2 = classs[index2];
            this.walkConfig(
                    classValue2, text + this.formatKey(classValue2.getSimpleName()) + ".", entries);
        }
    }

    private String formatKey(String text) {
        return text.toLowerCase().replace("_", "-");
    }

    public String tr(IProfile profile, String text) {
        if (text != null && profile != null) {
            String text2;
            return (text2 = this.valuePaths.get(text)) == null
                    ? text
                    : this.get(profile, text2, text);
        } else {
            return text;
        }
    }

    public List<String> tr(IProfile profile, List<String> values) {
        if (values != null && profile != null) {
            String text;
            return (text = this.valuePaths.get(values)) == null
                    ? values
                    : this.getList(profile, text, values);
        } else {
            return values;
        }
    }

    public String get(IProfile profile, String text, String text2) {
        Object result;
        return (result = this.resolve(this.getLocale(profile), text, false)) instanceof String
                ? (String) result
                : text2;
    }

    public List<String> getList(IProfile profile, String text, List<String> values) {
        Object result;
        return (result = this.resolve(this.getLocale(profile), text, true)) instanceof List
                ? (List) result
                : values;
    }

    public List<String> getAvailableLocales() {
        TreeSet treeSet = new TreeSet();
        for (LocaleHandler.Entry entry : this.registered) {
            treeSet.addAll(LocaleHandler.Entry.getYamlDocumentsByName(entry).keySet());
        }
        ArrayList<String> values;
        (values = new ArrayList()).add("Automatic");
        values.addAll(treeSet);
        return values;
    }

    public String getLocale(IProfile profile) {
        if (profile == null) {
            return null;
        } else {
            String text;
            if ((text = profile.getSetting("language", "")) != null
                    && !text.isEmpty()
                    && !text.equalsIgnoreCase("Automatic")) {
                return this.normalize(text);
            } else {
                String text2;
                if ((text2 = this.locales.get(profile.getUniqueId())) != null) {
                    return text2;
                } else {
                    return profile.getClientFingerprint() == null
                            ? null
                            : this.normalize(profile.getClientFingerprint().getLocale());
                }
            }
        }
    }

    public String getLocale(UUID uUID) {
        return this.locales.get(uUID);
    }

    public void forget(UUID uUID) {
        this.locales.remove(uUID);
    }

    public int getOverlayCount() {
        return this.overlayCount;
    }

    public void announce(UUID uUID, String text) {
        String text2;
        if ((text2 = this.normalize(text)) != null) {
            this.locales.put(uUID, text2);
        }
    }

    protected Object resolve(String text, String text2, boolean matches) {
        if (text != null && this.overlayCount != 0) {
            Object result;
            if ((result = this.lookup(text, text2, matches)) != null && result != MISSING) {
                return result;
            }
            int value;
            return (value = text.indexOf(95)) > 0
                            && (result = this.lookup(text.substring(0, value), text2, matches))
                                    != null
                            && result != MISSING
                    ? result
                    : null;
        } else {
            return null;
        }
    }

    private Object lookup(String text, String text2, boolean matches) {
        LocaleHandler.Entry entry = null;
        for (LocaleHandler.Entry entry2 : this.registered) {
            if (text2.startsWith(LocaleHandler.Entry.getTextString(entry2))) {
                entry = entry2;
                break;
            }
        }
        if (entry == null) {
            return null;
        }
        YamlDocument yamlDocument;
        if ((yamlDocument =
                        (YamlDocument) LocaleHandler.Entry.getYamlDocumentsByName(entry).get(text))
                == null) {
            return null;
        }
        String text3 = text2.substring(LocaleHandler.Entry.getTextString(entry).length());
        String text4 = text + '\u0000' + text2;
        Object result;
        if ((result = this.cache.get(text4)) != null) {
            return result;
        }
        Object result2;
        if (matches) {
            List values;
            result2 =
                    (values = yamlDocument.getStringList(text3)) != null && !values.isEmpty()
                            ? values
                            : MISSING;
        } else {
            String text5;
            result2 = (text5 = yamlDocument.getString(text3)) == null ? MISSING : text5;
        }
        this.cache.put(text4, result2);
        return result2;
    }

    protected String normalize(String text) {
        if (text == null) {
            return null;
        }
        String text2;
        return (text2 = text.trim().toLowerCase().replace('-', '_')).isEmpty() ? null : text2;
    }

    static class Entry {

        private final String text;

        private final String textString;

        private final Class<?> type;

        private final Map<String, YamlDocument> yamlDocumentsByName = new LinkedHashMap<>();

        private Entry(String text, String textString, Class<?> classValue) {
            this.text = text;
            this.textString = textString;
            this.type = classValue;
        }

        static Map<String, YamlDocument> getYamlDocumentsByName(LocaleHandler.Entry entry) {
            return entry.yamlDocumentsByName;
        }

        static String getText(LocaleHandler.Entry entry) {
            return entry.text;
        }

        static Class<?> getType(LocaleHandler.Entry entry) {
            return entry.type;
        }

        static String getTextString(LocaleHandler.Entry entry) {
            return entry.textString;
        }
    }
}
