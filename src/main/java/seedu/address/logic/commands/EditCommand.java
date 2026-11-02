package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_STUDENTS;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.AcademicLevel;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Student;
import seedu.address.model.subject.Subject;

/** Edits the details of an existing student. */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits a student. Parameters: INDEX "
            + "[n/NAME] [lvl/LEVEL] [s/SUBJECT]... [pn/PARENT_PHONE] [pe/PARENT_EMAIL]";
    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited student: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON =
            "Another student (%1$s) already has this name and parent phone number.";

    public static final String MESSAGE_SUBJECT_REQUIRED = "A student must have at least one nonblank subject.";

    private static final Logger logger = LogsCenter.getLogger(EditCommand.class);

    private final Index index;
    private final EditPersonDescriptor editStudentDescriptor;
    private final String duplicateSubjectWarning;

    /**
     * Creates an edit command for the student at the given index.
     *
     * @param index index of the student to edit.
     * @param editStudentDescriptor fields to update.
     */
    public EditCommand(Index index, EditPersonDescriptor editStudentDescriptor) {
        this(index, editStudentDescriptor, "");
    }

    /**
     * Creates an edit command with an optional warning about duplicate subjects in the input.
     */
    public EditCommand(Index index, EditPersonDescriptor editStudentDescriptor, String duplicateSubjectWarning) {
        requireNonNull(index);
        requireNonNull(editStudentDescriptor);
        requireNonNull(duplicateSubjectWarning);
        this.index = index;
        this.editStudentDescriptor = new EditPersonDescriptor(editStudentDescriptor);
        this.duplicateSubjectWarning = duplicateSubjectWarning;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Student> lastShownList = model.getFilteredStudentList();

        if (index.getZeroBased() >= lastShownList.size()) {
            logger.fine("Student edit rejected: displayed index is out of range.");
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Student studentToEdit = lastShownList.get(index.getZeroBased());
        if (editStudentDescriptor.getSubjects().orElse(studentToEdit.getSubjects()).isEmpty()) {
            logger.fine("Student edit rejected: no subjects would remain.");
            throw new CommandException(MESSAGE_SUBJECT_REQUIRED);
        }
        Student editedStudent = createEditedStudent(studentToEdit, editStudentDescriptor);
        assert editedStudent.getLessonNotes().equals(studentToEdit.getLessonNotes())
                : "Editing a student profile must preserve lesson notes";

        if (!studentToEdit.isSameStudent(editedStudent)) {
            Optional<Student> clashingStudent = model.getAddressBook().getStudentList().stream()
                    .filter(editedStudent::isSameStudent)
                    .findFirst();
            if (clashingStudent.isPresent()) {
                logger.fine("Student edit rejected: duplicate student.");
                throw new CommandException(String.format(MESSAGE_DUPLICATE_PERSON,
                        clashingStudent.get().getName()));
            }
        }

        model.setStudent(studentToEdit, editedStudent);
        model.updateFilteredStudentList(PREDICATE_SHOW_ALL_STUDENTS);
        logger.fine("Student profile updated in the model.");
        String successMessage = String.format(MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedStudent));
        return new CommandResult(duplicateSubjectWarning.isEmpty()
                ? successMessage : successMessage + "\n" + duplicateSubjectWarning);
    }

    /**
     * Creates and returns a {@code Student} with the details of {@code studentToEdit}
     * edited with {@code editStudentDescriptor}.
     */
    private static Student createEditedStudent(Student studentToEdit, EditPersonDescriptor descriptor) {
        Name updatedName = descriptor.getName().orElse(studentToEdit.getName());
        AcademicLevel updatedLevel = descriptor.getAcademicLevel().orElse(studentToEdit.getAcademicLevel());
        Set<Subject> updatedSubjects = descriptor.getSubjects().orElse(studentToEdit.getSubjects());
        Phone updatedPhone = descriptor.getParentPhone().orElse(studentToEdit.getParentPhone());
        Email updatedEmail = descriptor.getParentEmail().orElse(studentToEdit.getParentEmail());
        return new Student(updatedName, updatedLevel, updatedSubjects, updatedPhone, updatedEmail,
                studentToEdit.getLessonNotes());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof EditCommand otherEditCommand)) {
            return false;
        }
        return index.equals(otherEditCommand.index)
                && editStudentDescriptor.equals(otherEditCommand.editStudentDescriptor)
                && duplicateSubjectWarning.equals(otherEditCommand.duplicateSubjectWarning);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editStudentDescriptor", editStudentDescriptor)
                .toString();
    }

    /** Stores the fields to be updated for a student. */
    public static class EditPersonDescriptor {
        private Name name;
        private AcademicLevel academicLevel;
        private Set<Subject> subjects;
        private Phone parentPhone;
        private Email parentEmail;

        public EditPersonDescriptor() {}

        /** Copies the fields from an existing descriptor. */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setAcademicLevel(toCopy.academicLevel);
            setSubjects(toCopy.subjects);
            setParentPhone(toCopy.parentPhone);
            setParentEmail(toCopy.parentEmail);
        }

        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, academicLevel, subjects, parentPhone, parentEmail);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setAcademicLevel(AcademicLevel academicLevel) {
            this.academicLevel = academicLevel;
        }

        public Optional<AcademicLevel> getAcademicLevel() {
            return Optional.ofNullable(academicLevel);
        }

        public void setSubjects(Set<Subject> subjects) {
            this.subjects = subjects == null ? null : new HashSet<>(subjects);
        }

        public Optional<Set<Subject>> getSubjects() {
            return subjects == null ? Optional.empty() : Optional.of(Collections.unmodifiableSet(subjects));
        }

        public void setParentPhone(Phone parentPhone) {
            this.parentPhone = parentPhone;
        }

        public Optional<Phone> getParentPhone() {
            return Optional.ofNullable(parentPhone);
        }

        public void setParentEmail(Email parentEmail) {
            this.parentEmail = parentEmail;
        }

        public Optional<Email> getParentEmail() {
            return Optional.ofNullable(parentEmail);
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if (!(other instanceof EditPersonDescriptor otherDescriptor)) {
                return false;
            }
            return Objects.equals(name, otherDescriptor.name)
                    && Objects.equals(academicLevel, otherDescriptor.academicLevel)
                    && Objects.equals(subjects, otherDescriptor.subjects)
                    && Objects.equals(parentPhone, otherDescriptor.parentPhone)
                    && Objects.equals(parentEmail, otherDescriptor.parentEmail);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("academicLevel", academicLevel)
                    .add("subjects", subjects)
                    .add("parentPhone", parentPhone)
                    .add("parentEmail", parentEmail)
                    .toString();
        }
    }
}
