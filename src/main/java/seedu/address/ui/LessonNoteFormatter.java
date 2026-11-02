package seedu.address.ui;

import java.util.ArrayList;
import java.util.List;

import seedu.address.model.lesson.LessonNote;

/**
 * Formats a student's lesson notes into the lines shown on a {@code PersonCard}.
 * Kept free of JavaFX so that the formatting rules can be tested on their own.
 */
public class LessonNoteFormatter {

    public static final String LESSON_NOTES_HEADER = "Lesson Notes:";

    /** The most recent notes shown on a card; any beyond this are summarised by a count. */
    public static final int MAX_DISPLAYED_LESSON_NOTES = 3;

    /** Maximum number of Unicode code points included in a note preview before truncation. */
    public static final int MAX_DISPLAYED_NOTE_TEXT_LENGTH = 60;

    private static final String MESSAGE_MORE_LESSON_NOTES = "+ %d more";
    private static final String LESSON_NOTE_FORMAT = "%d. %s | %s | %s";
    private static final String TRUNCATION_SUFFIX = "...";

    private LessonNoteFormatter() {
        // utility class
    }

    /**
     * Returns the lines to display for {@code notes}: a header, the most recent
     * {@code MAX_DISPLAYED_LESSON_NOTES} notes numbered from 1, and a count of any remaining
     * notes. Returns an empty list when the student has no lesson notes, so that the card can
     * leave the section out entirely.
     *
     * @param notes the student's lesson notes, already sorted from the most recent to the earliest
     */
    public static List<String> format(List<LessonNote> notes) {
        if (notes.isEmpty()) {
            return List.of();
        }

        List<String> lines = new ArrayList<>();
        lines.add(LESSON_NOTES_HEADER);

        int displayedNotes = Math.min(notes.size(), MAX_DISPLAYED_LESSON_NOTES);
        for (int i = 0; i < displayedNotes; i++) {
            lines.add(formatNote(i + 1, notes.get(i)));
        }

        int hiddenNotes = notes.size() - displayedNotes;
        if (hiddenNotes > 0) {
            lines.add(String.format(MESSAGE_MORE_LESSON_NOTES, hiddenNotes));
        }

        return lines;
    }

    /**
     * Returns a single note line, prefixed with its 1-based position.
     */
    public static String formatNote(int position, LessonNote note) {
        return String.format(LESSON_NOTE_FORMAT, position, note.getDate(), note.getSubject(),
                truncate(note.getNoteText().value));
    }

    /**
     * Returns {@code text} cut to {@code MAX_DISPLAYED_NOTE_TEXT_LENGTH} Unicode code points,
     * followed by an ellipsis when it was cut. The full text stays available through the history command.
     */
    public static String truncate(String text) {
        int codePointCount = text.codePointCount(0, text.length());
        if (codePointCount <= MAX_DISPLAYED_NOTE_TEXT_LENGTH) {
            return text;
        }

        int endIndex = text.offsetByCodePoints(0, MAX_DISPLAYED_NOTE_TEXT_LENGTH);
        return text.substring(0, endIndex).stripTrailing() + TRUNCATION_SUFFIX;
    }

}
