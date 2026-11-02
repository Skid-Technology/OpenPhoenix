package xyz.refinedev.phoenix.permissible.database;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PermissionInfo {

    @SerializedName("permission")
    private final String permission;

    @SerializedName("plugin")
    private final String plugin;

    @SerializedName("description")
    private final String description;

    @SerializedName("commands")
    private final String[] commands;

    @SerializedName("source")
    private final String source;

    @SerializedName("placeholders")
    private String[] placeholders;

    public PermissionInfo(
            String permission, String plugin, String description, String[] texts, String source) {
        this.permission = permission;
        this.plugin = plugin;
        this.description = description;
        this.commands = texts;
        this.source = source;
        this.placeholders = substringsBetween(permission, "[", "]");
        this.placeholders = this.placeholders == null ? new String[0] : this.placeholders;
    }

    public boolean hasPlaceholders() {
        return this.placeholders.length != 0;
    }

    public boolean isThisPermission(String text) {
        if (text.equalsIgnoreCase(this.permission)) {
            return true;
        }
        if (!this.hasPlaceholders()) {
            return false;
        }
        String text2 = this.permission;
        String[] texts = this.placeholders;
        int value = this.placeholders.length;
        for (int index = 0; index < value; index++) {
            String text3 = texts[index];
            text2 = text2.replace("[" + text3 + "]", "$$$");
        }
        value = (texts = text2.split("[$$$]")).length;
        for (int index2 = 0; index2 < value; index2++) {
            String text4 = texts[index2];
            if (!text.contains(text4)) {
                return false;
            }
        }
        return true;
    }

    public static String[] substringsBetween(String text, String text2, String text3) {
        if (text != null && !text.isEmpty() && !text2.isEmpty() && !text3.isEmpty()) {
            int size = text.length();
            int size2 = text3.length();
            int size3 = text2.length();
            ArrayList<String> values = new ArrayList<>();
            int value = 0;
            int value2;
            while (value < size - size2 && (value2 = text.indexOf(text2, value)) >= 0) {
                value2 += size3;
                int value3;
                if ((value3 = text.indexOf(text3, value2)) < 0) {
                    break;
                }
                values.add(text.substring(value2, value3));
                value = value3 + size2;
            }
            return values.isEmpty() ? null : values.toArray(new String[0]);
        } else {
            return null;
        }
    }

    public String getPermission() {
        return this.permission;
    }

    public String getPlugin() {
        return this.plugin;
    }

    public String getDescription() {
        return this.description;
    }

    public String[] getCommands() {
        return this.commands;
    }

    public String getSource() {
        return this.source;
    }

    public String[] getPlaceholders() {
        return this.placeholders;
    }

    public PermissionInfo(
            String permission,
            String plugin,
            String description,
            String[] texts,
            String source,
            String[] texts2) {
        this.permission = permission;
        this.plugin = plugin;
        this.description = description;
        this.commands = texts;
        this.source = source;
        this.placeholders = texts2;
    }
}
