package xyz.refinedev.phoenix.chat;

import org.bukkit.entity.Player;

import xyz.refinedev.lib.commands.annotation.command.Command;
import xyz.refinedev.lib.commands.annotation.parameter.Named;
import xyz.refinedev.lib.commands.annotation.parameter.Optional;
import xyz.refinedev.lib.commands.annotation.parameter.Text;
import xyz.refinedev.lib.commands.annotation.parameter.classifier.Sender;
import xyz.refinedev.phoenix.BukkitAPI;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.chat.api.ChatChannelType;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

public class ChatChannelUtils {

    private final CommonPlatform platform;

    private final ChatChannelType chatChannelType;

    @Command(name = "")
    public void execute(@Sender Player player, @Optional @Named("message") @Text String text) {
        IProfile cachedProfile =
                this.platform.getProfileHandler().getCachedProfile(player.getUniqueId());
        if (text == null) {
            if (cachedProfile
                    .getSetting("channel", "Default")
                    .equalsIgnoreCase(this.chatChannelType.getName())) {
                cachedProfile.updateMetadata("channel", "Default", true);
            } else {
                cachedProfile.updateMetadata("channel", this.chatChannelType.getName(), true);
            }
            player.sendMessage(
                    Color.translate(
                            LocaleUtil.tr(player, MessageConfig.GENERAL.CHANNEL_SWITCHED)
                                    .replace(
                                            "<channel>",
                                            cachedProfile.getSetting("channel", "Default"))));
        } else {
            BukkitAPI.sendGlobalChatChannel(cachedProfile, this.chatChannelType, text);
        }
    }

    public ChatChannelUtils(CommonPlatform commonPlatform, ChatChannelType chatChannelType2) {
        this.platform = commonPlatform;
        this.chatChannelType = chatChannelType2;
    }
}
