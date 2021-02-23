package net.playeranalytics.plugin.scheduling;

public interface Task {

    boolean isGameThread();

    void cancel();

}
