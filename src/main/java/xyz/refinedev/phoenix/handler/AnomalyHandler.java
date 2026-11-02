package xyz.refinedev.phoenix.handler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.general.Tasks;

import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.UnaryOperator;

public class AnomalyHandler {

    private final Phoenix plugin;

    private final Map<UUID, Deque<Long>> punishments = new ConcurrentHashMap<>();

    private final Map<UUID, Long> trippedUntil = new ConcurrentHashMap<>();

    private static final long time = 1000L;

    public AnomalyHandler(Phoenix phoenix) {
        this.plugin = phoenix;
    }

    public void recordPunishment(Player player) {
        if (SettingsConfig.STAFF_PROTECTION.ENABLED) {
            long now = System.currentTimeMillis();
            long value = Math.max(1, SettingsConfig.STAFF_PROTECTION.WINDOW_SECONDS) * time;
            Deque values;
            (values =
                            this.punishments.computeIfAbsent(
                                    player.getUniqueId(), result -> new ConcurrentLinkedDeque<>()))
                    .addLast(now);
            Long value2;
            while ((value2 = (Long) values.peekFirst()) != null && value2 < now - value) {
                values.pollFirst();
            }
            if (values.size()
                    >= Math.max(1, SettingsConfig.STAFF_PROTECTION.PUNISHMENT_THRESHOLD)) {
                Long value3;
                if ((value3 = this.trippedUntil.get(player.getUniqueId())) == null
                        || value3 <= now) {
                    this.trippedUntil.put(player.getUniqueId(), now + value);
                    this.trip(player, values.size());
                }
            }
        }
    }

    public void alertPanic(Player player) {
        this.alert(
                MessageConfig.STAFF_PROTECTION.PANIC_ALERT,
                result -> result.replace("<staff>", player.getName()));
        this.dispatch(SettingsConfig.STAFF_PROTECTION.PANIC_COMMANDS, player.getName());
    }

    public void forget(UUID uUID) {
        this.punishments.remove(uUID);
    }

    private void trip(Player player, int value) {
        this.alert(
                MessageConfig.STAFF_PROTECTION.ALERT,
                result ->
                        result.replace("<staff>", player.getName())
                                .replace("<count>", String.valueOf(value))
                                .replace(
                                        "<window>",
                                        String.valueOf(
                                                SettingsConfig.STAFF_PROTECTION.WINDOW_SECONDS)));
        this.dispatch(SettingsConfig.STAFF_PROTECTION.TRIGGER_COMMANDS, player.getName());
    }

    private void alert(String text, UnaryOperator<String> unaryOperator) {
        this.plugin
                .getLogger()
                .warning(Color.stripColor(Color.translate(unaryOperator.apply(text))));
        Iterator iterator2 = Bukkit.getOnlinePlayers().iterator();
        while (iterator2.hasNext()) {
            Player player;
            if ((player = (Player) iterator2.next())
                    .hasPermission(SettingsConfig.STAFF_PROTECTION.ALERT_PERMISSION)) {
                player.sendMessage(
                        Color.translate(unaryOperator.apply(LocaleUtil.tr(player, text))));
            }
        }
    }

    private void dispatch(List<String> values, String text) {
        if (!values.isEmpty()) {
            Tasks.run(
                    () -> {
                        Iterator<String> iterator2 = values.iterator();
                        while (iterator2.hasNext()) {
                            String text2;
                            if ((text2 = (String) iterator2.next()) != null
                                    && !text2.trim().isEmpty()) {
                                Bukkit.dispatchCommand(
                                        Bukkit.getConsoleSender(), text2.replace("<player>", text));
                            }
                        }
                    });
        }
    }
}
