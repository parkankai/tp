package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.lesson.LessonDate;
import seedu.address.model.lesson.LessonNote;
import seedu.address.model.lesson.NoteText;
import seedu.address.model.person.Student;
import seedu.address.model.subject.Subject;
import seedu.address.testutil.PersonBuilder;

public class EditNoteCommandTest {

    private static final LessonNote NEWER_NOTE = new LessonNote(
            new LessonDate("2026-10-01"), new Subject("Math"), new NoteText("Revised equations"));
    private static final LessonNote OLDER_NOTE = new LessonNote(
            new LessonDate("2026-09-20"), new Subject("Science"), new NoteText("Covered photosynthesis"));

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    private Student seedFirstStudentWith(LessonNote... notes) {
        Student firstStudent = model.getFilteredStudentList().get(INDEX_FIRST_PERSON.getZeroBased());
        Student seededStudent = new PersonBuilder(firstStudent).withLessonNotes(notes).build();
        model.setStudent(firstStudent, seededStudent);
        return seededStudent;
    }

    private Model createExpectedModel(Student originalStudent, Student editedStudent) {
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setStudent(originalStudent, editedStudent);
        return expectedModel;
    }

    @Test
    public void findNoteIndex_equalNotes_returnsExactInstanceIndex() {
        LessonNote equalNote = new LessonNote(
                NEWER_NOTE.getDate(), NEWER_NOTE.getSubject(), NEWER_NOTE.getNoteText());

        assertEquals(2, EditNoteCommand.findNoteIndex(List.of(equalNote, NEWER_NOTE), NEWER_NOTE));
    }

    @Test
    public void findNoteIndex_missingNote_throwsAssertionError() {
        assertThrows(AssertionError.class, () ->
                EditNoteCommand.findNoteIndex(List.of(OLDER_NOTE), NEWER_NOTE));
        assertThrows(AssertionError.class, () ->
                EditNoteCommand.findNoteIndex(List.of(), NEWER_NOTE));
    }

    @Test
    public void findNoteIndex_onlyEqualNote_throwsAssertionError() {
        LessonNote equalNote = new LessonNote(
                NEWER_NOTE.getDate(), NEWER_NOTE.getSubject(), NEWER_NOTE.getNoteText());

        assertThrows(AssertionError.class, () ->
                EditNoteCommand.findNoteIndex(List.of(equalNote), NEWER_NOTE));
    }

    @Test
    public void execute_editText_success() {
        Student seededStudent = seedFirstStudentWith(NEWER_NOTE, OLDER_NOTE);
        LessonNote editedNote = new LessonNote(
                NEWER_NOTE.getDate(), NEWER_NOTE.getSubject(), new NoteText("Practised factorisation"));
        Student editedStudent = new PersonBuilder(seededStudent)
                .withLessonNotes(editedNote, OLDER_NOTE).build();

        EditNoteCommand command = new EditNoteCommand(
                INDEX_FIRST_PERSON, INDEX_FIRST_PERSON,
                Optional.empty(), Optional.empty(),
                Optional.of(new NoteText("Practised factorisation")));

        String expectedMessage = String.format(EditNoteCommand.MESSAGE_EDIT_NOTE_SUCCESS,
                INDEX_FIRST_PERSON.getOneBased(), seededStudent.getName());

        assertCommandSuccess(command, model, expectedMessage,
                createExpectedModel(seededStudent, editedStudent));
        assertEquals(editedNote, model.getFilteredStudentList()
                .get(INDEX_FIRST_PERSON.getZeroBased()).getLessonNotes().get(0));
    }

