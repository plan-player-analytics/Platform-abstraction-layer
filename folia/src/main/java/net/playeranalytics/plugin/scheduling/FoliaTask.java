package net.playeranalytics.plugin.scheduling;

public class FoliaTask implements Task {

    private final io.papermc.paper.threadedregions.scheduler.ScheduledTask task;
    private final boolean isAsync;

    public FoliaTask(io.papermc.paper.threadedregions.scheduler.ScheduledTask task, boolean isAsync) {
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
