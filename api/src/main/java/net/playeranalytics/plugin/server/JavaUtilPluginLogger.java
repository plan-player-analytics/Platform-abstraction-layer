package net.playeranalytics.plugin.server;

import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaUtilPluginLogger implements PluginLogger {

    private final Logger logger;
    private final Consumer<String> coloredInfoLogger;

    public JavaUtilPluginLogger(Logger logger) {
        this(logger, logger::info);
    }

    public JavaUtilPluginLogger(Logger logger, Consumer<String> coloredInfoLogger) {
        this.logger = logger;
        this.coloredInfoLogger = coloredInfoLogger;
    }

    @Override
    public PluginLogger info(String message) {
        if (!message.isEmpty() && message.charAt(0) == '\u00a7') { // §
            coloredInfoLogger.accept(message);
        } else {
            logger.info(message);
        }
        return this;
    }

    @Override
    public PluginLogger warn(String message) {
        logger.warning(message);
        return this;
    }

    @Override
    public PluginLogger error(String message) {
        logger.severe(message);
        return this;
    }

    @Override
    public PluginLogger warn(String message, Throwable throwable) {
        logger.log(Level.WARNING, message, throwable);
        return this;
    }

    @Override
    public PluginLogger error(String message, Throwable throwable) {
        logger.log(Level.SEVERE, message, throwable);
        return this;
    }
}
