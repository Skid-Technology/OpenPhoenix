package xyz.refinedev.phoenix.chat.channel;

import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.chat.api.ChatChannelType;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.UUID;

public class DefaultChatChannel extends ChatChannelType {

    @Override
    public String getName() {
        return "Default";
    }

    @Override
    public boolean canSendMessage(Player player) {
        IProfile profile;
        if ((profile =
                        Phoenix.getInstance()
                                .getProfileHandler()
                                .getCachedProfile(player.getUniqueId()))
                == null) {
            Phoenix.getInstance()
                    .getLogger()
                    .warning(player.getName() + " tried to chat, but there is no profile cached.");
            return false;
        } else if (!profile.isSetting("global-chat")) {
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.ERROR.GLOBAL_CHAT_DISABLED)));
            return false;
        } else {
            return true;
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public String getFormat() {
        return Color.translate(SettingsConfig.FORMATS.CHAT_FORMAT);
    }

    @Override
    public boolean canReceiveMessage(UUID uUID, Player player) {
        IProfile profile;
        if ((profile = Phoenix.getInstance().getProfileHandler().getCachedProfile(uUID)) == null) {
            Phoenix.getInstance()
                    .getLogger()
                    .warning(uUID + " tried to chat, but there is no profile cached.");
            return false;
        } else {
            IProfile profile2;
            if ((profile2 =
                            Phoenix.getInstance()
                                    .getProfileHandler()
                                    .getCachedProfile(player.getUniqueId()))
                    == null) {
                Phoenix.getInstance()
                        .getLogger()
                        .warning(
                                "The recipient '"
                                        + player.getName()
                                        + "' of a chat message has a null profile.");
                return false;
            } else if (profile.checkPermission("core.globalchat.bypass", false)) {
                return true;
            } else {
                return profile2.getIgnoring().contains(uUID)
                        ? false
                        : profile2.isSetting("global-chat");
            }
        }
    }
}
