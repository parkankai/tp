package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.lesson.LessonDate;
import seedu.address.model.lesson.LessonNote;
import seedu.address.model.lesson.NoteText;
import seedu.address.model.subject.Subject;
import seedu.address.testutil.PersonBuilder;

public class StudentTest {

    private static final LessonNote EARLIEST_NOTE = new LessonNote(new LessonDate("2024-09-04"),
            new Subject("Math"), new NoteText("Introduced factorisation"));
    private static final LessonNote MIDDLE_NOTE = new LessonNote(new LessonDate("2024-09-11"),
            new Subject("Science"), new NoteText("Recapped photosynthesis"));
    private static final LessonNote LATEST_NOTE = new LessonNote(new LessonDate("2024-09-18"),
            new Subject("Math"), new NoteText("Covered quadratic equations"));

    @Test
    public void constructor_nullLessonNotes_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Student(ALICE.getName(), ALICE.getAcademicLevel(),
                ALICE.getSubjects(), ALICE.getParentPhone(), ALICE.getParentEmail(), null));
    }

    @Test
    public void constructor_emptySubjects_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Student(ALICE.getName(), ALICE.getAcademicLevel(),
                Set.of(), ALICE.getParentPhone(), ALICE.getParentEmail()));
    }

    @Test
    public void constructor_withoutLessonNotes_hasNoLessonNotes() {
        Student student = new Student(ALICE.getName(), ALICE.getAcademicLevel(), ALICE.getSubjects(),
                ALICE.getParentPhone(), ALICE.getParentEmail());
        assertTrue(student.getLessonNotes().isEmpty());
    }

    @Test
    public void findSubject_subjectTaken_returnsProfileSubject() {
        Student student = new PersonBuilder().withSubjects("Math", "Science").build();
        assertEquals(Optional.of(new Subject("Math")), student.findSubject(new Subject("Math")));

        // different case -> returns the subject as spelled in the profile
        assertEquals("Math", student.findSubject(new Subject("math")).get().subjectName);
    }

    @Test
    public void findSubject_subjectNotTaken_returnsEmpty() {
        Student student = new PersonBuilder().withSubjects("Math").build();
        assertTrue(student.findSubject(new Subject("Geography")).isEmpty());
    }

    @Test
    public void findSubject_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ALICE.findSubject(null));
    }

    @Test
    public void getLessonNotes_notesGivenOutOfOrder_sortedByMostRecentDateFirst() {
        Student student = new PersonBuilder().withLessonNotes(MIDDLE_NOTE, EARLIEST_NOTE, LATEST_NOTE).build();
        assertEquals(List.of(LATEST_NOTE, MIDDLE_NOTE, EARLIEST_NOTE), student.getLessonNotes());
    }

    @Test
    public void getLessonNotes_notesOnSameDate_keepGivenOrder() {
        LessonNote firstGiven = new LessonNote(new LessonDate("2024-09-18"), new Subject("Science"),
                new NoteText("Make-up lesson"));
        LessonNote secondGiven = LATEST_NOTE;

        Student student = new PersonBuilder().withLessonNotes(EARLIEST_NOTE, firstGiven, secondGiven).build();
        assertEquals(List.of(firstGiven, secondGiven, EARLIEST_NOTE), student.getLessonNotes());
    }

    @Test
    public void getLessonNotes_modifyList_throwsUnsupportedOperationException() {
        Student student = new PersonBuilder().withLessonNotes(LATEST_NOTE).build();
        assertThrows(UnsupportedOperationException.class, () -> student.getLessonNotes().add(EARLIEST_NOTE));
        assertThrows(UnsupportedOperationException.class, () -> student.getLessonNotes().remove(0));
    }

    @Test
    public void getLessonNotes_originalListModified_studentUnchanged() {
        List<LessonNote> originalList = new ArrayList<>(List.of(LATEST_NOTE));
        Student student = new Student(ALICE.getName(), ALICE.getAcademicLevel(), ALICE.getSubjects(),
                ALICE.getParentPhone(), ALICE.getParentEmail(), originalList);

        originalList.add(EARLIEST_NOTE);

        assertEquals(List.of(LATEST_NOTE), student.getLessonNotes());
    }

    @Test
    public void equals_lessonNotes() {
        Student student = new PersonBuilder().build();

        // different lesson notes -> returns false
        assertFalse(student.equals(new PersonBuilder(student).withLessonNotes(LATEST_NOTE).build()));

        // same lesson notes given in a different order -> returns true
        Student studentWithNotes = new PersonBuilder(student).withLessonNotes(EARLIEST_NOTE, LATEST_NOTE).build();
        Student studentWithReorderedNotes = new PersonBuilder(student).withLessonNotes(LATEST_NOTE, EARLIEST_NOTE)
                .build();
        assertTrue(studentWithNotes.equals(studentWithReorderedNotes));
        assertEquals(studentWithNotes.hashCode(), studentWithReorderedNotes.hashCode());
    }

    @Test
    public void toStringMethod() {
        String expected = Student.class.getCanonicalName() + "{name=" + ALICE.getName()
                + ", academicLevel=" + ALICE.getAcademicLevel() + ", subjects=" + ALICE.getSubjects()
                + ", parentPhone=" + ALICE.getParentPhone() + ", parentEmail=" + ALICE.getParentEmail()
                + ", lessonNotes=" + ALICE.getLessonNotes() + "}";
        assertEquals(expected, ALICE.toString());
    }

    @Test
    public void isSameStudent_null_returnsFalse() {
        Student student = new PersonBuilder().build();

        assertFalse(student.isSameStudent(null));
    }

    @Test
    public void isSameStudent_sameNameDifferentPhone_returnsFalse() {
        Student student = new PersonBuilder().build();
        Student sameNameDifferentPhone = new PersonBuilder(student)
                .withPhone("91234567")
                .build();

        assertFalse(student.isSameStudent(sameNameDifferentPhone));
    }

    @Test
    public void isSameStudent_sameNameAndPhoneDifferentSubjects_returnsTrue() {
        Student student = new PersonBuilder().build();
        Student sameNameAndPhoneDifferentSubjects = new PersonBuilder(student)
                .withSubjects("Science")
                .build();

        assertTrue(student.isSameStudent(sameNameAndPhoneDifferentSubjects));
    }

    @Test
    public void equals_nonStudent_returnsFalse() {
        Student student = new PersonBuilder().build();

        assertFalse(student.equals("student"));
    }

    @Test
    public void hashCode_sameStudents_returnsSameHashCode() {
        Student student = new PersonBuilder().build();

        assertEquals(student.hashCode(), new PersonBuilder(student).build().hashCode());
    }

    @Test
    public void equals_differentFields_returnsFalse() {
        Student student = new PersonBuilder().build();

        assertFalse(student.equals(new PersonBuilder(student).withAcademicLevel("Pri 1").build()));
        assertFalse(student.equals(new PersonBuilder(student).withSubjects("Science").build()));
        assertFalse(student.equals(new PersonBuilder(student).withPhone("91234567").build()));
        assertFalse(student.equals(new PersonBuilder(student).withEmail("student@example.com").build()));
    }
}
