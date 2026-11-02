package xyz.refinedev.phoenix.utils.file;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.ErrorReporter;
import xyz.refinedev.phoenix.utils.FastStreams;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Level;

public class ConfigFile extends YamlConfiguration {

    private File file;

    private JavaPlugin plugin;

    private String name;

    public ConfigFile(JavaPlugin javaPlugin, String name) {
        this.file = new File(javaPlugin.getDataFolder(), name);
        this.plugin = javaPlugin;
        this.name = name;
        if (!this.file.exists()) {
            javaPlugin.saveResource(name, false);
        }
        try {
            this.load(this.file);
        } catch (IOException | InvalidConfigurationException exception) {
            javaPlugin.getLogger().log(Level.SEVERE, ErrorReporter.formatString(exception));
        }
    }

    public ConfigFile(JavaPlugin javaPlugin, String text, String name) {
        this.plugin = javaPlugin;
        this.name = name;
        try {
            File file2 = new File(text);
            Field field;
            (field = JavaPlugin.class.getDeclaredField("dataFolder")).setAccessible(true);
            field.set(javaPlugin, file2);
        } catch (Exception exception) {
            javaPlugin.getLogger().log(Level.SEVERE, "Could not set dataFolder!");
        }
        this.file = new File(text, name);
        if (!this.file.exists()) {
            javaPlugin.saveResource(name, false);
        }
        try {
            this.load(this.file);
        } catch (IOException | InvalidConfigurationException exception2) {
            javaPlugin.getLogger().log(Level.SEVERE, "Could not load " + name + "!");
        }
    }

    public void load(String text) {
        this.file = new File(text, this.name);
        if (!this.file.exists()) {
            this.plugin.saveResource(this.name, false);
        }
        try {
            this.load(this.file);
        } catch (IOException | InvalidConfigurationException exception) {
            this.plugin.getLogger().log(Level.SEVERE, ErrorReporter.formatString(exception));
        }
    }

    public void load() {
        this.file = new File(this.plugin.getDataFolder(), this.name);
        if (!this.file.exists()) {
            this.plugin.saveResource(this.name, false);
        }
        try {
            this.load(this.file);
        } catch (IOException | InvalidConfigurationException exception) {
            this.plugin.getLogger().log(Level.SEVERE, ErrorReporter.formatString(exception));
        }
    }

    public void save() {
        try {
            this.save(this.file);
        } catch (IOException oException) {
            this.plugin.getLogger().log(Level.SEVERE, ErrorReporter.formatString(oException));
        }
    }

    public int getInt(String text) {
        return super.getInt(text, 0);
    }

    public double getDouble(String text) {
        return super.getDouble(text, 0.0);
    }

    public boolean getBoolean(String text) {
        return super.getBoolean(text, false);
    }

    public String getString(String text, boolean matches) {
        return super.getString(text, null);
    }

    public String getString(String text) {
        return Color.translate(
                super.getString(text, "String at path &7'" + text + "&7' not found."));
    }

    public List<String> getStringList(String text) {
        return FastStreams.mapArrayList(super.getStringList(text), Color::translate);
    }

    public List<String> getStringList(String text, boolean matches) {
        return !super.contains(text)
                ? null
                : FastStreams.mapArrayList(super.getStringList(text), Color::translate);
    }

    public File getFile() {
        return this.file;
    }
}
