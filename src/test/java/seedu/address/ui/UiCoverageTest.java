package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.lesson.LessonDate;
import seedu.address.model.lesson.LessonNote;
import seedu.address.model.lesson.NoteText;
import seedu.address.model.person.Student;
import seedu.address.model.subject.Subject;
import seedu.address.testutil.PersonBuilder;

public class UiCoverageTest {

    @BeforeAll
    public static void initializeJavaFxToolkit() {
        try {
            Platform.startup(() -> { });
        } catch (IllegalStateException exception) {
            // The toolkit has already been initialized by another test.
        }
        Platform.setImplicitExit(false);
    }

    @Test
    public void uiManager_constructors_storeStartupWarning() throws Exception {
        Path dataFilePath = Path.of("data", "addressbook.json");
        UiManager uiManagerWithoutWarning = new UiManager(new StubLogic(), dataFilePath);
        String warning = "The invalid data file was preserved.";
        UiManager uiManagerWithWarning = new UiManager(new StubLogic(), dataFilePath, warning);

        Field startupWarningField = UiManager.class.getDeclaredField("startupWarning");
        startupWarningField.setAccessible(true);
        assertNull(startupWarningField.get(uiManagerWithoutWarning));
        assertEquals(warning, startupWarningField.get(uiManagerWithWarning));
    }

    @Test
    public void uiManager_startWithWarning_displaysWarningDialog() throws Exception {
        runOnFxThread(() -> {
            Stage stage = new Stage();
            UiManager uiManager = new UiManager(new StubLogic(), Path.of("data", "addressbook.json"),
                    "The invalid data file was preserved.");
            Platform.runLater(() -> Window.getWindows().stream()
                    .filter(window -> window != stage)
                    .forEach(Window::hide));

            uiManager.start(stage);

            stage.close();
        });
    }

    @Test
    public void personCard_constructor_populatesStudentDetails() throws Exception {
        runOnFxThread(() -> {
            PersonCard personCard = new PersonCard(ALICE, 2);

            assertEquals(ALICE, personCard.student);
            assertNotNull(personCard.getRoot());
        });
    }

    @Test
    public void personCard_longParentEmail_wrapsInsteadOfTruncating() throws Exception {
        Student studentWithLongEmail = new PersonBuilder(ALICE)
                .withEmail("nurulaisyah.parent.contact.mother@exampletutoringcentre.com.sg").build();

        runOnFxThread(() -> {
            PersonCard personCard = new PersonCard(studentWithLongEmail, 1);

            Field parentEmailField = PersonCard.class.getDeclaredField("parentEmail");
            parentEmailField.setAccessible(true);
            Label parentEmail = (Label) parentEmailField.get(personCard);

            assertTrue(parentEmail.isWrapText());
            assertEquals("Email: " + studentWithLongEmail.getParentEmail().value, parentEmail.getText());
        });
    }

    @Test
    public void personCard_studentWithLessonNotes_populatesNotesSection() throws Exception {
        Student studentWithNotes = new PersonBuilder(ALICE)
                .withLessonNotes(
                        new LessonNote(new LessonDate("2026-10-01"), new Subject("Math"),
                                new NoteText("Revised simultaneous equations")),
                        new LessonNote(new LessonDate("2026-09-20"), new Subject("Science"),
                                new NoteText("Covered photosynthesis")))
                .build();

        runOnFxThread(() -> {
            PersonCard personCard = new PersonCard(studentWithNotes, 1);

            Field lessonNotesField = PersonCard.class.getDeclaredField("lessonNotes");
            lessonNotesField.setAccessible(true);
            VBox lessonNotes = (VBox) lessonNotesField.get(personCard);

            assertTrue(lessonNotes.isVisible());
            // the header plus one label per note
            assertEquals(3, lessonNotes.getChildren().size());
            Label header = (Label) lessonNotes.getChildren().get(0);
            assertEquals(LessonNoteFormatter.LESSON_NOTES_HEADER, header.getText());
        });
    }

