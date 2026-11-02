package seedu.address.ui;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.logging.Logger;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.person.Person;
import seedu.address.model.schedule.ScheduledSession;

/**
 * A weekly timetable with one row per day and time running left to right. Each session is a card that starts
 * and ends at its exact times, except that a session shorter than 30 minutes gets a 30-minute card so its
 * details stay readable. Each card is only as tall as its details need, and sits just below any earlier card
 * it overlaps on the same day. A day's row grows when its cards need more room.
 * The viewport shows five days and {@code VISIBLE_HOURS} hours; the rest is reached by scrolling.
 * The day labels and time header stay pinned to the viewport edges.
 */
class ScheduleGrid extends Pane {

    private static final int HEADER_HEIGHT = 30;
    private static final int AXIS_WIDTH = 60;
    private static final int MIN_HOUR_WIDTH = 120;
    private static final int MIN_ROW_HEIGHT = 90;
    private static final int MINUTES_IN_HOUR = 60;
    private static final int MINUTES_IN_DAY = 24 * MINUTES_IN_HOUR;
    // Shorter sessions get a card this long, as narrower cards are too small to show their details.
    private static final int MIN_CARD_MINUTES = 30;
    // Keeps content slightly smaller than the viewport so scrollbars do not flicker on and off.
    private static final int FIT_MARGIN = 2;
    private static final int DAYS = 7;
    private static final int VISIBLE_DAYS = 5;
    private static final int VISIBLE_START_HOUR = 10;
    private static final int VISIBLE_END_HOUR = 18;
    private static final String BACKGROUND_COLOUR = "#2b2b2b";
    private static final String LINE_COLOUR = "#555555";

    private final Logger logger = LogsCenter.getLogger(ScheduleGrid.class);
    private final List<Placement> placements = new ArrayList<>();
    private final List<Label> dayLabels = new ArrayList<>();
    private final List<Label> hourLabels = new ArrayList<>();
    private final List<Line> hourLines = new ArrayList<>();
    private final List<Line> dayLines = new ArrayList<>();
    private final Region corner = new Region();
    private double viewportWidth;
    private double viewportHeight;
    private double offsetX;
    private double offsetY;
    private int rangeStartHour = VISIBLE_START_HOUR;
    private int rangeEndHour = VISIBLE_END_HOUR;

    ScheduleGrid() {
        setStyle("-fx-background-color: " + BACKGROUND_COLOUR + ";");
        corner.setStyle("-fx-background-color: " + BACKGROUND_COLOUR + ";");
    }

    /** Sets the visible size, which determines how large the columns and rows are. */
    void setViewport(double width, double height) {
        viewportWidth = width;
        viewportHeight = height;
        requestLayout();
    }

    /** Sets how far the viewport is scrolled, so pinned labels can follow it. */
    void setScrollOffsets(double x, double y) {
        offsetX = x;
        offsetY = y;
        requestLayout();
    }

    /** Returns the width of one hour on the timeline. */
    double hourWidth() {
        double visibleHours = VISIBLE_END_HOUR - VISIBLE_START_HOUR;
        return Math.max(MIN_HOUR_WIDTH, Math.floor((viewportWidth - AXIS_WIDTH) / visibleHours) - FIT_MARGIN);
    }

    /** Returns the standard height of a day row. A day's row grows taller if its cards need more room. */
    double rowHeight() {
        return Math.max(MIN_ROW_HEIGHT, Math.floor((viewportHeight - HEADER_HEIGHT) / VISIBLE_DAYS) - FIT_MARGIN);
    }

