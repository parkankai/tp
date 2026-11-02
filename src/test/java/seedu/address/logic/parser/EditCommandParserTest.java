package seedu.address.logic.parser;

import static seedu.address.logic.commands.CommandTestUtil.LEVEL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PARENT_EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PARENT_PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.SUBJECT_DESC_MATH;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.person.AcademicLevel;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.testutil.EditPersonDescriptorBuilder;

public class EditCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format("Invalid command format!\n%1$s", EditCommand.MESSAGE_USAGE);
    private final EditCommandParser parser = new EditCommandParser();

    @Test
    public void parse_missingParts_failure() {
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1", EditCommand.MESSAGE_NOT_EDITED);
        assertParseFailure(parser, "0" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 invalid preamble", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        String userInput = INDEX_FIRST_PERSON.getOneBased() + NAME_DESC_AMY + LEVEL_DESC_AMY
                + SUBJECT_DESC_MATH + PARENT_PHONE_DESC_AMY + PARENT_EMAIL_DESC_AMY;
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName("Amy Bee")
                .withAcademicLevel("Sec 2").withSubjects("Math").withPhone("91111111")
                .withEmail("amy@example.com").build();

        assertParseSuccess(parser, userInput, new EditCommand(INDEX_FIRST_PERSON, descriptor));
    }

    @Test
    public void parse_eachFieldSpecified_success() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName("Amy Bee").build();
        assertParseSuccess(parser, "1" + NAME_DESC_AMY, new EditCommand(INDEX_FIRST_PERSON, descriptor));

        descriptor = new EditPersonDescriptorBuilder().withAcademicLevel("Sec 2").build();
        assertParseSuccess(parser, "1" + LEVEL_DESC_AMY, new EditCommand(INDEX_FIRST_PERSON, descriptor));

        descriptor = new EditPersonDescriptorBuilder().withSubjects("Math").build();
        assertParseSuccess(parser, "1" + SUBJECT_DESC_MATH, new EditCommand(INDEX_FIRST_PERSON, descriptor));

        descriptor = new EditPersonDescriptorBuilder().withPhone("91111111").build();
        assertParseSuccess(parser, "1" + PARENT_PHONE_DESC_AMY,
                new EditCommand(INDEX_FIRST_PERSON, descriptor));

        descriptor = new EditPersonDescriptorBuilder().withEmail("amy@example.com").build();
        assertParseSuccess(parser, "1" + PARENT_EMAIL_DESC_AMY,
                new EditCommand(INDEX_FIRST_PERSON, descriptor));
    }

    @Test
    public void parse_duplicateSingleValueField_failure() {
        assertParseFailure(parser, "1" + NAME_DESC_AMY + NAME_DESC_AMY,
                "Multiple values specified for the following single-valued field(s): n/");
        assertParseFailure(parser, "1" + LEVEL_DESC_AMY + LEVEL_DESC_AMY,
                "Multiple values specified for the following single-valued field(s): lvl/");
        assertParseFailure(parser, "1" + PARENT_PHONE_DESC_AMY + PARENT_PHONE_DESC_AMY,
                "Multiple values specified for the following single-valued field(s): pn/");
        assertParseFailure(parser, "1" + PARENT_EMAIL_DESC_AMY + PARENT_EMAIL_DESC_AMY,
                "Multiple values specified for the following single-valued field(s): pe/");
    }

    @Test
    public void parse_emptySubject_failure() {
        assertParseFailure(parser, "1 s/", EditCommand.MESSAGE_SUBJECT_REQUIRED);
        assertParseFailure(parser, "1 s/   ", EditCommand.MESSAGE_SUBJECT_REQUIRED);
        assertParseFailure(parser, "1 s/Math s/", EditCommand.MESSAGE_SUBJECT_REQUIRED);
        assertParseFailure(parser, "1 s/ s/Math", EditCommand.MESSAGE_SUBJECT_REQUIRED);
    }

    @Test
    public void parse_validNameAndInvalidPhone_failure() {
        assertParseFailure(parser, "1 n/Jamie Tan pn/abc", Phone.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_nameWithSonOf_subjectsUnchanged() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName("John s/o Lee").build();

        assertParseSuccess(parser, "1 n/John s/o Lee", new EditCommand(INDEX_FIRST_PERSON, descriptor));
    }

    @Test
    public void parse_multipleSubjects_success() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withSubjects("Math", "Science").build();

        assertParseSuccess(parser, "1 s/Math s/Science", new EditCommand(INDEX_FIRST_PERSON, descriptor));
    }

    @Test
    public void parse_duplicateSubjects_successWithWarning() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withSubjects("Math").build();

        assertParseSuccess(parser, "1 s/Math s/math", new EditCommand(INDEX_FIRST_PERSON, descriptor,
                Messages.getWarningMessageForDuplicateSubjects(List.of("math"))));
    }

    @Test
    public void parse_invalidName_failure() {
        assertParseFailure(parser, "1 n/!!!", Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1 n/", Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_nameExceedsMaximumLength_failure() {
        assertParseFailure(parser, "1 n/" + "a".repeat(Name.MAX_LENGTH + 1), Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidAcademicLevel_failure() {
        assertParseFailure(parser, "1 lvl/Secondary 2", AcademicLevel.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1 lvl/", AcademicLevel.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidPhone_failure() {
        assertParseFailure(parser, "1 pn/123", Phone.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1 pn/", Phone.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidEmail_failure() {
        assertParseFailure(parser, "1 pe/not-an-email", Email.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1 pe/", Email.MESSAGE_CONSTRAINTS);
    }
}
