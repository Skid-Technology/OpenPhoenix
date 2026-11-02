package xyz.refinedev.phoenix.menu.impl;

import org.bukkit.ChatColor;

import xyz.refinedev.phoenix.config.MenuConfig;

public enum SocialMediaType {
    YOUTUBE(
            MenuConfig.SOCIAL_MEDIA.YOUTUBE_NAME,
            ChatColor.RED,
            "^https?:\\/\\/(www\\.)?youtube\\.com\\/(channel\\/UC[\\w-]{21}[AQgw]|(c\\/|user\\/)?[\\w@-]+)$",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTI0ZTgyNWIyNWZmNzkzOGI1OGFmNTJmNThlNGQ1NGVjNTE0M2JkNWExYjQ4NGYxZmY5ZGQ2YmEwYWNhOWRjYiJ9fX0="),
    DISCORD(
            MenuConfig.SOCIAL_MEDIA.DISCORD_NAME,
            ChatColor.DARK_AQUA,
            "^[A-Za-z0-9]{3,32}$",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDJmODc2YzY1Y2M1ZGIwYTYwMTZiNWM2NjU5Y2YwNTYzMGU3NDZmYTUwOTJmMDA1YzkxNzBlNzJiNTBkYzYwZCJ9fX0="),
    INSTAGRAM(
            MenuConfig.SOCIAL_MEDIA.INSTAGRAM_NAME,
            ChatColor.LIGHT_PURPLE,
            "(?:(?:http|https):\\/\\/)?(?:www.)?(?:instagram.com|instagr.am|instagr.com)\\/(.+)",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTBkNDY0MTg2ZTFhNTBkZGFhMTRiZTIyNTk2MTFhNGU4NDU4NTE1YTUzNjdhOTM4OWE5Y2M3Yzg5Yzk0YTkzYiJ9fX0="),
    TWITTER(
            MenuConfig.SOCIAL_MEDIA.TWITTER_NAME,
            ChatColor.AQUA,
            "(https:\\/\\/(twitter.com|x.com)\\/(?![a-zA-Z0-9_]+\\/)([a-zA-Z0-9_]+))",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjQxOTY2MjM1NzVkNmM2MzdlZTc3ZWIyODEwYzRkMDI1MTUzMDhjYmIxYTQ2ZGNjMDNlZDAyMjY5MmI1N2U0YiJ9fX0=");

    private final String name;

    private final ChatColor color;

    private final String regex;

    private final String texture;

    public String getName() {
        return this.name;
    }

    public ChatColor getColor() {
        return this.color;
    }

    public String getRegex() {
        return this.regex;
    }

    public String getTexture() {
        return this.texture;
    }

    SocialMediaType(String name, ChatColor chatColor, String regex, String texture) {
        this.name = name;
        this.color = chatColor;
        this.regex = regex;
        this.texture = texture;
    }
}
