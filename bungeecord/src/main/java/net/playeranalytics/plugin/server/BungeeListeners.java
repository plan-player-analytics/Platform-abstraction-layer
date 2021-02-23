package net.playeranalytics.plugin.server;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeListeners implements Listeners {

    private final Plugin plugin;

    public BungeeListeners(Plugin plugin) {
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
        plugin.getProxy().getPluginManager().registerListener(plugin, (Listener) listener);
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
        plugin.getProxy().getPluginManager().unregisterListener((Listener) listener);
    }

    @Override
    public void unregisterListeners() {
        plugin.getProxy().getPluginManager().unregisterListeners(plugin);
    }
}
