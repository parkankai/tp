package seedu.address.model.subject;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;

/**
 * Represents a Subject in the address book.
 * Guarantees: immutable; name is valid as declared in {@link #isValidSubjectName(String)}
 */
public class Subject {

    public static final String MESSAGE_CONSTRAINTS =
            "Subject names should be 2 to 30 characters long, contain only letters and spaces, "
                    + "and start and end with a letter.";

    /*
     * 2 to 30 characters, letters and spaces only.
     * The first and last characters must be letters,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX = "[A-Za-z][A-Za-z ]{0,28}[A-Za-z]";

    public final String subjectName;

    /**
     * Constructs a {@code Subject}.
     *
     * @param subjectName A valid subject name.
     */
    public Subject(String subjectName) {
        requireNonNull(subjectName);
        checkArgument(isValidSubjectName(subjectName), MESSAGE_CONSTRAINTS);
        this.subjectName = subjectName;
    }

    /**
     * Returns true if a given string is a valid subject name.
     */
    public static boolean isValidSubjectName(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return subjectName;
    }

    /**
     * Returns true if both subjects have the same name, ignoring case.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Subject otherSubject)) {
            return false;
        }

        return subjectName.equalsIgnoreCase(otherSubject.subjectName);
    }

    @Override
    public int hashCode() {
        return subjectName.toLowerCase(Locale.ROOT).hashCode();
    }

}
