package seedu.address.logic.parser;

import static seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_LEVEL_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PARENT_EMAIL_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PARENT_PHONE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_SUBJECT_DESC;
import static seedu.address.logic.commands.CommandTestUtil.LEVEL_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.PARENT_EMAIL_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.PARENT_PHONE_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_WHITESPACE;
import static seedu.address.logic.commands.CommandTestUtil.SUBJECT_DESC_MATH;
import static seedu.address.logic.commands.CommandTestUtil.SUBJECT_DESC_SCIENCE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PARENT_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PARENT_PHONE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddCommand;
import seedu.address.model.person.AcademicLevel;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Student;
import seedu.address.model.subject.Subject;
import seedu.address.testutil.PersonBuilder;

public class AddCommandParserTest {
    private final AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        Student expectedStudent = new PersonBuilder().withName("Jack")
                .withAcademicLevel("Sec 2").withSubjects("Math", "Science")
                .withPhone("91234567").withEmail("test@example.com").build();

        String userInput = PREAMBLE_WHITESPACE + NAME_DESC_BOB.replace("Bob Choo", "Jack")
                + LEVEL_DESC_BOB.replace("Pri 5", "Sec 2") + SUBJECT_DESC_MATH + SUBJECT_DESC_SCIENCE
                + PARENT_PHONE_DESC_BOB.replace("92222222", "91234567")
                + PARENT_EMAIL_DESC_BOB.replace("bob@example.com", "test@example.com");

        assertParseSuccess(parser, userInput, new AddCommand(expectedStudent));
    }

    @Test
    public void parse_subjectWithSpaces_success() {
        Student expectedStudent = new PersonBuilder().withSubjects("Computer Science").build();
        String userInput = NAME_DESC_BOB + LEVEL_DESC_BOB + " s/Computer Science"
                + PARENT_PHONE_DESC_BOB + PARENT_EMAIL_DESC_BOB;

        assertParseSuccess(parser, userInput, new AddCommand(
                new PersonBuilder(expectedStudent).withName("Bob Choo")
                        .withAcademicLevel("Pri 5").withPhone("92222222")
                        .withEmail("bob@example.com").build()));
    }

    @Test
    public void parse_nameWithSonOf_success() {
        Student expectedStudent = new PersonBuilder().withName("Ravi s/o Kumar")
                .withAcademicLevel("Pri 5").withSubjects("Math").withPhone("92222222")
                .withEmail("bob@example.com").build();
        String userInput = " n/Ravi s/o Kumar" + LEVEL_DESC_BOB + SUBJECT_DESC_MATH
                + PARENT_PHONE_DESC_BOB + PARENT_EMAIL_DESC_BOB;

        assertParseSuccess(parser, userInput, new AddCommand(expectedStudent));
    }

    @Test
    public void duplicateSubjects_merge_success() {
        Student expectedStudent = new PersonBuilder().withSubjects("Math").build();
        String userInput = NAME_DESC_BOB + LEVEL_DESC_BOB + " s/Math s/math"
                + PARENT_PHONE_DESC_BOB + PARENT_EMAIL_DESC_BOB;

        assertParseSuccess(parser, userInput, new AddCommand(
                new PersonBuilder(expectedStudent).withName("Bob Choo")
                        .withAcademicLevel("Pri 5").withPhone("92222222")
                        .withEmail("bob@example.com").build(),
                Messages.getWarningMessageForDuplicateSubjects(List.of("math"))));
    }

    @Test
    public void parse_missingRequiredField_failure() {
        String validInput = NAME_DESC_BOB + LEVEL_DESC_BOB + SUBJECT_DESC_MATH
                + PARENT_PHONE_DESC_BOB + PARENT_EMAIL_DESC_BOB;

        assertParseFailure(parser, validInput.replace(NAME_DESC_BOB, ""), AddCommand.MESSAGE_WRONG_FORMAT);
        assertParseFailure(parser, validInput.replace(LEVEL_DESC_BOB, ""), AddCommand.MESSAGE_WRONG_FORMAT);
        assertParseFailure(parser, validInput.replace(SUBJECT_DESC_MATH, ""), AddCommand.MESSAGE_WRONG_FORMAT);
        assertParseFailure(parser, validInput.replace(PARENT_PHONE_DESC_BOB, ""), AddCommand.MESSAGE_WRONG_FORMAT);
        assertParseFailure(parser, validInput.replace(PARENT_EMAIL_DESC_BOB, ""), AddCommand.MESSAGE_WRONG_FORMAT);
    }

    @Test
    public void parse_invalidField_failure() {
        String validInput = NAME_DESC_BOB + LEVEL_DESC_BOB + SUBJECT_DESC_MATH
                + PARENT_PHONE_DESC_BOB + PARENT_EMAIL_DESC_BOB;

        assertParseFailure(parser, validInput.replace(NAME_DESC_BOB, INVALID_NAME_DESC),
                Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, validInput.replace(LEVEL_DESC_BOB, INVALID_LEVEL_DESC),
                AcademicLevel.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, validInput.replace(SUBJECT_DESC_MATH, INVALID_SUBJECT_DESC),
                Subject.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, validInput.replace(PARENT_PHONE_DESC_BOB, INVALID_PARENT_PHONE_DESC),
                Phone.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, validInput.replace(PARENT_EMAIL_DESC_BOB, INVALID_PARENT_EMAIL_DESC),
                Email.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_nameExceedsMaximumLength_failure() {
        String validInput = NAME_DESC_BOB + LEVEL_DESC_BOB + SUBJECT_DESC_MATH
                + PARENT_PHONE_DESC_BOB + PARENT_EMAIL_DESC_BOB;
        String longName = "a".repeat(Name.MAX_LENGTH + 1);

        assertParseFailure(parser, validInput.replace("n/Bob Choo", "n/" + longName),
                Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidLevel_reportsLevelConstraint() {
        String userInput = " n/Jack lvl/Pri 1 2 s/Physics pn/91234567 pe/test@gmail";

        assertParseFailure(parser, userInput, AcademicLevel.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_duplicateSingleValueField_failure() {
        String validInput = NAME_DESC_BOB + LEVEL_DESC_BOB + SUBJECT_DESC_MATH
                + PARENT_PHONE_DESC_BOB + PARENT_EMAIL_DESC_BOB;

        assertParseFailure(parser, validInput + NAME_DESC_BOB,
                getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
        assertParseFailure(parser, validInput + LEVEL_DESC_BOB,
                getErrorMessageForDuplicatePrefixes(PREFIX_LEVEL));
        assertParseFailure(parser, validInput + PARENT_PHONE_DESC_BOB,
                getErrorMessageForDuplicatePrefixes(PREFIX_PARENT_PHONE));
        assertParseFailure(parser, validInput + PARENT_EMAIL_DESC_BOB,
                getErrorMessageForDuplicatePrefixes(PREFIX_PARENT_EMAIL));
    }

    @Test
    public void parse_nonEmptyPreamble_failure() {
        String validInput = PREAMBLE_NON_EMPTY + NAME_DESC_BOB + LEVEL_DESC_BOB + SUBJECT_DESC_MATH
                + PARENT_PHONE_DESC_BOB + PARENT_EMAIL_DESC_BOB;
        assertParseFailure(parser, validInput, AddCommand.MESSAGE_WRONG_FORMAT);
    }
}
