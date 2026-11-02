package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's email in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidEmail(String)}
 */
public class Email {

    public static final String MESSAGE_CONSTRAINTS = "Emails should be of the format example@domain";

    // Keep '-' last so it is treated literally inside the regex character class.
    private static final String SPECIAL_CHARACTERS = "+_.'-";
    private static final String ALPHANUMERIC_NO_UNDERSCORE = "[^\\W_]+";

    /*
     * The local part must start and end with an alphanumeric character.
     * Apostrophes and consecutive special characters are allowed internally.
     * The negative lookahead rejects consecutive dots before '@'.
     */
    private static final String LOCAL_PART_REGEX =
            "^(?![^@]*\\.\\.)" + ALPHANUMERIC_NO_UNDERSCORE
                    + "([" + SPECIAL_CHARACTERS + "]+" + ALPHANUMERIC_NO_UNDERSCORE + ")*";
    private static final String DOMAIN_PART_REGEX = ALPHANUMERIC_NO_UNDERSCORE
            + "(-" + ALPHANUMERIC_NO_UNDERSCORE + ")*";
    private static final String DOMAIN_REGEX = DOMAIN_PART_REGEX
            + "(\\." + DOMAIN_PART_REGEX + ")*";
    public static final String VALIDATION_REGEX = LOCAL_PART_REGEX + "@" + DOMAIN_REGEX;

    public final String value;

    /**
     * Constructs an {@code Email}.
     *
     * @param email A valid email address.
     */
    public Email(String email) {
        requireNonNull(email);
        checkArgument(isValidEmail(email), MESSAGE_CONSTRAINTS);
        value = email;
    }

    /**
     * Returns true if a given string is a valid email.
     */
    public static boolean isValidEmail(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Email otherEmail)) {
            return false;
        }

        return value.equals(otherEmail.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
