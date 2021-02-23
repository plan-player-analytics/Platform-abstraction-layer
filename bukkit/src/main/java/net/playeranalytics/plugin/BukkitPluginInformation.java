package net.playeranalytics.plugin;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class BukkitPluginInformation implements PluginInformation {

    private final JavaPlugin plugin;

    public BukkitPluginInformation(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public File getDataFolder() {
        return plugin.getDataFolder();
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }
}
