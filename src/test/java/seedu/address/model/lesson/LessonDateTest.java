package seedu.address.model.lesson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class LessonDateTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new LessonDate(null));
    }

    @Test
    public void constructor_invalidLessonDate_throwsIllegalArgumentException() {
        String invalidLessonDate = "";
        assertThrows(IllegalArgumentException.class, () -> new LessonDate(invalidLessonDate));
    }

    @Test
    public void isValidLessonDate() {
        // null lesson date
        assertThrows(NullPointerException.class, () -> LessonDate.isValidLessonDate(null));

        // invalid format
        assertFalse(LessonDate.isValidLessonDate("")); // empty string
        assertFalse(LessonDate.isValidLessonDate(" ")); // spaces only
        assertFalse(LessonDate.isValidLessonDate("18-09-2026")); // day first
        assertFalse(LessonDate.isValidLessonDate("2026/09/18")); // wrong separator
        assertFalse(LessonDate.isValidLessonDate("2026-9-18")); // month not padded
        assertFalse(LessonDate.isValidLessonDate("26-09-18")); // two-digit year
        assertFalse(LessonDate.isValidLessonDate("2026-09-18 ")); // trailing space
        assertFalse(LessonDate.isValidLessonDate("18 Sep 2026")); // month in words

        // not a real calendar date
        assertFalse(LessonDate.isValidLessonDate("2026-02-30")); // 30 February
        assertFalse(LessonDate.isValidLessonDate("2025-02-29")); // 29 February in a non-leap year
        assertFalse(LessonDate.isValidLessonDate("2026-13-01")); // month 13
        assertFalse(LessonDate.isValidLessonDate("2026-00-10")); // month 0

        // future date
        assertFalse(LessonDate.isValidLessonDate(LocalDate.now().plusDays(1).toString())); // tomorrow

        // before the minimum permitted date
        assertFalse(LessonDate.isValidLessonDate("0000-01-01")); // year 0000
        assertFalse(LessonDate.isValidLessonDate("0001-01-01")); // year 0001
        assertFalse(LessonDate.isValidLessonDate("1999-12-31")); // day before minimum

        // valid lesson date
        assertTrue(LessonDate.isValidLessonDate("2000-01-01")); // minimum
        assertTrue(LessonDate.isValidLessonDate("2020-01-01")); // past date
        assertTrue(LessonDate.isValidLessonDate("2024-02-29")); // 29 February in a leap year
        assertTrue(LessonDate.isValidLessonDate(LocalDate.now().minusDays(1).toString())); // yesterday
        assertTrue(LessonDate.isValidLessonDate(LocalDate.now().toString())); // today
    }

    @Test
    public void compareTo() {
        LessonDate earlier = new LessonDate("2024-09-04");
        LessonDate later = new LessonDate("2024-09-18");

        // earlier date -> negative
        assertTrue(earlier.compareTo(later) < 0);

        // later date -> positive
        assertTrue(later.compareTo(earlier) > 0);

        // same date -> zero
        assertEquals(0, earlier.compareTo(new LessonDate("2024-09-04")));
    }

    @Test
    public void toStringMethod() {
        assertEquals("2024-09-18", new LessonDate("2024-09-18").toString());
    }

    @Test
    public void equals() {
        LessonDate lessonDate = new LessonDate("2024-09-18");

        // same values -> returns true
        assertTrue(lessonDate.equals(new LessonDate("2024-09-18")));

        // same object -> returns true
        assertTrue(lessonDate.equals(lessonDate));

        // null -> returns false
        assertFalse(lessonDate.equals(null));

        // different types -> returns false
        assertFalse(lessonDate.equals(5.0f));

        // different values -> returns false
        assertFalse(lessonDate.equals(new LessonDate("2024-09-04")));
    }

    @Test
    public void hashCode_sameDate_sameHashCode() {
        assertEquals(new LessonDate("2024-09-18").hashCode(), new LessonDate("2024-09-18").hashCode());
    }
}