    /**
     * Rebuilds the timetable from {@code sessions}, colouring each card using {@code colourOf}
     * and heading it with {@code titleOf}.
     */
    void setSessions(List<ScheduledSession> sessions, Function<Person, String> colourOf,
            Function<Person, String> titleOf) {
        getChildren().clear();
        placements.clear();
        dayLabels.clear();
        hourLabels.clear();
        hourLines.clear();
        dayLines.clear();
        Map<String, Integer> sessionNumbers = new HashMap<>();
        for (int i = 0; i < sessions.size(); i++) {
            sessionNumbers.put(sessions.get(i).getSessionId(), i + 1);
        }

        int minStart = VISIBLE_START_HOUR * MINUTES_IN_HOUR;
        int maxEnd = VISIBLE_END_HOUR * MINUTES_IN_HOUR;
        for (ScheduledSession session : sessions) {
            minStart = Math.min(minStart, startMinutes(session));
            maxEnd = Math.max(maxEnd, cardEndMinutes(session));
        }
        rangeStartHour = minStart / MINUTES_IN_HOUR;
        rangeEndHour = (maxEnd + MINUTES_IN_HOUR - 1) / MINUTES_IN_HOUR;

        for (int hour = rangeStartHour; hour <= rangeEndHour; hour++) {
            Label label = new Label(String.format("%02d00", hour));
            label.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 10px; -fx-background-color: "
                    + BACKGROUND_COLOUR + ";");
            label.setAlignment(Pos.CENTER_LEFT);
            hourLabels.add(label);
            Line line = new Line();
            line.setStyle("-fx-stroke: " + LINE_COLOUR + ";");
            hourLines.add(line);
        }
        for (DayOfWeek day : DayOfWeek.values()) {
            Label label = new Label(day.getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toUpperCase(Locale.ENGLISH));
            label.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-background-color: "
                    + BACKGROUND_COLOUR + ";");
            label.setAlignment(Pos.CENTER);
            dayLabels.add(label);
            Line line = new Line();
            line.setStyle("-fx-stroke: " + LINE_COLOUR + ";");
            dayLines.add(line);
        }
        getChildren().addAll(hourLines);
        getChildren().addAll(dayLines);

        for (DayOfWeek day : DayOfWeek.values()) {
            List<ScheduledSession> daySessions = sessions.stream()
                    .filter(s -> s.getDay() == day)
                    .sorted(Comparator.comparingInt(ScheduleGrid::startMinutes)
                            .thenComparingInt(ScheduleGrid::endMinutes))
                    .toList();
            placeDay(day.getValue() - 1, daySessions, colourOf, titleOf,
                    session -> sessionNumbers.get(session.getSessionId()));
        }

        getChildren().addAll(hourLabels);
        getChildren().addAll(dayLabels);
        getChildren().add(corner);
        requestLayout();
    }

    private static int startMinutes(ScheduledSession session) {
        LocalTime start = session.getTimeSlot().start;
        return start.getHour() * MINUTES_IN_HOUR + start.getMinute();
    }

    private static int endMinutes(ScheduledSession session) {
        LocalTime end = session.getTimeSlot().end;
        return end.getHour() * MINUTES_IN_HOUR + end.getMinute();
    }

    /**
     * Returns where the card of {@code session} ends, in minutes since midnight. A session shorter than
     * {@code MIN_CARD_MINUTES} gets a longer card so its details fit, but no card runs past midnight.
     */
    private static int cardEndMinutes(ScheduledSession session) {
        int start = startMinutes(session);
        int cardEnd = Math.min(MINUTES_IN_DAY, Math.max(endMinutes(session), start + MIN_CARD_MINUTES));
        assert cardEnd > start : "A card must end after it starts";
        return cardEnd;
    }

    /** Creates a card for each session in a day. The sessions must be sorted by start time. */
    private void placeDay(int dayIndex, List<ScheduledSession> daySessions, Function<Person, String> colourOf,
            Function<Person, String> titleOf, ToIntFunction<ScheduledSession> numberOf) {
        for (ScheduledSession session : daySessions) {
            int start = startMinutes(session);
            int end = cardEndMinutes(session);
            if (end > endMinutes(session)) {
                logger.fine("Lengthened the card of short session " + session.getSessionId() + " to fit its details");
            }

            Person student = session.getStudent();
            ScheduleSessionCard card = new ScheduleSessionCard(session, colourOf.apply(student),
                    titleOf.apply(student), numberOf.applyAsInt(session));
            placements.add(new Placement(card, dayIndex, start, end));
            getChildren().add(card);
        }
    }

    private double xOf(int minutes) {
        return AXIS_WIDTH + (minutes / 60.0 - rangeStartHour) * hourWidth();
    }

    private double cardWidth(Placement placement) {
        double width = xOf(placement.end) - xOf(placement.start);
        assert width > 0 : "A card must have a positive width";
        return width;
    }

    /** Sizes each card to fit its details, stacks it below cards it overlaps and returns the row heights. */
    private double[] stackCards() {
        double[] dayHeights = new double[DAYS];
        Arrays.fill(dayHeights, rowHeight());
        for (int i = 0; i < placements.size(); i++) {
            Placement placement = placements.get(i);
            List<Placement> overlapping = placements.subList(0, i).stream()
                    .filter(placement::isOverlappingInTime).toList();
            placement.height = Math.ceil(placement.card.prefHeight(cardWidth(placement)));
            placement.top = highestFreeTop(placement.height, overlapping);
            assert overlapping.stream().noneMatch(other -> other.isCovering(placement.top, placement.bottom()))
                    : "A card must not cover a card it overlaps in time";
            dayHeights[placement.dayIndex] = Math.max(dayHeights[placement.dayIndex], placement.bottom());
        }
        return dayHeights;
    }

    /** Returns the highest top at which a card {@code height} tall covers none of the {@code overlapping} cards. */
    private static double highestFreeTop(double height, List<Placement> overlapping) {
        double top = 0;
        for (Placement other : overlapping.stream().sorted(Comparator.comparingDouble(card -> card.top)).toList()) {
            if (other.isCovering(top, top + height)) {
                top = other.bottom();
            }
        }
        return top;
    }

    private double contentHeight() {
        return HEADER_HEIGHT + Arrays.stream(stackCards()).sum();
    }

    @Override
    protected double computePrefWidth(double height) {
        return AXIS_WIDTH + (rangeEndHour - rangeStartHour) * hourWidth();
    }

    @Override
    protected double computePrefHeight(double width) {
        return contentHeight();
    }

    @Override
    protected double computeMinHeight(double width) {
        return contentHeight();
    }

    @Override
    protected void layoutChildren() {
        double[] dayHeights = stackCards();
        double hourWidth = hourWidth();
        double right = AXIS_WIDTH + (rangeEndHour - rangeStartHour) * hourWidth;
        double bottom = HEADER_HEIGHT + Arrays.stream(dayHeights).sum();

        for (int i = 0; i < hourLines.size(); i++) {
            double x = AXIS_WIDTH + i * hourWidth;
            Line line = hourLines.get(i);
            line.setStartX(x);
            line.setEndX(x);
            line.setStartY(0);
            line.setEndY(bottom);
            hourLabels.get(i).resizeRelocate(x + 3, offsetY, hourWidth - 3, HEADER_HEIGHT);
        }

        double[] rowTops = new double[DAYS];
        double y = HEADER_HEIGHT;
        for (int i = 0; i < DAYS; i++) {
            rowTops[i] = y;
            double height = dayHeights[i];
            dayLabels.get(i).resizeRelocate(offsetX, y, AXIS_WIDTH, height);
            Line line = dayLines.get(i);
            line.setStartX(0);
            line.setEndX(right);
            line.setStartY(y + height);
            line.setEndY(y + height);
            y += height;
        }

        for (Placement placement : placements) {
            double left = xOf(placement.start);
            double width = cardWidth(placement);
            double top = rowTops[placement.dayIndex] + placement.top;
            placement.card.resizeRelocate(left, top, width, placement.height);
            placement.card.setClip(new Rectangle(width, placement.height));
        }
        corner.resizeRelocate(offsetX, offsetY, AXIS_WIDTH, HEADER_HEIGHT);
    }

    /** A card with its start and end in minutes since midnight, and its top and height in its day's row. */
    private static class Placement {
        private final Region card;
        private final int dayIndex;
        private final int start;
        private final int end;
        private double top;
        private double height;

        Placement(Region card, int dayIndex, int start, int end) {
            this.card = card;
            this.dayIndex = dayIndex;
            this.start = start;
            this.end = end;
        }

        double bottom() {
            return top + height;
        }

        /** Returns true if both cards are on the same day and their times overlap. Touching times do not overlap. */
        boolean isOverlappingInTime(Placement other) {
            return dayIndex == other.dayIndex && start < other.end && other.start < end;
        }

        /** Returns true if this card covers any part of its row between the heights {@code from} and {@code to}. */
        boolean isCovering(double from, double to) {
            return top < to && from < bottom();
        }
    }
}
