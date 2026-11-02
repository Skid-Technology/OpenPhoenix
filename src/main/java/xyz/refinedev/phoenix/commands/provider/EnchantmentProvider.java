package xyz.refinedev.phoenix.commands.provider;

import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.lib.commands.execution.CommandExecution;
import xyz.refinedev.lib.commands.execution.argument.CommandArgument;
import xyz.refinedev.lib.commands.wrapper.binding.provider.Provider;
import xyz.refinedev.lib.commands.wrapper.binding.provider.ProviderType;
import xyz.refinedev.lib.commands.wrapper.suggestion.CommandSuggestion;
import xyz.refinedev.lib.xseries.XEnchantment;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class EnchantmentProvider extends Provider<XEnchantment> {

    public EnchantmentProvider() {
        super(ProviderType.ARGUMENT, "enchantment");
    }

    public XEnchantment provide(
            CommandExecution commandExecution, CommandArgument commandArgument) {
        Optional<XEnchantment> optional;
        if (!(optional = XEnchantment.of(commandArgument.getValue())).isPresent()) {
            throw new ExitMessage(
                    LocaleUtil.tr(commandExecution, MessageConfig.ERROR.INVALID_ENCHANTMENT)
                            .replace("<name>", Color.escape(commandArgument.getValue())));
        } else {
            return (XEnchantment) optional.get();
        }
    }

    public List<String> suggest(
            CommandSuggestion commandSuggestion, CommandArgument commandArgument) {
        ArrayList<String> values = new ArrayList<>();
        for (XEnchantment xEnchantment : XEnchantment.REGISTRY.getValues()) {
            values.addAll(Arrays.asList(xEnchantment.getNames()));
        }
        return values;
    }
}
