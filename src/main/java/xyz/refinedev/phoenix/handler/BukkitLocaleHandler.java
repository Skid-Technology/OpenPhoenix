package xyz.refinedev.phoenix.handler;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.PacketEventsAPI;
import xyz.refinedev.lib.com.github.retrooper.packetevents.event.PacketListenerAbstract;
import xyz.refinedev.lib.com.github.retrooper.packetevents.event.PacketListenerPriority;
import xyz.refinedev.lib.com.github.retrooper.packetevents.event.PacketReceiveEvent;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Client;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSettings;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.MenuConfig;
import xyz.refinedev.phoenix.config.MessageConfig;
import xyz.refinedev.phoenix.profile.IProfile;

import java.util.List;
import java.util.logging.Level;

public class BukkitLocaleHandler extends LocaleHandler {

    private static final String MENU_PREFIX = "menus/";

    private final Phoenix plugin;

    public BukkitLocaleHandler(Phoenix phoenix) {
        super(phoenix);
        this.plugin = phoenix;
        this.register("messages", "", MessageConfig.class);
        this.register("menus", "menus/", MenuConfig.class);
        try {
            PacketEventsAPI packetEventsAPI;
            if ((packetEventsAPI = phoenix.getPacketEvents()) != null) {
                packetEventsAPI
                        .getEventManager()
                        .registerListener(new BukkitLocaleHandler.ClientLocaleListener(this));
            }
        } catch (Throwable throwable) {
            phoenix.getLogger()
                    .log(
                            Level.WARNING,
                            "Locales are unavailable, the settings listener did not register.",
                            throwable);
        }
        this.load();
    }

    public String tr(CommandSender sender, String text) {
        if (text != null && sender instanceof Player) {
            String text2;
            return (text2 = this.pathOf(text)) == null
                    ? text
                    : this.get((Player) sender, text2, text);
        } else {
            return text;
        }
    }

    public List<String> tr(CommandSender sender, List<String> values) {
        if (values != null && sender instanceof Player) {
            String text;
            return (text = this.pathOf(values)) == null
                    ? values
                    : this.getList((Player) sender, text, values);
        } else {
            return values;
        }
    }

    public String getTranslation(Player player, String text) {
        if (text == null) {
            return null;
        }
        Object result;
        return (result = this.resolve(this.getLocale(player), text, false)) instanceof String
                ? (String) result
                : null;
    }

    public String get(Player player, String text, String text2) {
        Object result;
        return (result = this.resolve(this.getLocale(player), text, false)) instanceof String
                ? (String) result
                : text2;
    }

    public List<String> getList(Player player, String text, List<String> values) {
        Object result;
        return (result = this.resolve(this.getLocale(player), text, true)) instanceof List
                ? (List) result
                : values;
    }

    public String getLocale(Player player) {
        IProfile profile;
        return (profile = this.plugin.getProfileHandler().getCachedProfile(player.getUniqueId()))
                        != null
                ? this.getLocale(profile)
                : this.getLocale(player.getUniqueId());
    }

    static class ClientLocaleListener extends PacketListenerAbstract {

        private final LocaleHandler localeHandler;

        private ClientLocaleListener(LocaleHandler localeHandler2) {
            super(PacketListenerPriority.MONITOR);
            this.localeHandler = localeHandler2;
        }

        public void onPacketReceive(PacketReceiveEvent packetReceiveEvent) {
            if (packetReceiveEvent.getPacketType() == Client.CLIENT_SETTINGS) {
                try {
                    Object result;
                    if (!((result = packetReceiveEvent.getPlayer()) instanceof Player)) {
                        return;
                    }
                    WrapperPlayClientSettings wrapperPlayClientSettings =
                            new WrapperPlayClientSettings(packetReceiveEvent);
                    this.localeHandler.announce(
                            ((Player) result).getUniqueId(), wrapperPlayClientSettings.getLocale());
                } catch (Throwable throwable) {
                }
            }
        }
    }
}
