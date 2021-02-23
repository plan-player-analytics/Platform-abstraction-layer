package net.playeranalytics.plugin.server;

public interface PluginLogger {

    PluginLogger info(String message);

    PluginLogger warn(String message);

    PluginLogger error(String message);

    PluginLogger warn(String message, Throwable throwable);

    PluginLogger error(String message, Throwable throwable);

}
