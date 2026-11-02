package seedu.address.ui;

import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.person.Person;

/**
 * Panel containing the list of persons.
 */
public class PersonListPanel extends UiPart<Region> {
    private static final String FXML = "PersonListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(PersonListPanel.class);
    private String keyword;

    @FXML
    private ListView<Person> personListView;

    /**
     * Creates a {@code PersonListPanel} with the given {@code ObservableList}.
     */
    public PersonListPanel(ObservableList<Person> personList) {
        super(FXML);
        personListView.setItems(personList);
        personListView.setCellFactory(listView -> new PersonListViewCell());
        ListViewUtil.enableCompleteLastItemScroll(personListView);
    }

    /**
     * Sets the keyword a {@code PersonCard} would have and refreshes the {@code personListView}.
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
        logger.fine("PersonListPanel loading with keyword:" + keyword);
        personListView.refresh();
    }

    /** Selects the student row corresponding to the linked student id. */
    public void selectStudent(String studentId) {
        for (int i = 0; i < personListView.getItems().size(); i++) {
            Person person = personListView.getItems().get(i);
            String id = person.getStudentId();
            if (id.equals(studentId)) {
                personListView.getSelectionModel().select(i);
                personListView.scrollTo(i);
                return;
            }
        }
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Person} using a {@code PersonCard}.
     */
    class PersonListViewCell extends ListCell<Person> {
        @Override
        protected void updateItem(Person person, boolean empty) {
            super.updateItem(person, empty);

            if (empty || person == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new PersonCard(person, getIndex() + 1, keyword).getRoot());
            }
        }
    }

}
