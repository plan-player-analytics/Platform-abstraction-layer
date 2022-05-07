package net.playeranalytics.plugin;

import net.playeranalytics.plugin.scheduling.BukkitRunnableFactory;
import net.playeranalytics.plugin.scheduling.RunnableFactory;
import net.playeranalytics.plugin.server.BukkitListeners;
import net.playeranalytics.plugin.server.JavaUtilPluginLogger;
import net.playeranalytics.plugin.server.Listeners;
import net.playeranalytics.plugin.server.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitPlatformLayer implements PlatformAbstractionLayer {

    private final JavaPlugin plugin;

    private PluginLogger pluginLogger;
    private Listeners listeners;
    private RunnableFactory runnableFactory;
    private PluginInformation pluginInformation;

    public BukkitPlatformLayer(JavaPlugin plugin) {
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

}
