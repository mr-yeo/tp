package seedu.address.logic;

import static java.util.Objects.requireNonNull;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.person.ParentContact;
import seedu.address.model.person.Person;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n%1$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX =
            "The student index provided is invalid.";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d student(s) listed!";
    public static final String MESSAGE_PARENTS_LISTED_OVERVIEW = "%1$d parents(s) listed!";
    public static final String MESSAGE_INVALID_PARENT_DISPLAYED_INDEX =
            "The parent index provided is invalid.";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";
    public static final String MESSAGE_UNABLE_TO_UNDO = "Nothing to undo!";
    public static final String MESSAGE_UNABLE_TO_REDO = "Nothing to redo!";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Formats the {@code person} for display to the user.
     */
    public static String format(Person person) {
        requireNonNull(person);
        final StringBuilder builder = new StringBuilder();
        builder.append(person.getName())
                .append("; Phone: ")
                .append(person.getPhone())
                .append("; Email: ")
                .append(person.getEmail())
                .append("; Address: ")
                .append(person.getAddress())
                .append("; Subject: ")
                .append(person.getSubject());
        if (person.getGradeLevel().isPresent()) {
            builder.append("; Grade Level: ")
                    .append(person.getGradeLevel());
        }
        if (person.getPhotoPath().isPresent()) {
            builder.append("; Photo: ")
                    .append(person.getPhotoPath());
        }
        if (!person.getTags().isEmpty()) {
            builder.append("; Tags: ");
            person.getTags().forEach(builder::append);
        }
        return builder.toString();
    }

    /**
     * Formats the {@code parent} for display to the user.
     */
    public static String format(ParentContact parent) {
        requireNonNull(parent);
        final StringBuilder builder = new StringBuilder();
        builder.append(parent.getName())
                .append("; Phone: ")
                .append(parent.getPhone());
        if (parent.getEmail() != null) {
            builder.append("; Email: ")
                    .append(parent.getEmail());
        }
        if (parent.getPhotoPath().isPresent()) {
            builder.append("; Photo: ")
                    .append(parent.getPhotoPath());
        }
        if (!parent.getRequirements().isEmpty()) {
            builder.append("; Requirements: ")
                    .append(String.join(", ", parent.getRequirements()));
        }
        return builder.toString();
    }

}
