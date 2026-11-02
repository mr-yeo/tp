package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.time.DayOfWeek;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;
import seedu.address.model.schedule.ScheduledSession;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.ScheduledSessionBuilder;

public class ScheduleGridTest {

    private static final double AXIS_WIDTH = 60;
    private static final double HEADER_HEIGHT = 30;

    @BeforeAll
    static void initialiseJavaFx() throws Exception {
        FxTestSupport.initialiseJavaFx();
    }

    @Test
    public void layout_cardIsPositionedBySlotAndSizedByDuration() throws Exception {
        ScheduleGrid grid = layoutGrid(List.of(session(ALICE, DayOfWeek.MONDAY, "10-00", "11-00")));
        Region card = cards(grid).get(0);

        assertEquals(AXIS_WIDTH, card.getLayoutX(), 0.001);
        assertEquals(HEADER_HEIGHT, card.getLayoutY(), 0.001);
        assertEquals(hourWidth(grid), card.getWidth(), 0.001);
        assertTrue(card.getHeight() > 0);
    }

    @Test
    public void layout_cardWidthScalesWithDuration() throws Exception {
        ScheduleGrid grid = layoutGrid(List.of(
                session(ALICE, DayOfWeek.MONDAY, "10-00", "11-00"),
                session(ALICE, DayOfWeek.TUESDAY, "10-00", "12-00")));
        List<Region> cards = cards(grid);

        assertEquals(2, cards.get(1).getWidth() / cards.get(0).getWidth(), 0.001);
    }

    @Test
    public void layout_cardsUseExactTimes() throws Exception {
        ScheduleGrid grid = layoutGrid(List.of(
                session(ALICE, DayOfWeek.MONDAY, "10-00", "11-00"),
                session(ALICE, DayOfWeek.TUESDAY, "10-20", "11-40"),
                session(ALICE, DayOfWeek.WEDNESDAY, "12-00", "13-10"),
                session(ALICE, DayOfWeek.THURSDAY, "12-00", "13-20")));
        List<Region> cards = cards(grid);
        double hourWidth = hourWidth(grid);

        // 10:20 starts a third of an hour after 10:00
        assertEquals(AXIS_WIDTH + hourWidth / 3, cards.get(1).getLayoutX(), 0.001);
        assertEquals(hourWidth * 4 / 3, cards.get(1).getWidth(), 0.001);
        // 13:10 and 13:20 end at different places
        double endOfTen = cards.get(2).getLayoutX() + cards.get(2).getWidth();
        double endOfTwenty = cards.get(3).getLayoutX() + cards.get(3).getWidth();
        assertEquals(hourWidth / 6, endOfTwenty - endOfTen, 0.001);
    }

    @Test
    public void layout_shortSession_isDrawnHalfAnHourWideFromItsStart() throws Exception {
        ScheduleGrid grid = layoutGrid(List.of(session(ALICE, DayOfWeek.SUNDAY, "10-00", "10-15")));
        ScheduleSessionCard card = (ScheduleSessionCard) cards(grid).get(0);

        assertEquals(AXIS_WIDTH, card.getLayoutX(), 0.001);
        assertEquals(hourWidth(grid) / 2, card.getWidth(), 0.001);
        // the card still shows the session's real times
        assertEquals("10:00 - 10:15", ((Label) card.getChildren().get(3)).getText());
    }

    @Test
    public void layout_shortSessionsCloseTogether_stackInsteadOfCovering() throws Exception {
        ScheduleGrid grid = layoutGrid(List.of(
                session(ALICE, DayOfWeek.MONDAY, "10-00", "10-15"),
                session(BENSON, DayOfWeek.MONDAY, "10-15", "10-30")));
        List<Region> cards = cards(grid);

        assertEquals(bottomOf(cards.get(0)), cards.get(1).getLayoutY(), 0.001);
    }

    @Test
    public void layout_shortSessionAtEndOfDay_stopsAtMidnight() throws Exception {
        ScheduleGrid grid = layoutGrid(List.of(session(ALICE, DayOfWeek.MONDAY, "23-45", "23-50")));
        Region card = cards(grid).get(0);

        assertEquals(grid.prefWidth(-1), card.getLayoutX() + card.getWidth(), 0.001);
        assertEquals("2400", labels(grid).stream().filter(l -> l.getText().length() == 4)
                .reduce((first, second) -> second).orElseThrow().getText());
    }

    @Test
    public void layout_overlappingSessionsStackAndTheDayRowGrows() throws Exception {
        ScheduleGrid grid = layoutStyledGrid(List.of(
                session(ALICE, DayOfWeek.MONDAY, "10-00", "12-00"),
                session(BENSON, DayOfWeek.MONDAY, "11-00", "13-00"),
                session(ALICE, DayOfWeek.TUESDAY, "10-00", "11-00")));
        List<Region> cards = cards(grid);

        assertEquals(bottomOf(cards.get(0)), cards.get(1).getLayoutY(), 0.001);
        assertTrue(bottomOf(cards.get(1)) > HEADER_HEIGHT + grid.rowHeight());
        assertEquals(bottomOf(cards.get(1)), cards.get(2).getLayoutY(), 0.001);
    }

