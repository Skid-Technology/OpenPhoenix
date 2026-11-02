package xyz.refinedev.phoenix.utils.nametags.tag;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.NameTagVisibility;

import xyz.refinedev.lib.jetbrains.NotNull;
import xyz.refinedev.lib.jetbrains.Nullable;

public interface PlayerTag {

    PlayerTag DEFAULT = new PlayerTag.DefaultTag();

    static PlayerTag defaultTag() {
        return DEFAULT;
    }

    @NotNull
    String prefix();

    @NotNull
    String suffix();

    NameTagVisibility visibility();

    ChatColor color();

    int priority();

    class DefaultTag implements PlayerTag {

        @NotNull
        @Override
        public String prefix() {
            return "";
        }

        @NotNull
        @Override
        public String suffix() {
            return "";
        }

        @Nullable
        @Override
        public ChatColor color() {
            return null;
        }

        @Override
        public NameTagVisibility visibility() {
            return NameTagVisibility.ALWAYS;
        }

        @Override
        public int priority() {
            return 0;
        }
    }
}
