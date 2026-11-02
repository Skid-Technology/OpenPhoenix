package xyz.refinedev.phoenix.hook.api;

import org.bukkit.entity.Player;

import xyz.refinedev.phoenix.Phoenix;

public abstract class IHook {

    boolean isRegistered = false;

    boolean hasTried = false;

    public void setTried() {
        this.hasTried = true;
    }

    public boolean hasTriedToRegister() {
        return this.isRegistered;
    }

    public void setRegistered() {
        this.isRegistered = true;
    }

    public boolean isRegistered() {
        return this.isRegistered;
    }

    public abstract String getRequiredPlugin();

    public abstract void register(Phoenix phoenix);

    public void refreshPlayer(Player player) {}
}
