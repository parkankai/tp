package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.storage.Storage;

public class MainAppTest {

    @Test
    public void initModelManager_corruptDataWithBackup_startsEmptyAndRecordsWarning() throws Exception {
        Path dataFilePath = Path.of("data", "addressbook.json");
        Path backupPath = Path.of("data", "addressbook.corrupted-1.json");
        MainApp mainApp = new MainApp();

        assertTrue(mainApp.initModelManager(new CorruptDataStorage(dataFilePath, backupPath), new UserPrefs())
                .getAddressBook().getStudentList().isEmpty());

        Field warningField = MainApp.class.getDeclaredField("dataLoadingWarning");
        warningField.setAccessible(true);
        assertEquals("The data file could not be loaded and has been preserved at " + backupPath
                + ". TutorBroPro has started with an empty data set.", warningField.get(mainApp));
    }

    @Test
    public void initModelManager_corruptDataWithoutBackup_startsEmptyAndRecordsWarning() throws Exception {
        Path dataFilePath = Path.of("data", "addressbook.json");
        MainApp mainApp = new MainApp();

        assertTrue(mainApp.initModelManager(new CorruptDataStorage(dataFilePath, null), new UserPrefs())
                .getAddressBook().getStudentList().isEmpty());

        Field warningField = MainApp.class.getDeclaredField("dataLoadingWarning");
        warningField.setAccessible(true);
        assertEquals("The data file at " + dataFilePath + " could not be loaded. "
                + "TutorBroPro has started with an empty data set; the original file was not changed.",
                warningField.get(mainApp));
    }

    private static class CorruptDataStorage implements Storage {
        private final Path dataFilePath;
        private final Path backupPath;

        CorruptDataStorage(Path dataFilePath, Path backupPath) {
            this.dataFilePath = dataFilePath;
            this.backupPath = backupPath;
        }

        @Override
        public Path getUserPrefsFilePath() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<UserPrefs> readUserPrefs() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Path getAddressBookFilePath() {
            return dataFilePath;
        }

        @Override
        public Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException {
            throw new DataLoadingException(new IOException("Invalid data"), backupPath);
        }

        @Override
        public void saveAddressBook(ReadOnlyAddressBook addressBook) {
            throw new UnsupportedOperationException();
        }
    }
}
