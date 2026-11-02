package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.subject.Subject;

/**
 * Tests whether a {@code Student} matches the supplied academic level and
 * subjects.
 *
 * If an academic level is present, the student's level must equal it.
 * The student must also take every subject in {@code requiredSubjects}.
 */
public class StudentMatchesFilterPredicate implements Predicate<Student> {

    private final Optional<AcademicLevel> academicLevel;
    private final Set<Subject> requiredSubjects;

    /**
     * Creates a predicate using the supplied filter criteria.
     */
    public StudentMatchesFilterPredicate(
            Optional<AcademicLevel> academicLevel,
            Set<Subject> requiredSubjects) {
        requireNonNull(academicLevel);
        requireNonNull(requiredSubjects);

        this.academicLevel = academicLevel;
        this.requiredSubjects = Set.copyOf(requiredSubjects);
    }

    @Override
    public boolean test(Student student) {
        requireNonNull(student);

        boolean matchesLevel = academicLevel
                .map(level -> level.equals(student.getAcademicLevel()))
                .orElse(true);

        boolean matchesSubjects = requiredSubjects.stream()
                .allMatch(requiredSubject -> student.getSubjects().stream()
                        .anyMatch(studentSubject -> normalizeSubjectName(studentSubject)
                                .equalsIgnoreCase(normalizeSubjectName(requiredSubject))));

        return matchesLevel && matchesSubjects;
    }

    /**
     * Returns the subject name with consecutive spaces replaced by a single space.
     */
    private static String normalizeSubjectName(Subject subject) {
        return subject.subjectName.replaceAll(" +", " ");
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof StudentMatchesFilterPredicate otherPredicate)) {
            return false;
        }

        return academicLevel.equals(otherPredicate.academicLevel)
                && requiredSubjects.equals(otherPredicate.requiredSubjects);
    }

    @Override
    public int hashCode() {
        return Objects.hash(academicLevel, requiredSubjects);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("academicLevel", academicLevel)
                .add("requiredSubjects", requiredSubjects)
                .toString();
    }
}
