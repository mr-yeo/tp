package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.scene.Scene;

/**
 * Helpers for tests that need the JavaFX toolkit.
 */
final class FxTestSupport {

    private FxTestSupport() {}

    /** Starts the JavaFX toolkit if it is not already running. */
    static void initialiseJavaFx() throws Exception {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException e) {
            // JavaFX toolkit has already been initialized.
        }

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(latch::countDown);
        assertTrue(latch.await(10, TimeUnit.SECONDS), "JavaFX toolkit did not initialise in time");
    }

    /** Waits off the JavaFX thread for layout and the deferred work from the first layout to finish. */
    static void waitForLayout(Scene scene) throws Exception {
        assertTrue(!Platform.isFxApplicationThread(), "Layout must be awaited outside the JavaFX thread");
        CountDownLatch pulses = new CountDownLatch(2);
        Runnable listener = () -> {
            pulses.countDown();
            if (pulses.getCount() > 0) {
                Platform.requestNextPulse();
            }
        };
        try {
            runOnFxThread(() -> {
                scene.addPostLayoutPulseListener(listener);
                scene.getRoot().requestLayout();
                Platform.requestNextPulse();
            });
            assertTrue(pulses.await(10, TimeUnit.SECONDS), "JavaFX scene did not finish layout in time");
        } finally {
            // This also lets work queued by the last layout run before assertions are scheduled.
            runOnFxThread(() -> scene.removePostLayoutPulseListener(listener));
        }
    }

    /** Runs {@code operation} on the JavaFX thread and returns its result. */
    static <T> T runOnFxThread(Callable<T> operation) throws Exception {
        FutureTask<T> task = new FutureTask<>(operation);
        Platform.runLater(task);
        return task.get(10, TimeUnit.SECONDS);
    }

    /** Runs {@code operation} on the JavaFX thread. */
    static void runOnFxThread(Runnable operation) throws Exception {
        runOnFxThread(() -> {
            operation.run();
            return null;
        });
    }
}
