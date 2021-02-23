package net.playeranalytics.plugin.server;

import org.slf4j.Logger;

public class SLF4JPluginLogger implements PluginLogger {

    private final Logger logger;

    public SLF4JPluginLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public PluginLogger info(String message) {
        logger.info(message);
        return this;
    }

    @Override
    public PluginLogger warn(String message) {
        logger.warn(message);
        return this;
    }

    @Override
    public PluginLogger error(String message) {
        logger.error(message);
        return this;
    }

    @Override
    public PluginLogger warn(String message, Throwable throwable) {
        logger.warn(message, throwable);
        return this;
    }

    @Override
    public PluginLogger error(String message, Throwable throwable) {
        logger.error(message, throwable);
        return this;
    }
}
