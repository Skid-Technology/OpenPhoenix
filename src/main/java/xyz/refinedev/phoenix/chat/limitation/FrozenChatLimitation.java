package xyz.refinedev.phoenix.chat.limitation;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.chat.api.ChatLimitation;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.utils.Color;
import xyz.refinedev.phoenix.utils.LocaleUtil;

import java.util.Iterator;

public class FrozenChatLimitation extends ChatLimitation {

    public FrozenChatLimitation(Phoenix phoenix, Phoenix phoenix2) {
        super(
                "frozen",
                2,
                (result, result2) -> {
                    if (!phoenix.getFreezeHandler().isFrozen(result.getUniqueId())) {
                        return true;
                    }
                    String replacedChatFormat =
                            phoenix2.getChatHandler().getReplacedChatFormat(result, result2);
                    Iterator iterator2 = Bukkit.getOnlinePlayers().iterator();
                    while (iterator2.hasNext()) {
                        Player player;
                        if ((player = (Player) iterator2.next()).hasPermission("core.staff")) {
                            player.sendMessage(
                                    Color.translate(
                                                    LocaleUtil.tr(
                                                            player, MessageConfig.FREEZE.PREFIX))
                                            + replacedChatFormat);
                        }
                    }
                    Bukkit.getConsoleSender()
                            .sendMessage(
                                    Color.translate(
                                                    LocaleUtil.tr(
                                                            Bukkit.getConsoleSender(),
                                                            MessageConfig.FREEZE.PREFIX))
                                            + replacedChatFormat);
                    return false;
                });
    }
}
