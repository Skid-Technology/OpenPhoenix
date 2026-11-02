package xyz.refinedev.phoenix.listener;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.SettingsConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TabCompletionListener implements Listener {

    private final Phoenix plugin;

    private static final String PERMISSION = "core.bypass.tabcomplete";

    public void handleTabCompleteEvent(TabCompleteEvent tabCompleteEvent) {
        if (tabCompleteEvent.getSender() instanceof Player) {
            Player player;
            if (!(player = (Player) tabCompleteEvent.getSender()).hasPermission(PERMISSION)) {
                String buffer = tabCompleteEvent.getBuffer();
                String[] texts = tabCompleteEvent.getBuffer().replaceFirst("/", "").split(" ");
                if (!buffer.equals("/") && texts.length != 0) {
                    String text;
                    if ((text = texts[0]).contains(":")) {
                        int value = text.indexOf(":");
                        text = text.substring(0, value);
                    }
                    Command command;
                    if ((command =
                                    this.plugin
                                            .getCommandHandler()
                                            .getRegistry()
                                            .getCommandMap()
                                            .getCommand(text))
                            == null) {
                        if (this.matchesEnabled(text)) {
                            tabCompleteEvent.setCancelled(true);
                        } else {
                            Stream<Command> stream2 =
                                    this.plugin
                                            .getCommandHandler()
                                            .getRegistry()
                                            .getKnownCommands()
                                            .values()
                                            .stream();
                            Player player2 = player;
                            Set values2 =
                                    stream2.filter(result -> result.testPermissionSilent(player2))
                                            .map(result2 -> "/" + result2.getName().toLowerCase())
                                            .collect(Collectors.toSet());
                            tabCompleteEvent.setCompletions(new ArrayList(values2));
                        }
                    } else if (this.matchesEnabled(text) || !command.testPermissionSilent(player)) {
                        tabCompleteEvent.setCompletions(Collections.emptyList());
                    }
                } else {
                    Stream<Command> stream3 =
                            this.plugin
                                    .getCommandHandler()
                                    .getRegistry()
                                    .getKnownCommands()
                                    .values()
                                    .stream();
                    Player player3 = player;
                    Set values3 =
                            stream3.filter(result -> result.testPermissionSilent(player3))
                                    .map(result2 -> "/" + result2.getName().toLowerCase())
                                    .collect(Collectors.toSet());
                    for (String text2 : SettingsConfig.SERVER.COMMANDS.BLOCKED_COMMANDS) {
                        text2 = "/" + text2.toLowerCase();
                        if (values3.contains(text2)) {
                            values3.remove(text2);
                        }
                    }
                    tabCompleteEvent.setCompletions(new ArrayList(values3));
                }
            }
        }
    }

    private boolean matchesEnabled(String text) {
        return SettingsConfig.SERVER.COMMANDS.BLOCKED_COMMANDS.contains(text.toLowerCase());
    }

    public TabCompletionListener(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
