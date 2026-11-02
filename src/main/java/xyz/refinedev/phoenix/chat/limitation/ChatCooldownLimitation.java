package xyz.refinedev.phoenix.chat.limitation;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.chat.api.ChatLimitation;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

public class ChatCooldownLimitation extends ChatLimitation {

    private static final String AUTH_TEXT = "auth";

    public ChatCooldownLimitation(Phoenix phoenix) {
        super(
                AUTH_TEXT,
                6,
                (result, result2) -> {
                    IProfile profile;
                    if ((profile =
                                    phoenix.getProfileHandler()
                                            .getCachedProfile(result.getUniqueId()))
                            == null) {
                        return false;
                    } else if (profile.isTwoFactor()) {
                        result.sendMessage(
                                Color.translate(
                                        LocaleUtil.tr(result, MessageConfig.AUTH.CANNOT_CHAT)));
                        return false;
                    } else {
                        return true;
                    }
                });
    }
}
