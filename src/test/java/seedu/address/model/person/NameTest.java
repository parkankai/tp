package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class NameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Name(null));
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        String invalidName = "";
        assertThrows(IllegalArgumentException.class, () -> new Name(invalidName));
        assertThrows(IllegalArgumentException.class, () -> new Name("   "));
        assertThrows(IllegalArgumentException.class, () -> new Name("John@Doe"));
        assertThrows(IllegalArgumentException.class, () -> new Name("a".repeat(Name.MAX_LENGTH + 1)));
    }

    @Test
    public void constructor_normalisesName() {
        Name name = new Name("  John   Doe  ");
        assertEquals("John Doe", name.fullName);
        assertEquals(new Name("John-Doe/2"), new Name("john-doe/2"));
    }

    @Test
    public void isValidName() {
        // null name
        assertThrows(NullPointerException.class, () -> Name.isValidName(null));

        // invalid name
        assertFalse(Name.isValidName("")); // empty string
        assertFalse(Name.isValidName(" ")); // spaces only
        assertFalse(Name.isValidName("^")); // only non-alphanumeric characters
        assertFalse(Name.isValidName("peter*")); // contains non-alphanumeric characters

        // valid name
        assertTrue(Name.isValidName("peter jack")); // alphabets only
        assertTrue(Name.isValidName("12345")); // numbers only
        assertTrue(Name.isValidName("peter the 2nd")); // alphanumeric characters
        assertTrue(Name.isValidName("Capital Tan")); // with capital letters
        assertTrue(Name.isValidName("David Roger Jackson Ray Jr 2nd")); // long names
        assertTrue(Name.isValidName("a".repeat(Name.MAX_LENGTH))); // maximum length
        assertFalse(Name.isValidName("a".repeat(Name.MAX_LENGTH + 1))); // exceeds maximum length
    }

    @Test
    public void constructor_apostrophesAndAccents_preservesName() {
        assertEquals("O'Connor", new Name("O'Connor").fullName);
        assertEquals("Jos\u00e9 O\u2019Connor", new Name("Jos\u00e9 O\u2019Connor").fullName);
        assertEquals("Jose\u0301 Tan", new Name("Jose\u0301 Tan").fullName);
    }

    @Test
    public void isValidName_unsupportedCharacters_returnsFalse() {
        assertFalse(Name.isValidName("'"));
        assertFalse(Name.isValidName("\u2019"));
        assertFalse(Name.isValidName("\u0301"));
        assertFalse(Name.isValidName("John@Doe"));
        assertFalse(Name.isValidName("John\ud83d\ude00"));
    }

    @Test
    public void equals_accentedNames_ignoresCase() {
        Name name = new Name("Jos\u00e9 O'Connor");
        Name sameName = new Name("JOS\u00c9 O'CONNOR");

        assertEquals(name, sameName);
        assertEquals(name.hashCode(), sameName.hashCode());
        assertFalse(name.equals(new Name("Jose O'Connor")));
    }

    @Test
    public void equals() {
        Name name = new Name("Valid Name");

        // same values -> returns true
        assertTrue(name.equals(new Name("Valid Name")));
        assertTrue(name.equals(new Name("valid name")));

        // same object -> returns true
        assertTrue(name.equals(name));

        // null -> returns false
        assertFalse(name.equals(null));

        // different types -> returns false
        assertFalse(name.equals(5.0f));

        // different values -> returns false
        assertFalse(name.equals(new Name("Other Valid Name")));

        assertEquals(name.fullName.toLowerCase().hashCode(), name.hashCode());
    }

    @Test
    public void isValidName_punctuationOnly_returnsFalse() {
        assertFalse(Name.isValidName("---"));
        assertFalse(Name.isValidName("/"));
        assertFalse(Name.isValidName("- / -"));
        assertFalse(Name.isValidName("-\u0301/"));
    }

    @Test
    public void constructor_punctuationOnly_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Name("---"));
        assertThrows(IllegalArgumentException.class, () -> new Name("/"));
        assertThrows(IllegalArgumentException.class, () -> new Name("  - / -  "));
    }

    @Test
    public void isValidName_punctuationWithAlphanumeric_returnsTrue() {
        assertTrue(Name.isValidName("Anne-Marie"));
        assertTrue(Name.isValidName("Ali s/o Ahmad"));
        assertTrue(Name.isValidName("-A/"));
        assertTrue(Name.isValidName("/1-"));
        assertTrue(Name.isValidName("\u674e"));
        assertTrue(Name.isValidName("/\u00e9-"));
    }
}
