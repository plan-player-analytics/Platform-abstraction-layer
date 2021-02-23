package net.playeranalytics.plugin.scheduling;

public abstract class PluginRunnable implements Runnable {

    Task toCancel;

    void setCancellable(Task toCancel) {
        this.toCancel = toCancel;
    }

    public void cancel() {
        if (toCancel != null) toCancel.cancel();
    }
}
