package net.playeranalytics.plugin.server;

import org.spongepowered.api.Sponge;

public class SpongeListeners implements Listeners {

    private final Object plugin;

    public SpongeListeners(Object plugin) {
        this.plugin = plugin;
    }

    @Override
    public void registerListener(Object listener) {
        Sponge.getEventManager().registerListeners(plugin, listener);
    }

    @Override
    public void unregisterListener(Object listener) {
        Sponge.getEventManager().unregisterListeners(listener);
    }

    @Override
    public void unregisterListeners() {
        Sponge.getEventManager().unregisterPluginListeners(plugin);
    }
}
