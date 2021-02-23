package net.playeranalytics.plugin.scheduling;

import java.util.concurrent.TimeUnit;

public interface UnscheduledTask {

    Task runTaskAsynchronously();

    Task runTaskLaterAsynchronously(long delayTicks);

    Task runTaskTimerAsynchronously(long delayTicks, long periodTicks);

    Task runTask();

    Task runTaskLater(long delayTicks);

    Task runTaskTimer(long delayTicks, long periodTicks);

    default Task runTaskLaterAsynchronously(long delay, TimeUnit unit) {
        return runTaskLaterAsynchronously(TimeAmount.toTicks(delay, unit));
    }

    default Task runTaskTimerAsynchronously(long delay, TimeUnit delayUnit, long period, TimeUnit periodUnit) {
        return runTaskTimerAsynchronously(
                TimeAmount.toTicks(delay, delayUnit), TimeAmount.toTicks(period, periodUnit)
        );
    }

    default Task runTaskTimerAsynchronously(long delay, long period, TimeUnit unit) {
        return runTaskTimerAsynchronously(delay, unit, period, unit);
    }

    default Task runTaskLater(long delay, TimeUnit unit) {
        return runTaskLater(TimeAmount.toTicks(delay, unit));
    }

    default Task runTaskTimer(long delay, TimeUnit delayUnit, long period, TimeUnit periodUnit) {
        return runTaskTimer(
                TimeAmount.toTicks(delay, delayUnit), TimeAmount.toTicks(period, periodUnit)
        );
    }

    default Task runTaskTimer(long delay, long period, TimeUnit unit) {
        return runTaskTimer(delay, unit, period, unit);
    }
}
