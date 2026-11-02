package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.ParentContact;
import seedu.address.testutil.ParentContactBuilder;

public class DeleteParentPhotoCommandTest {

    private final Model model = new ModelManager();

    @Test
    public void execute_parentWithoutPhoto_failure() {
        ParentContact parent = new ParentContactBuilder().build();
        model.addParentContact(parent);

        assertCommandFailure(new DeleteParentPhotoCommand(INDEX_FIRST_PERSON), model,
                String.format(DeleteParentPhotoCommand.MESSAGE_NO_PHOTO, parent.getName()));
    }

    @Test
    public void execute_parentWithPhoto_success() {
        ParentContact parent = new ParentContactBuilder().withPhotoPath("photos/parent.png").build();
        model.addParentContact(parent);

        Model expectedModel = new ModelManager(model.getAddressBook(), model.getUserPrefs());
        ParentContact updated = new ParentContactBuilder(parent).withPhotoPath("").build();
        expectedModel.setParentContact(parent, updated);

        assertCommandSuccess(new DeleteParentPhotoCommand(INDEX_FIRST_PERSON), model,
                "Deleted photo from parent: " + updated.getName(), expectedModel);
    }
}
