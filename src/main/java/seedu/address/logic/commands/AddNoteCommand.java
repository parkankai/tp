package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;

import java.util.ArrayList;
import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.lesson.LessonNote;
import seedu.address.model.person.Student;
import seedu.address.model.subject.Subject;

/**
 * Adds a lesson note to an existing student in the address book.
 */
public class AddNoteCommand extends Command {

    public static final String COMMAND_WORD = "addnote";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Adds a lesson note to the student identified by the index number "
            + "used in the displayed student list.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + PREFIX_DATE + "DATE "
            + PREFIX_SUBJECT + "SUBJECT "
            + PREFIX_NOTE + "NOTE_TEXT\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_DATE + "2026-10-01 "
            + PREFIX_SUBJECT + "Math "
            + PREFIX_NOTE + "Revised simultaneous equations.";

    public static final String MESSAGE_ADD_NOTE_SUCCESS = "Added lesson note to %1$s (%2$s, %3$s): %4$s";

    public static final String MESSAGE_DUPLICATE_NOTE =
            "This student already has a lesson note with the same date, subject and text.";

    private final Index index;
    private final LessonNote lessonNote;

    /**
     * @param index of the student in the filtered student list to add the lesson note to
     * @param lessonNote to add to the student
     */
    public AddNoteCommand(Index index, LessonNote lessonNote) {
        requireAllNonNull(index, lessonNote);

        this.index = index;
        this.lessonNote = lessonNote;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Student> lastShownList = model.getFilteredStudentList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Student studentToEdit = lastShownList.get(index.getZeroBased());

        Subject subject = studentToEdit.findSubject(lessonNote.getSubject())
                .orElseThrow(() -> new CommandException(String.format(Messages.MESSAGE_SUBJECT_NOT_TAKEN,
                        studentToEdit.getName(), lessonNote.getSubject())));
        // Store the subject as it is spelled in the student's profile
        LessonNote noteToAdd = new LessonNote(lessonNote.getDate(), subject, lessonNote.getNoteText());

        if (studentToEdit.getLessonNotes().contains(noteToAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_NOTE);
        }

        // getLessonNotes() is unmodifiable, so the new note is appended to a copy
        List<LessonNote> updatedLessonNotes = new ArrayList<>(studentToEdit.getLessonNotes());
        updatedLessonNotes.add(noteToAdd);

        Student editedStudent = new Student(studentToEdit.getName(), studentToEdit.getAcademicLevel(),
                studentToEdit.getSubjects(), studentToEdit.getParentPhone(), studentToEdit.getParentEmail(),
                updatedLessonNotes);

        model.setStudent(studentToEdit, editedStudent);

        return new CommandResult(generateSuccessMessage(editedStudent, noteToAdd));
    }

    /**
     * Generates a command execution success message naming the student and the lesson note added.
     */
    private String generateSuccessMessage(Student editedStudent, LessonNote addedNote) {
        return String.format(MESSAGE_ADD_NOTE_SUCCESS, editedStudent.getName(), addedNote.getDate(),
                addedNote.getSubject(), addedNote.getNoteText());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddNoteCommand otherAddNoteCommand)) {
            return false;
        }

        return index.equals(otherAddNoteCommand.index)
                && lessonNote.equals(otherAddNoteCommand.lessonNote);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("lessonNote", lessonNote)
                .toString();
    }
}
