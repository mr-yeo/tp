package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class HelpWindowTest {

    @BeforeAll
    static void initialiseJavaFx() throws Exception {
        FxTestSupport.initialiseJavaFx();
        FxTestSupport.runOnFxThread(() -> Platform.setImplicitExit(false));
    }

    @Test
    public void escape_whileShowing_hidesWindow() throws Exception {
        FxTestSupport.runOnFxThread(() -> {
            HelpWindow helpWindow = new HelpWindow();
            helpWindow.show();
            pressKey(copyButtonOf(helpWindow), KeyCode.ESCAPE);
            assertFalse(helpWindow.isShowing());
        });
    }

    @Test
    public void otherKey_whileShowing_keepsWindowShown() throws Exception {
        FxTestSupport.runOnFxThread(() -> {
            HelpWindow helpWindow = new HelpWindow();
            helpWindow.show();
            try {
                pressKey(copyButtonOf(helpWindow), KeyCode.TAB);
                assertTrue(helpWindow.isShowing());
            } finally {
                helpWindow.hide();
            }
        });
    }

    private static Node copyButtonOf(HelpWindow helpWindow) {
        return helpWindow.getRoot().getScene().lookup("#copyButton");
    }

    /** Presses and releases {@code code} on {@code target}. */
    private static void pressKey(Node target, KeyCode code) {
        Event.fireEvent(target, new KeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.CHAR_UNDEFINED, "", code,
                false, false, false, false));
        Event.fireEvent(target, new KeyEvent(KeyEvent.KEY_RELEASED, KeyEvent.CHAR_UNDEFINED, "", code,
                false, false, false, false));
    }
}
