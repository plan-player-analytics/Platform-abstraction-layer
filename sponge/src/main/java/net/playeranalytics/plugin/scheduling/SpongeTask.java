package net.playeranalytics.plugin.scheduling;

import org.spongepowered.api.scheduler.ScheduledTask;

public class SpongeTask implements Task {

    private final ScheduledTask task;
    private final boolean gameThread;

    public SpongeTask(ScheduledTask task, boolean gameThread) {
        this.task = task;
        this.gameThread = gameThread;
    }

    @Override
    public boolean isGameThread() {
        return gameThread;
    }

    @Override
    public void cancel() {
        task.cancel();
    }
}
