package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.ParentContact;
import seedu.address.model.person.Person;
import seedu.address.testutil.ParentContactBuilder;
import seedu.address.testutil.PersonBuilder;

public class MessagesTest {

    @Test
    public void formatPerson_noTags_omitsTagsLabel() {
        Person person = new PersonBuilder().build();

        String formatted = Messages.format(person);

        assertFalse(formatted.contains("Tags"));
        assertEquals("Amy Bee; Phone: 85355255; Email: amy@gmail.com; "
                + "Address: 123, Jurong West Ave 6, #08-111; Subject: Mathematics", formatted);
    }

    @Test
    public void formatPerson_withTags_showsTags() {
        Person person = new PersonBuilder().withTags("friend").build();

        assertEquals("Amy Bee; Phone: 85355255; Email: amy@gmail.com; "
                + "Address: 123, Jurong West Ave 6, #08-111; Subject: Mathematics; Tags: [friend]",
                Messages.format(person));
    }

    @Test
    public void formatParent_noRequirements_omitsRequirementsLabel() {
        ParentContact parent = new ParentContactBuilder().build();

        String formatted = Messages.format(parent);

        assertFalse(formatted.contains("Requirements"));
        assertEquals("Amy Bee; Phone: 85355255; Email: amy@gmail.com", formatted);
    }

    @Test
    public void formatParent_withRequirements_showsRequirements() {
        ParentContact parent = new ParentContactBuilder()
                .withRequirements("Vegetarian", "No peanuts").build();

        assertEquals("Amy Bee; Phone: 85355255; Email: amy@gmail.com; "
                + "Requirements: Vegetarian, No peanuts", Messages.format(parent));
    }
}
