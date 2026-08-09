package net.playeranalytics.plugin.scheduling;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class FoliaRunnableFactory implements RunnableFactory {

    private final JavaPlugin plugin;
    private final AtomicLong lifecycle = new AtomicLong();
    private final ReentrantLock lifecycleLock = new ReentrantLock();
    private final Condition tasksFinished = lifecycleLock.newCondition();
    private final ThreadLocal<Integer> executionDepth = ThreadLocal.withInitial(() -> 0);
    private int runningTasks;

    public FoliaRunnableFactory(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public UnscheduledTask create(Runnable runnable) {
        return new UnscheduledFoliaTask(plugin, guardCurrentLifecycle(runnable), task -> {});
    }

    @Override
    public UnscheduledTask create(PluginRunnable runnable) {
        return new UnscheduledFoliaTask(plugin, guardCurrentLifecycle(runnable), runnable::setCancellable);
    }

    private Runnable guardCurrentLifecycle(Runnable runnable) {
        long scheduledLifecycle = lifecycle.get();
        return () -> {
            if (!startTask(scheduledLifecycle)) {
                return;
            }
            executionDepth.set(executionDepth.get() + 1);
            try {
                runnable.run();
            } finally {
                int newDepth = executionDepth.get() - 1;
                if (newDepth == 0) {
                    executionDepth.remove();
                } else {
                    executionDepth.set(newDepth);
                }
                finishTask();
            }
        };
    }

    private boolean startTask(long scheduledLifecycle) {
        lifecycleLock.lock();
        try {
            if (scheduledLifecycle != lifecycle.get()) {
                return false;
            }
            runningTasks++;
            return true;
        } finally {
            lifecycleLock.unlock();
        }
    }

    private void finishTask() {
        lifecycleLock.lock();
        try {
            runningTasks--;
            tasksFinished.signalAll();
        } finally {
            lifecycleLock.unlock();
        }
    }

    @Override
    public void cancelAllKnownTasks() {
        lifecycle.incrementAndGet();
        try {
            plugin.getServer().getAsyncScheduler().cancelTasks(plugin);
        } finally {
            try {
                plugin.getServer().getGlobalRegionScheduler().cancelTasks(plugin);
            } finally {
                awaitRunningTasks();
            }
        }
    }

    private void awaitRunningTasks() {
        int tasksOnCurrentThread = executionDepth.get();
        lifecycleLock.lock();
        try {
            while (runningTasks > tasksOnCurrentThread) {
                tasksFinished.awaitUninterruptibly();
            }
        } finally {
            lifecycleLock.unlock();
        }
    }
}
