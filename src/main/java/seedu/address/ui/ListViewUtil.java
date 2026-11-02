package seedu.address.ui;

import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/** Keyboard-scrolling helpers shared by variable-height lists. */
final class ListViewUtil {
    private ListViewUtil() {}

    /** Ensures keyboard navigation reveals the whole final item, not merely its top edge. */
    static void enableCompleteLastItemScroll(ListView<?> listView) {
        listView.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() != KeyCode.DOWN || event.isAltDown() || event.isControlDown()
                    || event.isMetaDown() || event.isShiftDown()) {
                return;
            }
            Platform.runLater(() -> {
                if (listView.getSelectionModel().getSelectedIndex() == listView.getItems().size() - 1) {
                    listView.lookupAll(".scroll-bar").stream()
                            .filter(node -> node instanceof ScrollBar bar
                                    && bar.getOrientation() == Orientation.VERTICAL)
                            .map(node -> (ScrollBar) node)
                            .forEach(bar -> bar.setValue(bar.getMax()));
                }
            });
        });
    }
}
