package net.playeranalytics.plugin.scheduling;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.ServerScheduler;

public class NukkitRunnableFactory implements RunnableFactory {

    private final PluginBase plugin;

    public NukkitRunnableFactory(PluginBase plugin) {
        this.plugin = plugin;
    }

    @Override
    public UnscheduledTask create(Runnable runnable) {
        return new UnscheduledNukkitTask(plugin, getScheduler(), runnable, task -> {});
    }

    @Override
    public UnscheduledTask create(PluginRunnable runnable) {
        return new UnscheduledNukkitTask(plugin, getScheduler(), runnable, runnable::setCancellable);
    }

    @Override
    public void cancelAllKnownTasks() {
        getScheduler().cancelTask(plugin);
    }

    private ServerScheduler getScheduler() {
        return plugin.getServer().getScheduler();
    }
}
