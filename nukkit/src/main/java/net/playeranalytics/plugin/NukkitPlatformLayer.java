package net.playeranalytics.plugin;

import cn.nukkit.plugin.PluginBase;
import net.playeranalytics.plugin.dependencies.DependencyLoader;
import net.playeranalytics.plugin.scheduling.NukkitRunnableFactory;
import net.playeranalytics.plugin.scheduling.RunnableFactory;
import net.playeranalytics.plugin.server.Listeners;
import net.playeranalytics.plugin.server.NukkitListeners;
import net.playeranalytics.plugin.server.NukkitPluginLogger;
import net.playeranalytics.plugin.server.PluginLogger;

import java.net.URLClassLoader;

public class NukkitPlatformLayer implements PlatformAbstractionLayer {

    private final PluginBase plugin;

    private PluginLogger pluginLogger;
    private Listeners listeners;
    private RunnableFactory runnableFactory;
    private PluginInformation pluginInformation;
    private DependencyLoader dependencyLoader;

    public NukkitPlatformLayer(PluginBase plugin) {
        this.plugin = plugin;
    }

    @Override
    public PluginLogger getPluginLogger() {
        if (pluginLogger == null) pluginLogger = new NukkitPluginLogger(plugin.getLogger());
        return pluginLogger;
    }

    @Override
    public Listeners getListeners() {
        if (listeners == null) listeners = new NukkitListeners(plugin);
        return listeners;
    }

    @Override
    public RunnableFactory getRunnableFactory() {
        if (runnableFactory == null) runnableFactory = new NukkitRunnableFactory(plugin);
        return runnableFactory;
    }

    @Override
    public PluginInformation getPluginInformation() {
        if (pluginInformation == null) pluginInformation = new NukkitPluginInformation(plugin);
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
