package net.playeranalytics.plugin.scheduling;

public class BukkitTask implements Task {

    private final org.bukkit.scheduler.BukkitTask task;

    public BukkitTask(org.bukkit.scheduler.BukkitTask task) {this.task = task;}

    @Override
    public boolean isGameThread() {
        return task.isSync();
    }

    @Override
    public void cancel() {
        task.cancel();
    }
}