    @Test
    public void execute_editDate_reordersHistory() {
        Student seededStudent = seedFirstStudentWith(NEWER_NOTE, OLDER_NOTE);
        LessonNote editedOlderNote = new LessonNote(
                new LessonDate("2026-10-10"), OLDER_NOTE.getSubject(), OLDER_NOTE.getNoteText());
        Student editedStudent = new PersonBuilder(seededStudent)
                .withLessonNotes(editedOlderNote, NEWER_NOTE).build();

        EditNoteCommand command = new EditNoteCommand(
                INDEX_FIRST_PERSON, INDEX_SECOND_PERSON,
                Optional.of(new LessonDate("2026-10-10")), Optional.empty(), Optional.empty());

        String expectedMessage = String.format(EditNoteCommand.MESSAGE_EDIT_NOTE_MOVED_SUCCESS,
                INDEX_SECOND_PERSON.getOneBased(), seededStudent.getName(),
                INDEX_FIRST_PERSON.getOneBased());

        assertCommandSuccess(command, model, expectedMessage,
                createExpectedModel(seededStudent, editedStudent));
        assertEquals(editedOlderNote, model.getFilteredStudentList()
                .get(INDEX_FIRST_PERSON.getZeroBased()).getLessonNotes().get(0));
    }

    @Test
    public void execute_editDate_movesNoteDown() {
        Student seededStudent = seedFirstStudentWith(NEWER_NOTE, OLDER_NOTE);
        LessonNote editedNote = new LessonNote(
                new LessonDate("2026-09-10"),
                NEWER_NOTE.getSubject(), NEWER_NOTE.getNoteText());
        Student editedStudent = new PersonBuilder(seededStudent)
                .withLessonNotes(OLDER_NOTE, editedNote).build();

        EditNoteCommand command = new EditNoteCommand(
                INDEX_FIRST_PERSON, INDEX_FIRST_PERSON,
                Optional.of(editedNote.getDate()),
                Optional.empty(), Optional.empty());

        String expectedMessage = String.format(EditNoteCommand.MESSAGE_EDIT_NOTE_MOVED_SUCCESS,
                INDEX_FIRST_PERSON.getOneBased(), seededStudent.getName(),
                INDEX_SECOND_PERSON.getOneBased());

        assertCommandSuccess(command, model, expectedMessage,
                createExpectedModel(seededStudent, editedStudent));
        assertEquals(editedNote, model.getFilteredStudentList()
                .get(INDEX_FIRST_PERSON.getZeroBased()).getLessonNotes()
                .get(INDEX_SECOND_PERSON.getZeroBased()));
    }

    @Test
    public void execute_editNoteToExistingNote_throwsCommandException() {
        seedFirstStudentWith(NEWER_NOTE, OLDER_NOTE);
        EditNoteCommand command = new EditNoteCommand(
                INDEX_FIRST_PERSON, INDEX_SECOND_PERSON,
                Optional.of(NEWER_NOTE.getDate()),
                Optional.of(NEWER_NOTE.getSubject()),
                Optional.of(NEWER_NOTE.getNoteText()));

        assertCommandFailure(command, model, EditNoteCommand.MESSAGE_DUPLICATE_NOTE);
    }

    @Test
    public void execute_editSubjectNotTaken_throwsCommandException() {
        Student seededStudent = seedFirstStudentWith(NEWER_NOTE);
        EditNoteCommand command = new EditNoteCommand(
                INDEX_FIRST_PERSON, INDEX_FIRST_PERSON,
                Optional.empty(), Optional.of(new Subject("Geography")), Optional.empty());

        assertCommandFailure(command, model, String.format(Messages.MESSAGE_SUBJECT_NOT_TAKEN,
                seededStudent.getName(), "Geography"));
    }

