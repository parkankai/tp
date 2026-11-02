package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LESSON_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditNoteCommand;
import seedu.address.model.lesson.LessonDate;
import seedu.address.model.lesson.NoteText;
import seedu.address.model.subject.Subject;

public class EditNoteCommandParserTest {

    private static final String VALID_DATE = "2026-10-01";
    private static final String VALID_SUBJECT = "Math";
    private static final String VALID_NOTE = "Revised simultaneous equations";

    private static final String LESSON_INDEX_DESC =
            " " + PREFIX_LESSON_INDEX + INDEX_SECOND_PERSON.getOneBased();
    private static final String DATE_DESC = " " + PREFIX_DATE + VALID_DATE;
    private static final String SUBJECT_DESC = " " + PREFIX_SUBJECT + VALID_SUBJECT;
    private static final String NOTE_DESC = " " + PREFIX_NOTE + VALID_NOTE;

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditNoteCommand.MESSAGE_USAGE);

    private final EditNoteCommandParser parser = new EditNoteCommandParser();

    @Test
    public void parse_allFieldsPresent_success() throws Exception {
        String input = INDEX_FIRST_PERSON.getOneBased()
                + LESSON_INDEX_DESC + DATE_DESC + SUBJECT_DESC + NOTE_DESC;

        EditNoteCommand expected = new EditNoteCommand(
                INDEX_FIRST_PERSON, INDEX_SECOND_PERSON,
                Optional.of(new LessonDate(VALID_DATE)),
                Optional.of(new Subject(VALID_SUBJECT)),
                Optional.of(new NoteText(VALID_NOTE)));

        assertParseSuccess(parser, input, expected);
    }

    @Test
    public void parse_partialFields_success() throws Exception {
        String input = INDEX_FIRST_PERSON.getOneBased()
                + LESSON_INDEX_DESC + NOTE_DESC;

        EditNoteCommand expected = new EditNoteCommand(
                INDEX_FIRST_PERSON, INDEX_SECOND_PERSON,
                Optional.empty(), Optional.empty(),
                Optional.of(new NoteText(VALID_NOTE)));

        assertParseSuccess(parser, input, expected);
    }

    @Test
    public void parse_partialFieldsWithoutNote_success() {
        String input = INDEX_FIRST_PERSON.getOneBased() + LESSON_INDEX_DESC + DATE_DESC + SUBJECT_DESC;
        EditNoteCommand expected = new EditNoteCommand(
                INDEX_FIRST_PERSON, INDEX_SECOND_PERSON,
                Optional.of(new LessonDate(VALID_DATE)), Optional.of(new Subject(VALID_SUBJECT)), Optional.empty());

        assertParseSuccess(parser, input, expected);
    }

    @Test
    public void parse_noteContainsCommandPrefixes_success() {
        String noteWithPrefixes = "Practised ln/ notation s/ Math d/ daily note/ carefully";
        String input = INDEX_FIRST_PERSON.getOneBased() + LESSON_INDEX_DESC
                + " " + PREFIX_NOTE + noteWithPrefixes;
        EditNoteCommand expected = new EditNoteCommand(
                INDEX_FIRST_PERSON, INDEX_SECOND_PERSON,
                Optional.empty(), Optional.empty(), Optional.of(new NoteText(noteWithPrefixes)));

        assertParseSuccess(parser, input, expected);
    }

    @Test
    public void parse_lessonIndexAfterNote_failure() {
        String input = INDEX_FIRST_PERSON.getOneBased() + NOTE_DESC + LESSON_INDEX_DESC;

        assertParseFailure(parser, input, MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_missingIndexOrEditableField_failure() {
        assertParseFailure(parser, LESSON_INDEX_DESC + NOTE_DESC, MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, String.valueOf(INDEX_FIRST_PERSON.getOneBased()) + NOTE_DESC,
                MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, INDEX_FIRST_PERSON.getOneBased() + LESSON_INDEX_DESC,
                EditNoteCommand.MESSAGE_NOT_EDITED);
    }

    @Test
    public void parse_invalidDate_failure() {
        String input = INDEX_FIRST_PERSON.getOneBased() + LESSON_INDEX_DESC
                + " " + PREFIX_DATE + "2026-02-30";
        assertParseFailure(parser, input, LessonDate.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidSubject_failure() {
        String input = INDEX_FIRST_PERSON.getOneBased() + LESSON_INDEX_DESC
                + " " + PREFIX_SUBJECT + "M";
        assertParseFailure(parser, input, Subject.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidNoteText_failure() {
        String input = INDEX_FIRST_PERSON.getOneBased() + LESSON_INDEX_DESC
                + " " + PREFIX_NOTE + "   ";
        assertParseFailure(parser, input, NoteText.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_duplicatePrefix_failure() {
        String input = INDEX_FIRST_PERSON.getOneBased()
                + LESSON_INDEX_DESC + LESSON_INDEX_DESC + NOTE_DESC;
        assertParseFailure(parser, input,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_LESSON_INDEX));
    }
}
