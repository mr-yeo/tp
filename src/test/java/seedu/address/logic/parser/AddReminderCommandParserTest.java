package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddReminderCommand;

class AddReminderCommandParserTest {
    private final AddReminderCommandParser parser = new AddReminderCommandParser();
    private final String invalidFormat = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
            AddReminderCommand.MESSAGE_USAGE);

    @Test void parse_validText_success() {
        assertParseSuccess(parser, " r/  Collect worksheets from cabinet B  ",
                new AddReminderCommand("Collect worksheets from cabinet B"));
        assertParseSuccess(parser, " r/Store worksheets in B/2",
                new AddReminderCommand("Store worksheets in B/2"));
    }

    @Test void parse_validTimedPastAndWhitespace_success() {
        assertParseSuccess(parser, " r/ Call Alice's parent  at/ 2026-10-22 18:00 ",
                new AddReminderCommand("Call Alice's parent", LocalDateTime.of(2026, 10, 22, 18, 0)));
        assertParseSuccess(parser, " r/Past reminder at/2020-02-29 00:00",
                new AddReminderCommand("Past reminder", LocalDateTime.of(2020, 2, 29, 0, 0)));
    }

    @Test void parse_missingBlankOrWhitespaceText_failure() {
        assertParseFailure(parser, "", invalidFormat);
        assertParseFailure(parser, "Collect worksheets", invalidFormat);
        assertParseFailure(parser, " r/", invalidFormat);
        assertParseFailure(parser, " r/   ", invalidFormat);
    }

    @Test void parse_duplicatePrefix_failure() {
        assertParseFailure(parser, " r/First r/Second",
                "Multiple values specified for the following single-valued field(s): r/");
        assertParseFailure(parser, " n/Alice r/First", invalidFormat);
        assertParseFailure(parser, " r/First at/2026-10-22 18:00 at/2026-10-23 18:00",
                "Multiple values specified for the following single-valued field(s): at/");
    }

    @Test void parse_invalidDueTime_failure() {
        assertParseFailure(parser, " r/First at/", invalidFormat);
        assertParseFailure(parser, " r/First at/2026-10-22", invalidFormat);
        assertParseFailure(parser, " r/First at/2026-02-30 18:00", invalidFormat);
        assertParseFailure(parser, " r/First at/2027-02-29 18:00", invalidFormat);
        assertParseFailure(parser, " r/First at/2026-10-22 25:00", invalidFormat);
        assertParseFailure(parser, " r/First at/22/10/2026 18:00", invalidFormat);
    }

    @Test void parse_extraPreambleOrUnsupportedKnownPrefix_failure() {
        assertParseFailure(parser, "extra r/First", invalidFormat);
    }
}
