package xyz.refinedev.phoenix.commands;

import org.bukkit.command.CommandSender;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.command.Help;
import xyz.refinedev.lib.commands.annotation.command.Requires;
import xyz.refinedev.lib.commands.annotation.parameter.Default;
import xyz.refinedev.lib.commands.annotation.parameter.Flag;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Text;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.commands.api.annotations.LogFiles;
import xyz.refinedev.phoenix.commands.api.annotations.RequireSecurity;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.LocaleUtil;
import xyz.refinedev.phoenix.utils.PaginatedListClickable;
import xyz.refinedev.phoenix.utils.chat.Clickable;
import xyz.refinedev.phoenix.utils.general.Tasks;

import java.io.File;
import java.util.ArrayList;

@Help
@RequireSecurity
@Register(name = "mclogs")
@Requires("core.command.mclogs")
public class MclogsCommand {

    public final Phoenix plugin;

    public MclogsCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }

    @Command(name = "upload")
    public void upload(
            @Sender CommandSender sender,
            @LogFiles @Text String text,
            @Flag("password") String text2,
            @Flag("delete-after-open") boolean matches)
            throws java.io.IOException {
        this.plugin
                .getMcLogsHandler()
                .sendCrashReports(sender, text, text2, text.contains("crash"), matches);
    }

    @Command(name = "list")
    public void list(@Sender CommandSender sender, @Default("1") @Named("page") int value) {
        PaginatedListClickable paginatedListClickable =
                new PaginatedListClickable("Log Files", null, "/mclogs list");
        ArrayList<Clickable> values = new ArrayList<>();
        File file;
        String[] texts;
        if ((file = new File("crash-reports")).exists() && (texts = file.list()) != null) {
            String[] texts2 = texts;
            int value2 = texts.length;
            for (int index = 0; index < value2; index++) {
                String text;
                if ((text = texts2[index])
                        .matches(this.plugin.getMcLogsHandler().getPattern().pattern())) {
                    values.add(
                            new Clickable(
                                    LocaleUtil.tr(sender, MessageConfig.GENERAL.GENERIC_LIST_FORMAT)
                                            + text));
                }
            }
        }
        File file2;
        String[] texts3;
        if ((file2 = new File("logs")).exists() && (texts3 = file2.list()) != null) {
            String[] texts4 = texts3;
            int value3 = texts3.length;
            for (int index2 = 0; index2 < value3; index2++) {
                String text2;
                if ((text2 = texts4[index2])
                        .matches(this.plugin.getMcLogsHandler().getPattern().pattern())) {
                    values.add(
                            new Clickable(
                                    LocaleUtil.tr(sender, MessageConfig.GENERAL.GENERIC_LIST_FORMAT)
                                            + text2));
                }
            }
        }
        paginatedListClickable.display(sender, value, values);
    }
}
