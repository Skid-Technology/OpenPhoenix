package xyz.refinedev.phoenix.handler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.chat.ChatCompletionAction;
import xyz.refinedev.lib.com.github.retrooper.packetevents.protocol.player.ClientVersion;
import xyz.refinedev.lib.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCustomChatCompletions;
import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.config.GlobalConfig;
import xyz.refinedev.phoenix.config.SettingsConfig;
import xyz.refinedev.phoenix.profile.IProfile;
import xyz.refinedev.phoenix.rank.IRank;
import xyz.refinedev.phoenix.server.IServerData;
import xyz.refinedev.phoenix.utils.general.Tasks;
import xyz.refinedev.phoenix.utils.version.ClientVersionUtil;
import xyz.refinedev.phoenix.utils.version.ServerVersion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class ChatCompletionHandler {

    private static final ServerVersion serverVersion = ServerVersion.V_1_19;

    private static final ClientVersion clientVersion = ClientVersion.V_1_19;

    private final Phoenix plugin;

    private final Map<UUID, Set<String>> stringsById = new ConcurrentHashMap<>();

    private final Map<UUID, String> stringsByIdMap = new ConcurrentHashMap<>();

    private final Set<String> strings = ConcurrentHashMap.newKeySet();

    private int countCount;

    private boolean enabled;

    public ChatCompletionHandler(Phoenix phoenix) {
        this.plugin = phoenix;
        Tasks.runAsyncTimer(this::initialize, 600L, 600L);
    }

    public void applyPlayer(Player player) {
        if (this.serverSupports()) {
            if (player.isOnline()) {
                try {
                    if (ClientVersionUtil.supports(player, clientVersion)) {
                        Set<String> values = this.findSet(player);
                        ArrayList values2;
                        (values2 = new ArrayList(values)).addAll(this.findList(player));
                        if (SettingsConfig.MODERN.CHAT_COMPLETIONS.NETWORK_PLAYERS) {
                            values2.addAll(this.strings);
                        }
                        if (!values2.isEmpty()) {
                            this.updatePlayer(player, ChatCompletionAction.SET, values2);
                        }
                        this.stringsById.put(player.getUniqueId(), new HashSet<>(values));
                    }
                    this.applyPlayerByPLAYERS(player);
                    if (!player.isOnline()) {
                        this.applyPlayerByStringsById(player);
                    }
                } catch (Throwable throwable) {
                    this.updateString(
                            "an error occurred while sending completions on join", throwable);
                }
            }
        }
    }

    public void applyPlayerByStringsById(Player player) {
        this.stringsById.remove(player.getUniqueId());
        String text = this.stringsByIdMap.remove(player.getUniqueId());
        if (this.serverSupports() && text != null) {
            try {
                Iterator iterator2 = Bukkit.getOnlinePlayers().iterator();
                while (iterator2.hasNext()) {
                    Player player2;
                    if (!(player2 = (Player) iterator2.next())
                            .getUniqueId()
                            .equals(player.getUniqueId())) {
                        this.applyPlayerByClientVersion(player2, text);
                    }
                }
            } catch (Throwable throwable) {
                this.updateString(
                        "an error occurred while removing completions on quit", throwable);
            }
        }
    }

    public void applyPlayerByStringsByIdMap(Player player) {
        if (this.serverSupports()) {
            try {
                String text = this.stringsByIdMap.get(player.getUniqueId());
                String text2;
                if ((text2 = this.formatString(player)).equals(text)) {
                    return;
                }
                this.stringsByIdMap.put(player.getUniqueId(), text2);
                Iterator iterator2 = Bukkit.getOnlinePlayers().iterator();
                while (iterator2.hasNext()) {
                    Player player2;
                    if (!(player2 = (Player) iterator2.next())
                            .getUniqueId()
                            .equals(player.getUniqueId())) {
                        if (text != null) {
                            this.applyPlayerByClientVersion(player2, text);
                        }
                        if (this.plugin.getVisibilityHandler().canSee(player2, player)) {
                            this.applyPlayer(player2, text2);
                        }
                    }
                }
            } catch (Throwable throwable) {
                this.updateString(
                        "an error occurred while updating completions after a name change",
                        throwable);
            }
        }
    }

    public void applyPlayerByPlugin(Player player) {
        if (this.serverSupports()) {
            try {
                String text = this.formatString(player);
                Iterator iterator2 = Bukkit.getOnlinePlayers().iterator();
                while (iterator2.hasNext()) {
                    Player player2;
                    if (!(player2 = (Player) iterator2.next())
                            .getUniqueId()
                            .equals(player.getUniqueId())) {
                        if (this.plugin.getVisibilityHandler().canSee(player2, player)) {
                            this.applyPlayer(player2, text);
                        } else {
                            this.applyPlayerByClientVersion(player2, text);
                        }
                    }
                }
            } catch (Throwable throwable) {
                this.updateString(
                        "an error occurred while updating completions after a visibility change",
                        throwable);
            }
        }
    }

    public void initialize() {
        if (this.serverSupports() && SettingsConfig.MODERN.CHAT_COMPLETIONS.NETWORK_PLAYERS) {
            try {
                HashSet<String> values = new HashSet<>();
                Iterator<IServerData> iterator2 =
                        this.plugin.getNetworkHandler().getServers().iterator();
                while (iterator2.hasNext()) {
                    IServerData serverData;
                    if ((serverData = (IServerData) iterator2.next()).isOnline()
                            && !serverData
                                    .getServerName()
                                    .equalsIgnoreCase(GlobalConfig.SERVER.NAME)) {
                        List<UUID> onlyVanishedOnlinePlayers =
                                this.plugin
                                        .getNetworkHandler()
                                        .getOnlyVanishedOnlinePlayers(serverData);
                        for (UUID uUID : serverData.getOnlinePlayers()) {
                            String text;
                            if (!onlyVanishedOnlinePlayers.contains(uUID)
                                    && Bukkit.getPlayer(uUID) == null
                                    && (text = this.plugin.getUuidCache().getName(uUID)) != null
                                    && !text.isEmpty()) {
                                values.add(text);
                            }
                        }
                    }
                }
                HashSet values2;
                (values2 = new HashSet(values)).removeAll(this.strings);
                HashSet values3;
                (values3 = new HashSet<>(this.strings)).removeAll(values);
                if (values2.isEmpty() && values3.isEmpty()) {
                    return;
                }
                this.strings.removeAll(values3);
                this.strings.addAll(values2);
                Iterator iterator3 = Bukkit.getOnlinePlayers().iterator();
                while (iterator3.hasNext()) {
                    Player player;
                    if (ClientVersionUtil.supports(
                            player = (Player) iterator3.next(), clientVersion)) {
                        if (!values2.isEmpty()) {
                            this.updatePlayer(
                                    player, ChatCompletionAction.ADD, new ArrayList<>(values2));
                        }
                        if (!values3.isEmpty()) {
                            this.updatePlayer(
                                    player, ChatCompletionAction.REMOVE, new ArrayList<>(values3));
                        }
                    }
                }
            } catch (Throwable throwable) {
                this.updateString(
                        "an error occurred while refreshing the network's names", throwable);
            }
        } else {
            this.initializeByStrings();
        }
    }

    private void initializeByStrings() {
        if (!this.strings.isEmpty()) {
            ArrayList values = new ArrayList<>(this.strings);
            this.strings.clear();
            try {
                Iterator iterator2 = Bukkit.getOnlinePlayers().iterator();
                while (iterator2.hasNext()) {
                    Player player;
                    if (ClientVersionUtil.supports(
                            player = (Player) iterator2.next(), clientVersion)) {
                        this.updatePlayer(player, ChatCompletionAction.REMOVE, values);
                    }
                }
            } catch (Throwable throwable) {
                this.updateString(
                        "an error occurred while dropping the network's names", throwable);
            }
        }
    }

    public void initializeByStringsById() {
        this.stringsById.clear();
        this.stringsByIdMap.clear();
        if (this.serverSupports()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                this.applyPlayer(player);
            }
        }
    }

    private void applyPlayerByPLAYERS(Player player) {
        if (SettingsConfig.MODERN.CHAT_COMPLETIONS.PLAYERS) {
            String text = this.formatString(player);
            this.stringsByIdMap.put(player.getUniqueId(), text);
            Iterator iterator2 = Bukkit.getOnlinePlayers().iterator();
            while (iterator2.hasNext()) {
                Player player2;
                if (!(player2 = (Player) iterator2.next())
                                .getUniqueId()
                                .equals(player.getUniqueId())
                        && this.plugin.getVisibilityHandler().canSee(player2, player)) {
                    this.applyPlayer(player2, text);
                }
            }
        }
    }

    private void applyPlayer(Player player, String text) {
        if (SettingsConfig.MODERN.CHAT_COMPLETIONS.PLAYERS) {
            if (ClientVersionUtil.supports(player, clientVersion)) {
                Set<String> values;
                if ((values = this.stringsById.get(player.getUniqueId())) != null
                        && values.add(text)) {
                    this.updatePlayer(
                            player, ChatCompletionAction.ADD, Collections.singletonList(text));
                }
            }
        }
    }

    private void applyPlayerByClientVersion(Player player, String text) {
        if (ClientVersionUtil.supports(player, clientVersion)) {
            Set<String> values;
            if ((values = this.stringsById.get(player.getUniqueId())) != null
                    && values.remove(text)) {
                this.updatePlayer(
                        player, ChatCompletionAction.REMOVE, Collections.singletonList(text));
            }
        }
    }

    private Set<String> findSet(Player player) {
        HashSet<String> values = new HashSet<>();
        if (!SettingsConfig.MODERN.CHAT_COMPLETIONS.PLAYERS) {
            return values;
        }
        for (Player player2 : Bukkit.getOnlinePlayers()) {
            if (this.plugin.getVisibilityHandler().canSee(player, player2)) {
                values.add(this.formatString(player2));
            }
        }
        return values;
    }

    private List<String> findList(Player player) {
        ArrayList<String> values = new ArrayList<>();
        if (SettingsConfig.MODERN.CHAT_COMPLETIONS.COMMANDS) {
            Iterator<Entry<String, String>> iterator2 =
                    this.plugin.getCommandHandler().getRegisteredCommands().entrySet().iterator();
            while (iterator2.hasNext()) {
                Entry entry;
                String text;
                if ((text = (String) (entry = (Entry) iterator2.next()).getValue()) == null
                        || text.isEmpty()
                        || player.hasPermission(text)) {
                    values.add("/" + (String) entry.getKey());
                }
            }
        }
        if (SettingsConfig.MODERN.CHAT_COMPLETIONS.RANKS
                && player.hasPermission(SettingsConfig.MODERN.CHAT_COMPLETIONS.RANKS_PERMISSION)) {
            for (IRank rank : this.plugin.getRankHandler().getSortedRanks()) {
                values.add(rank.getName());
            }
        }
        values.addAll(SettingsConfig.MODERN.CHAT_COMPLETIONS.CUSTOM);
        return values;
    }

    private String formatString(Player player) {
        IProfile profile;
        if ((profile = this.plugin.getProfileHandler().getCachedProfile(player.getUniqueId()))
                == null) {
            return player.getName();
        }
        String text;
        return (text = profile.getPlayerName()) != null && !text.isEmpty()
                ? text
                : player.getName();
    }

    private boolean serverSupports() {
        return !this.enabled
                && SettingsConfig.MODERN.CHAT_COMPLETIONS.ENABLED
                && ClientVersionUtil.serverSupports(serverVersion);
    }

    private void updatePlayer(
            Player player, ChatCompletionAction chatCompletionAction, List<String> values) {
        if (ClientVersionUtil.sendPacket(
                player, new WrapperPlayServerCustomChatCompletions(chatCompletionAction, values))) {
            this.countCount = 0;
        } else {
            this.updateString("the packet could not be sent", null);
        }
    }

    private void updateString(String text, Throwable throwable) {
        if (++this.countCount >= 3) {
            this.enabled = true;
            this.plugin
                    .getLogger()
                    .log(
                            Level.WARNING,
                            "Chat completions have been disabled after 3 failures: "
                                    + text
                                    + ". Restart the server to try again.",
                            throwable);
        }
    }
}
