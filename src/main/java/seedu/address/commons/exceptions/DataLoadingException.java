package seedu.address.commons.exceptions;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Represents an error during loading of data from a file.
 */
public class DataLoadingException extends Exception {
    private final Path backupPath;

    public DataLoadingException(Exception cause) {
        this(cause, null);
    }

    /**
     * Creates an exception with the path where the invalid data was preserved.
     */
    public DataLoadingException(Exception cause, Path backupPath) {
        super(cause);
        this.backupPath = backupPath;
    }

    /** Returns the location of the preserved invalid data file, if one was created. */
    public Optional<Path> getBackupPath() {
        return Optional.ofNullable(backupPath);
    }

}
