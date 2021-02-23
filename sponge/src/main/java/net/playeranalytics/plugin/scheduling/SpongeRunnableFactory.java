package net.playeranalytics.plugin.scheduling;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

public class SpongeRunnableFactory implements RunnableFactory {

    private final Object plugin;

    public SpongeRunnableFactory(Object plugin) {this.plugin = plugin;}

    @Override
    public UnscheduledTask create(Runnable runnable) {
        return new UnscheduledSpongeTask(plugin, runnable, task -> {});
    }

    @Override
    public UnscheduledTask create(PluginRunnable runnable) {
        return new UnscheduledSpongeTask(plugin, runnable, runnable::setCancellable);
    }

    @Override
    public void cancelAllKnownTasks() {
        Sponge.getScheduler().getScheduledTasks(plugin).forEach(Task::cancel);
    }
}
