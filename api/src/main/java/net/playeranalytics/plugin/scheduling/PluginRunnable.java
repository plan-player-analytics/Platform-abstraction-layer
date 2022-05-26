package net.playeranalytics.plugin.scheduling;

public abstract class PluginRunnable implements Runnable {

    Task toCancel;

    void setCancellable(Task toCancel) {
        this.toCancel = toCancel;
    }

    public void cancel() {
        if (toCancel != null) {
            try {
                toCancel.cancel();
            } catch (Exception ignored) {
                // Ignore cancel exceptions, they usually mean that task was already cancelled.
            }
        }
    }
}
