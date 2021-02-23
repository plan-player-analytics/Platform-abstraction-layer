package net.playeranalytics.plugin;

import cn.nukkit.plugin.PluginBase;

import java.io.File;

public class NukkitPluginInformation implements PluginInformation {

    private final PluginBase plugin;

    public NukkitPluginInformation(PluginBase plugin) {
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
