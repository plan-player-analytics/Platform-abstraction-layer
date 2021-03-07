package net.playeranalytics.plugin;

import org.spongepowered.api.plugin.Plugin;

import java.io.File;
import java.io.InputStream;

public class SpongePluginInformation implements PluginInformation {

    private final Object plugin;
    private final File dataFolder;

    public SpongePluginInformation(Object plugin, File dataFolder) {
        this.plugin = plugin;
        this.dataFolder = dataFolder;
    }

    @Override
    public InputStream getResourceFromJar(String byName) {
        return getClass().getResourceAsStream("/" + byName);
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
