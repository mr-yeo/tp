package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import seedu.address.logic.LogicManager;
import seedu.address.model.ModelManager;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class MainWindowShortcutTest {
    @TempDir
    public Path folder;

    @BeforeAll
    static void initialiseJavaFx() throws Exception {
        FxTestSupport.initialiseJavaFx();
        FxTestSupport.runOnFxThread(() -> Platform.setImplicitExit(false));
    }

    @Test
    public void escapeThenEnter_inCommandBox_closesWindow() throws Exception {
        checkShownWindow(window -> {
            Stage stage = window.getPrimaryStage();
            Node commandBox = stage.getScene().lookup("#commandTextField");
            pressKey(commandBox, KeyCode.ESCAPE);
            assertTrue(fileMenuOf(stage).isShowing());
            assertTrue(stage.isShowing());

            pressKey(commandBox, KeyCode.ENTER);
            assertFalse(stage.isShowing());
        });
    }

    @Test
    public void escape_whileMenuBarIsActive_leavesMenuWithoutClosingWindow() throws Exception {
        checkShownWindow(window -> {
            Stage stage = window.getPrimaryStage();
            Node commandBox = stage.getScene().lookup("#commandTextField");
            // JavaFX uses Ctrl+F10 to activate the menu bar on macOS.
            pressKey(commandBox, KeyCode.F10, System.getProperty("os.name").startsWith("Mac"));
            pressKey(commandBox, KeyCode.ESCAPE);
            assertFalse(fileMenuOf(stage).isShowing());
            assertTrue(stage.isShowing());
        });
    }

    @Test
    public void escapeAndF1_fromEveryFocusableControlOnEveryTab_openFileMenuAndHelp() throws Exception {
        checkShownWindow(window -> {
            Stage stage = window.getPrimaryStage();
            HelpWindow helpWindow = UiTestUtil.fieldOf(window, "helpWindow");
            TabPane tabs = (TabPane) stage.getScene().lookup("#recordTabs");
            for (int tab = 0; tab < tabs.getTabs().size(); tab++) {
                tabs.getSelectionModel().select(tab);
                stage.getScene().getRoot().applyCss();
                stage.getScene().getRoot().layout();
                for (Node control : focusableControlsIn(stage.getScene().getRoot())) {
                    control.requestFocus();
                    Node focused = stage.getScene().getFocusOwner();
                    String place = tabs.getTabs().get(tab).getText() + " tab, " + focused.getId();

                    pressKey(focused, KeyCode.ESCAPE);
                    assertTrue(fileMenuOf(stage).isShowing(), "Esc did not open the File menu in " + place);
                    pressKey(focused, KeyCode.ESCAPE);
                    assertFalse(fileMenuOf(stage).isShowing(), "Esc did not close the File menu in " + place);
                    assertTrue(stage.isShowing(), "Esc closed the window in " + place);

                    pressKey(focused, KeyCode.F1);
                    assertTrue(helpWindow.isShowing(), "F1 did not open the user guide in " + place);
                    helpWindow.hide();
                }
            }
        });
    }

    private static Menu fileMenuOf(Stage stage) {
        return ((MenuBar) stage.getScene().lookup("#menuBar")).getMenus().get(0);
    }

    /** Returns every visible, enabled control under {@code root} that the user can Tab to. */
    private static List<Node> focusableControlsIn(Parent root) {
        List<Node> controls = new ArrayList<>();
        for (Node child : root.getChildrenUnmodifiable()) {
            if (!child.isVisible() || child.isDisabled()) {
                continue;
            }
            if (child.isFocusTraversable()) {
                controls.add(child);
            }
            if (child instanceof Parent parent) {
                controls.addAll(focusableControlsIn(parent));
            }
        }
        return controls;
    }

    /** A check on a shown main window, which may look into the window's private parts. */
    private interface WindowCheck {
        void check(MainWindow window) throws ReflectiveOperationException;
    }

    /**
     * Shows a main window with the command box focused, runs {@code check} on it, then closes it.
     */
    private void checkShownWindow(WindowCheck check) throws Exception {
        FxTestSupport.runOnFxThread(() -> {
            StorageManager storage = new StorageManager(
                    new JsonAddressBookStorage(folder.resolve("book.json")),
                    new JsonUserPrefsStorage(folder.resolve("prefs.json")));
            Stage stage = new Stage();
            MainWindow window = new MainWindow(stage, new LogicManager(new ModelManager(), storage),
                    folder.resolve("book.json"));
            window.fillInnerParts();
            stage.show();
            stage.getScene().lookup("#commandTextField").requestFocus();
            try {
                check.check(window);
            } catch (ReflectiveOperationException e) {
                throw new AssertionError(e);
            } finally {
                stage.close();
            }
        });
    }

    /**
     * Presses and releases {@code code} on {@code target}. Keys go to the main window
     * even while a menu is open, as real key presses do, and the window passes them on to the open menu.
     */
    private static void pressKey(Node target, KeyCode code) {
        pressKey(target, code, false);
    }

    private static void pressKey(Node target, KeyCode code, boolean controlDown) {
        Event.fireEvent(target, new KeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.CHAR_UNDEFINED, "", code,
                false, controlDown, false, false));
        Event.fireEvent(target, new KeyEvent(KeyEvent.KEY_RELEASED, KeyEvent.CHAR_UNDEFINED, "", code,
                false, controlDown, false, false));
    }
}
