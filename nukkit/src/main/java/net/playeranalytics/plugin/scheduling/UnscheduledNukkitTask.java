package net.playeranalytics.plugin.scheduling;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.ServerScheduler;

import java.util.function.Consumer;

public class UnscheduledNukkitTask implements UnscheduledTask {

    private final PluginBase plugin;
    private final ServerScheduler scheduler;
    private final Runnable runnable;
    private final Consumer<Task> cancellableConsumer;

    public UnscheduledNukkitTask(PluginBase plugin, ServerScheduler scheduler, Runnable runnable, Consumer<Task> cancellableConsumer) {
        this.plugin = plugin;
        this.scheduler = scheduler;
        this.runnable = runnable;
        this.cancellableConsumer = cancellableConsumer;
    }


    @Override
    public Task runTaskAsynchronously() {
        NukkitTask task = new NukkitTask(scheduler.scheduleTask(plugin, runnable, true));
        cancellableConsumer.accept(task);
        return task;
    }

    @Override
    public Task runTaskLaterAsynchronously(long delayTicks) {
        NukkitTask task = new NukkitTask(scheduler.scheduleDelayedTask(plugin, runnable, (int) delayTicks, true));
        cancellableConsumer.accept(task);
        return task;
    }

    @Override
    public Task runTaskTimerAsynchronously(long delayTicks, long periodTicks) {
        NukkitTask task = new NukkitTask(scheduler.scheduleDelayedRepeatingTask(plugin, runnable, (int) delayTicks, (int) periodTicks, true));
        cancellableConsumer.accept(task);
        return task;
    }

    @Override
    public Task runTask() {
        NukkitTask task = new NukkitTask(scheduler.scheduleTask(plugin, runnable));
        cancellableConsumer.accept(task);
        return task;
    }

    @Override
    public Task runTaskLater(long delayTicks) {
        NukkitTask task = new NukkitTask(scheduler.scheduleDelayedTask(plugin, runnable, (int) delayTicks));
        cancellableConsumer.accept(task);
        return task;
    }

    @Override
    public Task runTaskTimer(long delayTicks, long periodTicks) {
        NukkitTask task = new NukkitTask(scheduler.scheduleDelayedRepeatingTask(plugin, runnable, (int) delayTicks, (int) periodTicks));
        cancellableConsumer.accept(task);
        return task;
    }
}
