package net.playeranalytics.plugin;

import cn.nukkit.plugin.PluginBase;

import java.io.File;
import java.io.InputStream;

public class NukkitPluginInformation implements PluginInformation {

    private final PluginBase plugin;

    public NukkitPluginInformation(PluginBase plugin) {
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
