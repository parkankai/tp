package seedu.address.model.subject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class SubjectTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Subject(null));
    }

    @Test
    public void constructor_invalidSubjectName_throwsIllegalArgumentException() {
        String expectedMessage = "Subject names should be 2 to 30 characters long, contain only letters and spaces, "
                + "and start and end with a letter.";

        assertThrows(IllegalArgumentException.class, expectedMessage, () -> new Subject(""));
        assertThrows(IllegalArgumentException.class, () -> new Subject("1"));
    }

    @Test
    public void isValidSubjectName() {
        assertThrows(NullPointerException.class, () -> Subject.isValidSubjectName(null));

        assertFalse(Subject.isValidSubjectName(""));
        assertFalse(Subject.isValidSubjectName(" "));
        assertFalse(Subject.isValidSubjectName("M"));
        assertFalse(Subject.isValidSubjectName("a".repeat(31)));
        assertFalse(Subject.isValidSubjectName("Math2"));
        assertFalse(Subject.isValidSubjectName("A-Math"));
        assertFalse(Subject.isValidSubjectName(" Math"));
        assertFalse(Subject.isValidSubjectName("Math "));

        assertTrue(Subject.isValidSubjectName("PE"));
        assertTrue(Subject.isValidSubjectName("a".repeat(30)));
        assertTrue(Subject.isValidSubjectName("Math"));
        assertTrue(Subject.isValidSubjectName("Additional Mathematics"));
    }

    @Test
    public void toStringMethod() {
        assertEquals("Math", new Subject("Math").toString());
    }

    @Test
    public void equals() {
        Subject subject = new Subject("Math");

        assertTrue(subject.equals(subject));
        assertTrue(subject.equals(new Subject("Math")));
        assertTrue(subject.equals(new Subject("math")));
        assertFalse(subject.equals(null));
        assertFalse(subject.equals(5.0f));
        assertFalse(subject.equals("Math"));
        assertFalse(subject.equals(new Subject("Science")));
    }

    @Test
    public void hashCode_sameNameDifferentCase_sameHashCode() {
        assertEquals(new Subject("Math").hashCode(), new Subject("math").hashCode());
    }
}
