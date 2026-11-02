package xyz.refinedev.phoenix.commands.provider;

import org.bukkit.command.CommandSender;

import xyz.refinedev.lib.commands.bukkit.actor.BukkitActor;
import xyz.refinedev.lib.commands.execution.CommandExecution;
import xyz.refinedev.lib.commands.execution.argument.CommandArgument;
import xyz.refinedev.lib.commands.wrapper.binding.provider.Provider;
import xyz.refinedev.lib.commands.wrapper.binding.provider.ProviderType;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.disguise.ISkin;
import xyz.refinedev.phoenix.profile.disguise.Skin;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

public class SkinProvider extends Provider<ISkin> {

    private final Phoenix plugin;

    public SkinProvider(Phoenix phoenix) {
        super(ProviderType.ARGUMENT, "skin");
        this.plugin = phoenix;
    }

    public ISkin provide(CommandExecution commandExecution, CommandArgument commandArgument) {
        String value = commandArgument.getValue();
        ISkin skin;
        if ((skin = this.plugin.getDisguiseHandler().getSkinByName(value).join()) == null) {
            CommandSender sender = ((BukkitActor) commandExecution.getActor()).getSender();
            sender.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(sender, MessageConfig.DISGUISE.SKIN_FALLBACK)
                                    .replace("<skin>", Color.escape(value))));
            return Skin.DEFAULT_SKIN;
        } else {
            return skin;
        }
    }

    public boolean isAsync() {
        return true;
    }
}
