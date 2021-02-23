package net.playeranalytics.plugin;

import net.playeranalytics.plugin.dependencies.DependencyLoader;
import net.playeranalytics.plugin.scheduling.RunnableFactory;
import net.playeranalytics.plugin.scheduling.SpongeRunnableFactory;
import net.playeranalytics.plugin.server.Listeners;
import net.playeranalytics.plugin.server.PluginLogger;
import net.playeranalytics.plugin.server.SLF4JPluginLogger;
import net.playeranalytics.plugin.server.SpongeListeners;
import org.slf4j.Logger;

import java.io.File;
import java.net.URLClassLoader;

public class SpongePlatformLayer implements PlatformAbstractionLayer {

    private final Object plugin;
    private final File dataFolder;
    private final Logger logger;

    private PluginLogger pluginLogger;
    private Listeners listeners;
    private RunnableFactory runnableFactory;
    private PluginInformation pluginInformation;
    private DependencyLoader dependencyLoader;

    public SpongePlatformLayer(Object plugin, File dataFolder, Logger logger) {
        this.plugin = plugin;
        this.dataFolder = dataFolder;
        this.logger = logger;
    }

    @Override
    public PluginLogger getPluginLogger() {
        if (pluginLogger == null) pluginLogger = new SLF4JPluginLogger(logger);
        return pluginLogger;
    }

    @Override
    public Listeners getListeners() {
        if (listeners == null) listeners = new SpongeListeners(plugin);
        return listeners;
    }

    @Override
    public RunnableFactory getRunnableFactory() {
        if (runnableFactory == null) runnableFactory = new SpongeRunnableFactory(plugin);
        return runnableFactory;
    }

    @Override
    public PluginInformation getPluginInformation() {
        if (pluginInformation == null) pluginInformation = new SpongePluginInformation(plugin, dataFolder);
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
