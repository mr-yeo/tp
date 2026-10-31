package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.LogicManager;

/**
 * Tests that a {@code ParentContact}'s {@code attributes} matches any of the keywords given.
 */
public class ParentContainsKeywordsPredicate implements Predicate<ParentContact> {
    private final List<String> keywords;
    private final Logger logger = LogsCenter.getLogger(LogicManager.class);
    private ParentContact parentContact;

    /**
     * Creates a {@code ParentContainsKeywordsPredicate} with keywords.
     *
     * @param keywords the keywords used for testing.
     */
    public ParentContainsKeywordsPredicate(List<String> keywords) {
        requireNonNull(keywords);
        this.keywords = keywords;
    }

    @Override
    public boolean test(ParentContact parentContact) {
        return keywords.stream()
                .anyMatch(keyword -> matchesParent(parentContact, keyword));
    }

    private boolean matchesParent(ParentContact parentContact, String keyword) {
        this.parentContact = parentContact;
        return containsIgnoreCase(parentContact.getName().fullName, keyword)
                || containsIgnoreCase(parentContact.getPhone().value, keyword)
                || containsIgnoreCase(parentContact.getEmail() == null ? null : parentContact.getEmail().value, keyword)
                || containsIgnoreCase(parentContact.getLinkedStudentName(), keyword)
                || checkIfRequirementsContainKeyword(parentContact.getRequirements(), keyword);
    }

    private boolean checkIfRequirementsContainKeyword(List<String> requirements, String keyword) {
        boolean result = false;
        for (String requirement : requirements) {
            result = result || containsIgnoreCase(requirement, keyword);
        }
        if (result) {
            logger.fine("Parent " + parentContact.getName().fullName + " matches " + keyword
                    + " in attribute: Requirements");
        }

        return result;
    }

    private boolean containsIgnoreCase(String field, String keyword) {
        if (field == null) {
            return false;
        }
        boolean result = field.toLowerCase().contains(keyword.toLowerCase());
        if (result) {
            logger.fine("Parent " + parentContact.getName().fullName + " matches " + keyword
                    + " in attribute: " + field);
        }
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ParentContainsKeywordsPredicate otherParentContainsKeywordsPredicate)) {
            return false;
        }

        return keywords.equals(otherParentContainsKeywordsPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}


