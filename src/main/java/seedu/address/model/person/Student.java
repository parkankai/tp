package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.lesson.LessonNote;
import seedu.address.model.subject.Subject;

/** Represents a student managed by the tutor. */
public class Student {

    public static final String MESSAGE_CONSTRAINTS = "A student must have at least one subject.";

    private final Name name;
    private final AcademicLevel academicLevel;
    private final Set<Subject> subjects = new HashSet<>();
    private final Phone parentPhone;
    private final Email parentEmail;
    private final List<LessonNote> lessonNotes;

    /**
     * Creates a student with the given name, academic level, subjects, and parent contact details,
     * and no lesson notes.
     */
    public Student(Name name, AcademicLevel academicLevel, Set<Subject> subjects,
            Phone parentPhone, Email parentEmail) {
        this(name, academicLevel, subjects, parentPhone, parentEmail, List.of());
    }

    /**
     * Creates a student with the given name, academic level, subjects, parent contact details and lesson notes.
     * The lesson notes are kept sorted from the most recent date to the earliest.
     */
    public Student(Name name, AcademicLevel academicLevel, Set<Subject> subjects,
            Phone parentPhone, Email parentEmail, List<LessonNote> lessonNotes) {
        requireAllNonNull(name, academicLevel, subjects, parentPhone, parentEmail, lessonNotes);
        checkArgument(!subjects.isEmpty(), MESSAGE_CONSTRAINTS);
        this.name = name;
        this.academicLevel = academicLevel;
        this.subjects.addAll(subjects);
        this.parentPhone = parentPhone;
        this.parentEmail = parentEmail;

        List<LessonNote> sortedLessonNotes = new ArrayList<>(lessonNotes);
        // List#sort is stable, so notes with the same date keep their given order
        sortedLessonNotes.sort(Comparator.comparing(LessonNote::getDate).reversed());
        this.lessonNotes = Collections.unmodifiableList(sortedLessonNotes);
    }

    public Name getName() {
        return name;
    }

    public AcademicLevel getAcademicLevel() {
        return academicLevel;
    }

    public Set<Subject> getSubjects() {
        return Collections.unmodifiableSet(subjects);
    }

    public Phone getParentPhone() {
        return parentPhone;
    }

    public Email getParentEmail() {
        return parentEmail;
    }

    /**
     * Returns an immutable list of lesson notes, sorted from the most recent date to the earliest,
     * which throws {@code UnsupportedOperationException} if modification is attempted.
     */
    public List<LessonNote> getLessonNotes() {
        return lessonNotes;
    }

    /**
     * Returns the subject in this student's profile that matches {@code subject}, ignoring case,
     * or an empty {@code Optional} if the student does not take it.
     */
    public Optional<Subject> findSubject(Subject subject) {
        requireNonNull(subject);
        return subjects.stream().filter(subject::equals).findFirst();
    }

    /** Returns true if both students have the same name and same parent phone number */
    public boolean isSameStudent(Student otherStudent) {
        return otherStudent != null
                && name.equals(otherStudent.name)
                && parentPhone.equals(otherStudent.parentPhone);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Student otherStudent)) {
            return false;
        }
        return name.equals(otherStudent.name)
                && academicLevel.equals(otherStudent.academicLevel)
                && subjects.equals(otherStudent.subjects)
                && parentPhone.equals(otherStudent.parentPhone)
                && parentEmail.equals(otherStudent.parentEmail)
                && lessonNotes.equals(otherStudent.lessonNotes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, academicLevel, subjects, parentPhone, parentEmail, lessonNotes);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("academicLevel", academicLevel)
                .add("subjects", subjects)
                .add("parentPhone", parentPhone)
                .add("parentEmail", parentEmail)
                .add("lessonNotes", lessonNotes)
                .toString();
    }
}
