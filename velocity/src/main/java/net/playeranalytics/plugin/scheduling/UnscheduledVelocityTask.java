package net.playeranalytics.plugin.scheduling;

import com.velocitypowered.api.scheduler.Scheduler;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class UnscheduledVelocityTask implements UnscheduledTask {

    private final Object plugin;
    private final Scheduler scheduler;
    private final Runnable runnable;
    private final Consumer<Task> cancellableConsumer;

    public UnscheduledVelocityTask(Object plugin, Scheduler scheduler, Runnable runnable, Consumer<Task> cancellableConsumer) {
        this.plugin = plugin;
        this.scheduler = scheduler;
        this.runnable = runnable;
        this.cancellableConsumer = cancellableConsumer;
    }

    @Override
    public Task runTaskAsynchronously() {
        VelocityTask task = new VelocityTask(scheduler.buildTask(plugin, runnable).schedule());
        cancellableConsumer.accept(task);
        return task;
    }

    @Override
    public Task runTaskLaterAsynchronously(long delayTicks) {
        VelocityTask task = new VelocityTask(scheduler.buildTask(plugin, runnable)
                .delay(TimeAmount.ticksToMillis(delayTicks), TimeUnit.MILLISECONDS)
                .schedule());
        cancellableConsumer.accept(task);
        return task;
    }

    @Override
    public Task runTaskTimerAsynchronously(long delayTicks, long periodTicks) {
        VelocityTask task = new VelocityTask(scheduler.buildTask(plugin, runnable)
                .delay(TimeAmount.ticksToMillis(delayTicks), TimeUnit.MILLISECONDS)
                .repeat(TimeAmount.ticksToMillis(periodTicks), TimeUnit.MILLISECONDS)
                .schedule());
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
