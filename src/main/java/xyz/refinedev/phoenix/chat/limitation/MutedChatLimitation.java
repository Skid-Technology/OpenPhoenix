package xyz.refinedev.phoenix.chat.limitation;

import xyz.refinedev.phoenix.chat.api.ChatLimitation;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

public class MutedChatLimitation extends ChatLimitation {

    public MutedChatLimitation() {
        super(
                "mutechat",
                7,
                (result, result2) -> {
                    if (SettingsConfig.CHAT_CONTROL.MUTED
                            && !result.hasPermission("core.mutechat.bypass")) {
                        result.sendMessage(
                                Color.translate(
                                        LocaleUtil.tr(result, MessageConfig.CHAT.CURRENTLY_MUTED)));
                        return false;
                    } else {
                        return true;
                    }
                });
    }
}