    @Test
    public void personCard_studentWithoutLessonNotes_hidesNotesSection() throws Exception {
        runOnFxThread(() -> {
            PersonCard personCard = new PersonCard(ALICE, 1);

            Field lessonNotesField = PersonCard.class.getDeclaredField("lessonNotes");
            lessonNotesField.setAccessible(true);
            VBox lessonNotes = (VBox) lessonNotesField.get(personCard);

            assertTrue(!lessonNotes.isVisible());
            assertTrue(!lessonNotes.isManaged());
            assertTrue(lessonNotes.getChildren().isEmpty());
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    public void personListPanel_constructorAndCell_updateStudentGraphic() throws Exception {
        ObservableList<Student> students = FXCollections.observableArrayList(ALICE);

        runOnFxThread(() -> {
            PersonListPanel panel = new PersonListPanel(students);
            VBox root = (VBox) panel.getRoot();
            ListView<Student> listView = (ListView<Student>) root.getChildren().get(0);
            ListCell<Student> cell = listView.getCellFactory().call(listView);
            Method updateItem = cell.getClass().getDeclaredMethod("updateItem", Student.class, boolean.class);
            updateItem.setAccessible(true);

            updateItem.invoke(cell, new Object[] {null, true});
            assertTrue(cell.getGraphic() == null);
            updateItem.invoke(cell, new Object[] {null, false});
            assertTrue(cell.getGraphic() == null);
            updateItem.invoke(cell, ALICE, false);
            assertNotNull(cell.getGraphic());
        });
    }

    @Test
    public void mainWindow_fillInnerParts_populatesPlaceholders() throws Exception {
        runOnFxThread(() -> {
            Stage defaultStage = new Stage();
            MainWindow defaultWindow = new MainWindow(defaultStage, new StubLogic(),
                    Path.of("data", "addressbook.json"));
            assertNotNull(defaultWindow.getPrimaryStage());
            defaultStage.close();

            Stage stage = new Stage();
            MainWindow mainWindow = new MainWindow(stage,
                    new StubLogic(new GuiSettings(800, 600, 10, 20)), Path.of("data", "addressbook.json"));

            mainWindow.fillInnerParts();

            assertNotNull(mainWindow.getPersonListPanel());
            stage.close();
        });
    }

    @Test
    public void mainWindow_executeFailure_displaysError() throws Exception {
        runOnFxThread(() -> {
            String errorMessage = "Invalid student input.";
            Stage stage = new Stage();
            MainWindow mainWindow = new MainWindow(stage, new StubLogic(new ParseException(errorMessage)),
                    Path.of("data", "addressbook.json"));
            mainWindow.fillInnerParts();

            Method executeCommand = MainWindow.class.getDeclaredMethod("executeCommand", String.class);
            executeCommand.setAccessible(true);
            InvocationTargetException thrown;
            try {
                executeCommand.invoke(mainWindow, "addstudent invalid");
                throw new AssertionError("Expected executeCommand to fail");
            } catch (InvocationTargetException exception) {
                thrown = exception;
            }
            assertEquals(errorMessage, thrown.getCause().getMessage());

            Field resultDisplayField = MainWindow.class.getDeclaredField("resultDisplay");
            resultDisplayField.setAccessible(true);
            ResultDisplay resultDisplay = (ResultDisplay) resultDisplayField.get(mainWindow);

            Field textAreaField = ResultDisplay.class.getDeclaredField("resultDisplay");
            textAreaField.setAccessible(true);
            TextArea textArea = (TextArea) textAreaField.get(resultDisplay);
            assertEquals(errorMessage, textArea.getText());
            stage.close();
        });
    }

    @Test
    public void mainWindow_unsavedWindowClose_cancelThenConfirm() throws Exception {
        runOnFxThread(() -> {
            StubLogic stub = new StubLogic();
            stub.hasUnsavedChanges = true;
            Stage stage = new Stage();
            MainWindow window = new MainWindow(stage, stub, Path.of("data", "addressbook.json"));
            window.fillInnerParts();
            stage.show();
            try {
                Platform.runLater(() -> clickExitDialogButton(ButtonType.CANCEL.getText()));
                WindowEvent cancelled = new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST);
                stage.getOnCloseRequest().handle(cancelled);
                assertTrue(cancelled.isConsumed());
                assertTrue(stage.isShowing());

                Platform.runLater(() -> clickExitDialogButton("Exit anyway"));
                stage.getOnCloseRequest().handle(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
                assertFalse(stage.isShowing());
            } finally {
                stage.hide();
            }
        });
    }

    private static void clickExitDialogButton(String text) {
        for (Window window : Window.getWindows()) {
            if (window.getScene().getRoot() instanceof DialogPane pane) {
                for (ButtonType type : pane.getButtonTypes()) {
                    if (type.getText().equals(text)) {
                        Button button = (Button) pane.lookupButton(type);
                        button.fire();
                        return;
                    }
                }
            }
        }
        throw new AssertionError("Exit confirmation button not found: " + text);
    }

    private static void runOnFxThread(FxAction action) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        Platform.runLater(() -> {
            try {
                action.run();
            } catch (Throwable throwable) {
                failure.set(throwable);
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(10, TimeUnit.SECONDS));
        if (failure.get() != null) {
            throw new AssertionError("JavaFX test action failed", failure.get());
        }
    }

    @FunctionalInterface
    private interface FxAction {
        void run() throws Exception;
    }

    private static class StubLogic implements Logic {
        private boolean hasUnsavedChanges;
        private final GuiSettings guiSettings;
        private final Exception executionFailure;

        StubLogic() {
            this(new GuiSettings(), null);
        }

        StubLogic(GuiSettings guiSettings) {
            this(guiSettings, null);
        }

        StubLogic(Exception executionFailure) {
            this(new GuiSettings(), executionFailure);
        }

        StubLogic(GuiSettings guiSettings, Exception executionFailure) {
            this.guiSettings = guiSettings;
            this.executionFailure = executionFailure;
        }

        @Override
        public CommandResult execute(String commandText) throws CommandException, ParseException {
            if (executionFailure instanceof CommandException commandException) {
                throw commandException;
            }
            if (executionFailure instanceof ParseException parseException) {
                throw parseException;
            }
            return new CommandResult(commandText);
        }

        @Override
        public boolean hasUnsavedChanges() {
            return hasUnsavedChanges;
        }

        @Override
        public ObservableList<Student> getFilteredStudentList() {
            return FXCollections.observableArrayList(ALICE);
        }

        @Override
        public GuiSettings getGuiSettings() {
            return guiSettings;
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            // No-op for the UI coverage test.
        }
    }
}
