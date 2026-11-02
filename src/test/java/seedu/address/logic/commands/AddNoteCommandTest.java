package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

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

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code AddNoteCommand}.
 */
public class AddNoteCommandTest {

    private static final LessonNote NOTE_MATH = new LessonNote(new LessonDate("2026-10-01"),
            new Subject("Math"), new NoteText("Revised simultaneous equations"));
    private static final LessonNote NOTE_SCIENCE = new LessonNote(new LessonDate("2026-09-20"),
            new Subject("Science"), new NoteText("Covered photosynthesis"));

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_addNoteUnfilteredList_success() {
        Student firstStudent = model.getFilteredStudentList().get(INDEX_FIRST_PERSON.getZeroBased());
        Student editedStudent = new PersonBuilder(firstStudent).withLessonNotes(NOTE_MATH).build();

        AddNoteCommand addNoteCommand = new AddNoteCommand(INDEX_FIRST_PERSON, NOTE_MATH);

        String expectedMessage = String.format(AddNoteCommand.MESSAGE_ADD_NOTE_SUCCESS,
                editedStudent.getName(), NOTE_MATH.getDate(), NOTE_MATH.getSubject(), NOTE_MATH.getNoteText());

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setStudent(firstStudent, editedStudent);

        assertCommandSuccess(addNoteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_addNoteToStudentWithExistingNote_appendsNote() {
        Student firstStudent = model.getFilteredStudentList().get(INDEX_FIRST_PERSON.getZeroBased());
        Student studentWithNote = new PersonBuilder(firstStudent).withLessonNotes(NOTE_SCIENCE).build();
        model.setStudent(firstStudent, studentWithNote);

        Student editedStudent = new PersonBuilder(firstStudent)
                .withLessonNotes(NOTE_SCIENCE, NOTE_MATH).build();

        AddNoteCommand addNoteCommand = new AddNoteCommand(INDEX_FIRST_PERSON, NOTE_MATH);

        String expectedMessage = String.format(AddNoteCommand.MESSAGE_ADD_NOTE_SUCCESS,
                editedStudent.getName(), NOTE_MATH.getDate(), NOTE_MATH.getSubject(), NOTE_MATH.getNoteText());

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setStudent(studentWithNote, editedStudent);

        assertCommandSuccess(addNoteCommand, model, expectedMessage, expectedModel);

        // both notes are kept, sorted from the most recent date to the earliest
        assertEquals(2, model.getFilteredStudentList().get(INDEX_FIRST_PERSON.getZeroBased())
                .getLessonNotes().size());
    }

    @Test
    public void execute_duplicateNote_throwsCommandException() {
        Student firstStudent = model.getFilteredStudentList().get(INDEX_FIRST_PERSON.getZeroBased());
        model.setStudent(firstStudent, new PersonBuilder(firstStudent).withLessonNotes(NOTE_MATH).build());

        AddNoteCommand addNoteCommand = new AddNoteCommand(INDEX_FIRST_PERSON, NOTE_MATH);

        assertCommandFailure(addNoteCommand, model, AddNoteCommand.MESSAGE_DUPLICATE_NOTE);
    }

    @Test
    public void execute_subjectNotTaken_throwsCommandException() {
        Student firstStudent = model.getFilteredStudentList().get(INDEX_FIRST_PERSON.getZeroBased());
        LessonNote geographyNote = new LessonNote(new LessonDate("2026-10-01"),
                new Subject("Geography"), new NoteText("Covered maps"));

        AddNoteCommand addNoteCommand = new AddNoteCommand(INDEX_FIRST_PERSON, geographyNote);

        assertCommandFailure(addNoteCommand, model, String.format(Messages.MESSAGE_SUBJECT_NOT_TAKEN,
                firstStudent.getName(), geographyNote.getSubject()));
    }

    @Test
    public void execute_subjectInDifferentCase_storesProfileSpelling() {
        Student firstStudent = model.getFilteredStudentList().get(INDEX_FIRST_PERSON.getZeroBased());
        LessonNote lowerCaseNote = new LessonNote(NOTE_MATH.getDate(), new Subject("math"), NOTE_MATH.getNoteText());
        Student editedStudent = new PersonBuilder(firstStudent).withLessonNotes(NOTE_MATH).build();

        AddNoteCommand addNoteCommand = new AddNoteCommand(INDEX_FIRST_PERSON, lowerCaseNote);

        String expectedMessage = String.format(AddNoteCommand.MESSAGE_ADD_NOTE_SUCCESS,
                editedStudent.getName(), NOTE_MATH.getDate(), "Math", NOTE_MATH.getNoteText());

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setStudent(firstStudent, editedStudent);

        assertCommandSuccess(addNoteCommand, model, expectedMessage, expectedModel);
        assertEquals("Math", model.getFilteredStudentList().get(INDEX_FIRST_PERSON.getZeroBased())
                .getLessonNotes().get(0).getSubject().subjectName);
    }

    @Test
    public void execute_filteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Student firstStudent = model.getFilteredStudentList().get(INDEX_FIRST_PERSON.getZeroBased());
        Student editedStudent = new PersonBuilder(firstStudent).withLessonNotes(NOTE_MATH).build();

        AddNoteCommand addNoteCommand = new AddNoteCommand(INDEX_FIRST_PERSON, NOTE_MATH);

        String expectedMessage = String.format(AddNoteCommand.MESSAGE_ADD_NOTE_SUCCESS,
                editedStudent.getName(), NOTE_MATH.getDate(), NOTE_MATH.getSubject(), NOTE_MATH.getNoteText());

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setStudent(firstStudent, editedStudent);
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);

        assertCommandSuccess(addNoteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredStudentList().size() + 1);
        AddNoteCommand addNoteCommand = new AddNoteCommand(outOfBoundIndex, NOTE_MATH);

        assertCommandFailure(addNoteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /**
     * Adds a lesson note to a student in a filtered list where the index is larger than the size of
     * the filtered list, but smaller than the size of the address book.
     */
    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getStudentList().size());

        AddNoteCommand addNoteCommand = new AddNoteCommand(outOfBoundIndex, NOTE_MATH);

        assertCommandFailure(addNoteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final AddNoteCommand standardCommand = new AddNoteCommand(INDEX_FIRST_PERSON, NOTE_MATH);

        // same values -> returns true
        assertTrue(standardCommand.equals(new AddNoteCommand(INDEX_FIRST_PERSON, NOTE_MATH)));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new AddNoteCommand(INDEX_SECOND_PERSON, NOTE_MATH)));

        // different lesson note -> returns false
        assertFalse(standardCommand.equals(new AddNoteCommand(INDEX_FIRST_PERSON, NOTE_SCIENCE)));
    }

    @Test
    public void toStringMethod() {
        AddNoteCommand addNoteCommand = new AddNoteCommand(INDEX_FIRST_PERSON, NOTE_MATH);
        String expected = AddNoteCommand.class.getCanonicalName()
                + "{index=" + INDEX_FIRST_PERSON + ", lessonNote=" + NOTE_MATH + "}";
        assertEquals(expected, addNoteCommand.toString());
    }
}
