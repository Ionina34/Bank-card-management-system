package banks.card.utils;

import lombok.Data;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BeanUtilsTest {

    @Data
    private class TestBean{
        private String name;
        private Integer age;
        private String email;
    }

    @Test
    void testCopyNotNullProperties_AllNonNull() {
        TestBean source = new TestBean();
        source.setName("John");
        source.setAge(30);
        source.setEmail("john@example.com");

        TestBean destination = new TestBean();
        destination.setName("Jane");
        destination.setAge(25);
        destination.setEmail("jane@example.com");

        BeanUtils.copyNotNullProperties(source, destination);

        assertEquals("John", destination.getName(), "Name should be copied");
        assertEquals(30, destination.getAge(), "Age should be copied");
        assertEquals("john@example.com", destination.getEmail(), "Email should be copied");
    }

    @Test
    void testCopyNotNullProperties_SomeNull() {
        TestBean source = new TestBean();
        source.setName("John");
        source.setAge(null); // Null field
        source.setEmail("john@example.com");

        TestBean destination = new TestBean();
        destination.setName("Jane");
        destination.setAge(25);
        destination.setEmail("jane@example.com");

        BeanUtils.copyNotNullProperties(source, destination);

        assertEquals("John", destination.getName(), "Name should be copied");
        assertEquals(25, destination.getAge(), "Age should not be copied (was null)");
        assertEquals("john@example.com", destination.getEmail(), "Email should be copied");
    }

    @Test
    void testCopyNotNullProperties_AllNull() {
        TestBean source = new TestBean();
        source.setName(null);
        source.setAge(null);
        source.setEmail(null);

        TestBean destination = new TestBean();
        destination.setName("Jane");
        destination.setAge(25);
        destination.setEmail("jane@example.com");

        BeanUtils.copyNotNullProperties(source, destination);

        assertEquals("Jane", destination.getName(), "Name should not be copied (was null)");
        assertEquals(25, destination.getAge(), "Age should not be copied (was null)");
        assertEquals("jane@example.com", destination.getEmail(), "Email should not be copied (was null)");
    }

    @Test
    void testCopyNotNullProperties_SourceAndDestinationSameClass() {
        TestBean source = new TestBean();
        source.setName("John");
        source.setAge(30);
        source.setEmail(null);

        TestBean destination = new TestBean();
        destination.setName(null);
        destination.setAge(null);
        destination.setEmail("jane@example.com");

        BeanUtils.copyNotNullProperties(source, destination);

        assertEquals("John", destination.getName(), "Name should be copied");
        assertEquals(30, destination.getAge(), "Age should be copied");
        assertEquals("jane@example.com", destination.getEmail(), "Email should not be copied (was null)");
    }

    @Test
    void testCopyNotNullProperties_EmptySource() {
        @Data
        class EmptyBean {
        }

        EmptyBean source = new EmptyBean();
        EmptyBean destination = new EmptyBean();

        BeanUtils.copyNotNullProperties(source, destination);

        assertNotNull(destination, "Destination should not be null");
    }

    @Test
    void testCopyNotNullProperties_NullSource() {
        TestBean source = null;
        TestBean destination = new TestBean();
        destination.setName("Jane");

        assertThrows(NullPointerException.class, () -> {
            BeanUtils.copyNotNullProperties(source, destination);
        }, "Should throw NullPointerException for null source");
    }

    @Test
    void testCopyNotNullProperties_NullDestination() {
        TestBean source = new TestBean();
        source.setName("John");
        TestBean destination = null;

        assertThrows(NullPointerException.class, () -> {
            BeanUtils.copyNotNullProperties(source, destination);
        }, "Should throw NullPointerException for null destination");
    }
}
