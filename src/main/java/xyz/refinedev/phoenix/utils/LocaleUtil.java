package xyz.refinedev.phoenix.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.bukkit.actor.BukkitActor;
import xyz.refinedev.lib.commands.execution.CommandExecution;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.handler.BukkitLocaleHandler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class LocaleUtil {

    private LocaleUtil() {}

    public static String tr(CommandSender sender, String text) {
        BukkitLocaleHandler bukkitLocaleHandler;
        return (bukkitLocaleHandler = handler()) == null
                ? text
                : bukkitLocaleHandler.tr(sender, text);
    }

    public static List<String> tr(CommandSender sender, List<String> values) {
        BukkitLocaleHandler bukkitLocaleHandler;
        return (bukkitLocaleHandler = handler()) == null
                ? values
                : bukkitLocaleHandler.tr(sender, values);
    }

    public static String tr(CommandExecution commandExecution, String text) {
        return commandExecution != null && commandExecution.getActor() instanceof BukkitActor
                ? tr(((BukkitActor) commandExecution.getActor()).getSender(), text)
                : text;
    }

    public static String pathOf(Object result) {
        BukkitLocaleHandler bukkitLocaleHandler;
        return (bukkitLocaleHandler = handler()) == null
                ? null
                : bukkitLocaleHandler.pathOf(result);
    }

    public static String translation(CommandSender sender, String text) {
        BukkitLocaleHandler bukkitLocaleHandler;
        return (bukkitLocaleHandler = handler()) != null && sender instanceof Player
                ? bukkitLocaleHandler.getTranslation((Player) sender, text)
                : null;
    }

    public static Map<String, String> replacements(String... text) {
        LinkedHashMap<String, String> entries = new LinkedHashMap<>();
        for (byte index = 0; index + 1 < text.length; index += 2) {
            entries.put(text[index], text[index + 1] == null ? "" : text[index + 1]);
        }
        return entries;
    }

    private static BukkitLocaleHandler handler() {
        Phoenix phoenix;
        return (phoenix = Phoenix.getInstance()) == null ? null : phoenix.getLocaleHandler();
    }
}