    @Test
    public void layout_cardsWithDifferentDetails_eachOnlyAsTallAsNeeded() throws Exception {
        ScheduleGrid grid = layoutStyledGrid(List.of(
                session(longAddressStudent(), DayOfWeek.MONDAY, "10-00", "11-00"),
                session(BENSON, DayOfWeek.MONDAY, "11-00", "12-00"),
                session(ALICE, DayOfWeek.TUESDAY, "10-00", "11-00")));
        List<Region> cards = cards(grid);

        for (Region card : cards) {
            double neededHeight = FxTestSupport.runOnFxThread(() -> card.prefHeight(card.getWidth()));
            assertEquals(Math.ceil(neededHeight), card.getHeight(), 0.001);
        }
        assertTrue(cards.get(1).getHeight() < cards.get(0).getHeight());
        assertTrue(cards.get(2).getHeight() < grid.rowHeight());
        assertEquals(HEADER_HEIGHT + cards.get(0).getHeight() + 6 * grid.rowHeight(), prefHeightOf(grid), 0.001);
    }

    @Test
    public void layout_tallCardOverlappingSomeCards_othersNotPushedDown() throws Exception {
        ScheduleGrid grid = layoutStyledGrid(List.of(
                session(longAddressStudent(), DayOfWeek.MONDAY, "10-00", "11-00"),
                session(BENSON, DayOfWeek.MONDAY, "10-00", "11-00"),
                session(ALICE, DayOfWeek.MONDAY, "14-00", "15-00"),
                session(BENSON, DayOfWeek.MONDAY, "14-00", "15-00")));
        List<Region> cards = cards(grid);

        assertEquals(bottomOf(cards.get(0)), cards.get(1).getLayoutY(), 0.001);
        assertEquals(HEADER_HEIGHT, cards.get(2).getLayoutY(), 0.001);
        assertEquals(bottomOf(cards.get(2)), cards.get(3).getLayoutY(), 0.001);
    }

    @Test
    public void layout_gapAboveOverlappedCard_filledByCardThatFits() throws Exception {
        ScheduleGrid grid = layoutStyledGrid(List.of(
                session(ALICE, DayOfWeek.MONDAY, "10-00", "11-00"),
                session(longAddressStudent(), DayOfWeek.MONDAY, "10-30", "12-00"),
                session(BENSON, DayOfWeek.MONDAY, "11-00", "12-00")));
        List<Region> cards = cards(grid);

        assertEquals(bottomOf(cards.get(0)), cards.get(1).getLayoutY(), 0.001);
        assertTrue(cards.get(2).getHeight() <= cards.get(0).getHeight());
        assertEquals(HEADER_HEIGHT, cards.get(2).getLayoutY(), 0.001);
    }

    @Test
    public void layout_longDetails_dayRowGrowsToFitTheWrappedCard() throws Exception {
        ScheduleGrid grid = layoutStyledGrid(List.of(
                session(longAddressStudent(), DayOfWeek.MONDAY, "10-00", "11-00"),
                session(BENSON, DayOfWeek.TUESDAY, "10-00", "11-00")));
        List<Region> cards = cards(grid);
        Region monday = cards.get(0);

        assertTrue(monday.getHeight() > grid.rowHeight());
        assertTrue(monday.getHeight() > cards.get(1).getHeight());
        assertEquals(bottomOf(monday), cards.get(1).getLayoutY(), 0.001);
    }

    @Test
    public void layout_touchingSessions_sitSideBySide() throws Exception {
        ScheduleGrid grid = layoutGrid(List.of(
                session(ALICE, DayOfWeek.MONDAY, "10-00", "11-00"),
                session(BENSON, DayOfWeek.MONDAY, "11-00", "12-00")));
        List<Region> cards = cards(grid);

        assertEquals(cards.get(0).getLayoutY(), cards.get(1).getLayoutY(), 0.001);
    }

    @Test
    public void layout_sessionBeforeDefaultRange_extendsTimelineEarlier() throws Exception {
        ScheduleGrid grid = layoutGrid(List.of(
                session(ALICE, DayOfWeek.MONDAY, "08-00", "09-00"),
                session(ALICE, DayOfWeek.MONDAY, "10-00", "11-00")));
        List<Region> cards = cards(grid);
        double hourWidth = cards.get(0).getWidth();

        assertEquals(AXIS_WIDTH, cards.get(0).getLayoutX(), 0.001);
        assertEquals(AXIS_WIDTH + 2 * hourWidth, cards.get(1).getLayoutX(), 0.001);
    }

