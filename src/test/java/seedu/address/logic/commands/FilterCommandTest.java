package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.AcademicLevel;
import seedu.address.model.person.Student;
import seedu.address.model.person.StudentMatchesFilterPredicate;
import seedu.address.model.subject.Subject;
import seedu.address.testutil.PersonBuilder;

public class FilterCommandTest {

    private final Model model =
            new ModelManager(
                    getTypicalAddressBook(), new UserPrefs());

    private final Model expectedModel =
            new ModelManager(
                    getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_matchingStudents_updatesFilteredList() {
        StudentMatchesFilterPredicate predicate =
                new StudentMatchesFilterPredicate(
                        Optional.empty(),
                        Set.of(new Subject("Math")));

        FilterCommand command = new FilterCommand(predicate);
        expectedModel.updateFilteredStudentList(predicate);

        String expectedMessage = String.format(
                MESSAGE_PERSONS_LISTED_OVERVIEW,
                expectedModel.getFilteredStudentList().size());

        assertCommandSuccess(
                command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noMatches_returnsEmptyList() {
        StudentMatchesFilterPredicate predicate =
                new StudentMatchesFilterPredicate(
                        Optional.of(new AcademicLevel("JC 2")),
                        Set.of(new Subject("Additional Mathematics")));

        FilterCommand command = new FilterCommand(predicate);
        expectedModel.updateFilteredStudentList(predicate);

        assertCommandSuccess(
                command,
                model,
                FilterCommand.MESSAGE_NO_STUDENTS_FOUND,
                expectedModel);

        assertTrue(model.getFilteredStudentList().isEmpty());
    }

    @Test
    public void execute_repeatedSubjectSpaces_displaysMatchingStudents() {
        Model testModel = new ModelManager();
        Student matchingStudent = new PersonBuilder().withName("Alice Tan")
                .withSubjects("Additional  Mathematics", "Science").build();
        Student otherStudent = new PersonBuilder().withName("Ben Tan").withSubjects("Science").build();
        testModel.addStudent(matchingStudent);
        testModel.addStudent(otherStudent);
        StudentMatchesFilterPredicate predicate = new StudentMatchesFilterPredicate(
                Optional.empty(), Set.of(new Subject("additional mathematics")));

        CommandResult result = new FilterCommand(predicate).execute(testModel);

        assertEquals(String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1), result.getFeedbackToUser());
        assertEquals(List.of(matchingStudent), testModel.getFilteredStudentList());
        assertEquals(List.of(matchingStudent, otherStudent), testModel.getAddressBook().getStudentList());
        assertTrue(matchingStudent.getSubjects().stream()
                .anyMatch(subject -> subject.subjectName.equals("Additional  Mathematics")));
    }

    @Test
    public void equals() {
        StudentMatchesFilterPredicate firstPredicate =
                new StudentMatchesFilterPredicate(
                        Optional.empty(),
                        Set.of(new Subject("Math")));

        StudentMatchesFilterPredicate secondPredicate =
                new StudentMatchesFilterPredicate(
                        Optional.of(new AcademicLevel("Sec 2")),
                        Set.of());

        FilterCommand firstCommand =
                new FilterCommand(firstPredicate);
        FilterCommand firstCommandCopy =
                new FilterCommand(firstPredicate);
        FilterCommand secondCommand =
                new FilterCommand(secondPredicate);

        assertTrue(firstCommand.equals(firstCommand));
        assertTrue(firstCommand.equals(firstCommandCopy));
        assertFalse(firstCommand.equals(secondCommand));
        assertFalse(firstCommand.equals(null));
        assertFalse(firstCommand.equals(1));
    }

    @Test
    public void toStringMethod() {
        StudentMatchesFilterPredicate predicate =
                new StudentMatchesFilterPredicate(
                        Optional.empty(),
                        Set.of(new Subject("Math")));

        FilterCommand command = new FilterCommand(predicate);

        String expected =
                FilterCommand.class.getCanonicalName()
                        + "{predicate=" + predicate + "}";

        assertEquals(expected, command.toString());
    }
}
