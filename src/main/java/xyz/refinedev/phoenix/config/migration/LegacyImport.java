package xyz.refinedev.phoenix.config.migration;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class LegacyImport {

    private final File folder;

    private final YamlConfiguration legacy;

    private final Map<String, YamlConfiguration> targets = new LinkedHashMap<>();

    private int copied;

    private static final String COULD_NOT_CREATE_TEXT = "Could not create ";

    LegacyImport(File file, File file2) {
        this.folder = file;
        this.legacy = YamlConfiguration.loadConfiguration(file2);
    }

    boolean has(String text) {
        return this.legacy.contains(text);
    }

    String string(String text, String text2) {
        return this.legacy.getString(text, text2);
    }

    ConfigurationSection section(String text) {
        return this.legacy.getConfigurationSection(text);
    }

    void copy(String text, String text2, String text3) {
        this.copy(text, text2, text3, Collections.emptySet());
    }

    void copy(String text, String text2, String text3, Set<String> values) {
        if (this.legacy.isConfigurationSection(text)) {
            ConfigurationSection section;
            for (String text4 :
                    (section = this.legacy.getConfigurationSection(text)).getKeys(true)) {
                if (!section.isConfigurationSection(text4) && !values.contains(text4)) {
                    this.set(text2, text3 + "." + text4, section.get(text4));
                }
            }
        } else if (this.legacy.contains(text)) {
            this.set(text2, text3, this.legacy.get(text));
        }
    }

    void set(String text, String text2, Object result) {
        this.target(text).set(text2, result);
        this.copied++;
    }

    String current(String text, String text2, String text3) {
        return this.target(text).getString(text2, text3);
    }

    private YamlConfiguration target(String text) {
        YamlConfiguration yamlConfiguration;
        if ((yamlConfiguration = this.targets.get(text)) != null) {
            return yamlConfiguration;
        }
        yamlConfiguration = YamlConfiguration.loadConfiguration(new File(this.folder, text));
        this.targets.put(text, yamlConfiguration);
        return yamlConfiguration;
    }

    void save() throws IOException {
        if (this.copied != 0) {
            if (!this.folder.exists() && !this.folder.mkdirs()) {
                throw new IOException(COULD_NOT_CREATE_TEXT + this.folder.getPath());
            }
            Iterator<Entry<String, YamlConfiguration>> iterator2 =
                    this.targets.entrySet().iterator();
            while (iterator2.hasNext()) {
                Entry entry;
                ((YamlConfiguration) (entry = (Entry) iterator2.next()).getValue())
                        .save(new File(this.folder, (String) entry.getKey()));
            }
        }
    }

    public int getCopied() {
        return this.copied;
    }
}
