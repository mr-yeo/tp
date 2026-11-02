package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class ResultDisplayTest {
    private static final String LONG_FEEDBACK = IntStream.rangeClosed(1, 40)
            .mapToObj(line -> "Line " + line)
            .collect(Collectors.joining("\n"));

    @BeforeAll
    static void initialiseJavaFx() throws Exception {
        FxTestSupport.initialiseJavaFx();
        FxTestSupport.runOnFxThread(() -> Platform.setImplicitExit(false));
    }

    @Test
    public void handleScrollKey_downOnce_scrollsStraightAway() throws Exception {
        checkShownDisplay(LONG_FEEDBACK, (display, text) -> {
            KeyEvent down = pressKey(display, KeyCode.DOWN);
            assertTrue(text.getScrollTop() > 0);
            assertTrue(down.isConsumed());
        });
    }

    @Test
    public void handleScrollKey_upAtStart_staysAtStart() throws Exception {
        checkShownDisplay(LONG_FEEDBACK, (display, text) -> {
            pressKey(display, KeyCode.UP);
            assertEquals(0, text.getScrollTop(), 0.001);
        });
    }

    @Test
    public void handleScrollKey_pageDown_scrollsFurtherThanDown() throws Exception {
        checkShownDisplay(LONG_FEEDBACK, (display, text) -> {
            pressKey(display, KeyCode.DOWN);
            double oneLine = text.getScrollTop();
            pressKey(display, KeyCode.HOME);
            pressKey(display, KeyCode.PAGE_DOWN);
            assertTrue(text.getScrollTop() > oneLine);
        });
    }

    @Test
    public void handleScrollKey_endThenHome_jumpToBottomAndBackToStart() throws Exception {
        checkShownDisplay(LONG_FEEDBACK, (display, text) -> {
            pressKey(display, KeyCode.END);
            double bottom = text.getScrollTop();
            assertTrue(bottom > 0);

            pressKey(display, KeyCode.DOWN);
            assertEquals(bottom, text.getScrollTop(), 0.001);

            pressKey(display, KeyCode.HOME);
            assertEquals(0, text.getScrollTop(), 0.001);
        });
    }

    @Test
    public void handleScrollKey_otherKeys_leftForTheTextBox() throws Exception {
        checkShownDisplay(LONG_FEEDBACK, (display, text) -> {
            assertFalse(pressKey(display, KeyCode.TAB).isConsumed());
            assertFalse(pressKey(display, new KeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.CHAR_UNDEFINED, "",
                    KeyCode.DOWN, true, false, false, false)).isConsumed());
            assertEquals(0, text.getScrollTop(), 0.001);
        });
    }

    /**
     * Shows a result display holding {@code feedback}, runs {@code check} on it, then closes it.
     */
    private static void checkShownDisplay(String feedback, BiConsumer<ResultDisplay, TextArea> check)
            throws Exception {
        Stage stage = FxTestSupport.runOnFxThread(() -> new Stage());
        try {
            ResultDisplay display = FxTestSupport.runOnFxThread(() -> {
                ResultDisplay result = new ResultDisplay();
                result.setFeedbackToUser(feedback);
                stage.setScene(new Scene(result.getRoot(), 400, 120));
                stage.show();
                return result;
            });
            FxTestSupport.waitForLayout(FxTestSupport.runOnFxThread(() -> stage.getScene()));
            FxTestSupport.runOnFxThread(() -> {
                TextArea text = (TextArea) display.getRoot().lookup("#resultDisplay");
                ScrollPane scroll = (ScrollPane) text.lookup(".scroll-pane");
                assertTrue(scroll.getViewportBounds().getHeight() > 0, "The text viewport must be laid out");
                assertTrue(scroll.getContent().getLayoutBounds().getHeight() > scroll.getViewportBounds().getHeight(),
                        "The feedback must overflow the viewport to test scrolling");
                check.accept(display, text);
            });
        } finally {
            FxTestSupport.runOnFxThread(() -> stage.close());
        }
    }

    private static KeyEvent pressKey(ResultDisplay display, KeyCode code) {
        return pressKey(display, new KeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.CHAR_UNDEFINED, "", code,
                false, false, false, false));
    }

    /**
     * Sends {@code event} to the display's key handling and returns it,
     * so tests can see whether it was used.
     */
    private static KeyEvent pressKey(ResultDisplay display, KeyEvent event) {
        display.handleScrollKey(event);
        display.getRoot().layout();
        return event;
    }
}
