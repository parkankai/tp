package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;

/**
 * Represents a Person's name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class Name {

    /** Error message shown when a student name fail. */
    public static final String MESSAGE_CONSTRAINTS =
            "Names should only contain letters, accent marks, digits, spaces, apostrophes, - and /, "
                    + "must contain at least one letter or digit, and should be at most 60 characters long.";

    public static final int MAX_LENGTH = 60;

    /*
     * The lookahead requires at least one letter or digit anywhere in the name.
     * Allows Unicode letters, combining accent marks, and straight or curly apostrophes.
     * The first character must be a letter, an ASCII digit, a slash, or a hyphen.
     */
    public static final String VALIDATION_REGEX =
            "(?=.*[\\p{L}0-9])[\\p{L}0-9/\\-][\\p{L}\\p{M}0-9/\\- '\u2019]*";

    public final String fullName;

    /**
     * Constructs a {@code Name}.
     *
     * @param name A valid name.
     */
    public Name(String name) {
        requireNonNull(name);
        String normalisedName = name.trim().replaceAll("\\s+", " ");
        checkArgument(isValidName(normalisedName), MESSAGE_CONSTRAINTS);
        fullName = normalisedName;
    }

    /**
     * Returns true if a given string is a valid name.
     */
    public static boolean isValidName(String test) {
        return test.length() <= MAX_LENGTH && test.matches(VALIDATION_REGEX);
    }


    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Name otherName)) {
            return false;
        }

        return fullName.equalsIgnoreCase(otherName.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.toLowerCase(Locale.ROOT).hashCode();
    }

}
