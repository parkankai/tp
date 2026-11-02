package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LESSON_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.AddNoteCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.DeleteNoteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.commands.EditNoteCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.HistoryCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.lesson.LessonDate;
import seedu.address.model.lesson.LessonNote;
import seedu.address.model.lesson.NoteText;
import seedu.address.model.person.AcademicLevel;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Student;
import seedu.address.model.person.StudentMatchesFilterPredicate;
import seedu.address.model.subject.Subject;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_add() throws Exception {
        Student person = new PersonBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(PersonUtil.getAddCommand(person));
        assertEquals(new AddCommand(person), command);
    }

    @Test
    public void parseCommand_addnote() throws Exception {
        LessonNote lessonNote = new LessonNote(new LessonDate("2026-10-01"),
                new Subject("Math"), new NoteText("Revised simultaneous equations"));
        AddNoteCommand command = (AddNoteCommand) parser.parseCommand(
                AddNoteCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased()
                + " " + PREFIX_DATE + "2026-10-01"
                + " " + PREFIX_SUBJECT + "Math"
                + " " + PREFIX_NOTE + "Revised simultaneous equations");
        assertEquals(new AddNoteCommand(INDEX_FIRST_PERSON, lessonNote), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand("  clear  ") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_clearWithArguments_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ClearCommand.MESSAGE_USAGE);
        assertThrows(ParseException.class, expectedMessage, () -> parser.parseCommand("clear 1"));
        assertThrows(ParseException.class, expectedMessage, () -> parser.parseCommand("clear abc"));
    }

    @Test
    public void parseCommand_deleteStudent() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                "deletestudent 1");
        assertEquals(new DeleteCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_deletenote() throws Exception {
        DeleteNoteCommand command = (DeleteNoteCommand) parser.parseCommand(
                DeleteNoteCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased()
                + " " + PREFIX_LESSON_INDEX + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new DeleteNoteCommand(INDEX_FIRST_PERSON, INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Student person = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " " + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = List.of("foo", "bar", "baz");
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindCommand(new NameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_history() throws Exception {
        HistoryCommand command = (HistoryCommand) parser.parseCommand(
                HistoryCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());

        assertEquals(new HistoryCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + " 3") instanceof ListCommand);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }

    @Test
    public void parseCommand_view() throws Exception {
        ViewCommand command = (ViewCommand) parser.parseCommand("view 1");
        assertEquals(new ViewCommand(INDEX_FIRST_PERSON), command);
    }
    @Test
    public void parseCommand_filter() throws Exception {
        StudentMatchesFilterPredicate predicate =
                new StudentMatchesFilterPredicate(
                        Optional.of(new AcademicLevel("Sec 2")),
                        Set.of(
                                new Subject("Math"),
                                new Subject("Science")));

        FilterCommand command = (FilterCommand) parser.parseCommand(
                FilterCommand.COMMAND_WORD
                        + " " + PREFIX_SUBJECT + "Math"
                        + " " + PREFIX_SUBJECT + "Science"
                        + " " + PREFIX_LEVEL + "Sec 2");

        assertEquals(new FilterCommand(predicate), command);
    }
    @Test
    public void parseCommand_editnote() throws Exception {
        EditNoteCommand expected = new EditNoteCommand(
                INDEX_FIRST_PERSON, INDEX_SECOND_PERSON,
                Optional.of(new LessonDate("2026-10-01")),
                Optional.empty(),
                Optional.of(new NoteText("Revised simultaneous equations")));

        EditNoteCommand actual = (EditNoteCommand) parser.parseCommand(
                "editnote 1 ln/2 d/2026-10-01 note/Revised simultaneous equations");

        assertEquals(expected, actual);
    }
}
