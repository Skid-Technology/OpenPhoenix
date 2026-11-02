package xyz.refinedev.phoenix.utils.nametags.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.ScoreboardManager;

import xyz.refinedev.phoenix.utils.nametags.util.ScoreboardUtil;

public class NameTagListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Player player;
        if ((player = playerJoinEvent.getPlayer()).getScoreboard()
                == scoreboardManager.getMainScoreboard()) {
            player.setScoreboard(scoreboardManager.getNewScoreboard());
        }
        ScoreboardUtil.separateTablistName(player);
    }
}
