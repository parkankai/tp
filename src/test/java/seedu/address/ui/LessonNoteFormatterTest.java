package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.lesson.LessonDate;
import seedu.address.model.lesson.LessonNote;
import seedu.address.model.lesson.NoteText;
import seedu.address.model.subject.Subject;

public class LessonNoteFormatterTest {

    private static LessonNote note(String date, String subject, String text) {
        return new LessonNote(new LessonDate(date), new Subject(subject), new NoteText(text));
    }

    @Test
    public void format_noNotes_returnsEmptyList() {
        assertTrue(LessonNoteFormatter.format(List.of()).isEmpty());
    }

    @Test
    public void format_singleNote_returnsHeaderAndNote() {
        List<String> lines = LessonNoteFormatter.format(List.of(note("2026-10-01", "Math", "Algebra")));

        assertEquals(List.of(
                LessonNoteFormatter.LESSON_NOTES_HEADER,
                "1. 2026-10-01 | Math | Algebra"), lines);
    }

    @Test
    public void format_notesNumberedInGivenOrder() {
        List<String> lines = LessonNoteFormatter.format(List.of(
                note("2026-10-01", "Math", "Algebra"),
                note("2026-09-20", "Science", "Cells")));

        assertEquals("1. 2026-10-01 | Math | Algebra", lines.get(1));
        assertEquals("2. 2026-09-20 | Science | Cells", lines.get(2));
    }

    @Test
    public void format_exactlyMaxNotes_hasNoOverflowLine() {
        List<String> lines = LessonNoteFormatter.format(List.of(
                note("2026-10-03", "Math", "a"),
                note("2026-10-02", "Math", "b"),
                note("2026-10-01", "Math", "c")));

        // header + 3 notes, and nothing else
        assertEquals(4, lines.size());
        assertEquals("3. 2026-10-01 | Math | c", lines.get(3));
    }

    @Test
    public void format_moreThanMaxNotes_countsTheRemainder() {
        List<String> lines = LessonNoteFormatter.format(List.of(
                note("2026-10-05", "Math", "a"),
                note("2026-10-04", "Math", "b"),
                note("2026-10-03", "Math", "c"),
                note("2026-10-02", "Math", "d"),
                note("2026-10-01", "Math", "e")));

        // header + 3 notes + the overflow line
        assertEquals(5, lines.size());
        assertEquals("+ 2 more", lines.get(4));
    }

    @Test
    public void formatNote_longText_isTruncated() {
        String longText = "a".repeat(LessonNoteFormatter.MAX_DISPLAYED_NOTE_TEXT_LENGTH + 10);
        String line = LessonNoteFormatter.formatNote(1, note("2026-10-01", "Math", longText));

        assertTrue(line.endsWith("..."));
    }

    @Test
    public void truncate_shorterThanLimit_returnsTextUnchanged() {
        assertEquals("Algebra", LessonNoteFormatter.truncate("Algebra"));
    }

    @Test
    public void truncate_atLimit_returnsTextUnchanged() {
        String atLimit = "a".repeat(LessonNoteFormatter.MAX_DISPLAYED_NOTE_TEXT_LENGTH);
        assertEquals(atLimit, LessonNoteFormatter.truncate(atLimit));
    }

    @Test
    public void truncate_oneOverLimit_isCutAndSuffixed() {
        String overLimit = "a".repeat(LessonNoteFormatter.MAX_DISPLAYED_NOTE_TEXT_LENGTH + 1);

        assertEquals("a".repeat(LessonNoteFormatter.MAX_DISPLAYED_NOTE_TEXT_LENGTH) + "...",
                LessonNoteFormatter.truncate(overLimit));
    }

    @Test
    public void truncate_trailingSpaceAtCut_isStripped() {
        String text = "a".repeat(LessonNoteFormatter.MAX_DISPLAYED_NOTE_TEXT_LENGTH - 1) + " bbbbb";

        assertEquals("a".repeat(LessonNoteFormatter.MAX_DISPLAYED_NOTE_TEXT_LENGTH - 1) + "...",
                LessonNoteFormatter.truncate(text));
    }

    @Test
    public void truncate_emojiAtLimit_preservesWholeEmoji() {
        String prefix = "a".repeat(LessonNoteFormatter.MAX_DISPLAYED_NOTE_TEXT_LENGTH - 1);
        String emoji = "\ud83d\ude00";

        assertEquals(prefix + emoji + "...", LessonNoteFormatter.truncate(prefix + emoji + "extra"));
        assertEquals(prefix + emoji, LessonNoteFormatter.truncate(prefix + emoji));
    }

    @Test
    public void truncate_emojiAfterLimit_excludesWholeEmoji() {
        String prefix = "a".repeat(LessonNoteFormatter.MAX_DISPLAYED_NOTE_TEXT_LENGTH);

        assertEquals(prefix + "...", LessonNoteFormatter.truncate(prefix + "\ud83d\ude00"));
    }

    @Test
    public void truncate_multipleEmojis_countsCodePoints() {
        String atLimit = "\ud83d\ude00".repeat(LessonNoteFormatter.MAX_DISPLAYED_NOTE_TEXT_LENGTH);

        assertEquals(atLimit, LessonNoteFormatter.truncate(atLimit));
        assertEquals(atLimit + "...", LessonNoteFormatter.truncate(atLimit + "extra"));
    }

    @Test
    public void formatNote_emojiAtLimit_preservesEmojiAndFullNote() {
        String preview = "a".repeat(LessonNoteFormatter.MAX_DISPLAYED_NOTE_TEXT_LENGTH - 1) + "\ud83d\ude00";
        String fullText = preview + " Great progress";
        LessonNote lessonNote = note("2026-10-01", "Math", fullText);

        assertEquals("1. 2026-10-01 | Math | " + preview + "...", LessonNoteFormatter.formatNote(1, lessonNote));
        assertEquals(fullText, lessonNote.getNoteText().value);
    }
}
