package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import seedu.address.model.person.Person;
import seedu.address.model.schedule.ScheduledSession;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.ScheduledSessionBuilder;

public class ScheduleSessionCardTest {

    @BeforeAll
    static void initialiseJavaFx() throws Exception {
        FxTestSupport.initialiseJavaFx();
    }

    @Test
    public void constructor_showsTitleDetailsAndTimeInOrder() throws Exception {
        ScheduledSession session = new ScheduledSessionBuilder().withStudent(ALICE)
                .withTimeSlot("10-00", "12-30").build();

        ScheduleSessionCard card = FxTestSupport.runOnFxThread(() ->
                new ScheduleSessionCard(session, "#f6a05a", "1. Alice Pauline", 3));

        assertEquals("Student: 1. Alice Pauline", labelAt(card, 0).getText());
        assertEquals(ALICE.getSubject().value + " | " + ALICE.getGradeLevel().value, labelAt(card, 1).getText());
        assertEquals(ALICE.getAddress().value, labelAt(card, 2).getText());
        assertEquals("10:00 - 12:30", labelAt(card, 3).getText());
        assertTrue(card.getStyle().contains("#f6a05a"));
    }

    private static Label labelAt(ScheduleSessionCard card, int index) {
        return (Label) card.getChildren().get(index);
    }

    @Test
    public void constructor_everyDetailWrapsWhenTooLong() throws Exception {
        ScheduleSessionCard card = FxTestSupport.runOnFxThread(() ->
                new ScheduleSessionCard(new ScheduledSessionBuilder().build(), "#f6a05a", "Amy Bee", 1));

        card.getChildren().stream()
                .filter(node -> node instanceof Label)
                .forEach(node -> assertTrue(((Label) node).isWrapText()));
    }

    @Test
    public void prefHeight_longAddress_growsInsteadOfCuttingItOff() throws Exception {
        Person longAddressStudent = new PersonBuilder(ALICE)
                .withAddress("Blk 123 Tampines Street 45, #12-345, Next to the Tampines Hub Community Centre").build();

        double shortHeight = cardHeightAtWidth(ALICE, 120);
        double longHeight = cardHeightAtWidth(longAddressStudent, 120);

        assertTrue(longHeight > shortHeight);
    }

    private static double cardHeightAtWidth(Person student, double width) throws Exception {
        return FxTestSupport.runOnFxThread(() -> {
            ScheduledSession session = new ScheduledSessionBuilder().withStudent(student).build();
            ScheduleSessionCard card = new ScheduleSessionCard(session, "#f6a05a", "1. Alice Pauline", 1);
            new Scene(new StackPane(card));
            card.applyCss();
            return card.prefHeight(width);
        });
    }

    @Test
    public void sessionNumber_fitsNarrowCardWithoutExposingIdentity() throws Exception {
        FxTestSupport.runOnFxThread(() -> {
            ScheduledSession session = new ScheduledSessionBuilder().build();
            ScheduleSessionCard card = new ScheduleSessionCard(session, "#f6a05a", "Amy Bee", 2);
            StackPane container = new StackPane(card);
            new Scene(container, 60, 90);
            container.resize(60, 90);
            container.applyCss();
            container.layout();
            Label number = (Label) card.lookup("#sessionNumber");
            assertEquals("Session #2", number.getText());
            assertTrue(number.getWidth() <= card.getWidth());
            assertTrue(number.getLayoutY() + number.getHeight() <= card.getHeight());
        });
    }
}
