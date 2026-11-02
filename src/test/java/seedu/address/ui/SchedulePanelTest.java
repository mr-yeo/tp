package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalSchedule.ALICE_MONDAY_MORNING;
import static seedu.address.testutil.TypicalSchedule.ALICE_WEDNESDAY;
import static seedu.address.testutil.TypicalSchedule.BENSON_MONDAY_OVERLAP;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import seedu.address.model.person.Person;
import seedu.address.model.schedule.ScheduledSession;

public class SchedulePanelTest {

    private static final String FIRST_COLOUR = "#f6a05a";
    private static final String SECOND_COLOUR = "#6ccfcf";

    @BeforeAll
    static void initialiseJavaFx() throws Exception {
        FxTestSupport.initialiseJavaFx();
    }

    @Test
    public void constructor_noSessions_showsEmptyTimetableAndLegend() throws Exception {
        SchedulePanel panel = createPanel(List.of(), List.of(ALICE, BENSON), List.of(ALICE, BENSON));

        assertEquals(0, cards(panel).size());
        assertEquals(0, legend(panel).getChildren().size());
    }

    @Test
    public void constructor_sessions_showsCardsWithDisplayedIndexAndColours() throws Exception {
        SchedulePanel panel = createPanel(List.of(ALICE_MONDAY_MORNING, BENSON_MONDAY_OVERLAP),
                List.of(ALICE, BENSON), List.of(ALICE, BENSON));

        List<ScheduleSessionCard> cards = cards(panel);
        assertEquals(2, cards.size());
        assertEquals("Student: 1. Alice Pauline", titleOf(cards.get(0)));
        assertEquals("Student: 2. Benson Meier", titleOf(cards.get(1)));
        assertTrue(cards.get(0).getStyle().contains(FIRST_COLOUR));
        assertTrue(cards.get(1).getStyle().contains(SECOND_COLOUR));
    }

    @Test
    public void constructor_studentWithSeveralSessions_shareColourAndHaveOneLegendEntry() throws Exception {
        SchedulePanel panel = createPanel(List.of(ALICE_MONDAY_MORNING, ALICE_WEDNESDAY),
                List.of(ALICE, BENSON), List.of(ALICE, BENSON));

        List<ScheduleSessionCard> cards = cards(panel);
        assertTrue(cards.get(0).getStyle().contains(FIRST_COLOUR));
        assertTrue(cards.get(1).getStyle().contains(FIRST_COLOUR));
        assertEquals(1, legend(panel).getChildren().size());
        assertEquals("Alice Pauline", legendName(panel, 0));
    }

    @Test
    public void constructor_legendListsOnlyScheduledStudentsInStudentOrder() throws Exception {
        SchedulePanel panel = createPanel(List.of(BENSON_MONDAY_OVERLAP, ALICE_MONDAY_MORNING),
                List.of(ALICE, BENSON), List.of(ALICE, BENSON));

        assertEquals(2, legend(panel).getChildren().size());
        assertEquals("Alice Pauline", legendName(panel, 0));
        assertEquals("Benson Meier", legendName(panel, 1));
    }

    @Test
    public void constructor_studentNotInDisplayedList_showsNameWithoutIndex() throws Exception {
        SchedulePanel panel = createPanel(List.of(ALICE_MONDAY_MORNING, BENSON_MONDAY_OVERLAP),
                List.of(ALICE, BENSON), List.of(BENSON));

        List<ScheduleSessionCard> cards = cards(panel);
        assertEquals("Student: Alice Pauline", titleOf(cards.get(0)));
        assertEquals("Student: 1. Benson Meier", titleOf(cards.get(1)));
    }

    @Test
    public void sessionsListChanges_panelRedraws() throws Exception {
        ObservableList<ScheduledSession> sessions = FXCollections.observableArrayList();
        ObservableList<Person> persons = FXCollections.observableArrayList(ALICE, BENSON);
        SchedulePanel panel = FxTestSupport.runOnFxThread(() -> new SchedulePanel(sessions, persons, persons));

        FxTestSupport.runOnFxThread(() -> sessions.add(ALICE_MONDAY_MORNING));
        assertEquals(1, cards(panel).size());
        assertEquals(1, legend(panel).getChildren().size());

        FxTestSupport.runOnFxThread(() -> sessions.remove(ALICE_MONDAY_MORNING));
        assertEquals(0, cards(panel).size());
        assertEquals(0, legend(panel).getChildren().size());
    }

    @Test
    public void displayedListChanges_cardTitlesUpdate() throws Exception {
        ObservableList<ScheduledSession> sessions = FXCollections.observableArrayList(ALICE_MONDAY_MORNING);
        ObservableList<Person> persons = FXCollections.observableArrayList(ALICE, BENSON);
        ObservableList<Person> displayed = FXCollections.observableArrayList(ALICE, BENSON);
        SchedulePanel panel = FxTestSupport.runOnFxThread(() -> new SchedulePanel(sessions, persons, displayed));
        assertEquals("Student: 1. Alice Pauline", titleOf(cards(panel).get(0)));

        FxTestSupport.runOnFxThread(() -> displayed.remove(ALICE));
        assertEquals("Student: Alice Pauline", titleOf(cards(panel).get(0)));

        FxTestSupport.runOnFxThread(() -> displayed.add(0, ALICE));
        assertEquals("Student: 1. Alice Pauline", titleOf(cards(panel).get(0)));
    }

