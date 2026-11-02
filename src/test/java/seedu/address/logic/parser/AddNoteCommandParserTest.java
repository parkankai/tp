package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddNoteCommand;
import seedu.address.model.lesson.LessonDate;
import seedu.address.model.lesson.LessonNote;
import seedu.address.model.lesson.NoteText;
import seedu.address.model.subject.Subject;

public class AddNoteCommandParserTest {

    private static final String VALID_DATE = "2026-10-01";
    private static final String VALID_SUBJECT = "Math";
    private static final String VALID_NOTE = "Revised simultaneous equations";

    private static final String DATE_DESC = " " + PREFIX_DATE + VALID_DATE;
    private static final String SUBJECT_DESC = " " + PREFIX_SUBJECT + VALID_SUBJECT;
    private static final String NOTE_DESC = " " + PREFIX_NOTE + VALID_NOTE;

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddNoteCommand.MESSAGE_USAGE);

    private AddNoteCommandParser parser = new AddNoteCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        String userInput = INDEX_FIRST_PERSON.getOneBased() + DATE_DESC + SUBJECT_DESC + NOTE_DESC;
        LessonNote expectedNote = new LessonNote(new LessonDate(VALID_DATE), new Subject(VALID_SUBJECT),
                new NoteText(VALID_NOTE));

        assertParseSuccess(parser, userInput, new AddNoteCommand(INDEX_FIRST_PERSON, expectedNote));
    }

    @Test
    public void parse_fieldsBeforeNoteInAnyOrder_success() {
        String userInput = INDEX_FIRST_PERSON.getOneBased() + SUBJECT_DESC + DATE_DESC + NOTE_DESC;
        LessonNote expectedNote = new LessonNote(new LessonDate(VALID_DATE), new Subject(VALID_SUBJECT),
                new NoteText(VALID_NOTE));

        assertParseSuccess(parser, userInput, new AddNoteCommand(INDEX_FIRST_PERSON, expectedNote));
    }

    @Test
    public void parse_noteContainsCommandPrefixes_success() {
        String noteWithPrefixes = "Practised s/ notation d/ daily note/ carefully ln/1";
        String userInput = INDEX_FIRST_PERSON.getOneBased() + DATE_DESC + SUBJECT_DESC
                + " " + PREFIX_NOTE + noteWithPrefixes;
        LessonNote expectedNote = new LessonNote(new LessonDate(VALID_DATE), new Subject(VALID_SUBJECT),
                new NoteText(noteWithPrefixes));

        assertParseSuccess(parser, userInput, new AddNoteCommand(INDEX_FIRST_PERSON, expectedNote));
    }

    @Test
    public void parse_fieldsAfterNote_failure() {
        String userInput = INDEX_FIRST_PERSON.getOneBased() + NOTE_DESC + DATE_DESC + SUBJECT_DESC;

        assertParseFailure(parser, userInput, MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_missingCompulsoryField_failure() {
        // no index
        assertParseFailure(parser, DATE_DESC + SUBJECT_DESC + NOTE_DESC, MESSAGE_INVALID_FORMAT);

        // no date
        assertParseFailure(parser, INDEX_FIRST_PERSON.getOneBased() + SUBJECT_DESC + NOTE_DESC,
                MESSAGE_INVALID_FORMAT);

        // no subject
        assertParseFailure(parser, INDEX_FIRST_PERSON.getOneBased() + DATE_DESC + NOTE_DESC,
                MESSAGE_INVALID_FORMAT);

        // no note text
        assertParseFailure(parser, INDEX_FIRST_PERSON.getOneBased() + DATE_DESC + SUBJECT_DESC,
                MESSAGE_INVALID_FORMAT);

        // nothing at all
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidIndex_failure() {
        assertParseFailure(parser, "a" + DATE_DESC + SUBJECT_DESC + NOTE_DESC, MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "0" + DATE_DESC + SUBJECT_DESC + NOTE_DESC, MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "-1" + DATE_DESC + SUBJECT_DESC + NOTE_DESC, MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidDate_failure() {
        // wrong format
        assertParseFailure(parser, INDEX_FIRST_PERSON.getOneBased() + " " + PREFIX_DATE + "01-01-2026"
                + SUBJECT_DESC + NOTE_DESC, LessonDate.MESSAGE_CONSTRAINTS);

        // not a real calendar date
        assertParseFailure(parser, INDEX_FIRST_PERSON.getOneBased() + " " + PREFIX_DATE + "2026-02-30"
                + SUBJECT_DESC + NOTE_DESC, LessonDate.MESSAGE_CONSTRAINTS);

        // in the future
        assertParseFailure(parser, INDEX_FIRST_PERSON.getOneBased() + " " + PREFIX_DATE + "2099-01-01"
                + SUBJECT_DESC + NOTE_DESC, LessonDate.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidSubject_failure() {
        assertParseFailure(parser, INDEX_FIRST_PERSON.getOneBased() + DATE_DESC + " " + PREFIX_SUBJECT + "M"
                + NOTE_DESC, Subject.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidNoteText_failure() {
        // blank note text
        assertParseFailure(parser, INDEX_FIRST_PERSON.getOneBased() + DATE_DESC + SUBJECT_DESC + " "
                + PREFIX_NOTE + "   ", NoteText.MESSAGE_CONSTRAINTS);

        // longer than the maximum allowed length
        assertParseFailure(parser, INDEX_FIRST_PERSON.getOneBased() + DATE_DESC + SUBJECT_DESC + " "
                + PREFIX_NOTE + "a".repeat(NoteText.MAX_LENGTH + 1), NoteText.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_duplicatePrefix_failure() {
        assertParseFailure(parser, INDEX_FIRST_PERSON.getOneBased() + DATE_DESC + DATE_DESC + SUBJECT_DESC
                + NOTE_DESC, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_DATE));

        assertParseFailure(parser, INDEX_FIRST_PERSON.getOneBased() + DATE_DESC + SUBJECT_DESC + SUBJECT_DESC
                + NOTE_DESC, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_SUBJECT));
    }
}
