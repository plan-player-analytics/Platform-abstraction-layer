package net.playeranalytics.plugin;

import net.playeranalytics.plugin.scheduling.FoliaRunnableFactory;
import net.playeranalytics.plugin.scheduling.RunnableFactory;
import net.playeranalytics.plugin.server.FoliaListeners;
import net.playeranalytics.plugin.server.JavaUtilPluginLogger;
import net.playeranalytics.plugin.server.Listeners;
import net.playeranalytics.plugin.server.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

public class FoliaPlatformLayer implements PlatformAbstractionLayer {

    private final JavaPlugin plugin;

    private PluginLogger pluginLogger;
    private Listeners listeners;
    private RunnableFactory runnableFactory;
    private PluginInformation pluginInformation;

    public FoliaPlatformLayer(JavaPlugin plugin) {
        if (!isFolia()) {
            throw new IllegalStateException("Tried to enable Folia plugin on non-Folia server!");
        }
        this.plugin = plugin;
    }

    @Override
    public PluginLogger getPluginLogger() {
        if (pluginLogger == null) {
            String pluginName = plugin.getDescription().getName();
            pluginLogger = new JavaUtilPluginLogger(
                    plugin.getLogger(),
                    coloredMessage -> plugin.getServer().getConsoleSender().sendMessage("[" + pluginName + "] " + coloredMessage)
            );
        }
        return pluginLogger;
    }

    @Override
    public Listeners getListeners() {
        if (listeners == null) listeners = new FoliaListeners(plugin);
        return listeners;
    }

    @Override
    public RunnableFactory getRunnableFactory() {
        if (runnableFactory == null) runnableFactory = new FoliaRunnableFactory(plugin);
        return runnableFactory;
    }

    @Override
    public PluginInformation getPluginInformation() {
        if (pluginInformation == null) pluginInformation = new FoliaPluginInformation(plugin);
        return pluginInformation;
    }

    private static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
