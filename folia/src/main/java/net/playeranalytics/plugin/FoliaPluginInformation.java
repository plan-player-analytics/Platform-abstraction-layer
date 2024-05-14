package net.playeranalytics.plugin;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;

public class FoliaPluginInformation implements PluginInformation {

    private final JavaPlugin plugin;

    public FoliaPluginInformation(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public InputStream getResourceFromJar(String byName) {
        return plugin.getResource(byName);
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
