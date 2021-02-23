package net.playeranalytics.plugin.scheduling;

public interface RunnableFactory {

    UnscheduledTask create(Runnable runnable);

    UnscheduledTask create(PluginRunnable runnable);

    void cancelAllKnownTasks();
}
