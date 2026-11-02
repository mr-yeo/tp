package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.CommandSummary;

/**
 * Panel containing the list of commands, shown by the {@code help} command.
 */
public class CommandListPanel extends UiPart<Region> {
    private static final String FXML = "CommandListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(CommandListPanel.class);

    @FXML
    private ListView<CommandSummary> commandListView;

    /**
     * Creates a {@code CommandListPanel} with the given list of {@code CommandSummary}.
     */
    public CommandListPanel(List<CommandSummary> commandSummaries) {
        super(FXML);
        requireNonNull(commandSummaries);
        assert !commandSummaries.isEmpty() : "Command list should not be empty";

        // Unmodifiable so the list shown to the user cannot be changed through the ListView
        commandListView.setItems(FXCollections.unmodifiableObservableList(
                FXCollections.observableArrayList(commandSummaries)));
        commandListView.setCellFactory(listView -> new CommandListViewCell());
        ListViewUtil.enableCompleteLastItemScroll(commandListView);
        logger.fine("Created command list with " + commandSummaries.size() + " commands.");
    }

    /**
     * Custom {@code ListCell} that displays a {@code CommandSummary} using a {@code CommandCard}.
     */
    class CommandListViewCell extends ListCell<CommandSummary> {
        @Override
        protected void updateItem(CommandSummary commandSummary, boolean empty) {
            super.updateItem(commandSummary, empty);

            if (empty || commandSummary == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new CommandCard(commandSummary).getRoot());
            }
        }
    }

}
