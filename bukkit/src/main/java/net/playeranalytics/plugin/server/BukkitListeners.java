package net.playeranalytics.plugin.server;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitListeners implements Listeners {

    private final JavaPlugin plugin;

    public BukkitListeners(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void registerListener(Object listener) {
        if (listener == null) {
            throw new IllegalArgumentException("'listener' can not be null!");
        }
        if (!(listener instanceof Listener)) {
            throw new IllegalArgumentException("'listener' needs to be of type " + listener.getClass().getName() +
                    ", but was " + listener.getClass());
        }
        plugin.getServer().getPluginManager().registerEvents((Listener) listener, plugin);
    }

    @Override
    public void unregisterListener(Object listener) {
        if (listener == null) {
            throw new IllegalArgumentException("'listener' can not be null!");
        }
        if (!(listener instanceof Listener)) {
            throw new IllegalArgumentException("'listener' needs to be of type " + listener.getClass().getName() +
                    ", but was " + listener.getClass());
        }
        HandlerList.unregisterAll((Listener) listener);
    }

    @Override
    public void unregisterListeners() {
        HandlerList.unregisterAll(plugin);
    }
}
