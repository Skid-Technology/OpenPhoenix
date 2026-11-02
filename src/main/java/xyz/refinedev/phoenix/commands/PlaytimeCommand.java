package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Help;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Option;
import xyz.refinedev.lib.commands.annotation.parameter.Optional;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.commands.api.annotations.DurationInput;
import xyz.refinedev.phoenix.commands.provider.DurationProvider;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.login.ILogin;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.Duration;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Help
@Register(name = "playtime", aliases = "pt")
public class PlaytimeCommand {

    private final Phoenix plugin;

    @Command(name = "", async = true)
    public void execute(
            @Sender Player player, @Optional @Option("coverdisguise") IProfile profile) {
        if (profile != null) {
            List<ILogin> values = this.findList(profile.getUuid(), "perm");
            long value = 0L;
            for (ILogin login : values) {
                value += login.getDuration();
            }
            for (String text : LocaleUtil.tr(player, MessageConfig.PLAYTIME.PLAYTIME_OTHER)) {
                player.sendMessage(
                        Color.translate(
                                text.replace("<player>", profile.getNameWithColor())
                                        .replace("<playtime>", DateUtils.formatTimeMillis(value))
                                        .replace("<logins>", String.valueOf(values.size()))));
            }
        } else {
            List<ILogin> values2 = this.findList(player.getUniqueId(), "perm");
            long value2 = 0L;
            for (ILogin login2 : values2) {
                value2 += login2.getDuration();
            }
            for (String text2 : LocaleUtil.tr(player, MessageConfig.PLAYTIME.PLAYTIME_SELF)) {
                player.sendMessage(
                        Color.translate(
                                text2.replace("<playtime>", DateUtils.formatTimeMillis(value2))
                                        .replace("<logins>", String.valueOf(values2.size()))));
            }
        }
    }

    @Command(name = "weekly", async = true)
    public void weekly(@Sender Player player, @Optional @Option("coverdisguise") IProfile profile) {
        if (profile != null) {
            List<ILogin> values = this.findList(profile.getUuid(), "7d");
            long value = 0L;
            for (ILogin login : values) {
                value += login.getDuration();
            }
            for (String text : LocaleUtil.tr(player, MessageConfig.PLAYTIME.PLAYTIME_OTHER)) {
                player.sendMessage(
                        Color.translate(
                                text.replace("<player>", profile.getNameWithColor())
                                        .replace("<playtime>", DateUtils.formatTimeMillis(value))
                                        .replace("<logins>", String.valueOf(values.size()))));
            }
        } else {
            List<ILogin> values2 = this.findList(player.getUniqueId(), "7d");
            long value2 = 0L;
            for (ILogin login2 : values2) {
                value2 += login2.getDuration();
            }
            for (String text2 : LocaleUtil.tr(player, MessageConfig.PLAYTIME.PLAYTIME_SELF)) {
                player.sendMessage(
                        Color.translate(
                                text2.replace("<playtime>", DateUtils.formatTimeMillis(value2))
                                        .replace("<logins>", String.valueOf(values2.size()))));
            }
        }
    }

    @Command(name = "daily", async = true)
    public void daily(@Sender Player player, @Optional @Option("coverdisguise") IProfile profile) {
        if (profile != null) {
            List<ILogin> values = this.findList(profile.getUuid(), "1d");
            long value = 0L;
            for (ILogin login : values) {
                value += login.getDuration();
            }
            for (String text : LocaleUtil.tr(player, MessageConfig.PLAYTIME.PLAYTIME_OTHER)) {
                player.sendMessage(
                        Color.translate(
                                text.replace("<player>", profile.getNameWithColor())
                                        .replace("<playtime>", DateUtils.formatTimeMillis(value))
                                        .replace("<logins>", String.valueOf(values.size()))));
            }
        } else {
            List<ILogin> values2 = this.findList(player.getUniqueId(), "1d");
            long value2 = 0L;
            for (ILogin login2 : values2) {
                value2 += login2.getDuration();
            }
            for (String text2 : LocaleUtil.tr(player, MessageConfig.PLAYTIME.PLAYTIME_SELF)) {
                player.sendMessage(
                        Color.translate(
                                text2.replace("<playtime>", DateUtils.formatTimeMillis(value2))
                                        .replace("<logins>", String.valueOf(values2.size()))));
            }
        }
    }

    @Command(name = "monthly", async = true)
    public void monthly(
            @Sender Player player, @Optional @Option("coverdisguise") IProfile profile) {
        if (profile != null) {
            List<ILogin> values = this.findList(profile.getUuid(), "30d");
            long value = 0L;
            for (ILogin login : values) {
                value += login.getDuration();
            }
            for (String text : LocaleUtil.tr(player, MessageConfig.PLAYTIME.PLAYTIME_OTHER)) {
                player.sendMessage(
                        Color.translate(
                                text.replace("<player>", profile.getNameWithColor())
                                        .replace("<playtime>", DateUtils.formatTimeMillis(value))
                                        .replace("<logins>", String.valueOf(values.size()))));
            }
        } else {
            List<ILogin> values2 = this.findList(player.getUniqueId(), "30d");
            long value2 = 0L;
            for (ILogin login2 : values2) {
                value2 += login2.getDuration();
            }
            for (String text2 : LocaleUtil.tr(player, MessageConfig.PLAYTIME.PLAYTIME_SELF)) {
                player.sendMessage(
                        Color.translate(
                                text2.replace("<playtime>", DateUtils.formatTimeMillis(value2))
                                        .replace("<logins>", String.valueOf(values2.size()))));
            }
        }
    }

    @Command(name = "custom", async = true)
    public void custom(
            @Sender Player player,
            @Optional @Option("coverdisguise") IProfile profile,
            @Optional @Named("since") @DurationInput String text) {
        if (text != null) {
            DurationProvider.tr(player, text, true);
        }
        if (profile != null) {
            List<ILogin> values = this.findList(profile.getUuid(), text);
            long value = 0L;
            for (ILogin login : values) {
                value += login.getDuration();
            }
            for (String text2 : LocaleUtil.tr(player, MessageConfig.PLAYTIME.PLAYTIME_OTHER)) {
                player.sendMessage(
                        Color.translate(
                                text2.replace("<player>", profile.getNameWithColor())
                                        .replace("<playtime>", DateUtils.formatTimeMillis(value))
                                        .replace("<logins>", String.valueOf(values.size()))));
            }
        } else {
            List<ILogin> values2 = this.findList(player.getUniqueId(), text);
            long value2 = 0L;
            for (ILogin login2 : values2) {
                value2 += login2.getDuration();
            }
            for (String text3 : LocaleUtil.tr(player, MessageConfig.PLAYTIME.PLAYTIME_SELF)) {
                player.sendMessage(
                        Color.translate(
                                text3.replace("<playtime>", DateUtils.formatTimeMillis(value2))
                                        .replace("<logins>", String.valueOf(values2.size()))));
            }
        }
    }

    private List<ILogin> findList(UUID uUID, String text) {
        List<ILogin> logins = this.plugin.getLoginHandler().getLogins(uUID);
        if (text == null) {
            return logins;
        }
        ArrayList<ILogin> values = new ArrayList<>();
        Duration duration;
        long value =
                (duration = Duration.of(text)) != null && !duration.isEnabled()
                        ? System.currentTimeMillis() - duration.getTimeTime()
                        : -5L;
        for (ILogin login : logins) {
            if (value == -5L || login.getLogin() >= value) {
                values.add(login);
            }
        }
        return values;
    }

    public PlaytimeCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
