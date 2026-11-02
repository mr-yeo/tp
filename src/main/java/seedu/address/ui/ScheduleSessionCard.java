package seedu.address.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import seedu.address.model.person.Person;
import seedu.address.model.schedule.ScheduledSession;

/**
 * A coloured card showing a scheduled student's name, subject, grade level and address,
 * with its time slot and displayed session number. {@link ScheduleGrid} makes every card at least 30 minutes long
 * so these details have room to show, and only as tall as they need. Details too long for one line wrap.
 */
class ScheduleSessionCard extends VBox {

    private static final String TEXT_STYLE = "-fx-font-size: 10px; -fx-text-fill: #1d1d1d;";

    ScheduleSessionCard(ScheduledSession session, String colour, String title, int number) {
        Person student = session.getStudent();
        setPadding(new Insets(2, 4, 2, 4));
        setSpacing(0);
        setStyle("-fx-background-color: " + colour + "; -fx-border-color: #1d1d1d; -fx-border-width: 1;");
        setMinSize(0, 0);

        Label name = createLabel("Student: " + title, TEXT_STYLE + " -fx-font-weight: bold;");
        Label subjectAndGrade = createLabel(student.getSubject().value + " | " + student.getGradeLevel().value,
                TEXT_STYLE);
        Label address = createLabel(student.getAddress().value, TEXT_STYLE);
        Label time = createLabel(session.getTimeSlot().start + " - " + session.getTimeSlot().end,
                "-fx-font-size: 9px; -fx-text-fill: #1d1d1d;");
        time.setAlignment(Pos.CENTER_RIGHT);

        getChildren().addAll(name, subjectAndGrade, address, time);

        Label sessionNumber = createLabel("Session #" + number, TEXT_STYLE + " -fx-font-weight: bold;");
        sessionNumber.setId("sessionNumber");
        getChildren().add(sessionNumber);
    }

    private static Label createLabel(String text, String style) {
        Label label = new Label(text);
        label.setStyle(style);
        label.setMinHeight(0);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setWrapText(true);
        return label;
    }
}
