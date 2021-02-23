package net.playeranalytics.plugin.scheduling;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.TaskStatus;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class VelocityRunnableFactory implements RunnableFactory {

    private final Object plugin;
    private final ProxyServer proxy;

    private final Set<VelocityTask> tasks;

    public VelocityRunnableFactory(Object plugin, ProxyServer proxy) {
        this.plugin = plugin;
        this.proxy = proxy;
        tasks = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }

    @Override
    public UnscheduledTask create(Runnable runnable) {
        cleanUp();
        return new UnscheduledVelocityTask(plugin, proxy.getScheduler(), runnable,
                task -> tasks.add((VelocityTask) task)
        );
    }

    @Override
    public UnscheduledTask create(PluginRunnable runnable) {
        cleanUp();
        return new UnscheduledVelocityTask(plugin, proxy.getScheduler(), runnable, task -> {
            tasks.add((VelocityTask) task);
            runnable.setCancellable(task);
        });
    }

    private synchronized void cleanUp() {
        tasks.removeIf(task -> task.status() != TaskStatus.SCHEDULED);
    }

    @Override
    public void cancelAllKnownTasks() {
        tasks.forEach(Task::cancel);
        tasks.clear();
    }
}
