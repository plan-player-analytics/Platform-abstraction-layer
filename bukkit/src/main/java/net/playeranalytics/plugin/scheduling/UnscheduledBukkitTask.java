package net.playeranalytics.plugin.scheduling;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Consumer;

public class UnscheduledBukkitTask extends BukkitRunnable implements UnscheduledTask {

    private final JavaPlugin plugin;
    private final Runnable runnable;
    private final Consumer<Task> cancellableConsumer;

    public UnscheduledBukkitTask(JavaPlugin plugin, Runnable runnable, Consumer<Task> cancellableConsumer) {
        this.plugin = plugin;
        this.runnable = runnable;
        this.cancellableConsumer = cancellableConsumer;
    }

    @Override
    public void run() {
        runnable.run();
    }

    @Override
    public Task runTaskAsynchronously() {
        BukkitTask task = new BukkitTask(super.runTaskAsynchronously(plugin));
        cancellableConsumer.accept(task);
        return task;
    }

    @Override
    public Task runTaskLaterAsynchronously(long delayTicks) {
        BukkitTask task = new BukkitTask(super.runTaskLaterAsynchronously(plugin, delayTicks));
        cancellableConsumer.accept(task);
        return task;
    }

    @Override
    public Task runTaskTimerAsynchronously(long delayTicks, long periodTicks) {
        BukkitTask task = new BukkitTask(super.runTaskTimerAsynchronously(plugin, delayTicks, periodTicks));
        cancellableConsumer.accept(task);
        return task;
    }

    @Override
    public Task runTask() {
        BukkitTask task = new BukkitTask(super.runTask(plugin));
        cancellableConsumer.accept(task);
        return task;
    }

    @Override
    public Task runTaskLater(long delayTicks) {
        BukkitTask task = new BukkitTask(super.runTaskLater(plugin, delayTicks));
        cancellableConsumer.accept(task);
        return task;
    }

    @Override
    public Task runTaskTimer(long delayTicks, long periodTicks) {
        BukkitTask task = new BukkitTask(super.runTaskTimer(plugin, delayTicks, periodTicks));
        cancellableConsumer.accept(task);
        return task;
    }
}
