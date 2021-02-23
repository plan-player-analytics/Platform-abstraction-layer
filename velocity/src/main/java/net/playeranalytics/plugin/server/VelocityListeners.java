package net.playeranalytics.plugin.server;

import com.velocitypowered.api.proxy.ProxyServer;

public class VelocityListeners implements Listeners {

    private final Object plugin;
    private final ProxyServer proxy;

    public VelocityListeners(Object plugin, ProxyServer proxy) {
        this.plugin = plugin;
        this.proxy = proxy;
    }

    @Override
    public void registerListener(Object listener) {
        proxy.getEventManager().register(plugin, listener);
    }

    @Override
    public void unregisterListener(Object listener) {
        proxy.getEventManager().unregisterListener(plugin, listener);
    }

    @Override
    public void unregisterListeners() {
        proxy.getEventManager().unregisterListeners(plugin);
    }
}
