package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.subject.Subject;
import seedu.address.testutil.PersonBuilder;

public class StudentMatchesFilterPredicateTest {

    @Test
    public void test_noCriteria_returnsTrue() {
        StudentMatchesFilterPredicate predicate =
                new StudentMatchesFilterPredicate(
                        Optional.empty(), Set.of());

        assertTrue(predicate.test(new PersonBuilder().build()));
    }

    @Test
    public void test_matchingLevel_returnsTrue() {
        Student student = new PersonBuilder()
                .withAcademicLevel("Sec 2")
                .build();

        StudentMatchesFilterPredicate predicate =
                new StudentMatchesFilterPredicate(
                        Optional.of(new AcademicLevel("Sec 2")),
                        Set.of());

        assertTrue(predicate.test(student));
    }

    @Test
    public void test_differentLevel_returnsFalse() {
        Student student = new PersonBuilder()
                .withAcademicLevel("Sec 2")
                .build();

        StudentMatchesFilterPredicate predicate =
                new StudentMatchesFilterPredicate(
                        Optional.of(new AcademicLevel("Sec 3")),
                        Set.of());

        assertFalse(predicate.test(student));
    }

    @Test
    public void test_hasAllRequiredSubjects_returnsTrue() {
        Student student = new PersonBuilder()
                .withSubjects("Math", "Science", "English")
                .build();

        StudentMatchesFilterPredicate predicate =
                new StudentMatchesFilterPredicate(
                        Optional.empty(),
                        Set.of(
                                new Subject("Math"),
                                new Subject("Science")));

        assertTrue(predicate.test(student));
    }

    @Test
    public void test_missingRequiredSubject_returnsFalse() {
        Student student = new PersonBuilder()
                .withSubjects("Math")
                .build();

        StudentMatchesFilterPredicate predicate =
                new StudentMatchesFilterPredicate(
                        Optional.empty(),
                        Set.of(
                                new Subject("Math"),
                                new Subject("Science")));

        assertFalse(predicate.test(student));
    }

    @Test
    public void test_levelAndSubjectsMustBothMatch() {
        Student student = new PersonBuilder()
                .withAcademicLevel("Sec 2")
                .withSubjects("Math", "Science")
                .build();

        StudentMatchesFilterPredicate matchingPredicate =
                new StudentMatchesFilterPredicate(
                        Optional.of(new AcademicLevel("Sec 2")),
                        Set.of(new Subject("Math")));

        StudentMatchesFilterPredicate wrongLevelPredicate =
                new StudentMatchesFilterPredicate(
                        Optional.of(new AcademicLevel("Sec 3")),
                        Set.of(new Subject("Math")));

        StudentMatchesFilterPredicate wrongSubjectPredicate =
                new StudentMatchesFilterPredicate(
                        Optional.of(new AcademicLevel("Sec 2")),
                        Set.of(new Subject("English")));

        assertTrue(matchingPredicate.test(student));
        assertFalse(wrongLevelPredicate.test(student));
        assertFalse(wrongSubjectPredicate.test(student));
    }

    @Test
    public void test_matchingIsCaseInsensitive() {
        Student student = new PersonBuilder()
                .withAcademicLevel("Sec 2")
                .withSubjects("Mathematics")
                .build();

        StudentMatchesFilterPredicate predicate =
                new StudentMatchesFilterPredicate(
                        Optional.of(new AcademicLevel("sec 2")),
                        Set.of(new Subject("mathematics")));

        assertTrue(predicate.test(student));
    }
    @Test
    public void test_repeatedSubjectSpaces_returnsTrue() {
        for (String storedName : Set.of("Additional Mathematics", "Additional  Mathematics")) {
            Student student = new PersonBuilder().withSubjects(storedName, "Science").build();
            for (String requiredName : Set.of("additional mathematics", "ADDITIONAL   MATHEMATICS")) {
                StudentMatchesFilterPredicate predicate = new StudentMatchesFilterPredicate(
                        Optional.empty(), Set.of(new Subject(requiredName)));

                assertTrue(predicate.test(student));
            }
        }
    }

    @Test
    public void test_repeatedSpacesWithMissingSubject_returnsFalse() {
        Student student = new PersonBuilder().withSubjects("Additional  Mathematics").build();
        StudentMatchesFilterPredicate predicate = new StudentMatchesFilterPredicate(
                Optional.empty(), Set.of(new Subject("Additional Mathematics"), new Subject("Science")));

        assertFalse(predicate.test(student));
    }

    @Test
    public void test_repeatedSpacesWithWrongLevel_returnsFalse() {
        Student student = new PersonBuilder().withAcademicLevel("Sec 2")
                .withSubjects("Additional  Mathematics").build();
        StudentMatchesFilterPredicate predicate = new StudentMatchesFilterPredicate(
                Optional.of(new AcademicLevel("Sec 3")), Set.of(new Subject("Additional Mathematics")));

        assertFalse(predicate.test(student));
    }

    @Test
    public void test_missingWordSpace_returnsFalse() {
        Student student = new PersonBuilder().withSubjects("Additional Mathematics").build();
        StudentMatchesFilterPredicate predicate = new StudentMatchesFilterPredicate(
                Optional.empty(), Set.of(new Subject("AdditionalMathematics")));

        assertFalse(predicate.test(student));
    }
}
