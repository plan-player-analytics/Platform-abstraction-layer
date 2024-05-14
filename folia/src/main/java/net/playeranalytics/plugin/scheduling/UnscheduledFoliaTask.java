package net.playeranalytics.plugin.scheduling;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class UnscheduledFoliaTask extends BukkitRunnable implements UnscheduledTask {

    private final JavaPlugin plugin;
    private final Runnable runnable;
    private final Consumer<Task> cancellableConsumer;
    private final AsyncScheduler asyncScheduler;
    private final GlobalRegionScheduler globalRegionScheduler;

    public UnscheduledFoliaTask(JavaPlugin plugin, Runnable runnable, Consumer<Task> cancellableConsumer) {
        this.plugin = plugin;
        this.runnable = runnable;
        this.cancellableConsumer = cancellableConsumer;
        this.asyncScheduler = plugin.getServer().getAsyncScheduler();
        this.globalRegionScheduler = plugin.getServer().getGlobalRegionScheduler();
    }

    @Override
    public void run() {
        runnable.run();
    }

    @Override
    public Task runTaskAsynchronously() {
        FoliaTask task = new FoliaTask(asyncScheduler.runNow(plugin, (plugin) -> runnable.run()), true);
        cancellableConsumer.accept(task);
        return task;
    }

    @Override
    public Task runTaskLaterAsynchronously(long delayTicks) {
        FoliaTask task = new FoliaTask(asyncScheduler.runDelayed(plugin, (plugin) -> runnable.run(), delayTicks / 20, TimeUnit.MILLISECONDS), true);
        cancellableConsumer.accept(task);
        return task;
    }

    @Override
    public Task runTaskTimerAsynchronously(long delayTicks, long periodTicks) {
        FoliaTask task = new FoliaTask(asyncScheduler.runAtFixedRate(plugin, (plugin) -> runnable.run(), delayTicks / 20, periodTicks / 20, TimeUnit.MILLISECONDS), true);
        cancellableConsumer.accept(task);
        return task;
    }

    @Override
    public Task runTask() {
        FoliaTask task = new FoliaTask(globalRegionScheduler.run(plugin, (plugin) -> runnable.run()), false);
        cancellableConsumer.accept(task);
        return task;
    }

    @Override
    public Task runTaskLater(long delayTicks) {
        FoliaTask task = new FoliaTask(globalRegionScheduler.runDelayed(plugin, (plugin) -> runnable.run(), delayTicks), false);
        cancellableConsumer.accept(task);
        return task;
    }

    @Override
    public Task runTaskTimer(long delayTicks, long periodTicks) {
        FoliaTask task = new FoliaTask(globalRegionScheduler.runAtFixedRate(plugin, (plugin) -> runnable.run(), delayTicks, periodTicks), false);
        cancellableConsumer.accept(task);
        return task;
    }
}
