package net.playeranalytics.plugin;

import java.io.File;
import java.nio.file.Path;

public interface PluginInformation {

    File getDataFolder();

    default Path getDataDirectory() {
        return getDataFolder().toPath();
    }

    String getVersion();

}
