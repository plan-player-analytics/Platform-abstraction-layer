package net.playeranalytics.plugin.scheduling;

import net.md_5.bungee.api.scheduler.ScheduledTask;

public class BungeeTask implements Task {

    private final ScheduledTask task;

    public BungeeTask(ScheduledTask task) {this.task = task;}

    @Override
    public boolean isGameThread() {
        return false;
    }

    @Override
    public void cancel() {
        task.cancel();
    }
}
