package xyz.refinedev.phoenix.chat.limitation;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.chat.api.ChatLimitation;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.chat.Clickable;

public class AuthenticationChatLimitation extends ChatLimitation {

    private static final String SHADOWMUTE_TEXT = "shadowmute";

    public AuthenticationChatLimitation(Phoenix phoenix) {
        super(
                SHADOWMUTE_TEXT,
                5,
                (result, result2) -> {
                    IProfile profile;
                    if ((profile =
                                    phoenix.getProfileHandler()
                                            .getCachedProfile(result.getUniqueId()))
                            == null) {
                        return false;
                    } else if (profile.isShadowMuted()) {
                        Clickable clickable;
                        (clickable = new Clickable())
                                .addComponent(
                                        phoenix.getChatHandler()
                                                .buildDefaultChatComponent(
                                                        result, result, result2));
                        clickable.sendToPlayer(result);
                        return false;
                    } else {
                        return true;
                    }
                });
    }
}
