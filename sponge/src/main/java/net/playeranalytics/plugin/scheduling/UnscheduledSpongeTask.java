package net.playeranalytics.plugin.scheduling;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.plugin.PluginContainer;

import java.util.function.Consumer;
import java.util.function.Function;

public class UnscheduledSpongeTask implements UnscheduledTask {

    private final PluginContainer plugin;
    private final Runnable runnable;
    private final Consumer<Task> cancellableConsumer;

    public UnscheduledSpongeTask(PluginContainer plugin, Runnable runnable, Consumer<Task> cancellableConsumer) {
        this.plugin = plugin;
        this.runnable = runnable;
        this.cancellableConsumer = cancellableConsumer;
    }

    private Task createTask(boolean async,
                            Function<org.spongepowered.api.scheduler.Task.Builder,
                                    org.spongepowered.api.scheduler.Task.Builder> apply) {
        org.spongepowered.api.scheduler.Task.Builder taskBuilder = org.spongepowered.api.scheduler.Task.builder()
                .execute(runnable)
                .plugin(plugin);
        taskBuilder = apply.apply(taskBuilder);

        Scheduler scheduler = async
                ? Sponge.asyncScheduler()
                : Sponge.server().scheduler();
        ScheduledTask scheduledTask = scheduler.submit(taskBuilder.build());

        SpongeTask task = new SpongeTask(
                scheduledTask,
                !async
        );
        cancellableConsumer.accept(task);
        return task;
    }

    @Override
    public Task runTaskAsynchronously() {
        return createTask(true, builder -> builder);
    }

    @Override
    public Task runTaskLaterAsynchronously(long delayTicks) {
        return createTask(true, builder -> builder.delay(Ticks.of(delayTicks)));
    }

    @Override
    public Task runTaskTimerAsynchronously(long delayTicks, long periodTicks) {
        return createTask(true,
                builder -> builder
                        .delay(Ticks.of(delayTicks))
                        .interval(Ticks.of(periodTicks)));
    }

    @Override
    public Task runTask() {
        return createTask(false, builder -> builder);
    }

    @Override
    public Task runTaskLater(long delayTicks) {
        return createTask(false, builder ->
                builder.delay(Ticks.of(delayTicks)));
    }

    @Override
    public Task runTaskTimer(long delayTicks, long periodTicks) {
        return createTask(false,
                builder -> builder
                        .delay(Ticks.of(delayTicks))
                        .interval(Ticks.of(periodTicks)));
    }
}
