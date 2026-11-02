package seedu.address.logic.commands;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.ParentContact;
import seedu.address.model.person.PhotoPath;

/** Removes a photo from a parent contact. */
public class DeleteParentPhotoCommand extends Command {
    public static final String COMMAND_WORD = "delete-parent-photo";
    public static final String MESSAGE_NO_PHOTO = "Parent has no photo to delete: %1$s";
    private final Index index;

    public DeleteParentPhotoCommand(Index index) {
        this.index = index;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        if (index.getZeroBased() >= model.getParentContactList().size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PARENT_DISPLAYED_INDEX);
        }
        ParentContact old = model.getParentContactList().get(index.getZeroBased());
        if (!old.getPhotoPath().isPresent()) {
            throw new CommandException(String.format(MESSAGE_NO_PHOTO, old.getName()));
        }
        ParentContact updated = new ParentContact(
                old.getId(), old.getName(), old.getPhone(), old.getEmail(), old.getLinkedStudentId(),
                old.getLinkedStudentName(), old.getRequirements(), PhotoPath.NONE);
        model.setParentContact(old, updated);
        return new CommandResult("Deleted photo from parent: " + updated.getName());
    }
}
