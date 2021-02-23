package net.playeranalytics.plugin.scheduling;

import net.md_5.bungee.api.plugin.Plugin;

public class BungeeRunnableFactory implements RunnableFactory {

    private final Plugin plugin;

    public BungeeRunnableFactory(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public UnscheduledTask create(Runnable runnable) {
        return new UnscheduledBungeeTask(plugin, runnable, task -> {});
    }

    @Override
    public UnscheduledTask create(PluginRunnable runnable) {
        return new UnscheduledBungeeTask(plugin, runnable, runnable::setCancellable);
    }

    @Override
    public void cancelAllKnownTasks() {
        plugin.getProxy().getScheduler().cancel(plugin);
    }
}