    @Test
    public void prefSize_defaultRangeHasEightHoursAndSevenDays() throws Exception {
        ScheduleGrid grid = layoutGrid(List.of(session(ALICE, DayOfWeek.MONDAY, "10-00", "11-00")));

        double hourWidth = hourWidth(grid);
        assertEquals(AXIS_WIDTH + 8 * hourWidth, grid.prefWidth(-1), 0.001);
        assertEquals(HEADER_HEIGHT + 7 * grid.rowHeight(), prefHeightOf(grid), 0.001);
    }

    @Test
    public void prefSize_sessionAfterDefaultRange_widensTimeline() throws Exception {
        ScheduleGrid defaultGrid = layoutGrid(List.of(session(ALICE, DayOfWeek.MONDAY, "10-00", "11-00")));
        ScheduleGrid widerGrid = layoutGrid(List.of(session(ALICE, DayOfWeek.MONDAY, "19-00", "20-30")));

        assertTrue(widerGrid.prefWidth(-1) > defaultGrid.prefWidth(-1));
    }

    @Test
    public void layout_labelsFollowScrollOffsets() throws Exception {
        ScheduleGrid grid = layoutGrid(List.of());

        FxTestSupport.runOnFxThread(() -> {
            grid.setScrollOffsets(120, 45);
            grid.layout();
        });

        assertEquals(120, label(grid, "MON").getLayoutX(), 0.001);
        assertEquals(45, label(grid, "1000").getLayoutY(), 0.001);
        assertEquals(7, labels(grid).stream().filter(l -> l.getText().length() == 3).count());
    }

    private static ScheduledSession session(Person student, DayOfWeek day, String from, String to) {
        return new ScheduledSessionBuilder().withStudent(student).withDay(day).withTimeSlot(from, to).build();
    }

    @Test
    public void sessionNumbers_followListOrderDespiteTimetableSorting() throws Exception {
        ScheduledSession friday = session(ALICE, DayOfWeek.FRIDAY, "10-00", "11-00");
        ScheduledSession mondayLate = session(ALICE, DayOfWeek.MONDAY, "14-00", "15-00");
        ScheduledSession mondayEarly = session(BENSON, DayOfWeek.MONDAY, "10-00", "11-00");
        ScheduleGrid grid = layoutGrid(List.of(friday, mondayLate, mondayEarly));
        List<Region> cards = cards(grid);
        assertEquals("Session #3", ((Label) cards.get(0).lookup("#sessionNumber")).getText());
        assertEquals("Session #2", ((Label) cards.get(1).lookup("#sessionNumber")).getText());
        assertEquals("Session #1", ((Label) cards.get(2).lookup("#sessionNumber")).getText());
        FxTestSupport.runOnFxThread(() -> grid.setSessions(List.of(friday, mondayLate),
                student -> "#f6a05a", student -> student.getName().fullName));
        assertEquals("Session #2", ((Label) cards(grid).get(0).lookup("#sessionNumber")).getText());
    }

    private static ScheduleGrid layoutGrid(List<ScheduledSession> sessions) throws Exception {
        return FxTestSupport.runOnFxThread(() -> {
            ScheduleGrid grid = new ScheduleGrid();
            grid.setViewport(1500, 600);
            grid.setSessions(sessions, student -> "#f6a05a", student -> student.getName().fullName);
            grid.layout();
            return grid;
        });
    }

    private static ScheduleGrid layoutStyledGrid(List<ScheduledSession> sessions) throws Exception {
        return FxTestSupport.runOnFxThread(() -> {
            ScheduleGrid grid = new ScheduleGrid();
            new Scene(grid);
            grid.setViewport(1500, 600);
            grid.setSessions(sessions, student -> "#f6a05a", student -> student.getName().fullName);
            grid.applyCss();
            grid.layout();
            return grid;
        });
    }

    private static Person longAddressStudent() {
        return new PersonBuilder(ALICE).withAddress("Blk 123 Tampines Street 45, #12-345, "
                + "Next to the Tampines Hub Community Centre and the Tampines Mall, Opposite the Bus Interchange, "
                + "Above the Coffee Shop on the Second Floor").build();
    }

    private static double prefHeightOf(ScheduleGrid grid) throws Exception {
        return FxTestSupport.runOnFxThread(() -> grid.prefHeight(-1));
    }

    private static double bottomOf(Region card) {
        return card.getLayoutY() + card.getHeight();
    }

    private static List<Region> cards(ScheduleGrid grid) {
        return grid.getChildren().stream()
                .filter(node -> node instanceof ScheduleSessionCard)
                .map(node -> (Region) node)
                .toList();
    }

    private static double hourWidth(ScheduleGrid grid) {
        return (grid.prefWidth(-1) - AXIS_WIDTH) / 8;
    }

    private static List<Label> labels(ScheduleGrid grid) {
        return grid.getChildren().stream()
                .filter(node -> node instanceof Label)
                .map(node -> (Label) node)
                .toList();
    }

    private static Node label(ScheduleGrid grid, String text) {
        return labels(grid).stream().filter(l -> l.getText().equals(text)).findFirst().orElseThrow();
    }
}
