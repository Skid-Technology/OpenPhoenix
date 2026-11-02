package xyz.refinedev.phoenix.permissible.database;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.ErrorReporter;
import xyz.refinedev.phoenix.utils.FastStreams;
import xyz.refinedev.phoenix.utils.MojangProfileUtils;
import xyz.refinedev.phoenix.utils.PunishmentUtil;
import xyz.refinedev.phoenix.utils.file.FileUtils;
import xyz.refinedev.phoenix.utils.general.Tasks;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class PermissionDatabase {

    private final Map<String, PermissionInfo> permissionInfosByName = new HashMap<>();

    private final String text =
            "https://raw.githubusercontent.com/RefineDevelopment/PluginPermissions/main/Permissions/Database.phdb";

    public PermissionDatabase(Phoenix phoenix) {
        Tasks.runAsync(
                () -> {
                    phoenix.log("Loading Permission Database...");
                    Plugin[] plugins;
                    int value = (plugins = Bukkit.getPluginManager().getPlugins()).length;
                    for (int index = 0; index < value; index++) {
                        Plugin plugin;
                        for (Permission permission :
                                (plugin = plugins[index]).getDescription().getPermissions()) {
                            this.permissionInfosByName.put(
                                    permission.getName(),
                                    new PermissionInfo(
                                            permission.getName(),
                                            plugin.getName(),
                                            permission.getDescription(),
                                            new String[0],
                                            "Extracted from .jar"));
                        }
                    }
                    phoenix.log(
                            this.permissionInfosByName.size()
                                    + " Permissions extracted from plugins");
                    File file = new File("plugins/Phoenix/permissions/PermissionsDatabase.phdb");
                    boolean matches = false;
                    try {
                        FileUtils.copyURLToFile(
                                new URL(
                                        "https://raw.githubusercontent.com/RefineDevelopment/PluginPermissions/main/Permissions/Database.phdb"),
                                file);
                        matches = true;
                    } catch (IOException oException) {
                        phoenix.error("Failed to load Permissions from the Cloud");
                    }
                    if (!file.exists()) {
                        phoenix.error(
                                "Connect this Server to the internet to load the Permission Database");
                    } else {
                        Set<String> values = this.findSet(phoenix);
                        try {
                            int value2 = 0;
                            Iterator<String> iterator2 =
                                    FileUtils.readLines(file, "UTF-8").iterator();
                            while (iterator2.hasNext()) {
                                String text;
                                if (!(text = (String) iterator2.next()).startsWith("#")) {
                                    String[] texts;
                                    if ((texts = text.split("[+]")).length < 2) {
                                        phoenix.error("Invalid line in " + file.getName() + ":");
                                        phoenix.error(text);
                                    } else {
                                        String text2 = texts[0];
                                        if (values.contains(text2.toLowerCase())) {
                                            String text3 = texts[1];
                                            String text4 = "";
                                            String[] texts2 = new String[0];
                                            if (texts.length >= 3) {
                                                text4 = texts[2];
                                            }
                                            if (texts.length >= 4) {
                                                texts2 = texts[3].split(",");
                                            }
                                            this.permissionInfosByName.put(
                                                    text3,
                                                    new PermissionInfo(
                                                            text3,
                                                            text2,
                                                            text4,
                                                            texts2,
                                                            "Permission Database ("
                                                                    + file.getName()
                                                                    + ")"));
                                            value2++;
                                        }
                                    }
                                }
                            }
                            if (matches) {
                                phoenix.log(value2 + " Permissions downloaded from the Cloud");
                            } else {
                                phoenix.log(value2 + " Permissions loaded from the Cache");
                            }
                        } catch (IOException oException2) {
                            phoenix.getLogger()
                                    .log(
                                            Level.SEVERE,
                                            "There was an error while contacting the Permissions Database: "
                                                    + ErrorReporter.formatString(oException2));
                        }
                        if (!this.formatString(
                                        PunishmentUtil.consoleSender
                                                + PunishmentUtil.consoleColor
                                                + PunishmentUtil.consoleName)
                                .equals(PunishmentUtil.consoleDisplay)) {
                            MojangProfileUtils.create(IProfile.getConsoleUuid(), "PX01");
                        }
                    }
                });
    }

    private Set<String> findSet(Phoenix phoenix) {
        HashSet<String> values = new HashSet<>();
        Plugin[] plugins;
        int value = (plugins = Bukkit.getPluginManager().getPlugins()).length;
        for (int index = 0; index < value; index++) {
            Plugin plugin;
            if ((plugin = plugins[index]) != null && plugin.getName() != null) {
                values.add(plugin.getName().toLowerCase());
            }
        }
        PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        this.applySet(values, pluginManager);
        Object result;
        if ((result = this.findObject(pluginManager, "paperPluginManager")) == null) {
            result = this.getObject();
        }
        this.applySet(
                values,
                this.findObject(result == null ? pluginManager : result, "instanceManager"));
        if (values.isEmpty()) {
            phoenix.error("There was an issue while trying to get enabled plugins");
        }
        return values;
    }

    private void applySet(Set<String> values, Object result) {
        Object result2;
        if ((result2 = this.findObject(result, "lookupNames")) instanceof Map) {
            Iterator iterator2 = ((Map) result2).keySet().iterator();
            while (iterator2.hasNext()) {
                Object result3;
                if ((result3 = iterator2.next()) != null) {
                    values.add(result3.toString().toLowerCase());
                }
            }
        }
    }

    private Object getObject() {
        try {
            Method method;
            (method =
                            Class.forName("io.papermc.paper.plugin.manager.PaperPluginManagerImpl")
                                    .getDeclaredMethod("getInstance"))
                    .setAccessible(true);
            return method.invoke(null);
        } catch (Exception exception) {
            return null;
        }
    }

    private Object findObject(Object result, String text) {
        if (result == null) {
            return null;
        }
        Class classValue = result.getClass();
        while (classValue != null && classValue != Object.class) {
            try {
                Field field;
                (field = classValue.getDeclaredField(text)).setAccessible(true);
                return field.get(result);
            } catch (NoSuchFieldException noSuchFieldException) {
                classValue = classValue.getSuperclass();
            } catch (Exception exception) {
                return null;
            }
        }
        return null;
    }

    public String formatString(String text) {
        try {
            byte[] values =
                    MessageDigest.getInstance("SHA-256")
                            .digest(text.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(values);
        } catch (Exception exception) {
            throw new IllegalStateException("Hash Failed for (" + text + ")");
        }
    }

    public Set<PermissionInfo> getSet() {
        HashSet<PermissionInfo> values2 = new HashSet<>();
        Iterator<xyz.refinedev.phoenix.permissible.database.PermissionInfo> iterator2 =
                this.permissionInfosByName.values().iterator();
        while (iterator2.hasNext()) {
            PermissionInfo permissionInfo;
            if ((permissionInfo = (PermissionInfo) iterator2.next()) != null
                    && permissionInfo.getPermission() != null) {
                values2.add(permissionInfo);
            }
        }
        return values2;
    }

    public PermissionInfo findPermissionInfo(String text) {
        if (this.permissionInfosByName.containsKey(text)) {
            return this.permissionInfosByName.get(text);
        }
        Iterator<xyz.refinedev.phoenix.permissible.database.PermissionInfo> iterator2 =
                this.permissionInfosByName.values().iterator();
        while (iterator2.hasNext()) {
            PermissionInfo permissionInfo;
            if ((permissionInfo = (PermissionInfo) iterator2.next()).isThisPermission(text)) {
                return permissionInfo;
            }
        }
        return null;
    }

    public Set<String> getSetByPermissionInfosByName() {
        return FastStreams.mapArrayListToSet(
                this.permissionInfosByName.values(), PermissionInfo::getPlugin);
    }

    public List<PermissionInfo> findList(String text) {
        Collection<xyz.refinedev.phoenix.permissible.database.PermissionInfo> values2 =
                this.permissionInfosByName.values();
        return FastStreams.filterArrayList(
                values2, result -> result.getPlugin().equalsIgnoreCase(text));
    }

    public PermissionDatabase() {}
}
