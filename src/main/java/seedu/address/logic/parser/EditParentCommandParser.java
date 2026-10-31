package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LINK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REQUIREMENT;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditParentCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;

/** Parses input arguments for editing a parent contact. */
public class EditParentCommandParser implements Parser<EditParentCommand> {
    private static final Pattern PREFIX_PATTERN = Pattern.compile("(?:^| )([a-zA-Z][a-zA-Z0-9-]*)/");
    private static final Set<String> SUPPORTED_PREFIXES = Set.of("n", "p", "e", "link", "r");

    @Override
    public EditParentCommand parse(String args) throws ParseException {
        try {
            return parseEdit(args);
        } catch (ParseException e) {
            throw new ParseException(EditParentCommand.withUsage(e.getMessage()), e);
        }
    }

    private EditParentCommand parseEdit(String args) throws ParseException {
        Matcher matcher = PREFIX_PATTERN.matcher(args);
        while (matcher.find()) {
            if (!SUPPORTED_PREFIXES.contains(matcher.group(1))) {
                throw new ParseException("Unsupported prefix: " + matcher.group(1) + "/.");
            }
        }
        ArgumentMultimap map = ArgumentTokenizer.tokenize(" " + args.trim(), PREFIX_NAME, PREFIX_PHONE,
                PREFIX_EMAIL, PREFIX_LINK, PREFIX_REQUIREMENT);
        if (map.getPreamble().isEmpty()) {
            throw new ParseException("A parent index is required.");
        }
        Index index = parseIndex(map.getPreamble(), "Parent");
        map.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_LINK);
        Name name = map.getValue(PREFIX_NAME).isPresent()
                ? ParserUtil.parseName(map.getValue(PREFIX_NAME).get()) : null;
        Phone phone = map.getValue(PREFIX_PHONE).isPresent()
                ? ParserUtil.parsePhone(map.getValue(PREFIX_PHONE).get()) : null;
        Email email = map.getValue(PREFIX_EMAIL).isPresent()
                ? ParserUtil.parseOptionalEmail(map.getValue(PREFIX_EMAIL).get()) : null;
        boolean linkSpecified = map.getValue(PREFIX_LINK).isPresent();
        String linkValue = map.getValue(PREFIX_LINK).orElse("");
        Index linkIndex = linkValue.isEmpty() ? null : parseIndex(linkValue, "Student");
        List<String> requirements = null;
        if (map.getValue(PREFIX_REQUIREMENT).isPresent()) {
            requirements = map.getAllValues(PREFIX_REQUIREMENT);
            if (requirements.size() == 1 && requirements.getFirst().isEmpty()) {
                requirements = List.of();
            } else if (requirements.stream().anyMatch(String::isEmpty)) {
                throw new ParseException("Use a single empty r/ to clear requirements, "
                        + "or supply nonempty text for every r/ entry.");
            }
        }
        if (name == null && phone == null && email == null && !linkSpecified && requirements == null) {
            throw new ParseException("At least one field to edit must be provided.");
        }
        return new EditParentCommand(index, name, phone, email, linkSpecified, linkIndex, requirements);
    }

    private Index parseIndex(String value, String field) throws ParseException {
        try {
            return ParserUtil.parseIndex(value);
        } catch (ParseException e) {
            throw new ParseException(field + " index must be a positive integer.", e);
        }
    }
}
