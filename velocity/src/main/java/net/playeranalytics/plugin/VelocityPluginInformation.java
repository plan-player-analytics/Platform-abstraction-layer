package net.playeranalytics.plugin;

import com.velocitypowered.api.plugin.Plugin;

import java.io.File;

public class VelocityPluginInformation implements PluginInformation {

    private final Object plugin;
    private final File dataFolder;

    public VelocityPluginInformation(Object plugin, File dataFolder) {
        this.plugin = plugin;
        this.dataFolder = dataFolder;
    }

    @Override
    public File getDataFolder() {
        return dataFolder;
    }

    @Override
    public String getVersion() {
        return plugin.getClass().getAnnotation(Plugin.class).version();
    }
}
