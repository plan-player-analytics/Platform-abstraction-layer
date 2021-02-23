package net.playeranalytics.plugin.scheduling;

import com.velocitypowered.api.scheduler.ScheduledTask;
import com.velocitypowered.api.scheduler.TaskStatus;

public class VelocityTask implements Task {

    private final ScheduledTask task;

    public VelocityTask(ScheduledTask task) {
        this.task = task;
    }

    @Override
    public boolean isGameThread() {
        return false;
    }

    @Override
    public void cancel() {
        task.cancel();
    }

    public TaskStatus status() {return task.status();}
}
