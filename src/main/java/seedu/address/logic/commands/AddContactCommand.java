package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.ParentContact;
import seedu.address.model.person.Person;

/**
 * Adds a parent contact to the address book.
 */
public class AddContactCommand extends Command {
    public static final String COMMAND_WORD = "add-parent";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Adds a parent to Tutoria. Parameters: n/NAME p/PHONE [e/EMAIL] "
            + "[link/STUDENT_INDEX] [r/REQUIREMENT]...\n"
            + "Example: " + COMMAND_WORD
            + " n/Mrs Tan p/98765432 e/tanmrs@email.com link/1 r/No food during session";
    public static final String MESSAGE_SUCCESS = "New parent added: %1$s";
    public static final String MESSAGE_DUPLICATE_PARENT = "This parent already exists in Tutoria.";
    public static final String MESSAGE_STUDENT_ALREADY_LINKED =
            "This student already has two parents in Tutoria.";
    private final ParentContact contact;
    private final Index linkIndex;

    /**
     * Creates a command to add a parent contact and optionally link it to a student.
     */
    public AddContactCommand(ParentContact contact, Index linkIndex) {
        this.contact = requireNonNull(contact);
        this.linkIndex = linkIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        String studentId = null;
        String studentName = null;
        if (linkIndex != null) {
            List<Person> students = model.getFilteredPersonList();
            if (linkIndex.getZeroBased() >= students.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
            Person student = students.get(linkIndex.getZeroBased());
            studentId = student.getStudentId();
            studentName = student.getName().fullName;
        }
        ParentContact toAdd = new ParentContact(
                contact.getId(), contact.getName(), contact.getPhone(), contact.getEmail(),
                studentId, studentName, contact.getRequirements());
        if (model.hasParentContact(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_PARENT);
        }
        final String resolvedStudentId = studentId;
        if (resolvedStudentId != null && model.getParentContactList().stream()
                .filter(p -> resolvedStudentId.equals(p.getLinkedStudentId()))
                .count() >= 2) {
            throw new CommandException(MESSAGE_STUDENT_ALREADY_LINKED);
        }
        model.addParentContact(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(toAdd)));
    }
}
