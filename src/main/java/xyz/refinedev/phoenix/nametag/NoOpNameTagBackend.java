package xyz.refinedev.phoenix.nametag;

import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.utils.nametags.NameTagHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NoOpNameTagBackend implements NameTagBackend {

    private final NameTagHandler nameTagHandler;

    private final NameTagBackendFactory nameTagBackendFactory;

    @Override
    public void refresh(Player player, Player player2) {
        if (this.nameTagHandler.isInitialized()) {
            if (!this.nameTagBackendFactory.matchesEnabled(player)) {
                this.nameTagHandler.applyToViewers(player2, Collections.singletonList(player));
            }
        }
    }

    @Override
    public void refreshFor(Player player, List<Player> values) {
        if (this.nameTagHandler.isInitialized()) {
            if (!this.nameTagBackendFactory.matchesEnabled(player)) {
                List<Player> values2 = Collections.singletonList(player);
                for (Player player2 : values) {
                    this.nameTagHandler.applyToViewers(player2, values2);
                }
            }
        }
    }

    @Override
    public void refreshTargets(Player player, List<Player> values) {
        if (this.nameTagHandler.isInitialized()) {
            ArrayList<Player> values2 = new ArrayList<>();
            for (Player player2 : values) {
                if (!this.nameTagBackendFactory.matchesEnabled(player2)) {
                    values2.add(player2);
                }
            }
            if (!values2.isEmpty()) {
                this.nameTagHandler.applyToViewers(player, values2);
            }
        }
    }

    @Override
    public void remove(Player player, Player player2) {}

    public void applyPlayer(Player player) {
        if (this.nameTagHandler.isInitialized()) {
            this.nameTagHandler.unload(player);
        }
    }

    public void initialize() {
        if (this.nameTagHandler.isInitialized()) {
            this.nameTagHandler.unload();
        }
    }

    public NoOpNameTagBackend(
            NameTagHandler nameTagHandler2, NameTagBackendFactory nameTagBackendFactory2) {
        this.nameTagHandler = nameTagHandler2;
        this.nameTagBackendFactory = nameTagBackendFactory2;
    }
}
