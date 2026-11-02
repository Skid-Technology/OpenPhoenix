package xyz.refinedev.phoenix.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Help;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Default;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Option;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.menu.impl.tags.CustomTagsAdminMenu;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.tag.CustomTagStatus;
import xyz.refinedev.phoenix.profile.tag.ICustomTag;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.PaginatedListClickable;
import xyz.refinedev.phoenix.utils.chat.Clickable;

import java.util.ArrayList;
import java.util.Iterator;

@Help
@Register(
        name = "customtagsadmin",
        aliases = {"customtagadmin", "cta"})
@Requires("core.command.customtagsadmin")
public class CustomtagsadminCommand {

    private final Phoenix plugin;

    @Command(name = "manage")
    @Requires("core.command.customtagsadmin.manage")
    public void manage(@Sender Player player) {
        this.plugin.getMenuHandler().openMenu(new CustomTagsAdminMenu(), player);
    }

    @Command(name = "list")
    @Requires("core.command.customtagsadmin.list")
    public void list(
            @Sender CommandSender sender,
            @Named("player") @Option({"disguise", "multiple"}) IProfile profile,
            @Default("1") @Named("page") int value) {
        PaginatedListClickable paginatedListClickable =
                new PaginatedListClickable(
                        "Custom Tags",
                        profile.getNameWithColorNoDisguise(),
                        "/customtagsadmin list " + profile.getName());
        ArrayList<Clickable> values = new ArrayList<>();
        Iterator<ICustomTag> iterator2 =
                this.plugin.getCustomTagHandler().getTags(profile.getUniqueId()).iterator();
        while (iterator2.hasNext()) {
            ICustomTag customTag;
            String text =
                    (customTag = (ICustomTag) iterator2.next()).getStatus()
                                    == CustomTagStatus.ACCEPTED
                            ? LocaleUtil.tr(sender, MenuConfig.CUSTOM_TAGS.STATUS_ACCEPTED)
                            : (customTag.getStatus() == CustomTagStatus.DENIED
                                    ? LocaleUtil.tr(sender, MenuConfig.CUSTOM_TAGS.STATUS_DENIED)
                                    : LocaleUtil.tr(
                                            sender, MenuConfig.CUSTOM_TAGS.STATUS_UNDECIDED));
            Clickable clickable =
                    new Clickable(
                            LocaleUtil.tr(sender, MessageConfig.CUSTOM_TAGS_ADMIN.FORMAT)
                                    .replace("<tag>", customTag.getDisplayName())
                                    .replace("<tagId>", customTag.getName())
                                    .replace("<status>", text));
            values.add(clickable);
        }
        paginatedListClickable.display(sender, value, values);
    }

    @Command(name = "check-tokens")
    @Requires("core.command.customtagsadmin.check-tokens")
    public void checkTokens(
            @Sender CommandSender sender,
            @Named("player") @Option({"disguise", "multiple"}) IProfile profile) {
        int intSetting = profile.getIntSetting("custom-tag-tokens", 0);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.CUSTOM_TAGS_ADMIN.CHECK)
                                .replace("<target>", profile.getNameWithColorNoDisguise())
                                .replace("<amount>", intSetting + "")));
    }

    @Command(name = "add-tokens")
    @Requires("core.command.customtagsadmin.add-tokens")
    public void addTokens(
            @Sender CommandSender sender,
            @Named("player") @Option({"disguise", "multiple"}) IProfile profile,
            @Named("amount") int value) {
        int value2 = profile.getIntSetting("custom-tag-tokens", 0) + value;
        profile.updateMetadata("custom-tag-tokens", String.valueOf(value2), true);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.CUSTOM_TAGS_ADMIN.ADDED_TOKENS)
                                .replace("<target>", profile.getNameWithColorNoDisguise())
                                .replace("<amount>", value + "")
                                .replace("<newTokens>", value2 + "")));
    }

    @Command(name = "remove-tokens")
    @Requires("core.command.customtagsadmin.remove-tokens")
    public void removeTokens(
            @Sender CommandSender sender,
            @Named("player") @Option({"disguise", "multiple"}) IProfile profile,
            @Named("amount") int value) {
        int value2;
        if ((value2 = profile.getIntSetting("custom-tag-tokens", 0) - value) < 0) {
            throw new ExitMessage(
                    LocaleUtil.tr(sender, MessageConfig.CUSTOM_TAGS_ADMIN.NOT_ENOUGH_TOKENS)
                            .replace("<target>", profile.getNameWithColorNoDisguise()));
        }
        profile.updateMetadata("custom-tag-tokens", String.valueOf(value2), true);
        sender.sendMessage(
                Color.translate(
                        LocaleUtil.tr(sender, MessageConfig.CUSTOM_TAGS_ADMIN.REMOVED_TOKENS)
                                .replace("<target>", profile.getNameWithColorNoDisguise())
                                .replace("<amount>", value + "")
                                .replace("<leftTokens>", value2 + "")));
    }

    public CustomtagsadminCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
