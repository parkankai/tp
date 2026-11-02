package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.FileUtil;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.ReadOnlyAddressBook;

/**
 * A class to access AddressBook data stored as a JSON file on the hard disk.
 */
public class JsonAddressBookStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonAddressBookStorage.class);

    private Path filePath;
    private boolean isWriteBlocked;

    public JsonAddressBookStorage(Path filePath) {
        this.filePath = filePath;
    }

    public Path getAddressBookFilePath() {
        return filePath;
    }

    /**
     * Returns AddressBook data as a {@link ReadOnlyAddressBook}.
     * Returns {@code Optional.empty()} if storage file is not found.
     *
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException {
        try {
            return readAddressBook(filePath);
        } catch (DataLoadingException e) {
            try {
                Path backupPath = getNextCorruptedFilePath(filePath);
                preserveInvalidData(filePath, backupPath);
                isWriteBlocked = false;
                logger.warning("Invalid data file preserved at " + backupPath);
                throw new DataLoadingException(e, backupPath);
            } catch (IOException backupException) {
                isWriteBlocked = true;
                logger.warning("Could not preserve invalid data file at " + filePath + ": "
                        + backupException.getMessage());
                throw e;
            }
        }
    }

    /**
     * Similar to {@link #readAddressBook()}.
     *
     * @param filePath location of the data. Cannot be null.
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyAddressBook> readAddressBook(Path filePath) throws DataLoadingException {
        requireNonNull(filePath);

        Optional<JsonSerializableAddressBook> jsonAddressBook = JsonUtil.readJsonFileStrict(
                filePath, JsonSerializableAddressBook.class);
        if (!jsonAddressBook.isPresent()) {
            return Optional.empty();
        }

        try {
            return Optional.of(jsonAddressBook.get().toModelType());
        } catch (IllegalValueException ive) {
            logger.info("Illegal values found in " + filePath + ": " + ive.getMessage());
            throw new DataLoadingException(ive);
        }
    }

    /** Moves invalid data aside so that a fresh address book can be created safely. */
    void preserveInvalidData(Path source, Path backupPath) throws IOException {
        Files.move(source, backupPath);
    }

    private Path getNextCorruptedFilePath(Path originalPath) {
        String fileName = originalPath.getFileName().toString();
        int extensionIndex = fileName.lastIndexOf('.');
        String baseName = extensionIndex < 0 ? fileName : fileName.substring(0, extensionIndex);
        String extension = extensionIndex < 0 ? "" : fileName.substring(extensionIndex);
        Path parent = originalPath.getParent();

        for (int index = 1; ; index++) {
            Path candidate = Path.of(baseName + ".corrupted-" + index + extension);
            candidate = parent == null ? candidate : parent.resolve(candidate);
            if (!Files.exists(candidate)) {
                return candidate;
            }
        }
    }

    /**
     * Saves the given {@link ReadOnlyAddressBook} to the storage.
     * @param addressBook cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
        saveAddressBook(addressBook, filePath);
    }

    /**
     * Similar to {@link #saveAddressBook(ReadOnlyAddressBook)}.
     *
     * @param filePath location of the data. Cannot be null.
     */
    public void saveAddressBook(ReadOnlyAddressBook addressBook, Path filePath) throws IOException {
        requireNonNull(addressBook);
        requireNonNull(filePath);
        if (isWriteBlocked && this.filePath.equals(filePath)) {
            throw new IOException("Refusing to overwrite an invalid data file that could not be preserved");
        }

        FileUtil.createIfMissing(filePath);
        JsonUtil.saveJsonFile(new JsonSerializableAddressBook(addressBook), filePath);
    }

}
