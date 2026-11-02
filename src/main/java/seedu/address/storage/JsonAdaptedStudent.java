package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.lesson.LessonNote;
import seedu.address.model.person.AcademicLevel;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Student;
import seedu.address.model.subject.Subject;

/** Jackson-friendly version of {@link Student}. */
class JsonAdaptedStudent {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Student's %s field is missing!";

    private final String name;
    private final String academicLevel;
    private final List<String> subjects = new ArrayList<>();
    private final String parentPhone;
    private final String parentEmail;
    private final List<JsonAdaptedLessonNote> lessonNotes = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedStudent} with the given student details and no lesson notes.
     */
    public JsonAdaptedStudent(String name, String academicLevel, List<String> subjects,
            String parentPhone, String parentEmail) {
        this(name, academicLevel, subjects, parentPhone, parentEmail, null);
    }

    /**
     * Constructs a {@code JsonAdaptedStudent} with the given student details.
     */
    @JsonCreator
    public JsonAdaptedStudent(@JsonProperty("name") String name,
            @JsonProperty("academicLevel") String academicLevel,
            @JsonProperty("subjects") List<String> subjects,
            @JsonProperty("parentPhone") String parentPhone,
            @JsonProperty("parentEmail") String parentEmail,
            @JsonProperty("lessonNotes") List<JsonAdaptedLessonNote> lessonNotes) {
        this.name = name;
        this.academicLevel = academicLevel;
        if (subjects != null) {
            this.subjects.addAll(subjects);
        }
        this.parentPhone = parentPhone;
        this.parentEmail = parentEmail;
        // Data files saved before lesson notes were introduced do not have this field
        if (lessonNotes != null) {
            this.lessonNotes.addAll(lessonNotes);
        }
    }

    public JsonAdaptedStudent(Student source) {
        name = source.getName().fullName;
        academicLevel = source.getAcademicLevel().value;
        source.getSubjects().forEach(subject -> subjects.add(subject.subjectName));
        parentPhone = source.getParentPhone().value;
        parentEmail = source.getParentEmail().value;
        source.getLessonNotes().forEach(lessonNote -> lessonNotes.add(new JsonAdaptedLessonNote(lessonNote)));
    }

    public Student toModelType() throws IllegalValueException {
        if (name == null || !Name.isValidName(name)) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (academicLevel == null || !AcademicLevel.isValidAcademicLevel(academicLevel)) {
            throw new IllegalValueException(AcademicLevel.MESSAGE_CONSTRAINTS);
        }
        if (subjects.isEmpty()) {
            throw new IllegalValueException("Student must have at least one subject.");
        }
        if (parentPhone == null || !Phone.isValidPhone(parentPhone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        if (parentEmail == null || !Email.isValidEmail(parentEmail)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }

        Set<Subject> modelSubjects = new HashSet<>();
        for (String subject : subjects) {
            if (subject == null || !Subject.isValidSubjectName(subject)) {
                throw new IllegalValueException(Subject.MESSAGE_CONSTRAINTS);
            }
            modelSubjects.add(new Subject(subject));
        }

        List<LessonNote> modelLessonNotes = new ArrayList<>();
        for (JsonAdaptedLessonNote lessonNote : lessonNotes) {
            if (lessonNote == null) {
                throw new IllegalValueException("Lesson notes list contains null lesson note(s).");
            }
            modelLessonNotes.add(lessonNote.toModelType());
        }

        return new Student(new Name(name), new AcademicLevel(academicLevel), modelSubjects,
                new Phone(parentPhone), new Email(parentEmail), modelLessonNotes);
    }
}
