package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.CommandTestUtil.LEVEL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PARENT_EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PARENT_PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.SUBJECT_DESC_MATH;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.AMY;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.lesson.LessonDate;
import seedu.address.model.lesson.LessonNote;
import seedu.address.model.lesson.NoteText;
import seedu.address.model.person.Student;
import seedu.address.model.subject.Subject;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;

    @BeforeEach
    public void setUp() {
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "deletestudent 9";
        assertCommandException(deleteCommand, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_SUCCESS, model);
    }

    @Test
    public void execute_clearWithArguments_preservesStudentsNotesAndSavedData() throws Exception {
        LessonNote note = new LessonNote(new LessonDate("2024-09-18"),
                new Subject("Math"), new NoteText("Covered algebra"));
        Student student = new PersonBuilder().withLessonNotes(note).build();
        Student otherStudent = new PersonBuilder().withName("Betty Lim").build();
        model.addStudent(student);
        model.addStudent(otherStudent);
        logic.execute("list");
        Path dataFile = temporaryFolder.resolve("addressBook.json");
        String savedData = Files.readString(dataFile);
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ClearCommand.MESSAGE_USAGE);

        for (String command : List.of("clear 1", "clear abc")) {
            assertThrows(ParseException.class, expectedMessage, () -> logic.execute(command));
            assertEquals(List.of(student, otherStudent), model.getAddressBook().getStudentList());
            assertEquals(List.of(note), model.getAddressBook().getStudentList().getFirst().getLessonNotes());
            assertEquals(savedData, Files.readString(dataFile));
        }
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_helpAndExitWithFailingStorage_returnsResultWithoutSaving() throws Exception {
        model.addStudent(AMY);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        for (IOException failure : List.of(DUMMY_IO_EXCEPTION, DUMMY_AD_EXCEPTION)) {
            AtomicInteger saveAttempts = new AtomicInteger();
            JsonAddressBookStorage addressBookStorage =
                    new JsonAddressBookStorage(temporaryFolder.resolve("blocked.json")) {
                        @Override
                        public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                            saveAttempts.incrementAndGet();
                            throw failure;
                        }
                    };
            JsonUserPrefsStorage userPrefsStorage =
                    new JsonUserPrefsStorage(temporaryFolder.resolve("blockedUserPrefs.json"));
            Logic testLogic = new LogicManager(model, new StorageManager(addressBookStorage, userPrefsStorage));

            assertEquals(new CommandResult(HelpCommand.SHOWING_HELP_MESSAGE, true, false), testLogic.execute("help"));
            assertEquals(new CommandResult(ExitCommand.MESSAGE_EXIT_ACKNOWLEDGEMENT, false, true),
                    testLogic.execute("exit"));
            assertEquals(0, saveAttempts.get());
            assertEquals(expectedModel, model);
        }
    }

    @Test
    public void hasUnsavedChanges_failedSaveThenRecovery_savesEntireCurrentState() throws Exception {
        ToggleStorage storage = new ToggleStorage();
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("recoveryPrefs.json"))));
        assertFalse(logic.hasUnsavedChanges());
        assertThrows(CommandException.class, () -> logic.execute("list"));
        assertFalse(logic.hasUnsavedChanges());

        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + LEVEL_DESC_AMY
                + SUBJECT_DESC_MATH + PARENT_PHONE_DESC_AMY + PARENT_EMAIL_DESC_AMY;
        assertThrows(CommandException.class, () -> logic.execute(addCommand));
        assertTrue(logic.hasUnsavedChanges());
        logic.execute("help");
        logic.execute("exit");
        assertTrue(logic.hasUnsavedChanges());

        storage.isFailing = false;
        logic.execute("list");
        assertFalse(logic.hasUnsavedChanges());
        assertEquals(model.getAddressBook(), new AddressBook(storage.readAddressBook().orElseThrow()));
        assertEquals(1, storage.readAddressBook().orElseThrow().getStudentList().size());
    }

    @Test
    public void hasUnsavedChanges_failedAddThenDelete_returnsToBaseline() throws Exception {
        ToggleStorage storage = new ToggleStorage();
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("undoPrefs.json"))));
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + LEVEL_DESC_AMY
                + SUBJECT_DESC_MATH + PARENT_PHONE_DESC_AMY + PARENT_EMAIL_DESC_AMY;
        assertThrows(CommandException.class, () -> logic.execute(addCommand));
        assertTrue(logic.hasUnsavedChanges());
        assertThrows(CommandException.class, () -> logic.execute("deletestudent 1"));
        assertFalse(logic.hasUnsavedChanges());
    }

    @Test
    public void hasUnsavedChanges_rejectedCommand_doesNotChangeBaseline() {
        assertThrows(ParseException.class, () -> logic.execute("invalid"));
        assertThrows(CommandException.class, () -> logic.execute("deletestudent 1"));
        assertFalse(logic.hasUnsavedChanges());
    }

    private class ToggleStorage extends JsonAddressBookStorage {
        private boolean isFailing = true;

        ToggleStorage() {
            super(temporaryFolder.resolve("recovery.json"));
        }

        @Override
        public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
            if (isFailing) {
                throw DUMMY_IO_EXCEPTION;
            }
            super.saveAddressBook(addressBook);
        }
    }

    @Test
    public void getFilteredStudentList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredStudentList().remove(0));
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        // Inject LogicManager with a JsonAddressBookStorage that throws the IOException e when saving
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(prefPath) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);

        logic = new LogicManager(model, storage);

        // Triggers the saveAddressBook method by executing an add command
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + LEVEL_DESC_AMY
                + SUBJECT_DESC_MATH + PARENT_PHONE_DESC_AMY + PARENT_EMAIL_DESC_AMY;
        Student expectedPerson = new PersonBuilder(AMY).build();
        ModelManager expectedModel = new ModelManager();
        expectedModel.addStudent(expectedPerson);
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }
}
