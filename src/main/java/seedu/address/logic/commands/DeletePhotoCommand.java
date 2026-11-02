package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.PhotoPath;

/**
 * Deletes the photo of a person identified using its displayed index from the address book.
 */
public class DeletePhotoCommand extends Command {

    public static final String COMMAND_WORD = "delete-photo";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the photo of the student identified by the index number used in the displayed student list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 3";

    public static final String MESSAGE_DELETE_PHOTO_SUCCESS = "Deleted photo from student: %1$s";
    public static final String MESSAGE_NO_PHOTO = "Student has no photo to delete: %1$s";

    private final Index targetIndex;

    /**
     * Creates a DeletePhotoCommand to delete a student's photo.
     */
    public DeletePhotoCommand(Index targetIndex) {
        requireNonNull(targetIndex);
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToUpdate = lastShownList.get(targetIndex.getZeroBased());
        if (!personToUpdate.getPhotoPath().isPresent()) {
            throw new CommandException(String.format(MESSAGE_NO_PHOTO, Messages.format(personToUpdate)));
        }
        Person updatedPerson = new Person(personToUpdate.getName(), personToUpdate.getPhone(),
                personToUpdate.getEmail(), personToUpdate.getAddress(), personToUpdate.getSubject(),
            personToUpdate.getGradeLevel(), PhotoPath.NONE, personToUpdate.getTaskList(), personToUpdate.getTags());

        model.setPerson(personToUpdate, updatedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_DELETE_PHOTO_SUCCESS, Messages.format(updatedPerson)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof DeletePhotoCommand otherDeletePhotoCommand)) {
            return false;
        }

        return targetIndex.equals(otherDeletePhotoCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
