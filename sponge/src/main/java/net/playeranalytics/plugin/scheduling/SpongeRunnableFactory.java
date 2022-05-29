package net.playeranalytics.plugin.scheduling;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.plugin.PluginContainer;

public class SpongeRunnableFactory implements RunnableFactory {

    private final PluginContainer plugin;

    public SpongeRunnableFactory(PluginContainer plugin) {this.plugin = plugin;}

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
        Sponge.asyncScheduler().tasks(plugin).forEach(ScheduledTask::cancel);
        Sponge.server().scheduler().tasks(plugin).forEach(ScheduledTask::cancel);
    }
}
