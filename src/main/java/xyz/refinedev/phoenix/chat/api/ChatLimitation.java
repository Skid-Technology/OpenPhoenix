package xyz.refinedev.phoenix.chat.api;

import org.bukkit.entity.Player;

import java.util.function.BiFunction;

public class ChatLimitation {

    private final String name;

    private final int priority;

    private final BiFunction<Player, String, Boolean> function;

    public String getName() {
        return this.name;
    }

    public int getPriority() {
        return this.priority;
    }

    public BiFunction<Player, String, Boolean> getFunction() {
        return this.function;
    }

    public ChatLimitation(
            String name, int priority, BiFunction<Player, String, Boolean> biFunction) {
        this.name = name;
        this.priority = priority;
        this.function = biFunction;
    }
}