    @Test
    public void execute_editSubjectInDifferentCase_storesProfileSpelling() {
        // The first student takes Math but not Science
        Student seededStudent = seedFirstStudentWith(NEWER_NOTE, OLDER_NOTE);
        LessonNote editedNote = new LessonNote(
                OLDER_NOTE.getDate(), new Subject("Math"), OLDER_NOTE.getNoteText());
        Student editedStudent = new PersonBuilder(seededStudent)
                .withLessonNotes(NEWER_NOTE, editedNote).build();

        EditNoteCommand command = new EditNoteCommand(
                INDEX_FIRST_PERSON, INDEX_SECOND_PERSON,
                Optional.empty(), Optional.of(new Subject("math")), Optional.empty());

        String expectedMessage = String.format(EditNoteCommand.MESSAGE_EDIT_NOTE_SUCCESS,
                INDEX_SECOND_PERSON.getOneBased(), seededStudent.getName());

        assertCommandSuccess(command, model, expectedMessage,
                createExpectedModel(seededStudent, editedStudent));
        assertEquals("Math", model.getFilteredStudentList().get(INDEX_FIRST_PERSON.getZeroBased())
                .getLessonNotes().get(1).getSubject().subjectName);
    }

    @Test
    public void execute_editTextOfNoteForSubjectNoLongerTaken_success() {
        // The first student does not take Science, but the note was recorded for it earlier
        Student seededStudent = seedFirstStudentWith(OLDER_NOTE);
        LessonNote editedNote = new LessonNote(
                OLDER_NOTE.getDate(), OLDER_NOTE.getSubject(), new NoteText("Recapped respiration"));
        Student editedStudent = new PersonBuilder(seededStudent).withLessonNotes(editedNote).build();

        EditNoteCommand command = new EditNoteCommand(
                INDEX_FIRST_PERSON, INDEX_FIRST_PERSON,
                Optional.empty(), Optional.empty(), Optional.of(editedNote.getNoteText()));

        String expectedMessage = String.format(EditNoteCommand.MESSAGE_EDIT_NOTE_SUCCESS,
                INDEX_FIRST_PERSON.getOneBased(), seededStudent.getName());

        assertCommandSuccess(command, model, expectedMessage,
                createExpectedModel(seededStudent, editedStudent));
    }

    @Test
    public void execute_invalidLessonIndex_throwsCommandException() {
        seedFirstStudentWith(NEWER_NOTE);
        EditNoteCommand command = new EditNoteCommand(
                INDEX_FIRST_PERSON, INDEX_SECOND_PERSON,
                Optional.empty(), Optional.empty(),
                Optional.of(new NoteText("Updated note")));

        assertCommandFailure(command, model, EditNoteCommand.MESSAGE_INVALID_LESSON_NOTE_INDEX);
    }

