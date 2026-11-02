package seedu.address.ui;

import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.Logic;
import seedu.address.model.person.ParentContact;

/** Displays parent contacts in the Contacts tab. */
public class ParentListPanel extends UiPart<Region> {
    private static final String FXML = "PersonListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(PersonListPanel.class);
    private final Logic logic;
    private String keyword;

    @FXML
    private ListView<ParentContact> personListView;

    /**
     * Creates a panel displaying parent contacts.
     */
    public ParentListPanel(ObservableList<ParentContact> contacts, Logic logic) {
        super(FXML);
        this.logic = logic;

        personListView.setItems(contacts);
        personListView.setCellFactory(view -> new ParentListViewCell());
        ListViewUtil.enableCompleteLastItemScroll(personListView);
    }

    /**
     * Sets the keyword a {@code ParentContactCard} would have
     * and refreshes the parent contact list.
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
        logger.fine("ParentListPanel loading with keyword:" + keyword);
        personListView.refresh();
    }

    /**
     * Custom {@code ListCell} that displays a ParentContact using a ParentContactCard.
     */
    class ParentListViewCell extends ListCell<ParentContact> {
        @Override
        protected void updateItem(ParentContact contact, boolean empty) {
            super.updateItem(contact, empty);

            if (empty || contact == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new ParentContactCard(
                        contact,
                        getIndex() + 1,
                        logic,
                        keyword
                ).getRoot());

                setText(null);
            }
        }
    }
}
