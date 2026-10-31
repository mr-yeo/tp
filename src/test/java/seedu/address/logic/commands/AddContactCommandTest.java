package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.ParentContact;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.testutil.PersonBuilder;

public class AddContactCommandTest {
    private static final ParentContact PARENT = new ParentContact(
            new Name("Mary Tan"), new Phone("98765432"), new Email("mary@example.com"), null, null,
            List.of("Prefers email"));

    @Test
    public void constructor_nullContact_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddContactCommand(null, null));
    }

    @Test
    public void execute_newParent_success() throws Exception {
        ModelManager model = new ModelManager();
        CommandResult result = new AddContactCommand(PARENT, null).execute(model);

        assertEquals(String.format(AddContactCommand.MESSAGE_SUCCESS, Messages.format(PARENT)),
                result.getFeedbackToUser());
        assertEquals(List.of(PARENT), model.getParentContactList());
    }

    @Test
    public void execute_duplicateParent_throwsCommandException() {
        ModelManager model = new ModelManager();
        model.addParentContact(PARENT);
        ParentContact sameIdentity = new ParentContact(new Name("mary tan"), PARENT.getPhone(),
                new Email("different@example.com"), null, null, List.of());

        assertThrows(CommandException.class, AddContactCommand.MESSAGE_DUPLICATE_PARENT, () ->
                new AddContactCommand(sameIdentity, null).execute(model));
    }

    @Test
    public void execute_invalidLink_throwsCommandException() {
        ModelManager model = new ModelManager();
        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, () ->
                new AddContactCommand(PARENT, Index.fromOneBased(1)).execute(model));
    }

    @Test
    public void execute_linkedStudent_success() throws Exception {
        ModelManager model = new ModelManager();
        Person student = new PersonBuilder().withName("John Tan").withPhone("91234567").build();
        model.addPerson(student);

        new AddContactCommand(PARENT, Index.fromOneBased(1)).execute(model);

        assertEquals(student.getStudentId(), model.getParentContactList().get(0).getLinkedStudentId());
        assertEquals(student.getName().fullName, model.getParentContactList().get(0).getLinkedStudentName());
    }

    @Test
    public void execute_studentAlreadyLinked_throwsCommandException() throws Exception {
        ModelManager model = new ModelManager();
        model.addPerson(new PersonBuilder().withName("John Tan").withPhone("91234567").build());
        new AddContactCommand(PARENT, Index.fromOneBased(1)).execute(model);

        ParentContact anotherParent = new ParentContact(new Name("Peter Tan"), new Phone("91234568"),
                new Email("peter@example.com"), null, null, List.of());
        new AddContactCommand(anotherParent, Index.fromOneBased(1)).execute(model);

        ParentContact yetAnotherParent = new ParentContact(new Name("Pete Tan"), new Phone("91234568"),
                new Email("peter@example.com"), null, null, List.of());
        assertThrows(CommandException.class, AddContactCommand.MESSAGE_STUDENT_ALREADY_LINKED, () ->
                new AddContactCommand(yetAnotherParent, Index.fromOneBased(1)).execute(model));
    }
}
