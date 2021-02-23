package net.playeranalytics.plugin.scheduling;

import org.bukkit.plugin.java.JavaPlugin;

public class BukkitRunnableFactory implements RunnableFactory {

    private final JavaPlugin plugin;

    public BukkitRunnableFactory(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public UnscheduledTask create(Runnable runnable) {
        return new UnscheduledBukkitTask(plugin, runnable, task -> {});
    }

    @Override
    public UnscheduledTask create(PluginRunnable runnable) {
        return new UnscheduledBukkitTask(plugin, runnable, runnable::setCancellable);
    }

    @Override
    public void cancelAllKnownTasks() {
        plugin.getServer().getScheduler().cancelTasks(plugin);
    }
}
