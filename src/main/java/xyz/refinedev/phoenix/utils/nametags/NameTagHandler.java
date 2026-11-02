package xyz.refinedev.phoenix.utils.nametags;

import lombok.Generated;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import xyz.refinedev.lib.jetbrains.Nullable;
import xyz.refinedev.phoenix.utils.nametags.listener.NameTagListener;
import xyz.refinedev.phoenix.utils.nametags.providers.PlayerNameProvider;
import xyz.refinedev.phoenix.utils.nametags.providers.PlayerTagProvider;
import xyz.refinedev.phoenix.utils.nametags.providers.TeamNameProvider;
import xyz.refinedev.phoenix.utils.nametags.providers.impl.DefaultPlayerTagProvider;
import xyz.refinedev.phoenix.utils.nametags.providers.impl.DefaultTeamNameProvider;
import xyz.refinedev.phoenix.utils.nametags.tag.PlayerTag;
import xyz.refinedev.phoenix.utils.nametags.team.TeamService;
import xyz.refinedev.phoenix.utils.nametags.team.impl.DefaultTeamService;
import xyz.refinedev.phoenix.utils.nametags.util.VersionUtil;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NameTagHandler {

    private final Set<PlayerTagProvider> tagProviders = new TreeSet<>();

    private final JavaPlugin plugin;

    private final Executor asyncExecutor;

    private boolean initialized = false;

    private boolean displayTablist = true;

    private TeamService teamService;

    private TeamNameProvider teamNameProvider;

    @Nullable private PlayerNameProvider nameProvider;

    public NameTagHandler(JavaPlugin javaPlugin) {
        this.plugin = javaPlugin;
        this.teamNameProvider = new DefaultTeamNameProvider(this);
        this.asyncExecutor = Executors.newSingleThreadExecutor();
        this.nameProvider = null;
    }

    public void registerTagProvider(PlayerTagProvider playerTagProvider) {
        if (this.initialized) {
            throw new IllegalStateException(
                    "Cannot register a PlayerTagProvider after the NameTagHandler has been initialized.");
        }
        this.tagProviders.add(playerTagProvider);
    }

    public void unregisterTagProvider(PlayerTagProvider playerTagProvider) {
        if (this.initialized) {
            throw new IllegalStateException(
                    "Cannot unregister a PlayerTagProvider after the NameTagHandler has been initialized.");
        }
        this.tagProviders.remove(playerTagProvider);
    }

    public void init() {
        this.initialized = true;
        if (this.tagProviders.isEmpty()) {
            this.tagProviders.add(new DefaultPlayerTagProvider());
        }
        this.teamService = new DefaultTeamService(this.teamNameProvider, this.nameProvider);
        Bukkit.getPluginManager().registerEvents(new NameTagListener(), this.plugin);
    }

    public void unload() {
        if (this.initialized) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                this.unload(player);
            }
        }
    }

    public void refresh(Player player) {
        if (this.initialized) {
            this.refreshViewers(player);
            this.refreshTargets(player);
        }
    }

    public void unload(Player player) {
        if (this.initialized) {
            ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
            Scoreboard scoreboard;
            if ((scoreboard = player.getScoreboard()) != scoreboardManager.getMainScoreboard()) {
                Iterator iterator2 = scoreboard.getTeams().iterator();
                while (iterator2.hasNext()) {
                    ((Team) iterator2.next()).unregister();
                }
                iterator2 = scoreboard.getObjectives().iterator();
                while (iterator2.hasNext()) {
                    ((Objective) iterator2.next()).unregister();
                }
                if (scoreboard != scoreboardManager.getMainScoreboard()) {
                    player.setScoreboard(scoreboardManager.getMainScoreboard());
                }
            }
        }
    }

    public void refreshViewers(Player player) {
        if (this.initialized) {
            this.applyToViewers(player, Bukkit.getOnlinePlayers());
        }
    }

    public void refreshTargets(Player player) {
        if (this.initialized) {
            for (Player player2 : Bukkit.getOnlinePlayers()) {
                this.resolveTagsAndSend(player, player2);
            }
        }
    }

    public void applyToViewers(Player player, Collection<? extends Player> values) {
        if (this.initialized) {
            for (Player player2 : values) {
                this.resolveTagsAndSend(player2, player);
            }
        }
    }

    private void resolveTagsAndSend(Player player, Player player2) {
        if (this.initialized) {
            this.asyncExecutor.execute(
                    () -> {
                        try {
                            PlayerTag playerTag = this.resolve(player, player2);
                            String text =
                                    this.teamNameProvider.buildTeamName(
                                            player2, playerTag.priority());
                            BukkitScheduler scheduler = Bukkit.getScheduler();
                            scheduler.runTask(
                                    this.plugin,
                                    () -> {
                                        try {
                                            this.teamService.sendNameTag(
                                                    player, player2, text, playerTag);
                                            if (this.displayTablist) {
                                                this.sendTabListName(player, player2, playerTag);
                                            }
                                        } catch (Throwable throwable) {
                                            this.handleTagError(throwable);
                                        }
                                    });
                        } catch (Throwable throwable2) {
                            this.handleTagError(throwable2);
                        }
                    });
        }
    }

    private void sendTabListName(Player player, Player player2, PlayerTag playerTag) {
        if (this.initialized) {
            if (VersionUtil.isPlayerOn17(player)) {
                this.teamService.displayLegacyTablist(player, player2, playerTag);
            } else {
                this.teamService.displayTablist(player, player2, playerTag);
            }
        }
    }

    public PlayerTag resolve(Player player, Player player2) {
        return this.resolveNext(player, player2, this.tagProviders.iterator())
                .orElse(PlayerTag.defaultTag());
    }

    private Optional<PlayerTag> resolveNext(
            Player player, Player player2, Iterator<PlayerTagProvider> iterator) {
        if (!iterator.hasNext()) {
            return Optional.of(PlayerTag.defaultTag());
        }
        Optional<PlayerTag> optional;
        return (optional = ((PlayerTagProvider) iterator.next()).buildTag(player, player2))
                                .isPresent()
                        && optional.get() != PlayerTag.defaultTag()
                ? optional
                : this.resolveNext(player, player2, iterator);
    }

    private void handleTagError(Throwable throwable) {
        this.plugin
                .getLogger()
                .severe("An error occurred while resolving a PlayerTag: " + throwable.getMessage());
        throwable.printStackTrace();
    }

    @Generated
    public boolean isInitialized() {
        return this.initialized;
    }

    @Generated
    public boolean isDisplayTablist() {
        return this.displayTablist;
    }

    @Generated
    public void setDisplayTablist(boolean displayTablist) {
        this.displayTablist = displayTablist;
    }

    @Generated
    public TeamService getTeamService() {
        return this.teamService;
    }

    @Generated
    public void setTeamService(TeamService teamService2) {
        this.teamService = teamService2;
    }

    @Generated
    public TeamNameProvider getTeamNameProvider() {
        return this.teamNameProvider;
    }

    @Generated
    public void setTeamNameProvider(TeamNameProvider teamNameProvider2) {
        this.teamNameProvider = teamNameProvider2;
    }

    @Nullable
    @Generated
    public PlayerNameProvider getNameProvider() {
        return this.nameProvider;
    }

    @Generated
    public void setNameProvider(@Nullable PlayerNameProvider playerNameProvider) {
        this.nameProvider = playerNameProvider;
    }
}
