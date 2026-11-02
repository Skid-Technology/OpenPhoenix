package xyz.refinedev.phoenix.utils.nametags;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.NameTagVisibility;

import xyz.refinedev.lib.caffeine.cache.Cache;
import xyz.refinedev.lib.caffeine.cache.Caffeine;
import xyz.refinedev.phoenix.utils.nametags.tag.PlayerTag;
import xyz.refinedev.phoenix.utils.nametags.tag.impl.DefaultPlayerTag;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public final class PlayerTagCache {

    private static final Cache<PlayerTagCache.TagKey, PlayerTag> CACHE =
            Caffeine.newBuilder()
                    .expireAfterAccess(10L, TimeUnit.MINUTES)
                    .maximumSize(10000L)
                    .build();

    public static PlayerTag of(String text, String text2, ChatColor chatColor, int value) {
        return of(text, text2, chatColor, value, NameTagVisibility.ALWAYS);
    }

    public static PlayerTag of(
            String text,
            String text2,
            ChatColor chatColor,
            int value,
            NameTagVisibility nameTagVisibility) {
        PlayerTagCache.TagKey tagKey =
                new PlayerTagCache.TagKey(text, text2, chatColor, nameTagVisibility, value);
        PlayerTag playerTag;
        if ((playerTag = (PlayerTag) CACHE.getIfPresent(tagKey)) == null) {
            playerTag =
                    new DefaultPlayerTag()
                            .prefix(text)
                            .suffix(text2)
                            .visibility(nameTagVisibility)
                            .priority(value);
            CACHE.put(tagKey, playerTag);
        }
        return playerTag;
    }

    private static final class TagKey {

        private final String prefix;

        private final String suffix;

        private final ChatColor color;

        private final NameTagVisibility visibility;

        private final int priority;

        private TagKey(
                String prefix,
                String suffix,
                ChatColor chatColor,
                NameTagVisibility nameTagVisibility,
                int priority) {
            this.prefix = prefix;
            this.suffix = suffix;
            this.color = chatColor;
            this.visibility = nameTagVisibility;
            this.priority = priority;
        }

        public String prefix() {
            return this.prefix;
        }

        public String suffix() {
            return this.suffix;
        }

        public ChatColor color() {
            return this.color;
        }

        public NameTagVisibility visibility() {
            return this.visibility;
        }

        public int priority() {
            return this.priority;
        }

        @Override
        public boolean equals(Object result) {
            if (result == this) {
                return true;
            } else if (result != null && result.getClass() == this.getClass()) {
                PlayerTagCache.TagKey tagKey = (PlayerTagCache.TagKey) result;
                return Objects.equals(this.prefix, tagKey.prefix)
                        && Objects.equals(this.suffix, tagKey.suffix)
                        && Objects.equals(this.color, tagKey.color)
                        && Objects.equals(this.visibility, tagKey.visibility)
                        && this.priority == tagKey.priority;
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                    this.prefix, this.suffix, this.color, this.visibility, this.priority);
        }

        @Override
        public String toString() {
            return "TagKey[prefix="
                    + this.prefix
                    + ", suffix="
                    + this.suffix
                    + ", color="
                    + this.color
                    + ", visibility="
                    + this.visibility
                    + ", priority="
                    + this.priority
                    + ']';
        }
    }
}
