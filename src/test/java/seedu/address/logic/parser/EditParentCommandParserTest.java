package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditParentCommand;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;

public class EditParentCommandParserTest {
    private final EditParentCommandParser parser = new EditParentCommandParser();

    @Test
    public void parse_invalidParentIndex_reportsReasonAndUsage() {
        assertFailure("", "A parent index is required.");
        assertFailure(" n/Mary", "A parent index is required.");
        for (String index : new String[] {"0", "-1", "abc", "1 extra", "2147483648"}) {
            assertFailure(" " + index + " n/Mary", "Parent index must be a positive integer.");
        }
    }

    @Test
    public void parse_noEditsOrUnsupportedPrefix_reportsReasonAndUsage() {
        assertFailure(" 1", "At least one field to edit must be provided.");
        assertFailure(" 1 x/hello", "Unsupported prefix: x/.");
        assertFailure(" 1 r/No food x/hello", "Unsupported prefix: x/.");
    }

    @Test
    public void parse_duplicateSingleValuedFields_reportsReasonAndUsage() {
        for (Prefix prefix : new Prefix[] {CliSyntax.PREFIX_NAME, CliSyntax.PREFIX_PHONE,
            CliSyntax.PREFIX_EMAIL, CliSyntax.PREFIX_LINK}) {
            assertFailure(" 1 " + prefix + "Mary " + prefix + "Mary",
                    Messages.getErrorMessageForDuplicatePrefixes(prefix));
        }
    }

    @Test
    public void parse_invalidValues_reportsReasonAndUsage() {
        assertFailure(" 1 n/", Name.MESSAGE_CONSTRAINTS);
        assertFailure(" 1 n/Mary#", Name.MESSAGE_CONSTRAINTS);
        assertFailure(" 1 p/12", Phone.MESSAGE_CONSTRAINTS);
        assertFailure(" 1 e/invalid", Email.MESSAGE_CONSTRAINTS);
        assertFailure(" 1 link/0", "Student index must be a positive integer.");
        assertFailure(" 1 link/abc", "Student index must be a positive integer.");
        assertFailure(" 1 r/ r/Keep", "Use a single empty r/ to clear requirements, "
                + "or supply nonempty text for every r/ entry.");
        assertFailure(" 1 r/Keep r/", "Use a single empty r/ to clear requirements, "
                + "or supply nonempty text for every r/ entry.");
    }

    private void assertFailure(String input, String reason) {
        assertParseFailure(parser, input, EditParentCommand.withUsage(reason));
    }
}