    @Test
    public void personListChanges_legendAndColoursUpdate() throws Exception {
        ObservableList<ScheduledSession> sessions = FXCollections.observableArrayList(BENSON_MONDAY_OVERLAP);
        ObservableList<Person> persons = FXCollections.observableArrayList(BENSON);
        SchedulePanel panel = FxTestSupport.runOnFxThread(() -> new SchedulePanel(sessions, persons, persons));
        assertTrue(cards(panel).get(0).getStyle().contains(FIRST_COLOUR));

        FxTestSupport.runOnFxThread(() -> persons.add(0, ALICE));

        assertTrue(cards(panel).get(0).getStyle().contains(SECOND_COLOUR));
    }

    @Test
    public void shownInScene_sizesTimetableToViewportAndTracksScrolling() throws Exception {
        SchedulePanel panel = createPanel(List.of(ALICE_MONDAY_MORNING), List.of(ALICE), List.of(ALICE));

        ScheduleGrid grid = FxTestSupport.runOnFxThread(() -> {
            Scene scene = new Scene(panel.getRoot(), 1200, 700);
            panel.getRoot().applyCss();
            panel.getRoot().layout();
            ScrollPane scroll = scrollPane(panel);
            scroll.setHvalue(0.5);
            scroll.setVvalue(0.5);
            panel.getRoot().layout();
            return grid(panel);
        });

        assertNotNull(scene(panel));
        assertTrue(FxTestSupport.runOnFxThread(() -> grid.prefWidth(-1)) > 0);
        assertTrue(FxTestSupport.runOnFxThread(() -> grid.prefHeight(-1)) > 0);
    }

    @Test
    public void handleScrollKey_scrollKeys_scrollTimetableAndAreConsumed() throws Exception {
        SchedulePanel panel = createPanel(List.of(ALICE_MONDAY_MORNING), List.of(ALICE), List.of(ALICE));

        FxTestSupport.runOnFxThread(() -> {
            new Scene(panel.getRoot(), 600, 400);
            panel.getRoot().applyCss();
            panel.getRoot().layout();
            ScrollPane scroll = scrollPane(panel);
            assertTrue(scroll.isFocusTraversable());

            KeyEvent right = keyPressed(KeyCode.RIGHT);
            panel.handleScrollKey(right);
            assertTrue(right.isConsumed());
            assertTrue(scroll.getHvalue() > 0);

            panel.handleScrollKey(keyPressed(KeyCode.DOWN));
            assertTrue(scroll.getVvalue() > 0);

            panel.handleScrollKey(keyPressed(KeyCode.END));
            assertEquals(1, scroll.getHvalue(), 0.001);
            panel.handleScrollKey(keyPressed(KeyCode.HOME));
            assertEquals(0, scroll.getHvalue(), 0.001);

            for (int i = 0; i < 50; i++) {
                panel.handleScrollKey(keyPressed(KeyCode.PAGE_DOWN));
            }
            assertEquals(1, scroll.getVvalue(), 0.001);
            return null;
        });
    }

    @Test
    public void handleScrollKey_tab_notConsumedSoFocusCanMoveOn() throws Exception {
        SchedulePanel panel = createPanel(List.of(), List.of(ALICE), List.of(ALICE));
        KeyEvent tab = keyPressed(KeyCode.TAB);

        FxTestSupport.runOnFxThread(() -> panel.handleScrollKey(tab));

        assertFalse(tab.isConsumed());
    }

    private static KeyEvent keyPressed(KeyCode code) {
        return new KeyEvent(KeyEvent.KEY_PRESSED, "", "", code, false, false, false, false);
    }

    private static SchedulePanel createPanel(List<ScheduledSession> sessions, List<Person> persons,
            List<Person> displayed) throws Exception {
        return FxTestSupport.runOnFxThread(() -> new SchedulePanel(FXCollections.observableArrayList(sessions),
                FXCollections.observableArrayList(persons), FXCollections.observableArrayList(displayed)));
    }

    private static List<ScheduleSessionCard> cards(SchedulePanel panel) throws Exception {
        return grid(panel).getChildren().stream()
                .filter(node -> node instanceof ScheduleSessionCard)
                .map(node -> (ScheduleSessionCard) node)
                .toList();
    }

    private static String titleOf(ScheduleSessionCard card) {
        return ((Label) card.getChildren().get(0)).getText();
    }

    private static String legendName(SchedulePanel panel, int index) throws Exception {
        HBox entry = (HBox) legend(panel).getChildren().get(index);
        return ((Label) entry.getChildren().get(1)).getText();
    }

    private static Scene scene(SchedulePanel panel) {
        return panel.getRoot().getScene();
    }

    private static FlowPane legend(SchedulePanel panel) throws Exception {
        return getField(panel, "legendPane");
    }

    private static ScrollPane scrollPane(SchedulePanel panel) throws Exception {
        return getField(panel, "scheduleScroll");
    }

    private static ScheduleGrid grid(SchedulePanel panel) throws Exception {
        return getField(panel, "grid");
    }

    @SuppressWarnings("unchecked")
    private static <T> T getField(SchedulePanel panel, String fieldName) throws ReflectiveOperationException {
        Field field = SchedulePanel.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(panel);
    }
}
