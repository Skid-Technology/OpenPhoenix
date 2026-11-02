package xyz.refinedev.phoenix.nametag;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import xyz.refinedev.lib.com.github.retrooper.packetevents.PacketEvents;
import xyz.refinedev.phoenix.CommonPlatform;
import xyz.refinedev.phoenix.config.NameTagsConfig;
import xyz.refinedev.phoenix.nametag.line.LineRenderer;
import xyz.refinedev.phoenix.nametag.packet.PacketNameTagBackend;
import xyz.refinedev.phoenix.nametag.packet.TeamPacketListener;
import xyz.refinedev.phoenix.utils.Utilities;
import xyz.refinedev.phoenix.utils.general.Tasks;
import xyz.refinedev.phoenix.utils.nametags.NameTagHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class NameTagService {

    private final NameTagBackendFactory nameTagBackendFactory;

    private final NameTagExecutor nameTagExecutor;

    private final NoOpNameTagBackend noOpNameTagBackend;

    private final PacketNameTagBackend packetNameTagBackend;

    private final boolean enabled;

    private boolean enabledEnabled;

    private boolean enabled2;

    private volatile List<Player> players = Collections.emptyList();

    private static final Pattern pattern = Pattern.compile("^_?[a-z]{2}a?[&\u00a7].*$");

    public NameTagService(CommonPlatform commonPlatform, NameTagHandler nameTagHandler) {
        this.nameTagBackendFactory = new NameTagBackendFactory();
        this.nameTagExecutor = new NameTagExecutor();
        this.noOpNameTagBackend =
                new NoOpNameTagBackend(nameTagHandler, this.nameTagBackendFactory);
        this.packetNameTagBackend =
                new PacketNameTagBackend(
                        commonPlatform, this.nameTagBackendFactory, this.nameTagExecutor);
        this.enabled = !nameTagHandler.isInitialized();
        this.packetNameTagBackend.setConsumer(
                result -> {
                    Tasks.run(() -> this.applyPlayer5(result));
                });
        PacketEvents.getAPI()
                .getEventManager()
                .registerListener(
                        new TeamPacketListener(
                                this.packetNameTagBackend,
                                this.nameTagBackendFactory,
                                this.nameTagExecutor));
        this.setPlayers();
    }

    public void initialize() {
        Tasks.run(
                () -> {
                    try {
                        Scoreboard mainScoreboard =
                                Bukkit.getScoreboardManager().getMainScoreboard();
                        Iterator iterator2 = new ArrayList(mainScoreboard.getTeams()).iterator();
                        while (iterator2.hasNext()) {
                            Team team;
                            String name = (team = (Team) iterator2.next()).getName();
                            if (pattern.matcher(name).matches()) {
                                team.unregister();
                                Bukkit.getLogger().info("[PhoenixNT] removed stale team " + name);
                            }
                        }
                    } catch (Throwable throwable) {
                        Bukkit.getLogger()
                                .warning("[PhoenixNT] could not clean stale teams: " + throwable);
                    }
                });
    }

    public void initializeByEnabledEnabled() {
        if (!this.enabledEnabled) {
            boolean matches = false;
            LineRenderer[] lineRenderers;
            int value = (lineRenderers = NameTagsConfig.lines()).length;
            for (int index = 0; index < value; index++) {
                if (lineRenderers[index].getConditionalLine().isEnabled()) {
                    matches = true;
                    break;
                }
            }
            if (matches) {
                int value2 = Math.max(1, NameTagsConfig.DISPLAY.ANIMATION_TICK);
                this.enabledEnabled = true;
                Tasks.runTimer(
                        () -> {
                            this.applyConsumer(
                                    result ->
                                            this.packetNameTagBackend.updateCount(value2, result));
                        },
                        value2,
                        value2);
            }
        }
    }

    public void initializeByREFRESH_INTERVAL() {
        int value = NameTagsConfig.DISPLAY.REFRESH_INTERVAL;
        if (NameTagsConfig.DISPLAY.REFRESH_INTERVAL > 0) {
            Tasks.runTimer(
                    () -> {
                        PacketNameTagBackend packetNameTagBackend2 = this.packetNameTagBackend;
                        this.applyConsumer(packetNameTagBackend2::applyList);
                    },
                    value,
                    value);
        }
    }

    public void applyPlayer(Player player) {
        this.setPlayers();
        this.applyConsumer(
                result -> {
                    if (player.isOnline()) {
                        this.nameTagBackendFactory.findNameTagBackendType(player);
                        this.packetNameTagBackend.applyPlayer(player);
                        this.noOpNameTagBackend.refreshTargets(player, result);
                        this.noOpNameTagBackend.refreshFor(player, result);
                        this.packetNameTagBackend.applyPlayer(player, result);
                        this.applyPlayerByEnabled(player);
                    }
                });
    }

    public void applyPlayerByVoid(Player player) {
        this.applyConsumer(
                result -> {
                    this.packetNameTagBackend.applyPlayerByNameTagTeam(player);
                    this.packetNameTagBackend.applyPlayerByUUIDsByIndex(player, result);
                    this.packetNameTagBackend.applyPlayer(player);
                    this.packetNameTagBackend.applyPlayer(player, result);
                });
    }

    public void applyPlayerByPlayer(Player player) {
        this.applyConsumer(
                result -> {
                    this.packetNameTagBackend.applyPlayerByUUIDsByIndex(player, result);
                    this.noOpNameTagBackend.applyPlayer(player);
                    this.nameTagBackendFactory.applyUUID(player.getUniqueId());
                });
        NameTagService nameTagService = this;
        Tasks.runLater(nameTagService::setPlayers, 1L);
    }

    public void applyPlayer2(Player player) {
        this.applyConsumer(
                result -> {
                    this.noOpNameTagBackend.refreshTargets(player, result);
                    this.noOpNameTagBackend.refreshFor(player, result);
                    this.packetNameTagBackend.applyPlayer(player, result);
                    this.applyPlayerByEnabled(player);
                });
    }

    public void applyPlayer3(Player player) {
        this.applyConsumer(
                result -> {
                    this.noOpNameTagBackend.refreshFor(player, result);
                    this.packetNameTagBackend.refreshFor(player, result);
                });
    }

    public void applyPlayer4(Player player) {
        this.applyConsumer(
                result -> {
                    this.noOpNameTagBackend.refreshTargets(player, result);
                    this.packetNameTagBackend.refreshTargets(player, result);
                    this.applyPlayerByEnabled(player);
                });
    }

    private void applyPlayerByEnabled(Player player) {
        if (this.enabled) {
            String text;
            if ((text = this.packetNameTagBackend.formatString(player)) != null) {
                String text2 = text;
                Tasks.run(
                        () -> {
                            if (player.isOnline() && !text2.equals(player.getPlayerListName())) {
                                player.setPlayerListName(text2);
                            }
                        });
            }
        }
    }

    public void applyPlayer5(Player player) {
        this.applyConsumer(result -> this.noOpNameTagBackend.refreshFor(player, result));
    }

    public void applyPlayer6(Player player) {
        this.applyConsumer(result -> this.packetNameTagBackend.applyPlayerByVoid(player, result));
    }

    public void applyPlayer7(Player player) {
        this.applyConsumer(
                result -> {
                    for (Player player2 : (Iterable<Player>) ((Iterable) result)) {
                        this.packetNameTagBackend.remove(player2, player);
                    }
                });
    }

    public void initializeByNameTagExecutor() {
        this.applyConsumer(
                result -> {
                    this.packetNameTagBackend.applyListByNameTagStatesById(result);
                    this.noOpNameTagBackend.initialize();
                    this.nameTagBackendFactory.initialize();
                });
        this.nameTagExecutor.applyTime(2000L);
        this.enabledEnabled = false;
    }

    private void applyConsumer(Consumer<List<Player>> consumer) {
        List<Player> values = this.players;
        this.nameTagExecutor.applyRunnable(() -> consumer.accept(values));
    }

    private void setPlayers() {
        this.players = Utilities.getOnlinePlayers();
    }

    public NameTagBackendFactory getNameTagBackendFactory() {
        return this.nameTagBackendFactory;
    }

    public NoOpNameTagBackend getNoOpNameTagBackend() {
        return this.noOpNameTagBackend;
    }

    public PacketNameTagBackend getPacketNameTagBackend() {
        return this.packetNameTagBackend;
    }

    public boolean isEnabledEnabled() {
        return this.enabledEnabled;
    }

    public boolean isEnabled2() {
        return this.enabled2;
    }

    public void setEnabled2(boolean enabled2) {
        this.enabled2 = enabled2;
    }
}
