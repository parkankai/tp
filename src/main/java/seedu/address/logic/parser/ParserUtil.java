package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.lesson.LessonDate;
import seedu.address.model.lesson.NoteText;
import seedu.address.model.person.AcademicLevel;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.subject.Subject;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index must be a positive integer.";

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws ParseException if the specified index is invalid (not a non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /** Parses a string into an {@code AcademicLevel}. */
    public static AcademicLevel parseAcademicLevel(String level) throws ParseException {
        requireNonNull(level);
        try {
            return new AcademicLevel(level);
        } catch (IllegalArgumentException exception) {
            throw new ParseException(AcademicLevel.MESSAGE_CONSTRAINTS, exception);
        }
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (!Phone.isValidPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /** Parses a string into a {@code Subject}. */
    public static Subject parseSubject(String subject) throws ParseException {
        requireNonNull(subject);
        try {
            return new Subject(subject);
        } catch (IllegalArgumentException exception) {
            throw new ParseException(Subject.MESSAGE_CONSTRAINTS, exception);
        }
    }

    /** Parses subject values into a set, merging duplicate subjects case-insensitively and space-insensitively. */
    public static Set<Subject> parseSubjects(Collection<String> subjects) throws ParseException {
        requireNonNull(subjects);
        final Set<Subject> subjectSet = new HashSet<>();
        for (String subject : subjects) {
            Subject parsedSubject = parseSubject(subject);
            if (subjectSet.stream().noneMatch(existingSubject -> existingSubject.matches(parsedSubject))) {
                subjectSet.add(parsedSubject);
            }
        }
        return subjectSet;
    }

    /**
     * Returns subject values that duplicate an earlier value, ignoring case and repeated spaces.
     */
    public static List<String> findDuplicateSubjects(Collection<String> subjects) throws ParseException {
        requireNonNull(subjects);
        final Set<Subject> uniqueSubjects = new HashSet<>();
        final List<String> duplicateSubjects = new ArrayList<>();
        for (String subject : subjects) {
            Subject parsedSubject = parseSubject(subject);
            if (uniqueSubjects.stream().anyMatch(existingSubject -> existingSubject.matches(parsedSubject))) {
                duplicateSubjects.add(subject);
            } else {
                uniqueSubjects.add(parsedSubject);
            }
        }
        return duplicateSubjects;
    }

    /** Parses a string into a {@code LessonDate}. */
    public static LessonDate parseLessonDate(String date) throws ParseException {
        requireNonNull(date);
        try {
            return new LessonDate(date.trim());
        } catch (IllegalArgumentException exception) {
            throw new ParseException(LessonDate.MESSAGE_CONSTRAINTS, exception);
        }
    }

    /** Parses a string into a {@code NoteText}. */
    public static NoteText parseNoteText(String noteText) throws ParseException {
        requireNonNull(noteText);
        try {
            return new NoteText(noteText.trim());
        } catch (IllegalArgumentException exception) {
            throw new ParseException(NoteText.MESSAGE_CONSTRAINTS, exception);
        }
    }

}
