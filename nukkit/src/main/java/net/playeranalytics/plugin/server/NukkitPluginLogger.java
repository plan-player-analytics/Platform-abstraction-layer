package net.playeranalytics.plugin.server;

public class NukkitPluginLogger implements PluginLogger {

    private final cn.nukkit.plugin.PluginLogger logger;

    public NukkitPluginLogger(cn.nukkit.plugin.PluginLogger logger) {
        this.logger = logger;
    }

    @Override
    public PluginLogger info(String message) {
        logger.info(message);
        return this;
    }

    @Override
    public PluginLogger warn(String message) {
        logger.warning(message);
        return this;
    }

    @Override
    public PluginLogger error(String message) {
        logger.error(message);
        return this;
    }

    @Override
    public PluginLogger warn(String message, Throwable throwable) {
        logger.warning(message, throwable);
        return this;
    }

    @Override
    public PluginLogger error(String message, Throwable throwable) {
        logger.error(message, throwable);
        return this;
    }
}
