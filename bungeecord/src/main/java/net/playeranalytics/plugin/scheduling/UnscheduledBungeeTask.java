package net.playeranalytics.plugin.scheduling;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.TaskScheduler;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class UnscheduledBungeeTask implements UnscheduledTask {

    private final Plugin plugin;
    private final Runnable runnable;
    private final Consumer<Task> cancellableConsumer;
    private final TaskScheduler scheduler;

    public UnscheduledBungeeTask(Plugin plugin, Runnable runnable, Consumer<Task> cancellableConsumer) {
        this.plugin = plugin;
        this.runnable = runnable;
        this.cancellableConsumer = cancellableConsumer;
        this.scheduler = plugin.getProxy().getScheduler();
    }

    @Override
    public Task runTaskAsynchronously() {
        BungeeTask task = new BungeeTask(scheduler.runAsync(plugin, runnable));
        cancellableConsumer.accept(task);
        return task;
    }

    @Override
    public Task runTaskLaterAsynchronously(long delayTicks) {
        BungeeTask task = new BungeeTask(scheduler.schedule(
                plugin,
                runnable,
                TimeAmount.ticksToMillis(delayTicks),
                TimeUnit.MILLISECONDS
        ));
        cancellableConsumer.accept(task);
        return task;
    }

    @Override
    public Task runTaskTimerAsynchronously(long delayTicks, long periodTicks) {
        BungeeTask task = new BungeeTask(scheduler.schedule(
                plugin,
                runnable,
                TimeAmount.ticksToMillis(delayTicks),
                TimeAmount.ticksToMillis(periodTicks),
                TimeUnit.MILLISECONDS
        ));
        cancellableConsumer.accept(task);
        return task;
    }

    @Override
    public Task runTask() {
        return runTaskAsynchronously();
    }

    @Override
    public Task runTaskLater(long delayTicks) {
        return runTaskLaterAsynchronously(delayTicks);
    }

    @Override
    public Task runTaskTimer(long delayTicks, long periodTicks) {
        return runTaskTimerAsynchronously(delayTicks, periodTicks);
    }
}
