package xyz.refinedev.phoenix.commands.modifier;

import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.lib.commands.execution.CommandExecution;
import xyz.refinedev.lib.commands.wrapper.CommandParameter;
import xyz.refinedev.lib.commands.wrapper.binding.modifier.ArgumentModifier;
import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.LocaleUtil;

public class PunishmentArgumentModifier implements ArgumentModifier<String> {

    public String modify(
            String text, CommandExecution commandExecution, CommandParameter commandParameter) {
        if (!BukkitAPI.isDisguiseValidName(text)) {
            throw new ExitMessage(LocaleUtil.tr(commandExecution, MessageConfig.DISGUISE.VALID));
        } else {
            return text;
        }
    }
}
