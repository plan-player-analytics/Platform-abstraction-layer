package net.playeranalytics.plugin;

import net.playeranalytics.plugin.dependencies.DependencyLoader;
import net.playeranalytics.plugin.scheduling.BukkitRunnableFactory;
import net.playeranalytics.plugin.scheduling.RunnableFactory;
import net.playeranalytics.plugin.server.BukkitListeners;
import net.playeranalytics.plugin.server.JavaUtilPluginLogger;
import net.playeranalytics.plugin.server.Listeners;
import net.playeranalytics.plugin.server.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URLClassLoader;

public class BukkitPlatformLayer implements PlatformAbstractionLayer {

    private final JavaPlugin plugin;

    private PluginLogger pluginLogger;
    private Listeners listeners;
    private RunnableFactory runnableFactory;
    private PluginInformation pluginInformation;
    private DependencyLoader dependencyLoader;

    public BukkitPlatformLayer(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public PluginLogger getPluginLogger() {
        if (pluginLogger == null) pluginLogger = new JavaUtilPluginLogger(plugin.getLogger());
        return pluginLogger;
    }

    @Override
    public Listeners getListeners() {
        if (listeners == null) listeners = new BukkitListeners(plugin);
        return listeners;
    }

    @Override
    public RunnableFactory getRunnableFactory() {
        if (runnableFactory == null) runnableFactory = new BukkitRunnableFactory(plugin);
        return runnableFactory;
    }

    @Override
    public PluginInformation getPluginInformation() {
        if (pluginInformation == null) pluginInformation = new BukkitPluginInformation(plugin);
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
