package xyz.refinedev.phoenix.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Help;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Default;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Option;
import xyz.refinedev.lib.commands.annotation.parameter.Text;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.profile.notes.INote;
import xyz.refinedev.phoenix.profile.notes.Note;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.PaginatedListClickable;
import xyz.refinedev.phoenix.utils.chat.Clickable;

import java.util.ArrayList;
import java.util.UUID;

@Help
@Register(name = "notes", aliases = "note")
@Requires("core.command.notes")
public class NotesCommand {

    @Command(name = "")
    public void execute(
            @Sender CommandSender sender,
            @Option({"disguise", "multiple"}) IProfile profile,
            @Default("1") @Named("page") int value) {
        ArrayList<Clickable> values = new ArrayList<>();
        for (INote note : profile.getNotes()) {
            Clickable clickable =
                    new Clickable(
                            LocaleUtil.tr(sender, MessageConfig.NOTES.FORMAT)
                                    .replace("<issuedBy>", note.getNiceIssuedBy())
                                    .replace("<reason>", note.getNote())
                                    .replace("<date>", DateUtils.getDate(note.getIssuedAt())));
            if (sender.hasPermission("core.command.notes.remove")) {
                clickable.add(
                        Color.translate(LocaleUtil.tr(sender, MessageConfig.NOTES.REMOVE_X)),
                        Color.translate(LocaleUtil.tr(sender, MessageConfig.NOTES.HOVER_REM0VE)),
                        "/note remove " + profile.getName() + " " + note.getUuid().toString());
            }
            values.add(clickable);
        }
        new PaginatedListClickable(
                        "Notes",
                        profile.getNameWithColorNoDisguise(),
                        "/notes " + profile.getName())
                .display(sender, value, values);
    }

    @Command(name = "add")
    @Requires("core.command.notes.add")
    public void add(
            @Sender CommandSender sender,
            @Option({"disguise", "create", "multiple"}) IProfile profile,
            @Named("note") @Text String text) {
        Note note;
        (note = new Note()).setIssuedAt(System.currentTimeMillis());
        note.setIssuedBy(
                sender instanceof Player
                        ? ((Player) sender).getUniqueId()
                        : IProfile.getConsoleUuid());
        note.setNote(text);
        profile.getNotes().add(note);
        profile.saveAndUpdate();
        for (String text2 : LocaleUtil.tr(sender, MessageConfig.NOTES.ADDED)) {
            sender.sendMessage(
                    Color.translate(
                            text2.replace("<player>", profile.getNameWithColorNoDisguise())
                                    .replace("<note>", text)
                                    .replace("<id>", note.getUuid().toString())));
        }
    }

    @Command(name = "remove")
    @Requires("core.command.notes.remove")
    public void remove(
            @Sender CommandSender sender,
            @Option({"disguise", "multiple"}) IProfile profile,
            @Named("id") String text) {
        try {
            UUID.fromString(text);
        } catch (Exception exception) {
            throw new ExitMessage(LocaleUtil.tr(sender, MessageConfig.NOTES.DOES_NOT_EXIST));
        }
        INote note;
        if ((note = profile.getNote(UUID.fromString(text))) == null) {
            throw new ExitMessage(LocaleUtil.tr(sender, MessageConfig.NOTES.DOES_NOT_EXIST));
        }
        profile.getNotes().remove(note);
        profile.saveAndUpdate();
        for (String text2 : LocaleUtil.tr(sender, MessageConfig.NOTES.REMOVED)) {
            sender.sendMessage(
                    Color.translate(
                            text2.replace("<player>", profile.getNameWithColorNoDisguise())
                                    .replace("<note>", note.getNote())
                                    .replace("<id>", note.getUuid().toString())));
        }
    }
}
