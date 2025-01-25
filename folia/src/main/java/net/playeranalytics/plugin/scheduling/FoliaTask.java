package net.playeranalytics.plugin.scheduling;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

public class FoliaTask implements Task {

    private final ScheduledTask task;
    private final boolean isAsync;

    public FoliaTask(ScheduledTask task, boolean isAsync) {
        this.task = task;
        this.isAsync = isAsync;
    }

    @Override
    public boolean isGameThread() {
        return !isAsync;
    }

    @Override
    public void cancel() {
        task.cancel();
    }
}
