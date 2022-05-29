package net.playeranalytics.plugin.server;

import org.spongepowered.api.Sponge;
import org.spongepowered.plugin.PluginContainer;

import java.util.HashSet;
import java.util.Set;

public class SpongeListeners implements Listeners {

    private final PluginContainer plugin;
    private final Set<Object> listeners = new HashSet<>();

    public SpongeListeners(PluginContainer plugin) {
        this.plugin = plugin;
    }

    @Override
    public void registerListener(Object listener) {
        Sponge.eventManager().registerListeners(plugin, listener);
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    @Override
    public void unregisterListener(Object listener) {
        Sponge.eventManager().unregisterListeners(listener);
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    @Override
    public void unregisterListeners() {
        Set<Object> listeners;
        synchronized (this.listeners) {
            listeners = new HashSet<>(this.listeners);
            this.listeners.clear();
        }
        for (Object listener : listeners) {
            Sponge.eventManager().unregisterListeners(listener);
        }
    }
}
