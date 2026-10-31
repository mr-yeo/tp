package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.GradeLevel;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Subject;
import seedu.address.model.schedule.TimeSlot;
import seedu.address.model.tag.Tag;
import seedu.address.model.task.Task;
import seedu.address.model.task.TaskStatus;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index must be a positive integer.";
    public static final String MESSAGE_INVALID_DAY =
            "Day must be a day of the week, e.g. monday or mon (case-insensitive).";

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws ParseException if the specified index is invalid (not a non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (!Phone.isValidPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code String address} into an {@code Address}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code address} is invalid.
     */
    public static Address parseAddress(String address) throws ParseException {
        requireNonNull(address);
        String trimmedAddress = address.trim();
        if (!Address.isValidAddress(trimmedAddress)) {
            throw new ParseException(Address.MESSAGE_CONSTRAINTS);
        }
        return new Address(trimmedAddress);
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        return getEmail(email);
    }

    private static Email getEmail(String email) throws ParseException {
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String email} in when it can be optional. Uses {@code parseEmail} if not empty.
     */
    public static Email parseOptionalEmail(String email) throws ParseException {
        if (email.isEmpty()) {
            return Email.getNone();
        }
        return getEmail(email);
    }

    /**
     * Parses a {@code String subject} into a {@code Subject}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code subject} is invalid.
     */
    public static Subject parseSubject(String subject) throws ParseException {
        requireNonNull(subject);
        String trimmedSubject = subject.trim();
        if (!Subject.isValidSubject(trimmedSubject)) {
            throw new ParseException(Subject.MESSAGE_CONSTRAINTS);
        }
        return new Subject(trimmedSubject);
    }

    /**
     * Parses a {@code String gradeLevel} into a {@code GradeLevel}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code gradeLevel} is invalid.
     */
    public static GradeLevel parseGradeLevel(String gradeLevel) throws ParseException {
        requireNonNull(gradeLevel);
        String trimmedGradeLevel = gradeLevel.trim();
        if (!GradeLevel.isValidGradeLevel(trimmedGradeLevel)) {
            throw new ParseException(GradeLevel.MESSAGE_CONSTRAINTS);
        }
        return new GradeLevel(trimmedGradeLevel);
    }

    /**
     * Parses a {@code String gradeLevel} into a {@code GradeLevel}.
     * Leading and trailing whitespaces will be trimmed.
     * Allows empty strings to remove the {@code GradeLevel}.
     *
     * @throws ParseException if the given {@code gradeLevel} is invalid.
     */
    public static GradeLevel parseOptionalGradeLevel(String gradeLevel) throws ParseException {
        if (gradeLevel.isEmpty()) {
            return GradeLevel.NONE;
        }
        String trimmedGradeLevel = gradeLevel.trim();
        if (!GradeLevel.isValidGradeLevel(trimmedGradeLevel)) {
            throw new ParseException(GradeLevel.MESSAGE_CONSTRAINTS);
        }
        return new GradeLevel(trimmedGradeLevel);
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses a {@code String description} into a {@code Task}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given description is invalid.
     */
    public static Task parseTask(String description) throws ParseException {
        requireNonNull(description);
        String trimmedDescription = description.trim();
        if (!Task.isValidDescription(trimmedDescription)) {
            throw new ParseException(Task.MESSAGE_CONSTRAINTS);
        }
        if (!Task.isWithinLengthLimit(trimmedDescription)) {
            throw new ParseException(Task.MESSAGE_LENGTH_CONSTRAINTS);
        }
        return new Task(trimmedDescription);
    }

    /** Parses trimmed, non-blank reminder text. */
    public static String parseReminderText(String text) throws ParseException {
        requireNonNull(text);
        String trimmedText = text.trim();
        if (!seedu.address.model.reminder.Reminder.isValidText(trimmedText)) {
            throw new ParseException(seedu.address.model.reminder.Reminder.MESSAGE_CONSTRAINTS);
        }
        return trimmedText;
    }

    /** Parses a strict local reminder timestamp in {@code YYYY-MM-DD HH:mm} format. */
    public static LocalDateTime parseReminderTime(String value) throws ParseException {
        requireNonNull(value);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm")
                .withResolverStyle(ResolverStyle.STRICT);
        try {
            return LocalDateTime.parse(value.trim(), formatter);
        } catch (DateTimeParseException e) {
            throw new ParseException("Due time must use a valid YYYY-MM-DD HH:mm value.", e);
        }
    }

    /**
     * Parses a {@code String status} into a {@code TaskStatus}, ignoring case and surrounding whitespace.
     *
     * @throws ParseException if the given status is neither "done" nor "not done".
     */
    public static TaskStatus parseTaskStatus(String status) throws ParseException {
        requireNonNull(status);
        if (!TaskStatus.isValidStatus(status)) {
            throw new ParseException(TaskStatus.MESSAGE_CONSTRAINTS);
        }
        return TaskStatus.fromString(status);
    }

    /**
     * Parses a {@code String day} into a {@code DayOfWeek}, ignoring case and surrounding whitespace.
     * Accepts full names (e.g. "monday") or 3-letter abbreviations (e.g. "mon").
     *
     * @throws ParseException if the given day is not recognised.
     */
    public static DayOfWeek parseDay(String day) throws ParseException {
        requireNonNull(day);
        String trimmedDay = day.trim().toLowerCase(Locale.ROOT);
        for (DayOfWeek candidate : DayOfWeek.values()) {
            String fullName = candidate.name().toLowerCase(Locale.ROOT);
            if (trimmedDay.equals(fullName) || trimmedDay.equals(fullName.substring(0, 3))) {
                return candidate;
            }
        }
        throw new ParseException(MESSAGE_INVALID_DAY);
    }

    /**
     * Parses {@code from} and {@code to} (HH-mm) into a {@code TimeSlot}.
     *
     * @throws ParseException if either time is malformed or {@code from} is not before {@code to}.
     */
    public static TimeSlot parseTimeSlot(String from, String to) throws ParseException {
        requireNonNull(from);
        requireNonNull(to);
        String trimmedFrom = from.trim();
        String trimmedTo = to.trim();
        if (!TimeSlot.isValidTimeSlot(trimmedFrom, trimmedTo)) {
            throw new ParseException(TimeSlot.MESSAGE_CONSTRAINTS);
        }
        return TimeSlot.of(trimmedFrom, trimmedTo);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }
}
