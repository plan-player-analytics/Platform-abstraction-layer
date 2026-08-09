package net.playeranalytics.plugin.scheduling;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FoliaRunnableFactoryTest {

    private JavaPlugin plugin;
    private Server server;
    private AsyncScheduler asyncScheduler;
    private GlobalRegionScheduler globalRegionScheduler;
    private ScheduledTask scheduledTask;

    private FoliaRunnableFactory factory;

    @BeforeEach
    void setUp() {
        plugin = mock(JavaPlugin.class);
        server = mock(Server.class);
        asyncScheduler = mock(AsyncScheduler.class);
        globalRegionScheduler = mock(GlobalRegionScheduler.class);
        scheduledTask = mock(ScheduledTask.class);
        when(plugin.getServer()).thenReturn(server);
        when(server.getAsyncScheduler()).thenReturn(asyncScheduler);
        when(server.getGlobalRegionScheduler()).thenReturn(globalRegionScheduler);
        factory = new FoliaRunnableFactory(plugin);
    }

    @Test
    void convertsTicksToMillisecondsForAsyncTasks() {
        when(asyncScheduler.runDelayed(eq(plugin), any(), anyLong(), any())).thenReturn(scheduledTask);
        when(asyncScheduler.runAtFixedRate(eq(plugin), any(), anyLong(), anyLong(), any())).thenReturn(scheduledTask);

        factory.create(() -> {}).runTaskLaterAsynchronously(20L);
        factory.create(() -> {}).runTaskTimerAsynchronously(40L, 60L);

        verify(asyncScheduler).runDelayed(eq(plugin), any(), eq(1_000L), eq(TimeUnit.MILLISECONDS));
        verify(asyncScheduler).runAtFixedRate(
                eq(plugin), any(), eq(2_000L), eq(3_000L), eq(TimeUnit.MILLISECONDS)
        );
    }

    @Test
    void cancelledLifecycleTasksDoNotRunAfterReload() {
        when(asyncScheduler.runNow(eq(plugin), any())).thenReturn(scheduledTask);
        AtomicInteger executions = new AtomicInteger();
        ArgumentCaptor<Consumer<ScheduledTask>> callbacks = consumerCaptor();

        factory.create(executions::incrementAndGet).runTaskAsynchronously();
        factory.cancelAllKnownTasks();
        factory.create(executions::incrementAndGet).runTaskAsynchronously();
        verify(asyncScheduler, times(2)).runNow(eq(plugin), callbacks.capture());

        List<Consumer<ScheduledTask>> capturedCallbacks = callbacks.getAllValues();
        capturedCallbacks.get(0).accept(scheduledTask);
        capturedCallbacks.get(1).accept(scheduledTask);

        assertEquals(1, executions.get());
        verify(asyncScheduler).cancelTasks(plugin);
        verify(globalRegionScheduler).cancelTasks(plugin);
    }

    @Test
    void cancellationWaitsForRunningTaskBeforeReturning() throws Exception {
        when(asyncScheduler.runNow(eq(plugin), any())).thenReturn(scheduledTask);
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        ArgumentCaptor<Consumer<ScheduledTask>> callback = consumerCaptor();
        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            factory.create(() -> {
                started.countDown();
                await(release);
            }).runTaskAsynchronously();
            verify(asyncScheduler).runNow(eq(plugin), callback.capture());

            Future<?> task = executor.submit(() -> callback.getValue().accept(scheduledTask));
            assertTrue(started.await(1L, TimeUnit.SECONDS));
            Future<?> cancellation = executor.submit(factory::cancelAllKnownTasks);
            verify(asyncScheduler, org.mockito.Mockito.timeout(1_000L)).cancelTasks(plugin);

            assertThrows(TimeoutException.class, () -> cancellation.get(100L, TimeUnit.MILLISECONDS));
            release.countDown();
            task.get(1L, TimeUnit.SECONDS);
            cancellation.get(1L, TimeUnit.SECONDS);
        } finally {
            release.countDown();
            executor.shutdownNow();
        }
    }

    @Test
    void cancellationFromRunningTaskDoesNotDeadlock() throws Exception {
        when(asyncScheduler.runNow(eq(plugin), any())).thenReturn(scheduledTask);
        ArgumentCaptor<Consumer<ScheduledTask>> callback = consumerCaptor();
        ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            factory.create(factory::cancelAllKnownTasks).runTaskAsynchronously();
            verify(asyncScheduler).runNow(eq(plugin), callback.capture());

            Future<?> task = executor.submit(() -> callback.getValue().accept(scheduledTask));
            task.get(1L, TimeUnit.SECONDS);
            verify(asyncScheduler).cancelTasks(plugin);
            verify(globalRegionScheduler).cancelTasks(plugin);
        } finally {
            executor.shutdownNow();
        }
    }

    @SuppressWarnings("unchecked")
    private static ArgumentCaptor<Consumer<ScheduledTask>> consumerCaptor() {
        return ArgumentCaptor.forClass(Consumer.class);
    }

    private static void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
