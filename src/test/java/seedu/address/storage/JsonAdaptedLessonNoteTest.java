package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.storage.JsonAdaptedLessonNote.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.lesson.LessonDate;
import seedu.address.model.lesson.LessonNote;
import seedu.address.model.lesson.NoteText;
import seedu.address.model.subject.Subject;

public class JsonAdaptedLessonNoteTest {
    private static final String INVALID_DATE = "18-09-2024";
    private static final String INVALID_SUBJECT = "Math2";
    private static final String INVALID_NOTE_TEXT = " ";

    private static final String VALID_DATE = "2024-09-18";
    private static final String VALID_SUBJECT = "Math";
    private static final String VALID_NOTE_TEXT = "Covered quadratic equations";

    private static final LessonNote VALID_LESSON_NOTE = new LessonNote(new LessonDate(VALID_DATE),
            new Subject(VALID_SUBJECT), new NoteText(VALID_NOTE_TEXT));

    @Test
    public void toModelType_validLessonNoteDetails_returnsLessonNote() throws Exception {
        JsonAdaptedLessonNote lessonNote = new JsonAdaptedLessonNote(VALID_LESSON_NOTE);
        assertEquals(VALID_LESSON_NOTE, lessonNote.toModelType());
    }

    @Test
    public void toModelType_validStrings_returnsLessonNote() throws Exception {
        JsonAdaptedLessonNote lessonNote = new JsonAdaptedLessonNote(VALID_DATE, VALID_SUBJECT, VALID_NOTE_TEXT);
        assertEquals(VALID_LESSON_NOTE, lessonNote.toModelType());
    }

    @Test
    public void toModelType_invalidDate_throwsIllegalValueException() {
        JsonAdaptedLessonNote lessonNote = new JsonAdaptedLessonNote(INVALID_DATE, VALID_SUBJECT, VALID_NOTE_TEXT);
        String expectedMessage = LessonDate.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, lessonNote::toModelType);
    }

    @Test
    public void toModelType_dateBeforeMinimum_throwsIllegalValueException() {
        JsonAdaptedLessonNote lessonNote = new JsonAdaptedLessonNote("1999-12-31", VALID_SUBJECT, VALID_NOTE_TEXT);
        String expectedMessage = LessonDate.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, lessonNote::toModelType);
    }

    @Test
    public void toModelType_nullDate_throwsIllegalValueException() {
        JsonAdaptedLessonNote lessonNote = new JsonAdaptedLessonNote(null, VALID_SUBJECT, VALID_NOTE_TEXT);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, LessonDate.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, lessonNote::toModelType);
    }

    @Test
    public void toModelType_invalidSubject_throwsIllegalValueException() {
        JsonAdaptedLessonNote lessonNote = new JsonAdaptedLessonNote(VALID_DATE, INVALID_SUBJECT, VALID_NOTE_TEXT);
        String expectedMessage = Subject.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, lessonNote::toModelType);
    }

    @Test
    public void toModelType_nullSubject_throwsIllegalValueException() {
        JsonAdaptedLessonNote lessonNote = new JsonAdaptedLessonNote(VALID_DATE, null, VALID_NOTE_TEXT);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Subject.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, lessonNote::toModelType);
    }

    @Test
    public void toModelType_invalidNoteText_throwsIllegalValueException() {
        JsonAdaptedLessonNote lessonNote = new JsonAdaptedLessonNote(VALID_DATE, VALID_SUBJECT, INVALID_NOTE_TEXT);
        String expectedMessage = NoteText.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, lessonNote::toModelType);
    }

    @Test
    public void toModelType_nullNoteText_throwsIllegalValueException() {
        JsonAdaptedLessonNote lessonNote = new JsonAdaptedLessonNote(VALID_DATE, VALID_SUBJECT, null);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, NoteText.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, lessonNote::toModelType);
    }

}
