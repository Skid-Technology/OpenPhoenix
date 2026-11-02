package xyz.refinedev.phoenix.commands.condition;

import xyz.refinedev.lib.commands.actor.Actor;
import xyz.refinedev.lib.commands.bukkit.actor.BukkitActor;
import xyz.refinedev.lib.commands.exception.execution.ExitMessage;
import xyz.refinedev.lib.commands.wrapper.binding.condition.ExecuteCondition;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.commands.api.annotations.RequireSecurity;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.LocaleUtil;

public class PhoenixExecuteCondition implements ExecuteCondition<RequireSecurity> {

    private final CommonPlatform platform;

    public boolean validate(Actor actor, RequireSecurity requireSecurity) {
        if (!actor.isPlayer()) {
            return true;
        } else if (!this.platform.getSecurityHandler().hasSecurity(actor.getUniqueId())) {
            throw new ExitMessage(
                    LocaleUtil.tr(
                            ((BukkitActor) actor).getSender(),
                            MessageConfig.SECURITY.NOT_SECURITY_USER));
        } else {
            return true;
        }
    }

    public PhoenixExecuteCondition(CommonPlatform commonPlatform) {
        this.platform = commonPlatform;
    }
}
