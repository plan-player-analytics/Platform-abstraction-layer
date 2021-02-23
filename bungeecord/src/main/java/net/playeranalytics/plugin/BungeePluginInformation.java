package net.playeranalytics.plugin;

import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;

public class BungeePluginInformation implements PluginInformation {

    private final Plugin plugin;

    public BungeePluginInformation(Plugin plugin) {
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
