package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.PhotoPath;
import seedu.address.model.task.TaskStatus;
import seedu.address.testutil.PersonBuilder;

public class DeletePhotoCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_studentWithPhotoUnfilteredList_success() {
        Person personWithPhoto = addPhotoToFirstPerson();
        DeletePhotoCommand deletePhotoCommand = new DeletePhotoCommand(INDEX_FIRST_PERSON);

        Person updatedPerson = createPersonWithoutPhoto(personWithPhoto);
        String expectedMessage = String.format(DeletePhotoCommand.MESSAGE_DELETE_PHOTO_SUCCESS,
                Messages.format(updatedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personWithPhoto, updatedPerson);

        assertCommandSuccess(deletePhotoCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_personWithTasks_preservesTasks() throws CommandException {
        Person originalPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person personWithTasks = new PersonBuilder(originalPerson).withPhotoPath("photos/amy.png")
                .withTasks("do homework")
                .withTask("revise notes", TaskStatus.DONE).build();
        model.setPerson(originalPerson, personWithTasks);

        new DeletePhotoCommand(INDEX_FIRST_PERSON).execute(model);

        assertEquals(personWithTasks.getTaskList(),
                model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased()).getTaskList());
    }

    @Test
    public void execute_studentWithoutPhoto_failure() {
        Person personWithoutPhoto = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeletePhotoCommand deletePhotoCommand = new DeletePhotoCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeletePhotoCommand.MESSAGE_NO_PHOTO,
                Messages.format(personWithoutPhoto));

        assertCommandFailure(deletePhotoCommand, model, expectedMessage);
    }

    @Test
    public void execute_studentWithPhotoFilteredList_success() {
        Person personWithPhoto = addPhotoToFirstPerson();
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        DeletePhotoCommand deletePhotoCommand = new DeletePhotoCommand(INDEX_FIRST_PERSON);

        Person updatedPerson = createPersonWithoutPhoto(personWithPhoto);
        String expectedMessage = String.format(DeletePhotoCommand.MESSAGE_DELETE_PHOTO_SUCCESS,
                Messages.format(updatedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personWithPhoto, updatedPerson);

        assertCommandSuccess(deletePhotoCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidPersonIndex_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        DeletePhotoCommand deletePhotoCommand = new DeletePhotoCommand(outOfBoundIndex);

        assertCommandFailure(deletePhotoCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        DeletePhotoCommand deleteFirstPhotoCommand = new DeletePhotoCommand(INDEX_FIRST_PERSON);
        DeletePhotoCommand deleteFirstPhotoCommandCopy = new DeletePhotoCommand(INDEX_FIRST_PERSON);
        DeletePhotoCommand deleteSecondPhotoCommand = new DeletePhotoCommand(INDEX_SECOND_PERSON);

        assertTrue(deleteFirstPhotoCommand.equals(deleteFirstPhotoCommand));
        assertTrue(deleteFirstPhotoCommand.equals(deleteFirstPhotoCommandCopy));
        assertFalse(deleteFirstPhotoCommand.equals(deleteSecondPhotoCommand));
        assertFalse(deleteFirstPhotoCommand.equals(null));
        assertFalse(deleteFirstPhotoCommand.equals(1));
    }

    @Test
    public void toStringMethod() {
        DeletePhotoCommand deletePhotoCommand = new DeletePhotoCommand(INDEX_FIRST_PERSON);
        String expected = DeletePhotoCommand.class.getCanonicalName() + "{targetIndex=" + INDEX_FIRST_PERSON + "}";
        assertEquals(expected, deletePhotoCommand.toString());
    }

    private Person addPhotoToFirstPerson() {
        Person person = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person personWithPhoto = new Person(person.getName(), person.getPhone(), person.getEmail(),
                person.getAddress(), person.getSubject(), person.getGradeLevel(), new PhotoPath("photos/amy.png"),
            person.getTaskList(), person.getTags());
        model.setPerson(person, personWithPhoto);
        return personWithPhoto;
    }

    private static Person createPersonWithoutPhoto(Person person) {
        return new Person(person.getName(), person.getPhone(), person.getEmail(), person.getAddress(),
            person.getSubject(), person.getGradeLevel(), PhotoPath.NONE, person.getTaskList(), person.getTags());
    }
}
