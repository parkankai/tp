package seedu.address.model.lesson;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Represents the date of a lesson in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidLessonDate(String)}
 */
public class LessonDate implements Comparable<LessonDate> {

    public static final LocalDate MIN_DATE = LocalDate.of(2000, Month.JANUARY, 1);

    public static final String MESSAGE_CONSTRAINTS =
            "Date must be in YYYY-MM-DD format, cannot be before " + MIN_DATE
                    + ", and cannot be a future date.";

    /*
     * Exactly 4 digits, 2 digits and 2 digits, separated by hyphens.
     * Whether the digits form a real calendar date is checked separately.
     */
    public static final String VALIDATION_REGEX = "\\d{4}-\\d{2}-\\d{2}";

    // STRICT rejects dates that do not exist, such as 2026-02-30.
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);

    public final LocalDate value;

    /**
     * Constructs a {@code LessonDate}.
     *
     * @param date A valid lesson date.
     */
    public LessonDate(String date) {
        requireNonNull(date);
        checkArgument(isValidLessonDate(date), MESSAGE_CONSTRAINTS);
        value = LocalDate.parse(date, FORMATTER);
    }

    /**
     * Returns true if a given string is a real calendar date in YYYY-MM-DD format
     * that is not before {@link #MIN_DATE} or later than today.
     */
    public static boolean isValidLessonDate(String test) {
        if (!test.matches(VALIDATION_REGEX)) {
            return false;
        }

        try {
            LocalDate date = LocalDate.parse(test, FORMATTER);
            return !date.isBefore(MIN_DATE) && !date.isAfter(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    @Override
    public int compareTo(LessonDate other) {
        return value.compareTo(other.value);
    }

    @Override
    public String toString() {
        return value.format(FORMATTER);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof LessonDate otherDate)) {
            return false;
        }

        return value.equals(otherDate.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
