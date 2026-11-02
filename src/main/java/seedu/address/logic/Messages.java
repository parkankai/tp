package seedu.address.logic;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.person.Student;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n%1$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The student index provided is invalid.";
    public static final String MESSAGE_SUBJECT_NOT_TAKEN = "%1$s does not take %2$s.";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d student(s) listed!";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";
    public static final String MESSAGE_DUPLICATE_SUBJECT_WARNING =
            "Warning: Duplicate subject '%s' was ignored; subjects are case-insensitive.";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Returns warnings for subject values that were merged with an earlier subject value.
     */
    public static String getWarningMessageForDuplicateSubjects(List<String> duplicateSubjects) {
        return duplicateSubjects.stream()
                .map(subject -> String.format(MESSAGE_DUPLICATE_SUBJECT_WARNING, subject))
                .collect(Collectors.joining("\n"));
    }

    /**
     * Formats the {@code student} for display to the user.
     */
    public static String format(Student student) {
        final StringBuilder builder = new StringBuilder();
        builder.append(student.getName())
                .append("; Level: ")
                .append(student.getAcademicLevel())
                .append("; Subjects: ")
                .append(student.getSubjects())
                .append("; Parent Phone: ")
                .append(student.getParentPhone())
                .append("; Parent Email: ")
                .append(student.getParentEmail());
        return builder.toString();
    }

}