    @Test
    public void execute_invalidStudentIndex_throwsCommandException() {
        Index invalidStudentIndex = Index.fromOneBased(model.getFilteredStudentList().size() + 1);
        EditNoteCommand command = new EditNoteCommand(
                invalidStudentIndex, INDEX_FIRST_PERSON,
                Optional.empty(), Optional.empty(),
                Optional.of(new NoteText("Updated note")));

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_filteredList_preservesFilter() {
        Student seededStudent = seedFirstStudentWith(NEWER_NOTE);
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        LessonNote editedNote = new LessonNote(
                NEWER_NOTE.getDate(), NEWER_NOTE.getSubject(), new NoteText("Updated note"));
        Student editedStudent = new PersonBuilder(seededStudent).withLessonNotes(editedNote).build();

        EditNoteCommand command = new EditNoteCommand(
                INDEX_FIRST_PERSON, INDEX_FIRST_PERSON,
                Optional.empty(), Optional.empty(), Optional.of(editedNote.getNoteText()));

        Model expectedModel = createExpectedModel(seededStudent, editedStudent);
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);

        String expectedMessage = String.format(EditNoteCommand.MESSAGE_EDIT_NOTE_SUCCESS,
                INDEX_FIRST_PERSON.getOneBased(), seededStudent.getName());

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_sameDetails_throwsCommandException() {
        seedFirstStudentWith(NEWER_NOTE, OLDER_NOTE);
        EditNoteCommand command = new EditNoteCommand(
                INDEX_FIRST_PERSON, INDEX_FIRST_PERSON,
                Optional.of(NEWER_NOTE.getDate()),
                Optional.of(NEWER_NOTE.getSubject()),
                Optional.of(NEWER_NOTE.getNoteText()));

        assertCommandFailure(command, model, EditNoteCommand.MESSAGE_NO_CHANGES);
    }

    @Test
    public void execute_sameTextOnly_throwsCommandException() {
        seedFirstStudentWith(NEWER_NOTE, OLDER_NOTE);
        EditNoteCommand command = new EditNoteCommand(
                INDEX_FIRST_PERSON, INDEX_FIRST_PERSON,
                Optional.empty(), Optional.empty(),
                Optional.of(NEWER_NOTE.getNoteText()));

        assertCommandFailure(command, model, EditNoteCommand.MESSAGE_NO_CHANGES);
    }

    @Test
    public void equals() {
        EditNoteCommand standard = new EditNoteCommand(
                INDEX_FIRST_PERSON, INDEX_FIRST_PERSON,
                Optional.of(new LessonDate("2026-10-01")),
                Optional.of(new Subject("Math")),
                Optional.of(new NoteText("Reviewed equations")));

        assertTrue(standard.equals(standard));
        assertTrue(standard.equals(new EditNoteCommand(
                INDEX_FIRST_PERSON, INDEX_FIRST_PERSON,
                Optional.of(new LessonDate("2026-10-01")),
                Optional.of(new Subject("Math")),
                Optional.of(new NoteText("Reviewed equations")))));

        assertFalse(standard.equals(null));
        assertFalse(standard.equals(new ClearCommand()));

        assertFalse(standard.equals(new EditNoteCommand(
                INDEX_SECOND_PERSON, INDEX_FIRST_PERSON,
                Optional.of(new LessonDate("2026-10-01")),
                Optional.of(new Subject("Math")),
                Optional.of(new NoteText("Reviewed equations")))));
        assertFalse(standard.equals(new EditNoteCommand(
                INDEX_FIRST_PERSON, INDEX_SECOND_PERSON,
                Optional.of(new LessonDate("2026-10-01")),
                Optional.of(new Subject("Math")),
                Optional.of(new NoteText("Reviewed equations")))));
        assertFalse(standard.equals(new EditNoteCommand(
                INDEX_FIRST_PERSON, INDEX_FIRST_PERSON,
                Optional.of(new LessonDate("2026-10-02")),
                Optional.of(new Subject("Math")),
                Optional.of(new NoteText("Reviewed equations")))));
        assertFalse(standard.equals(new EditNoteCommand(
                INDEX_FIRST_PERSON, INDEX_FIRST_PERSON,
                Optional.of(new LessonDate("2026-10-01")),
                Optional.of(new Subject("Science")),
                Optional.of(new NoteText("Reviewed equations")))));
        assertFalse(standard.equals(new EditNoteCommand(
                INDEX_FIRST_PERSON, INDEX_FIRST_PERSON,
                Optional.of(new LessonDate("2026-10-01")),
                Optional.of(new Subject("Math")),
                Optional.of(new NoteText("Different note")))));
    }

    @Test
    public void toStringMethod() {
        EditNoteCommand command = new EditNoteCommand(
                INDEX_FIRST_PERSON, INDEX_SECOND_PERSON,
                Optional.of(new LessonDate("2026-10-01")),
                Optional.of(new Subject("Math")),
                Optional.of(new NoteText("Reviewed equations")));

        String expected = EditNoteCommand.class.getCanonicalName()
                + "{studentIndex=" + INDEX_FIRST_PERSON
                + ", lessonIndex=" + INDEX_SECOND_PERSON
                + ", date=Optional[2026-10-01]"
                + ", subject=Optional[Math]"
                + ", noteText=Optional[Reviewed equations]}";

        assertEquals(expected, command.toString());
    }
}
