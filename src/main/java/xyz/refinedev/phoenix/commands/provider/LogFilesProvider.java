package xyz.refinedev.phoenix.commands.provider;

import xyz.refinedev.lib.commands.execution.CommandExecution;
import xyz.refinedev.lib.commands.execution.argument.CommandArgument;
import xyz.refinedev.lib.commands.wrapper.binding.provider.Provider;
import xyz.refinedev.lib.commands.wrapper.binding.provider.ProviderType;
import xyz.refinedev.lib.commands.wrapper.suggestion.CommandSuggestion;
import xyz.refinedev.phoenix.Phoenix;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LogFilesProvider extends Provider<String> {

    private final Phoenix plugin;

    public LogFilesProvider(Phoenix phoenix) {
        super(ProviderType.ARGUMENT, "logFiles");
        this.plugin = phoenix;
    }

    public String provide(CommandExecution commandExecution, CommandArgument commandArgument) {
        return commandArgument.getValue();
    }

    public List<String> suggest(
            CommandSuggestion commandSuggestion, CommandArgument commandArgument) {
        ArrayList<String> values = new ArrayList<>();
        File file;
        String[] texts;
        if ((file = new File("crash-reports")).exists() && (texts = file.list()) != null) {
            String[] texts2 = texts;
            int value = texts.length;
            for (int index = 0; index < value; index++) {
                String text;
                if ((text = texts2[index])
                        .matches(this.plugin.getMcLogsHandler().getPattern().pattern())) {
                    values.add(text);
                }
            }
        }
        File file2;
        String[] texts3;
        if ((file2 = new File("logs")).exists() && (texts3 = file2.list()) != null) {
            String[] texts4 = texts3;
            int value2 = texts3.length;
            for (int index2 = 0; index2 < value2; index2++) {
                String text2;
                if ((text2 = texts4[index2])
                        .matches(this.plugin.getMcLogsHandler().getPattern().pattern())) {
                    values.add(text2);
                }
            }
        }
        return values;
    }
}
