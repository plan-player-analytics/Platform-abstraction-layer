package net.playeranalytics.plugin.server;

import cn.nukkit.event.HandlerList;
import cn.nukkit.event.Listener;
import cn.nukkit.plugin.PluginBase;

public class NukkitListeners implements Listeners {

    private final PluginBase plugin;

    public NukkitListeners(PluginBase plugin) {
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
