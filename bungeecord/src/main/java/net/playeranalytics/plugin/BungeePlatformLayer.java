package net.playeranalytics.plugin;

import net.md_5.bungee.api.plugin.Plugin;
import net.playeranalytics.plugin.dependencies.DependencyLoader;
import net.playeranalytics.plugin.scheduling.BungeeRunnableFactory;
import net.playeranalytics.plugin.scheduling.RunnableFactory;
import net.playeranalytics.plugin.server.BungeeListeners;
import net.playeranalytics.plugin.server.JavaUtilPluginLogger;
import net.playeranalytics.plugin.server.Listeners;
import net.playeranalytics.plugin.server.PluginLogger;

import java.net.URLClassLoader;

public class BungeePlatformLayer implements PlatformAbstractionLayer {

    private final Plugin plugin;

    private PluginLogger pluginLogger;
    private Listeners listeners;
    private RunnableFactory runnableFactory;
    private PluginInformation pluginInformation;
    private DependencyLoader dependencyLoader;

    public BungeePlatformLayer(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public PluginLogger getPluginLogger() {
        if (pluginLogger == null) pluginLogger = new JavaUtilPluginLogger(plugin.getLogger());
        return pluginLogger;
    }

    @Override
    public Listeners getListeners() {
        if (listeners == null) listeners = new BungeeListeners(plugin);
        return listeners;
    }

    @Override
    public RunnableFactory getRunnableFactory() {
        if (runnableFactory == null) runnableFactory = new BungeeRunnableFactory(plugin);
        return runnableFactory;
    }

    @Override
    public PluginInformation getPluginInformation() {
        if (pluginInformation == null) pluginInformation = new BungeePluginInformation(plugin);
        return pluginInformation;
    }

    @Override
    public DependencyLoader getDependencyLoader() {
        if (dependencyLoader == null) {
            dependencyLoader = new DependencyLoader(
                    (URLClassLoader) getClass().getClassLoader(),
                    getPluginLogger(),
                    getPluginInformation()
            );
        }
        return dependencyLoader;
    }
}
