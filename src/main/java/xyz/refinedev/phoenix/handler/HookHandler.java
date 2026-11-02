package xyz.refinedev.phoenix.handler;

import org.bukkit.Bukkit;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.hook.api.IHook;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

public class HookHandler {

    private final Phoenix plugin;

    private final List<IHook> hooks = new ArrayList<>();

    public boolean canRegister(String text) {
        return Bukkit.getPluginManager().getPlugin(text) != null;
    }

    public void addAndRegister(IHook hook) {
        this.hooks.add(hook);
        hook.setTried();
        if (Bukkit.getPluginManager().getPlugin(hook.getRequiredPlugin()) != null) {
            try {
                hook.register(this.plugin);
            } catch (Exception exception) {
                this.plugin
                        .getLogger()
                        .log(
                                Level.SEVERE,
                                "Failed to register hook " + hook.getRequiredPlugin() + ": ",
                                exception);
                return;
            }
            hook.setRegistered();
        }
    }

    public void registerAllHooks() {
        Iterator<IHook> iterator2 = this.hooks.iterator();
        while (iterator2.hasNext()) {
            IHook hook;
            if (!(hook = (IHook) iterator2.next()).hasTriedToRegister()) {
                hook.setTried();
                if (Bukkit.getPluginManager().getPlugin(hook.getRequiredPlugin()) != null) {
                    try {
                        hook.register(this.plugin);
                    } catch (Exception exception) {
                        this.plugin
                                .getLogger()
                                .log(
                                        Level.SEVERE,
                                        "Failed to register hook "
                                                + hook.getRequiredPlugin()
                                                + ": ",
                                        exception);
                        return;
                    }
                    hook.setRegistered();
                }
            }
        }
    }

    public IHook getHook(String text) {
        Iterator<IHook> iterator2 = this.hooks.iterator();
        while (iterator2.hasNext()) {
            IHook hook;
            if ((hook = (IHook) iterator2.next()).getRequiredPlugin().equalsIgnoreCase(text)) {
                return hook;
            }
        }
        return null;
    }

    public boolean isHookEnabled(String text) {
        Iterator<IHook> iterator2 = this.hooks.iterator();
        while (iterator2.hasNext()) {
            IHook hook;
            if ((hook = (IHook) iterator2.next()).getRequiredPlugin().equalsIgnoreCase(text)) {
                return hook.isRegistered();
            }
        }
        return false;
    }

    public void addHook(IHook hook) {
        this.hooks.add(hook);
    }

    public HookHandler(Phoenix phoenix) {
        this.plugin = phoenix;
    }
}
