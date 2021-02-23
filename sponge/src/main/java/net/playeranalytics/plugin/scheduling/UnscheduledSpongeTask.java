package net.playeranalytics.plugin.scheduling;

import java.util.function.Consumer;

public class UnscheduledSpongeTask implements UnscheduledTask {

    private final Object plugin;
    private final Runnable runnable;
    private final Consumer<Task> cancellableConsumer;

    public UnscheduledSpongeTask(Object plugin, Runnable runnable, Consumer<Task> cancellableConsumer) {
        this.plugin = plugin;
        this.runnable = runnable;
        this.cancellableConsumer = cancellableConsumer;
    }

    @Override
    public Task runTaskAsynchronously() {
        SpongeTask task = new SpongeTask(
                org.spongepowered.api.scheduler.Task.builder()
                        .execute(runnable)
                        .async()
                        .submit(plugin)
        );
        cancellableConsumer.accept(task);
        return task;
    }

    @Override
    public Task runTaskLaterAsynchronously(long delayTicks) {
        SpongeTask task = new SpongeTask(
                org.spongepowered.api.scheduler.Task.builder()
                        .execute(runnable)
                        .delayTicks(delayTicks)
                        .async()
                        .submit(plugin)
        );
        cancellableConsumer.accept(task);
        return task;
    }

    @Override
    public Task runTaskTimerAsynchronously(long delayTicks, long periodTicks) {
        SpongeTask task = new SpongeTask(
                org.spongepowered.api.scheduler.Task.builder()
                        .execute(runnable)
                        .delayTicks(delayTicks)
                        .intervalTicks(periodTicks)
                        .async()
                        .submit(plugin)
        );
        cancellableConsumer.accept(task);
        return task;
    }

    @Override
    public Task runTask() {
        SpongeTask task = new SpongeTask(
                org.spongepowered.api.scheduler.Task.builder()
                        .execute(runnable)
                        .submit(plugin)
        );
        cancellableConsumer.accept(task);
        return task;
    }

    @Override
    public Task runTaskLater(long delayTicks) {
        SpongeTask task = new SpongeTask(
                org.spongepowered.api.scheduler.Task.builder()
                        .execute(runnable)
                        .delayTicks(delayTicks)
                        .submit(plugin)
        );
        cancellableConsumer.accept(task);
        return task;
    }

    @Override
    public Task runTaskTimer(long delayTicks, long periodTicks) {
        SpongeTask task = new SpongeTask(
                org.spongepowered.api.scheduler.Task.builder()
                        .execute(runnable)
                        .delayTicks(delayTicks)
                        .intervalTicks(periodTicks)
                        .submit(plugin)
        );
        cancellableConsumer.accept(task);
        return task;
    }
}
