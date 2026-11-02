package xyz.refinedev.phoenix.commands;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Text;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.lib.commands.annotation.registration.Register;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.setup.OwnerClaim;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

@Register(name = "pxclaim")
public class PxclaimCommand {

    private final Phoenix plugin;

    @Command(name = "", async = true, description = "Claim ownership of a new Phoenix server")
    public void execute(@Sender Player player, @Named("code") @Text String text) {
        OwnerClaim ownerClaim;
        if ((ownerClaim = this.plugin.getOwnerClaim()) == null) {
            player.sendMessage(
                    Color.translate(LocaleUtil.tr(player, MessageConfig.PHOENIX.CLAIM.NOTHING)));
        } else {
            OwnerClaim.Entry entry = ownerClaim.create(player, text);
            switch (entry.getOutcome()) {
                case CLAIMED:
                    player.sendMessage(
                            Color.translate(
                                    LocaleUtil.tr(player, MessageConfig.PHOENIX.CLAIM.CLAIMED)
                                            .replace("<rank>", entry.getRank().getName())));
                    if (entry.isEnabled()) {
                        player.sendMessage(
                                Color.translate(
                                        LocaleUtil.tr(
                                                player, MessageConfig.PHOENIX.CLAIM.SECURITY)));
                    }
                    break;
                case WRONG_CODE:
                    player.sendMessage(
                            Color.translate(
                                    LocaleUtil.tr(player, MessageConfig.PHOENIX.CLAIM.WRONG)
                                            .replace(
                                                    "<left>",
                                                    String.valueOf(ownerClaim.getCount()))));
                    break;
                case LOCKED:
                    player.sendMessage(
                            Color.translate(
                                    LocaleUtil.tr(player, MessageConfig.PHOENIX.CLAIM.LOCKED)));
                    break;
                case NO_RANKS:
                    player.sendMessage(
                            Color.translate(
                                    LocaleUtil.tr(player, MessageConfig.PHOENIX.CLAIM.NO_RANKS)));
                    break;
                case PROFILE_NOT_LOADED:
                    player.sendMessage(
                            Color.translate(
                                    LocaleUtil.tr(player, MessageConfig.ERROR.PROFILE_NOT_LOADED)));
                    break;
                default:
                    player.sendMessage(
                            Color.translate(
                                    LocaleUtil.tr(player, MessageConfig.PHOENIX.CLAIM.NOTHING)));
            }
        }
    }

    public PxclaimCommand(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
