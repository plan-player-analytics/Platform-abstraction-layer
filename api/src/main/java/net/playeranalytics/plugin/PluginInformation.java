package net.playeranalytics.plugin;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;

public interface PluginInformation {

    InputStream getResourceFromJar(String byName);

    File getDataFolder();

    default Path getDataDirectory() {
        return getDataFolder().toPath();
    }

    String getVersion();

}
