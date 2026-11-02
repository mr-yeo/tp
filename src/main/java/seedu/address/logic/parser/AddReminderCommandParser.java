package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMINDER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMINDER_TIME;

import java.time.LocalDateTime;

import seedu.address.logic.commands.AddReminderCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses arguments for {@link AddReminderCommand}. */
public class AddReminderCommandParser implements Parser<AddReminderCommand> {

    @Override
    public AddReminderCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_REMINDER, PREFIX_REMINDER_TIME);
        if (!argMultimap.getPreamble().isEmpty() || argMultimap.getValue(PREFIX_REMINDER).isEmpty()) {
            throw invalidFormat();
        }
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_REMINDER, PREFIX_REMINDER_TIME);
        String text = argMultimap.getValue(PREFIX_REMINDER).orElse("");
        try {
            String reminderText = ParserUtil.parseReminderText(text);
            LocalDateTime dueAt = argMultimap.getValue(PREFIX_REMINDER_TIME).isPresent()
                    ? ParserUtil.parseReminderTime(argMultimap.getValue(PREFIX_REMINDER_TIME).orElseThrow()) : null;
            return new AddReminderCommand(reminderText, dueAt);
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    AddReminderCommand.MESSAGE_USAGE), pe);
        }
    }

    private static ParseException invalidFormat() {
        return new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddReminderCommand.MESSAGE_USAGE));
    }
}
