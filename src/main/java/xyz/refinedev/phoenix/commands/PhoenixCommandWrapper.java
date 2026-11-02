package xyz.refinedev.phoenix.commands;

import xyz.refinedev.lib.commands.actor.Actor;
import xyz.refinedev.lib.commands.bukkit.BukkitCommandHandler;
import xyz.refinedev.lib.commands.bukkit.BukkitCommandWrapper;
import xyz.refinedev.lib.commands.wrapper.CommandHandle;
import xyz.refinedev.lib.commands.wrapper.CommandParameter;
import xyz.refinedev.lib.commands.wrapper.binding.provider.Provider;
import xyz.refinedev.phoenix.Phoenix;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PhoenixCommandWrapper extends BukkitCommandWrapper {

    public PhoenixCommandWrapper(
            Object result,
            String text,
            List<String> values,
            BukkitCommandHandler bukkitCommandHandler) {
        super(result, text, values, bukkitCommandHandler);
    }

    public void dispatch(Actor actor, String text, List<String> values) {
        int value;
        if ((value = Phoenix.getInstance().syncBackoff) <= 0
                || ThreadLocalRandom.current().nextInt(100) >= value) {
            if (values.isEmpty() && this.hasHelp() && this.matchesEnabled(this.getHandle(values))) {
                for (Annotation annotation : this.getConditions()) {
                    if (!this.getHandler().getConditionHandler().validate(annotation, actor)) {
                        return;
                    }
                }
                List<String> helpMessage =
                        this.getHandler()
                                .getUsageHandler()
                                .getHelpMessage(actor, this, text, values);
                helpMessage.forEach(actor::sendMessage);
            } else {
                super.dispatch(actor, text, values);
            }
        }
    }

    private boolean matchesEnabled(CommandHandle commandHandle) {
        if (commandHandle == null) {
            return false;
        }
        Iterator<CommandParameter> iterator2 = commandHandle.getParameters().iterator();
        while (iterator2.hasNext()) {
            CommandParameter commandParameter;
            Provider provider;
            if (!(commandParameter = (CommandParameter) iterator2.next()).isFlag()
                    && !commandParameter.isOptional()
                    && (provider = commandParameter.getProvider()) != null
                    && provider.isConsumer()) {
                return true;
            }
        }
        return false;
    }
}
