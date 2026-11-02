package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LESSON_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;

import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditNoteCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.lesson.LessonDate;
import seedu.address.model.lesson.NoteText;
import seedu.address.model.subject.Subject;

/**
 * Parses arguments for the editnote command.
 */
public class EditNoteCommandParser implements Parser<EditNoteCommand> {

    @Override
    public EditNoteCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenizeWithTerminalPrefix(args, PREFIX_NOTE,
                PREFIX_LESSON_INDEX, PREFIX_DATE, PREFIX_SUBJECT);

        if (argMultimap.getValue(PREFIX_LESSON_INDEX).isEmpty()) {
            throw new ParseException(String.format(
                    MESSAGE_INVALID_COMMAND_FORMAT, EditNoteCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(
                PREFIX_LESSON_INDEX, PREFIX_DATE, PREFIX_SUBJECT, PREFIX_NOTE);

        Index studentIndex;
        Index lessonIndex;
        try {
            studentIndex = ParserUtil.parseIndex(argMultimap.getPreamble());
            lessonIndex = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_LESSON_INDEX).get());
        } catch (ParseException exception) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditNoteCommand.MESSAGE_USAGE), exception);
        }

        if (argMultimap.getValue(PREFIX_DATE).isEmpty()
                && argMultimap.getValue(PREFIX_SUBJECT).isEmpty()
                && argMultimap.getValue(PREFIX_NOTE).isEmpty()) {
            throw new ParseException(EditNoteCommand.MESSAGE_NOT_EDITED);
        }

        Optional<LessonDate> date = argMultimap.getValue(PREFIX_DATE).isPresent()
                ? Optional.of(ParserUtil.parseLessonDate(argMultimap.getValue(PREFIX_DATE).get()))
                : Optional.empty();
        Optional<Subject> subject = argMultimap.getValue(PREFIX_SUBJECT).isPresent()
                ? Optional.of(ParserUtil.parseSubject(argMultimap.getValue(PREFIX_SUBJECT).get()))
                : Optional.empty();
        Optional<NoteText> noteText = argMultimap.getValue(PREFIX_NOTE).isPresent()
                ? Optional.of(ParserUtil.parseNoteText(argMultimap.getValue(PREFIX_NOTE).get()))
                : Optional.empty();

        return new EditNoteCommand(studentIndex, lessonIndex, date, subject, noteText);
    }
}
