package xyz.refinedev.phoenix.chat.limitation;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.chat.api.ChatLimitation;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.handler.ICooldownHandler;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.DateUtils;
import xyz.refinedev.phoenix.utils.LocaleUtil;

public class SlowChatLimitation extends ChatLimitation {

    private static final long timeTime = 1000L;

    public SlowChatLimitation(Phoenix phoenix) {
        super(
                "slowchat",
                8,
                (result, result2) -> {
                    ICooldownHandler cooldownHandler = phoenix.getCooldownHandler();
                    if (!result.hasPermission("core.slowchat.bypass")) {
                        if (cooldownHandler.isOnCooldown("slowchat", result.getUniqueId())) {
                            result.sendMessage(
                                    Color.translate(
                                            LocaleUtil.tr(
                                                            result,
                                                            MessageConfig.CHAT.CURRENTLY_SLOWED)
                                                    .replace(
                                                            "<seconds>",
                                                            DateUtils.formatTimeMillis(
                                                                    cooldownHandler.getRemaining(
                                                                            "slowchat",
                                                                            result
                                                                                    .getUniqueId())))));
                            return false;
                        } else {
                            phoenix.getCooldownHandler()
                                    .placeOnCooldown(
                                            "slowchat",
                                            result.getUniqueId(),
                                            SettingsConfig.CHAT_CONTROL.SLOW_DELAY * timeTime);
                            return true;
                        }
                    } else {
                        return true;
                    }
                });
    }
}
