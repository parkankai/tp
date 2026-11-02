package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.lesson.LessonDate;
import seedu.address.model.lesson.LessonNote;
import seedu.address.model.lesson.NoteText;
import seedu.address.model.person.Student;
import seedu.address.model.subject.Subject;
import seedu.address.testutil.PersonBuilder;

public class JsonAddressBookStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonAddressBookStorageTest");

    @TempDir
    public Path testFolder;

    @Test
    public void read_nullFilePath_throwsNullPointerException() {
        JsonAddressBookStorage storage = new JsonAddressBookStorage(testFolder.resolve("students.json"));
        assertThrows(NullPointerException.class, () -> storage.readAddressBook(null));
    }

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readAddressBook("NonExistentFile.json").isPresent());
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("notJsonFormatAddressBook.json"));
    }

    @Test
    public void readAddressBook_invalidData_preservesCorruptedFile() throws IOException {
        Path filePath = testFolder.resolve("addressbook.json");
        String invalidData = "{ invalid json";
        Files.writeString(filePath, invalidData);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);

        DataLoadingException exception = assertThrows(DataLoadingException.class, storage::readAddressBook);

        Path backupPath = testFolder.resolve("addressbook.corrupted-1.json");
        assertEquals(backupPath, exception.getBackupPath().orElseThrow());
        assertEquals(invalidData, Files.readString(backupPath));
        assertFalse(Files.exists(filePath));

        storage.saveAddressBook(new AddressBook());

        assertTrue(Files.exists(filePath));
        assertEquals(invalidData, Files.readString(backupPath));
    }

    @Test
    public void readAddressBook_existingCorruptedFile_usesNextAvailableName() throws IOException {
        Path filePath = testFolder.resolve("addressbook.json");
        Files.writeString(filePath, "{ invalid json");
        Files.writeString(testFolder.resolve("addressbook.corrupted-1.json"), "older data");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);

        DataLoadingException exception = assertThrows(DataLoadingException.class, storage::readAddressBook);

        assertEquals(testFolder.resolve("addressbook.corrupted-2.json"), exception.getBackupPath().orElseThrow());
    }

    @Test
    public void readAddressBook_backupFails_preservesOriginalAndBlocksWrites() throws IOException {
        Path filePath = testFolder.resolve("addressbook.json");
        String invalidData = "{ invalid json";
        Files.writeString(filePath, invalidData);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath) {
            @Override
            void preserveInvalidData(Path source, Path backupPath) throws IOException {
                throw new IOException("Backup failed");
            }
        };

        DataLoadingException exception = assertThrows(DataLoadingException.class, storage::readAddressBook);

        assertTrue(exception.getBackupPath().isEmpty());
        assertEquals(invalidData, Files.readString(filePath));
        assertThrows(IOException.class, () -> storage.saveAddressBook(new AddressBook()));
    }

    @Test
    public void getNextCorruptedFilePath_fileWithoutParentOrExtension_usesCorruptedSuffix() throws Exception {
        JsonAddressBookStorage storage = new JsonAddressBookStorage(Path.of("addressbook"));
        Method method = JsonAddressBookStorage.class.getDeclaredMethod("getNextCorruptedFilePath", Path.class);
        method.setAccessible(true);

        assertEquals(Path.of("addressbook.corrupted-1"), method.invoke(storage, Path.of("addressbook")));
    }

    @Test
    public void saveAddressBook_writeBlocked_throwsIoException() throws Exception {
        Path filePath = testFolder.resolve("addressbook.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);
        Field writeBlockedField = JsonAddressBookStorage.class.getDeclaredField("isWriteBlocked");
        writeBlockedField.setAccessible(true);
        writeBlockedField.set(storage, true);

        IOException exception = assertThrows(IOException.class, () -> storage.saveAddressBook(new AddressBook()));

        assertEquals("Refusing to overwrite an invalid data file that could not be preserved", exception.getMessage());
    }

    @Test
    public void read_unknownProperty_exceptionThrown() throws IOException {
        Path filePath = testFolder.resolve("addressbook.json");
        Files.writeString(filePath, "{\"students\":[],\"unknownProperty\":true}");

        assertThrows(DataLoadingException.class, () ->
                new JsonAddressBookStorage(filePath).readAddressBook(filePath));
        assertTrue(Files.exists(filePath));
    }

    @Test
    public void read_nullDocument_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("nullDocumentAddressBook.json"));
    }

    @Test
    public void read_nullStudent_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("nullStudentAddressBook.json"));
    }

    @Test
    public void read_nullLessonNote_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("nullLessonNoteAddressBook.json"));
    }

    @Test
    public void read_nullSubject_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("nullSubjectAddressBook.json"));
    }

    @Test
    public void readAndSaveAddressBook_success() throws Exception {
        Path filePath = testFolder.resolve("students.json");
        AddressBook original = getTypicalAddressBook();
        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);

        storage.saveAddressBook(original, filePath);
        ReadOnlyAddressBook readBack = storage.readAddressBook(filePath).orElseThrow();

        assertEquals(original, new AddressBook(readBack));
    }

    @Test
    public void readAndSaveAddressBook_studentWithLessonNotes_success() throws Exception {
        Path filePath = testFolder.resolve("students.json");
        Student studentWithLessonNotes = new PersonBuilder().withName("Hoon Meier").withLessonNotes(
                new LessonNote(new LessonDate("2024-09-18"), new Subject("Math"), new NoteText("Covered algebra")),
                new LessonNote(new LessonDate("2024-09-04"), new Subject("Science"), new NoteText("Covered cells")))
                .build();
        AddressBook original = getTypicalAddressBook();
        original.addStudent(studentWithLessonNotes);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);

        storage.saveAddressBook(original, filePath);
        ReadOnlyAddressBook readBack = storage.readAddressBook(filePath).orElseThrow();

        assertEquals(original, new AddressBook(readBack));
    }

    @Test
    public void saveAddressBook_nullAddressBook_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveAddressBook(null, "SomeFile.json"));
    }

    @Test
    public void saveAddressBook_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveAddressBook(new AddressBook(), null));
    }

    private java.util.Optional<ReadOnlyAddressBook> readAddressBook(String filePath) throws Exception {
        return new JsonAddressBookStorage(Paths.get(filePath))
                .readAddressBook(TEST_DATA_FOLDER.resolve(filePath));
    }

    private void saveAddressBook(ReadOnlyAddressBook addressBook, String filePath) {
        try {
            new JsonAddressBookStorage(Paths.get(filePath))
                    .saveAddressBook(addressBook, TEST_DATA_FOLDER.resolve(filePath));
        } catch (IOException exception) {
            throw new AssertionError("There should not be an error writing to the file.", exception);
        }
    }
}
