package net.playeranalytics.plugin;

import net.playeranalytics.plugin.dependencies.DependencyLoader;
import net.playeranalytics.plugin.scheduling.RunnableFactory;
import net.playeranalytics.plugin.server.Listeners;
import net.playeranalytics.plugin.server.PluginLogger;

public interface PlatformAbstractionLayer {

    PluginLogger getPluginLogger();

    Listeners getListeners();

    RunnableFactory getRunnableFactory();

    PluginInformation getPluginInformation();

    DependencyLoader getDependencyLoader();
}
