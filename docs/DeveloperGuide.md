---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# TutorBroPro Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* This project is based on the
  [AddressBook-Level3 project](https://github.com/se-edu/addressbook-level3)
  created by the [SE-EDU initiative](https://se-education.org/). We retain
  its overall component structure and adapt it for managing students and
  lesson notes.
* The project uses [JavaFX](https://openjfx.io/) for the graphical interface,
  [Jackson](https://github.com/FasterXML/jackson) for JSON serialization, and
  [JUnit 5](https://junit.org/junit5/) for automated testing.

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

The following provides a quick overview of the main components and their interactions.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/AY2627S1-CS2103T-F11-4/tp/blob/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/AY2627S1-CS2103T-F11-4/tp/blob/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shutdown, it saves the current user preferences.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `deletestudent 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* provides its functionality through a concrete `{Component Name}Manager` class that implements the corresponding API interface.

For example, the `Logic` component defines its API in `Logic.java` and implements it in `LogicManager.java`. Other components interact with a component through its interface rather than its concrete class, preventing them from coupling to that component's implementation, as illustrated in the following partial class diagram.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2627S1-CS2103T-F11-4/tp/blob/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `PersonListPanel`, and `StatusBarFooter`. All of these, including `MainWindow`, inherit from the abstract `UiPart` class, which captures common behavior among classes that represent visible GUI parts.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/AY2627S1-CS2103T-F11-4/tp/blob/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/AY2627S1-CS2103T-F11-4/tp/blob/master/src/main/java/seedu/address/ui/MainWindow.java).

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component because it displays `Student` objects from the model.

### Logic component

**API** : [`Logic.java`](https://github.com/AY2627S1-CS2103T-F11-4/tp/blob/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking a successful `execute("deletestudent 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `deletestudent 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>


How the `Logic` component works:

1. When `Logic` is called upon to execute a command, the command is passed to an `AddressBookParser` object, which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command retrieves the currently displayed student list from the `Model`. For a valid displayed index, it selects the student, counts their lesson notes, and calls `Model#deleteStudent(studentToDelete)`. The student object owns its lesson notes, so no separate note-deletion call is needed.
1. The deletion success message includes the student's name and the number of associated lesson notes removed. The diagram shows the successful path; invalid indices are rejected before deletion.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddCommandParser`). The parser uses the other classes shown above to parse the user command and create an `XYZCommand` object (e.g., `AddCommand`). The `AddressBookParser` returns that object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddCommandParser` and `DeleteCommandParser`, implement the `Parser` interface so they can be treated similarly where appropriate, for example during testing.

### Model component
**API** : [`Model.java`](https://github.com/AY2627S1-CS2103T-F11-4/tp/blob/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Student` objects (which are contained in a `UniqueStudentList` object).
* stores the `Student` objects selected by the current filter, such as search results, in a separate _filtered_ list. It exposes this list as an unmodifiable `ObservableList<Student>` that the UI can observe and bind to, so the UI updates when the list changes.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)
### Storage component

**API** : [`Storage.java`](https://github.com/AY2627S1-CS2103T-F11-4/tp/blob/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonAddressBookStorage` and `JsonUserPrefsStorage` (one class per data file).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Clear command safeguard

The `clear` command rejects an index or other arguments before any data is removed.
This is an additional precaution for a destructive command: a tutor intending to delete the student
at displayed index 2 might confuse `deletestudent 2` with `clear 2`. Rejecting `clear 2` prevents that
mistake from deleting every student and their lesson notes. The tutor must use `deletestudent 2`
to delete only that student. Plain `clear` still removes all students without a confirmation prompt.

### View student feature

The following sequence diagram shows how the `Logic` component handles the `view 2` command to retrieve and display the full profile of the second student in the currently displayed list.

<puml src="diagrams/ViewSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `view 2` Command" />

<box type="info" seamless>

**Note:** The lifeline for `ViewCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

### Editing a student profile

The following sequence diagram shows a successful student-profile edit, such as `edit 1 lvl/Sec 3`.
`commandText` represents the full command and `arguments` represents the text after `edit`.

<puml src="diagrams/EditStudentSequenceDiagram.puml" alt="Sequence for successfully editing a student profile" />

* `EditCommandParser` parses the displayed index and supplied fields into an `EditPersonDescriptor`, then creates an `EditCommand`.
* `EditCommand` selects the student from the currently displayed list and checks that the resulting subject set is nonempty before constructing the updated student. Omitted fields and existing lesson notes are preserved.
* If the student's identity changes (name or parent phone), the command checks for a duplicate in the full address book. The successful path shown assumes no duplicate exists; an academic-level-only edit skips this check.
* The command updates the student, restores the full displayed list, and returns a success message containing the updated profile. `LogicManager` saves the data before returning the result; storage interactions are omitted from this Logic-focused diagram.
* Rejected requests do not reach the model update. Invalid input produces a parsing error, while an invalid displayed index, empty subject replacement supplied directly to the command, or duplicate student produces a command error. These failure paths are omitted for readability.

### Filtering students

The `filter` command narrows the displayed student list to students who match an academic level, a set of subjects, or both. The sequence diagram below shows how `filter s/Math lvl/Sec 2` is parsed, applied to the model, and saved through the normal command-execution flow.

<puml src="diagrams/FilterSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `filter s/Math lvl/Sec 2` Command" />

* `FilterCommandParser` collects every `s/` value and at most one `lvl/` value. It rejects the command when neither is supplied, when text appears before the first prefix, or when `lvl/` is repeated.
* The parsed values become a `StudentMatchesFilterPredicate`, which holds an `Optional<AcademicLevel>` and a `Set<Subject>`. Keeping the criteria in one predicate object means `FilterCommand` does not need to know how matching works, and lets the predicate be compared in tests.
* `StudentMatchesFilterPredicate` matches a student when the level is absent or equal, **and** the student's subjects contain every required subject. Subject names are compared case-insensitively after consecutive spaces are normalized, so a student may take more subjects than were asked for, but not fewer.
* `FilterCommand` passes the predicate to `Model#updateFilteredStudentList(Predicate)` and then reports how many students matched. When nothing matches it returns `MESSAGE_NO_STUDENTS_FOUND` instead of a count.
* The predicate stays applied until another command replaces it, so later commands act on the filtered list. `list` clears it.
* Failure paths are omitted from the diagram for readability: an invalid level or subject produces a parsing error before any `FilterCommand` is created.

#### Design considerations: filtering students

**Aspect: How multiple subjects are combined:**

* **Alternative 1 (current choice):** A student must take every subject supplied.
  * Pros: Answers the question a tutor actually asks, such as "who do I teach both Math and Physics".
  * Cons: Supplying several subjects quickly narrows the list to nothing, which can look like a broken filter.

* **Alternative 2:** Match students taking any of the supplied subjects.
  * Pros: Easier to get a non-empty result.
  * Cons: Adding a subject widens the result, which reads backwards for a filter, and the same list can be produced by running `filter` once per subject.

**Aspect: What to display when nothing matches:**

* **Alternative 1 (current choice):** Apply the predicate anyway, leaving an empty list.
  * Pros: The displayed list always reflects the last command, so the index shown never disagrees with what a following command will act on.
  * Cons: A filter that matches nothing leaves the tutor looking at an empty list, with no reminder of what they were looking at before.

* **Alternative 2:** Leave the previous list displayed when nothing matches.
  * Pros: The tutor keeps their place after a filter that matched nothing.
  * Cons: The displayed list no longer corresponds to the command just run, and a following index-based command would act on students the tutor was not looking at.

### Managing lesson notes

`addnote`, `editnote` and `deletenote` all change the lesson notes of one student, and share the same shape. The sequence diagram below traces `addnote` through the `Logic` component; the diagram later in this section covers the failure paths of `deletenote`.

<puml src="diagrams/AddNoteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `addnote 1 d/2026-10-01 s/Math note/...` Command" />

Each command follows the same four steps:

1. Resolve the student from `Model#getFilteredStudentList()` using the displayed `INDEX`, failing with an invalid-index error if it is out of range.
2. Resolve the lesson note, where one is addressed, using the 1-based `ln/LESSON_INDEX` into that student's note list.
3. Copy the note list, because `Student#getLessonNotes()` returns an unmodifiable list, and apply the change to the copy.
4. Build a replacement `Student` carrying the other fields unchanged, and hand it to `Model#setStudent(Student, Student)`.

A `Student` is immutable, so there is no way to add, edit or remove a note in place. Every one of these commands therefore constructs a new `Student`.

The commands differ only in step 3:

* **`addnote`** requires all of `d/`, `s/` and `note/`. It resolves the subject against the student's current subjects, ignoring letter case, so the note stores the spelling used in the student's profile. It then builds and appends a `LessonNote`. It first rejects a note the student already has: `LessonNote#equals` compares date, subject and text, so a repeat of all three is refused while the same date and subject with different text is allowed.
* **`editnote`** requires `ln/` and at least one of `d/`, `s/` and `note/`. It builds the replacement from the old note using `orElse` for each field, so omitted fields carry over, and replaces the note at that position. A newly supplied subject must match one of the student's current subjects; an omitted subject is retained even if it has since been removed from the profile. Like `addnote`, it rejects an edit that would duplicate another lesson note for the same student. It also rejects an edit whose resulting date, subject and text are all unchanged.
* **`deletenote`** removes the note at that position. It distinguishes two failures: a student with no notes at all gets `MESSAGE_NO_LESSON_NOTES`, while an out-of-range index on a student who does have notes gets `MESSAGE_INVALID_LESSON_NOTE_INDEX`.

`deletenote` is the only one of the three that rejects a request for two different reasons, so its failure paths are worth showing rather than omitting.

<puml src="diagrams/DeleteNoteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `deletenote 1 ln/2` Command" />

Two consequences of rebuilding the `Student` are worth knowing:

* The `Student` constructor sorts lesson notes from the most recent date to the earliest, so the note numbering that `history` prints and that `ln/` accepts is recomputed on every change. Deleting a note renumbers the notes after it, and editing a note's date can move it to a different position.
* None of the three changes the displayed student list, so a filter applied by `find` or `filter` survives the command and the student stays on screen.

#### Editing a lesson note

The following sequence diagram shows successful execution of
`EditNoteCommand` after parsing and field validation. Collection operations
and simple getters are omitted for readability.

<puml src="diagrams/EditNoteSequenceDiagram.puml" alt="Sequence diagram for successfully editing a lesson note" />

Omitted fields retain their existing values. Rebuilding the student sorts
the notes by date, so changing a date may change the edited note's history
index. The current student filter is preserved.

After the command returns, `LogicManager` saves the updated address book
through storage. This persistence step is outside the diagram's scope.

#### Design considerations: lesson notes

**Aspect: How a lesson note is addressed:**

* **Alternative 1 (current choice):** By its position in the student's note list, as printed by `history`.
  * Pros: Nothing extra to store or show, and it matches how students are addressed everywhere else in the app.
  * Cons: Positions shift after a delete or a date edit, so a `history` output taken earlier can point at the wrong note.

* **Alternative 2:** Give each note an identifier that is stored with it and never changes.
  * Pros: A reference stays valid for the life of the note.
  * Cons: Another field to persist and display, and an opaque identifier is harder for a tutor to type than a position.

**Aspect: Where lesson notes are sorted:**

* **Alternative 1 (current choice):** The `Student` constructor sorts them.
  * Pros: Every reader, including the card, `history` and the note commands, sees the same order without repeating the sort.
  * Cons: Sorting runs on every `Student` construction, and an edit to a date silently renumbers the notes.

* **Alternative 2:** Keep insertion order and sort where notes are displayed.
  * Pros: Construction stays cheap, and positions change only when a note is removed.
  * Cons: Each display site has to sort, and two sites that disagree would show different numbering for the same note.

--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* requires a platform to manage a significant number of students
* requires information linked to the student to be clear and easy to update
* comfortable with CLI apps
* can type fast
* appreciates a lightweight, offline program

**Value proposition**: Manage students and associated information intuitively.


### User Stories

#### Student Management

* **As a tutor, I want to add a student profile, so that I can keep the student's academic and contact information in one place.**
    * Priority: Must have

* **As a tutor, I want to view a student's profile, so that I can quickly retrieve the information needed before a lesson.**
    * Priority: Must have

* **As a tutor, I want to edit a student's profile, so that I can keep their academic and contact information up to date.**
    * Priority: Must have

* **As a tutor, I want to delete a student profile, so that I can remove outdated or incorrect records.**
    * Priority: Must have

#### Lesson Records

* **As a tutor, I want to add a dated lesson note for a student, so that I can keep track of what was taught during each lesson.**
    * Priority: Must have

* **As a tutor, I want to view a student's lesson history, so that I can review the student's learning progress over time.**
    * Priority: Must have

* **As a tutor, I want to delete an incorrect lesson note, so that the student's records remain accurate.**
    * Priority: Must have

* **As a tutor, I want to edit a lesson note, so that I can correct inaccurate information in a previous lesson record.**
    * Priority: Should have

#### Organisation and Retrieval

* **As a tutor, I want to search for a student, so that I can find the correct profile quickly.**
    * Priority: Should have

* **As a tutor, I want to filter students by academic level and subject, so that I can manage a large number of student profiles more efficiently.**
    * Priority: Should have

### Use cases

(For all use cases below, the **System** is `TutorBroPro` and the **Actor** is the `tutor`, unless specified otherwise.)

The following rules apply to these use cases:

* Each name search or filter starts from all students, replacing the displayed results rather than narrowing a previous search or filter.
* Student selections refer to indices in the currently displayed student list. Lesson-note selections refer to indices in the target student's current lesson history.
* When an extension resumes at a request step, the tutor resubmits the request and TutorBroPro validates the entire request again. The system does not retain a partially completed command.

**Use case: UC01 - Add a student**

**Guarantees:** A student profile is created only if all the details are valid and no existing student has the same name and parent phone number. An invalid request leaves existing profiles unchanged.

**MSS**

1. Tutor requests to add a student, providing the student's details.
2. TutorBroPro adds the student and shows the updated list of students.

   Use case ends.

**Extensions**

* 1a. TutorBroPro detects that a required detail is missing or invalid.

  * 1a1. TutorBroPro informs the tutor of the problem.

  Use case resumes from step 1.

* 1b. TutorBroPro detects that a student with the same name and parent phone number already exists.

  * 1b1. TutorBroPro informs the tutor that the student already exists.

  Use case ends.

**Use case: UC02 - View a student's profile**

**MSS**

1. Tutor requests to find students by name, providing one or more search keywords.
2. TutorBroPro shows a list of matching students.
3. Tutor requests to view the profile of a specific student in the list.
4. TutorBroPro shows the student's full profile.

   Use case ends.

**Extensions**

* 1a. TutorBroPro detects that no search keywords were provided.

  * 1a1. TutorBroPro informs the tutor that at least one keyword is required.

  Use case resumes from step 1.

* 2a. No students match the keywords.

  * 2a1. TutorBroPro displays an empty student list and reports that no students were found.

  Use case ends.

* 3a. TutorBroPro detects that the specified student does not exist in the displayed list.

  * 3a1. TutorBroPro informs the tutor that the student selection is invalid.

  Use case resumes from step 3.

**Use case: UC03 - Add a lesson note**

**Preconditions:** A student list is displayed.

**Guarantees:** A lesson note is added only if all the details are valid. Existing lesson notes are not changed.

**MSS**

1. Tutor requests to add a lesson note to a specific student in the displayed list and provides the details of the lesson.
2. TutorBroPro adds the lesson note to the student's lesson history and confirms the addition.

   Use case ends.

**Extensions**

* 1a. TutorBroPro detects that the specified student does not exist in the displayed list.

  * 1a1. TutorBroPro informs the tutor that the student selection is invalid.

  Use case resumes from step 1.

* 1b. TutorBroPro detects that a lesson detail is missing or invalid.

  * 1b1. TutorBroPro informs the tutor of the missing or invalid detail.

  Use case resumes from step 1.

* 1c. TutorBroPro detects that the supplied subject is not in the student's current subject list.

  * 1c1. TutorBroPro informs the tutor that the student does not take that subject.

  Use case resumes from step 1.

**Use case: UC04 - View a student's lesson history**

**Preconditions:** A student list is displayed.

**MSS**

1. Tutor requests to view the lesson history of a specific student in the displayed list.
2. TutorBroPro shows the student's lesson notes, starting from the most recent.

   Use case ends.

**Extensions**

* 1a. TutorBroPro detects that the specified student does not exist in the displayed list.

  * 1a1. TutorBroPro informs the tutor that the student selection is invalid.

  Use case resumes from step 1.

* 1b. The student has no lesson notes.

  * 1b1. TutorBroPro informs the tutor that no lesson notes have been recorded for the student.

  Use case ends.

**Use case: UC05 - Find students by name**

**Guarantees:** Student profiles and lesson notes remain unchanged. An invalid request leaves the displayed student list unchanged.

**MSS**

1. Tutor requests to find students, providing one or more search keywords.
2. TutorBroPro searches the full student list and displays students with a name component that starts with any supplied keyword, ignoring letter case.

   Use case ends.

**Extensions**

* 1a. TutorBroPro detects that no search keywords were provided.

  * 1a1. TutorBroPro informs the tutor that at least one keyword is required.

  Use case resumes from step 1.

* 2a. No students match the keywords.

  * 2a1. TutorBroPro displays an empty student list and reports that no students were found.

  Use case ends.

**Use case: UC06 - Filter students by academic level or subject**

**Guarantees:** Student profiles and lesson notes remain unchanged. An invalid request leaves the displayed student list unchanged.

**Filtering rules:** All supplied criteria must match. When multiple subjects are supplied, a student must take every specified subject but may also take other subjects. If an academic level is supplied, the student must also match that level. Subject matching ignores letter case and compares complete subject names.

For example, filtering for Math includes students taking Math and Science. Filtering for Sec 2, Math and Science includes only Sec 2 students taking both subjects, including those taking additional subjects.

**MSS**

1. Tutor requests to filter students, specifying an academic level, one or more subjects, or both.
2. TutorBroPro searches the full student list and displays students matching all the specified criteria.

   Use case ends.

**Extensions**

* 1a. TutorBroPro detects that no filtering criteria were provided.

  * 1a1. TutorBroPro informs the tutor that at least one academic-level or subject criterion is required.

  Use case resumes from step 1.

* 1b. TutorBroPro detects an invalid academic level or subject.

  * 1b1. TutorBroPro informs the tutor of the invalid criterion.

  Use case resumes from step 1.

* 2a. No students match the criteria.

  * 2a1. TutorBroPro displays an empty student list and indicates that there are no matching students.

  Use case ends.

**Use case: UC07 - Edit a student's profile**

**Preconditions:** A student list is displayed.

**Guarantees:** An invalid request leaves all student profiles unchanged. A successful edit changes only the specified student's requested profile fields; their lesson notes remain attached and unchanged. The student retains at least one subject.

**Subject-editing rules:** Omitting subjects from an edit leaves the existing subjects unchanged. Supplying subjects replaces the entire subject list, so the tutor must supply all subjects they want to retain. An empty replacement subject list is invalid and leaves the entire profile unchanged.

**MSS**

1. Tutor requests to edit a student in the displayed list, specifying the student and the profile details to change.
2. TutorBroPro updates the student's profile, displays the full student list, and confirms the updated details.

   Use case ends.

**Extensions**

* 1a. TutorBroPro detects that the specified student does not exist in the displayed list.

  * 1a1. TutorBroPro informs the tutor that the student selection is invalid.

  Use case resumes from step 1.

* 1b. TutorBroPro detects that no changes were specified.

  * 1b1. TutorBroPro informs the tutor that at least one profile field must be provided for editing.

  Use case resumes from step 1.

* 1c. TutorBroPro detects an invalid profile detail, including a replacement subject list containing no subjects.

  * 1c1. TutorBroPro informs the tutor of the invalid detail and, if the subject list is empty, explains that at least one subject is required.

  Use case resumes from step 1.

* 1d. TutorBroPro detects that the edited profile has the same name and parent phone number as another student.

  * 1d1. TutorBroPro informs the tutor that another student already has that name and parent phone number.

  Use case resumes from step 1.

**Use case: UC08 - Delete a student**

**Preconditions:** A student list is displayed.

**Guarantees:** A successful deletion removes the specified student and all their associated lesson notes. Other students and their notes remain unchanged. An invalid request deletes nothing.

Deletion is immediate, without a confirmation prompt or an undo operation.

**MSS**

1. Tutor requests to delete a student from the displayed list.
2. TutorBroPro deletes the student and all their associated lesson notes.
3. TutorBroPro updates the displayed student list, clears the deleted student's profile if it was being shown, and confirms the deletion, including the number of lesson notes removed.

   Use case ends.

**Extensions**

* 1a. TutorBroPro detects that the specified student does not exist in the displayed list.

  * 1a1. TutorBroPro informs the tutor that the student selection is invalid.

  Use case resumes from step 1.

**Use case: UC09 - Edit a lesson note**

**Preconditions:** A student list is displayed.

**Guarantees:** An invalid request leaves all lesson notes unchanged. A successful edit changes only the specified note's requested fields; other notes and the student's profile remain unchanged.

**MSS**

1. Tutor requests to edit a lesson note, specifying the student, the note, and the lesson details to change.
2. TutorBroPro updates the note and reorders the student's lesson history if the lesson date changed.
3. TutorBroPro confirms that the note was updated.

   Use case ends.

**Extensions**

* 1a. TutorBroPro detects that the specified student or lesson note is invalid.

  * 1a1. TutorBroPro informs the tutor of the invalid selection.

  Use case resumes from step 1.

* 1b. TutorBroPro detects that no changes were specified.

  * 1b1. TutorBroPro informs the tutor that at least one lesson-note field must be provided for editing.

  Use case resumes from step 1.

* 1c. TutorBroPro detects an invalid lesson detail, such as an invalid or future date, a subject that the student does not currently take, or invalid note text.

  * 1c1. TutorBroPro informs the tutor of the invalid detail.

  Use case resumes from step 1.

* 1d. TutorBroPro detects that the resulting date, subject and note text are unchanged.

  * 1d1. TutorBroPro informs the tutor that no changes were made.

  Use case resumes from step 1.

**Use case: UC10 - Delete a lesson note**

**Preconditions:** A student list is displayed, and the target student has the relevant lesson-note index.

**Guarantees:** A successful deletion removes only the specified lesson note. The student's profile and other lesson notes remain unchanged. An invalid request deletes nothing.

Deletion is immediate, without a confirmation prompt or an undo operation.

**MSS**

1. Tutor requests to delete a lesson note, specifying the student and the note.
2. TutorBroPro deletes the note and updates the lesson history, renumbering the remaining entries.
3. TutorBroPro confirms that the note was deleted.

   Use case ends.

**Extensions**

* 1a. TutorBroPro detects that the specified student or lesson note is invalid.

  * 1a1. TutorBroPro informs the tutor of the invalid selection.

  Use case resumes from step 1.

### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `25` or above installed.
2.  Should be able to hold up to 1000 students without noticeable sluggishness in performance for typical usage.
3.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
4.  The graphical user interface should display its core components without truncation or overlap at resolutions of 1920×1080 and above, and all features should remain accessible at resolutions of 1280×720 and above.
5.  Student names should use permitted Unicode letters, accent marks, digits, spaces, apostrophes, hyphens and slashes; subjects should use letters and spaces; lesson-note text should be nonblank, contain no line breaks, and be at most 500 characters long.
6.  An invalid command should leave all stored student profiles and lesson notes unchanged and should display an error message stating what went wrong, including the correct command format where the command word is recognised.
7.  Student profiles and lesson notes should be saved after every successful data-modifying command. If the application terminates unexpectedly, the last successfully saved state should remain readable after restarting the application.
8.  If the data file is invalid after a manual edit, the application should not crash and should inform the user of the problem.
9.  Student and parent information should remain on the user’s computer and should not be transmitted to external services.

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, or macOS
* **Private contact detail**: A contact detail that is not meant to be shared with others
* **Tutor**: The user who manages student profiles and lesson records.
* **Student profile**: A record containing a student’s name, academic level, subjects, and parent or guardian contact details.
* **Academic level**: The student’s education level, such as `Pri 4`, `Sec 2`, or `JC 1`.
* **Subject**: An academic topic associated with a student or lesson note, such as Mathematics or Science.
* **Parent or guardian contact**: The phone number and email address used to contact a student’s parent or guardian.
* **Parent phone number**: An 8-digit phone number beginning with `8` or `9`, used to contact a student’s parent or guardian.
* **Duplicate student**: A student whose name and parent phone number both match those of an existing student.
* **Lesson note**: A dated record describing the subject and content covered during a lesson.
* **Lesson history**: The collection of lesson notes associated with a student.
* **Student index**: The number used to identify a student in the currently displayed list.
* **Lesson index**: The number used to identify a lesson note within a student’s lesson history.
* **Search keyword**: A word supplied by the tutor to search student names. Matching ignores letter case and checks whether a name component starts with the keyword; for example, `Ale`, `ale`, and `ALEX` all match `Alex Yeoh`.
* **Displayed list**: The list of students currently shown by the application, possibly after applying a search filter.
* **Data file**: The local text file used to store student profiles and lesson notes.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.
</box>

### Launch and shutdown

1. Initial launch

   1. Download the JAR file and copy it into an empty folder.

   1. From a terminal in the folder containing the JAR, run `java -jar addressbook.jar`.<br>
      Expected: The GUI opens with a set of sample students. The window size may not be optimal. If double-clicking the JAR is supported on the operating system, it may also be used.

1. Saving window preferences

   1. Resize the window to an optimal size. Move the window to a different location. Close the window.

   1. Relaunch the app with `java -jar addressbook.jar`.<br>
       Expected: The most recent window size and location are retained.

### Adding a student

1. Adding a student while all students are being shown

   1. Prerequisites: List all students using the `list` command.

   1. Test case: `addstudent n/John Doe lvl/Sec 2 s/Math pn/98765432 pe/johnd@example.com`<br>
      Expected: The student is added to the bottom of the list. The status message shows the new profile.

   1. Test case: repeat the same command.<br>
      Expected: No student is added. Two students count as the same when they share a name *and* a parent phone number, so the duplicate is rejected.

   1. Test case: `addstudent n/John Doe lvl/Sec 2 s/Math pn/91111111 pe/johnd@example.com`<br>
      Expected: The student is added. The name is shared but the parent phone differs, so this is not a duplicate.

1. Adding a student with invalid fields

   1. Test cases: `addstudent n/Amy Tan lvl/Sec 9 s/Math pn/91234567 pe/amy@example.com`, then the same command with `pn/12345678`, then with `s/M`.<br>
      Expected: Each is rejected and nothing is added. Levels must be `Pri 1`-`Pri 6`, `Sec 1`-`Sec 5` or `JC 1`-`JC 2`; parent phone numbers must be 8 digits starting with 8 or 9; subjects must be 2 to 30 letters and spaces.

   1. Test case: `addstudent n/Amy Tan lvl/Sec 2 pn/91234567 pe/amy@example.com`<br>
      Expected: Rejected. At least one subject is compulsory.

### Editing a student

1. Start with two students with different parent phone numbers. Give the first student a lesson note.
   Record both profiles and the note before testing.
1. Run `edit 1 lvl/Sec 3`. Only the first student's level changes; their other fields and notes remain unchanged.
1. Run `edit 1 s/Math s/Science`. The subjects become Math and Science, replacing the previous set.
1. Run `edit 1 n/Changed Name pn/abc`, then `edit 1 s/`. Each command reports an error and changes nothing.
1. Edit the first student's name to match the second student's name, keeping different phone numbers.
   The edit succeeds. Then try also matching the second student's phone; the duplicate edit is rejected.
1. Restore distinct student names, then use `find` to display only a student who was not first in the full list.
   Run `edit 1 lvl/Sec 4`.
   That displayed student is updated, and the full student list is shown afterward.
1. Restart the application from the same folder. Successful edits persist, rejected edits do not appear,
   and lesson notes and profiles match their state immediately before restarting.

### Deleting a student

1. Deleting a student while all students are being shown

   1. Prerequisites: List all students using the `list` command, with multiple students in the list.

   1. Test case: `deletestudent 1`<br>
      Expected: The first student is deleted from the list. The status message shows the deleted student's name and the number of lesson notes removed.

   1. Test case: `deletestudent 0`<br>
      Expected: No student is deleted. The status message shows error details.

   1. Other incorrect delete commands to try: `deletestudent`, `deletestudent x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. Deleting a student with lesson notes and checking persistence

   1. Prerequisites: Two students are displayed. The first student, Alex Yeoh, has two lesson notes; the second student also has notes. Record the second student's details and lesson history before testing.

   1. Test case: `deletestudent 1`<br>
      Expected: Alex and their notes are removed immediately without a confirmation prompt. The message is `Deleted student: Alex Yeoh, and their 2 lesson note(s).` The remaining student's details and notes are unchanged. Any previous profile or history output is replaced by the deletion message.

   1. Close and restart the application.<br>
      Expected: Alex does not reappear. View the remaining student's profile and history using their current displayed index; their details and notes are unchanged.

1. Deleting a student without lesson notes

   1. Prerequisites: The first displayed student has no lesson notes.

   1. Test case: `deletestudent 1`<br>
      Expected: The student is removed and the success message reports `0 lesson note(s)`.

1. Deleting from search results

   1. Prerequisites: At least two students exist. Search by a unique name for a student who is not first in the full list, so that the student becomes index 1 in the results.

   1. Test case: `deletestudent 1`<br>
      Expected: The student shown in the search results is removed, rather than the original first student. Other students and their notes remain unchanged.

### Clearing all entries

1. Rejecting an accidental indexed clear

   1. Prerequisites: At least two students are displayed.

   1. Test case: `clear 2`<br>
      Expected: The command is rejected and all students and lesson notes remain unchanged.

1. Clearing the address book

   1. Test case: `clear`<br>
      Expected: All students and their lesson notes are removed immediately, without a confirmation prompt. The change is saved to `data/addressbook.json`.

   1. Restart the application.<br>
      Expected: The student list remains empty.


### Viewing a student's profile

1. Prerequisites: At least one student exists, with at least one of them having lesson notes.

1. Test case: `view 1`<br>
   Expected: The result box shows the first student's name, level, subjects, parent phone, parent email and a count of lesson notes recorded. The displayed list is unchanged.

1. Test case: `view 0`, then `view x` where x is larger than the list size.<br>
   Expected: No profile is shown and the status message reports an invalid index.

### Finding and filtering students

1. Finding by name

   1. Prerequisites: List all students using the `list` command, with at least two students having different names.

   1. Test case: `find` followed by the first name of one student.<br>
      Expected: Only matching students are displayed and the status message reports how many were listed.

   1. Test case: `find zzzz` (matching nobody).<br>
      Expected: The displayed list becomes empty and the status message says no matching students were found. Run `list` to bring the students back.

1. Filtering by subject and level

   1. Prerequisites: At least one student takes Math at `Sec 2`, and at least one does not.

   1. Test case: `filter s/Math`<br>
      Expected: Only students taking Math are displayed.

   1. Test case: `filter s/Math lvl/Sec 2`<br>
      Expected: Only students who are `Sec 2` *and* take Math are displayed. A student must match the level and take every subject listed.

   1. Test case: `filter`<br>
      Expected: Rejected. At least one subject or level must be supplied.

1. Restoring the full list

   1. Test case: run `list` after any `find` or `filter`.<br>
      Expected: All students are displayed again, and displayed indexes return to their full-list positions.

### Managing lesson notes

1. Adding and viewing lesson notes

   1. Prerequisites: At least one student exists with no lesson notes and has Math in their current subject list.

   1. Test case: `addnote 1 d/2026-10-01 s/Math note/Revised simultaneous equations`<br>
      Expected: The note is added, the status message echoes it, and the note appears on the student's card under `Lesson Notes:`.

   1. Test case: `history 1`<br>
      Expected: The note is listed, numbered from 1, most recent date first.

   1. Add three more notes with different dates, then run `history 1`.<br>
      Expected: All four are listed newest first. The card shows only the three most recent, followed by `+ 1 more`.

   1. Test case: repeat an `addnote` command exactly.<br>
      Expected: Rejected. A student cannot have two notes with the same date, subject and text.

   1. Test case: use `addnote` with a valid subject that is not in the student's current subject list.<br>
      Expected: Rejected with a message stating that the student does not take that subject.

   1. Test cases: `addnote 1 d/2099-01-01 s/Math note/Future`, `addnote 1 d/2026-02-30 s/Math note/Not a date`, `addnote 1 d/2026-10-01 s/Math note/`<br>
      Expected: Each is rejected. Dates must be real calendar dates in `YYYY-MM-DD` format and cannot be in the future; note text cannot be blank.

1. Editing a lesson note

   1. Prerequisites: The first displayed student has at least two lesson notes. Run `history 1` and record the numbering.

   1. Test case: `editnote 1 ln/2 note/Recapped photosynthesis`<br>
      Expected: Only the text of the second note changes; its date and subject are unchanged.

   1. Test case: edit the date of a note so that it becomes the most recent, then run `history 1`.<br>
      Expected: The notes are re-sorted and the numbering changes, so the edited note is now number 1.

   1. Test case: `editnote 1 ln/1`<br>
      Expected: Rejected. At least one of `d/`, `s/` or `note/` must be provided.

   1. Test case: supply the existing value of one or more fields without changing any lesson-note detail.<br>
      Expected: Rejected with a message stating that no changes were made.

1. Deleting a lesson note

   1. Prerequisites: The first displayed student has at least two lesson notes.

   1. Test case: `deletenote 1 ln/2`<br>
      Expected: That note is removed and the status message echoes it. Running `history 1` shows the remaining notes renumbered.

   1. Test case: `deletenote 1 ln/99`<br>
      Expected: Rejected with an invalid lesson note index message.

   1. Test case: `deletenote 1 ln/1`, where the first displayed student has no notes.<br>
      Expected: Rejected with a message saying that student has no lesson notes to delete.

1. Lesson notes and the displayed list

   1. Prerequisites: At least three students exist. Use `find` to display only one student who has Math in their current subject list.

   1. Test case: `addnote 1 d/2026-10-01 s/Math note/Filter check`, then `deletenote 1 ln/1`<br>
      Expected: Both commands act on the displayed student, and the filtered list stays on screen afterwards.

1. Persistence

   1. Close and restart the app from the same folder, then run `history` on a student with notes.<br>
      Expected: All notes, their order and their contents are unchanged.

### Saving data

1. Dealing with a missing data file

   1. Prerequisites: Close the app. Delete `data/addressbook.json` from the folder containing the JAR file.

   1. Relaunch the app.<br>
      Expected: The app starts with the sample students instead of an empty list. The file is recreated after the first successful save (neither `help` nor `exit` attempts a save).

1. Dealing with a corrupted data file

   1. Prerequisites: Close the app. Open `data/addressbook.json` in a text editor and make it invalid, for example by deleting a closing brace or setting a student's `parentPhone` to `"abc"`.

   1. Relaunch the app.<br>
      Expected: The app starts with an **empty** student list rather than sample data, and `addressbook.log.0` records that the data file could not be loaded. The invalid file is left on disk until a command writes new data over it, so back it up first if you want to keep it.

1. Automatic saving and exiting

   1. Prerequisites: The app is running with at least one student.

   1. Run a valid `addnote` command and verify there is no save error, then close the app.<br>
      Expected: `data/addressbook.json` already reflects the latest state; there is no separate save step because `LogicManager` saves after every successfully executed command except `help` and `exit`.

### Unsaved changes and exit confirmation

`LogicManager` takes an `AddressBook` snapshot of the startup dataset and replaces it only after
`Storage.saveAddressBook` succeeds. `Logic.hasUnsavedChanges()` compares the complete current student
list, including lesson notes, with that snapshot. The startup state is the baseline even if loading
previously fell back to an empty dataset. Storage backup and write-protection behavior is unchanged.

A failed save raises the existing `CommandException`; it does not roll back model changes. A later
successful save writes the entire current dataset and updates the snapshot. Rejected commands and
failed saves of unchanged data do not themselves mark data as changed. Returning exactly to the
baseline also removes the need for a warning.

`help` and `exit` bypass saving. `MainWindow` checks for unsaved changes for CLI `exit`, File → Exit,
and the main window close request. With unsaved changes, Cancel or dismissing the dialog keeps the
window open; Exit anyway closes it without saving. Cancelling consumes the originating close event
so JavaFX cannot close the window afterward.

Manual checks with a test copy of the data:

1. Make saving fail, execute a valid data-changing command, and verify the save error is displayed
   while the changed data remains visible.
2. Run `help`: the help window opens without attempting a save.
3. Try each exit route. Choose Cancel (or dismiss the dialog): the app and data remain available.
4. Restore saving and run `list`. Restart after exiting: the latest dataset should be restored,
   and there should be no unsaved-changes confirmation after the successful save.
5. Repeat the failed-save scenario and choose Exit anyway: the app closes and the unsaved changes
   are absent on restart.
