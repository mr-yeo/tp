package seedu.address.logic.parser;

import java.time.DateTimeException;
import java.time.LocalDate;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.StatsCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.statistics.HistoryValidation;

/** Parses stats and stats-student, requiring either both date boundaries or neither. */
public class StatsCommandParser implements Parser<StatsCommand> {
    private final boolean studentReport;

    public StatsCommandParser(boolean studentReport) {
        this.studentReport = studentReport;
    }

    @Override
    public StatsCommand parse(String args) throws ParseException {
        try {
            return parseReport(args);
        } catch (DateTimeException e) {
            throw new ParseException("Invalid statistics date range: from/ and to/ "
                    + "must contain a valid calendar date in YYYY-MM-DD format."
                    + "\n" + StatsCommand.usage(studentReport), e);
        } catch (ParseException | IllegalArgumentException e) {
            throw new ParseException(e.getMessage() + "\n" + StatsCommand.usage(studentReport), e);
        }
    }

    private StatsCommand parseReport(String args) throws ParseException {
        ArgumentMultimap values = HistoryCommandParser.arguments(args, "from", "to");
        if (studentReport && values.getPreamble().isEmpty()) {
            throw new ParseException("A student index is required.");
        }
        Index student = studentReport ? ParserUtil.parseIndex(values.getPreamble()) : null;
        boolean hasFrom = values.getValue(new Prefix("from/")).isPresent();
        boolean hasTo = values.getValue(new Prefix("to/")).isPresent();
        if (!studentReport && !values.getPreamble().isEmpty()) {
            throw new ParseException("stats does not take a student index or other unprefixed arguments.");
        }
        if (hasFrom != hasTo) {
            throw new ParseException("Supply both from/ and to/ dates together.");
        }
        try {
            LocalDate from = hasFrom ? HistoryValidation.date(values.getValue(new Prefix("from/")).orElseThrow())
                    : null;
            LocalDate to = hasTo ? HistoryValidation.date(values.getValue(new Prefix("to/")).orElseThrow()) : null;
            if (from != null && from.isAfter(to)) {
                throw new IllegalArgumentException("from must be on or before to.");
            }
            return new StatsCommand(student, from, to);
        } catch (IllegalArgumentException e) {
            throw new ParseException("Invalid statistics date range: " + e.getMessage(), e);
        }
    }
}
